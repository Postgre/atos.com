package com.atos.assembly_screen;

public class MaterialCustomDataItem 
{
	
	private String handle;
	private String sfc_bo;
	private String workcenter_bo;
	private String host_operation;
	private String materialItem;
	private String slave_operation;
	private String host_operation_bo;
	private String slave_operation_bo;
	private String validate_element;
	private String material;
	private boolean select;
	private boolean editable;
	private boolean matarialValueEnable;
	private boolean error;
	private String errorMessage;
	
	
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
	public String getHandle() {
		return handle;
	}
	public void setHandle(String handle) {
		this.handle = handle;
	}
	public String getMaterialItem() {
		return materialItem;
	}
	public void setMaterialItem(String materialItem) {
		this.materialItem = materialItem;
	}
	public boolean isSelect() {
		return select;
	}
	public void setSelect(boolean select) {
		this.select = select;
	}
	public boolean isEditable() {
		return editable;
	}
	public void setEditable(boolean editable) {
		this.editable = editable;
	}
	
	public boolean isMatarialValueEnable() {
		return matarialValueEnable;
	}
	public void setMatarialValueEnable(boolean matarialValueEnable) {
		this.matarialValueEnable = matarialValueEnable;
	}
	public boolean isError() {
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
	public String getMaterial() {
		return material;
	}
	public void setMaterial(String material) {
		this.material = material;
	}
	
	
}
