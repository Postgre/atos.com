package com.atos.orderboardvolvo;

public class BOMCustomDataItem {

	
	private boolean error;
	private String errorMessage;
	private String material;
	private String shopOrder;
	private String itemRef;
	private int leadTime;
	private int serialStart;
	private int serialEnd;
	private int quantity;
	private boolean createOrder;
	private String shopOrderRef;
	private boolean shopOrderEnable;
	private boolean success;
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	public BOMCustomDataItem(){
		this.shopOrderEnable=true;
	}
	public boolean isShopOrderEnable() {
		return shopOrderEnable;
	}
	public void setShopOrderEnable(boolean shopOrderEnable) {
		this.shopOrderEnable = shopOrderEnable;
	}
	public String getShopOrderRef() {
		return shopOrderRef;
	}
	public void setShopOrderRef(String shopOrderRef) {
		this.shopOrderRef = shopOrderRef;
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
	public String getMaterial() {
		return material;
	}
	public void setMaterial(String material) {
		this.material = material;
	}
	public String getItemRef() {
		return itemRef;
	}
	public void setItemRef(String itemRef) {
		this.itemRef = itemRef;
	}
	public int getLeadTime() {
		return leadTime;
	}
	public void setLeadTime(int leadTime) {
		this.leadTime = leadTime;
	}
	public int getSerialStart() {
		return serialStart;
	}
	public void setSerialStart(int serialStart) {
		this.serialStart = serialStart;
	}
	public int getSerialEnd() {
		return serialEnd;
	}
	public void setSerialEnd(int serialEnd) {
		this.serialEnd = serialEnd;
	}
	public int getQuantity() {
		return quantity;
	}
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	public boolean isCreateOrder() {
		return createOrder;
	}
	public void setCreateOrder(boolean createOrder) {
		this.createOrder = createOrder;
	}
	public String getShopOrder() {
		return shopOrder;
	}
	public void setShopOrder(String shopOrder) {
		this.shopOrder = shopOrder;
	}
	
	
	
}

