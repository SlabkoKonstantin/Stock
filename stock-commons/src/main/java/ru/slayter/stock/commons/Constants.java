package ru.slayter.stock.commons;

public final class Constants {
	public static final String DEFAULT_ADVISOR_CONFIG_PATH = "./etc/advisor_config.xml";
	public static final String EMPTY = "";
	public static final String FILE_NAME_PREFIX_DATE_FORMAT = "yyyyMMdd_HHmmss";
	public static final String LOG_PREFIX = "THREAD_LOGGERID";
	public static final String PREFIX = "PREFIX";
	public static final String CANLES_LINK = "CANDLES_LINK";
	public static final String TIME_FRAME = "TIME_FRAME";
	public static final String DAY = "DAY";
	public static final String DEPTH_VALUE = "DEPTH_VALUE";
	public static final String DEPTH_VALUE_DEFAULT = "20";
	public static final String DEPTH_TYPE = "DEPTH_TYPE";
	public static final String REPORT_HTML_PATH = "REPORT_HTML_PATH";
	public static final String REPORT_HTML_PATH_DEF_VALUE = "./reports";
	public static final String REPORT_IMAGES_PATH_VALUE = "images";

	public static enum DEPTH_TYPES {
		YEAR, MONTH, WEEK, DAY
	};
}
