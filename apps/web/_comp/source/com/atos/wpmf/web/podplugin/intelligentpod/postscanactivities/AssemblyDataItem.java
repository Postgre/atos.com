package com.atos.wpmf.web.podplugin.intelligentpod.postscanactivities;

public class AssemblyDataItem {
	private String assemblyName;
	private String assemblyStatus;
	private int assemblyQuantity;
	private int bomQuantity;
	private String assemblyType;
	private boolean assembled;
	
	public int getBomQuantity() {
		return bomQuantity;
	}
	public void setBomQuantity(int bomQuantity) {
		this.bomQuantity = bomQuantity;
	}
	public boolean isAssembled() {
		return assembled;
	}
	public void setAssembled(boolean assembled) {
		this.assembled = assembled;
	}
	public String getAssemblyType() {
		return assemblyType;
	}
	public void setAssemblyType(String assemblyType) {
		this.assemblyType = assemblyType;
	}
	public int getAssemblyQuantity() {
		return assemblyQuantity;
	}
	public void setAssemblyQuantity(int assemblyQuantity) {
		this.assemblyQuantity = assemblyQuantity;
	}
	public String getAssemblyName() {
		return assemblyName;
	}
	public void setAssemblyName(String assemblyName) {
		this.assemblyName = assemblyName;
	}
	public String getAssemblyStatus() {
		return assemblyStatus;
	}
	public void setAssemblyStatus(String assemblyStatus) {
		this.assemblyStatus = assemblyStatus;
	}
	
}
