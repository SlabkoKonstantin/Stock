package ru.slayter.stock.charts.drawers;

import java.awt.Color;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.Graphics2D;
import java.awt.BasicStroke;

import java.awt.geom.Line2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

import org.jfree.ui.Drawable;

public class Circle implements Drawable {
	private Paint outlinePaint;
	private Stroke outlineStroke;
	private Paint fillPaint;

	public Circle(final Paint outlinePaint, final Stroke outlineStroke, final Paint fillPaint) {
		this.outlinePaint = outlinePaint;
		this.outlineStroke = outlineStroke;
		this.fillPaint = fillPaint;
	}

	public void draw(final Graphics2D g2, final Rectangle2D area) {
		Ellipse2D ellipse = new Ellipse2D.Double(area.getX(), area.getY(), area.getWidth(), area.getHeight());
		if (this.fillPaint != null) {
			g2.setPaint(this.fillPaint);
			g2.fill(ellipse);
		}
		if (this.outlinePaint != null && this.outlineStroke != null) {
			g2.setPaint(this.outlinePaint);
			g2.setStroke(this.outlineStroke);
			g2.draw(ellipse);
		}

		g2.setPaint(Color.black);
		g2.setStroke(new BasicStroke(1.0f));
		Line2D line1 = new Line2D.Double(area.getCenterX(), area.getMinY(), area.getCenterX(), area.getMaxY());
		Line2D line2 = new Line2D.Double(area.getMinX(), area.getCenterY(), area.getMaxX(), area.getCenterY());
		g2.draw(line1);
		g2.draw(line2);
	}
}
