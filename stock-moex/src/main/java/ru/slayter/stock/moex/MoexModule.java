package ru.slayter.stock.moex;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;

import ru.slayter.stock.commons.Candle;
import ru.slayter.stock.commons.Constants;
import ru.slayter.stock.commons.Emitent;
import ru.slayter.stock.moex.MoexConstants.CANDLE_TIME_FRAME;

/**
 * Moex library
 *
 */
public class MoexModule {
	private static final String USER_AGENT = "Mozilla/5.0 (X11; U; Linux x86_64; en-US; rv:1.9.2.13) Gecko/20101206 Ubuntu/10.10 (maverick) Firefox/3.6.13";
	private Logger logger;

	public MoexModule(Logger logger) {
		this.logger = logger;
	}

	private JSONArray getCandlesData(String link, Emitent emitent, LocalDate from, LocalDate till, int interval,
			int starts) throws MoexException, ClientProtocolException, IOException {
		JSONArray result = null;
		if ((link == null) || (link.isEmpty()) || (emitent == null) || (interval == 0)) {
			throw new MoexException("Incorrect input parameters in MoexMain.getCandles.");
		} else {
			// sections
			logger.debug("Source URL: {}", link);

			String url = link.toLowerCase().replaceAll("\\[engine\\]", emitent.getEngine());

			url = url.replaceAll("\\[market\\]", emitent.getMarket());
			url = url.replaceAll("\\[boards\\]", emitent.getBoard());
			url = url.replaceAll("\\[security\\]", emitent.getSecurity());

			// dates
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern(MoexConstants.DEF_SHORT_DATE_FORMAT);
			String strFromDate = from.format(formatter);
			String strTillDate = till.format(formatter);

			url = url.replaceAll("\\[from\\]", strFromDate);
			url = url.replaceAll("\\[till\\]", strTillDate);

			// interval
			url = url.replaceAll("\\[interval\\]", String.valueOf(interval));
			// starts
			url = url.replaceAll("\\[starts\\]", String.valueOf(starts));

			logger.info("URL: {}", url);

			// тянем котировки
			// proxy
			// HttpHost proxy = new HttpHost("127.0.0.1",8888);
			// DefaultProxyRoutePlanner routePlanner = new DefaultProxyRoutePlanner(proxy);
			// HttpClient client =
			// HttpClientBuilder.create().setRoutePlanner(routePlanner).build();

			HttpClient client = HttpClientBuilder.create().build();
			HttpGet request = new HttpGet(url);

			// add request header
			request.addHeader("User-Agent", USER_AGENT);
			HttpResponse response = client.execute(request);

			logger.debug("Response Code : {}", response.getStatusLine().getStatusCode());

			if (response.getStatusLine().getStatusCode() < 200 || response.getStatusLine().getStatusCode() >= 400) {
				throw new IOException("Got bad response, error code = " + response.getStatusLine().getStatusCode());
			} else {
				HttpEntity entity = response.getEntity();
				if (entity != null) {

					String answer = EntityUtils.toString(entity);
					EntityUtils.consume(entity);

					// парсим json
					JSONObject answerJsonObject = new JSONObject(answer);
					JSONObject candles = answerJsonObject.getJSONObject("candles");

					result = candles.getJSONArray("data");
					if (result.length() > 0) {
						logger.trace(result.toString());
					}
				}
			}
		}
		return result;
	}

	private ArrayList<Candle> getCandles(String link, Emitent emitent, LocalDate from, LocalDate till,
			CANDLE_TIME_FRAME frame)
			throws MoexException, ClientProtocolException, IOException, JSONException, ParseException {

		ArrayList<Candle> result = new ArrayList<>();
		JSONArray data = null;
		int starts = 0;
		Integer len = 0;

		do {
			data = getCandlesData(link, emitent, from, till, frame.getValue(), starts);
			if (data != null) {
				len = data.length();
				// logger.trace("len {}", len);
				starts = starts + len;

				for (int i = 0; i < data.length(); i++) {
					// logger.trace("json_data({}): ({})", i, data.get(i));
					JSONArray item = (JSONArray) data.get(i);
					// logger.trace("Answer candle length = {}", item.length());

					if (item.length() != MoexConstants.ANSWER_CANDLE_LENGTH) {
						throw new MoexException("Incorrect answer candle length.");
					} else {
						// вытягиваем информацию из json, попутно преобразуя ее в нужные форматы
						double open = ((Number) item.get(0)).doubleValue();
						// logger.trace("open = {}", open);
						double close = ((Number) item.get(1)).doubleValue();
						// logger.trace("close = {}", close);
						double high = ((Number) item.get(2)).doubleValue();
						// logger.trace("high = {}", high);
						double low = ((Number) item.get(3)).doubleValue();
						// logger.trace("low = {}", low);
						double value = ((Number) item.get(4)).doubleValue();
						// logger.trace("value = {}", value);
						double volume = ((Number) item.get(5)).doubleValue();
						// logger.trace("volume = {}", volume);
						Date begin = (new SimpleDateFormat(MoexConstants.DEF_LONG_DATE_FORMAT))
								.parse(String.valueOf(item.get(6)));
						// logger.trace("begin = {}", begin);
						Date end = (new SimpleDateFormat(MoexConstants.DEF_LONG_DATE_FORMAT))
								.parse(String.valueOf(item.get(7)));
						// logger.trace("end = {}", end);
						int timeFrame = frame.getValue();

						Candle candle = new Candle(open, close, high, low, value, volume, begin, end, timeFrame);
						result.add(candle);
						
					}

				}
			}
		} while ((len > 0) || (data == null));
		return result;
	}

	public ArrayList<Candle> getCandles(String link, Emitent emitent, int depth, Constants.DEPTH_TYPES measurement,
			CANDLE_TIME_FRAME frame)
			throws MoexException, ClientProtocolException, IOException, JSONException, ParseException {

		if (depth < 1) {
			throw new MoexException("Incorrect depth value: " + depth);
		}

		LocalDate tillDate = LocalDate.now();
		LocalDate fromDate;

		switch (measurement) {
		case YEAR:
			logger.debug("YEAR, {}", depth);
			fromDate = tillDate.minus(depth, ChronoUnit.YEARS);
			break;
		case MONTH:
			logger.debug("MONTH, {}", depth);
			fromDate = tillDate.minus(depth, ChronoUnit.MONTHS);
			break;
		case WEEK:
			logger.debug("WEEK, {}", depth);
			fromDate = tillDate.minus(depth, ChronoUnit.WEEKS);
			break;
		default:
			logger.debug("DAY, {}", depth);
			fromDate = tillDate.minus(depth, ChronoUnit.DAYS);
		}
		return getCandles(link, emitent, fromDate, tillDate, frame);
	}
}
