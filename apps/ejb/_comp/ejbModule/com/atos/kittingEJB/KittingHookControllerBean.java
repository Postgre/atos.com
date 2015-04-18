package com.atos.kittingEJB;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.ejb.Stateless;

import com.sap.me.common.CustomValue;
import com.sap.me.common.ObjectReference;
import com.sap.me.customdata.CustomDataConfigurationServiceInterface;
import com.sap.me.demand.FindShopOrderResponse;
import com.sap.me.demand.ReleaseShopOrderResponse;
import com.sap.me.demand.ShopOrderBasicConfiguration;
import com.sap.me.demand.ShopOrderFullConfiguration;
import com.sap.me.demand.ShopOrderServiceInterface;
import com.sap.me.extension.Services;
import com.sap.me.frame.SystemBase;
import com.sap.me.frame.service.CommonMethods;
import com.sap.me.plant.WorkCenterBasicConfiguration;
import com.sap.me.plant.WorkCenterConfigurationServiceInterface;
import com.sap.me.productdefinition.BOMBasicConfiguration;
import com.sap.me.productdefinition.ItemConfigurationServiceInterface;
import com.sap.me.productdefinition.ItemFullConfiguration;
import com.sap.me.production.PostCompleteHookDTO;
import com.sap.me.production.SfcBasicData;
import com.sap.me.production.SfcDataServiceInterface;
import com.sap.me.production.SfcStateServiceInterface;
import com.sap.me.production.SfcStepFullData;
import com.sap.me.production.StartHookDTO;
import com.sap.me.security.RunAsServiceLocator;
import com.sap.me.wpmf.MessageType;
import com.sap.me.wpmf.util.MessageHandler;
import com.visiprise.frame.configuration.ServiceReference;
@Stateless
public class KittingHookControllerBean implements KittingHookInterface
{
	private static final long serialVersionUID = 1L;
	private SfcStateServiceInterface sfcStateServiceInterface;
	private ItemConfigurationServiceInterface itemConfigurationServiceInterface;
	private SfcBasicData sfcBasicData;
	private SfcDataServiceInterface sfcDataService;
	private static final String SFC_DATA_SERVICE = "SfcDataService";
	private static final String COM_SAP_ME_PRODUCTION = "com.sap.me.production";
	private String site = CommonMethods.getSite();
	private String user = CommonMethods.getUserId();
	private String CurrentSfc=null;
	private final SystemBase dbBase = SystemBase.createSystemBase("jdbc/jts/wipPool");
	
	private Connection getConnection(){
		Connection con = null;

		con = dbBase.getDBConnection();

		return con;
	}
	
