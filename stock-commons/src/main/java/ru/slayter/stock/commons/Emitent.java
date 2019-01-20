package ru.slayter.stock.commons;

public class Emitent {

	private int id;
	private String name;
	private String ticker;
	private String engine;
	private String market;
	private String board;
	private String security;
	private boolean isShortAllowed;
	private int lotSize;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTicker() {
		return ticker;
	}

	public void setTicker(String ticker) {
		this.ticker = ticker;
	}

	public String getEngine() {
		return engine;
	}

	public void setEngine(String engine) {
		this.engine = engine;
	}

	public String getMarket() {
		return market;
	}

	public void setMarket(String market) {
		this.market = market;
	}

	public String getBoard() {
		return board;
	}

	public void setBoard(String board) {
		this.board = board;
	}

	public String getSecurity() {
		return security;
	}

	public void setSecurity(String security) {
		this.security = security;
	}

	public boolean isShortAllowed() {
		return isShortAllowed;
	}

	public void setShortAllowed(boolean isShortAllowed) {
		this.isShortAllowed = isShortAllowed;
	}

	public int getLotSize() {
		return lotSize;
	}

	public void setLotSize(int lotSize) {
		this.lotSize = lotSize;
	}

	@Override
	public String toString() {
		return "Emitent [id=" + id + ", name=" + name + ", ticker=" + ticker + ", engine=" + engine + ", market="
				+ market + ", board=" + board + ", security=" + security + ", isShortAllowed=" + isShortAllowed
				+ ", lotSize=" + lotSize + "]";
	}

}
