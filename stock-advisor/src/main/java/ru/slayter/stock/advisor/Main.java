package ru.slayter.stock.advisor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.slayter.stock.advisor.configuration.Configuration;
import ru.slayter.stock.advisor.configuration.XmlConfiguration;
import ru.slayter.stock.advisor.tasks.TaskManager;
import ru.slayter.stock.commons.Constants;

public class Main {
	// logger
	private final static Logger logger = LoggerFactory.getLogger(Main.class);

	public static void main(String[] args) {
		try {
			logger.info("Advisor started"); // засекаем запуск
			try {
				Execute(args); // собственно выполняем рабочий код
			} catch (Exception ex) {
				logger.error("Main exception interceptor: {}", ex.toString());
			} finally {
				logger.info("Advisor stopped"); // засекаем остановку
				logger.info("-----------------------------------------------");
			}
		} catch (NoClassDefFoundError e) {
			String message = "NoClassDefFoundError: " + e.getLocalizedMessage()
					+ ", please check the libs integrity!!!";
			System.out.print(message);
			logger.error(message);
		}
	}

	private static void Execute(String[] args) throws Exception {
		// читаем конфигурацию - это дефолтные значения, если что, можно
		// передать в командной строке другие
		String path = Constants.DEFAULT_ADVISOR_CONFIG_PATH;

		// обрабатываем командную строку
		if (args.length > 0) {
			path = args[0];
		}
		
		Configuration configuration = new Configuration();
		XmlConfiguration manager = new XmlConfiguration(configuration, path);

		logger.info("Reading configuration from {}", path);
		configuration = manager.load();

		if (configuration != null) {
			logger.info("Configuration succesfully set");

			logger.info("Start stock analysis....");
			TaskManager taskManager = new TaskManager(configuration);
			int errorCount = taskManager.execute();
			logger.info("The analysis was successfully completed with {} errors.", errorCount);
		} else {
			throw new Exception(manager.getLastErrorMessage());
		}		
	}
}
