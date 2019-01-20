package ru.slayter.stock.strategies;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import ru.slayter.stock.commons.Constants;
import ru.slayter.stock.commons.Emitent;

public class StrategyBase implements Strategable {
	
	protected final static Logger logger = LoggerFactory.getLogger(Strategable.class);
	protected StrategyResult strategyResult = new StrategyResult();
	protected Properties strategyProperties;
	protected Properties taskProperties;
	
	@Override
	public void setStrategyProperties(Properties properties) {
		this.strategyProperties = properties;
	}	

	@Override
	public void setTaskProperties(Properties properties) {
		this.taskProperties = properties;
	}
	
	@Override
	public StrategyResult execute(Emitent emitent) {
		return this.strategyResult;
	}

	@Override
	public String toString() {
		return "StrategyBase []";
	}

	@Override
	public void setLogPrefix(String prefix) {
		MDC.put(Constants.LOG_PREFIX, prefix);				
	}

	public String getDefTaskProperty(String key, String defValue) {
		String result = this.taskProperties.getProperty(key);
		if ((result == null) || (result.isEmpty())) 
			result = defValue;
		return result;
	}
	
	public String getDefStrategyProperty(String key, String defValue) {
		String result = this.strategyProperties.getProperty(key);
		if ((result == null) || (result.isEmpty())) 
			result = defValue;
		return result;		
	}
	
	public void error(String msg, Object obj1, Object obj2) {
		this.strategyResult.incErrorCount();		
		
		StringBuilder sb = new StringBuilder();
		sb.append("ERROR_").append(this.strategyResult.getErrorCount()).append(": ").append(msg);

		
		if ((obj1 != null) && (obj2 != null)) {			
			logger.error(sb.toString(), obj1, obj2);
		} else if ((obj1 != null) && (obj2 == null)) {
			logger.error(sb.toString(), obj1);
		} else {
			logger.error(sb.toString());
		}
	}

	public void error(String msg, Object obj1) {
		this.error(msg, obj1, null);
	}

	public void error(String msg) {		
		this.error(msg, null, null);
	}

}
