package ru.slayter.stock.strategies;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jfree.chart.ChartColor;
import org.jfree.data.time.FixedMillisecond;
import org.jfree.ui.TextAnchor;

import com.webfirmframework.wffweb.tag.html.AbstractHtml.TagType;
import com.webfirmframework.wffweb.tag.html.Body;
import com.webfirmframework.wffweb.tag.html.H2;
import com.webfirmframework.wffweb.tag.html.Html;
import com.webfirmframework.wffweb.tag.html.P;
import com.webfirmframework.wffweb.tag.html.TitleTag;
import com.webfirmframework.wffweb.tag.html.attribute.Alt;
import com.webfirmframework.wffweb.tag.html.attribute.Height;
import com.webfirmframework.wffweb.tag.html.attribute.HttpEquiv;
import com.webfirmframework.wffweb.tag.html.attribute.Src;
import com.webfirmframework.wffweb.tag.html.attribute.Width;
import com.webfirmframework.wffweb.tag.html.attribute.global.Id;
import com.webfirmframework.wffweb.tag.html.attributewff.CustomAttribute;
import com.webfirmframework.wffweb.tag.html.html5.attribute.Content;
import com.webfirmframework.wffweb.tag.html.images.Img;
import com.webfirmframework.wffweb.tag.html.metainfo.Head;
import com.webfirmframework.wffweb.tag.html.metainfo.Meta;
import com.webfirmframework.wffweb.tag.html.stylesandsemantics.Div;
import com.webfirmframework.wffweb.tag.html.stylesandsemantics.StyleTag;
import com.webfirmframework.wffweb.tag.htmlwff.CustomTag;
import com.webfirmframework.wffweb.tag.htmlwff.NoTag;

//import ru.slayter.stock.advisor.tasks.Task;
import ru.slayter.stock.charts.Candlestick;
import ru.slayter.stock.charts.items.Indicator;
import ru.slayter.stock.charts.items.LevelLine;
import ru.slayter.stock.charts.items.MarkedPoint;
import ru.slayter.stock.charts.items.Marker;
import ru.slayter.stock.charts.items.TimedLine;
import ru.slayter.stock.charts.items.TimedLine.Type;
import ru.slayter.stock.charts.items.TimedPoint;
import ru.slayter.stock.charts.items.TrendLine;
import ru.slayter.stock.commons.Candle;
import ru.slayter.stock.commons.Constants;
import ru.slayter.stock.commons.Emitent;
import ru.slayter.stock.moex.MoexConstants;
import ru.slayter.stock.moex.MoexModule;

public class StrategyPeak extends StrategyBase {

	@Override
	public String toString() {
		return "StrategyPeak []";
	}

