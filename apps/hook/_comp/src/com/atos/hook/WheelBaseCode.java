package com.atos.hook;

public enum WheelBaseCode {
	WHEELBASE19 ("J");
	 
	private String wheelBaseCode;
 
	private WheelBaseCode(String wheelBaseCode) {
		this.wheelBaseCode = wheelBaseCode;
	}
	public  String getWheelBaseCode() {
		 return this.wheelBaseCode;
	}
}
