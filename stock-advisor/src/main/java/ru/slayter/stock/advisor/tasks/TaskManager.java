package ru.slayter.stock.advisor.tasks;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.slayter.stock.advisor.configuration.Configuration;
import ru.slayter.stock.advisor.database.Database;
import ru.slayter.stock.advisor.reports.Report;
import ru.slayter.stock.commons.Constants;
import ru.slayter.stock.strategies.StrategyLoadException;
import ru.slayter.stock.strategies.StrategyLoader;

public class TaskManager {
	private final static Logger logger = LoggerFactory.getLogger(TaskManager.class);
	private CopyOnWriteArrayList<Task> tasksList;
	private Configuration configuration;

	public TaskManager(Configuration configuration) {
		this.configuration = configuration;
	}

	public int execute() {
		int errorCount = 0;
		// читаем задачи из локальной базы
		logger.info("Get tasks from local database {}...", this.configuration.getDatabase());
		Database database = new Database(this.configuration.getDatabase());
		tasksList = database.getTasksList();
		logger.info("There are {} tasks readed from local database.", tasksList.size());

		// и обрабатываем их
		if (tasksList.size() > 0) {			
			
			StrategyLoader strategyLoader = new StrategyLoader();
			for (Task task : tasksList) {
				logger.info(task.toString());
				try {
					// вытягиваем свойства задачи и помещаем их в задачу
					Properties taskProperties =database.getTaskProperties(task.getId());
					taskProperties.setProperty(Constants.REPORT_HTML_PATH, this.configuration.getReports()); // добавляем информацию о каталоге отчета					
					task.setProperties(taskProperties); 
					// тут на всякий случай отметим, что свойство задачи PREFIX задается еще и в коде TaskCallable
					
					// динамически загружаем класс-исполнитель
					String filePath = configuration.getModules() + "//" + task.getStrategy().getFileName();
					logger.debug("File path: {}", filePath);
					task.getStrategy().setBehavior(strategyLoader.load(filePath));
					logger.debug("Loading: {}", task.getStrategy().getBehavior().toString());

					// вытягиваем свойства стратегии и помещаем их в стратегию
					task.getStrategy().setProperties(database.getStrategyProperties(task.getStrategy().getId()));
					
				} catch (StrategyLoadException e) {
					logger.error(e.getMessage());
				}

			}

			// и исполняем стратегии
			ExecutorService executor = Executors.newFixedThreadPool(configuration.getPool().getSize());
			try {
				// запускаем потоки задач
				List<Future<Task>> tasksCallable = new ArrayList<Future<Task>>();
				for (Task task : tasksList) {
					TaskCallable currentTask = new TaskCallable(task);
					Future<Task> currentTaskSubmitted = executor.submit(currentTask);
					logger.debug(
							"Submit task " + task.getDescription() + ", future = " + currentTaskSubmitted.toString());
					tasksCallable.add(currentTaskSubmitted);
				}

				// и начинаем их опрашивать на предмет завершения и возврата данных
				int taskTimeOut = configuration.getPool().getTimeouts().getOnetask();

				for (Future<Task> callable : tasksCallable) {
					try {
						Task res = callable.get(taskTimeOut, TimeUnit.SECONDS);
						logger.debug(
								"Task " + res.getDescription() + ", future = " + callable.toString() + " completed.");

						if (res.getStrategy().getResult().getErrorCount() > 0) {
							logger.info("{} errors found during execution task {}.", res.getDescription());
						}

						errorCount = errorCount + res.getStrategy().getResult().getErrorCount();
					} catch (InterruptedException ie) {
						logger.error("InterruptedException in future = " + callable.toString() + ": time is out");
						callable.cancel(true);
					} catch (ExecutionException ee) {
						logger.error("ExecutionException in future = " + callable.toString() + ": " + ee.getMessage());
						callable.cancel(true);
					} catch (TimeoutException te) {
						logger.error("TimeoutException in future = " + callable.toString());
						callable.cancel(true);
					}
				}
			} finally {
				logger.trace("Task executor shutdown");
				executor.shutdown(); // Отключаем сабмит новых задач
				try {
					// Ждем пока завершатся существующие задачи
					if (!executor.awaitTermination(configuration.getPool().getTimeouts().getAwaitTermination(),
							TimeUnit.SECONDS)) {
						logger.trace("Task executor shutdown now");
						executor.shutdownNow(); // Отменяем сейчас исполняющиеся задачи
						// Подождем, пока они все откликнутся на отмену
						if (!executor.awaitTermination(configuration.getPool().getTimeouts().getAwaitTermination(),
								TimeUnit.SECONDS))
							logger.trace("Pool did not terminate");
						Thread.currentThread().interrupt();
					}
				} catch (InterruptedException ie) {
					// Повторно отменим, если текущий поток получит запрос на прерывание
					logger.trace("Task executor shutdown now in InterruptedException");
					executor.shutdownNow();
					// Сохраним статус прерывания
					Thread.currentThread().interrupt();
				}
			}

			// отображаем отчет по выполнению задачи и возникших ошибках
			logger.info("----------------------- Task error report start -----------------");
			int overall_error_count = 0;
			for (Task task : tasksList) {
				if (task.getStrategy().getResult().getErrorCount() > 0) {
					overall_error_count = overall_error_count + task.getStrategy().getResult().getErrorCount();
					logger.info(task.getDescription() + ", errors : " + task.getStrategy().getResult().getErrorCount());
				}
			}
			logger.info("Overall errors: {}\n", overall_error_count);
			logger.info("----------------------- Task error report end -----------------");

			// по завершению генерим файл биржевого отчета
			logger.info("Generate report...");
			Report report = new Report(tasksList, configuration.getReports());
			report.create();
			logger.info("Generate report succesfully completed.");

		} else {
			logger.info("Tasks list is empty.");
		}

		return errorCount;
	}

}
