package com.atos.ordersequenceEJB;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateless;

import com.atos.kittingEJB.KittingDefinitionData;
import com.sap.me.common.CustomValue;
import com.sap.me.common.ObjectReference;
import com.sap.me.customdata.CustomDataConfigurationServiceInterface;
import com.sap.me.demand.FindShopOrderResponse;
import com.sap.me.demand.ItemRouterException;
import com.sap.me.demand.OperationStatusException;
import com.sap.me.demand.ReleaseShopOrderResponse;
import com.sap.me.demand.RepetitiveOrderDueException;
import com.sap.me.demand.RouterStatusException;
import com.sap.me.demand.SFCBOHandle;
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
import com.sap.me.production.StartHookDTO;
import com.sap.me.security.RunAsServiceLocator;
import com.sap.me.wpmf.MessageType;
import com.sap.me.wpmf.util.MessageHandler;
import com.visiprise.frame.configuration.ServiceReference;
import com.visiprise.globalization.util.DateTimeInterface;
@Stateless
public class OrderSequenceDataBean implements OrderSequenceDataInterface
{
	private ShopOrderServiceInterface shoporderService;
	private SfcStateServiceInterface sfcStateServiceInterface;
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
	private String site = CommonMethods.getSite();
	private String user = CommonMethods.getUserId();
	private String CurrentSfc=null;
	private String workcenter=null;
	private static final String orderSequenceTable = "Z_ORDERSEQUENCE";
	private final SystemBase dbBase = SystemBase.createSystemBase("jdbc/jts/wipPool");
	
	private Connection getConnection(){
		Connection con = null;

		con = dbBase.getDBConnection();

		return con;
	}
	
