package com.atos.hook.customDataEnums;

public enum Driver {
	 RHD("R"),LHD("L");
	 
	private String driverCode;
 
	private Driver(String driverCode) {
		this.driverCode = driverCode;
	}
	public  String getdriverCode() {
		 return this.driverCode;
	}
}
