package com.atos.orderboardvolvo;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
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
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import jxl.write.WriteException;

import org.apache.commons.lang3.StringUtils;

import com.sap.me.activity.ActivityConfigurationServiceInterface;
import com.sap.me.activity.ActivityOption;
import com.sap.me.activity.FindActivityOptionsRequest;
import com.sap.me.common.ObjectReference;
import com.sap.me.demand.BuildQtyReleasedQtyException;
import com.sap.me.demand.FindShopOrderByItemRequest;
import com.sap.me.demand.FindShopOrderByWorkCenterRequest;
import com.sap.me.demand.FindShopOrderResponse;
import com.sap.me.demand.ItemRouterException;
import com.sap.me.demand.OperationScheduleEndDateMustBeGreaterThanStartDateException;
import com.sap.me.demand.OperationStatusException;
import com.sap.me.demand.ReleaseShopOrderRequest;
import com.sap.me.demand.ReleaseShopOrderResponse;
import com.sap.me.demand.RepetitiveOrderDueException;
import com.sap.me.demand.RouterStatusException;
import com.sap.me.demand.SfcCountException;
import com.sap.me.demand.SfcPlanBuildQtyException;
import com.sap.me.demand.SfcPlanEntryIsEmptyException;
import com.sap.me.demand.SfcPlanReleasedQtyException;
import com.sap.me.demand.SfcPlanSerialNumberIsNotUniqueException;
import com.sap.me.demand.SfcPlanSfcIsNotUniqueException;
import com.sap.me.demand.SfcPlanShopOrderMaterialLotSizeIsNotOneException;
import com.sap.me.demand.SfcPlanUpdateException;
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
import com.sap.me.demand.ShopOrderRmaException;
import com.sap.me.demand.ShopOrderSchedulePlannedQuantityExceedsShopOrderBuildQuantityException;
import com.sap.me.demand.ShopOrderServiceInterface;
import com.sap.me.demand.ShopOrderStatus;
import com.sap.me.demand.ShopOrderStatusException;
import com.sap.me.demand.ShopOrderUpdateException;
import com.sap.me.demand.UsedSfcException;
import com.sap.me.demand.WorkCenterPermitException;
import com.sap.me.demand.WorkCenterRequiredException;
import com.sap.me.extension.Services;
import com.sap.me.frame.SystemBase;
import com.sap.me.frame.domain.BusinessException;
import com.sap.me.frame.service.CommonMethods;
import com.sap.me.labor.LaborChargeCodeBasicConfiguration;
import com.sap.me.labor.LaborChargeCodeConfigurationServiceInterface;
import com.sap.me.plant.WorkCenterBasicConfiguration;
import com.sap.me.plant.WorkCenterConfigurationServiceInterface;
import com.sap.me.plant.WorkCenterKeyData;
import com.sap.me.productdefinition.ItemBasicConfiguration;
import com.sap.me.productdefinition.ItemConfigurationServiceInterface;
import com.sap.me.productdefinition.OperationBasicConfiguration;
import com.sap.me.productdefinition.OperationConfigurationServiceInterface;
import com.sap.me.productdefinition.RouterBasicConfiguration;
import com.sap.me.productdefinition.RouterConfigurationServiceInterface;
import com.sap.me.production.ProcessLotBasicConfiguration;
import com.sap.me.production.ProcessLotServiceInterface;
import com.sap.me.production.SfcBasicData;
import com.sap.me.production.SfcStateServiceInterface;
import com.sap.me.production.podclient.BasePodPlugin;
import com.sap.me.security.RunAsServiceLocator;
import com.sap.me.wpmf.TableConfigurator;
import com.sap.me.wpmf.util.FacesUtility;
import com.sap.tc.ls.api.enumerations.LSMessageType;
import com.sap.tc.ls.internal.faces.component.UIMessageBar;
import com.sap.ui.faces.component.html.HtmlDataTable;
import com.sap.ui.faces.component.sap.UICommandInputText;
import com.visiprise.frame.configuration.ServiceReference;
import com.visiprise.globalization.util.DateTimeInterface;