	@Override
	public List<OrderSequenceData> readCustomTableData(String workcenter,String material,java.util.Date date){
		String qry=null;
		Connection con = null;
		qry = "SELECT HANDLE,ITEM,SFC,ORDERNO,PARENT_ORDERNO,WORKCENTER,PRIORITY,PLANNED_START_DATE,SPECIAL_INSTRUCTION,EXPORT_IMPORT,CREATED_DATE,CREATED_BY,STATUS,ACTIVE,MODEL,WHEEL_BASE,COUNTRY,COLOR,DRIVER from " +orderSequenceTable+ " where ACTIVE=? "+" ";
		if ((workcenter!=null)&&(!"".equals(workcenter)))
		{
			qry = qry + " and WORKCENTER like '" +workcenter+ "' ";
		}
		
		if ((material!=null)&&(!"".equals(material)))
		{
			qry = qry + " and ITEM like '" +material+ "' ";
		}
		if ((date!=null)&& (!"".equals(date)))
		{
			qry = qry + " and PLANNED_START_DATE = '" +utilDatetoSqlDate(date)+ "' ";
		}
		
		qry = qry + " ORDER BY PLANNED_START_DATE,PRIORITY ";
		PreparedStatement ps = null;
		List<OrderSequenceData> orderSequenceDataList = new ArrayList<OrderSequenceData>();
		try  {
			con = getConnection();
			ps = con.prepareStatement(qry);
			ps.setInt(1,1);
			ResultSet rs = ps.executeQuery();
			if(rs!=null) {
			while (rs.next()) {
				
				OrderSequenceData orderSequenceData = new OrderSequenceData();	
				orderSequenceData.setHandle(""+rs.getString("HANDLE"));
				orderSequenceData.setItem(""+rs.getString("ITEM"));
				orderSequenceData.setSfc(""+rs.getString("SFC"));
				orderSequenceData.setOrder(""+rs.getString("ORDERNO"));
				orderSequenceData.setCreateon(rs.getDate("CREATED_DATE"));
				orderSequenceData.setCreateby(""+rs.getString("CREATED_BY"));
				orderSequenceData.setStatus(rs.getInt("STATUS"));
				orderSequenceData.setPriority1(rs.getInt("PRIORITY"));
				orderSequenceData.setPlannedstartdate(rs.getDate("PLANNED_START_DATE"));
				orderSequenceData.setExport_import2(stringToBoolean(""+rs.getString("EXPORT_IMPORT")));
				orderSequenceData.setActive(rs.getInt("ACTIVE"));
				orderSequenceData.setParent_order(""+rs.getString("PARENT_ORDERNO"));
				orderSequenceData.setModel(""+rs.getString("MODEL"));
				orderSequenceData.setWheelbase(""+rs.getString("WHEEL_BASE"));
				orderSequenceData.setCountry(""+rs.getString("COUNTRY"));
				orderSequenceData.setColor(""+rs.getString("COLOR"));
				orderSequenceData.setDrive(""+rs.getString("DRIVER"));
				if(rs.getString("WORKCENTER")!=null) {
				orderSequenceData.setWorkcenter(""+rs.getString("WORKCENTER"));
				}
				else {
					orderSequenceData.setWorkcenter("--");
				}				
				
				if(rs.getString("SPECIAL_INSTRUCTION")!=null)
				{
				orderSequenceData.setSpecial_int(""+rs.getString("SPECIAL_INSTRUCTION"));
				}
				else {
					orderSequenceData.setSpecial_int("");
				}
				orderSequenceDataList.add(orderSequenceData);
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
	return orderSequenceDataList;
	}

	@Override
	public String findExportImportBySFC(String sfc) {
		String exportImportVal=null;
		Connection con = null;
		String qry = "SELECT EXPORT_IMPORT FROM Z_ORDERSEQUENCE WHERE SFC = ? AND ACTIVE = 1";
		PreparedStatement ps = null;
		try  {
			con = getConnection();
			ps = con.prepareStatement(qry);
			ps.setString(1,sfc);
			ResultSet rs = ps.executeQuery();
			if(rs!=null) {
			while (rs.next())
			{
				exportImportVal=rs.getString("EXPORT_IMPORT");
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
		return exportImportVal;
	}

	@Override
	public void updateDataExceptPriority(OrderSequenceData orderSequenceData,java.util.Date date,String boolString)
	{
	Connection con = null;
	String qry ="update " +orderSequenceTable+ " set PLANNED_START_DATE=?,EXPORT_IMPORT=?,SPECIAL_INSTRUCTION=?,UPDATED_DATE=?,UPDATED_BY=? where SFC=? AND ACTIVE = 1";
	PreparedStatement ps = null;
	
	try 
		{
		con = getConnection();
		ps = con.prepareStatement(qry);
		ps.setDate(1,utilDatetoSqlDate(date));
		if(boolString=="true")
		{
			boolString="Yes";
		}
		else if(boolString=="false")
		{
			boolString="No";
		}
		ps.setString(2,boolString);
		ps.setString(3,orderSequenceData.getSpecial_int());
		ps.setDate(4,orderSequenceData.getUpdateon());
		ps.setString(5, orderSequenceData.getUpdateby());
		ps.setString(6, orderSequenceData.getSfc());
		ps.executeUpdate();

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
  }

	
	@Override
	public void insertData(ShopOrderReleaseHookDTO shopOrderReleaseHookDTO,AdditionalInfoList infoList, DateTimeInterface plannedStartDate) throws ShopOrderNotFoundException, ShopOrderItemException, ShopOrderInputException, ShopOrderStatusException, ShopOrderQuantityException, RepetitiveOrderDueException, ShopOrderRmaException, ShopOrderUpdateException, ShopOrderRecursionException, ShopOrderItemStatusException, ShopOrderItemNewException, ShopOrderItemTypeException, ShopOrderBomStatusException, ShopOrderBomNewException, WorkCenterRequiredException, WorkCenterPermitException, ItemRouterException, RouterStatusException, OperationStatusException, SfcCountException, UsedSfcException, BusinessException
	{
		initServices(shopOrderReleaseHookDTO);
		String qry = "INSERT INTO " +orderSequenceTable+ "(HANDLE,ITEM,SFC,ORDERNO,WORKCENTER,ACTIVE,CREATED_BY,CREATED_DATE,PARENT_ORDERNO,STATUS,MODEL,WHEEL_BASE,COUNTRY,COLOR,DRIVER) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		Connection con = null;
		PreparedStatement preparedStatement = null;
		try {
			shopOrderFullConfiguration=this.shoporderService.readShopOrder(new ObjectReference(shopOrderReleaseHookDTO.getShopOrderBO().getValue()));
			con = getConnection();
			ItemKeyData itemKeyData=itemConfigurationServiceInterface.findItemKeyDataByRef(new ObjectReference(infoList.getItemBO().getValue()));
			sfcBasicData =sfcStateServiceInterface.findSfcDataByRef(new ObjectReference(infoList.getSfcBO().getValue()));
			String item=itemConfigurationServiceInterface.findItemKeyDataByRef(new ObjectReference(infoList.getItemBO().getValue())).getItem();
			String sfc=sfcStateServiceInterface.findSfcDataByRef(new ObjectReference(infoList.getSfcBO().getValue())).getSfc();
			String itemBO=itemKeyData.getRef();
			SFCBOHandle sfcRef = new SFCBOHandle(site,sfc);
			String sfcBO = sfcRef.getValue();
			String orderNO=shopOrderFullConfiguration.getShopOrder();
			String orderBO=shopOrderFullConfiguration.getShopOrderRef();
			int status=0;
			preparedStatement = con.prepareStatement(qry);
			preparedStatement.setString(1,"OrderSequenceBO:"+site+","+itemBO+","+sfcBO+","+orderBO+",StatusBO:"+site+","+status);
			preparedStatement.setString(2, item);
			preparedStatement.setString(3, sfc);
			preparedStatement.setString(4, orderNO);
			ItemFullConfiguration fullConfiguration=itemConfigurationServiceInterface.readItem(new ObjectReference(infoList.getItemBO().getValue()));
			List<CustomValue> itemCustomValues=fullConfiguration.getCustomData();
			String workcenter=null;
			String model=null;
			String wheelbase=null;
			String country=null;
			String color=null;
			String drive=null;
			for(CustomValue customValue:itemCustomValues){
				if("WORK_CENTER".equals(""+customValue.getName()))
				{
					workcenter=""+customValue.getValue();
				}
				if("MODEL".equals(""+customValue.getName()))
				{
					model=""+customValue.getValue();
				}
				if("WHEEL_BASE".equals(""+customValue.getName()))
				{
					wheelbase=""+customValue.getValue();
				}
				if("COUNTRY".equals(""+customValue.getName()))
				{
					country=""+customValue.getValue();
				}
				if("COLOR".equals(""+customValue.getName()))
				{
					color=""+customValue.getValue();
				}
				if("DRIVER".equals(""+customValue.getName()))
				{
					drive=""+customValue.getValue();
				}
			}
			preparedStatement.setString(5, workcenter);
			preparedStatement.setInt(6,1);
			preparedStatement.setString(7,user);
			preparedStatement.setDate(8,new Date(plannedStartDate.getTimeInMillis()));
			preparedStatement.setString(9,getParentOrder(shopOrderReleaseHookDTO));
			preparedStatement.setInt(10,status);
			preparedStatement.setString(11,model);
			preparedStatement.setString(12,wheelbase);
			preparedStatement.setString(13,country);
			preparedStatement.setString(14,color);
			preparedStatement.setString(15,drive);
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
	
	
		@Override
		public boolean updateData(OrderSequenceData orderSequenceData) throws BusinessException{
		Connection con = null;
		boolean isUpdated=false;
		String qry ="UPDATE " +orderSequenceTable+ " SET ACTIVE = ? WHERE WORKCENTER = ? AND SFC = ?";
		PreparedStatement ps = null;
		try 
			{
			con = getConnection();
			ps = con.prepareStatement(qry);
			ps.setInt(1,0);
			ps.setString(2,orderSequenceData.getWorkcenter());
			ps.setString(3,orderSequenceData.getSfc());
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
	@Override
	public boolean insertAfterUpdateData(OrderSequenceData orderSequenceData,Date date,String boolString) throws BusinessException{
	Connection con = null;
	boolean isInserted = false;
	String qry = "INSERT INTO " +orderSequenceTable+ "(HANDLE,ITEM,SFC,ORDERNO,PARENT_ORDERNO,WORKCENTER,SPECIAL_INSTRUCTION,ACTIVE,CREATED_BY,CREATED_DATE,STATUS,UPDATED_DATE,UPDATED_BY,PRIORITY,PLANNED_START_DATE,EXPORT_IMPORT,MODEL,WHEEL_BASE,COUNTRY,COLOR,DRIVER) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	PreparedStatement ps = null;
	try {
		con = getConnection();
		ps = con.prepareStatement(qry);
		ps.setString(1,orderSequenceData.getHandle());
		ps.setString(2,orderSequenceData.getItem());
		ps.setString(3,orderSequenceData.getSfc());
		ps.setString(4,orderSequenceData.getOrder());
		ps.setString(5,orderSequenceData.getParent_order());
		ps.setString(6,orderSequenceData.getWorkcenter());
		ps.setString(7,orderSequenceData.getSpecial_int());
		ps.setInt(8, orderSequenceData.getActive());
		ps.setString(9,orderSequenceData.getCreateby());
		ps.setDate(10,orderSequenceData.getCreateon());
		ps.setInt(11,orderSequenceData.getStatus());
		ps.setDate(12,orderSequenceData.getUpdateon());
		ps.setString(13,orderSequenceData.getUpdateby());
		ps.setInt(14,orderSequenceData.getPriority1());
		ps.setDate(15,orderSequenceData.getPlannedstartdate());
		if(boolString=="true"){
			boolString="Yes";
		}
		else if(boolString=="false"){
			boolString="No";
		}
		ps.setString(16,boolString);
		ps.setString(17,orderSequenceData.getModel());
		ps.setString(18,orderSequenceData.getWheelbase());
		ps.setString(19,orderSequenceData.getCountry());
		ps.setString(20,orderSequenceData.getColor());
		ps.setString(21,orderSequenceData.getDrive());
		ps.executeUpdate();
		isInserted = true;
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
	return isInserted;
  }
	@Override
	public boolean readDataPreStartHook(StartHookDTO dto) throws Exception,BusinessException
	{
		boolean isSuccessful=false;
		String sfcFromTable=null;
		initServices();
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
	String qry="SELECT SFC FROM " +orderSequenceTable+ " WHERE PRIORITY=(select MIN(PRIORITY) from Z_ORDERSEQUENCE where WORKCENTER=? and PLANNED_START_DATE=? and ACTIVE=? and STATUS != ?)and WORKCENTER=? and PLANNED_START_DATE=? and ACTIVE=? and STATUS != ?";
	Connection con = null;
	PreparedStatement ps = null;
	try  
	{
		con = getConnection();
		ps = con.prepareStatement(qry);
		ps.setString(1,workcenter);
		ps.setDate(2,utilDatetoSqlDate(new java.util.Date()));
		ps.setInt(3,1);
		ps.setInt(4,999);
		ps.setString(5,workcenter);
		ps.setDate(6,utilDatetoSqlDate(new java.util.Date()));
		ps.setInt(7,1);
		ps.setInt(8,999);
		ResultSet rs = ps.executeQuery();
		if(rs!=null) {
		while (rs.next()) {
			sfcFromTable=rs.getString("SFC");
			if(sfcFromTable.equals(CurrentSfc)) {
				isSuccessful=true;
				continue;
			}
			else {
				throw new Exception(" SFC "+sfcFromTable+" is having least Priority on current date :" +new java.util.Date()+ ", SFC " +CurrentSfc+ " can not start .");	
			}
		}
		}
	} 
	catch (SQLException e) {
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
		} catch (Exception e2) 
		{
			e2.printStackTrace();
		}
	}
	return isSuccessful;
	}
	@Override
	public void updateDataPostStartHook(StartHookDTO dto) throws Exception
	{
	Connection con = null;
	initServices();	
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
	String qry="UPDATE " +orderSequenceTable+ " SET ACTIVE = ?,STATUS = ? where SFC = ? and WORKCENTER = ? and PLANNED_START_DATE = ?";
	PreparedStatement ps = null;
	try {
		con = getConnection();
		ps = con.prepareStatement(qry);
		ps.setInt(1,0);
		ps.setInt(2,999);
		ps.setString(3,CurrentSfc);
		ps.setString(4,workcenter);
		ps.setDate(5,utilDatetoSqlDate(new java.util.Date()));
		ps.executeUpdate();
	
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
	
	@Override
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
	
	@Override
	public String getSfcFromTable(int givenPriority,String workcenter,java.util.Date plannedStartDate)
	{
		String sfcFromTable=null;
		String qry=null;
		Connection con = null;
		qry = "SELECT SFC FROM " +orderSequenceTable+ " WHERE PRIORITY = ? AND WORKCENTER = ? AND PLANNED_START_DATE = ? AND ACTIVE = 1";
		PreparedStatement ps = null;
		try  {
			con = getConnection();
			ps = con.prepareStatement(qry);
			ps.setInt(1,givenPriority);
			ps.setString(2, workcenter);
			ps.setDate(3, utilDatetoSqlDate(plannedStartDate));
			ResultSet rs = ps.executeQuery();
			if(rs!=null) {
			while (rs.next())
			{
				sfcFromTable=rs.getString("SFC");
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
		return sfcFromTable;
	}
	public boolean stringToBoolean(String format)
	{ 
		boolean b1 = Boolean.parseBoolean(format);
		if(format.equals("Yes")){
			b1=true;
		}
		else if(format.equals("No")){
			b1=false;
		}
		else if(format.equals(null)){
			b1=false;
		}
		return b1;
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
	
	public String getPreviousSFC(String sfcRef) throws BusinessException {
		Connection con = null;
		String sfc = null;
		String query = "SELECT SFC FROM SFC_ID_HISTORY WHERE SFC_BO = ?";
		PreparedStatement ps = null;
		try {
			con = getConnection();
			ps = con.prepareStatement(query);
			ps.setString(1, sfcRef);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				sfc = rs.getString("SFC");
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
		return sfc;
	}
	
	@Override
	public String readSpecialInstruction(String sfc) throws BusinessException
	{
		String qry=null;
		Connection con = null;
		SFCBOHandle sfcRef = new SFCBOHandle(site, sfc);
		String sfc_bo = sfcRef.getValue();
		String previousSfc = getPreviousSFC(sfc_bo);
		String specialInstruction=null;
		qry = "SELECT SPECIAL_INSTRUCTION FROM " +orderSequenceTable+ " WHERE (ACTIVE = 1 OR STATUS = 999) AND SFC = ?";
		PreparedStatement ps = null;
		try  {
			con = getConnection();
			ps = con.prepareStatement(qry);
			ps.setString(1,previousSfc);
			ResultSet rs = ps.executeQuery();
			if(rs!=null) {
			while (rs.next()) {
			
				specialInstruction = rs.getString("SPECIAL_INSTRUCTION");
				
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
	return specialInstruction;
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
	

}
