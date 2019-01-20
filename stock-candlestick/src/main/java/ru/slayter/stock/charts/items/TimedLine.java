package ru.slayter.stock.charts.items;

import java.awt.Color;
import java.util.ArrayList;

import org.jfree.chart.ChartColor;

import ru.slayter.stock.commons.Constants;

public abstract class TimedLine {

	public enum Type {
		Dotted, Solid
	};

	protected ArrayList<TimedPoint> points;
	private String series;
	private Color color;
	private float width;
	private Type type;
	private Annotation annotation;

	public TimedLine() {
		super();
		this.points = new ArrayList<>();
		this.series = Constants.EMPTY;
		this.color = ChartColor.BLACK;
		setWidth(1);
		this.type = Type.Solid;
		this.annotation = null;
	}

	public ArrayList<TimedPoint> getPoints() {
		if (this.points == null) {
			return new ArrayList<>();
		} else {
			return this.points;
		}
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public float getWidth() {
		return width;
	}

	public void setWidth(float width) {
		if (width < 0.0f) {
			this.width = 0.0f;
		} else {
			this.width = width;
		}
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public String getSeries() {
		return series;
	}

	public void setSeries(String series) {
		this.series = series;
	}

	public Annotation getAnnotation() {
		return annotation;
	}

	public void setAnnotation(Annotation annotation) {
		this.annotation = annotation;
	}

	@Override
	public String toString() {
		return "TimedLine [points=" + points + ", series=" + series + ", color=" + color + ", width=" + width
				+ ", type=" + type + "]";
	}

}
