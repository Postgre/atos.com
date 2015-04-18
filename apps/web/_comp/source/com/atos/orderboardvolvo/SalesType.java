package com.atos.orderboardvolvo;

public enum  SalesType {
	DOMESTIC ("D"), EXPORT ("E");
	 
	private String code;
 
	private SalesType(String s) {
		code = s;
	}
 
	public String getCode() {
		return code;
	}
}
