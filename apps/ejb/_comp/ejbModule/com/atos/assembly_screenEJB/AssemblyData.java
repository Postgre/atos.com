package com.atos.assembly_screenEJB;

import java.sql.Date;

public class AssemblyData 
{

	private String handle;
	private String sfc_bo;
	private String workcenter_bo;
	private String host_operation;
	private String slave_operation;
	private String host_operation_bo;
	private String slave_operation_bo;
	private String validate_element;
	private int active;
	private Date created_date;
	private Date updated_date;
	private String created_by;
	private String updated_by;
	
	public String getHost_operation_bo() {
		return host_operation_bo;
	}
	public void setHost_operation_bo(String hostOperationBo) {
		host_operation_bo = hostOperationBo;
	}
	public String getSlave_operation_bo() {
		return slave_operation_bo;
	}
	public void setSlave_operation_bo(String slaveOperationBo) {
		slave_operation_bo = slaveOperationBo;
	}
	public String getHandle() {
		return handle;
	}
	public void setHandle(String handle) {
		this.handle = handle;
	}
	public String getSfc_bo() {
		return sfc_bo;
	}
	public void setSfc_bo(String sfcBo) {
		sfc_bo = sfcBo;
	}
	public String getWorkcenter_bo() {
		return workcenter_bo;
	}
	public void setWorkcenter_bo(String workcenterBo) {
		workcenter_bo = workcenterBo;
	}
	
	public String getHost_operation() {
		return host_operation;
	}
	public void setHost_operation(String hostOperation) {
		host_operation = hostOperation;
	}
	public String getSlave_operation() {
		return slave_operation;
	}
	public void setSlave_operation(String slaveOperation) {
		slave_operation = slaveOperation;
	}
	public String getValidate_element() {
		return validate_element;
	}
	public void setValidate_element(String validateElement) {
		validate_element = validateElement;
	}
	public int getActive() {
		return active;
	}
	public void setActive(int active) {
		this.active = active;
	}
	public Date getCreated_date() {
		return created_date;
	}
	public void setCreated_date(Date createdDate) {
		created_date = createdDate;
	}
	public Date getUpdated_date() {
		return updated_date;
	}
	public void setUpdated_date(Date updatedDate) {
		updated_date = updatedDate;
	}
	public String getCreated_by() {
		return created_by;
	}
	public void setCreated_by(String createdBy) {
		created_by = createdBy;
	}
	public String getUpdated_by() {
		return updated_by;
	}
	public void setUpdated_by(String updatedBy) {
		updated_by = updatedBy;
	}
	
	
}
