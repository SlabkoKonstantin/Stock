package ru.slayter.stock.charts.items;

import org.jfree.data.time.FixedMillisecond;

public class TimedPoint {
	private FixedMillisecond time;
	private double value;

	public TimedPoint() {
		super();
		this.time = new FixedMillisecond(0);
		this.value = 0;
	}

	public TimedPoint(FixedMillisecond time, double value) {
		super();
		this.time = time;
		this.value = value;
	}

	public FixedMillisecond getTime() {
		return time;
	}

	public void setTime(FixedMillisecond time) {
		this.time = time;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "TimedPoint [time=" + time + ", value=" + value + "]";
	}
	
}
