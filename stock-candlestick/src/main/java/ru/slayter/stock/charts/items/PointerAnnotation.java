package ru.slayter.stock.charts.items;

import java.awt.Color;

import org.jfree.ui.TextAnchor;

public class PointerAnnotation extends Annotation {

	private double baseRadius;
	private double tipRadius;
	
	public PointerAnnotation(double x, double y, String caption, double angle, Color backgroundColor, Color color, TextAnchor textAnchor) {
		super(x, y, caption, angle, backgroundColor, color, textAnchor);
		this.baseRadius = 20;
		this.tipRadius = 10;
	}

	public PointerAnnotation(double x, double y, String caption, double angle, Color backgroundColor, Color color, double baseRadius, double tipRadius, TextAnchor textAnchor) {
		super(x, y, caption, angle, backgroundColor, color, textAnchor);
		this.baseRadius = baseRadius;
		this.tipRadius = tipRadius;
	}

	public double getBaseRadius() {
		return baseRadius;
	}

	public double getTipRadius() {
		return tipRadius;
	}

	@Override
	public String toString() {
		return "PointerAnnotation [baseRadius=" + baseRadius + ", tipRadius=" + tipRadius + ", toString()="
				+ super.toString() + "]";
	}
	
}
