package ru.slayter.stock.charts;

import java.awt.BasicStroke;
import java.awt.Color;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jfree.chart.ChartColor;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYAnnotation;
import org.jfree.chart.annotations.XYDrawableAnnotation;
import org.jfree.chart.annotations.XYPointerAnnotation;
import org.jfree.chart.annotations.XYTextAnnotation;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.SegmentedTimeline;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.DatasetRenderingOrder;
import org.jfree.chart.plot.Marker;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.CandlestickRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.renderer.xy.XYSplineRenderer;
import org.jfree.data.time.FixedMillisecond;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.time.TimeSeriesDataItem;
import org.jfree.data.xy.DefaultHighLowDataset;
import org.jfree.data.xy.OHLCDataset;
import org.jfree.ui.RectangleAnchor;
import org.jfree.ui.TextAnchor;
import org.slf4j.Logger;

import ru.slayter.stock.charts.drawers.Circle;
import ru.slayter.stock.charts.items.Indicator;
import ru.slayter.stock.charts.items.LevelLine;
import ru.slayter.stock.charts.items.MarkedPoint;
import ru.slayter.stock.charts.items.TimedLine;
import ru.slayter.stock.charts.items.TimedPoint;
import ru.slayter.stock.charts.items.TrendLine;
import ru.slayter.stock.commons.Candle;

/**
 * Main drawing class
 *
 */
public class Candlestick extends javax.swing.JPanel {
	private static final long serialVersionUID = 6572418994566827197L;

	private Logger logger;

	public Candlestick(Logger logger) {
		super();
		this.logger = logger;
	}

	public int createImage(ArrayList<Candle> candles, String header, int width, int height, List<Object> items,
			String target) {
		int result = 0;
		try {
			OHLCDataset ohlcDataset = createOHLCDataset(candles);

			JFreeChart chart = ChartFactory.createCandlestickChart(header, "Время", "Цена", ohlcDataset, true);

			CandlestickRenderer renderer = new CandlestickRenderer();
			renderer.setAutoWidthMethod(CandlestickRenderer.WIDTHMETHOD_AVERAGE);
			XYPlot plot = chart.getXYPlot();
			plot.setRenderer(renderer);

			for (Object item : items) {
				switch (item.getClass().getName()) {
				case "ru.slayter.stock.charts.items.LevelLine":
					drawLevelLine(plot, (LevelLine) item, items.indexOf(item) + 1);
					break;
				case "ru.slayter.stock.charts.items.TrendLine":
					drawTrendLine(plot, (TrendLine) item, items.indexOf(item) + 1);
					break;
				case "ru.slayter.stock.charts.items.Marker":
					drawMarker(plot, (ru.slayter.stock.charts.items.Marker) item);
					break;
				case "ru.slayter.stock.charts.items.MarkedPoint":
					drawMarkedPoint(plot, (MarkedPoint) item);
					break;
				case "ru.slayter.stock.charts.items.Indicator":
					drawIndicator(plot, (Indicator) item, items.indexOf(item) + 1);
					break;
				default:
					logger.error("Unknown graphics class: {}", item.getClass().getName());
				}
			}

			logger.debug("Save image to file {}", target);

			// Misc
			plot.setRangeGridlinePaint(ChartColor.lightGray);
			plot.setBackgroundPaint(ChartColor.white);
			plot.setDatasetRenderingOrder(DatasetRenderingOrder.FORWARD);

			NumberAxis numberAxis = (NumberAxis) plot.getRangeAxis(); // это ось Y
			numberAxis.setAutoRangeIncludesZero(false); // включим autosize по оси Y

			ValueAxis axis = plot.getDomainAxis(); // это ось Х
			axis.setVerticalTickLabels(true); // развернем подписи под осью Х
			((DateAxis) axis).setTimeline(SegmentedTimeline.newMondayThroughFridayTimeline()); // Уберем выходные
			chart.removeLegend(); // уберем легенду

			final ChartPanel chartPanel = new ChartPanel(chart);
			this.add(chartPanel);

			ChartUtilities.saveChartAsPNG(new File(target), chart, width, height);

		} catch (Exception e) {
			logger.error("Error in createImage: {}", e.getMessage());
			result = 1;
		}
		return result;
	}

