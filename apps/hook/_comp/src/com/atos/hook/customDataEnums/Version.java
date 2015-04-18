package com.atos.hook.customDataEnums;

public enum Version {
	CAB ("C"),COWL("F"),THROW_AWAY_COWL("T");
	 
	private String versionVal;
 
	private Version(String versionVal) {
		this.versionVal = versionVal;
	}
	public  String getVersionVal() {
		 return this.versionVal;
	}
}
