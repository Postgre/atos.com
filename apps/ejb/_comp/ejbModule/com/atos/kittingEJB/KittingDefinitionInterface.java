package com.atos.kittingEJB;

import java.util.List;
import javax.ejb.Local;
import javax.faces.model.SelectItem;

import com.sap.me.frame.domain.BusinessException;
@Local
public interface KittingDefinitionInterface

{
	public List<KittingDefinitionData> readCustomData(String kitno);
	
	public boolean insertData(String kitno,String kittype,String status);
	
	public boolean updateData(String kitno,String kittype,String status);
	
	public boolean insertIntoKIT_TYPE(String kittype);
	
	public String getAllKitNoFromTable(String kitno);
	
	public List<SelectItem> kitTypeBrowse();
	
	public List<SelectItem> kitStatusBrowse();
	
	public List<MaterialItem> kitNumberBrowse(String kitno)
	throws BusinessException;

}
