package ru.slayter.stock.moex;

public class MoexConstants {

	// длина элемента json-объекта свечи
	public final static int ANSWER_CANDLE_LENGTH = 8;
	// короткий формат даты
	public final static String DEF_SHORT_DATE_FORMAT = "yyyy-MM-dd";
	// длинный формат даты
	public final static String DEF_LONG_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
	// таймфреймы свечей
	public enum CANDLE_TIME_FRAME {

		/*
		 * Значения параметра interval:
		 * 1 - 1-минутки
		 * 10 - 10-тиминутки
		 * 24 - суточные
		 * 60 - часовые
		 * 7 - недельные
		 * 31 - месячные
		 */
		
		ONE_MINUTE(1),
		TEN_MINUTE(10),
		HOUR(60),
		DAY(24),
		WEEK(7),
		MONTH(31);
		
		private final int interval;
		
		CANDLE_TIME_FRAME(int value) {
			interval = value;
		}
		
		public int getValue() {
			return interval;
		}
	}
}
