package com.atos.orderboardvolvo;

public class MaterialCustomDataItem {

	private String material;
	private String dataValue;
	private String dataField;
	private String outboundString;
	private String turnAroundNo;
	private String employeeNo;
	private String handler;
	private boolean select;
	private String orderno; 
	private String workcenter;
	private String qtyAvailable;
	private String qtytobebuild;
	private String laborchargecode;
	private String scheduledstartdate;
	private String scheduleenddate;
	private boolean disable;
	private boolean released;
	private String shopOrderRef;
	private String quantityToBeReleased;
	private int priority; 
	private boolean error;
	private String errorMessage;
	private boolean errorPriority;
	private String errorMessagePriority;	
	
	
	
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
	public String getQuantityToBeReleased() {
		return quantityToBeReleased;
	}
	public void setQuantityToBeReleased(String quantityToBeReleased) {
		this.quantityToBeReleased = quantityToBeReleased;
	}
	public String getShopOrderRef() {
		return shopOrderRef;
	}
	public void setShopOrderRef(String shopOrderRef) {
		this.shopOrderRef = shopOrderRef;
	}
	public boolean isReleased() {
		return released;
	}
	public void setReleased(boolean released) {
		this.released = released;
	}
	public String getDataField() {
		return dataField;
	}
	public void setDataField(String dataFiled) {
		this.dataField = dataFiled;
	}
	public String getWorkcenter() {
		return workcenter;
	}
	public void setWorkcenter(String workcenter) {
		this.workcenter = workcenter;
	}
	
	
	public String getTurnAroundNo() {
		return turnAroundNo;
	}
	public void setTurnAroundNo(String turnAroundNo) {
		this.turnAroundNo = turnAroundNo;
	}
	public String getEmployeeNo() {
		return employeeNo;
	}
	public void setEmployeeNo(String employeeNo) {
		this.employeeNo = employeeNo;
	}
	public String getHandler() {
		return handler;
	}
	public void setHandler(String handler) {
		this.handler = handler;
	}
	public String getOutboundString() {
		return outboundString;
	}
	public void setOutboundString(String outboundString) {
		this.outboundString = outboundString;
	}
	
	public String getMaterial() {
		return material;
	}
	public void setMaterial(String material) {
		this.material = material;
	}
	public void setDataValue(String dataValue) {
		this.dataValue = dataValue;
	}

	public String getDataValue() {
		return dataValue;
	}
	public void setSelect(boolean select) {
		this.select = select;
	}

	public int getPriority() {
		return priority;
	}
	public void setPriority(int priority) {
		this.priority = priority;
	}
	public boolean isErrorPriority() {
		return errorPriority;
	}
	public void setErrorPriority(boolean errorPriority) {
		this.errorPriority = errorPriority;
	}
	public String getErrorMessagePriority() {
		return errorMessagePriority;
	}
	public void setErrorMessagePriority(String errorMessagePriority) {
		this.errorMessagePriority = errorMessagePriority;
	}
	public boolean isSelect() {
		return select;
	}
	public String getOrderno() {
		return orderno;
	}
	public void setOrderno(String orderno) {
		this.orderno = orderno;
	}
	
	public String getQtyAvailable() {
		return qtyAvailable;
	}
	public void setQtyAvailable(String qtyAvailable) {
		this.qtyAvailable = qtyAvailable;
	}
	public String getQtytobebuild() {
		return qtytobebuild;
	}
	public void setQtytobebuild(String qtytobebuild) {
		this.qtytobebuild = qtytobebuild;
	}
	public String getLaborchargecode() {
		return laborchargecode;
	}
	public void setLaborchargecode(String laborchargecode) {
		this.laborchargecode = laborchargecode;
	}
	public String getScheduledstartdate() {
		return scheduledstartdate;
	}
	public void setScheduledstartdate(String scheduledstartdate) {
		this.scheduledstartdate = scheduledstartdate;
	}
	public String getScheduleenddate() {
		return scheduleenddate;
	}
	public void setScheduleenddate(String scheduleenddate) {
		this.scheduleenddate = scheduleenddate;
	}
	public boolean isDisable() {
		return disable;
	}
	public void setDisable(boolean disable) {
		this.disable = disable;
	}
	
}

