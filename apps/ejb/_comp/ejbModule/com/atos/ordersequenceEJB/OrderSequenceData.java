package com.atos.ordersequenceEJB;
import java.sql.Date;
public class OrderSequenceData
{
	private String item;
	private String workcenter;
	private String sfc;
	private String order;
	private String parent_order;
	private int priority1;
	private int olderPriority;
	private Date plannedstartdate;
	private String special_int;
	private String export_import;
	private int active;
	private int status;
	private String createby;
	private Date createon;
	private String updateby;
	private Date updateon;
	private String handle;
	private boolean export_import2;
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
	public String getSfcBO() {
		return sfcBO;
	}
	public void setSfcBO(String sfcBO) {
		this.sfcBO = sfcBO;
	}
	public String getHandle() {
		return handle;
	}
	public int getOlderPriority() {
		return olderPriority;
	}
	public void setOlderPriority(int olderPriority) {
		this.olderPriority = olderPriority;
	}
	public void setHandle(String handle) {
		this.handle = handle;
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
	public String getUpdateby() {
		return updateby;
	}
	public void setUpdateby(String updateby) {
		this.updateby = updateby;
	}
	public Date getUpdateon() {
		return updateon;
	}
	public void setUpdateon(Date updateon) {
		this.updateon = updateon;
	}
	public Date getCreateon() {
		return createon;
	}
	public void setCreateon(Date createon) {
		this.createon = createon;
	}
	public String getCreateby() {
		return createby;
	}
	public void setCreateby(String createby) {
		this.createby = createby;
	}
	public boolean isExport_import2() {
		return export_import2;
	}
	public void setExport_import2(boolean exportImport2) {
		export_import2 = exportImport2;
	}
	public String getExport_import() {
		return export_import;
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
	public String isExport_import() {
		return export_import;
	}
	public void setExport_import(String exportImport) {
		export_import = exportImport;
	}
	public String getItem()
	{
		return item;	
	}
	public void setItem(String item)
	{
		this.item=item;	
	}
	public String getWorkcenter()
	{
		return workcenter;
	}
	public void setWorkcenter(String workcenter)
	{
		this.workcenter=workcenter;	
	}
	public String getSfc()
	{
		return sfc;
	}
	public void setSfc(String sfc)
	{
		this.sfc=sfc;
	}
	public String getOrder() {
		return order;
	}
	public void setOrder(String order) {
		this.order = order;
	}
	public String getParent_order() {
		return parent_order;
	}
	public void setParent_order(String parentOrder) {
		parent_order = parentOrder;
	}
}

	
