package ru.slayter.stock.commons;

import java.util.Date;

public class Candle {
	
	private double open;
	private double close;
	private double high;
	private double low;
	private double value;
	private double volume;
	private Date begin;
	private Date end;	
	private int frame;
	
	public Candle(double open, double close, double high, double low, double value, double volume, Date begin,
			Date end, int frame) {
		super();
		this.open = open;
		this.close = close;
		this.high = high;
		this.low = low;
		this.value = value;
		this.volume = volume;
		this.begin = begin;
		this.end = end;
		this.frame = frame;
	}

	public int getFrame() {
		return frame;
	}

	public double getOpen() {
		return open;
	}

	public double getClose() {
		return close;
	}

	public double getHigh() {
		return high;
	}

	public double getLow() {
		return low;
	}

	public double getValue() {
		return value;
	}

	public double getVolume() {
		return volume;
	}

	public Date getBegin() {
		return begin;
	}

	public Date getEnd() {
		return end;
	}

	@Override
	public String toString() {
		return "Candle [open=" + open + ", close=" + close + ", high=" + high + ", low=" + low + ", value=" + value
				+ ", volume=" + volume + ", begin=" + begin + ", end=" + end + ", frame=" + frame + "]";
	}

}
