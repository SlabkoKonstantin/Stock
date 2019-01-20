package ru.slayter.stock.strategies;

import java.util.Properties;

import ru.slayter.stock.commons.Constants;

public class Strategy {
	
	private int id;
	private String sign;
	private String fileName;
	private Strategable behavior = null;
	private StrategyResult result;
	private Properties properties;
	
	public Strategy() {
		super();
		this.id = -1;
		this.sign = Constants.EMPTY;
		this.fileName = Constants.EMPTY;
		this.behavior = null;
		this.setResult(new StrategyResult());
		this.properties = null;	
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public Strategable getBehavior() {
		return behavior;
	}

	public void setBehavior(Strategable behavior) {
		this.behavior = behavior;
	}
	
	public StrategyResult getResult() {
		return result;
	}

	public void setResult(StrategyResult result) {
		this.result = result;
	}
	
	public Properties getProperties() {
		return properties;
	}

	public void setProperties(Properties properties) {
		this.properties = properties;
	}

	@Override
	public String toString() {
		return "StrategyProperties [id=" + id + ", sign=" + sign + ", fileName=" + fileName + "]";
	}	
}
