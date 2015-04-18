package com.atos.assembly_screenEJB;

import java.util.List;

import javax.ejb.Local;

@Local
public interface AssemblyScreenInterface 

{
	
	public List<AssemblyData> readCustomData(String radioOption,String validateField); 
	
	public boolean saveMaterialData(AssemblyData assemblyData,String radioOption,String validateField);
	
	public boolean updateMaterialData(AssemblyData assemblyData,String radioOption,String validateField);
}
