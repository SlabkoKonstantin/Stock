package ru.slayter.stock.strategies;

import ru.slayter.stock.commons.Constants;

public class StrategyResult {
	
	private int errorCount; // число зафиксированных ошибок
	private String detailLink; // линк на файл с детализацией анализа
	private String analysisResume; // резюме анализа

	public StrategyResult() {
		super();
		this.errorCount = 0;
		this.detailLink = Constants.EMPTY;
		this.analysisResume = Constants.EMPTY;
	}

	public int getErrorCount() {
		return errorCount;
	}
	
	public void incErrorCount() {
		this.errorCount++;
	}

	public String getDetailLink() {
		return detailLink;
	}

	public void setDetailLink(String detailLink) {
		this.detailLink = detailLink;
	}

	public String getAnalysisResume() {
		return analysisResume;
	}

	public void setAnalysisResume(String analysisResume) {
		this.analysisResume = analysisResume;
	}	
	
}
