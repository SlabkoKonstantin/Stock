package ru.slayter.stock.strategies;

import ru.slayter.stock.commons.Constants;
import ru.slayter.stock.commons.Emitent;

public class StrategyMacd extends StrategyBase
{

	@Override
	public String toString() {
		return "StrategyMacd []";
	}
	
	@Override
	public StrategyResult execute(Emitent emitent) {
		logger.debug("MacdStrategy execute.");
		//error("Test error in macd");
		
		// заполняем заключение
		this.strategyResult.setAnalysisResume(Constants.EMPTY);
		this.strategyResult.setDetailLink(Constants.EMPTY);
		
		return this.strategyResult;
	}	
	
}
