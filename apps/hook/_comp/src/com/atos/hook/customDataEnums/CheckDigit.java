package com.atos.hook.customDataEnums;

public enum CheckDigit {
	DOMESTIC ("0"),EXPORT("1");
	 
	private String checkDigitVal;
 
	private CheckDigit(String checkDigitVal) {
		this.checkDigitVal = checkDigitVal;
	}
	public  String getCheckDigitVal() {
		 return this.checkDigitVal;
	}
}
