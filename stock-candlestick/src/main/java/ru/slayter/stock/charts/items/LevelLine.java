package ru.slayter.stock.charts.items;

import java.awt.Color;

import org.jfree.data.time.FixedMillisecond;

public class LevelLine extends TimedLine {
	
	private double level;
	
	public LevelLine(double level, FixedMillisecond start, FixedMillisecond end, String series, Color color, float width, Type type) {		
		super();
		this.level = level;
		this.getPoints().add(new TimedPoint(start, level));
		this.getPoints().add(new TimedPoint(end, level));
		this.setSeries(series);
		this.setColor(color);
		this.setWidth(width);
		this.setType(type);
	}

	public double getLevel() {
		return this.level;
	}

	@Override
	public String toString() {
		return "LevelLine [toString()=" + super.toString() + "]";
	}

	
}