	private XYAnnotation createAnnotation(ru.slayter.stock.charts.items.Annotation annotation) {
		XYAnnotation result = null;
		switch (annotation.getClass().getName()) {
		case "ru.slayter.stock.charts.items.PointerAnnotation":
			ru.slayter.stock.charts.items.PointerAnnotation ptrAnnotation = (ru.slayter.stock.charts.items.PointerAnnotation) annotation;
			result = createPtrAnnotation(ptrAnnotation.getCaption(), ptrAnnotation.getX(), ptrAnnotation.getY(),
					ptrAnnotation.getBackgroundColor(), ptrAnnotation.getColor(), ptrAnnotation.getAngle(),
					ptrAnnotation.getBaseRadius(), ptrAnnotation.getTipRadius(), ptrAnnotation.getTextAnchor());
			break;
		case "ru.slayter.stock.charts.items.TextAnnotation":
			ru.slayter.stock.charts.items.TextAnnotation textAnnotation = (ru.slayter.stock.charts.items.TextAnnotation) annotation;
			result = createTextAnnotation(textAnnotation.getCaption(), textAnnotation.getX(), textAnnotation.getY(),
					textAnnotation.getBackgroundColor(), textAnnotation.getColor(), textAnnotation.getAngle(),
					textAnnotation.getTextAnchor());
			break;
		default:
			logger.error("Unknown annotation class: {}", annotation.getClass().getName());
		}
		return result;
	}

	private void drawTimedLine(XYPlot plot, TimedLine line, int index) {
		TimeSeriesCollection xyDataset = createXYDataset(line.getPoints(), line.getSeries());
		plot.setDataset(index, xyDataset);
		plot.mapDatasetToRangeAxis(index, 0);
		XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer(true, false);
		// ставим цвет линии
		renderer.setSeriesPaint(0, line.getColor());
		// ставим тип линии и толщину
		switch (line.getType()) {
		case Dotted:
			renderer.setSeriesStroke(0, new BasicStroke(line.getWidth(), BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND,
					1.0f, new float[] { 8.0f, 4.0f }, 0.0f));
			break;
		default:
			renderer.setSeriesStroke(0, new BasicStroke(line.getWidth()));
		}
		// аннотация
		if (line.getAnnotation() != null) {
			plot.addAnnotation(createAnnotation(line.getAnnotation()));
		}
		plot.setRenderer(index, renderer);
	}

	private void drawIndicator(XYPlot plot, Indicator line, int index) {
		TimeSeriesCollection xyDataset = createXYDataset(line.getPoints(), line.getSeries());
		plot.setDataset(index, xyDataset);
		plot.mapDatasetToRangeAxis(index, 0);
		XYSplineRenderer renderer = new XYSplineRenderer();
		renderer.setPrecision(line.getPrecision());
		// ставим цвет линии
		renderer.setSeriesPaint(0, line.getColor());
		// ставим тип линии и толщину
		switch (line.getType()) {
		case Dotted:
			renderer.setSeriesStroke(0, new BasicStroke(line.getWidth(), BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND,
					1.0f, new float[] { 8.0f, 4.0f }, 0.0f));
			break;
		default:
			renderer.setSeriesStroke(0, new BasicStroke(line.getWidth()));
		}
		// аннотация
		if (line.getAnnotation() != null) {
			plot.addAnnotation(createAnnotation(line.getAnnotation()));
		}
		plot.setRenderer(index, renderer);
	}

	private void drawTrendLine(XYPlot plot, TrendLine line, int index) {
		logger.debug("Draw trend line: {}, index = {}", line.toString(), index);
		if (line.getPoints().size() > 0) {
			drawTimedLine(plot, line, index);
		}
	}

	private void drawLevelLine(XYPlot plot, LevelLine line, int index) {
		logger.debug("Draw level line: {}, index = {}", line.toString(), index);
		if (line.getPoints().size() > 0) {
			drawTimedLine(plot, line, index);
		}
	}

