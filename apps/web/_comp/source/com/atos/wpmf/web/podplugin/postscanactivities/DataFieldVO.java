package com.atos.wpmf.web.podplugin.postscanactivities;

public class DataFieldVO {
	private String handle;
	private String dataFieldBO;
	private boolean required;
	public String getHandle() {
		return handle;
	}
	public void setHandle(String handle) {
		this.handle = handle;
	}
	public String getDataFieldBO() {
		return dataFieldBO;
	}
	public void setDataFieldBO(String dataFieldBO) {
		this.dataFieldBO = dataFieldBO;
	}
	public boolean isRequired() {
		return required;
	}
	public void setRequired(boolean required) {
		this.required = required;
	}
	
}
