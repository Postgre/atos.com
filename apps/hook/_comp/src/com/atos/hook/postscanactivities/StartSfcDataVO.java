package com.atos.hook.postscanactivities;

public class StartSfcDataVO {
	private String sfcRef;
	private String resourceRef;
	private String operationRef;
	public String getSfcRef() {
		return sfcRef;
	}
	public void setSfcRef(String sfcRef) {
		this.sfcRef = sfcRef;
	}
	public String getResourceRef() {
		return resourceRef;
	}
	public void setResourceRef(String resourceRef) {
		this.resourceRef = resourceRef;
	}
	public String getOperationRef() {
		return operationRef;
	}
	public void setOperationRef(String operationRef) {
		this.operationRef = operationRef;
	}
}
