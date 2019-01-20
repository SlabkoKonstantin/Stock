package ru.slayter.stock.strategies;

import java.util.Properties;

import ru.slayter.stock.commons.Emitent;

public interface Strategable {

	public void setLogPrefix(String prefix);
	public void setStrategyProperties(Properties properties);
	public void setTaskProperties(Properties properties);
	public StrategyResult execute(Emitent emitent);
}
