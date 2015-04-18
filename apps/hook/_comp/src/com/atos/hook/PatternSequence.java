package com.atos.hook;

import java.sql.Date;

public class PatternSequence {
	private String handle;
	private String patternBo;
	private String patternAttribute;
	private String PatternValue;
	private String patternValueType;
	private String currentVersion;
	private int sequenceNo;	
	private Date createdOn;
	private String createdBy;
	
	
	public String getPatternValueType() {
		return patternValueType;
	}
	public void setPatternValueType(String patternValueType) {
		this.patternValueType = patternValueType;
	}
	public int getSequenceNo() {
		return sequenceNo;
	}
	public void setSequenceNo(int sequenceNo) {
		this.sequenceNo = sequenceNo;
	}
	public String getHandle() {
		return handle;
	}
	public void setHandle(String handle) {
		this.handle = handle;
	}
	public String getPatternBo() {
		return patternBo;
	}
	public void setPatternBo(String patternBo) {
		this.patternBo = patternBo;
	}
	public String getPatternAttribute() {
		return patternAttribute;
	}
	public void setPatternAttribute(String patternAttribute) {
		this.patternAttribute = patternAttribute;
	}
	public String getPatternValue() {
		return PatternValue;
	}
	public void setPatternValue(String patternValue) {
		PatternValue = patternValue;
	}
	public String getCurrentVersion() {
		return currentVersion;
	}
	public void setCurrentVersion(String currentVersion) {
		this.currentVersion = currentVersion;
	}
	public Date getCreatedOn() {
		return createdOn;
	}
	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}
	public String getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	
	
	
}
