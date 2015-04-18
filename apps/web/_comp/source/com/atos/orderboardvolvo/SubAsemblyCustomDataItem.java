package com.atos.orderboardvolvo;
/**
 * this class represent row of sub assembly row
 */


public class SubAsemblyCustomDataItem {	
	
	private String orderno;	
	private String qtyAvailable;
	private String qtytobebuild;	
	private String scheduledstartdate;
	private String scheduleenddate;
	private String shopOrderRef;
	private int priority; 
	private String material;
	private String chasisStart;
	private String chasisEnd;
	private String subassStatus;
	private String buttonStyle;
	
	
	public String getButtonStyle() {
		return buttonStyle;
	}
	public void setButtonStyle(String buttonStyle) {
		this.buttonStyle = buttonStyle;
	}
	public String getSubassStatus() {
		return subassStatus;
	}
	public void setSubassStatus(String subassStatus) {
		this.subassStatus = subassStatus;
	}
	public String getChasisStart() {
		return chasisStart;
	}
	public void setChasisStart(String chasisStart) {
		this.chasisStart = chasisStart;
	}
	public String getChasisEnd() {
		return chasisEnd;
	}
	public void setChasisEnd(String chasisEnd) {
		this.chasisEnd = chasisEnd;
	}	
	public String getMaterial() {
		return material;
	}
	public void setMaterial(String material) {
		this.material = material;
	}
	public String getShopOrderRef() {
		return shopOrderRef;
	}
	public void setShopOrderRef(String shopOrderRef) {
		this.shopOrderRef = shopOrderRef;
	}
	public int getPriority() {
		return priority;
	}
	public void setPriority(int priority) {
		this.priority = priority;
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
		
}

