package com.atos.assembly_screenEJB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;

import com.sap.me.demand.SFCBOHandle;
import com.sap.me.frame.SystemBase;
import com.sap.me.frame.service.CommonMethods;

@Stateless
public class AseemblyDataScreenBean implements AssemblyScreenInterface

{

	private String user=CommonMethods.getUserId();
	private String site=CommonMethods.getSite();
	private static final String customTableName = "Z_ASSEMBLY_DATA_VALIDATION";
	private final SystemBase dbBase = SystemBase.createSystemBase("jdbc/jts/wipPool");
	private String sfc=null;
	
	private Connection getConnection(){
		Connection con = null;

		con = dbBase.getDBConnection();

		return con;
	}

	@Override
	public List<AssemblyData> readCustomData(String radioOption,String validateField) 
	
	{
		String qry=null;
		SFCBOHandle sfcBOHandle = new SFCBOHandle(site, validateField);
		String sfcBO = sfcBOHandle.getValue();
		String workcenterBO = "WorkCenterBO:"+site+","+validateField;
		Connection con = null;
		qry = "SELECT HANDLE,SFC_BO,WORKCENTER_BO,HOST_OPERATION,SLAVE_OPERATION,VALIDATE_ELEMENT,VALIDATE_STATUS,CHECK_FIELD,ACTIVE,CREATED_BY,CREATED_DATE FROM " +customTableName+ " WHERE ACTIVE = 1 "+" ";
		
		if("SFC".equals(radioOption))
		{
			qry = qry + " AND SFC_BO like '" +sfcBO+ "' ";
		}
		
		if("WORKCENTER".equals(radioOption))
		{
			qry = qry + " AND WORKCENTER_BO like '" +workcenterBO+ "' ";
		}
		
		PreparedStatement ps = null;
		List<AssemblyData> assemblyDataList = new ArrayList<AssemblyData>();
		try  {
			con = getConnection();
			ps = con.prepareStatement(qry);
			ResultSet rs = ps.executeQuery();
			if(rs!=null) {
			while (rs.next())
			
			{
				AssemblyData assemblyData=new AssemblyData();
				assemblyData.setHandle(""+rs.getString("HANDLE"));
				assemblyData.setSfc_bo(""+rs.getString("SFC_BO"));
				assemblyData.setWorkcenter_bo(""+rs.getString("WORKCENTER_BO"));
				assemblyData.setHost_operation(""+rs.getString("HOST_OPERATION"));
				assemblyData.setSlave_operation(""+rs.getString("SLAVE_OPERATION"));
				assemblyData.setValidate_element(""+rs.getString("VALIDATE_ELEMENT"));
				assemblyDataList.add(assemblyData);
			}
			}
			}
			catch (SQLException e) 	{
			e.printStackTrace();
		} 
			catch(NullPointerException w)	{
				w.printStackTrace();
			}
		finally	{
			try {
					if (ps != null)  {
						ps.close();
						}
					if (con != null) {
						con.close();
						}
					} 
			catch (Exception e2) {
				e2.printStackTrace();
			}
		}	
	return assemblyDataList;
	}

	@Override
	public boolean saveMaterialData(AssemblyData assemblyData,String radioOption,String validateField)
	
