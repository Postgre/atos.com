package com.atos.hook;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.sap.me.common.ObjectReference;
import com.sap.me.customdata.CustomDataConfigurationServiceInterface;
import com.sap.me.demand.FindShopOrderResponse;
import com.sap.me.demand.ItemRouterException;
import com.sap.me.demand.OperationStatusException;
import com.sap.me.demand.ReleaseShopOrderResponse;
import com.sap.me.demand.RepetitiveOrderDueException;
import com.sap.me.demand.RouterStatusException;
import com.sap.me.demand.SfcCountException;
import com.sap.me.demand.ShopOrderBasicConfiguration;
import com.sap.me.demand.ShopOrderBomNewException;
import com.sap.me.demand.ShopOrderBomStatusException;
import com.sap.me.demand.ShopOrderFullConfiguration;
import com.sap.me.demand.ShopOrderInputException;
import com.sap.me.demand.ShopOrderItemException;
import com.sap.me.demand.ShopOrderItemNewException;
import com.sap.me.demand.ShopOrderItemStatusException;
import com.sap.me.demand.ShopOrderItemTypeException;
import com.sap.me.demand.ShopOrderNotFoundException;
import com.sap.me.demand.ShopOrderQuantityException;
import com.sap.me.demand.ShopOrderRecursionException;
import com.sap.me.demand.ShopOrderReleaseHookDTO;
import com.sap.me.demand.ShopOrderRmaException;
import com.sap.me.demand.ShopOrderServiceInterface;
import com.sap.me.demand.ShopOrderStatusException;
import com.sap.me.demand.ShopOrderUpdateException;
import com.sap.me.demand.UsedSfcException;
import com.sap.me.demand.WorkCenterPermitException;
import com.sap.me.demand.WorkCenterRequiredException;
import com.sap.me.demand.ShopOrderReleaseHookDTO.AdditionalInfoList;
import com.sap.me.extension.Services;
import com.sap.me.frame.SystemBase;
import com.sap.me.frame.domain.BusinessException;
import com.sap.me.frame.service.CommonMethods;
import com.sap.me.plant.WorkCenterBasicConfiguration;
import com.sap.me.plant.WorkCenterConfigurationServiceInterface;
import com.sap.me.productdefinition.BOMBasicConfiguration;
import com.sap.me.productdefinition.ItemConfigurationServiceInterface;
import com.sap.me.productdefinition.ItemFullConfiguration;
import com.sap.me.productdefinition.ItemKeyData;
import com.sap.me.production.SfcBasicData;
import com.sap.me.production.SfcDataServiceInterface;
import com.sap.me.production.SfcStateServiceInterface;
import com.sap.me.production.SfcStepFullData;
import com.sap.me.security.RunAsServiceLocator;
import com.visiprise.frame.configuration.ServiceReference;
import com.visiprise.frame.service.ext.ActivityInterface;
import com.visiprise.globalization.util.DateTimeInterface;

public class PostOrderReleaseHookWIP_Visibility implements ActivityInterface<ShopOrderReleaseHookDTO> 
{
	private ItemConfigurationServiceInterface itemConfigurationServiceInterface;
	private WorkCenterConfigurationServiceInterface workCenterConfigurationService;
	private CustomDataConfigurationServiceInterface customDataConfigurationServiceInterface;
	private ShopOrderBasicConfiguration shopOrderConfiguration;
	private BOMBasicConfiguration bomBasicConfiguration;
	private ItemFullConfiguration itemFullConfiguration; 
	private ShopOrderBasicConfiguration shopOrderBasicConfiguration;
	private ShopOrderFullConfiguration shopOrderFullConfiguration;
	private WorkCenterBasicConfiguration workCenterBasicConfiguration;
	private SfcBasicData sfcBasicData;
	private SfcStepFullData sfcStepFullData;
	private ReleaseShopOrderResponse releaseShopOrderResponse;
	private FindShopOrderResponse findShopOrderResponse;
	private SfcDataServiceInterface sfcDataService;
	private static final String SFC_DATA_SERVICE = "SfcDataService";
	private static final String COM_SAP_ME_PRODUCTION = "com.sap.me.production";
	private String CurrentSfc=null;
	private String workcenter=null;
	private final SystemBase dbBase = SystemBase.createSystemBase("jdbc/jts/wipPool");
	
