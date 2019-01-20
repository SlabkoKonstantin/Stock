package ru.slayter.stock.charts.items;

import java.awt.Color;

public class Marker {
	
	private double value;
	private String caption;
	private Color color;

	public Marker(double value, String caption, Color darkMagenta) {
		super();
		this.value = value;
		this.caption = caption;
		this.color = darkMagenta;
	}

	public double getValue() {
		return value;
	}

	public String getCaption() {
		return caption;
	}

	public Color getColor() {
		return color;
	}

	@Override
	public String toString() {
		return "MarkedPoint [value=" + value + ", caption=" + caption + ", color=" + color + "]";
	}

}
