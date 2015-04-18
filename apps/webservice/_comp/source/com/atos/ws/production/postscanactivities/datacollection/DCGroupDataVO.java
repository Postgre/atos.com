package com.atos.ws.production.postscanactivities.datacollection;

import java.util.List;

public class DCGroupDataVO {
 private String dgGroupRef;
 private String dcGroupName;
 List<DCParemeterDataVO> dcParemeterDataVO;
public String getDgGroupRef() {
	return dgGroupRef;
}
public void setDgGroupRef(String dgGroupRef) {
	this.dgGroupRef = dgGroupRef;
}
public String getDcGroupName() {
	return dcGroupName;
}
public void setDcGroupName(String dcGroupName) {
	this.dcGroupName = dcGroupName;
}
public List<DCParemeterDataVO> getDcParemeterDataVO() {
	return dcParemeterDataVO;
}
public void setDcParemeterDataVO(List<DCParemeterDataVO> dcParemeterDataVO) {
	this.dcParemeterDataVO = dcParemeterDataVO;
}
}