	private Connection getConnection(){
		Connection con = null;

		con = dbBase.getDBConnection();

		return con;
	}
	
	private static final long serialVersionUID = 1L;
	private ShopOrderServiceInterface shoporderService;
	private SfcStateServiceInterface sfcStateServiceInterface;
	private String site = CommonMethods.getSite();
	private String user = CommonMethods.getUserId();

	@Override
	public void execute(ShopOrderReleaseHookDTO shopOrderReleaseHookDTO)
			throws Exception {

		ServiceReference shoporderServiceRef = new ServiceReference(
				"com.sap.me.demand", "ShopOrderService");
		shoporderService = RunAsServiceLocator.getService(shoporderServiceRef,
				ShopOrderServiceInterface.class, user, site, null);
		ShopOrderFullConfiguration fullConfiguration = shoporderService
				.readShopOrder(new ObjectReference(shopOrderReleaseHookDTO
						.getShopOrderBO().getValue()));
		ServiceReference sfcServiceRef = new ServiceReference(
				"com.sap.me.production", "SfcStateService");
		this.sfcStateServiceInterface = RunAsServiceLocator.getService(
				sfcServiceRef, SfcStateServiceInterface.class, user, site,
				null);
		List<AdditionalInfoList> additionalInfoList = shopOrderReleaseHookDTO.getAdditionalInfoList();
		for (AdditionalInfoList infoList : additionalInfoList) 
		{	
			insertData(shopOrderReleaseHookDTO,infoList,fullConfiguration.getPlannedStartDate());

		}
	}
	
