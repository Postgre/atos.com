package com.atos.kittingEJB;
import java.sql.Connection;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;

import com.sap.me.frame.SystemBase;
import com.sap.me.frame.service.CommonMethods;

@Stateless
public class KittingAssignmentControllerBean implements KittingAssignmentScreenInterface

{

	private String user=CommonMethods.getUserId();
	private String site=CommonMethods.getSite();
	private final SystemBase dbBase = SystemBase.createSystemBase("jdbc/jts/wipPool");
	private String sfc=null;
	
	private Connection getConnection(){
		Connection con = null;

		con = dbBase.getDBConnection();

		return con;
	}

	@Override
	public void assignKit(String kitno) {
		Connection con = null;
		boolean isUpdated=false;
		String qry = "UPDATE Z_KITDEF SET KIT_STATUS_BO = ?,KIT_STATUS = ? WHERE KIT_NO = ?";
		PreparedStatement ps = null;
		try {
			con = getConnection();
			ps = con.prepareStatement(qry);
			ps.setString(1, "");
			ps.executeUpdate();
			
			} 
		catch (SQLException e){
		e.printStackTrace();
		} 
		finally {
		try {
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
	}

	@Override
	public List<KittingDefinitionData> readCustomData(String workcenter,java.util.Date date) {

		String qry=null;
		String tempQuery=null;
		Connection con = null;
		tempQuery = "SELECT SFC,PRIORITY,PLANNED_START_DATE,WORKCENTER FROM Z_ORDERSEQUENCE where ACTIVE = ? "+" ";
		qry = tempQuery + " and WORKCENTER like '" +workcenter+ "' and PLANNED_START_DATE = '" +utilDatetoSqlDate(date)+ "'  ";
		PreparedStatement ps = null;
		List<KittingDefinitionData> kittingDefinitionDataList = new ArrayList<KittingDefinitionData>();
		try  {
			con = getConnection();
			ps = con.prepareStatement(qry);
			ps.setInt(1,1);
			ResultSet rs = ps.executeQuery();
			if(rs!=null) {
			while (rs.next()) 
			{
				KittingDefinitionData kittingDefinitionData = new KittingDefinitionData();	
				kittingDefinitionData.setSfcfromPPC(""+rs.getString("SFC"));
				kittingDefinitionData.setPriorityFromPPC(rs.getInt("PRIORITY"));
				kittingDefinitionData.setPlannedStartDateFromPPCDate(rs.getDate("PLANNED_START_DATE"));
				kittingDefinitionData.setWorkcenter(""+rs.getString("WORKCENTER"));
				
				String sfcBO=findSFCBO_SFC(kittingDefinitionData.getSfcfromPPC());
				String kitDefBO=findKitDefBObySFC(sfcBO);
				String kitNumber=getAllKitNoFromTable(kitDefBO);
				kittingDefinitionData.setKitno(kitNumber);
				kittingDefinitionDataList.add(kittingDefinitionData);
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
	return kittingDefinitionDataList;
	}
	
	
	
	
	public String getAllKitNoFromTable(String kitDefBO)
	{
		String qry=null;
		String kitNoFromTable=null;
		Connection con = null;
		qry = "SELECT KIT_NO FROM Z_KITDEF WHERE ACTIVE = 1 AND HANDLE = ? ";
		PreparedStatement ps = null;
		try  {
			con = getConnection();
			ps = con.prepareStatement(qry);
			ps.setString(1, kitDefBO);
			ResultSet rs = ps.executeQuery();
			if(rs!=null) {
			while (rs.next()) 
			{
				kitNoFromTable=rs.getString("KIT_NO");
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
		return kitNoFromTable;
	}
	

	public List<KittingDefinitionData> getSfc() {

		String qry=null;
		String tempQuery=null;
		Connection con = null;
		qry = "SELECT SFC Z_ORDERSEQUENCE where ACTIVE = ? "+" ";
		PreparedStatement ps = null;
		List<KittingDefinitionData> kittingDefinitionDataList = new ArrayList<KittingDefinitionData>();
		try  {
			con = getConnection();
			ps = con.prepareStatement(qry);
			ps.setInt(1,1);
			ResultSet rs = ps.executeQuery();
			if(rs!=null) {
			while (rs.next()) {
				
				KittingDefinitionData kittingDefinitionData = new KittingDefinitionData();	
				kittingDefinitionData.setSfcfromPPC(""+rs.getString("SFC"));
				kittingDefinitionDataList.add(kittingDefinitionData);
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
	return kittingDefinitionDataList;
	}
	
	
	public Date utilDatetoSqlDate(java.util.Date sqlDate){
		if(sqlDate!=null)
		{
			java.sql.Date sqlDate1= new java.sql.Date(sqlDate.getTime());
			return sqlDate1;	
		}
		else{
			return null;
		}
	}

	@Override
	public void insertData(String sfcBO,String kitDefBO,int priority,Date date,String workcenter)
	{
		Connection con = null;
		//KittingDefinitionData kittingDefinition=new KittingDefinitionData();
		String qry = "INSERT INTO Z_KIT_ASSIGNMENT(HANDLE,SFC_BO,KITDEF_BO,ACTIVE,CREATED_BY,CREATED_DATE,PRIORITY,WORKCENTER) VALUES (?,?,?,?,?,?,?,?)";
		PreparedStatement ps = null;
		try 
		{
			con = getConnection();
			ps = con.prepareStatement(qry);
			ps.setString(1,"KitAssignmentBO:"+site+","+sfcBO);
			ps.setString(2,sfcBO);
			ps.setString(3, kitDefBO);
			ps.setInt(4, 1);
			ps.setString(5,user);
			ps.setDate(6,date);
			ps.setInt(7,priority);
			ps.setString(8,workcenter);
			ps.executeUpdate();
			}
		catch (SQLException e){
		e.printStackTrace();
		} 
		finally {
		try {
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
		
	}
	@Override
	public boolean updateKitDef(String kitno) 
	{
		boolean isUpdated=false;
		Connection con = null;
		String qry ="UPDATE Z_KITDEF SET KIT_STATUS_BO = ?,KIT_STATUS = ? ,UPDATED_BY = ?,UPDATED_DATE = ? WHERE KIT_NO = ? AND ACTIVE = 1";
		PreparedStatement preparedStatement = null;
		try 
			{
			con = getConnection();
			String kitStatusTemp="ASSIGNED";
			preparedStatement = con.prepareStatement(qry);
			preparedStatement.setString(1,"KitStatusBO:"+site+","+kitStatusTemp);
			preparedStatement.setString(2,kitStatusTemp);
			preparedStatement.setString(3,user);
			preparedStatement.setDate(4,utilDatetoSqlDate(new java.util.Date()));
			preparedStatement.setString(5,kitno);
			preparedStatement.executeUpdate();
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
			if (preparedStatement != null) {

				preparedStatement.close();
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
	@Override
	public void updateKitAssignmentExceptSfcBO(String kitDefBO,String sfcBO)
	{
		Connection con = null;
		//KittingDefinitionData kittingDefinition=new KittingDefinitionData();
		String qry = "UPDATE Z_KIT_ASSIGNMENT SET KITDEF_BO = ? WHERE SFC_BO = ?";
		PreparedStatement ps = null;
		try 
		{
			con = getConnection();
			ps = con.prepareStatement(qry);
			ps.setString(1, kitDefBO);
			ps.setString(2,sfcBO);
			ps.executeUpdate();
			}
		catch (SQLException e){
		e.printStackTrace();
		} 
		finally {
		try {
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
		
	}
	@Override
	public String findKitDefBO(String kitno)
	{
		String qry=null;
		String KitDefBO=null;
		Connection con = null;
		qry="SELECT HANDLE FROM Z_KITDEF WHERE KIT_NO = ? ";
		PreparedStatement ps = null;
		try  {
			con = getConnection();
			ps = con.prepareStatement(qry);
			ps.setString(1,kitno);
			ResultSet rs = ps.executeQuery();
			if(rs!=null) {
			while (rs.next()) {
				KitDefBO=rs.getString("HANDLE");
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
	
		return KitDefBO;
	}
	@Override
	public String findKitDefBO_KitAssignment(String kitDefBO)
	{
		String qry=null;
		String kitDefBOfromTable=null;
		Connection con = null;
		qry = "SELECT KITDEF_BO FROM Z_KIT_ASSIGNMENT WHERE ACTIVE = 1 AND KITDEF_BO like ?";
		PreparedStatement ps = null;
		try  {
			con = getConnection();
			ps = con.prepareStatement(qry);
			ps.setString(1,kitDefBO);
			ResultSet rs = ps.executeQuery();
			if(rs!=null) {
			while (rs.next()) 
			{
				kitDefBOfromTable=rs.getString("KITDEF_BO");
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
		return kitDefBOfromTable;
	}
	@Override
	public String findKitDefBObySFC(String sfcBO)
	{
		String qry=null;
		String kitDefBOfromTable=null;
		Connection con = null;
		qry = "SELECT KITDEF_BO FROM Z_KIT_ASSIGNMENT WHERE ACTIVE = 1 AND SFC_BO = ?";
		PreparedStatement ps = null;
		try  {
			con = getConnection();
			ps = con.prepareStatement(qry);
			ps.setString(1,sfcBO);
			ResultSet rs = ps.executeQuery();
			if(rs!=null) {
			while (rs.next()) 
			{
				kitDefBOfromTable=rs.getString("KITDEF_BO");
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
		return kitDefBOfromTable;
	}
	@Override
	public String findKitNo(String kitno)
	{
		String qry=null;
		String kitNoFromTable=null;
		Connection con = null;
		qry = "SELECT KIT_NO FROM Z_KITDEF WHERE ACTIVE = 1 AND KIT_NO like '"+kitno+"' AND KIT_STATUS = ? ";
		PreparedStatement ps = null;
		try  {
			con = getConnection();
			ps = con.prepareStatement(qry);
			ps.setString(1, "AVAILABLE");
			ResultSet rs = ps.executeQuery();
			if(rs!=null) {
			while (rs.next()) 
			{
				kitNoFromTable=rs.getString("KIT_NO");
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
		return kitNoFromTable;
	}
	@Override
	public String findSFCBO_Assignment(String sfcBO)
	{
		String qry=null;
		String sfcBOfromTable=null;
		Connection con = null;
		qry = "SELECT SFC_BO FROM Z_KIT_ASSIGNMENT WHERE ACTIVE = 1 AND SFC_BO = ?";
		PreparedStatement ps = null;
		try  {
			con = getConnection();
			ps = con.prepareStatement(qry);
			ps.setString(1,sfcBO);
			ResultSet rs = ps.executeQuery();
			if(rs!=null) {
			while (rs.next()) 
			{
				sfcBOfromTable=rs.getString("SFC_BO");
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
		return sfcBOfromTable;
	}
	@Override
	public String findSFCBO_SFC(String sfc)
	{
		String qry=null;
		String sfcBOfromTable=null;
		Connection con = null;
		qry = "SELECT HANDLE FROM SFC WHERE SFC = ?";
		PreparedStatement ps = null;
		try  {
			con = getConnection();
			ps = con.prepareStatement(qry);
			ps.setString(1,sfc);
			ResultSet rs = ps.executeQuery();
			if(rs!=null) {
			while (rs.next()) 
			{
				sfcBOfromTable=rs.getString("HANDLE");
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
		return sfcBOfromTable;
	}
	
	


}
