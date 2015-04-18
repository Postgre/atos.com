package com.atos.ws.production;

import java.util.List;

import com.atos.ws.production.postscanactivities.datacollection.DCGroupDataVO;

public class DataCollectionRequest {
	private String site;	
	private String operation;
	private String operationRef;
	private String resource;
	private String resourceRef;
	private String sfc ;
	private String comments;
	private String userRefDetails;
	
	public String getUserRefDetails() {
		return userRefDetails;
	}
	public void setUserRefDetails(String userRefDetails) {
		this.userRefDetails = userRefDetails;
	}
	private List<DCGroupDataVO> dcGroupDataVOList;
	
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
	public String getSite() {
		return site;
	}
	public void setSite(String site) {
		this.site = site;
	}
	public String getOperation() {
		return operation;
	}
	public List<DCGroupDataVO> getDcGroupDataVOList() {
		return dcGroupDataVOList;
	}
	public void setDcGroupDataVOList(List<DCGroupDataVO> dcGroupDataVOList) {
		this.dcGroupDataVOList = dcGroupDataVOList;
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