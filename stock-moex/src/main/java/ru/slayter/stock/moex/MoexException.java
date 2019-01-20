package ru.slayter.stock.moex;

public class MoexException extends Exception {

	private static final long serialVersionUID = 7441548630094426083L;

	public MoexException(String s) {
		super(s);
	}

	public MoexException(Exception e) {
		super(e);
	}
	
	
}
