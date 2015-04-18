package com.atos.genericpattern;

public class PatternCustomDataItem {

	private Integer sequenceNo;
	private String patternAttribute;
	private String attributeValue;
	private String attributeValueDb;
	private boolean currentVesion;
	private boolean select;
	private boolean editable;
	private boolean attributeValueEnable;
	private boolean error;
	private String errorMessage;

	
	

	

	public String getAttributeValueDb() {
		return attributeValueDb;
	}

	public void setAttributeValueDb(String attributeValueDb) {
		this.attributeValueDb = attributeValueDb;
	}

	public boolean getError() {
		return error;
	}

	public void setError(boolean error) {
		this.error = error;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public boolean isAttributeValueEnable() {
		return attributeValueEnable;
	}

	public void setAttributeValueEnable(boolean attributeValueEnable) {
		this.attributeValueEnable = attributeValueEnable;
	}

	public boolean isEditable() {
		return editable;
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
	}

	public Integer getSequenceNo() {
		return sequenceNo;
	}

	public void setSequenceNo(Integer sequenceNo) {
		this.sequenceNo = sequenceNo;
	}

	public String getPatternAttribute() {
		return patternAttribute;
	}

	public void setPatternAttribute(String patternAttribute) {
		this.patternAttribute = patternAttribute;
	}

	public String getAttributeValue() {
		return attributeValue;
	}

	public void setAttributeValue(String attributeValue) {
		this.attributeValue = attributeValue;
	}

	public boolean isCurrentVesion() {
		return currentVesion;
	}

	public void setCurrentVesion(boolean currentVesion) {
		this.currentVesion = currentVesion;
	}

	public void setSelect(boolean select) {
		this.select = select;
	}

	public boolean isSelect() {
		return select;
	}
}

