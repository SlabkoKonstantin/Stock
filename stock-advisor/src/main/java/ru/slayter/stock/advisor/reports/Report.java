package ru.slayter.stock.advisor.reports;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.CopyOnWriteArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webfirmframework.wffweb.tag.html.AbstractHtml.TagType;
import com.webfirmframework.wffweb.tag.html.Body;
import com.webfirmframework.wffweb.tag.html.Html;
import com.webfirmframework.wffweb.tag.html.attribute.Href;
import com.webfirmframework.wffweb.tag.html.attributewff.CustomAttribute;
import com.webfirmframework.wffweb.tag.html.links.A;
import com.webfirmframework.wffweb.tag.html.metainfo.Head;
import com.webfirmframework.wffweb.tag.html.stylesandsemantics.StyleTag;
import com.webfirmframework.wffweb.tag.html.tables.Table;
import com.webfirmframework.wffweb.tag.html.tables.Td;
import com.webfirmframework.wffweb.tag.html.tables.Th;
import com.webfirmframework.wffweb.tag.html.tables.Tr;
import com.webfirmframework.wffweb.tag.htmlwff.CustomTag;
import com.webfirmframework.wffweb.tag.htmlwff.NoTag;

import ru.slayter.stock.advisor.tasks.Task;
import ru.slayter.stock.commons.Constants;

public class Report {

	private String lastErrorMessage;
	private final static Logger logger = LoggerFactory.getLogger(Report.class);
	private CopyOnWriteArrayList<Task> tasksList;
	private String reportFileName;
	private String reportDirectory;

	public Report(CopyOnWriteArrayList<Task> tasksList, String directory) {
		String date = new SimpleDateFormat(Constants.FILE_NAME_PREFIX_DATE_FORMAT).format(new Date());
		this.reportDirectory = directory;
		this.reportFileName = ".//" + directory + "//" + date + "_report.html";
		this.tasksList = tasksList;
		this.lastErrorMessage = Constants.EMPTY;
	}

	public boolean create() {
		boolean wasError = false;

		logger.info("Generate HTML report...");

		try {
			new File(this.reportDirectory).mkdirs();

			BufferedWriter fw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(this.reportFileName), StandardCharsets.UTF_8));			
			
			try {
				Html html = new Html(null);
				Head head = new Head(html);				
				html.setPrependDocType(true); // prepends the doc type <!DOCTYPE html>
				//CustomTag metaTag = new CustomTag("meta", TagType.NON_CLOSING, head, new CustomAttribute("http-equiv", "Content-Type"), new CustomAttribute("content", "text/html; charset=utf-8"));
				new CustomTag("meta", TagType.NON_CLOSING, head, new CustomAttribute("http-equiv", "Content-Type"), new CustomAttribute("content", "text/html; charset=utf-8"));
				
				//StyleTag styleTag = new StyleTag(head, new CustomAttribute("type","text/css")) {
				new StyleTag(head, new CustomAttribute("type","text/css")) {
					private static final long serialVersionUID = 3291456522571549739L;

					{
		    	        new NoTag(this, "TABLE {border-collapse: collapse;}");
		    	        new NoTag(this, "TH {background: Silver;text-align: center;}");
		    	        new NoTag(this, "TD {vertical-align: top;background: #fff;}");
		    	        new NoTag(this, "TH, TD {border: 1px solid black;padding: 2px;}");
		    	        new NoTag(this, ".bar {border: 1px solid black;background: lightgreen;}");
		    	        new NoTag(this, ".c {border: 0px;}.a {width: 150px;}.b {width: 90px;}.tdc {text-align: center;");
		    	        new NoTag(this, "}");
		    	    }
		    	};

				// Td td1 = new Td(this, new CustomAttribute("colspan", "3"))
				
				//<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
				//<style type="text/css">TABLE {border-collapse: collapse;}TH {background: Silver;text-align: center;}TD {vertical-align: top;background: #fff;}TH, TD {border: 1px solid black;padding: 2px;}.bar {border: 1px solid black;background: lightgreen;}.c {border: 0px;}.a {width: 150px;}.b {width: 90px;}.tdc {text-align: center;}</style>

				Body body = new Body(html);

				// основная таблица
				Table table = new Table(body);
				// строка - заголовок
				Tr headerRow = new Tr(table);
				
				// эмитент
				Th headerEmitent = new Th(headerRow);
				new NoTag(headerEmitent,"Эмитент");

				// стратегия
				Th headerStrategy = new Th(headerRow);
				new NoTag(headerStrategy,"Стратегия");
				
				// размер лота
				Th headerLotSize = new Th(headerRow);
				new NoTag(headerLotSize,"Размер лота");

				// шорт
				Th headerIsShort = new Th(headerRow);
				new NoTag(headerIsShort,"Шорт");
							
				// резюме
				Th headerAnalysisResume = new Th(headerRow);
				new NoTag(headerAnalysisResume,"Рекомендация");
				
				// детализация
				Th headerDetailLink = new Th(headerRow);
				new NoTag(headerDetailLink,"Подробно");
								
				for (Task task : tasksList) {
					// задача
					Tr tr = new Tr(table);

					// эмитент
					Td ticker = new Td(tr);
					new NoTag(ticker, task.getEmitent().getTicker());
					
					// стратегия
					Td strategy = new Td(tr);
					new NoTag(strategy, task.getStrategy().getSign());					

					// размер лота
					Td lotSize = new Td(tr);
					new NoTag(lotSize, String.valueOf(task.getEmitent().getLotSize()));
					
					// возможен ли шорт
					Td isShort = new Td(tr);
					new NoTag(isShort, (task.getEmitent().isShortAllowed()?"Да":Constants.EMPTY));

					// рекомендация
					Td conclusion = new Td(tr);
					new NoTag(conclusion, task.getStrategy().getResult().getAnalysisResume());
					
					// детализация
					Td details = new Td(tr);
					if (task.getStrategy().getResult().getDetailLink().isEmpty()) {
						new NoTag(details, Constants.EMPTY);
					} else {
						logger.debug("Link to detail report file is {}", task.getStrategy().getResult().getDetailLink());
						Href ref = new Href(task.getStrategy().getResult().getDetailLink());
	                    A a = new A(details, ref);
	                    new NoTag(a,"отчет...");						
					}
					
				}
				fw.write(html.toHtmlString());
			} catch (IOException iox) {
				wasError = true;
				lastErrorMessage = "Error creating report file: " + iox.getMessage();
			} finally {
				fw.close();
			}
			logger.info("File {} successfully writed.", this.reportFileName);
		} catch (Exception ex) {
			wasError = true;
			lastErrorMessage = "Error creating report file: " + ex.getLocalizedMessage();
		}
		return wasError;
	}

	public String getLastErrorMessage() {
		return lastErrorMessage;
	}

}