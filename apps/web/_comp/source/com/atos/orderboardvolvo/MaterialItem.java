package com.atos.orderboardvolvo;

public class MaterialItem {

	private String material;
	private String version;
	private Boolean selected = new Boolean(false);

	public void setMaterial(String material) {
		this.material = material;
	}

	public String getMaterial() {
		return material;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getVersion() {
		return version;
	}

	public void setSelected(Boolean selected) {
		this.selected = selected;
	}

	public Boolean getSelected() {
		return selected;
	}
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof MaterialItem)) {
			return false;
		}
		return (this.material == ((MaterialItem) obj).material);
	}

	public int hashCode() {
		return this.material.hashCode();
	}


}
