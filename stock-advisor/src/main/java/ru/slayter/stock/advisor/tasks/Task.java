package ru.slayter.stock.advisor.tasks;

import java.util.Properties;

import ru.slayter.stock.commons.Emitent;
import ru.slayter.stock.strategies.Strategy;

public class Task {

	private int id;
	private String description;
	private Emitent emitent;
	private Strategy strategy;
	private Properties properties;
	

	public Task() {
		super();
		this.id = -1;
		this.description = null;
		this.emitent = null;
		this.strategy = null;
		this.properties = null;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Emitent getEmitent() {
		return emitent;
	}

	public void setEmitent(Emitent emitent) {
		this.emitent = emitent;
	}

	public Strategy getStrategy() {
		return strategy;
	}

	public void setStrategy(Strategy strategyProperties) {
		this.strategy = strategyProperties;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Properties getProperties() {
		return properties;
	}

	public void setProperties(Properties properties) {
		this.properties = properties;
	}

	@Override
	public String toString() {
		return "Task [id=" + id + ", description=" + description + ", emitent=" + emitent + ", strategy=" + strategy
				+ "]";
	}
}
