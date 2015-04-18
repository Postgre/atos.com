package com.atos.ordersequenceEJB;

import java.util.Date;

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
	
	/***sequencing changes******/
	private String item;
	private String workcenter;
	private String sfc;
	private int sequence;
	private Date date;
	public String startDate;
	public String createDate;
	public java.sql.Date sqlDate;
	private Date scheduleStartDate;
	private Date createon;
	private String createby;
	private String sfcBOfrmPPC;
	private String sfcBO;
	private String model;
	private String wheelbase;
	private String country;
	private String color;
	private String drive;
	
	
	public String getModel() {
		return model;
	}
	public void setModel(String model) {
		this.model = model;
	}
	public String getWheelbase() {
		return wheelbase;
	}
	public void setWheelbase(String wheelbase) {
		this.wheelbase = wheelbase;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getColor() {
		return color;
	}
	public void setColor(String color) {
		this.color = color;
	}
	public String getDrive() {
		return drive;
	}
	public void setDrive(String drive) {
		this.drive = drive;
	}
	
	
	public String getSfcBOfrmPPC() {
		return sfcBOfrmPPC;
	}

	public void setSfcBOfrmPPC(String sfcBOfrmPPC) {
		this.sfcBOfrmPPC = sfcBOfrmPPC;
	}

	public String getCreateDate() {
		return createDate;
	}

	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}

	public Date getCreateon() {
		return createon;
	}

	public void setCreateon(Date createon) {
		this.createon = createon;
	}

	
//	WorkCenterDO wo = new WorkCenterDO();
//	wo.getWorkCenter();
	
	public String getCreateby() {
		return createby;
	}

	public void setCreateby(String createby) {
		this.createby = createby;
	}

	public Date getScheduleStartDate() {
		return scheduleStartDate;
	}

	public void setScheduleStartDate(Date scheduleStartDate) {
		this.scheduleStartDate = scheduleStartDate;
	}

	public java.sql.Date getSqlDate() {
		return sqlDate;
	}

	public void setSqlDate(java.sql.Date sqlDate) {
		this.sqlDate = sqlDate;
	}
	/*** Sequence SFC Release***/
	private String handle;
	public String getHandle() {
		return handle;
	}

	public void setHandle(String handle) {
		this.handle = handle;
	}
	private String itembo;
	private String sfcbo;
	private String orderbo;
	private String parent_orderbo;
	private String workcenterbo;
	private int priority1;
	private int priorityBeforeUpdating;
	private Date plannedstartdate;
	private String special_int;
	private String export_import;
	private int active;
	private int status;
	private String created_on;

	
	public int getPriorityBeforeUpdating() {
		return priorityBeforeUpdating;
	}

	public void setPriorityBeforeUpdating(int priorityBeforeUpdating) {
		this.priorityBeforeUpdating = priorityBeforeUpdating;
	}

	public String getCreated_on() {
		return created_on;
	}

	public void setCreated_on(String createdOn) {
		created_on = createdOn;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getActive() {
		return active;
	}

	public void setActive(int active) {
		this.active = active;
	}
	private boolean export_import2;
	
	public boolean isExport_import2() {
		return export_import2;
	}

	public void setExport_import2(boolean exportImport2) {
		export_import2 = exportImport2;
	}

	public String getItembo() {
		return itembo;
	}

	public void setItembo(String itembo) {
		this.itembo = itembo;
	}

	public String getSfcbo() {
		return sfcbo;
	}

	public void setSfcbo(String sfcbo) {
		this.sfcbo = sfcbo;
	}

	public String getOrderbo() {
		return orderbo;
	}

	public void setOrderbo(String orderbo) {
		this.orderbo = orderbo;
	}

	public String getParent_orderbo() {
		return parent_orderbo;
	}

	public void setParent_orderbo(String parentOrderbo) {
		parent_orderbo = parentOrderbo;
	}

	public String getWorkcenterbo() {
		return workcenterbo;
	}

	public void setWorkcenterbo(String workcenterbo) {
		this.workcenterbo = workcenterbo;
	}

	public int getPriority1() {
		return priority1;
	}

	public void setPriority1(int priority1) {
		this.priority1 = priority1;
	}

	public Date getPlannedstartdate() {
		return plannedstartdate;
	}

	public void setPlannedstartdate(Date plannedstartdate) {
		this.plannedstartdate = plannedstartdate;
	}

	public String getSpecial_int() {
		return special_int;
	}

	public void setSpecial_int(String specialInt) {
		special_int = specialInt;
	}


	public String getExport_import() {
		return export_import;
	}

	public void setExport_import(String exportImport) {
		export_import = exportImport;
	}



	public String getStartDate() {
		return startDate;
		
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getItem()
	{
		return item;
		
	}
	
	public void setItem(String item)
	{
		this.item=item;
		
	}
	
	
	
	public String getWorkcenter() {
		return workcenter;
	}

	public void setWorkcenter(String workcenter) {
		this.workcenter = workcenter;
	}

	public String getSfc()
	{
		return sfc;
	}
	
	public void setSfc(String sfc)
	{
		this.sfc=sfc;
	}
	
	public int getSequence()
	{
		return sequence;
	}
	public void setSequence(int sequence)
	{
		this.sequence=sequence;
	}
	

	
	public Date getDate() 
	{
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
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

