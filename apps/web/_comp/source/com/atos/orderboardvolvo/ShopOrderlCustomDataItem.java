package com.atos.orderboardvolvo;

import java.util.List;

public class ShopOrderlCustomDataItem {
	
	
	private boolean select;
	private String orderno;	
	private String qtyAvailable;
	private String qtytobebuild;	
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
	private String material;
	private String chasisStart;
	private String chasisEnd;
	private String subassStatus;
	private String buttonStyle;
	private List<SubAsemblyCustomDataItem> subAsemblyCustomDataItems;
	
	
	
	
	
	
	public List<SubAsemblyCustomDataItem> getSubAsemblyCustomDataItems() {
		return subAsemblyCustomDataItems;
	}
	public void setSubAsemblyCustomDataItems(
			List<SubAsemblyCustomDataItem> subAsemblyCustomDataItems) {
		this.subAsemblyCustomDataItems = subAsemblyCustomDataItems;
	}
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
	public void setSelect(boolean select) {
		this.select = select;
	}
	public String getMaterial() {
		return material;
	}
	public void setMaterial(String material) {
		this.material = material;
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