	private void drawMarker(XYPlot plot, ru.slayter.stock.charts.items.Marker item) {
		final Marker marker = new ValueMarker(item.getValue());
		marker.setPaint(item.getColor());
		marker.setLabel(item.getCaption());
		marker.setLabelAnchor(RectangleAnchor.TOP_RIGHT);
		marker.setLabelTextAnchor(TextAnchor.BOTTOM_RIGHT);
		plot.addRangeMarker(marker);
	}

	private void drawMarkedPoint(XYPlot plot, MarkedPoint item) {
		FixedMillisecond fm = new FixedMillisecond(item.getDate());
		double millis = fm.getFirstMillisecond();
		ru.slayter.stock.charts.drawers.Circle cd = new Circle(item.getColor(), new BasicStroke(1.0f), null);
		XYAnnotation bestBid = new XYDrawableAnnotation(millis, item.getValue(), 10, 10, cd);
		plot.addAnnotation(bestBid);
		// аннотация
		if (item.getAnnotation() != null) {
			plot.addAnnotation(createAnnotation(item.getAnnotation()));
		}
	}

	private XYPointerAnnotation createPtrAnnotation(String mess, double x, double y, Color backgroundColor,
			Color textColor, double angle, double baseRadius, double tipRadius, TextAnchor textAnchor) {
		XYPointerAnnotation pointer = new XYPointerAnnotation(mess, x, y, angle);
		pointer.setBaseRadius(baseRadius);
		pointer.setTipRadius(tipRadius);
		pointer.setBackgroundPaint(backgroundColor);
		// pointer.setFont(new Font("SansSerif", Font.PLAIN, 9));
		pointer.setPaint(textColor);
		pointer.setTextAnchor(textAnchor);
		return pointer;
	}

	private XYTextAnnotation createTextAnnotation(String caption, double x, double y, Color backgroundColor,
			Color textColor, double angle, TextAnchor textAnchor) {
		XYTextAnnotation annotation = new XYTextAnnotation(caption, x, y);
		annotation.setBackgroundPaint(backgroundColor);
		annotation.setPaint(textColor);
		// annotation.setFont(new Font("SansSerif", Font.PLAIN, 9));
		annotation.setRotationAngle(angle); // Math.PI / 4.0
		annotation.setTextAnchor(textAnchor);
		return annotation;
	}

	private OHLCDataset createOHLCDataset(ArrayList<Candle> candles) {
		final int nbTicks = candles.size();

		Date[] dates = new Date[nbTicks];
		double[] opens = new double[nbTicks];
		double[] highs = new double[nbTicks];
		double[] lows = new double[nbTicks];
		double[] closes = new double[nbTicks];
		double[] volumes = new double[nbTicks];

		int i = 0;
		for (Candle candle : candles) {
			dates[i] = candle.getBegin();
			opens[i] = candle.getOpen();
			highs[i] = candle.getHigh();
			lows[i] = candle.getLow();
			closes[i] = candle.getClose();
			volumes[i] = candle.getVolume();
			i++;
		}

		OHLCDataset dataset = new DefaultHighLowDataset("", dates, highs, lows, opens, closes, volumes);

		return dataset;
	}

	private TimeSeriesCollection createXYDataset(ArrayList<TimedPoint> points, String seriesName) {
		TimeSeriesCollection dataset = new TimeSeriesCollection();
		org.jfree.data.time.TimeSeries chartTimeSeries = new org.jfree.data.time.TimeSeries(seriesName);

		for (TimedPoint point : points) {
			chartTimeSeries.addOrUpdate(point.getTime(), point.getValue());
		}
		dataset.addSeries(chartTimeSeries);
		return dataset;
	}

	public Color getContrastColor(Color color) {
		int r = color.getRed();
		int g = color.getGreen();
		int b = color.getBlue();
		int yiq = ((r * 299) + (g * 587) + (b * 114)) / 1000;
		return (yiq >= 128) ? Color.BLACK : Color.WHITE;
	}

}
