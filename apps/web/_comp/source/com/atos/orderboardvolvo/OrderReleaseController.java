package com.atos.orderboardvolvo;

import java.math.BigDecimal;
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
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;

import com.sap.me.activity.ActivityConfigurationServiceInterface;
import com.sap.me.activity.ActivityOption;
import com.sap.me.activity.FindActivityOptionsRequest;
import com.sap.me.common.AttributeValue;
import com.sap.me.common.ObjectReference;
import com.sap.me.demand.FindShopOrderByItemRequest;
import com.sap.me.demand.FindShopOrderResponse;
import com.sap.me.demand.ItemRouterException;
import com.sap.me.demand.OperationStatusException;
import com.sap.me.demand.ReleaseShopOrderRequest;
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
import com.sap.me.demand.ShopOrderPlan;
import com.sap.me.demand.ShopOrderQuantityException;
import com.sap.me.demand.ShopOrderRecursionException;
import com.sap.me.demand.ShopOrderRmaException;
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
import com.sap.me.labor.LaborChargeCodeConfigurationServiceInterface;
import com.sap.me.productdefinition.ItemBOHandle;
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
import com.sap.ui.faces.component.sap.UICommandInputText;
import com.visiprise.frame.configuration.ServiceReference;
import com.visiprise.globalization.util.DateTimeInterface;