	{
		Connection con = null;
		boolean isUpdated=false;
		
		String qry ="INSERT INTO " +customTableName+ " (HANDLE,SFC_BO,WORKCENTER_BO,HOST_OPERATION,SLAVE_OPERATION,VALIDATE_ELEMENT,VALIDATE_STATUS,CHECK_FIELD,ACTIVE,CREATED_BY,CREATED_DATE,HOST_OPERATION_BO,SLAVE_OPERATION_BO)VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?)";
		PreparedStatement ps = null;
		try 
		{
			con = getConnection();
			//AssemblyData assemblyDataItem=new AssemblyData();
			ps = con.prepareStatement(qry);
			if("SFC".equals(radioOption))	
			{
				SFCBOHandle sfcBOHandle = new SFCBOHandle(site, validateField);
				String sfcReference = sfcBOHandle.getValue();
				ps.setString(1,"AssemblyDataBO:"+site+","+sfcReference);
			}	 else{
				ps.setString(1,"AssemblyDataBO:"+site+",WorkCenterBO:"+site+","+validateField);
			}
			if("SFC".equals(radioOption))
			{
				SFCBOHandle sfcBOHandle = new SFCBOHandle(site, validateField);
				String sfcReference = sfcBOHandle.getValue();
				ps.setString(2,sfcReference);
			} else {
				ps.setString(2, "                   ----  ");
			}
			if("WORKCENTER".equals(radioOption)){
				ps.setString(3,"WorkCenterBO:"+site+","+validateField);
			}else {
				ps.setString(3, "                   ----  ");
			}
			ps.setString(4,assemblyData.getHost_operation());
			ps.setString(5,assemblyData.getSlave_operation());
			ps.setString(6,assemblyData.getValidate_element());
			ps.setString(7,"YES");
			ps.setString(8, assemblyData.getValidate_element()+"_INFO");
			ps.setInt(9, 1);
			ps.setString(10,user);
			ps.setDate(11,utilDatetoSqlDate(new java.util.Date()));
			ps.setString(12,"HostOperationBO:"+site+","+assemblyData.getHost_operation());
			ps.setString(13,"SlaveOperationBO:"+site+","+assemblyData.getSlave_operation());
			ps.executeUpdate();
			isUpdated=true;
		} 
		catch (SQLException e)
		{
		e.printStackTrace();
		} 
		finally 
		{
		try 
		{
			if (ps != null) {

				ps.close();
			}
			if (con != null) {
				con.close();
			}
		} catch (Exception e2) {
			e2.printStackTrace();
		}
		}
		return isUpdated;

		
	}
	public java.sql.Date utilDatetoSqlDate(java.util.Date sqlDate) {
		if(sqlDate!=null){
			java.sql.Date sqlDate1= new java.sql.Date(sqlDate.getTime());
			return sqlDate1;	
		}
		return null;
		}

	@Override
	public boolean updateMaterialData(AssemblyData assemblyData,String radioOption, String validateField) {
		Connection con = null;
		boolean isUpdated=false;
		
		String qry ="INSERT INTO " +customTableName+ " (HANDLE,SFC_BO,WORKCENTER_BO,HOST_OPERATION,SLAVE_OPERATION,VALIDATE_ELEMENT,VALIDATE_STATUS,CHECK_FIELD,ACTIVE,CREATED_BY,CREATED_DATE)VALUES(?,?,?,?,?,?,?,?,?,?,?)";
		PreparedStatement ps = null;
		try 
		{
			con = getConnection();
			//AssemblyData assemblyDataItem=new AssemblyData();
			ps = con.prepareStatement(qry);
			if("SFC".equals(radioOption))	
			{
				SFCBOHandle sfcBOHandle = new SFCBOHandle(site, validateField);
				String sfcReference = sfcBOHandle.getValue();
				ps.setString(1,"AssemblyDataBO:"+site+","+sfcReference);
			}	 else{
				ps.setString(1,"AssemblyDataBO:"+site+",WorkCenterBO:"+site+","+validateField);
			}
			if("SFC".equals(radioOption)){
				SFCBOHandle sfcBOHandle = new SFCBOHandle(site, validateField);
				String sfcReference = sfcBOHandle.getValue();
				ps.setString(2,sfcReference);
			} else {
				ps.setString(2, "                   ----  ");
			}
			if("WORKCENTER".equals(radioOption)){
				ps.setString(3,"WorkCenterBO:"+site+","+validateField);
			}else {
				ps.setString(3, "                   ----  ");
			}
			ps.setString(4,assemblyData.getHost_operation());
			ps.setString(5,assemblyData.getSlave_operation());
			ps.setString(6,assemblyData.getValidate_element());
			ps.setString(7,"YES");
			ps.setString(8, assemblyData.getValidate_element()+"_INFO");
			ps.setInt(9, 1);
			ps.setString(10,user);
			ps.setDate(11,utilDatetoSqlDate(new java.util.Date()));
			ps.executeUpdate();
			isUpdated=true;
		} 
		catch (SQLException e)
		{
		e.printStackTrace();
		} 
		finally 
		{
		try 
		{
			if (ps != null) {

				ps.close();
			}
			if (con != null) {
				con.close();
			}
		} catch (Exception e2) {
			e2.printStackTrace();
		}
		}
		return isUpdated;
	}
		
	
	
	}
