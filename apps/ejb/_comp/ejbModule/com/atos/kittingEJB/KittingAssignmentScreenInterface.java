package com.atos.kittingEJB;

import java.sql.Date;
import java.util.List;

import javax.ejb.Local;

@Local
public interface KittingAssignmentScreenInterface 

{
public List<KittingDefinitionData> readCustomData(String workcenter,java.util.Date date);
	
	public void assignKit(String kitno);
	
	public void insertData(String sfcBO,String kitDefBO,int priority,Date date,String workcenter);

	public boolean updateKitDef(String kitno);
	
	public void updateKitAssignmentExceptSfcBO(String sfc,String kitDefBO);
	
	public String findKitDefBO(String kitno);
	
	public String findKitDefBO_KitAssignment(String kitDefBO);

	public String findKitDefBObySFC(String sfcBO);

	public String findKitNo(String kitno);

	public String findSFCBO_Assignment(String sfcBO);

	public String findSFCBO_SFC(String sfc);

}