	@Override
	public void preStartHookKitting(StartHookDTO dto) throws Exception 
	{
		initServices();
		String workcenter=null;
		String kitDefBO=null;
		String sfcBO=null;
		boolean isStarted=false;
		sfcBasicData =sfcStateServiceInterface.findSfcDataByRef(new ObjectReference(dto.getSfcBO().getValue()));
		CurrentSfc=sfcStateServiceInterface.findSfcDataByRef(new ObjectReference(dto.getSfcBO().getValue())).getSfc();
		ItemFullConfiguration fullConfiguration=itemConfigurationServiceInterface.readItem(new ObjectReference(sfcBasicData.getItemRef()));
		List<CustomValue> itemCustomValues=fullConfiguration.getCustomData();
		for(CustomValue customValue:itemCustomValues)
		{
		if("WORK_CENTER".equals(""+customValue.getName()))
		{
			workcenter=""+customValue.getValue();
			break;
		}
	}
		String qry=" SELECT KITDEF_BO,SFC_BO FROM Z_KIT_ASSIGNMENT WHERE  PRIORITY=(select MIN(PRIORITY) FROM Z_KIT_ASSIGNMENT WHERE WORKCENTER=? AND CREATED_DATE=? AND ACTIVE=1) AND WORKCENTER=? and CREATED_DATE=? and ACTIVE=1";
		Connection con = null;
		PreparedStatement ps = null;
		try  
		{
		con = getConnection();
		ps = con.prepareStatement(qry);
		ps.setString(1,workcenter);
		ps.setDate(2,utilDatetoSqlDate(new java.util.Date()));
		ps.setString(3,workcenter);
		ps.setDate(4,utilDatetoSqlDate(new java.util.Date()));
		ResultSet rs = ps.executeQuery();
		if(rs!=null) 
		{
			while (rs.next()) 
			{
				sfcBO=rs.getString("SFC_BO");
				if(getSfcBOfromSFCTable(CurrentSfc).equals(sfcBO))
				{
					isStarted=true;
					kitDefBO=rs.getString("KITDEF_BO");
					if(kitDefBO!=null && !"".equals(kitDefBO))
					{
			            String kitNo=getKitNoPreStart(kitDefBO);
						updateKitDef(kitNo);
					}
					MessageHandler.clear();
		            MessageHandler.handle(" SFC "+CurrentSfc+ " is associated with Kit .", null, MessageType.INFO);
		            continue;
				}
				}
		}
	}
		catch (SQLException e) {
		e.printStackTrace();

	}finally {
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
	

	public String getKitNoPreStart(String kitDefBO)
	{
		String qry=null;
		String kitNoFromTable=null;
		Connection con = null;
		qry = "SELECT KIT_NO FROM Z_KITDEF WHERE ACTIVE = 1 AND HANDLE = ? AND KIT_STATUS = ?";
		PreparedStatement ps = null;
		try  {
			con = getConnection();
			ps = con.prepareStatement(qry);
			ps.setString(1,kitDefBO);
			ps.setString(2,"ASSIGNED");
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
	
	

	
	public boolean updateKitDef(String kitno) 
	{
		boolean isUpdated=false;
		Connection con = null;
		String qry ="UPDATE Z_KITDEF SET KIT_STATUS_BO = ?,KIT_STATUS = ? ,UPDATED_BY = ?,UPDATED_DATE = ? WHERE KIT_NO = ? AND ACTIVE = 1";
		PreparedStatement preparedStatement = null;
		try 
			{
			con = getConnection();
			String kitStatusTemp="INUSE";
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
	public void postCompleteHookKitting(PostCompleteHookDTO dto) throws Exception 
	
	{
		initServices();
		sfcBasicData =sfcStateServiceInterface.findSfcDataByRef(new ObjectReference(dto.getSfcBO().getValue()));
		String kitDefBO=null;
		CurrentSfc=sfcStateServiceInterface.findSfcDataByRef(new ObjectReference(dto.getSfcBO().getValue())).getSfc();
		String qry="SELECT KITDEF_BO FROM Z_KIT_ASSIGNMENT WHERE SFC_BO = ? AND ACTIVE = 1";
		Connection con = null;
		PreparedStatement ps = null;
		try  
		{
		con = getConnection();
		ps = con.prepareStatement(qry);
		ps.setString(1,getSfcBOfromSFCTable(CurrentSfc));
		ResultSet rs = ps.executeQuery();
		if(rs!=null) 
		{
			while (rs.next()) {
			kitDefBO=rs.getString("KITDEF_BO");
			String kitNo=getKitNoPostComplete(kitDefBO);
			boolean isUpdated=updateKitDefAVAILABLE(kitNo);
			boolean isUpdatedAssignment=disableSfcBO(getSfcBOfromSFCTable(CurrentSfc),kitDefBO);
			if(isUpdated==true && isUpdatedAssignment==true)
			{
			MessageHandler.clear();
            MessageHandler.handle(" SFC "+CurrentSfc+ " is done and disassociated from Kit .", null, MessageType.SUCCESS);
			}
		}
		}
		}
		catch (SQLException e) {
		e.printStackTrace();

	}finally {
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
	private void initServices()
	{
		sfcDataService = Services.getService(COM_SAP_ME_PRODUCTION, SFC_DATA_SERVICE);
		ServiceReference itemServiceRef = new ServiceReference(
				"com.sap.me.productdefinition", "ItemConfigurationService");
		itemConfigurationServiceInterface = RunAsServiceLocator.getService(
				itemServiceRef, ItemConfigurationServiceInterface.class,
				user, site, null);
		ServiceReference sfcServiceRef = new ServiceReference(
				"com.sap.me.production", "SfcStateService");
		this.sfcStateServiceInterface = RunAsServiceLocator.getService(
				sfcServiceRef, SfcStateServiceInterface.class, user, site,
				null);
	
	
	}
	public String getKitNoPostComplete(String kitDefBO)
	{
		String qry=null;
		String kitNoFromTable=null;
		Connection con = null;
		qry = "SELECT KIT_NO FROM Z_KITDEF WHERE ACTIVE = 1 AND HANDLE = ? AND KIT_STATUS = ?";
		PreparedStatement ps = null;
		try  {
			con = getConnection();
			ps = con.prepareStatement(qry);
			ps.setString(1,kitDefBO);
			ps.setString(2,"INUSE");
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
	
	public String getSfcBOfromSFCTable(String sfc)
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

	
	public boolean updateKitDefINUSE(String kitno) 
	{
		boolean isUpdated=false;
		Connection con = null;
		String qry ="UPDATE Z_KITDEF SET KIT_STATUS_BO = ?,KIT_STATUS = ? ,UPDATED_BY = ?,UPDATED_DATE = ? WHERE KIT_NO = ? AND ACTIVE = 1";
		PreparedStatement preparedStatement = null;
		try 
			{
			con = getConnection();
			String kitStatusTemp="INUSE";
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
	
	public boolean updateKitDefAVAILABLE(String kitno) 
	{
		boolean isUpdated=false;
		Connection con = null;
		String qry ="UPDATE Z_KITDEF SET KIT_STATUS_BO = ?,KIT_STATUS = ? ,UPDATED_BY = ?,UPDATED_DATE = ? WHERE KIT_NO = ? AND ACTIVE = 1";
		PreparedStatement preparedStatement = null;
		try 
			{
			con = getConnection();
			String kitStatusTemp="AVAILABLE";
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
	
	public boolean disableSfcBO(String sfcBO,String kitDefBO) 
	{
		boolean isUpdated=false;
		Connection con = null;
		String qry ="UPDATE Z_KIT_ASSIGNMENT SET ACTIVE = 0 WHERE SFC_BO = ? and KITDEF_BO = ?";
		PreparedStatement preparedStatement = null;
		try 
			{
			con = getConnection();
			preparedStatement = con.prepareStatement(qry);
			preparedStatement.setString(1,sfcBO);
			preparedStatement.setString(2,kitDefBO);
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
	
		
		
	}



