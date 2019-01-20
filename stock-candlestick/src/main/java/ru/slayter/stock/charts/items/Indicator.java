package ru.slayter.stock.charts.items;

import java.awt.Color;
import java.util.ArrayList;

public class Indicator extends TimedLine {
	
	private int precision;

	public Indicator(ArrayList<TimedPoint> points, String series, Color color, float width, Type type, int precision) {
		super();
		this.points = points;
		this.setSeries(series);
		this.setColor(color);
		this.setWidth(width);
		this.setType(type);
		this.precision = precision;
	}

	public int getPrecision() {
		return precision;
	}

	@Override
	public String toString() {
		return "Indicator [toString()=" + super.toString() + "]";
	}
}
