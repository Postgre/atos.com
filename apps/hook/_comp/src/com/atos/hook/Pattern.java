package com.atos.hook;

import java.sql.Date;

public class Pattern {
	private String handle;
	private String patternName;
	private String patternType;
	private String patternTypeMaster;
	private String patternTypeValue;
	private Date createdDate;
	private String createdBy;
	public String getHandle() {
		return handle;
	}
	public void setHandle(String handle) {
		this.handle = handle;
	}
	public String getPatternName() {
		return patternName;
	}
	public void setPatternName(String patternName) {
		this.patternName = patternName;
	}
	public String getPatternType() {
		return patternType;
	}
	public void setPatternType(String patternType) {
		this.patternType = patternType;
	}
	public String getPatternTypeMaster() {
		return patternTypeMaster;
	}
	public void setPatternTypeMaster(String patternTypeMaster) {
		this.patternTypeMaster = patternTypeMaster;
	}
	public String getPatternTypeValue() {
		return patternTypeValue;
	}
	public void setPatternTypeValue(String patternTypeValue) {
		this.patternTypeValue = patternTypeValue;
	}
	public Date getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}
	public String getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	
	
}
