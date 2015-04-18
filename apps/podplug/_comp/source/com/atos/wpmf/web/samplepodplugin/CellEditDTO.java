package com.atos.wpmf.web.samplepodplugin;

import java.math.BigDecimal;

public class CellEditDTO {

	private String sfc;
	private String shopOrder;
	private String material;
	private BigDecimal qty;
	private boolean active;
	private String sfcData;
	private boolean createMessage;
	private boolean logComment;
	private String status;

	public CellEditDTO() {
		super();
	}

	public String getStatus() {
		return status;
	}


	public void setStatus(String status) {
		this.status = status;
	}
	

	public String getSfc() {
		return sfc;
	}


	public void setSfc(String sfc) {
		this.sfc = sfc;
	}
	
	public String getSfcData() {
		return sfcData;
	}


	public void setSfcData(String sfcData) {
		this.sfcData = sfcData;
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

	public BigDecimal getQty() {
		return qty;
	}

	public void setQty(BigDecimal qty) {
		this.qty = qty;
	}

	public boolean isActive() {
		return active;
	}


	public void setActive(boolean active) {
		this.active = active;
	}


	public boolean isCreateMessage() {
		return createMessage;
	}


	public void setCreateMessage(boolean createMessage) {
		this.createMessage = createMessage;
	}


	public boolean isLogComment() {
		return logComment;
	}


	public void setLogComment(boolean logComment) {
		this.logComment = logComment;
	}

}

