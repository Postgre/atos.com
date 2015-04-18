package com.atos.kittingEJB;

public class MaterialItem 
{

	private String material;
	private String version;
	private String workcenter;
	private String kitno;
	
	



	public String getKitno() {
		return kitno;
	}

	public void setKitno(String kitno) {
		this.kitno = kitno;
	}

	public String getWorkcenter() {
		return workcenter;
	}

	public void setWorkcenter(String workcenter) {
		this.workcenter = workcenter;
	}

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
	
	public boolean equals(Object obj)
	{
		if (obj == null || !(obj instanceof MaterialItem)) {
			return false;
		}
		return (this.material == ((MaterialItem) obj).material);
	}

	public int hashCode() {
		return this.material.hashCode();
	}


}
