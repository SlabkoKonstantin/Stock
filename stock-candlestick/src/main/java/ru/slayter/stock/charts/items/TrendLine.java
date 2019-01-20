package ru.slayter.stock.charts.items;

import java.awt.Color;

public class TrendLine extends TimedLine {

	public TrendLine(TimedPoint startPoint, TimedPoint endPoint, String series, Color color, float width, Type type) {
		super();
		this.getPoints().add(startPoint);
		this.getPoints().add(endPoint);
		this.setSeries(series);
		this.setColor(color);
		this.setWidth(width);
		this.setType(type);
	}

	@Override
	public String toString() {
		return "TrendLine [toString()=" + super.toString() + "]";
	}

}
