package com.atos.genericpattern;

public class PatternItem {

	

	private String value;
	private String version;
	private String dbValue;
	private Boolean selected = new Boolean(false);
	private RouterRef routerRef;

	public PatternItem() {

	}

	public PatternItem(String value) {
		this.value=value;
	}
	public PatternItem(String value,String dbValue) {
		this.value=value;
		this.dbValue=dbValue;
	}
	public String getDbValue() {
		return dbValue;
	}

	public void setDbValue(String dbValue) {
		this.dbValue = dbValue;
	}
	
	public RouterRef getRouterRef() {
		return routerRef;
	}

	public void setRouterRef(RouterRef routerRef) {
		this.routerRef = routerRef;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getVersion() {
		return version;
	}

	public void setSelected(Boolean selected) {
		this.selected = selected;
	}

	public Boolean getSelected() {
		return selected;
	}

}
