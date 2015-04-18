package com.atos.ws.production.postscanactivities.datacollection;


public class DCParemeterDataVO {
 private String parameterName;
 private String parameterStatus;
 private String minValue;
 private String maxValue;
 private String parameterRef;
 private String actualValue;
 private String dcComment;
 private String actualNumber;
 private String originalComment;

public String getActualNumber() {
	return actualNumber;
}
public void setActualNumber(String actualNumber) {
	this.actualNumber = actualNumber;
}
public String getOriginalComment() {
	return originalComment;
}
public void setOriginalComment(String originalComment) {
	this.originalComment = originalComment;
}
public String getActualValue() {
	return actualValue;
}
public void setActualValue(String actualValue) {
	this.actualValue = actualValue;
}
public String getDcComment() {
	return dcComment;
}
public void setDcComment(String dcComment) {
	this.dcComment = dcComment;
}
public String getParameterName() {
	return parameterName;
}
public void setParameterName(String parameterName) {
	this.parameterName = parameterName;
}
public String getParameterStatus() {
	return parameterStatus;
}
public void setParameterStatus(String parameterStatus) {
	this.parameterStatus = parameterStatus;
}

public String getMinValue() {
	return minValue;
}
public void setMinValue(String minValue) {
	this.minValue = minValue;
}
public String getMaxValue() {
	return maxValue;
}
public void setMaxValue(String maxValue) {
	this.maxValue = maxValue;
}
public String getParameterRef() {
	return parameterRef;
}
public void setParameterRef(String parameterRef) {
	this.parameterRef = parameterRef;
}
}