	@Override
	public StrategyResult execute(Emitent emitent) {
		logger.info("PeakStrategy execute.");

		// выводим перечень свойств задачи
		logger.debug("Task properties:");
		for (String key : this.taskProperties.stringPropertyNames()) {
			String value = this.taskProperties.getProperty(key);
			logger.debug(key + " => " + value);
		}

		// выводим перечень свойств стратегии
		logger.debug("Strategy properties:");
		for (String key : this.strategyProperties.stringPropertyNames()) {
			String value = this.strategyProperties.getProperty(key);
			logger.debug(key + " => " + value);
		}

		// выводим свойства эмитента
		logger.debug("Emitent properties:");
		logger.debug("Lot size: {}", emitent.getLotSize());
		logger.debug("Is short allowed: {}", emitent.isShortAllowed());

		MoexModule moexModule = new MoexModule(logger);

		try {
			// определяем параметры для запроса свечек с сервера биржи

			// таймфрейм свечи
			MoexConstants.CANDLE_TIME_FRAME frame = MoexConstants.CANDLE_TIME_FRAME
					.valueOf(this.taskProperties.getProperty(Constants.TIME_FRAME, Constants.DAY));

			// глубину запроса цифрой
			Integer deepValue = Integer
					.parseInt(this.taskProperties.getProperty(Constants.DEPTH_VALUE, Constants.DEPTH_VALUE_DEFAULT));

			// тип глубины - час/день/неделя
			Constants.DEPTH_TYPES depthType = Constants.DEPTH_TYPES
					.valueOf(this.taskProperties.getProperty(Constants.DEPTH_TYPE, Constants.DAY));

			// тянем свечки с сервера
			ArrayList<Candle> candles = moexModule.getCandles(
					this.strategyProperties.getProperty(Constants.CANLES_LINK), emitent, deepValue, depthType, frame);
			logger.debug("Overall {} candles received.", candles.size());

			// выводим их
			for (Candle candle : candles) {
				logger.debug(candles.indexOf(candle) + ") " + candle.toString());
			}

			// анализируем на пробой. Пробой - это случай, когда цена закрытия свечи
			// оказалась выше
			// (на снижении - ниже) последнего экстремума цены (уровня
			// сопротивления/поддержки).
			Candle lastCandle = candles.get(candles.size() - 1);
			logger.debug("Last candle is {}", lastCandle.toString());

			Candle maxCandle = null;
			for (int i = candles.size() - 1; i >= 0; i--) {
				logger.debug(i + ") " + candles.get(i).toString());
				// мы идем с правого края массива к левому
				int nextIndex01 = i - 1;
				if (nextIndex01 >= 1) {
					int nextIndex02 = i - 2;
					if (nextIndex02 >= 0) {
						// только здесь мы можем проверить значение цен на экстремум
						double price01 = candles.get(i).getHigh();
						double price02 = candles.get(nextIndex01).getHigh();
						double price03 = candles.get(nextIndex02).getHigh();

						if ((price02 > price01) && (price02 > price03)) {
							maxCandle = candles.get(nextIndex01);
							logger.debug("Extremum high candle is {}", maxCandle.toString());
							break;
						}
					}
				}
			}

			Candle minCandle = null;
			for (int i = candles.size() - 1; i >= 0; i--) {
				logger.debug(i + ") " + candles.get(i).toString());
				// мы идем с правого края массива к левому
				int nextIndex01 = i - 1;
				if (nextIndex01 >= 1) {
					int nextIndex02 = i - 2;
					if (nextIndex02 >= 0) {
						// только здесь мы можем проверить значение цен на экстремум
						double price01 = candles.get(i).getLow();
						double price02 = candles.get(nextIndex01).getLow();
						double price03 = candles.get(nextIndex02).getLow();

						if ((price02 < price01) && (price02 < price03)) {
							minCandle = candles.get(nextIndex01);
							logger.debug("Extremum min candle is {}", minCandle.toString());
							break;
						}
					}
				}
			}

			// формируем файл детального отчета
			String prefix = this.taskProperties.getProperty(Constants.PREFIX);
			String detailReportPath = this.getDefTaskProperty(Constants.REPORT_HTML_PATH,
					Constants.REPORT_HTML_PATH_DEF_VALUE) + "/" + prefix + "/";
			String detailReportFileName = new SimpleDateFormat(Constants.FILE_NAME_PREFIX_DATE_FORMAT)
					.format(new Date()) + "_" + prefix + "_detail.html";
			String detailReportFile = detailReportPath + detailReportFileName;
			String detailReportFileLink = "./" + prefix + "/" + detailReportFileName;

			logger.info("Generate detail HTML report: {}{}", detailReportPath, detailReportFileName);

			String detailReportImagesPath = detailReportPath + Constants.REPORT_IMAGES_PATH_VALUE + "/";
			logger.debug("Detail HTML report images path: {}", detailReportImagesPath);

			try {
				// создаем целевые каталоги, если их нет
				new File(detailReportPath).mkdirs();
				new File(detailReportImagesPath).mkdirs();

			} catch (Exception ex) {
				error("in Strategy Peak execute: {} ", ex.toString());
			}

			// рисуем рисунки
			Candlestick candlestick = new Candlestick(logger);
			List<Object> additionalItems = new ArrayList<Object>();

			if (maxCandle != null) {
				// уровень max Price
				int startIndex = candles.indexOf(maxCandle) - 1;
				int endIndex = candles.size()-1;
				double level = maxCandle.getHigh();
				LevelLine ll = new LevelLine(level, new FixedMillisecond(candles.get(startIndex).getBegin().getTime()),
						new FixedMillisecond(candles.get(endIndex).getBegin().getTime()),
						String.valueOf(maxCandle.getHigh()), ChartColor.DARK_MAGENTA, 1.5f, TimedLine.Type.Solid); // аннотация

				ru.slayter.stock.charts.items.TextAnnotation ll_caption = new ru.slayter.stock.charts.items.TextAnnotation(
						candles.get(endIndex).getBegin().getTime(), level+0.0001, Double.toString(level), 0, ChartColor.WHITE,
						ChartColor.DARK_MAGENTA, TextAnchor.BOTTOM_CENTER);
				ll.setAnnotation(ll_caption);

				additionalItems.add(ll);
			}

			if (minCandle != null) {
				// уровень min Price
				int startIndex = candles.indexOf(minCandle) - 1;
				int endIndex = candles.size()-1;
				double level = minCandle.getLow();
				LevelLine ll = new LevelLine(level, new FixedMillisecond(candles.get(startIndex).getBegin().getTime()),
						new FixedMillisecond(candles.get(endIndex).getBegin().getTime()),
						String.valueOf(maxCandle.getHigh()), ChartColor.DARK_MAGENTA, 1.5f, TimedLine.Type.Solid); // аннотация

				ru.slayter.stock.charts.items.TextAnnotation ll_caption = new ru.slayter.stock.charts.items.TextAnnotation(
						candles.get(endIndex).getBegin().getTime(), level+0.0001, Double.toString(level), 0, ChartColor.WHITE,
						ChartColor.DARK_MAGENTA, TextAnchor.BOTTOM_CENTER);
				ll.setAnnotation(ll_caption);

				additionalItems.add(ll);
			}

			/*
			 * // тренд int startIndex = 3; TimedPoint startPoint = new TimedPoint(new
			 * FixedMillisecond(candles.get(startIndex).getBegin().getTime()),
			 * candles.get(startIndex).getClose()); int endIndex = candles.size() - 3;
			 * TimedPoint endPoint = new TimedPoint(new
			 * FixedMillisecond(candles.get(endIndex).getBegin().getTime()),
			 * candles.get(endIndex).getClose());
			 * 
			 * TrendLine tl = new TrendLine(startPoint, endPoint, "Test trend line",
			 * ChartColor.DARK_BLUE, 2.5f, TimedLine.Type.Dotted); // аннотация
			 * ru.slayter.stock.charts.items.PointerAnnotation label = new
			 * ru.slayter.stock.charts.items.PointerAnnotation(
			 * candles.get(endIndex).getBegin().getTime(), candles.get(endIndex).getHigh(),
			 * Double.toString(candles.get(endIndex).getHigh()), 1.25 * Math.PI,
			 * ChartColor.DARK_BLUE, ChartColor.WHITE, 20, 5, TextAnchor.BOTTOM_CENTER);
			 * tl.setAnnotation(label); logger.debug("Trend line: {}", tl.toString());
			 * additionalItems.add(tl);
			 * 
			 * 
			 * 
			 * // маркер Marker marker = new Marker(candles.get(5).getLow(), "Low price",
			 * ChartColor.DARK_MAGENTA); additionalItems.add(marker);
			 * 
			 * // маркерная точка ru.slayter.stock.charts.items.PointerAnnotation
			 * pointCaption = new ru.slayter.stock.charts.items.PointerAnnotation(
			 * candles.get(7).getBegin().getTime(), candles.get(7).getHigh(),
			 * Double.toString(candles.get(7).getHigh()), 1.25 * Math.PI, ChartColor.WHITE,
			 * ChartColor.VERY_DARK_GREEN, 20, 5, TextAnchor.BOTTOM_CENTER); MarkedPoint
			 * point = new MarkedPoint(candles.get(7).getBegin(), candles.get(7).getHigh(),
			 * ChartColor.VERY_DARK_GREEN, pointCaption); additionalItems.add(point);
			 * 
			 * // индикатор ArrayList<TimedPoint> points = new ArrayList<>(); for (Candle
			 * candle : candles) { points.add(new TimedPoint(new
			 * FixedMillisecond(candle.getBegin().getTime()), candle.getClose())); }
			 * Indicator indicator = new Indicator(points, "Closed time indicator",
			 * ChartColor.VERY_DARK_BLUE, 1.5f, Type.Solid, 8);
			 * additionalItems.add(indicator);
			 */
			// имя файла картинки
			String imageFile01Name = new SimpleDateFormat(Constants.FILE_NAME_PREFIX_DATE_FORMAT).format(new Date())
					+ "_" + prefix + "_image01.png"; // имя собственно файла
			String imageFile01FullName = detailReportImagesPath + imageFile01Name; // полное имя файла
			String imageFile01Link = "./" + Constants.REPORT_IMAGES_PATH_VALUE + "/" + imageFile01Name; // линк на файл
			int image01Width = 800;
			int image01Height = 600;
			candlestick.createImage(candles, emitent.getTicker(), image01Width, image01Height, additionalItems,
					imageFile01FullName);

			// пишем файл детального отчета
			BufferedWriter fw = new BufferedWriter(
					new OutputStreamWriter(new FileOutputStream(detailReportFile), StandardCharsets.UTF_8));
			try {

				Html html = new Html(null);
				html.setPrependDocType(true);
				Head head = new Head(html);
				new Meta(head, new HttpEquiv("Content-Type"), new Content("text/html; charset=utf-8"));
				new TitleTag(head) {
					private static final long serialVersionUID = 3562898178205324696L;
					{
						new NoTag(this, prefix + "_detail");
					}
				};
				new StyleTag(head, new CustomAttribute("type", "text/css")) {
					private static final long serialVersionUID = 3291456522571549739L;
					{
						new NoTag(this, "#pic { 	text-align: center; 	}");
						new NoTag(this, "#text { 	align: justify;	}");
						new NoTag(this, "P {	text-indent: 25pt;	text-align: justify;	}");
					}
				};
				Body body = new Body(html);

				// first question header
				new H2(body) {
					private static final long serialVersionUID = -7879466686368454076L;
					{
						new NoTag(this, "First question");
					}
				};

				// first question text
				new Div(body, new Id("text")) {
					private static final long serialVersionUID = -223069886743724600L;
					{
						new P(this) {
							private static final long serialVersionUID = 1848781743269799467L;
							{
								new NoTag(this, "First question answer");
							}
						};
					}
				};

				// first question image
				new Div(body, new Id("pic")) {
					private static final long serialVersionUID = 8021444205173352101L;
					{
						new Img(this, new Src(imageFile01Link), new Width(String.valueOf(image01Width)),
								new Height(String.valueOf(image01Height)), new Alt(imageFile01Name));
					}
				};

				// second question header
				new H2(body) {
					private static final long serialVersionUID = -6720880369304251055L;
					{
						new NoTag(this, "Second question");
					}
				};

				// Second question text
				new Div(body, new Id("text")) {
					private static final long serialVersionUID = -5943705781692573219L;
					{
						new P(this) {
							private static final long serialVersionUID = 1848781743269799467L;
							{
								new NoTag(this, "Second question answer");
							}
						};
					}
				};

				// Second question image
				new Div(body, new Id("pic")) {
					private static final long serialVersionUID = 3284870454018963017L;
					{
						new Img(this, new Src(imageFile01Link), new Width(String.valueOf(image01Width)),
								new Height(String.valueOf(image01Height)), new Alt(imageFile01Name));
					}
				};

				fw.write(html.toHtmlString());
			} catch (IOException iox) {
				error("Error creating report file {}", iox.getMessage());
			} finally {
				fw.close();
			}
			logger.info("File {} successfully writed.", detailReportPath + detailReportFileName);

			// вопросы:
			// 1. Какая сейчас тенденция? Рост/падение/боковик
			// 2. В какой фазе своего развития находится тенденция? Произошел пробой или
			// нет, коррекция или направленное движение
			// 3. Где находятся основные уровни сопротивления?
			// 4. Где находятся промежуточные уровни поддержки ?
			// 5. Где находятся основные уровни поддержки?
			// 6. Где находятся промежуточные уровни сопротивления?
			// 7. Если тенденция - боковик - где находятся нижние и верхние его границы?
			// 8. Какая волатильность - низкая или высокая?
			// 9. Какова ликвидность акции?
			// 10. Что говорят обьемы торгов, поддерживается ли движение объемами?
			// 11. Не превышают ли объемы торгов последнее время? Не появился ли крупный
			// игрок? Что он делает - покупает или продает?

			// Так на всех трех таймфреймах.
			// По итогам - рекомендация, что конкретно делать.

			// Расчет открытого риска - фин результат по сделке = ((цена продажи - цена
			// покупки) / цена покупки) * 100
			// 100 - весь капитал.
			// расчет - какой процент от капитала будет приемлем в качестве риска для данной
			// позиции?
			// рассчитаем коэффициент риска = открытый риск / желаемый риск
			// размер позиции = размер депозита /коэффициент риска

			// заполняем заключение
			StringBuilder resume = new StringBuilder(); 
			if (lastCandle.getClose() > maxCandle.getHigh()) {
				resume.append("Цена закрытия последней свечи ").append(String.valueOf(lastCandle.getClose()));
				resume.append(" превысила последний уровень сопротивления ").append(String.valueOf(maxCandle.getHigh())).append(". ");
				resume.append("Образовался пробой уровня сопротивления.");
			} else if (lastCandle.getClose() < minCandle.getLow()) {
				resume.append("Цена закрытия последней свечи ").append(String.valueOf(lastCandle.getClose()));
				resume.append(" пробила вниз последний уровень поддержки ").append(String.valueOf(minCandle.getLow())).append(". ");
				resume.append("Образовался пробой уровня поддержки.");
			} else {
				resume.append("Пробоя не зафиксировано.");
			}
			
			this.strategyResult.setAnalysisResume(resume.toString());
			logger.debug("Link to detail report file: {}", detailReportFileLink);
			this.strategyResult.setDetailLink(detailReportFileLink);

		} catch (Exception e) {
			logger.error(e.getMessage());
		}

		return this.strategyResult;
	}

}