public class  OrderReleaseController extends BasePodPlugin implements
		ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String material;
	private String employeeNo;
	private SubAssemblyOrderController subAsseblyOrder ;
	private Integer quantityRelease;	
	private Date clockOnDate;
	private Date scheduleStartDate;
	private Date scheduleEndDate;
	private String version;
	private Boolean materialBrowseRendered;
	private Boolean quantityToBeReleasedRendered;
	private Boolean orderReleasePopupRendered;
	private Boolean disableBrowsePopup;
	private Boolean disableMaterial;
	private List<MaterialItem> materialList = new ArrayList<MaterialItem>();
	private List<MaterialItem> priorityList = new ArrayList<MaterialItem>();
	private List<OrderReleased> orderReleasedList = new ArrayList<OrderReleased>();
	private ShopOrderServiceInterface shoporderService;
	private ItemConfigurationServiceInterface itemConfigurationServiceInterface;
	private RouterConfigurationServiceInterface routerConfigurationServiceInterface;
	private OperationConfigurationServiceInterface operationConfigurationServiceInterface;
	private SfcStateServiceInterface sfcStateServiceInterface;
	private ProcessLotServiceInterface processLotServiceInterface;
	private ActivityConfigurationServiceInterface activityConfigService;
	private String message;
	private String site = CommonMethods.getSite();
	private List<ShopOrderlCustomDataItem> materialCustomDataList;
	private TableConfigurator tableConfigBean = null;
	private CommonMethods commonMethods;
	private boolean sequencing;
	private boolean releasable;	
	private static final String CHILD_ORDERS = "CHILD_ORDERS";
	private static final String CHILD_ORDERS_SEPERATOR = ",";
	private static final String SHOP_ORD_BO = "ShopOrderBO:";
	private static final String YES="Y";
	private static final String NO="N";
	private static final String COMPLTED="COMPLTED";
	private static final String INPROGRESS="INPROGRESS";
	private static final String NOTSTARTED="NOTSTARTED";
	private String user=CommonMethods.getUserId();
	private boolean disableProductionLine;

	public boolean isDisableProductionLine() {
		return disableProductionLine;
	}
	public void setDisableProductionLine(boolean disableProductionLine) {
		this.disableProductionLine = disableProductionLine;
	}
	public OrderReleaseController() {
		ServiceReference shoporderServiceRef = new ServiceReference(
				"com.sap.me.demand", "ShopOrderService");
		shoporderService = RunAsServiceLocator.getService(shoporderServiceRef,
				ShopOrderServiceInterface.class, user, site, null);
		ServiceReference itemServiceRef = new ServiceReference(
				"com.sap.me.productdefinition", "ItemConfigurationService");
		itemConfigurationServiceInterface = RunAsServiceLocator.getService(
				itemServiceRef, ItemConfigurationServiceInterface.class,
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
	public Integer getQuantityRelease() {
		return quantityRelease;
	}

	public void setQuantityRelease(Integer quantityRelease) {
		this.quantityRelease = quantityRelease;
	}
	
	public SubAssemblyOrderController getSubAsseblyOrder() {
		return subAsseblyOrder;
	}
	public void setSubAsseblyOrder(SubAssemblyOrderController subAsseblyOrder) {
		this.subAsseblyOrder = subAsseblyOrder;
	}
//	public void clear(ActionEvent event) 
//	{
//		
//		boolean isMaterial = this.disableMaterial;
//		boolean isProductionLine = this.disableProductionLine;
//		// clear bean caches
//		FacesUtility.removeSessionMapValue("orderReleaseBeanVolvo");
//		// Clear message bar area
//		this.message = null;
//		setMessageBar(false, null);
//		/*this.productionLine=null;
//		this.material=null;
//		this.productionLine="";
//		this.material="";*/
//		this.disableMaterial = isMaterial;
//		this.disableProductionLine = isProductionLine;
//		UIComponent tablePanel = findComponent(FacesUtility.getFacesContext()
//				.getViewRoot(), "orderReleaseForm:displayPanel");
//		if (tablePanel != null) {
//			FacesUtility.addControlUpdate(tablePanel);
//		}
		
		
		public void clear(ActionEvent event) 
		{
			
			boolean isMaterial = this.disableMaterial;
			boolean isProductionLine = this.disableProductionLine;
			// clear bean caches
			FacesUtility.removeSessionMapValue("orderReleaseBeanVolvo");
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
					.getViewRoot(), "orderReleaseForm:displayPanel");
			if (tablePanel != null) {
				FacesUtility.addControlUpdate(tablePanel);
			}
			
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

	

	/**
	 * Method set info and error messages
	 * 
	 * @param render
	 * @param messageType
	 */


	
	public void setMessageBar(boolean render, LSMessageType messageType) {
		HtmlOutputText messageBar = (HtmlOutputText) findComponent(FacesUtility
				.getFacesContext().getViewRoot(),
				"orderReleaseForm:messageBar");
		messageBar.setRendered(render);
		messageBar.setStyle("font-size:10pt;");
		if(messageType!=null&&messageType.equals(LSMessageType.ERROR))
		{
			messageBar.setStyle("color:#FF1500;font-size:10pt;");
		}
		UIComponent fieldButtonPanel = findComponent(FacesUtility
				.getFacesContext().getViewRoot(),
				"orderReleaseForm:panel1");
		if (fieldButtonPanel != null) {
			FacesUtility.addControlUpdate(fieldButtonPanel);
		}
	}

	public void readMaterialCustomData() {
		materialCustomDataList = new ArrayList<ShopOrderlCustomDataItem>();
		this.message = "";
		try {
			FindShopOrderResponse findShopOrderResponse = null;
			if (StringUtils.isNotEmpty(this.material)) {
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
			} else if (StringUtils.isEmpty(this.material)) {
				message = FacesUtility
						.getLocaleSpecificText("Select Material   in order to retrieve Shop Orders"
								+ this.material);
				setMessageBar(true, LSMessageType.ERROR);
				return;
			}
			if (findShopOrderResponse == null) {
				message = FacesUtility
						.getLocaleSpecificText("Shop Orders not found "
								+ this.material);
				setMessageBar(true, LSMessageType.INFO);
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

				ShopOrderlCustomDataItem materialCustomDataItem = new ShopOrderlCustomDataItem();
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
				materialCustomDataItem
				.setPriority(shopOrderBasicConfiguration.getPriority().intValue());
				//CHASIS START & END
				ShopOrderFullConfiguration fullConfiguration=shoporderService.readShopOrder(new ObjectReference(shopOrderBasicConfiguration.getShopOrderRef()));
				if(fullConfiguration.getShopOrderPlanList() !=null && fullConfiguration.getShopOrderPlanList().size()>0 ){
					materialCustomDataItem.setChasisStart(fullConfiguration.getShopOrderPlanList().get(0).getSerialNumber());
					materialCustomDataItem.setChasisEnd(fullConfiguration.getShopOrderPlanList().get(fullConfiguration.getShopOrderPlanList().size()-1).getSerialNumber());
				}
				//fetching customdata
				List<AttributeValue> customData = fullConfiguration.getCustomData();
				String childShopOrders="";
				for(AttributeValue value:customData){
					if(CHILD_ORDERS.equals(value.getAttribute())){
						childShopOrders=value.getValue();
					}
					
				}
				//getting child order status and iformation
				List<SubAsemblyCustomDataItem> childShopOrdconfigurations=getChildOrders(childShopOrders,materialCustomDataItem);
				materialCustomDataItem.setSubAsemblyCustomDataItems(childShopOrdconfigurations);
				materialCustomDataItem.setDisable(true);
				materialCustomDataList.add(materialCustomDataItem);
			}

		} catch (ShopOrderNotFoundException shopOrderNotFoundException) {
			message = FacesUtility
					.getLocaleSpecificText("shop orders not found");
			setMessageBar(true, LSMessageType.ERROR);

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
			setMessageBar(true, LSMessageType.ERROR);

		}else{
			Collections.sort(this.materialCustomDataList, new Comparator<ShopOrderlCustomDataItem>(){

				  public int compare(ShopOrderlCustomDataItem o1, ShopOrderlCustomDataItem o2)
				  {
					 Integer priority1=Integer.valueOf(o1.getPriority());
					 Integer priority2=Integer.valueOf(o2.getPriority());
				     return priority1-priority2;
				  }
				}); 
			this.materialCustomDataList.get(0).setDisable(false);
			this.materialCustomDataList.get(0).setSelect(true);
		}
		// Make sure you add the table to the list of control updates so that
		// the new model value will be shown on the UI.
		UIComponent tablePanel = findComponent(FacesUtility.getFacesContext()
				.getViewRoot(), "orderReleaseForm:displayPanel");
		if (tablePanel != null) {
			FacesUtility.addControlUpdate(tablePanel);
		}

		if ("".equals(this.message)) {
			// refresh message bar
			setMessageBar(false, LSMessageType.INFO);
		}

	}
	private List<SubAsemblyCustomDataItem> getChildOrders(String childOrders, ShopOrderlCustomDataItem shopOrderlCustomDataItem)throws BusinessException{
		int completed=0;
		int notStarted=0;
		
		List<SubAsemblyCustomDataItem> subAssCustomDataItems = new ArrayList<SubAsemblyCustomDataItem>();
		if(childOrders == null || StringUtils.isBlank(childOrders)){
			return subAssCustomDataItems;
		}
		//split with separator
		String[] childOrdersArr=childOrders.split(CHILD_ORDERS_SEPERATOR);
		for(String childOrd:childOrdersArr){
			String shopOrdRef=SHOP_ORD_BO+CommonMethods.getSite()+","+childOrd;
			ShopOrderFullConfiguration fullConfigurationSubAss=this.shoporderService.readShopOrder(new ObjectReference(shopOrdRef));
			SubAsemblyCustomDataItem customDataItem = new SubAsemblyCustomDataItem();
			//Quantity Available
			if (fullConfigurationSubAss.getQuantityToBuild() != null
					&& fullConfigurationSubAss.getQuantityReleased() != null) {
			customDataItem.setQtyAvailable(""
					+ (fullConfigurationSubAss
							.getQuantityToBuild()
							.subtract(fullConfigurationSubAss
									.getQuantityReleased()))
							.toString());
			}else{
				customDataItem.setQtyAvailable("--");
			}
			//Quantity Built
			if (fullConfigurationSubAss.getQuantityToBuild() != null)
				customDataItem.setQtytobebuild(""
						+ fullConfigurationSubAss.getQuantityToBuild()
								.toString());
			else
				customDataItem.setQtytobebuild("--");
			//Priority
			if(fullConfigurationSubAss.getPriority()!=null){
				customDataItem.setPriority(fullConfigurationSubAss.getPriority().intValue());
			}
			//Order No
			customDataItem.setOrderno(fullConfigurationSubAss.getShopOrder());
			customDataItem.setShopOrderRef(fullConfigurationSubAss.getShopOrderRef());
			ItemBOHandle handle= new ItemBOHandle(fullConfigurationSubAss.getPlannedItemRef());
			customDataItem.setMaterial(handle.getItem());
			List<ShopOrderPlan> shopOrderPlans=fullConfigurationSubAss.getShopOrderPlanList();
			if(shopOrderPlans !=null && shopOrderPlans.size()>0){
				//Chasis Start			
				customDataItem.setChasisStart(shopOrderPlans.get(0).getSerialNumber());
				//Chasis End
				customDataItem.setChasisEnd(shopOrderPlans.get(shopOrderPlans.size()-1).getSerialNumber());
			}
			
			
			
			
			if(ShopOrderStatus.Releasable.equals(fullConfigurationSubAss.getStatus())){
				notStarted++;
				//sub assembly status
				customDataItem.setSubassStatus(NOTSTARTED);
				///com/biometdev/icons/select.png
				customDataItem.setButtonStyle("margin: 0 auto; width: 15px; height: 15px;background-color: Red;");
			}else if(ShopOrderStatus.Closed.equals(fullConfigurationSubAss.getStatus())||ShopOrderStatus.Done.equals(fullConfigurationSubAss.getStatus())){
				completed++;
				//sub assembly status
				customDataItem.setSubassStatus(COMPLTED);
				customDataItem.setButtonStyle("margin: 0 auto; width: 15px; height: 15px;background-color: Green;");
			}else{
				customDataItem.setSubassStatus(INPROGRESS);
				customDataItem.setButtonStyle("margin: 0 auto; width: 15px; height:15px;background-color: Yellow;");
			}
			subAssCustomDataItems.add(customDataItem);
		}
		/**
		 * All releasable status ---> Not Started-->Green
		   All Done/ Closed status ----> Completed-->Red;
		   else ---------------------> In Progress---->Yellow;
		 */
		if(completed == childOrdersArr.length){
			shopOrderlCustomDataItem.setSubassStatus(COMPLTED);
			shopOrderlCustomDataItem.setButtonStyle("/com/atos/icons/completed.png");
		}else if(notStarted == childOrdersArr.length){
			shopOrderlCustomDataItem.setSubassStatus(NOTSTARTED);
			shopOrderlCustomDataItem.setButtonStyle("/com/atos/icons/notstarted.png");
		}else{
			shopOrderlCustomDataItem.setSubassStatus(INPROGRESS);
			shopOrderlCustomDataItem.setButtonStyle("/com/atos/icons/inprogress.png");
		}
		return subAssCustomDataItems;
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

	
	// Action handle for browse clicking in cell
	public void processAction(ActionEvent event)
			throws AbortProcessingException {
		// The object on which the Event initially occurred
		UICommandInputText currentBrowseComponent = (UICommandInputText) event
				.getSource();
		// getClientId() returns a client-side identifier for this component
		// example orderReleaseForm:materialCustomDataTable:0:j_id1
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
		if (FacesUtility.getSessionMapValue("MATERIAL_FOR_DASHBOARD") != null) {
			this.disableMaterial = true;
			this.material = (String) FacesUtility
					.getSessionMapValue("MATERIAL_FOR_DASHBOARD");

		} else {
			this.disableMaterial = false;
			this.material = "";

		}
				
		//if either production line or material is present enable the other buttons
		if (StringUtils.isNotBlank(this.material)) {
			this.disableBrowsePopup = false;
		}else{
			this.disableBrowsePopup=true;
		}
		//if material is not disabled find the data for popups
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
	//method execute after selection of material from material list
	public void rowSelected(ActionEvent event) {

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

	

	public void rowSelectedQuantityRelease(ActionEvent event) {

		closeQuantityelease(event);
		// set value for input field

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
			List<ShopOrderlCustomDataItem> materialCustomDataList) {
		this.materialCustomDataList = materialCustomDataList;
	}

	public List<ShopOrderlCustomDataItem> getMaterialCustomDataList() {
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

		this.materialBrowseRendered = true;
		this.quantityToBeReleasedRendered = false;
		if (StringUtils.isNotEmpty(this.material)) {
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
		for (ShopOrderlCustomDataItem orderToReleaseTemp : this.materialCustomDataList) {
			if (orderToReleaseTemp.isSelect()) {
				selectCount++;
				String quantityReleased = orderToReleaseTemp
						.getQuantityToBeReleased();
				ReleaseShopOrderResponse shopOrderResponse =releaseShopOrders(orderToReleaseTemp);
				List<String> volvoSfcs=null;
				try {
					volvoSfcs=new VolvoSfcGeneration().generateSForVolvo(shopOrderResponse.getReleasedSfcList(), orderToReleaseTemp.getShopOrderRef());
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
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

					if (volvoSfcs != null
							&& volvoSfcs.size() > 0) {
						// /Setting operation							
							orderReleased.setFirstSFC(volvoSfcs.get(0));
							orderReleased.setFirstSFC(volvoSfcs.get(volvoSfcs.size()-1));
						
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
			this.quantityToBeReleasedRendered = false;

			this.orderReleasePopupRendered = true;
			if (noOfErrorOrders == 0) {
				message = "Orders are released successfully";
				UIMessageBar messageBar = (UIMessageBar) findComponent(
						FacesUtility.getFacesContext().getViewRoot(),
						"orderReleaseForm:messageBar");
				messageBar.setRendered(true);
				messageBar.setType(LSMessageType.INFO);

			} else {
				message = "Error in some orders while releasing";
				UIMessageBar messageBar = (UIMessageBar) findComponent(
						FacesUtility.getFacesContext().getViewRoot(),
						"orderReleaseForm:messageBar");
				messageBar.setRendered(true);
				messageBar.setType(LSMessageType.ERROR);

			}

		} else if (selectCount == 0) {
			// when no order selected and release is called
			message = FacesUtility
					.getLocaleSpecificText("Select order to release");
			setMessageBar(true, LSMessageType.ERROR);
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

	

	public void closeQuantityelease(ActionEvent event) {

		this.quantityToBeReleasedRendered = false;
	}

	
	// Action handle for browse clicking in cell
	public void openSubaasmblyWindow(ActionEvent event ) {
		String orderNo = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("order_id").toString();
		ShopOrderlCustomDataItem selectedShopOrderlCustomDataItem=null;
		for(ShopOrderlCustomDataItem customDataItem:this.materialCustomDataList){
			if(orderNo.equals(customDataItem.getOrderno())){
				selectedShopOrderlCustomDataItem=customDataItem;
				break;
			}
		}
		if(this.subAsseblyOrder ==null){
			this.subAsseblyOrder = new SubAssemblyOrderController();
		}
		this.subAsseblyOrder.setParentShopOrder(orderNo);
		this.subAsseblyOrder.setMaterial(this.material);
		this.subAsseblyOrder.setSite(this.site);
		this.subAsseblyOrder.setScheduleStartDate(this.scheduleStartDate==null?"--":this.scheduleStartDate.toString());
		this.subAsseblyOrder.setScheduleEndDate(this.scheduleEndDate==null?"--":this.scheduleEndDate.toString());
		this.subAsseblyOrder.setSubAsemblyCustomDataItemList(selectedShopOrderlCustomDataItem.getSubAsemblyCustomDataItems());
		// Java Script function in MaterialCustomDataView.jsp
		FacesUtility.addScriptCommand("window.customDataFieldBrowse();");	
		
	}
	
	
	
	public void closeOrderReleasePopup(ActionEvent event) {

		this.orderReleasePopupRendered = false;
		// Make sure you add the table to the list of control updates so that
		// the new model value will be shown on the UI.
		UIComponent tablePanel = findComponent(FacesUtility.getFacesContext()
				.getViewRoot(), "orderReleaseForm:displayPanel");
		if (tablePanel != null) {
			FacesUtility.addControlUpdate(tablePanel);
		}
		// updating message bar
		UIComponent fieldButtonPanel = findComponent(FacesUtility
				.getFacesContext().getViewRoot(),
				"orderReleaseForm:panel1");
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

	

	

	public String getEmployeeNo() {
		return employeeNo;
	}

	public void setEmployeeNo(String employeeNo) {
		this.employeeNo = employeeNo;
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
			ShopOrderlCustomDataItem orderToRelease) {
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

		}catch(Exception exp){
			// TODO Auto-generated catch block
			exp.printStackTrace();
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
				if (optionName.equals("MATERIAL_FOR_DASHBOARD")) {
					// this.material = optionValue;
					// this.disableMaterial = true;
					HttpSession httpSession = FacesUtility.getHttpSession();
					httpSession.setAttribute("MATERIAL_FOR_DASHBOARD", optionValue);
					material = optionValue;

				}
				

			}
			if (material == null) {
				FacesUtility.getHttpSession().removeAttribute("MATERIAL_FOR_DASHBOARD");
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
