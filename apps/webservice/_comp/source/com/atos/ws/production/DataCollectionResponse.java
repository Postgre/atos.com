package com.atos.ws.production;

import java.util.List;

import com.atos.ws.production.postscanactivities.datacollection.DCGroupDataVO;

public class DataCollectionResponse {
	
	private List<DCGroupDataVO> dcGroupDataVOList;
	
	
	public List<DCGroupDataVO> getDcGroupDataVOList() {
		return dcGroupDataVOList;
	}
	public void setDcGroupDataVOList(List<DCGroupDataVO> dcGroupDataVOList) {
		this.dcGroupDataVOList = dcGroupDataVOList;
	}
	

}