public class OrderReleaseDataController extends BasePodPlugin implements
		ActionListener {

	private String material;
	private String employeeNo;

	public Integer getQuantityRelease() {
		return quantityRelease;
	}

	public void setQuantityRelease(Integer quantityRelease) {
		this.quantityRelease = quantityRelease;
	}

	private Integer quantityRelease;
	private String priority;
	private boolean selectPriority;
	private boolean selectProductionLine;
	private Date clockOnDate;
	private Date scheduleStartDate;
	private Date scheduleEndDate;
	private String version;
	private String productionLine;
	private Boolean materialBrowseRendered;
	private Boolean priorityBrowseRendered;
	private Boolean productionLineBrowseRendered;
	private Boolean quantityToBeReleasedRendered;
	private Boolean orderReleasePopupRendered;
	private Boolean disableBrowsePopup;
	private Boolean disableMaterial;
	private Boolean disableProductionLine;
	private List<MaterialItem> materialList = new ArrayList<MaterialItem>();
	private List<MaterialItem> priorityList = new ArrayList<MaterialItem>();
	private List<MaterialItem> productionLineList = new ArrayList<MaterialItem>();
	private List<OrderReleased> orderReleasedList = new ArrayList<OrderReleased>();
	private ShopOrderServiceInterface shoporderService;
	private WorkCenterConfigurationServiceInterface workCenterConfigurationService;
	private ItemConfigurationServiceInterface itemConfigurationServiceInterface;
	private LaborChargeCodeConfigurationServiceInterface laborChargeCodeConfigurationServiceInterface;
	private RouterConfigurationServiceInterface routerConfigurationServiceInterface;
	private OperationConfigurationServiceInterface operationConfigurationServiceInterface;
	private SfcStateServiceInterface sfcStateServiceInterface;
	private ProcessLotServiceInterface processLotServiceInterface;
	private ActivityConfigurationServiceInterface activityConfigService;
	private String message;
	private String site = CommonMethods.getSite();
	private List<MaterialCustomDataItem> materialCustomDataList;
	private TableConfigurator tableConfigBean = null;
	private CommonMethods commonMethods;
	private boolean sequencing;
	private boolean releasable;
	private static final String RELEASABLE_RULE = "RELEASABLE";
	private static final String YES = "Y";
	private static final String NO = "N";
	private String user=CommonMethods.getUserId();

	public OrderReleaseDataController() {
		ServiceReference shoporderServiceRef = new ServiceReference(
				"com.sap.me.demand", "ShopOrderService");
		shoporderService = RunAsServiceLocator.getService(shoporderServiceRef,
				ShopOrderServiceInterface.class, user, site, null);
		ServiceReference workCenterServiceRef = new ServiceReference(
				"com.sap.me.plant", "WorkCenterConfigurationService");
		workCenterConfigurationService = RunAsServiceLocator.getService(
				workCenterServiceRef,
				WorkCenterConfigurationServiceInterface.class, user, site,
				null);
		ServiceReference itemServiceRef = new ServiceReference(
				"com.sap.me.productdefinition", "ItemConfigurationService");
		itemConfigurationServiceInterface = RunAsServiceLocator.getService(
				itemServiceRef, ItemConfigurationServiceInterface.class,
				user, site, null);
		ServiceReference laborChargeCodeServiceRef = new ServiceReference(
				"com.sap.me.labor", "LaborChargeCodeConfigurationService");
		this.laborChargeCodeConfigurationServiceInterface = RunAsServiceLocator
				.getService(laborChargeCodeServiceRef,
						LaborChargeCodeConfigurationServiceInterface.class,
						user, site, null);
		ServiceReference routerServiceRef = new ServiceReference(
				"com.sap.me.productdefinition", "RouterConfigurationService");
		this.routerConfigurationServiceInterface = RunAsServiceLocator
				.getService(routerServiceRef,
						RouterConfigurationServiceInterface.class, user,
						site, null);
		ServiceReference operationServiceRef = new ServiceReference(
				"com.sap.me.productdefinition", "OperationConfigurationService");
		this.operationConfigurationServiceInterface = RunAsServiceLocator
				.getService(operationServiceRef,
						OperationConfigurationServiceInterface.class, user,
						site, null);
		ServiceReference sfcServiceRef = new ServiceReference(
				"com.sap.me.production", "SfcStateService");
		this.sfcStateServiceInterface = RunAsServiceLocator.getService(
				sfcServiceRef, SfcStateServiceInterface.class, user, site,
				null);
		ServiceReference processLotServiceRef = new ServiceReference(
				"com.sap.me.production", "ProcessLotService");
		this.processLotServiceInterface = RunAsServiceLocator.getService(
				processLotServiceRef, ProcessLotServiceInterface.class,
				user, site, null);
		commonMethods = new CommonMethods();
		this.site = CommonMethods.getSite();
		activityConfigService = Services.getService("com.sap.me.activity",
				"ActivityConfigurationService");
		// Initialize Material Browse list
		initializeData();
	}

	public void clear(ActionEvent event) 
	{
		
		boolean isMaterial = this.disableMaterial;
		boolean isProductionLine = this.disableProductionLine;
		// clear bean caches
		FacesUtility.removeSessionMapValue("orderReleaseDataBeanVolvo");
		// Clear message bar area
		this.message = null;
		setMessageBar(false, null);
		/*this.productionLine=null;
		this.material=null;
		this.productionLine="";
		this.material="";*/
		this.disableMaterial = isMaterial;
		this.disableProductionLine = isProductionLine;
		UIComponent tablePanel = findComponent(FacesUtility.getFacesContext()
				.getViewRoot(), "orderReleaseDataForm:displayPanel");		
		if (tablePanel != null) {
			FacesUtility.addControlUpdate(tablePanel);
		}
		
	}

	/**
	 * The value change event handler for editable cells that modify cell
	 * values. This keeps track of modified rows.
	 * 
	 * @param event
	 *            the value change event.
	 */
	public void processEditCellChange(ValueChangeEvent event) {
		HtmlDataTable objHtmlDataTable = (HtmlDataTable) event.getSource();
		System.out.println(objHtmlDataTable.getRowIndex());
		String oldVal = (event.getOldValue() != null) ? event.getOldValue()
				.toString() : "";
		String newVal = (event.getNewValue() != null) ? event.getNewValue()
				.toString() : "";
		if (oldVal.equals(newVal))
			return;
		// If you checked - unchecked check box in table we should clean message
		// bar area
		this.message = null;
		setMessageBar(false, LSMessageType.ERROR);
	}

	// Activity closed , window unload
	public void processWindowClosed() {
		FacesUtility.removeSessionMapValue("materialCustomDataBean");

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

	/**
	 * Method set info and error messages
	 * 
	 * @param render
	 * @param messageType
	 */

	public void setMessageBar(boolean render, LSMessageType messageType) {
		HtmlOutputText messageBar = (HtmlOutputText) findComponent(FacesUtility
				.getFacesContext().getViewRoot(),
				"orderReleaseDataForm:messageBar");
		messageBar.setRendered(render);
		messageBar.setStyle("font-size:10pt;");
		if(messageType!=null&&messageType.equals(LSMessageType.ERROR))
		{
			messageBar.setStyle("color:#FF1500;font-size:10pt;");
		}
		UIComponent fieldButtonPanel = findComponent(FacesUtility
				.getFacesContext().getViewRoot(),
				"orderReleaseDataForm:fieldButtonPanel");
		if (fieldButtonPanel != null) {
			FacesUtility.addControlUpdate(fieldButtonPanel);
		}
	}

	public void readMaterialCustomData() {
		materialCustomDataList = new ArrayList<MaterialCustomDataItem>();
		this.message = "";
		try {
			FindShopOrderResponse findShopOrderResponse = null;
			if (StringUtils.isNotEmpty(this.productionLine)) {
				findShopOrderResponse = shoporderService
						.findShopOrderByWorkCenter(new FindShopOrderByWorkCenterRequest(
								this.productionLine));
				if (findShopOrderResponse == null) {
					message = FacesUtility
							.getLocaleSpecificText("Shop Orders not found for Production Line : "
									+ this.productionLine);
					setMessageBar(true, LSMessageType.ERROR);
					return;
				}

			} else if (StringUtils.isNotEmpty(this.material)) {
				findShopOrderResponse = shoporderService
						.findShopOrderByItem(new FindShopOrderByItemRequest(
								this.material));
				if (findShopOrderResponse == null) {
					message = FacesUtility
							.getLocaleSpecificText("Shop Orders not found for Material : "
									+ this.material);
					setMessageBar(true, LSMessageType.ERROR);
					return;
				}
			} else if (StringUtils.isEmpty(this.material)
					&& StringUtils.isEmpty(this.productionLine)) {
				message = FacesUtility
						.getLocaleSpecificText("Select Material / Production Line  in order to retrieve Shop Orders"
								+ this.material);
				setMessageBar(true, LSMessageType.ERROR);
				return;
			}
			if (findShopOrderResponse == null) {
				message = FacesUtility
						.getLocaleSpecificText("Shop Orders not found "
								+ this.material);
				setMessageBar(true, LSMessageType.ERROR);
				return;
			}
			List<ShopOrderBasicConfiguration> shopOrderBasicConfigurations = findShopOrderResponse
					.getShopOrderList();
			for (ShopOrderBasicConfiguration shopOrderBasicConfiguration : shopOrderBasicConfigurations) {
				// checking search criterias
				if (StringUtils.isNotBlank(this.material)
						&& (!StringUtils.contains(shopOrderBasicConfiguration
								.getPlannedItemRef(), this.material))) {
					continue;
				}
				if (StringUtils.isNotBlank(this.productionLine)
						&& (!StringUtils
								.contains(shopOrderBasicConfiguration
										.getPlannedWorkCenterRef(),
										this.productionLine))) {
					continue;
				}
				if (StringUtils.isNotBlank(this.priority)
						&& (!this.priority.equals(shopOrderBasicConfiguration
								.getPriority().toString()))) {
					continue;
				}
				if (this.scheduleEndDate != null
						&& (!compareDates(this.scheduleEndDate,
								shopOrderBasicConfiguration
										.getPlannedCompletionDate()))) {
					continue;
				}
				if (this.scheduleStartDate != null
						&& (!compareDates(this.scheduleStartDate,
								shopOrderBasicConfiguration
										.getPlannedStartDate()))) {
					continue;
				}
				if (!isStatusReleasable(shopOrderBasicConfiguration.getStatus())) {
					continue;
				}

				MaterialCustomDataItem materialCustomDataItem = new MaterialCustomDataItem();
				// select chckbox enable / disable
				materialCustomDataItem
						.setDisable(!isQuantityReasable(shopOrderBasicConfiguration));
				materialCustomDataItem
						.setShopOrderRef(shopOrderBasicConfiguration
								.getShopOrderRef());
				materialCustomDataItem.setOrderno(""
						+ shopOrderBasicConfiguration.getShopOrder());
				if (shopOrderBasicConfiguration.getPlannedItemRef() != null) {
					ItemBasicConfiguration basicConfiguration = this.itemConfigurationServiceInterface
							.findItemConfigurationByRef(new ObjectReference(
									shopOrderBasicConfiguration
											.getPlannedItemRef()));
					materialCustomDataItem.setMaterial(basicConfiguration
							.getItem());
				} else
					materialCustomDataItem.setMaterial("--");

				if (shopOrderBasicConfiguration.getPlannedWorkCenterRef() != null) {

					WorkCenterKeyData workCenterKeyData = this.workCenterConfigurationService
							.findWorkCenterKeyDataByRef(new ObjectReference(
									shopOrderBasicConfiguration
											.getPlannedWorkCenterRef()));
					materialCustomDataItem.setWorkcenter(""
							+ workCenterKeyData.getWorkCenter());
				} else
					materialCustomDataItem.setWorkcenter("--");

				if (shopOrderBasicConfiguration.getQuantityToBuild() != null
						&& shopOrderBasicConfiguration.getQuantityReleased() != null) {
					materialCustomDataItem
							.setQtyAvailable(""
									+ (shopOrderBasicConfiguration
											.getQuantityToBuild()
											.subtract(shopOrderBasicConfiguration
													.getQuantityReleased()))
											.toString());
					materialCustomDataItem
							.setQuantityToBeReleased((shopOrderBasicConfiguration
									.getQuantityToBuild()
									.subtract(shopOrderBasicConfiguration
											.getQuantityReleased())).toString());
				} else
					materialCustomDataItem.setQtyAvailable("--");

				if (shopOrderBasicConfiguration.getQuantityToBuild() != null)
					materialCustomDataItem.setQtytobebuild(""
							+ shopOrderBasicConfiguration.getQuantityToBuild()
									.toString());
				else
					materialCustomDataItem.setQtytobebuild("--");

				if (shopOrderBasicConfiguration.getLaborChargeCodeRef() != null) {
					LaborChargeCodeBasicConfiguration basicConfiguration = this.laborChargeCodeConfigurationServiceInterface
							.findLaborChargeCodeByRef(new ObjectReference(
									shopOrderBasicConfiguration
											.getLaborChargeCodeRef()));
					materialCustomDataItem.setLaborchargecode(""
							+ basicConfiguration.getLaborChargeCode());

				}

				else
					materialCustomDataItem.setLaborchargecode("--");

				if (shopOrderBasicConfiguration.getPlannedStartDate() != null)
					materialCustomDataItem
							.setScheduledstartdate(shopOrderBasicConfiguration
									.getPlannedStartDate().toString());
				else
					materialCustomDataItem.setScheduledstartdate("--");
				if (shopOrderBasicConfiguration.getPlannedCompletionDate() != null)
					materialCustomDataItem
							.setScheduleenddate(shopOrderBasicConfiguration
									.getPlannedCompletionDate().toString());
				else
					materialCustomDataItem.setScheduleenddate("--");
				materialCustomDataItem.setPriority(shopOrderBasicConfiguration
						.getPriority().intValue());
				materialCustomDataList.add(materialCustomDataItem);
			}

		} catch (ShopOrderNotFoundException shopOrderNotFoundException) {
			message = FacesUtility
					.getLocaleSpecificText("shop orders not found");
			setMessageBar(true, LSMessageType.INFO);

		} catch (ShopOrderInputException shopOrderInputException) {
			message = FacesUtility
					.getLocaleSpecificText("Error while finding shop orders"
							+ shopOrderInputException.getMessage());
			setMessageBar(true, LSMessageType.ERROR);

		} catch (BusinessException businessException) {
			message = FacesUtility
					.getLocaleSpecificText("Error while finding shop orders"
							+ businessException.getMessage());
			setMessageBar(true, LSMessageType.ERROR);
		}

		if (materialCustomDataList.size() == 0) {
			// Display INFO message on GUI
			message = FacesUtility.getLocaleSpecificText("noRecords.MESSAGE");
			setMessageBar(true, LSMessageType.INFO);

		}
		// Make sure you add the table to the list of control updates so that
		// the new model value will be shown on the UI.
		UIComponent tablePanel = findComponent(FacesUtility.getFacesContext()
				.getViewRoot(), "orderReleaseDataForm:displayPanel");
		if (tablePanel != null) {
			FacesUtility.addControlUpdate(tablePanel);
		}

		if ("".equals(this.message)) {
			// refresh message bar
			setMessageBar(false, LSMessageType.INFO);
		}

	}

	private boolean isQuantityReasable(
			ShopOrderBasicConfiguration shopOrderBasicConfiguration) {
		if (shopOrderBasicConfiguration.getQuantityToBuild() != null
				&& shopOrderBasicConfiguration.getQuantityReleased() != null) {
			BigDecimal quantityUnreleased = shopOrderBasicConfiguration
					.getQuantityToBuild().subtract(
							shopOrderBasicConfiguration.getQuantityReleased());
			if (quantityUnreleased.intValue() > 0) {
				return true;
			} else {
				return false;
			}
		}
		return false;

	}

	private boolean isStatusReleasable(ShopOrderStatus shopOrderStatus)
			throws BusinessException {
		if (shopOrderStatus == null)
			return false;

		return ShopOrderStatus.Releasable.equals(shopOrderStatus);
	}

	private boolean compareDates(Date calenderDate,
			DateTimeInterface dateTimeInterface) {

		try {
			if (calenderDate != null && dateTimeInterface != null) {
				Calendar tCalendar = Calendar.getInstance();
				tCalendar.setTime(calenderDate);
				String scheduleDateStr = dateTimeInterface.getDay()
						+ "-"
						+ (dateTimeInterface.getCalendarMonth()
								.getMonthNumber() + 1) + "-"
						+ dateTimeInterface.getYear();
				SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
				Date scheduledDate = dateFormat.parse(scheduleDateStr);
				return calenderDate.equals(scheduledDate);
			}
		} catch (ParseException e) {
			return false;
		}

		return false;
	}

	// yyyy-MM-dd HH:mm:ss.SSS
	private static String convertDateToString(String format, Date date) {
		if (date == null || format == null)
			return null;
		SimpleDateFormat dateFormat = new SimpleDateFormat(format);
		return dateFormat.format(date);
	}

	// Action handle for browse clicking in cell
	public void processAction(ActionEvent event)
			throws AbortProcessingException {
		// The object on which the Event initially occurred
		UICommandInputText currentBrowseComponent = (UICommandInputText) event
				.getSource();
		// getClientId() returns a client-side identifier for this component
		// example orderReleaseDataForm:materialCustomDataTable:0:j_id1
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
		// setting rule values for SEQUENCING
		if (FacesUtility.getSessionMapValue(RELEASABLE_RULE) != null) {

			if (((String) FacesUtility.getSessionMapValue(RELEASABLE_RULE))
					.equals(YES)) {
				this.releasable = true;
			} else {
				this.releasable = false;
			}

		} else {
			this.releasable = false;

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
						.getLocaleSpecificText("Business Exception while finding the materials");
				setMessageBar(true, LSMessageType.ERROR);
			}
		}

		this.priorityList = findAllPriority();
		if (!this.disableProductionLine) {
			try {
				this.productionLineList = findAllWorkCenters(this.productionLine);
			} catch (BusinessException businessException) {
				// Display INFO message on GUI
				message = FacesUtility
						.getLocaleSpecificText("Business Exception while finding the workcenters");
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

	/**
	 * @nooverride
	 * @param selected
	 *            set the selection of the current row
	 */
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

	public void rowSelectedPriority(ActionEvent event) {

		closePriorityBrowse(event);
		// set value for input field
		MaterialItem selectedMaterial = getSelectedPriority();
		if (selectedMaterial != null) {
			this.priority = selectedMaterial.getMaterial();
		}
	}

	public void rowSelectedQuantityRelease(ActionEvent event) {

		closeQuantityelease(event);
		// set value for input field

	}

	public void rowSelectedProductionLine(ActionEvent event) {

		closeProductionLineBrowse(event);
		// set value for input field
		MaterialItem selectedMaterial = getSelectedProductionLine();
		if (selectedMaterial != null) {
			this.productionLine = selectedMaterial.getMaterial();
		}
		if (StringUtils.isNotEmpty(this.material)
				|| StringUtils.isNotEmpty(this.productionLine)) {
			this.disableBrowsePopup = false;
		} else {
			this.disableBrowsePopup = true;
		}
	}

	public void showQuantityPopup(ActionEvent event) {

		this.quantityToBeReleasedRendered = true;
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

	private MaterialItem getSelectedPriority() {
		MaterialItem selectedMaterialItem = null;
		if (priorityList != null) {
			for (int i = 0; i < this.priorityList.size(); i++) {
				MaterialItem materialItem = priorityList.get(i);
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

	public String getVersion() {

		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public Boolean getMaterialBrowseRendered() {

		return materialBrowseRendered;
	}

	public Boolean getPriorityBrowseRendered() {
		return priorityBrowseRendered;
	}

	public void setPriorityBrowseRendered(Boolean priorityBrowseRendered) {
		this.priorityBrowseRendered = priorityBrowseRendered;
	}

	public void setMaterialBrowseRendered(Boolean materialBrowseRendered) {
		this.materialBrowseRendered = materialBrowseRendered;
	}

	public Boolean getQuantityToBeReleasedRendered() {
		return quantityToBeReleasedRendered;
	}

	public void setQuantityToBeReleasedRendered(
			Boolean quantityToBeReleasedRendered) {
		this.quantityToBeReleasedRendered = quantityToBeReleasedRendered;
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

	public List<MaterialItem> getPriorityList() {
		return priorityList;
	}

	public void setPriorityList(List<MaterialItem> shopOrderList) {
		this.priorityList = shopOrderList;
	}

	public List<MaterialItem> getProductionLineList() {
		return productionLineList;
	}

	public void setProductionLineList(List<MaterialItem> rcTypeList) {
		this.productionLineList = rcTypeList;
	}

	/**
	 * displays the popup for Material List
	 * 
	 * @param event
	 */
	public void showMaterialBrowse(ActionEvent event) {
		try {
			this.materialList = findAllMaterial(this.material);
		} catch (BusinessException exp) {
			// Display INFO message on GUI
			message = FacesUtility
					.getLocaleSpecificText("Business Exception while finding the Materials");
			setMessageBar(true, LSMessageType.ERROR);
		}

		this.priorityBrowseRendered = false;
		this.materialBrowseRendered = true;
		this.productionLineBrowseRendered = false;
		this.quantityToBeReleasedRendered = false;
		if (StringUtils.isNotEmpty(this.material)
				|| StringUtils.isNotEmpty(this.productionLine)) {
			this.disableBrowsePopup = false;
		} else {
			this.disableBrowsePopup = true;
		}

	}

	/**
	 * displays the popup for Priority List
	 * 
	 * @param event
	 */
	public void showPriorityBrowse(ActionEvent event) {
		this.materialBrowseRendered = false;
		this.priorityBrowseRendered = true;
		this.productionLineBrowseRendered = false;
		this.quantityToBeReleasedRendered = false;
	}

	/**
	 * displays the popup for ProductionLine List
	 * 
	 * @param event
	 */
	public void showProductionLineBrowse(ActionEvent event) {
		this.materialBrowseRendered = false;
		this.priorityBrowseRendered = false;
		this.productionLineBrowseRendered = true;
		this.quantityToBeReleasedRendered = false;
		try {
			this.productionLineList = findAllWorkCenters(this.productionLine);
		} catch (BusinessException businessException) {
			message = FacesUtility
					.getLocaleSpecificText("Business Exception while finding production lines");
			setMessageBar(true, LSMessageType.ERROR);
		}

		if (StringUtils.isNotEmpty(this.material)
				|| StringUtils.isNotEmpty(this.productionLine)) {
			this.disableBrowsePopup = false;
		} else {
			this.disableBrowsePopup = true;
		}
	}

	/**
	 * enable/disable priority/schedule start & end date inputText ontabout
	 * event
	 * 
	 * @param event
	 */
	public void activateOnProductionLineSelect(ActionEvent event) {
		if (StringUtils.isNotEmpty(this.material)
				|| StringUtils.isNotEmpty(this.productionLine)) {
			this.disableBrowsePopup = false;
		} else {
			this.disableBrowsePopup = true;
		}
	}

	/**
	 * release selected orders and show release summary popup
	 */
	public void showOrderReleasePopup(ActionEvent event) {
		this.orderReleasedList = new ArrayList<OrderReleased>();
		// no of orders selected
		int selectCount = 0;
		// no of orders that are failed to release
		int noOfErrorOrders = 0;
		for (MaterialCustomDataItem orderToReleaseTemp : this.materialCustomDataList) {
			if (orderToReleaseTemp.isSelect()) {
				selectCount++;
				String quantityReleased = orderToReleaseTemp
						.getQuantityToBeReleased();
				ReleaseShopOrderResponse shopOrderResponse = releaseShopOrders(orderToReleaseTemp);
				if (shopOrderResponse != null) {
					orderToReleaseTemp.setReleased(true);
					OrderReleased orderReleased = new OrderReleased();
					orderReleased.setQuantity(quantityReleased);

					// PROCESS LOT
					try {
						ProcessLotBasicConfiguration basicConfiguration = this.processLotServiceInterface
								.findProcessLotByRef(new ObjectReference(
										shopOrderResponse.getProcessLotRef()));
						orderReleased.setProcessLoss(basicConfiguration
								.getProcessLot());
					} catch (BusinessException e) {
						// TODO Auto-generated catch block
						orderReleased.setProcessLoss(shopOrderResponse
								.getProcessLotRef());
					}
					// ROUTING
					try {
						RouterBasicConfiguration routerBasicConfiguration = this.routerConfigurationServiceInterface
								.findRouterConfigurationByRef(new ObjectReference(
										shopOrderResponse.getRouterRef()));
						orderReleased.setRouting(routerBasicConfiguration
								.getRouter());
					} catch (BusinessException e) {
						// TODO Auto-generated catch block
						orderReleased.setRouting(shopOrderResponse
								.getRouterRef());
					}
					// SHOP ORDER
					try {
						ShopOrderBasicConfiguration shopOrderBasicConfiguration = this.shoporderService
								.findShopOrder(new ObjectReference(
										shopOrderResponse.getShopOrderRef()));
						orderReleased.setShopOrder(shopOrderBasicConfiguration
								.getShopOrder());
					} catch (BusinessException e) {
						// TODO Auto-generated catch block
						orderReleased.setShopOrder(shopOrderResponse
								.getShopOrderRef());
					}
					// MATERIAL
					try {

						ItemBasicConfiguration basicConfiguration = this.itemConfigurationServiceInterface
								.findItemConfigurationByRef(new ObjectReference(
										shopOrderResponse.getItemRef()));
						orderReleased.setMaterial(basicConfiguration.getItem());
					} catch (BusinessException e) {
						// TODO Auto-generated catch block
						orderReleased.setMaterial(shopOrderResponse
								.getItemRef());
					}

					if (shopOrderResponse.getReleasedSfcList() != null
							&& shopOrderResponse.getReleasedSfcList().size() > 0) {
						// /Setting operation
						try {
							SfcBasicData sfcBasicData = this.sfcStateServiceInterface
									.findSfcDataByRef(new ObjectReference(
											shopOrderResponse
													.getReleasedSfcList()
													.get(0).getSfcRef()));
							orderReleased.setFirstSFC(sfcBasicData.getSfc());
							sfcBasicData = this.sfcStateServiceInterface
									.findSfcDataByRef(new ObjectReference(
											shopOrderResponse
													.getReleasedSfcList()
													.get(
															shopOrderResponse
																	.getReleasedSfcList()
																	.size() - 1)
													.getSfcRef()));
							orderReleased.setLastSFC(sfcBasicData.getSfc());
						} catch (BusinessException e) {
							// Last & First SFC
							orderReleased.setFirstSFC(shopOrderResponse
									.getReleasedSfcList().get(0).getSfcRef());
							orderReleased.setLastSFC(shopOrderResponse
									.getReleasedSfcList().get(
											shopOrderResponse
													.getReleasedSfcList()
													.size() - 1).getSfcRef());
						}

						// /Setting operation
						try {
							OperationBasicConfiguration operationBasicConfiguration = this.operationConfigurationServiceInterface
									.findOperationConfigurationByRef(new ObjectReference(
											shopOrderResponse
													.getReleasedSfcList()
													.get(0).getOperationRef()));
							orderReleased
									.setOperation(operationBasicConfiguration
											.getOperation());
						} catch (BusinessException e) {
							// TODO Auto-generated catch block
							orderReleased.setOperation(shopOrderResponse
									.getReleasedSfcList().get(0)
									.getOperationRef());
						}

					}

					this.orderReleasedList.add(orderReleased);
				} else {
					noOfErrorOrders++;
				}
			}
		}
		if (!this.orderReleasedList.isEmpty()) {
			// when orders are selected to release.
			this.materialBrowseRendered = false;
			this.priorityBrowseRendered = false;
			this.productionLineBrowseRendered = false;
			this.quantityToBeReleasedRendered = false;

			this.orderReleasePopupRendered = true;
			if (noOfErrorOrders == 0) {
				message = "Orders are released successfully";
				UIMessageBar messageBar = (UIMessageBar) findComponent(
						FacesUtility.getFacesContext().getViewRoot(),
						"orderReleaseDataForm:messageBar");
				messageBar.setRendered(true);
				messageBar.setType(LSMessageType.INFO);

			} else {
				message = "Error in some orders while releasing";
				UIMessageBar messageBar = (UIMessageBar) findComponent(
						FacesUtility.getFacesContext().getViewRoot(),
						"orderReleaseDataForm:messageBar");
				messageBar.setRendered(true);
				messageBar.setType(LSMessageType.ERROR);

			}

		} else if (selectCount == 0) {
			// when no order selected and release is called
			message = FacesUtility
					.getLocaleSpecificText("Select order to release");
			setMessageBar(true, LSMessageType.INFO);
		} else if (selectCount > 0) {
			// when all orders that are selected result in error
			message = FacesUtility
					.getLocaleSpecificText("Error while releasing orders");
			setMessageBar(true, LSMessageType.ERROR);
		}

	}

	public void closeMaterialBrowse(ActionEvent event) {

		this.materialBrowseRendered = false;
	}

	public void closePriorityBrowse(ActionEvent event) {

		this.priorityBrowseRendered = false;
	}

	public void closeQuantityelease(ActionEvent event) {

		this.quantityToBeReleasedRendered = false;
	}

	public void closeProductionLineBrowse(ActionEvent event) {

		this.productionLineBrowseRendered = false;
	}

	public void saveOrders(ActionEvent event) throws BusinessException {

		int selectedOrders = 0;
		for (MaterialCustomDataItem orderToReleaseTemp : this.materialCustomDataList) {
			if (orderToReleaseTemp.isSelect()) {
				selectedOrders++;
				String errorMsg = "Errors:";
				boolean isError = false;
				if (orderToReleaseTemp.getPriority() == 0) {
					errorMsg += " Priority can not be blank";
					isError = true;
				} else if (orderToReleaseTemp.getPriority() < 1
						|| orderToReleaseTemp.getPriority() > 999) {
					errorMsg += " Value 'Priority' invalid; reason: Value must be a number between 1 and 999 ";
					isError = true;
				}
				if (isError) {
					orderToReleaseTemp.setErrorPriority(isError);
					orderToReleaseTemp.setErrorMessagePriority(errorMsg);
					continue;
				}
				ShopOrderFullConfiguration shopOrderFullConfiguration = shoporderService
						.readShopOrder(new ObjectReference(orderToReleaseTemp
								.getShopOrderRef()));
				int priority = orderToReleaseTemp.getPriority();
				shopOrderFullConfiguration
						.setPriority(new BigDecimal(priority));
				shopOrderFullConfiguration.setQuantityToBuild(new BigDecimal(
						orderToReleaseTemp.getQtyAvailable()));
				updateOrder(shopOrderFullConfiguration);
				orderToReleaseTemp.setSelect(false);

			}

		}

		if (selectedOrders != 0) {
			Collections.sort(this.materialCustomDataList,
					new Comparator<MaterialCustomDataItem>() {

						public int compare(MaterialCustomDataItem o1,
								MaterialCustomDataItem o2) {
							Integer priority1 = Integer.valueOf(o1
									.getPriority());
							Integer priority2 = Integer.valueOf(o2
									.getPriority());
							return priority1 - priority2;
						}
					});
			message = "Sequence is saved. ";
			setMessageBar(false, LSMessageType.INFO);
		} else {
			message = "Select orders for sequencing";
			setMessageBar(true, LSMessageType.ERROR);

		}

		UIComponent tablePanel = findComponent(FacesUtility.getFacesContext()
				.getViewRoot(), "orderReleaseDataForm:displayPanel");
		if (tablePanel != null) {
			// Adds UIComponent to the list of controls that are re-rendered in
			// the request
			FacesUtility.addControlUpdate(tablePanel);
		}
	}

	public void executeExportScript(ActionEvent event) {
		FacesUtility.addScriptCommand("window.customDataFieldBrowse();");
		WriteExcel writeExcel = new WriteExcel();
		try {
			writeExcel.write(this.materialCustomDataList);
		} catch (WriteException e) {
			message = "WriteException while creating excel " + e.getMessage();
			setMessageBar(true, LSMessageType.ERROR);
			e.printStackTrace();
		} catch (IOException e) {
			message = "IOException while creating excel " + e.getMessage();
			setMessageBar(true, LSMessageType.ERROR);
			e.printStackTrace();
		}
	}

	public void exportOrders(ActionEvent event) {

		FileInputStream excelInputStream = null;
		OutputStream responseOutputStream = null;
		try {
			// Get the FacesContext
	        FacesContext facesContext = FacesContext.getCurrentInstance();
	         
	        // Get HTTP response
	        HttpServletResponse response = (HttpServletResponse) facesContext.getExternalContext().getResponse();
	         
	        // Set response headers
	        response.reset();   // Reset the response in the first place
	        response.setHeader("Content-Disposition", "Attachment;Filename=\"shopOrderSequence.xls\"");  // Set only the content type
	         
	        // Open response output stream
	        responseOutputStream = response.getOutputStream();
	         
	        // Read PDF contents
	        File file = new File("C:\\myFolder\\shopOrderSequence.xls");
	        excelInputStream = new FileInputStream(file);
	        
	         
	        // Read PDF contents and write them to the output
	        byte[] bytesBuffer = new byte[2048];
	        int bytesRead;
	        while ((bytesRead = excelInputStream.read(bytesBuffer)) > 0) {
	            responseOutputStream.write(bytesBuffer, 0, bytesRead);
	        }
	         
	        // Make sure that everything is out
	        responseOutputStream.flush();
	          
	       
	         
	        

//			HttpServletResponse response = (HttpServletResponse) FacesContext
//					.getCurrentInstance().getExternalContext().getResponse();
//			HttpServletRequest request = (HttpServletRequest) FacesContext
//					.getCurrentInstance().getExternalContext().getRequest();
//			response.setContentType("application/vnd.ms-excel");
//			response.setHeader("Content-Disposition",
//					"attachment;filename=shopOrderSequence.xls");
//			response
//					.setHeader("Refresh", "3; url = " + request.getRequestURL());
//			File file = new File("C:\\myFolder\\shopOrderSequence.xls");
//			fileIn = new FileInputStream(file);
//			out = response.getOutputStream();
//
//			byte[] outputByte = new byte[4096];
//			// copy binary contect to output stream
//			while (fileIn.read(outputByte, 0, 4096) != -1) {
//				out.write(outputByte, 0, 4096);
//			}
//			FacesContext.getCurrentInstance().responseComplete();

		} catch (IOException e) {
			message = "IOException while creating excel " + e.getMessage();
			setMessageBar(true, LSMessageType.ERROR);
			e.printStackTrace();
		} finally {

			try {
				excelInputStream.close();
				 // Close both streams
		        responseOutputStream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		FacesUtility.addScriptCommand("alert('hi...');");
	}

	private void updateOrder(ShopOrderFullConfiguration orderFullConfiguration) {
		try {
			shoporderService.updateShopOrder(orderFullConfiguration);
		} catch (ShopOrderInputException e) {
			message = e.getMessage();
			e.printStackTrace();
			setMessageBar(true, LSMessageType.ERROR);
		} catch (BuildQtyReleasedQtyException e) {
			message = e.getMessage();
			e.printStackTrace();
			setMessageBar(true, LSMessageType.ERROR);
		} catch (SfcPlanSfcIsNotUniqueException e) {
			message = e.getMessage();
			e.printStackTrace();
			setMessageBar(true, LSMessageType.ERROR);
		} catch (SfcPlanSerialNumberIsNotUniqueException e) {
			message = e.getMessage();
			e.printStackTrace();
			setMessageBar(true, LSMessageType.ERROR);
		} catch (SfcPlanEntryIsEmptyException e) {
			message = e.getMessage();
			e.printStackTrace();
			setMessageBar(true, LSMessageType.ERROR);
		} catch (SfcPlanShopOrderMaterialLotSizeIsNotOneException e) {
			message = e.getMessage();
			e.printStackTrace();
			setMessageBar(true, LSMessageType.ERROR);
		} catch (SfcPlanBuildQtyException e) {
			message = e.getMessage();
			e.printStackTrace();
			setMessageBar(true, LSMessageType.ERROR);
		} catch (SfcPlanReleasedQtyException e) {
			message = e.getMessage();
			e.printStackTrace();
			setMessageBar(true, LSMessageType.ERROR);
		} catch (SfcPlanUpdateException e) {
			message = e.getMessage();
			e.printStackTrace();
			setMessageBar(true, LSMessageType.ERROR);
		} catch (ShopOrderSchedulePlannedQuantityExceedsShopOrderBuildQuantityException e) {
			message = e.getMessage();
			e.printStackTrace();
			setMessageBar(true, LSMessageType.ERROR);
		} catch (OperationScheduleEndDateMustBeGreaterThanStartDateException e) {
			message = e.getMessage();
			e.printStackTrace();
			setMessageBar(true, LSMessageType.ERROR);
		} catch (BusinessException e) {
			message = e.getMessage();
			e.printStackTrace();
			setMessageBar(true, LSMessageType.ERROR);
		}
	}

	public void closeOrderReleasePopup(ActionEvent event) {

		this.orderReleasePopupRendered = false;
		// Make sure you add the table to the list of control updates so that
		// the new model value will be shown on the UI.
		UIComponent tablePanel = findComponent(FacesUtility.getFacesContext()
				.getViewRoot(), "orderReleaseDataForm:displayPanel");
		if (tablePanel != null) {
			FacesUtility.addControlUpdate(tablePanel);
		}
		// updating message bar
		UIComponent fieldButtonPanel = findComponent(FacesUtility
				.getFacesContext().getViewRoot(),
				"orderReleaseDataForm:fieldButtonPanel");
		if (fieldButtonPanel != null) {
			FacesUtility.addControlUpdate(fieldButtonPanel);
		}

		// readMaterialCustomData();
	}

	public boolean getSelectPriority() {
		Map<String, Object> requestMap = FacesContext.getCurrentInstance()
				.getExternalContext().getRequestMap();
		MaterialItem currentRow = (MaterialItem) requestMap
				.get("priorityBrowseVar");

		if (currentRow != null) {
			return currentRow.getSelected().booleanValue();
		}
		return false;
	}

	public void setSelectPriority(boolean selectPriority) {
		Map<String, Object> requestMap = FacesContext.getCurrentInstance()
				.getExternalContext().getRequestMap();
		MaterialItem currentRow = (MaterialItem) requestMap
				.get("priorityBrowseVar");

		currentRow.setSelected(new Boolean(selectPriority));
	}

	public void setSelectProductionLine(boolean selectProductionLine) {
		Map<String, Object> requestMap = FacesContext.getCurrentInstance()
				.getExternalContext().getRequestMap();
		MaterialItem currentRow = (MaterialItem) requestMap
				.get("productionLineBrowseVar");

		currentRow.setSelected(new Boolean(selectProductionLine));
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

	public String getEmployeeNo() {
		return employeeNo;
	}

	public void setEmployeeNo(String employeeNo) {
		this.employeeNo = employeeNo;
	}

	public String getPriority() {
		return priority;
	}

	public void setPriority(String priority) {
		this.priority = priority;
	}

	public Date getClockOnDate() {
		return clockOnDate;
	}

	public void setClockOnDate(Date clockOnDate) {
		this.clockOnDate = clockOnDate;
	}

	public List<OrderReleased> getOrderReleasedList() {
		return orderReleasedList;
	}

	public void setOrderReleasedList(List<OrderReleased> orderReleasedList) {
		this.orderReleasedList = orderReleasedList;
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

	/**
	 * Release selected order Set the error message and flag if error while
	 * releasing
	 * 
	 * @param orderToRelease
	 * @return
	 */
	private ReleaseShopOrderResponse releaseShopOrders(
			MaterialCustomDataItem orderToRelease) {
		ReleaseShopOrderRequest releaseorderReq = new ReleaseShopOrderRequest();
		releaseorderReq.setShopOrderRef(orderToRelease.getShopOrderRef());
		BigDecimal quantityToBereleased = null;
		BigDecimal quantityAvailable = null;

		try {
			quantityAvailable = new BigDecimal(orderToRelease.getQtyAvailable());

			quantityToBereleased = new BigDecimal(orderToRelease
					.getQuantityToBeReleased());
			// validation quantity less than avaialable
			if (quantityAvailable.compareTo(quantityToBereleased) == -1) {
				orderToRelease.setError(true);
				orderToRelease
						.setErrorMessage("Quantity to be released is greater than quantity available");
				return null;
			}
			// check for quantity = 0
			if (quantityToBereleased.compareTo(new BigDecimal(0)) == 0) {
				orderToRelease.setError(true);
				orderToRelease
						.setErrorMessage("Quantity should be greater than 0");
				return null;
			}
			// check for quantity = decimal value
			if (!StringUtils
					.isNumeric(orderToRelease.getQuantityToBeReleased())) {
				orderToRelease.setError(true);
				orderToRelease
						.setErrorMessage("Only numeric values are allowed for quantity");
				return null;
			}
		} catch (NumberFormatException formatException) {
			orderToRelease.setError(true);
			orderToRelease
					.setErrorMessage("Quantity to be released is not proper");
		}
		releaseorderReq.setQuantityToRelease(quantityToBereleased);
		try {
			ReleaseShopOrderResponse releaseorderResp = shoporderService
					.releaseShopOrder(releaseorderReq);
			orderToRelease.setQtyAvailable(quantityAvailable.subtract(
					quantityToBereleased).toString());
			orderToRelease.setQuantityToBeReleased(quantityAvailable.subtract(
					quantityToBereleased).toString());
			orderToRelease.setSelect(false);
			if ((quantityAvailable.subtract(quantityToBereleased))
					.equals(new BigDecimal(0))) {
				orderToRelease.setDisable(true);
			}
			orderToRelease.setError(false);
			orderToRelease.setErrorMessage("");
			return releaseorderResp;

		} catch (ShopOrderNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		} catch (ShopOrderItemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		} catch (ShopOrderInputException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ShopOrderStatusException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		} catch (ShopOrderQuantityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		} catch (RepetitiveOrderDueException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ShopOrderRmaException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		} catch (ShopOrderUpdateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ShopOrderRecursionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		} catch (ShopOrderItemStatusException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		} catch (ShopOrderItemNewException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		} catch (ShopOrderItemTypeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		} catch (ShopOrderBomStatusException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		} catch (ShopOrderBomNewException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		} catch (WorkCenterRequiredException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (WorkCenterPermitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		} catch (ItemRouterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		} catch (RouterStatusException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		} catch (OperationStatusException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		} catch (SfcCountException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		} catch (UsedSfcException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		} catch (BusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		}

		return null;
	}

	/**
	 * this method reads the activity rule value and sets in session for further
	 * use
	 * 
	 * @throws BusinessException
	 */
	public void setActivityRuleValues() throws BusinessException {
		String activity = CommonMethods.getActivityID();
		if (activity != null) {
			FindActivityOptionsRequest optionsReq = new FindActivityOptionsRequest();
			optionsReq.setActivity(activity);
			List<ActivityOption> activityOptions = activityConfigService
					.findActivityOptions(optionsReq);
			String material = null;
			String sequence = null;
			String releasable = null;
			String productiuonLine = null;
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
				if (optionName.equals(RELEASABLE_RULE)) {
					// if rule is present disable the inputtextbox
					// this.productionLine = optionValue;
					// this.disableProductionLine = true;
					HttpSession httpSession = FacesUtility.getHttpSession();
					httpSession.setAttribute(RELEASABLE_RULE, optionValue);
					releasable = optionValue;

				}

			}
			if (material == null) {
				FacesUtility.getHttpSession().removeAttribute("MATERIAL");
			}
			if (productiuonLine == null) {
				FacesUtility.getHttpSession()
						.removeAttribute("PRODUCTION_LINE");
			}
			if (releasable == null) {
				FacesUtility.getHttpSession().removeAttribute(RELEASABLE_RULE);
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

	/**
	 * fetch work centres for production line select box
	 * 
	 * @param productionLineTemp
	 * @return
	 * @throws BusinessException
	 */
	public List<MaterialItem> findAllWorkCenters(String productionLineTemp)
			throws BusinessException {
		// finding all workcenters using service
		List<WorkCenterBasicConfiguration> workCenterBasicConfigurations = workCenterConfigurationService
				.findAllWorkCenters();
		List<MaterialItem> productionLines = new ArrayList<MaterialItem>();
		for (WorkCenterBasicConfiguration basicConfiguration : workCenterBasicConfigurations) {
			// if some initials given in browse commandinputtext show only those
			// work centers starting with given initials else show all
			// e.g if productionLineTemp = 'ABC' then show all workcenters
			// starting with 'ABC' (ABCWorkceneter1,ABCWorkceneter2 ...)
			if (StringUtils.isNotEmpty(productionLineTemp)) {
				if (StringUtils.startsWith(basicConfiguration.getWorkCenter(),
						productionLineTemp)
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

	/**
	 * Fetch the material for material select box ( only manufactured /
	 * manufactured & purchased)
	 * 
	 * @param material
	 * @return
	 * @throws BusinessException
	 */
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

	public List<MaterialItem> findAllPriority() {
		Connection con = null;
		String query = "select  DISTINCT PRIORITY from  SHOP_ORDER WHERE PRIORITY IS NOT NULL";
		PreparedStatement ps = null;
		List<MaterialItem> priorities = new ArrayList<MaterialItem>();
		try {

			con = getConnection();
			ps = con.prepareStatement(query);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				MaterialItem productionLine = new MaterialItem();
				productionLine.setMaterial("" + rs.getInt("PRIORITY"));
				priorities.add(productionLine);
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
		return priorities;
	}

}
