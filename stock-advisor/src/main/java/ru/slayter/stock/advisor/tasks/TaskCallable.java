package ru.slayter.stock.advisor.tasks;

import java.util.concurrent.Callable;

import ru.slayter.stock.commons.Constants;
import ru.slayter.stock.strategies.Strategable;

public class TaskCallable implements Callable<Task> {

	private Task task;

	public TaskCallable(Task task) {
		super();
		this.task = task;
	}

	@Override
	public Task call() throws Exception {
		Strategable executor = task.getStrategy().getBehavior();
		String prefix = this.task.getEmitent().getTicker() + "_" + this.task.getStrategy().getSign();
		executor.setLogPrefix(prefix);
		task.getProperties().setProperty(Constants.PREFIX, prefix);
		executor.setTaskProperties(task.getProperties());
		executor.setStrategyProperties(task.getStrategy().getProperties());
		task.getStrategy().setResult(executor.execute(task.getEmitent()));
		return task;
	}

}
