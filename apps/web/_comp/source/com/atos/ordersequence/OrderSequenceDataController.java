package com.atos.ordersequence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.component.html.HtmlOutputText;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ActionListener;
import javax.faces.event.ValueChangeEvent;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;

import com.atos.ordersequenceEJB.MaterialCustomDataItem;
import com.atos.ordersequenceEJB.MaterialItem;
import com.atos.ordersequenceEJB.OrderSequenceData;
import com.atos.ordersequenceEJB.OrderSequenceDataInterface;
import com.sap.me.activity.ActivityConfigurationServiceInterface;
import com.sap.me.activity.ActivityOption;
import com.sap.me.activity.FindActivityOptionsRequest;
import com.sap.me.demand.ShopOrderReleaseHookDTO;
import com.sap.me.demand.ShopOrderServiceInterface;
import com.sap.me.extension.Services;
import com.sap.me.frame.SystemBase;
import com.sap.me.frame.domain.BusinessException;
import com.sap.me.frame.service.CommonMethods;
import com.sap.me.plant.WorkCenterBasicConfiguration;
import com.sap.me.plant.WorkCenterConfigurationServiceInterface;
import com.sap.me.productdefinition.ItemConfigurationServiceInterface;
import com.sap.me.productdefinition.OperationConfigurationServiceInterface;
import com.sap.me.productdefinition.RouterConfigurationServiceInterface;
import com.sap.me.production.ProcessLotServiceInterface;
import com.sap.me.production.SfcStateServiceInterface;
import com.sap.me.production.podclient.BasePodPlugin;
import com.sap.me.security.RunAsServiceLocator;
import com.sap.me.wpmf.TableConfigurator;
import com.sap.me.wpmf.util.FacesUtility;
import com.sap.tc.ls.api.enumerations.LSMessageType;
import com.sap.ui.faces.component.sap.UICommandInputText;
import com.visiprise.frame.configuration.ServiceReference;

