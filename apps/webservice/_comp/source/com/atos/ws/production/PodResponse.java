package com.atos.ws.production;

import java.util.List;

import com.atos.ws.production.postscanactivities.AssemblyDataItem;
import com.atos.ws.production.postscanactivities.datacollection.DCGroupDataVO;

public class PodResponse {
	private String site;
	private String completedSfc;
	private String startedSFC;
	private String operationName;
	private String operationRef;
	private String resourceRef;
	private String userRef;
	private String resourceName;
	private String workInstructionUrl;
	private List<AssemblyDataItem> assemblyData;
	private List<DCGroupDataVO> dcGroupDataVOList;
	
	
	public String getOperationRef() {
		return operationRef;
	}
	public void setOperationRef(String operationRef) {
		this.operationRef = operationRef;
	}
	public String getResourceRef() {
		return resourceRef;
	}
	public void setResourceRef(String resourceRef) {
		this.resourceRef = resourceRef;
	}
	public String getUserRef() {
		return userRef;
	}
	public void setUserRef(String userRef) {
		this.userRef = userRef;
	}
	public String getOperationName() {
		return operationName;
	}
	public void setOperationName(String operationName) {
		this.operationName = operationName;
	}
	public String getResourceName() {
		return resourceName;
	}
	public void setResourceName(String resourceName) {
		this.resourceName = resourceName;
	}
	public String getSite() {
		return site;
	}
	public void setSite(String site) {
		this.site = site;
	}
	public String getCompletedSfc() {
		return completedSfc;
	}
	public void setCompletedSfc(String completedSfc) {
		this.completedSfc = completedSfc;
	}
	public String getStartedSFC() {
		return startedSFC;
	}
	public void setStartedSFC(String startedSFC) {
		this.startedSFC = startedSFC;
	}
	public String getWorkInstructionUrl() {
		return workInstructionUrl;
	}
	public void setWorkInstructionUrl(String workInstructionUrl) {
		this.workInstructionUrl = workInstructionUrl;
	}
	public List<AssemblyDataItem> getAssemblyData() {
		return assemblyData;
	}
	public void setAssemblyData(List<AssemblyDataItem> assemblyData) {
		this.assemblyData = assemblyData;
	}
	public List<DCGroupDataVO> getDcGroupDataVOList() {
		return dcGroupDataVOList;
	}
	public void setDcGroupDataVOList(List<DCGroupDataVO> dcGroupDataVOList) {
		this.dcGroupDataVOList = dcGroupDataVOList;
	}
	

}
