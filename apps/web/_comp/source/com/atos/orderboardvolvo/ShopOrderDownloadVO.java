package com.atos.orderboardvolvo;
public class ShopOrderDownloadVO {
	private String shopOrder;
	private String material;
	private int quantity;
	private boolean selected;
	
	public boolean isSelected() {
		return selected;
	}
	public void setSelected(boolean selected) {
		this.selected = selected;
	}
	public String getShopOrder() {
		return shopOrder;
	}
	public void setShopOrder(String shopOrder) {
		this.shopOrder = shopOrder;
	}
	public String getMaterial() {
		return material;
	}
	public void setMaterial(String material) {
		this.material = material;
	}
	public int getQuantity() {
		return quantity;
	}
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

}
