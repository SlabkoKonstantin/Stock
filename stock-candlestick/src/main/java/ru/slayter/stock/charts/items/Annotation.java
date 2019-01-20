package ru.slayter.stock.charts.items;

import java.awt.Color;

import org.jfree.ui.TextAnchor;

public abstract class Annotation {

	private double x;
	private double y;
	private String caption;
	private double angle;
	private Color backgroundColor;
	private Color color;
	private TextAnchor textAnchor;

	public Annotation(double x, double y, String caption, double angle, Color backgroundColor, Color color) {
		super();
		this.x = x;
		this.y = y;
		this.caption = caption;
		this.angle = angle;
		this.backgroundColor = backgroundColor;
		this.color = color;
		this.textAnchor = TextAnchor.BOTTOM_CENTER;
	}

	public Annotation(double x, double y, String caption, double angle, Color backgroundColor, Color color,
			TextAnchor textAnchor) {
		super();
		this.x = x;
		this.y = y;
		this.caption = caption;
		this.angle = angle;
		this.backgroundColor = backgroundColor;
		this.color = color;
		this.textAnchor = textAnchor;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public String getCaption() {
		return caption;
	}

	public double getAngle() {
		return angle;
	}

	public Color getBackgroundColor() {
		return backgroundColor;
	}

	public Color getColor() {
		return color;
	}

	public TextAnchor getTextAnchor() {
		return textAnchor;
	}

	@Override
	public String toString() {
		return "Annotation [x=" + x + ", y=" + y + ", caption=" + caption + ", angle=" + angle + ", backgroundColor="
				+ backgroundColor + ", color=" + color + ", textAnchor=" + textAnchor + "]";
	}

}
