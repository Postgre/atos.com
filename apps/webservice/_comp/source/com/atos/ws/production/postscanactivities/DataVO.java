package com.atos.ws.production.postscanactivities;

import com.sap.me.production.SfcBasicData;

public class DataVO {
	private String operationRef;
	private String resourceRef;
	private String sfcRef;
	SfcBasicData basicData;
	private String vendor;
	private String product;
	private String quality;
	private String operationCnfirm;
	public String getOperationCnfirm() {
		return operationCnfirm;
	}
	public void setOperationCnfirm(String operationCnfirm) {
		this.operationCnfirm = operationCnfirm;
	}
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
	public String getSfcRef() {
		return sfcRef;
	}
	public void setSfcRef(String sfcRef) {
		this.sfcRef = sfcRef;
	}
	public SfcBasicData getBasicData() {
		return basicData;
	}
	public void setBasicData(SfcBasicData basicData) {
		this.basicData = basicData;
	}
	public String getVendor() {
		return vendor;
	}
	public void setVendor(String vendor) {
		this.vendor = vendor;
	}
	public String getProduct() {
		return product;
	}
	public void setProduct(String product) {
		this.product = product;
	}
	public String getQuality() {
		return quality;
	}
	public void setQuality(String quality) {
		this.quality = quality;
	}
}
