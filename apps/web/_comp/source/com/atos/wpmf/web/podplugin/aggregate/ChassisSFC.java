package com.atos.wpmf.web.podplugin.aggregate;

import java.util.Date;

public class ChassisSFC 
{

	private String material;
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
	
	
	public String getItem() {
		return item;
	}

	public void setItem(String item) {
		this.item = item;
	}

	public String getSfc() {
		return sfc;
	}

	public void setSfc(String sfc) {
		this.sfc = sfc;
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

	public int getPriority1() {
		return priority1;
	}

	public void setPriority1(int priority1) {
		this.priority1 = priority1;
	}

	public int getOlderPriority() {
		return olderPriority;
	}

	public void setOlderPriority(int olderPriority) {
		this.olderPriority = olderPriority;
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

	public int getActive() {
		return active;
	}

	public void setActive(int active) {
		this.active = active;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getCreateby() {
		return createby;
	}

	public void setCreateby(String createby) {
		this.createby = createby;
	}

	public Date getCreateon() {
		return createon;
	}

	public void setCreateon(Date createon) {
		this.createon = createon;
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

	public String getHandle() {
		return handle;
	}

	public void setHandle(String handle) {
		this.handle = handle;
	}

	public String getWorkcenter() {
		return workcenter;
	}

	public void setWorkcenter(String workcenter) {
		this.workcenter = workcenter;
	}

	private Boolean selected = new Boolean(false);

	public void setMaterial(String material) {
		this.material = material;
	}

	public String getMaterial() {
		return material;
	}

	
	public void setSelected(Boolean selected) {
		this.selected = selected;
	}

	public Boolean getSelected() {
		return selected;
	}
	
	public boolean equals(Object obj)
	{
		if (obj == null || !(obj instanceof ChassisSFC)) {
			return false;
		}
		return (this.material == ((ChassisSFC) obj).material);
	}

	public int hashCode() {
		return this.material.hashCode();
	}


}
