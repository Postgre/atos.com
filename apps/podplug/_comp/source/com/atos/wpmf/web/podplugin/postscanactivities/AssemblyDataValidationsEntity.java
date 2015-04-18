package com.atos.wpmf.web.podplugin.postscanactivities;

import java.sql.Date;

public class AssemblyDataValidationsEntity {
/***actual operation **/
 private String firstOperation;
 /**operation for which as built summary needs to be checked */ 
 private String checkOperation;
 /**sub component name for  assembly */ 
 private String material;
 /**boolean which decides assembly data to be validated or not */ 
 private boolean validate; 
 /**assembly data that need to be validated*/ 
 private String checkField;
 /**this field tells validation required for ALL/SFC*/ 
 private String validateFor;
 /**if validateFor = 'SFC' sfc will have valid sfc value*/ 
 private String sfc;
 /**uniquely identifies the table row*/
 private String handle;
 private Date updatedOn;
 private Date createdOn;
 private String createdBy;
 private String updatedBy;
 
 
 
public String getCheckField() {
	return checkField;
}
public void setCheckField(String checkField) {
	this.checkField = checkField;
}
public String getSfc() {
	return sfc;
}
public void setSfc(String sfc) {
	this.sfc = sfc;
}
public String getHandle() {
	return handle;
}
public void setHandle(String handle) {
	this.handle = handle;
}
public Date getUpdatedOn() {
	return updatedOn;
}
public void setUpdatedOn(Date updatedOn) {
	this.updatedOn = updatedOn;
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
public String getUpdatedBy() {
	return updatedBy;
}
public void setUpdatedBy(String updatedBy) {
	this.updatedBy = updatedBy;
}
public String getValidateFor() {
	return validateFor;
}
public void setValidateFor(String validateFor) {
	this.validateFor = validateFor;
}
public String getFirstOperation() {
	return firstOperation;
}
public void setFirstOperation(String firstOperation) {
	this.firstOperation = firstOperation;
}
public String getCheckOperation() {
	return checkOperation;
}
public void setCheckOperation(String checkOperation) {
	this.checkOperation = checkOperation;
}
public String getMaterial() {
	return material;
}
public void setMaterial(String material) {
	this.material = material;
}
public boolean isValidate() {
	return validate;
}
public void setValidate(boolean validate) {
	this.validate = validate;
}

}
