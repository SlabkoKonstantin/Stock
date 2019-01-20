package ru.slayter.stock.charts.items;

import java.awt.Color;
import java.util.Date;

public class MarkedPoint {
	
	private Date date;
	private double value;
	private Color color;
	private Annotation annotation;

	public MarkedPoint(Date date, double value, Color color, Annotation annotation) {
		super();
		this.date = date;
		this.value = value;
		this.color = color;
		this.annotation = annotation;
	}

	public MarkedPoint(Date date, double value, Color color) {
		super();
		this.date = date;
		this.value = value;
		this.color = color;
		this.annotation = null;
	}

	public Date getDate() {
		return date;
	}

	public double getValue() {
		return value;
	}

	public Color getColor() {
		return color;
	}

	public Annotation getAnnotation() {
		return annotation;
	}

	@Override
	public String toString() {
		return "MarkedPoint [date=" + date + ", value=" + value + ", color=" + color + ", annotation=" + annotation
				+ "]";
	}

}
