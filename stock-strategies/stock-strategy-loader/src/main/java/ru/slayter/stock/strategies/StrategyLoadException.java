package ru.slayter.stock.strategies;

public class StrategyLoadException extends Exception {
	private static final long serialVersionUID = 7441548630094426080L;

	public StrategyLoadException(String s) {
		super(s);
	}

	public StrategyLoadException(Exception e) {
		super(e);
	}
}