public class OrderSequenceDataController extends BasePodPlugin implements
		ActionListener {

	private static final long serialVersionUID = 1L;
	private String material;
	private String priority;
	private boolean selectProductionLine;
	private boolean selectMaterial;
	private Date scheduleStartDate;
	private Date scheduleStartDate2;
	private Date scheduleEndDate;
	private String productionLine;
	private Boolean materialBrowseRendered;
	private Boolean productionLineBrowseRendered;
	private Boolean orderReleasePopupRendered;
	private Boolean disableBrowsePopup;
	private Boolean disableMaterial;
	private Boolean disableProductionLine;
	private Boolean disableStartDate;
	private String userID = CommonMethods.getUserId();
	private List<MaterialItem> materialList = new ArrayList<MaterialItem>();
	private List<MaterialItem> productionLineList = new ArrayList<MaterialItem>();
	private ShopOrderServiceInterface shoporderService;
	private WorkCenterConfigurationServiceInterface workCenterConfigurationService;
	private ItemConfigurationServiceInterface itemConfigurationServiceInterface;
	private RouterConfigurationServiceInterface routerConfigurationServiceInterface;
	private OperationConfigurationServiceInterface operationConfigurationServiceInterface;
	private SfcStateServiceInterface sfcStateServiceInterface;
	private ProcessLotServiceInterface processLotServiceInterface;
	private ActivityConfigurationServiceInterface activityConfigService;
	private String message;
	private String site = CommonMethods.getSite();
	private String user = CommonMethods.getUserId();
	private List<MaterialCustomDataItem> materialCustomDataList;
	private TableConfigurator tableConfigBean = null;
	private boolean sequencing;
	private boolean releasable;
	private static final String RELEASABLE_RULE = "RELEASABLE";
	private static final String SEQUENCING_RULE = "SEQUENCING";
	private static final String YES = "Y";
	private static final String NO = "N";
	public String startDate;
	private CommonMethods commonMethods;
	private ShopOrderReleaseHookDTO shopOrderReleaseHookDTO;
	private Date systemDate = new Date();

	public OrderSequenceDataController() {
		ServiceReference shoporderServiceRef = new ServiceReference(
				"com.sap.me.demand", "ShopOrderService");
		shoporderService = RunAsServiceLocator.getService(shoporderServiceRef,
				ShopOrderServiceInterface.class, user, site, null);
		ServiceReference workCenterServiceRef = new ServiceReference(
				"com.sap.me.plant", "WorkCenterConfigurationService");
		workCenterConfigurationService = RunAsServiceLocator
				.getService(workCenterServiceRef,
						WorkCenterConfigurationServiceInterface.class, user,
						site, null);
		ServiceReference itemServiceRef = new ServiceReference(
				"com.sap.me.productdefinition", "ItemConfigurationService");
		itemConfigurationServiceInterface = RunAsServiceLocator.getService(
				itemServiceRef, ItemConfigurationServiceInterface.class, user,
				site, null);

		ServiceReference routerServiceRef = new ServiceReference(
				"com.sap.me.productdefinition", "RouterConfigurationService");
		this.routerConfigurationServiceInterface = RunAsServiceLocator
				.getService(routerServiceRef,
						RouterConfigurationServiceInterface.class, user, site,
						null);
		ServiceReference operationServiceRef = new ServiceReference(
				"com.sap.me.productdefinition", "OperationConfigurationService");
		this.operationConfigurationServiceInterface = RunAsServiceLocator
				.getService(operationServiceRef,
						OperationConfigurationServiceInterface.class, user,
						site, null);
		ServiceReference sfcServiceRef = new ServiceReference(
				"com.sap.me.production", "SfcStateService");
		this.sfcStateServiceInterface = RunAsServiceLocator
				.getService(sfcServiceRef, SfcStateServiceInterface.class,
						user, site, null);
		ServiceReference processLotServiceRef = new ServiceReference(
				"com.sap.me.production", "ProcessLotService");
		this.processLotServiceInterface = RunAsServiceLocator.getService(
				processLotServiceRef, ProcessLotServiceInterface.class, user,
				site, null);
		commonMethods = new CommonMethods();
		this.site = CommonMethods.getSite();
		activityConfigService = Services.getService("com.sap.me.activity",
				"ActivityConfigurationService");
		// Initialize Material Browse list
		initializeData();
	}

	public String clear() {
		boolean isMaterial = this.disableMaterial;
		boolean isProductionLine = this.disableProductionLine;
		// clear bean caches
		FacesUtility.removeSessionMapValue("orderSequenceDataBean");
		// Clear message bar area
		this.message = null;
		setMessageBar(false, LSMessageType.ERROR);
		setMessageBar(false, LSMessageType.INFO);
		/*
		 * this.productionLine=null; this.material=null; this.productionLine="";
		 * this.material="";
		 */
		this.disableMaterial = isMaterial;
		this.disableProductionLine = isProductionLine;
		UIComponent tablePanel = findComponent(FacesUtility.getFacesContext()
				.getViewRoot(), "orderSequenceDataForm:displayPanel");
		if (tablePanel != null) {
			FacesUtility.addControlUpdate(tablePanel);
		}
		return null;
	}

	public void processEditCellChange(ValueChangeEvent event) {
		// HtmlDataTable objHtmlDataTable = (HtmlDataTable) event.getSource();
		// System.out.println(objHtmlDataTable.getRowIndex());
		String oldVal = (event.getOldValue() != null) ? event.getOldValue()
				.toString() : "";
		String newVal = (event.getNewValue() != null) ? event.getNewValue()
				.toString() : "";
		if (oldVal.equals(newVal))
			return;
		setSelectValue(true);
		// If you checked - unchecked check box in table we should clean message
		// bar area
		// this.message = null;
		// setMessageBar(false, LSMessageType.ERROR);
	}

	// Activity closed , window unload
	public void processWindowClosed() {
		FacesUtility.removeSessionMapValue("materialCustomDataBean");

	}

	public void setMessageBar(boolean render, LSMessageType messageType) {
		HtmlOutputText messageBarInfo = (HtmlOutputText) findComponent(FacesUtility
				.getFacesContext().getViewRoot(),
				"orderSequenceDataForm:messageBar");
		HtmlOutputText messageBarError = (HtmlOutputText) findComponent(FacesUtility
				.getFacesContext().getViewRoot(),
				"orderSequenceDataForm:messageBar2");
		if (messageType != null && messageType.equals(LSMessageType.ERROR)) {
			messageBarInfo.setRendered(false);
			messageBarError.setRendered(render);
			messageBarError.setStyle("font-size:10pt;font-family:Verdana;");
		}
		if (messageType != null && messageType.equals(LSMessageType.INFO)) {
			messageBarError.setRendered(false);
			messageBarInfo.setRendered(render);
			messageBarInfo.setStyle("font-size:10pt;font-family:Verdana;");
		}
		UIComponent fieldButtonPanel = findComponent(FacesUtility
				.getFacesContext().getViewRoot(),
				"orderSequenceDataForm:fieldButtonPanel");
		if (fieldButtonPanel != null) {
			FacesUtility.addControlUpdate(fieldButtonPanel);
		}
	}

	public String readMaterialData() throws NamingException {
		this.message = null;
		setMessageBar(false, LSMessageType.ERROR);
		setMessageBar(false, LSMessageType.INFO);
		String materialData = null;
		materialCustomDataList = new ArrayList<MaterialCustomDataItem>();
		Context ctx = new InitialContext();
		OrderSequenceDataInterface ordersequenceEjb = (OrderSequenceDataInterface) ctx
				.lookup("ejb:/appName=atos.com/apps~ear,jarName=atos.com~apps~ejb.jar, beanName=OrderSequenceDataBean, interfaceName=com.atos.ordersequenceEJB.OrderSequenceDataInterface");
		List<OrderSequenceData> UserDataList = ordersequenceEjb
				.readCustomTableData(this.productionLine, this.material,
						this.scheduleStartDate);
		if(this.scheduleStartDate!=null)
		{
		this.scheduleStartDate2 = this.scheduleStartDate;
		}
		for (OrderSequenceData orderSequenceData : UserDataList) {
			MaterialCustomDataItem materialCustomDataItem = new MaterialCustomDataItem();
			materialCustomDataItem.setHandle(orderSequenceData.getHandle());
			materialCustomDataItem.setItembo(orderSequenceData.getItem());
			materialCustomDataItem.setSfcbo(orderSequenceData.getSfc());
			materialCustomDataItem.setOrderbo(orderSequenceData.getOrder());
			materialCustomDataItem.setWorkcenterbo(orderSequenceData
					.getWorkcenter());
			materialCustomDataItem.setParent_orderbo(orderSequenceData
					.getParent_order());
			materialCustomDataItem.setPriority1(orderSequenceData
					.getPriority1());
			materialCustomDataItem.setPriorityBeforeUpdating(orderSequenceData
					.getPriority1());
			materialCustomDataItem.setStartDate(sqltoUtil(orderSequenceData.getPlannedstartdate()));
			materialCustomDataItem.setSpecial_int(orderSequenceData
					.getSpecial_int());
			materialCustomDataItem.setActive(orderSequenceData.getActive());
			materialCustomDataItem.setExport_import2(orderSequenceData
					.isExport_import2());
			materialCustomDataItem.setCreateby(orderSequenceData.getCreateby());
			materialCustomDataItem.setCreateon(orderSequenceData.getCreateon());
			materialCustomDataItem.setStatus(orderSequenceData.getStatus());
			materialCustomDataItem.setModel(orderSequenceData.getModel());
			materialCustomDataItem.setWheelbase(orderSequenceData.getWheelbase());
			materialCustomDataItem.setCountry(orderSequenceData.getCountry());
			materialCustomDataItem.setColor(orderSequenceData.getColor());
			materialCustomDataItem.setDrive(orderSequenceData.getDrive());
			materialCustomDataList.add(materialCustomDataItem);
			materialData = materialCustomDataList.toString();
			
		}

		UIComponent tablePanel = findComponent(FacesUtility.getFacesContext()
				.getViewRoot(), "orderSequenceDataForm:displayPanel");
		if (tablePanel != null) {
			FacesUtility.addControlUpdate(tablePanel);
		}

		if ("".equals(this.message)) {
			// refresh message bar
			setMessageBar(false, LSMessageType.INFO);
		}
		if (materialCustomDataList.size() == 0) {
			// Display INFO message on GUI
			message = FacesUtility
					.getLocaleSpecificText(" Your query returned no data .");
			setMessageBar(true, LSMessageType.ERROR);

		}

		return materialData;
	}

	public void updateOrderSequence(ActionEvent event)
			throws BusinessException, ParseException, NamingException {
		int selectCount = 0;
		OrderSequenceData orderSequenceData = new OrderSequenceData();
		for (MaterialCustomDataItem updating : this.materialCustomDataList) {
			if (updating.isSelect() == true) {
				selectCount++;
				orderSequenceData.setHandle(updating.getHandle());
				orderSequenceData.setItem(updating.getItembo());
				orderSequenceData.setSfc(updating.getSfcbo());
				orderSequenceData.setSfcBO(updating.getSfcBOfrmPPC());
				orderSequenceData.setWorkcenter(updating.getWorkcenterbo());
				orderSequenceData.setOrder(updating.getOrderbo());
				orderSequenceData.setParent_order(updating.getParent_orderbo());
				orderSequenceData.setPriority1(updating.getPriority1());
				orderSequenceData.setOlderPriority(updating
						.getPriorityBeforeUpdating());
				orderSequenceData.setExport_import(booleanToString(updating
						.isExport_import2()));
				orderSequenceData.setSpecial_int(updating.getSpecial_int());
				orderSequenceData.setPlannedstartdate(utilDatetoSqlDate(this.scheduleStartDate2));
				orderSequenceData.setCreateby(updating.getCreateby());
				java.sql.Date sqlDate = new java.sql.Date(updating
						.getCreateon().getTime());
				orderSequenceData.setCreateon(sqlDate);
				orderSequenceData.setUpdateby(this.userID);
				orderSequenceData.setUpdateon(utilDatetoSqlDate(new Date()));
				orderSequenceData.setActive(updating.getActive());
				orderSequenceData.setModel(updating.getModel());
				orderSequenceData.setWheelbase(updating.getWheelbase());
				orderSequenceData.setCountry(updating.getCountry());
				orderSequenceData.setColor(updating.getColor());
				orderSequenceData.setDrive(updating.getDrive());
				int Status = getStatusOrderSequence(updating.getSfcbo());
				orderSequenceData.setStatus(Status + 1);
				Context ctx = new InitialContext();
				OrderSequenceDataInterface ordersequenceEjb = (OrderSequenceDataInterface) ctx
						.lookup("ejb:/appName=atos.com/apps~ear,jarName=atos.com~apps~ejb.jar, beanName=OrderSequenceDataBean, interfaceName=com.atos.ordersequenceEJB.OrderSequenceDataInterface");
				if (this.scheduleStartDate2 != null) {
					if (this.scheduleStartDate2.before(getZeroTimeDate(systemDate))) 
					{
						message = FacesUtility
								.getLocaleSpecificText(" Given date to order sequencing in before today's date .");
						setMessageBar(true, LSMessageType.ERROR);
						break;
					} else {
						if (updating.getPriority1() != updating
								.getPriorityBeforeUpdating()) {
							String datafound = ordersequenceEjb.getSfcFromTable(updating.getPriority1(),updating.getWorkcenterbo(),utilDatetoSqlDate(this.scheduleStartDate2));
							if (datafound == null) {
								boolean updatedSuccess = false;
								ordersequenceEjb.updateData(orderSequenceData);
								updatedSuccess = true;
								if (updatedSuccess) {
									ordersequenceEjb.insertAfterUpdateData(
													orderSequenceData,
													utilDatetoSqlDate(this.scheduleStartDate2),
													booleanToString(updating
															.isExport_import2()));
									readMaterialData();
								}
							} else {
								message = FacesUtility
										.getLocaleSpecificText(" Given Priority is already exist for given planned start date and workcenter .");
								setMessageBar(true, LSMessageType.ERROR);
								break;
							}
						} else {
							ordersequenceEjb
									.updateDataExceptPriority(
											orderSequenceData,
											utilDatetoSqlDate(this.scheduleStartDate2),
											booleanToString(updating
													.isExport_import2()));
							readMaterialData();
						}
					}
				}
				message = FacesUtility
						.getLocaleSpecificText(" Order sequencing is successfully done .");
				setMessageBar(true, LSMessageType.INFO);
					
			}
			if (this.scheduleStartDate2 == null) {
				message = FacesUtility
						.getLocaleSpecificText(" Provide Planned start date to order sequencing .");
				setMessageBar(true, LSMessageType.ERROR);
			}
			if (selectCount == 0) {
				message = FacesUtility
						.getLocaleSpecificText(" Select row(s) to order sequencing .");
				setMessageBar(true, LSMessageType.ERROR);
			}
		}
		
		if ("".equals(this.message)) {
			// refresh message bar
			setMessageBar(false, LSMessageType.INFO);
		}
		
	}

	public static Date getZeroTimeDate(Date systemDate) {
		Date convertedDate = systemDate;
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(systemDate);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		convertedDate = calendar.getTime();
		return convertedDate;
	}

	public String datetoString(String format, Date date) {
		if (date != null) {
			DateFormat dateFormat = new SimpleDateFormat("MMM d, yyyy");
			String datestring = dateFormat.format(date);
			return datestring;
		} else {
			return null;
		}
	}

	// Conversion of Boolean to String
	public String booleanToString(boolean bool) {
		String boolString = Boolean.toString(bool);
		if (boolString.equals(true)) {
			boolString = "Yes";
		} else if (boolString.equals(false)) {
			boolString = "No";
		}
		return boolString;
	}

	public String sqltoUtil(java.sql.Date date) {
		if (date != null) {
			java.util.Date date1 = new java.util.Date(date.getTime());
			SimpleDateFormat sdf = new SimpleDateFormat("MMM d, yyyy");
			String formattedDate = sdf.format(date1);
			return formattedDate;
		} else {
			return null;
		}
	}

	public Date sqltoUtilDate(java.sql.Date date) {
		if (date != null) {
			java.util.Date date1 = new java.util.Date(date.getTime());
			return date1;
		} else {
			return null;
		}
	}

	public java.sql.Date utilDatetoSqlDate(java.util.Date sqlDate) {
		if (sqlDate != null) {
			java.sql.Date sqlDate1 = new java.sql.Date(sqlDate.getTime());
			return sqlDate1;
		}
		return null;
	}

	public void processAction(ActionEvent event)
			throws AbortProcessingException {
		// The object on which the Event initially occurred
		UICommandInputText currentBrowseComponent = (UICommandInputText) event
				.getSource();
		// getClientId() returns a client-side identifier for this component
		// example orderSequenceDataForm:materialCustomDataTable:0:j_id1
		String currentBrowseComponentId = currentBrowseComponent
				.getClientId(FacesContext.getCurrentInstance());
		FacesUtility.removeSessionMapValue("customDataBrowseBean");
		FacesUtility.setSessionMapValue(
				"customDataBrowseBean_currentBrowseComponentId",
				currentBrowseComponentId);
		// Java Script function in MaterialCustomDataView.jsp
		FacesUtility.addScriptCommand("window.customDataFieldBrowse();");
	}

	// Read all materials for current site
	public void initializeData() {

		try {
			setActivityRuleValues();
		} catch (BusinessException businessException) {
			// TODO Auto-generated catch block
			businessException.printStackTrace();
		}
		// setting rule values for material
		if (FacesUtility.getSessionMapValue("MATERIAL") != null) {
			this.disableMaterial = true;
			this.material = (String) FacesUtility
					.getSessionMapValue("MATERIAL");

		} else {
			this.disableMaterial = false;
			this.material = "";

		}
		// setting rule values for production line
		if (FacesUtility.getSessionMapValue("PRODUCTION_LINE") != null) {
			this.disableProductionLine = true;
			this.productionLine = (String) FacesUtility
					.getSessionMapValue("PRODUCTION_LINE");
		} else {
			this.disableProductionLine = false;
			this.productionLine = "";
		}
		// if either production line or material is present enable the other
		// buttons
		if (StringUtils.isNotBlank(this.productionLine)
				|| StringUtils.isNotBlank(this.material)) {
			this.disableBrowsePopup = false;
		} else {
			this.disableBrowsePopup = true;
		}
		// if material is not disabled find the data for popups
		if (!this.disableMaterial) {
			try {
				this.materialList = findAllMaterial(this.material);
			} catch (BusinessException e) {
				// Display INFO message on GUI
				message = FacesUtility
						.getLocaleSpecificText(" Business Exception while finding the materials . ");
				setMessageBar(true, LSMessageType.ERROR);
			}
		}
	}

	public boolean isSelected() {
		MaterialItem currentRow = getCurrentSelectedRowMaterailBrowseTable();
		if (currentRow != null) {
			return currentRow.getSelected().booleanValue();
		}
		return false;
	}

	public void setSelected(boolean selected) {
		MaterialItem currentRow = getCurrentSelectedRowMaterailBrowseTable();
		currentRow.setSelected(new Boolean(selected));
	}

	private MaterialItem getCurrentSelectedRowMaterailBrowseTable() {
		Map<String, Object> requestMap = FacesContext.getCurrentInstance()
				.getExternalContext().getRequestMap();
		MaterialItem currentRow = (MaterialItem) requestMap
				.get("materialBrowseVar");
		return currentRow;
	}

	public void rowSelected(ActionEvent event) {

		closeMaterialBrowse(event);
		// set value for input field
		MaterialItem selectedMaterial = getSelectedMaterial();
		if (selectedMaterial != null) {
			this.material = selectedMaterial.getMaterial();
		}
		if (StringUtils.isNotEmpty(this.material)
				|| StringUtils.isNotEmpty(this.productionLine)) {
			this.disableBrowsePopup = false;
		} else {
			this.disableBrowsePopup = true;
		}
	}

	public void rowSelectedProductionLine(ActionEvent event) {

		closeProductionLineBrowse(event);
		// set value for input field
		MaterialItem selectedMaterial = getSelectedProductionLine();
		if (selectedMaterial != null) {
			this.productionLine = selectedMaterial.getMaterial();
			// this.productionLine = selectedMaterial.getWorkcenter();
		}
		if (StringUtils.isNotEmpty(this.material)
				|| StringUtils.isNotEmpty(this.productionLine)) {
			this.disableBrowsePopup = false;
		} else {
			this.disableBrowsePopup = true;
		}
	}

	public void rowSelectedMaterial(ActionEvent event) {

		closeMaterialBrowse(event);
		// set value for input field
		MaterialItem selectedMaterial = getSelectedMaterial();
		if (selectedMaterial != null) {
			this.material = selectedMaterial.getMaterial();
		}
		if (StringUtils.isNotEmpty(this.material)) {
			this.disableBrowsePopup = false;
		} else {
			this.disableBrowsePopup = true;
		}
	}

	private MaterialItem getSelectedMaterial() {
		MaterialItem selectedMaterialItem = null;
		if (materialList != null) {
			for (int i = 0; i < this.materialList.size(); i++) {
				MaterialItem materialItem = materialList.get(i);
				if (materialItem != null && materialItem.getSelected()) {
					selectedMaterialItem = materialItem;
					break;
				}
			}
		}
		return selectedMaterialItem;
	}

	private MaterialItem getSelectedProductionLine() {
		MaterialItem selectedMaterialItem = null;
		if (productionLineList != null) {
			for (int i = 0; i < this.productionLineList.size(); i++) {
				MaterialItem materialItem = productionLineList.get(i);
				if (materialItem != null && materialItem.getSelected()) {
					selectedMaterialItem = materialItem;
					break;
				}

			}
		}
		return selectedMaterialItem;
	}

	public int getStatusOrderSequence(String sfc) {
		String qry = null;
		int statusfromTable = 0;
		Connection con = null;
		qry = " SELECT STATUS FROM Z_ORDERSEQUENCE WHERE SFC = ?";
		PreparedStatement ps = null;
		try {
			con = getConnection();
			ps = con.prepareStatement(qry);
			ps.setString(1, sfc);
			ResultSet rs = ps.executeQuery();
			if (rs != null) {
				while (rs.next()) {
					statusfromTable = rs.getInt("STATUS");
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
		return statusfromTable;
	}

	public void showMaterialBrowse(ActionEvent event) {
		try {
			this.materialList = findAllMaterial(this.material);
		} catch (BusinessException exp) {
			// Display INFO message on GUI
			message = FacesUtility
					.getLocaleSpecificText("Business Exception while finding the materials . ");
			setMessageBar(true, LSMessageType.ERROR);
		}

		this.materialBrowseRendered = true;
		this.productionLineBrowseRendered = false;
		if (StringUtils.isNotEmpty(this.material)
				|| StringUtils.isNotEmpty(this.productionLine)) {
			this.disableBrowsePopup = false;
		} else {
			this.disableBrowsePopup = true;
		}

	}

	public void showProductionLineBrowse(ActionEvent event) {
		this.materialBrowseRendered = false;
		this.productionLineBrowseRendered = true;
		try {
			// this.productionLineList =
			// findAllWorkCenters(this.productionLine);
			this.productionLineList = workcenterBrowse(this.productionLine);

		} catch (BusinessException businessException) {
			message = FacesUtility
					.getLocaleSpecificText("Business Exception while finding production lines");
			setMessageBar(true, LSMessageType.INFO);
		}

		if (StringUtils.isNotEmpty(this.material)
				|| StringUtils.isNotEmpty(this.productionLine)) {
			this.disableBrowsePopup = false;
		} else {
			this.disableBrowsePopup = true;
		}
	}

	public void activateOnProductionLineSelect(ActionEvent event) {
		if (StringUtils.isNotEmpty(this.material)
				|| StringUtils.isNotEmpty(this.productionLine)) {
			this.disableBrowsePopup = false;
		} else {
			this.disableBrowsePopup = true;
		}
	}

	public void closeMaterialBrowse(ActionEvent event) {

		this.materialBrowseRendered = false;
	}

	public void closeProductionLineBrowse(ActionEvent event) {

		this.productionLineBrowseRendered = false;
	}

	public void setSelectProductionLine(boolean selectProductionLine) {
		Map<String, Object> requestMap = FacesContext.getCurrentInstance()
				.getExternalContext().getRequestMap();
		MaterialItem currentRow = (MaterialItem) requestMap
				.get("productionLineBrowseVar");

		currentRow.setSelected(new Boolean(selectProductionLine));
	}

	public void setSelectValue(boolean checked) {
		Map<String, Object> requestMap = FacesContext.getCurrentInstance()
				.getExternalContext().getRequestMap();
		MaterialCustomDataItem currentRow = (MaterialCustomDataItem) requestMap
				.get("rows");
		currentRow.setSelect(checked);

	}

	public boolean getSelectProductionLine() {
		Map<String, Object> requestMap = FacesContext.getCurrentInstance()
				.getExternalContext().getRequestMap();
		MaterialItem currentRow = (MaterialItem) requestMap
				.get("productionLineBrowseVar");

		if (currentRow != null) {
			return currentRow.getSelected().booleanValue();
		}
		return false;
	}

	public void setSelectMaterial(boolean selectMaterial) {
		Map<String, Object> requestMap = FacesContext.getCurrentInstance()
				.getExternalContext().getRequestMap();
		MaterialItem currentRow = (MaterialItem) requestMap
				.get("materialBrowseVar");

		currentRow.setSelected(new Boolean(selectMaterial));
	}

	public boolean getSelectMaterial() {
		Map<String, Object> requestMap = FacesContext.getCurrentInstance()
				.getExternalContext().getRequestMap();
		MaterialItem currentRow = (MaterialItem) requestMap
				.get("materialBrowseVar");

		if (currentRow != null) {
			return currentRow.getSelected().booleanValue();
		}
		return false;
	}

	public void setActivityRuleValues() throws BusinessException {
		String activity = CommonMethods.getActivityID();
		if (activity != null) {
			FindActivityOptionsRequest optionsReq = new FindActivityOptionsRequest();
			optionsReq.setActivity(activity);
			List<ActivityOption> activityOptions = activityConfigService
					.findActivityOptions(optionsReq);
			String material = null;
			String productionLine = null;
			for (ActivityOption activityOption : activityOptions) {
				String optionName = activityOption.getExecUnitOption();
				String optionValue = activityOption.getSetting();
				if (optionName == null) {
					continue;
				}
				if (optionName.equals("MATERIAL")) {
					// this.material = optionValue;
					// this.disableMaterial = true;
					HttpSession httpSession = FacesUtility.getHttpSession();
					httpSession.setAttribute("MATERIAL", optionValue);
					material = optionValue;

				}
				if (optionName.equals("PRODUCTION_LINE")) {
					// if rule is present disable the inputtextbox
					// this.productionLine = optionValue;
					// this.disableProductionLine = true;
					HttpSession httpSession = FacesUtility.getHttpSession();
					httpSession.setAttribute("PRODUCTION_LINE", optionValue);
					productionLine = optionValue;

				}

			}
			if (material == null) {
				FacesUtility.getHttpSession().removeAttribute("MATERIAL");
			}
			if (productionLine == null) {
				FacesUtility.getHttpSession()
						.removeAttribute("PRODUCTION_LINE");
			}
		}

	}

	/**************** database code ************************/
	private final SystemBase dbBase = SystemBase
			.createSystemBase("jdbc/jts/wipPool");

	private Connection getConnection() {
		Connection con = null;

		con = dbBase.getDBConnection();

		return con;
	}
	/**************** database code ************************/

	public List<MaterialItem> workcenterBrowse(String productionLine)
			throws BusinessException {
		Connection con = null;
		String query = "select DISTINCT WORKCENTER from Z_ORDERSEQUENCE where WORKCENTER IS NOT NULL ";
		if (StringUtils.isNotBlank(productionLine)) {
			query = query + "  and WORKCENTER like '" + productionLine + "%'";
		}
		PreparedStatement ps = null;
		List<MaterialItem> user = new ArrayList<MaterialItem>();
		try {
			con = getConnection();
			ps = con.prepareStatement(query);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				MaterialItem downloadVO = new MaterialItem();
				downloadVO.setMaterial("" + rs.getString("WORKCENTER"));
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

	public List<MaterialItem> findAllWorkCenters(String productionLineTemp)
			throws BusinessException {
		List<WorkCenterBasicConfiguration> workCenterBasicConfigurations = workCenterConfigurationService
				.findAllWorkCenters();

		List<MaterialItem> productionLines = new ArrayList<MaterialItem>();
		for (WorkCenterBasicConfiguration basicConfiguration : workCenterBasicConfigurations) {

			if (StringUtils.isNotEmpty(productionLineTemp)) {

				if (StringUtils.startsWithIgnoreCase(basicConfiguration
						.getWorkCenter(), productionLineTemp)
						|| StringUtils.isEmpty(productionLineTemp)) {
					MaterialItem productionLine = new MaterialItem();
					productionLine.setMaterial(basicConfiguration
							.getWorkCenter());
					productionLines.add(productionLine);
				}
			} else {
				MaterialItem productionLine = new MaterialItem();
				productionLine.setMaterial(basicConfiguration.getWorkCenter());
				productionLines.add(productionLine);
			}

		}
		return productionLines;
	}

	public List<MaterialItem> findAllMaterial(String material)
			throws BusinessException {

		Connection con = null;
		String query = "select  DISTINCT ITEM from  ITEM WHERE ITEM_TYPE  in ('M','B') and ITEM IS NOT NULL";
		if (StringUtils.isNotBlank(material)) {
			query = query + "  and ITEM like '" + material + "%'";
		}
		PreparedStatement ps = null;
		List<MaterialItem> materials = new ArrayList<MaterialItem>();
		try {

			con = getConnection();
			ps = con.prepareStatement(query);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				MaterialItem productionLine = new MaterialItem();
				productionLine.setMaterial("" + rs.getString("ITEM"));
				materials.add(productionLine);
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
		return materials;
	}

	public Boolean getProductionLineBrowseRendered() {
		return this.productionLineBrowseRendered;
	}

	public void setProductionLineBrowseRendered(
			Boolean productionLineBrowseRendered) {
		this.productionLineBrowseRendered = productionLineBrowseRendered;
	}

	public String getProductionLine() {
		return productionLine;
	}

	public void setProductionLine(String productionLine) {
		this.productionLine = productionLine;
	}

	public String getPriority() {
		return priority;
	}

	public void setPriority(String priority) {
		this.priority = priority;
	}

	public Date getScheduleStartDate() {
		return scheduleStartDate;
	}

	public void setScheduleStartDate(Date scheduleStartDate) {
		this.scheduleStartDate = scheduleStartDate;
	}

	public Date getScheduleEndDate() {
		return scheduleEndDate;
	}

	public void setScheduleEndDate(Date scheduleEndDate) {
		this.scheduleEndDate = scheduleEndDate;
	}

	public String getSite() {
		return site;
	}

	public void setSite(String site) {
		this.site = site;
	}

	public Boolean getDisableBrowsePopup() {
		return disableBrowsePopup;
	}

	public void setDisableBrowsePopup(Boolean disableBrowsePopup) {
		this.disableBrowsePopup = disableBrowsePopup;
	}

	public boolean isSequencing() {
		return sequencing;
	}

	public void setSequencing(boolean sequencing) {
		this.sequencing = sequencing;
	}

	public boolean isReleasable() {
		return releasable;
	}

	public void setReleasable(boolean releasable) {
		this.releasable = releasable;
	}

	public void setMaterialCustomDataList(
			List<MaterialCustomDataItem> materialCustomDataList) {
		this.materialCustomDataList = materialCustomDataList;
	}

	public List<MaterialCustomDataItem> getMaterialCustomDataList() {
		return materialCustomDataList;
	}

	public String getMaterial() {

		return material;
	}

	public void setMaterial(String material) {
		this.material = material;
	}

	public Boolean getMaterialBrowseRendered() {

		return materialBrowseRendered;
	}

	public void setMaterialBrowseRendered(Boolean materialBrowseRendered) {
		this.materialBrowseRendered = materialBrowseRendered;
	}

	public Boolean getOrderReleasePopupRendered() {
		return orderReleasePopupRendered;
	}

	public void setOrderReleasePopupRendered(Boolean orderReleasePopupRendered) {
		this.orderReleasePopupRendered = orderReleasePopupRendered;
	}

	public String getMessage() {

		return message;
	}

	public void setMessage(String message) {

		this.message = message;
	}

	public List<MaterialItem> getMaterialList() {
		return this.materialList;
	}

	public List<MaterialItem> getProductionLineList() {
		return productionLineList;
	}

	public void setProductionLineList(List<MaterialItem> rcTypeList) {
		this.productionLineList = rcTypeList;
	}

	public TableConfigurator getTableConfigBean() {
		return tableConfigBean;
	}

	public void setTableConfigBean(TableConfigurator config) {
		this.tableConfigBean = config;

	}

	public Boolean getDisableMaterial() {
		return disableMaterial;
	}

	public void setDisableMaterial(Boolean disableMaterial) {
		this.disableMaterial = disableMaterial;
	}

	public Boolean getDisableProductionLine() {
		return disableProductionLine;
	}

	public void setDisableProductionLine(Boolean disableProductionLine) {
		this.disableProductionLine = disableProductionLine;
	}

	public Date getScheduleStartDate2() {
		return scheduleStartDate2;
	}

	public void setScheduleStartDate2(Date scheduleStartDate2) {
		this.scheduleStartDate2 = scheduleStartDate2;
	}

	public java.sql.Date getPlannedDate() {
		return plannedDate;
	}

	public void setPlannedDate(java.sql.Date plannedDate) {
		this.plannedDate = plannedDate;
	}

	private Date plannedStartDate;
	private java.sql.Date plannedDate;

	public Date getPlannedStartDate() {
		return plannedStartDate;
	}

	public void setPlannedStartDate(Date plannedStartDate) {
		this.plannedStartDate = plannedStartDate;
	}

	public Boolean getDisableStartDate() {
		return disableStartDate;
	}

	public void setDisableStartDate(Boolean disableStartDate) {
		this.disableStartDate = disableStartDate;
	}
}