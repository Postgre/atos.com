package com.atos.orderboardvolvo;

import java.util.List;

import com.sap.me.production.podclient.BasePodPlugin;
/**
 * this is controller for subassebly screen
 * @author Administrator
 *
 */
public class  SubAssemblyOrderController extends BasePodPlugin {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<SubAsemblyCustomDataItem> subAsemblyCustomDataItemList;
	private String parentShopOrder;
	private String site;
	private String material;
	private String scheduleStartDate;
	private String scheduleEndDate;

	public String getSite() {
		return site;
	}

	public void setSite(String site) {
		this.site = site;
	}

	public String getMaterial() {
		return material;
	}

	public void setMaterial(String material) {
		this.material = material;
	}

	public String getScheduleStartDate() {
		return scheduleStartDate;
	}

	public void setScheduleStartDate(String scheduleStartDate) {
		this.scheduleStartDate = scheduleStartDate;
	}

	public String getScheduleEndDate() {
		return scheduleEndDate;
	}

	public void setScheduleEndDate(String scheduleEndDate) {
		this.scheduleEndDate = scheduleEndDate;
	}

	public String getParentShopOrder() {
		return parentShopOrder;
	}

	public void setParentShopOrder(String parentShopOrder) {
		this.parentShopOrder = parentShopOrder;
	}

	
	public List<SubAsemblyCustomDataItem> getSubAsemblyCustomDataItemList() {
		return subAsemblyCustomDataItemList;
	}

	public void setSubAsemblyCustomDataItemList(
			List<SubAsemblyCustomDataItem> subAsemblyCustomDataItemList) {
		this.subAsemblyCustomDataItemList = subAsemblyCustomDataItemList;
	}

	public SubAssemblyOrderController() {
		
	}

	
}
