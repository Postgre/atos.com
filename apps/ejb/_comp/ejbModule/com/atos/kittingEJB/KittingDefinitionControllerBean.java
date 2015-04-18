package com.atos.kittingEJB;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateless;
import javax.faces.model.SelectItem;

import com.sap.me.frame.SystemBase;
import com.sap.me.frame.domain.BusinessException;
import com.sap.me.frame.service.CommonMethods;

@Stateless
public class KittingDefinitionControllerBean implements KittingDefinitionInterface
{
	private String user=CommonMethods.getUserId();
	private String site=CommonMethods.getSite();
	private final SystemBase dbBase = SystemBase.createSystemBase("jdbc/jts/wipPool");
	
	private Connection getConnection(){
		Connection con = null;

		con = dbBase.getDBConnection();

		return con;
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
	public List<KittingDefinitionData> readCustomData(String kitno)
	{
		String qry=null;
		String tempQuery=null;
		Connection con = null;
		tempQuery = "SELECT KIT_NO,KIT_TYPE,KIT_STATUS FROM Z_KITDEF WHERE ACTIVE = 1  "+" ";
		if ((!"".equals(kitno)&&kitno!=null))		
		{
			qry = tempQuery + " and KIT_NO like '" +kitno+ "' ";
		}		
		PreparedStatement ps = null;
		List<KittingDefinitionData> kittingDefinitionDataList=new ArrayList<KittingDefinitionData>();
		try  {
			con = getConnection();
			ps = con.prepareStatement(qry);
			ResultSet rs = ps.executeQuery();
			if(rs!=null) {
			while (rs.next()) 
			{
				KittingDefinitionData kittingDefinitionData=new KittingDefinitionData();
				kittingDefinitionData.setKitno(""+rs.getString("KIT_NO"));
				kittingDefinitionData.setKittype(""+rs.getString("KIT_TYPE"));		
				kittingDefinitionData.setStatus(""+rs.getString("KIT_STATUS"));
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

	@Override
	public boolean insertData(String kitno,String kittype,String status) 
	{
		boolean isInserted=false;
		String qry = "INSERT INTO Z_KITDEF(HANDLE,KIT_NO,KIT_TYPE_BO,KIT_STATUS_BO,KIT_TYPE,KIT_STATUS,ACTIVE,CREATED_BY,CREATED_DATE) VALUES (?,?,?,?,?,?,?,?,?)";
		Connection con = null;
		PreparedStatement preparedStatement = null;
		try 
		{
		
			con = getConnection();
			preparedStatement = con.prepareStatement(qry);
			preparedStatement.setString(1,"KitDefBO:"+site+","+kitno+",KitTypeBO:"+site+","+kittype);
			preparedStatement.setString(2,kitno);
			preparedStatement.setString(3,"KitTypeBO:"+site+","+kittype);
			preparedStatement.setString(4,"KitStatusBO:"+site+","+status);
			preparedStatement.setString(5,kittype);
			preparedStatement.setString(6,status);
			preparedStatement.setInt(7,1);
			preparedStatement.setString(8,user);
			preparedStatement.setDate(9,utilDatetoSqlDate(new java.util.Date()));
			preparedStatement.executeUpdate();
			isInserted=true;

		} 
		catch (SQLException e) {
			e.printStackTrace();

		}
		finally {
			try {
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
		return isInserted;
		
	}
	
	@Override
	public boolean updateData(String kitno,String kittype,String status)
	{
		boolean isUpdated=false;
		Connection con = null;
		String qry ="UPDATE Z_KITDEF SET HANDLE = ?,KIT_TYPE_BO = ?,KIT_STATUS_BO = ?,KIT_TYPE = ?,KIT_STATUS = ? ,UPDATED_BY = ?,UPDATED_DATE = ? WHERE KIT_NO = ? AND ACTIVE = 1";
		PreparedStatement preparedStatement = null;
		try 
			{
			con = getConnection();
			preparedStatement = con.prepareStatement(qry);
			preparedStatement.setString(1,"KitDefBO:"+site+","+kitno+",KitTypeBO:"+site+","+kittype);
			preparedStatement.setString(2,"KitTypeBO:"+site+","+kittype);
			preparedStatement.setString(3,"KitStatusBO:"+site+","+status);
			preparedStatement.setString(4,kittype);
			preparedStatement.setString(5,status);
			preparedStatement.setString(6,user);
			preparedStatement.setDate(7,utilDatetoSqlDate(new java.util.Date()));
			preparedStatement.setString(8,kitno);
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
	public boolean insertIntoKIT_TYPE(String kittype)
	{
		boolean isInserted=false;
		String qry = "INSERT INTO Z_KITTYPE(HANDLE,KIT_TYPE,ACTIVE,CREATED_BY,CREATED_DATE) VALUES (?,?,?,?,?)";
		Connection con = null;
		PreparedStatement preparedStatement = null;
		try 
		{
		
			con = getConnection();
			preparedStatement = con.prepareStatement(qry);
			preparedStatement.setString(1,"KitTypeBO:"+site+":"+kittype);
			preparedStatement.setString(2,kittype);
			preparedStatement.setInt(3,1);
			preparedStatement.setString(4,user);
			preparedStatement.setDate(5,utilDatetoSqlDate(new java.util.Date()));
			preparedStatement.executeUpdate();
			isInserted=true;

		} 
		catch (SQLException e) {
			e.printStackTrace();

		}
		finally {
			try {
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
		return isInserted;
	}
	@Override
	public String getAllKitNoFromTable(String kitno) {
		String qry = null;
		String kitNoFromTable = null;
		Connection con = null;
		qry = "SELECT KIT_NO FROM Z_KITDEF WHERE ACTIVE = 1 AND KIT_NO like '"
				+ kitno + "' ";
		PreparedStatement ps = null;
		try {
			con = getConnection();
			ps = con.prepareStatement(qry);
			ResultSet rs = ps.executeQuery();
			if (rs != null) {
				while (rs.next()) {
					kitNoFromTable = rs.getString("KIT_NO");
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (NullPointerException w) {
			w.printStackTrace();
		} finally {
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
		return kitNoFromTable;
	}
	
	@Override
	public List<SelectItem> kitTypeBrowse() {
		Connection con = null;
		String query = "SELECT DISTINCT KIT_TYPE from Z_KITTYPE where KIT_TYPE IS NOT NULL ";
		PreparedStatement ps = null;
		List<SelectItem> user = new ArrayList<SelectItem>();
		try {
			con = getConnection();
			ps = con.prepareStatement(query);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {

				String value = rs.getString("KIT_TYPE");
				user.add(new SelectItem(value));

			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
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
		return user;
	}
	
	@Override
	public List<SelectItem> kitStatusBrowse() {
		Connection con = null;
		String query = "SELECT DISTINCT KIT_STATUS from Z_KIT_STATUSDEF where KIT_STATUS IS NOT NULL ";
		PreparedStatement ps = null;
		List<SelectItem> kitStatus = new ArrayList<SelectItem>();
		try {
			con = getConnection();
			ps = con.prepareStatement(query);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				/* MaterialItem downloadVO=new MaterialItem(); */
				String value = rs.getString("KIT_STATUS");
				kitStatus.add(new SelectItem(value));
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
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
		return kitStatus;
	}

	@Override
	public List<MaterialItem> kitNumberBrowse(String kitno)
			throws BusinessException {
		Connection con = null;
		String query = "SELECT  KIT_NO from Z_KITDEF where KIT_NO IS NOT NULL ";
		if ((kitno != null) && !"".equals(kitno)) {
			query = query + "  and KIT_NO like '" + kitno + "%'";
		}
		PreparedStatement ps = null;
		List<MaterialItem> user = new ArrayList<MaterialItem>();
		try {
			con = getConnection();
			ps = con.prepareStatement(query);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				MaterialItem downloadVO = new MaterialItem();
				downloadVO.setKitno(""+rs.getString("KIT_NO"));
				user.add(downloadVO);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
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
		return user;
	}
	}



