package ru.slayter.stock.charts.items;

import java.awt.Color;

import org.jfree.ui.TextAnchor;

public class TextAnnotation extends Annotation {

	public TextAnnotation(double x, double y, String caption, double angle, Color backgroundColor, Color textColor, TextAnchor textAnchor) {
		super(x, y, caption, angle, backgroundColor, textColor, textAnchor);
	}

	@Override
	public String toString() {
		return "TextAnnotation [toString()=" + super.toString() + "]";
	}

}
