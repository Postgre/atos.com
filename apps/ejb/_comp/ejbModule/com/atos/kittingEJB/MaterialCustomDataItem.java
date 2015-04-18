package com.atos.kittingEJB;

import java.util.Date;

import com.sap.me.frame.service.CommonMethods;
import com.sap.me.plant.WorkCenterBOHandle;
import com.sap.me.plant.domain.WorkCenterDO;

public class MaterialCustomDataItem 
{
	private String kitnoString;
	private String kitno;
	private String kitnoAltBeforeUpdate;
	private String kittype;
	private String status;
	private boolean disableKitno;
	private boolean disableKittype;
	private boolean disableStatus;
	private String message;
	private String site=CommonMethods.getSite();
	private String user=CommonMethods.getUserId();
	private String sfcfromPPC;
	private int priorityFromPPC;
	private String plannedStartDateFromPPC;
	private String sfcBOfromPPC;
	private Date datefromPPC;
	private boolean select;
	private Boolean selected = new Boolean(false);
	private String workcenter;
	
	
	
	public String getWorkcenter() {
		return workcenter;
	}
	public void setWorkcenter(String workcenter) {
		this.workcenter = workcenter;
	}
	public Date getDatefromPPC() {
		return datefromPPC;
	}
	public void setDatefromPPC(Date datefromPPC) {
		this.datefromPPC = datefromPPC;
	}
	public Boolean getSelected() {
		return selected;
	}
	public void setSelected(Boolean selected) {
		this.selected = selected;
	}
	public String getSfcBOfromPPC() {
		return sfcBOfromPPC;
	}
	public void setSfcBOfromPPC(String sfcBOfromPPC) {
		this.sfcBOfromPPC = sfcBOfromPPC;
	}
	public String getKitnoAltBeforeUpdate() {
		return kitnoAltBeforeUpdate;
	}
	public void setKitnoAltBeforeUpdate(String kitnoAltBeforeUpdate) {
		this.kitnoAltBeforeUpdate = kitnoAltBeforeUpdate;
	}
	public boolean isSelect() {
		return select;
	}
	public void setSelect(boolean select) {
		this.select = select;
	}
	
	public String getKitno() {
		return kitno;
	}
	public void setKitno(String kitno) {
		this.kitno = kitno;
	}
	public String getKitnoString() {
		return kitnoString;
	}
	public void setKitnoString(String kitnoString) {
		this.kitnoString = kitnoString;
	}

	public String getKittype() {
		return kittype;
	}
	public void setKittype(String kittype) {
		this.kittype = kittype;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public boolean isDisableKitno() {
		return disableKitno;
	}
	public void setDisableKitno(boolean disableKitno) {
		this.disableKitno = disableKitno;
	}
	public boolean isDisableKittype() {
		return disableKittype;
	}
	public void setDisableKittype(boolean disableKittype) {
		this.disableKittype = disableKittype;
	}
	public boolean isDisableStatus() {
		return disableStatus;
	}
	public void setDisableStatus(boolean disableStatus) {
		this.disableStatus = disableStatus;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getSite() {
		return site;
	}
	public void setSite(String site) {
		this.site = site;
	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public String getSfcfromPPC() {
		return sfcfromPPC;
	}
	public void setSfcfromPPC(String sfcfromPPC) {
		this.sfcfromPPC = sfcfromPPC;
	}
	public int getPriorityFromPPC() {
		return priorityFromPPC;
	}
	public void setPriorityFromPPC(int priorityFromPPC) {
		this.priorityFromPPC = priorityFromPPC;
	}
	public String getPlannedStartDateFromPPC() {
		return plannedStartDateFromPPC;
	}
	public void setPlannedStartDateFromPPC(String plannedStartDateFromPPC) {
		this.plannedStartDateFromPPC = plannedStartDateFromPPC;
	}

	
}