	public void insertData(ShopOrderReleaseHookDTO shopOrderReleaseHookDTO,AdditionalInfoList infoList, DateTimeInterface plannedStartDate) throws ShopOrderNotFoundException, ShopOrderItemException, ShopOrderInputException, ShopOrderStatusException, ShopOrderQuantityException, RepetitiveOrderDueException, ShopOrderRmaException, ShopOrderUpdateException, ShopOrderRecursionException, ShopOrderItemStatusException, ShopOrderItemNewException, ShopOrderItemTypeException, ShopOrderBomStatusException, ShopOrderBomNewException, WorkCenterRequiredException, WorkCenterPermitException, ItemRouterException, RouterStatusException, OperationStatusException, SfcCountException, UsedSfcException, BusinessException
	{
		initServices(shopOrderReleaseHookDTO);
		String qry = "INSERT INTO Z_WIP_VISIBILITY (HANDLE,ITEM,SFC,ORDERSEQUENCE_BO,PARENT_SFC,STATUS,CREATED_BY,CREATED_DATE,ORDERNO,ACTIVE) VALUES (?,?,?,?,?,?,?,?,?,?)";
		Connection con = null;
		PreparedStatement preparedStatement = null;
		try {
			shopOrderFullConfiguration=this.shoporderService.readShopOrder(new ObjectReference(shopOrderReleaseHookDTO.getShopOrderBO().getValue()));
			con = getConnection();
			ItemKeyData itemKeyData=itemConfigurationServiceInterface.findItemKeyDataByRef(new ObjectReference(infoList.getItemBO().getValue()));
			sfcBasicData =sfcStateServiceInterface.findSfcDataByRef(new ObjectReference(infoList.getSfcBO().getValue()));
			String item=itemConfigurationServiceInterface.findItemKeyDataByRef(new ObjectReference(infoList.getItemBO().getValue())).getItem();
			String sfc=sfcStateServiceInterface.findSfcDataByRef(new ObjectReference(infoList.getSfcBO().getValue())).getSfc();
			String itemBO="ItemBO:"+site+","+item;
			String sfcBO="SFCBO:"+site+","+sfc;
			String orderNO=shopOrderFullConfiguration.getShopOrder();
			int status=0;
			preparedStatement = con.prepareStatement(qry);
			preparedStatement.setString(1,"WipVisibilityBO:"+site+","+itemBO+","+sfcBO+",ShopOrderBO:"+site+","+orderNO);
			preparedStatement.setString(2, item);
			preparedStatement.setString(3, sfc);
			preparedStatement.setString(4, getOrderSequenceBO(item,sfc));
			String parentOrderNo = getParentOrderNo(sfc,orderNO);
			preparedStatement.setString(5,getParentSFC(parentOrderNo));
			//preparedStatement.setString(5,"Working");
			preparedStatement.setInt(6,status);
			preparedStatement.setString(7,user);
			preparedStatement.setDate(8,utilDatetoSqlDate(new java.util.Date()));
			preparedStatement.setString(9,orderNO);
			preparedStatement.setInt(10,1);
			preparedStatement.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();

		}catch(BusinessException businessException){
			businessException.printStackTrace();
		}finally {
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
	}
	
	public java.sql.Date utilDatetoSqlDate(java.util.Date sqlDate) {
		if (sqlDate != null) {
			java.sql.Date sqlDate1 = new java.sql.Date(sqlDate.getTime());
			return sqlDate1;
		}
		return null;
	}
	public String getParentOrder(ShopOrderReleaseHookDTO shopOrderReleaseHookDTO)
	{
		String ParentShopOrder=null;
		String parentOrderRef=null;
		String parentOrder=null;
		String qry=" SELECT SHOP_ORDER_PARENT_BO FROM SHOP_ORDER_SUBASSEMBLY WHERE SUBASSEMBLY_SHOP_ORDER_BO=?";
		Connection con = null;
		PreparedStatement preparedStatement = null;
		try  
		{
			shopOrderFullConfiguration=this.shoporderService.readShopOrder(new ObjectReference(shopOrderReleaseHookDTO.getShopOrderBO().getValue()));
			parentOrder=shopOrderFullConfiguration.getShopOrderRef();
			con = getConnection();
			preparedStatement = con.prepareStatement(qry);
			preparedStatement.setString(1,parentOrder);
			ResultSet rs = preparedStatement.executeQuery();
			if(rs!=null) 
			{
			while (rs.next()) 
			{
				parentOrderRef=rs.getString("SHOP_ORDER_PARENT_BO");
				shopOrderFullConfiguration=this.shoporderService.readShopOrder(new ObjectReference(parentOrderRef));
				ParentShopOrder=shopOrderFullConfiguration.getShopOrder();

			}
			}
		}
			catch (SQLException e) {
			e.printStackTrace();

		}catch(BusinessException businessException){
			businessException.printStackTrace();
		}finally {
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

		return ParentShopOrder;
	}
	
	public String getParentOrderNo(String sfc,String orderNO)
	{
		//String parentOrderNo=null;
		String parentOrderNo=null;
		String qry="SELECT PARENT_ORDERNO FROM Z_ORDERSEQUENCE WHERE SFC = ? AND ORDERNO = ? ";
		Connection con = null;
		PreparedStatement preparedStatement = null;
		try  
		{
			con = getConnection();
			preparedStatement = con.prepareStatement(qry);
			preparedStatement.setString(1,sfc);
			preparedStatement.setString(2,orderNO);
			ResultSet rs = preparedStatement.executeQuery();
			if(rs!=null) 
			{
			while (rs.next()) 
			{
				parentOrderNo = rs.getString("PARENT_ORDERNO");

			}
			}
		}
			catch (SQLException e) {
			e.printStackTrace();

		}finally {
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

		return parentOrderNo;
	}
	
	
	
	public String getParentOrderNoandParentSFC(String item,String sfc,String orderNO)
	{
		//String parentOrderNo=null;
		String parentSFC=null;
		String handle = "OrderSequenceBO:"+site+",ItemBO:"+site+","+item+",SFCBO:"+site+","+sfc+",OrderBO:"+site+","+orderNO;
		String qry="SELECT ORDERNO,SFC FROM Z_ORDERSEQUENCE WHERE ORDERNO = (SELECT PARENT_ORDERNO FROM Z_ORDERSEQUENCE WHERE HANDLE like '" + handle + "%'";
		Connection con = null;
		PreparedStatement preparedStatement = null;
		try  
		{
			con = getConnection();
			preparedStatement = con.prepareStatement(qry);
			ResultSet rs = preparedStatement.executeQuery();
			if(rs!=null) 
			{
			while (rs.next()) 
			{
				String parentOrder = rs.getString("ORDERNO");
				parentSFC = rs.getString("SFC");
				

			}
			}
		}
			catch (SQLException e) {
			e.printStackTrace();

		}finally {
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

		return parentSFC;
	}
	
	
	public String getOrderSequenceBO(String item,String sfc)
	{
		String orderSequence_BO=null;
		String qry=" SELECT HANDLE FROM Z_ORDERSEQUENCE WHERE ITEM = ? AND SFC = ? AND ACTIVE = 1";
		Connection con = null;
		PreparedStatement preparedStatement = null;
		try  
		{
			con = getConnection();
			preparedStatement = con.prepareStatement(qry);
			preparedStatement.setString(1,item);
			preparedStatement.setString(2,sfc);
			ResultSet rs = preparedStatement.executeQuery();
			if(rs!=null) 
			{
			while (rs.next()) 
			{
				orderSequence_BO=rs.getString("HANDLE");

			}
			}
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

		return orderSequence_BO;
	}
	
	public String getParentSFC(String parentOrder)
	{
		String parentSFC=null;
		String qry="SELECT SFC FROM Z_ORDERSEQUENCE WHERE ORDERNO = ? AND ACTIVE = 1";
		Connection con = null;
		PreparedStatement preparedStatement = null;
		try  
		{
			con = getConnection();
			preparedStatement = con.prepareStatement(qry);
			preparedStatement.setString(1,parentOrder);
			ResultSet rs = preparedStatement.executeQuery();
			if(rs!=null) 
			{
			while (rs.next()) 
			{
				parentSFC=rs.getString("SFC");

			}
			}
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

		return parentSFC;
	}
	private void initServices(ShopOrderReleaseHookDTO shopOrderReleaseHookDTO) throws ShopOrderInputException, ShopOrderNotFoundException, BusinessException 
	{
		ServiceReference shoporderServiceRef = new ServiceReference(
				"com.sap.me.demand", "ShopOrderService");
		shoporderService = RunAsServiceLocator.getService(shoporderServiceRef,
				ShopOrderServiceInterface.class, user, site, null);
		ShopOrderFullConfiguration fullConfiguration = shoporderService
				.readShopOrder(new ObjectReference(shopOrderReleaseHookDTO
						.getShopOrderBO().getValue()));
		ServiceReference sfcServiceRef = new ServiceReference(
				"com.sap.me.production", "SfcStateService");
		this.sfcStateServiceInterface = RunAsServiceLocator.getService(
				sfcServiceRef, SfcStateServiceInterface.class, user, site,
				null);
		ServiceReference itemServiceRef = new ServiceReference(
				"com.sap.me.productdefinition", "ItemConfigurationService");
		itemConfigurationServiceInterface = RunAsServiceLocator.getService(
				itemServiceRef, ItemConfigurationServiceInterface.class,
				user, site, null);
		ServiceReference workCenterServiceRef = new ServiceReference(
				"com.sap.me.plant", "WorkCenterConfigurationService");
		workCenterConfigurationService = RunAsServiceLocator.getService(
				workCenterServiceRef,
				WorkCenterConfigurationServiceInterface.class, user, site,
				null);
		sfcDataService = Services.getService("com.sap.me.production", "SfcDataService");
	
	}

}
