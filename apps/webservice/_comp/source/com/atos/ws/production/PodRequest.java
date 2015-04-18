package com.atos.ws.production;

public class PodRequest {
	private String site;	
	private String scannedData;
	private String operation;
	private String operationRef;
	private String resourceRef;
	private String userRef;
	private String resource;
	private String sfc ;
	private String comments;
	
	public String getOperationRef() {
		return operationRef;
	}
	public void setOperationRef(String operationRef) {
		this.operationRef = operationRef;
	}
	public String getResourceRef() {
		return resourceRef;
	}
	public void setResourceRef(String resourceRef) {
		this.resourceRef = resourceRef;
	}
	public String getUserRef() {
		return userRef;
	}
	public void setUserRef(String userRef) {
		this.userRef = userRef;
	}
	public String getSite() {
		return site;
	}
	public void setSite(String site) {
		this.site = site;
	}
	public String getScannedData() {
		return scannedData;
	}
	public void setScannedData(String scannedData) {
		this.scannedData = scannedData;
	}
	public String getOperation() {
		return operation;
	}
	public void setOperation(String operation) {
		this.operation = operation;
	}
	public String getResource() {
		return resource;
	}
	public void setResource(String resource) {
		this.resource = resource;
	}
	public String getSfc() {
		return sfc;
	}
	public void setSfc(String sfc) {
		this.sfc = sfc;
	}
	public String getComments() {
		return comments;
	}
	public void setComments(String comments) {
		this.comments = comments;
	}

	

}