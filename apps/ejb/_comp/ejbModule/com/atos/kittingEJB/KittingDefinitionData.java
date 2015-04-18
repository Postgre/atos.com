package com.atos.kittingEJB;

import java.sql.Date;

import com.sap.me.frame.service.CommonMethods;

public class KittingDefinitionData 
{
	private String kitno;
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
	private String sfcBofromPPC;
	private String plannedStartDateFromPPC;
	private Date plannedStartDateFromPPCDate;
	private String workcenter;
	
	
	public String getWorkcenter() {
		return workcenter;
	}
	public void setWorkcenter(String workcenter) {
		this.workcenter = workcenter;
	}
	public String getSfcBofromPPC() {
		return sfcBofromPPC;
	}
	public void setSfcBofromPPC(String sfcBofromPPC) {
		this.sfcBofromPPC = sfcBofromPPC;
	}
	public Date getPlannedStartDateFromPPCDate() {
		return plannedStartDateFromPPCDate;
	}
	public void setPlannedStartDateFromPPCDate(Date plannedStartDateFromPPCDate) {
		this.plannedStartDateFromPPCDate = plannedStartDateFromPPCDate;
	}

	public String getKitno() {
		return kitno;
	}
	public void setKitno(String kitno) {
		this.kitno = kitno;
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
