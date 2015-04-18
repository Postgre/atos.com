package com.atos.orderboardvolvo;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.component.html.HtmlOutputText;
import javax.faces.event.ActionEvent;

import org.apache.commons.lang3.StringUtils;

import sun.swing.StringUIClientPropertyKey;

import com.sap.me.common.AttributeValue;
import com.sap.me.common.CustomValue;
import com.sap.me.common.ObjectReference;
import com.sap.me.demand.BuildQtyReleasedQtyException;
import com.sap.me.demand.CreateShopOrderRequest;
import com.sap.me.demand.CustomerException;
import com.sap.me.demand.CustomerOrderException;
import com.sap.me.demand.FindShopOrderByKeyFieldsRequest;
import com.sap.me.demand.InvalidColumnValueException;
import com.sap.me.demand.OperationScheduleEndDateMustBeGreaterThanStartDateException;
import com.sap.me.demand.RecordNotFoundException;
import com.sap.me.demand.SfcPlanBuildQtyException;
import com.sap.me.demand.SfcPlanEntryIsEmptyException;
import com.sap.me.demand.SfcPlanReleasedQtyException;
import com.sap.me.demand.SfcPlanSerialNumberIsNotUniqueException;
import com.sap.me.demand.SfcPlanSfcIsNotUniqueException;
import com.sap.me.demand.SfcPlanShopOrderMaterialLotSizeIsNotOneException;
import com.sap.me.demand.SfcPlanUpdateException;
import com.sap.me.demand.ShopOrderBasicConfiguration;
import com.sap.me.demand.ShopOrderFullConfiguration;
import com.sap.me.demand.ShopOrderInputException;
import com.sap.me.demand.ShopOrderItemQtyException;
import com.sap.me.demand.ShopOrderItemTypeException;
import com.sap.me.demand.ShopOrderPlan;
import com.sap.me.demand.ShopOrderSchedulePlannedQuantityExceedsShopOrderBuildQuantityException;
import com.sap.me.demand.ShopOrderServiceInterface;
import com.sap.me.demand.ShopOrderStatus;
import com.sap.me.demand.ShopOrderType;
import com.sap.me.demand.ShopOrderUsedException;
import com.sap.me.demand.SiteNonProductionException;
import com.sap.me.demand.StartBeforeEndException;
import com.sap.me.demand.ValueMissingException;
import com.sap.me.demand.WorkCenterPermitException;
import com.sap.me.frame.SystemBase;
import com.sap.me.frame.domain.BusinessException;
import com.sap.me.frame.service.CommonMethods;
import com.sap.me.productdefinition.BOMComponentRequest;
import com.sap.me.productdefinition.BOMConfigurationServiceInterface;
import com.sap.me.productdefinition.BomComponentProductionConfiguration;
import com.sap.me.productdefinition.ItemConfigurationServiceInterface;
import com.sap.me.productdefinition.ItemFullConfiguration;
import com.sap.me.productdefinition.domain.ItemType;
import com.sap.me.production.podclient.BasePodPlugin;
import com.sap.me.security.RunAsServiceLocator;
import com.sap.me.wpmf.util.FacesUtility;
import com.sap.tc.ls.api.enumerations.LSMessageType;
import com.sap.tc.ls.internal.faces.component.UIMessageBar;
import com.visiprise.frame.configuration.ServiceReference;
import com.visiprise.globalization.DateGlobalizationServiceInterface;
import com.visiprise.globalization.GlobalizationService;

public class OrderCreationDataController extends BasePodPlugin {

	private List<MaterialCustomDataItem> materialCustomDataList;
	private Boolean materialBrowseRendered;
	private List<MaterialItem> materialList = new ArrayList<MaterialItem>();
	private List<ShopOrderDownloadVO> shopOrderList = new ArrayList<ShopOrderDownloadVO>();
	private List<BOMCustomDataItem> bomCustomDataItems = new ArrayList<BOMCustomDataItem>();
	// message may contain info / error messages after action
	private String message;
	private String material;
	private String shopOrderDownloaded;
	private String materialDownloaded;
	private int builtQuantityDownloaded;
	private String materialToBeSearched;
	private Integer builtQuantity;
	private Integer serialStart;
	private Integer serialEnd;
	private Date scheduleStartDate;
	private Date scheduleEndDate;
	private Date plannedStartDate;
	private Date plannedEndDate;
	private String site;
	private String shopOrder;
	private boolean materialEnable;
	private boolean shopOrderEnable;
	private String selectedPanelId;
	private boolean renderBomSaveButton;
	private boolean renderConfirmationPopup;
	private boolean createOrderForBomComp;
	private boolean renderShopOrderBrowse;	
	private ShopOrderServiceInterface shoporderService;
	private ShopOrderFullConfiguration currshopOrderFullConfiguration = null;	
	private DateGlobalizationServiceInterface dateGlobalizationServiceInterface;
	private BOMConfigurationServiceInterface bomConfigurationServiceInterface;
	private ItemConfigurationServiceInterface itemConfigurationServiceInterface;
	private String confirmationMessage;
	private static final String LEAD_TIME="LEAD_TIME";
	private static final String CREATE_ORDER="CREATE_ORDER";
	private static final String YES="YES";
	private static final String PARENT_ID="PARENT_ID";
	private String user=CommonMethods.getUserId();
	// constants
	static final int PRIORITY = 500;

	public OrderCreationDataController() {

		// Initialize Material Browse list
		initializeData();
	}

	public ShopOrderServiceInterface getShoporderService() {
		return shoporderService;
	}

	public void setShoporderService(ShopOrderServiceInterface shoporderService) {
		this.shoporderService = shoporderService;
	}

	public void initializeData() {
		this.shopOrderEnable = true;
		this.materialEnable = true;
		this.plannedStartDate = new Date();
		this.plannedEndDate = new Date();
		materialCustomDataList = new ArrayList<MaterialCustomDataItem>();
		this.site = CommonMethods.getSite();
		ServiceReference shoporderServiceRef = new ServiceReference(
				"com.sap.me.demand", "ShopOrderService");
		shoporderService = RunAsServiceLocator.getService(shoporderServiceRef,
				ShopOrderServiceInterface.class, user, site, null);
		dateGlobalizationServiceInterface = (DateGlobalizationServiceInterface) GlobalizationService
				.getInvariantService("com.visiprise.globalization.DateGlobalizationService");
		ServiceReference bomConfigurationServiceRef = new ServiceReference(
				"com.sap.me.productdefinition", "BOMConfigurationService");
		bomConfigurationServiceInterface = RunAsServiceLocator.getService(
				bomConfigurationServiceRef,
				BOMConfigurationServiceInterface.class, user, CommonMethods
						.getSite(), null);
		ServiceReference itemConfigurationServiceRef = new ServiceReference(
				"com.sap.me.productdefinition", "ItemConfigurationService");
		itemConfigurationServiceInterface = RunAsServiceLocator.getService(
				itemConfigurationServiceRef,
				ItemConfigurationServiceInterface.class, user, CommonMethods
						.getSite(), null);
		this.selectedPanelId = "orderCreationDataForm:orderCreation";
		this.renderBomSaveButton = false;
		try {
			this.materialList = findAllMaterial("",
					this.site);
		} catch (BusinessException e) {
			
			e.printStackTrace();
		}

	}

	/**
	 * displays the popup for Material List
	 * 
	 * @param event
	 */
	public void showMaterialBrowse(ActionEvent event) {
		try {
			this.materialList = findAllMaterial(this.material, this.site);
		} catch (BusinessException exp) {
			// Display INFO message on GUI
			message = FacesUtility
					.getLocaleSpecificText("Business Exception while finding the Materials");
			setMessageBar(true, LSMessageType.ERROR);
		}

		this.materialBrowseRendered = true;

	}
	public void closeConfirmationPopup(ActionEvent event) {

		this.renderConfirmationPopup = false;
		// Make sure you add the table to the list of control updates so that
		// the new model value will be shown on the UI.
		UIComponent tablePanel = findComponent(FacesUtility.getFacesContext()
				.getViewRoot(), "orderCreationDataForm:materialCustomDataTable");
		if (tablePanel != null) {
			FacesUtility.addControlUpdate(tablePanel);
		}
		// updating message bar
		UIComponent fieldButtonPanel = findComponent(FacesUtility
				.getFacesContext().getViewRoot(),
				"orderCreationDataForm:fieldButtonPanel");
		if (fieldButtonPanel != null) {
			FacesUtility.addControlUpdate(fieldButtonPanel);
		}

		// readMaterialCustomData();
	}
	public void showModalPopupPanel(ActionEvent event) {

		this.renderConfirmationPopup = true;
		// Make sure you add the table to the list of control updates so that
		// the new model value will be shown on the UI.
		
	}
	public void confirmYes(ActionEvent event) {		
		try {
			if(this.bomCustomDataItems != null){				
				MaterialItem materialItem=getSelectedMaterial();
				this.bomCustomDataItems = readBOMForMaterial(materialItem.getVersion());
			}
			
		} catch (BusinessException e) {
			// TODO Auto-generated catch block
			message = e.getMessage();
			setMessageBar(true, LSMessageType.ERROR);
		}
		this.createOrderForBomComp=true;
		this.selectedPanelId = "orderCreationDataForm:bom";
		closeConfirmationPopup(event);

		// readMaterialCustomData();
	}
	public void confirmNo(ActionEvent event) {
		this.bomCustomDataItems=null;
		try {
			saveShopOrderCustomData(event);
		} catch (BusinessException e) {
			// TODO Auto-generated catch block
			message = e.getMessage();
			setMessageBar(true, LSMessageType.ERROR);
		}
		this.createOrderForBomComp=false;
		this.selectedPanelId = "orderCreationDataForm:orderCreation";
		closeConfirmationPopup(event);

		
	}

	/**
	 * displays the popup for Material List
	 * 
	 * @param event
	 */
	public void bomBtnClick(ActionEvent event) {
		this.renderBomSaveButton = true;

	}

	public void orderCreationBtnClick(ActionEvent event) {
		this.renderBomSaveButton = false;

	}

	/**
	 * displays the popup for Material List
	 * 
	 * @param event
	 */
	public void browseMaterial(ActionEvent event) {
		try {
			this.materialList = findAllMaterial(this.materialToBeSearched,
					this.site);
		} catch (BusinessException exp) {
			// Display INFO message on GUI
			message = FacesUtility
					.getLocaleSpecificText("Business Exception while finding the Materials");
			setMessageBar(true, LSMessageType.ERROR);
		}

		this.materialBrowseRendered = true;

	}

	public void rowSelected(ActionEvent event) {

		closeMaterialBrowse(event);
		// set value for input field
		MaterialItem selectedMaterial = getSelectedMaterial();
		if (selectedMaterial != null) {
			this.material = selectedMaterial.getMaterial();
		}

	}
	public void rowSelectedForShopOrder(ActionEvent event) {

		closeShopOrderBrowse(event);
		// set value for input field
		ShopOrderDownloadVO selectedShopOrderDownloadVO = getSelectedShopOrder();
		if (selectedShopOrderDownloadVO != null) {
			this.shopOrderDownloaded = selectedShopOrderDownloadVO.getShopOrder();
			this.materialDownloaded = selectedShopOrderDownloadVO.getMaterial();
			this.builtQuantityDownloaded=selectedShopOrderDownloadVO.getQuantity();
			this.shopOrder=formShopOrderName(this.shopOrderDownloaded,"");
			this.material=selectedShopOrderDownloadVO.getMaterial();
		}
	}

	public boolean isRenderShopOrderBrowse() {
		return renderShopOrderBrowse;
	}

	public void setRenderShopOrderBrowse(boolean renderShopOrderBrowse) {
		this.renderShopOrderBrowse = renderShopOrderBrowse;
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
	private ShopOrderDownloadVO getSelectedShopOrder() {
		ShopOrderDownloadVO selectedShopOrderDownloadVO = null;
		if (this.shopOrderList != null) {
			for (int i = 0; i < this.shopOrderList.size(); i++) {
				ShopOrderDownloadVO shopOrderDownloadVO = this.shopOrderList.get(i);
				if (shopOrderDownloadVO != null && shopOrderDownloadVO.isSelected()) {
					selectedShopOrderDownloadVO = shopOrderDownloadVO;
					break;
				}
			}
		}
		return selectedShopOrderDownloadVO;
	}

	public void closeMaterialBrowse(ActionEvent event) {

		this.materialBrowseRendered = false;
	}
	public void closeShopOrderBrowse(ActionEvent event) {

		this.renderShopOrderBrowse = false;
	}

	/**
	 * Method set info and error messages
	 * 
	 * @param render
	 * @param messageType
	 */
	public void setMessageBar(boolean render, LSMessageType messageType) {
		HtmlOutputText messageBar = (HtmlOutputText) findComponent(FacesUtility.getFacesContext().getViewRoot(), "orderCreationDataForm:messageBar");
		messageBar.setRendered(render);
		// Blue bold message
		if(messageType.equals(LSMessageType.ERROR)){
			messageBar.setStyle("color:#FF0000;font-size:10pt;");
		}else{
			messageBar.setStyle("color:#000000;font-size:10pt;");
		}
		
		UIComponent fieldButtonPanel = findComponent(FacesUtility.getFacesContext().getViewRoot(), "orderCreationDataForm:fieldButtonPanel");
		if (fieldButtonPanel != null) {
			FacesUtility.addControlUpdate(fieldButtonPanel);
		}
		
	}
	public void showConfirmationPopup(ActionEvent actionEvent) throws BusinessException{
		//Don't fetch the bom comp if shop order data is not valid
		if(validateForMandatoryFiledsOfShopOrder()){
			return;
		}else{
			HtmlOutputText messageBar = (HtmlOutputText) findComponent(FacesUtility.getFacesContext().getViewRoot(), "orderCreationDataForm:messageBar");
			messageBar.setRendered(false);
		}
		if(this.selectedPanelId.equals("orderCreationDataForm:orderCreation")){
			this.confirmationMessage="Do you want to create orders for components of material '"+this.material+"' ? ";
			this.renderConfirmationPopup = true;
		}else{
			saveShopOrderCustomData(actionEvent);
		}
		
	}
	public void saveShopOrderCustomData(ActionEvent actionEvent) throws BusinessException {	
		if(validateForMandatoryFiledsOfShopOrder()){
			return;
		}
		if (this.serialEnd != null && this.serialStart != null) {
			if (this.serialEnd < this.serialStart) {
				message = "serial numbers are not proper";
				setMessageBar(true, LSMessageType.ERROR);
				return;
			}
			if ((this.serialEnd.intValue() - this.serialStart.intValue()) != (this.builtQuantity-1)) {
				message = "Difference of serial number should be equal to built quantity.";
				setMessageBar(true, LSMessageType.ERROR);
				return;
			}
		}
		//subtracting lead time and checking date , if less than current date system should show error
		BOMCustomDataItem maxLeadTimeItem=getMaxLeadTime(this.bomCustomDataItems);
		if(maxLeadTimeItem!=null){
			Calendar c = Calendar.getInstance();
			c.setTime(this.plannedStartDate);
			c.add(Calendar.DAY_OF_MONTH, (-1*maxLeadTimeItem.getLeadTime()));
			if(c.getTime().compareTo(new Date())<0){
				message = "Planned date for subassembly "+maxLeadTimeItem.getMaterial()+"is before today's date";
				setMessageBar(true, LSMessageType.ERROR);
				return;
			}	
		}
		
		

		
		ShopOrderBasicConfiguration basicConfiguration = shoporderService
				.findShopOrderByKeyFields(new FindShopOrderByKeyFieldsRequest(
						this.shopOrder));
		List<String> serialNos = findAllSerialNumbers();
		if (basicConfiguration == null) {

			if (serialNos.contains("" + this.serialStart)) {
				message = "Serial Number Start ";
				if (serialNos.contains("" + this.serialEnd)) {
					message = message + " and End ";
				}
				message = message
						+ "already present , please select different range";
				setMessageBar(true, LSMessageType.ERROR);
				return;
			}
			if (serialNos.contains("" + this.serialEnd)) {
				message = "Serial Number End  "
						+ " already present , please select different range";
				setMessageBar(true, LSMessageType.ERROR);
				return;
			}
			MaterialItem itemSelected =getSelectedMaterial();
			CreateShopOrderRequest createShopOrderRequest = new CreateShopOrderRequest(
					itemSelected.getVersion());
			createShopOrderRequest.setShopOrder(this.shopOrder);
			createShopOrderRequest.setPriority(new BigDecimal(PRIORITY));
			createShopOrderRequest.setErpOrder(true);
			createShopOrderRequest.setQuantityToBuild(new BigDecimal(
					this.builtQuantity));
			createShopOrderRequest
					.setPlannedStartDate(this.plannedStartDate == null ? dateGlobalizationServiceInterface
							.createDateTime(new Date().getTime())
							: dateGlobalizationServiceInterface
									.createDateTime(this.plannedStartDate
											.getTime()));
			createShopOrderRequest
					.setPlannedCompletionDate(this.plannedEndDate == null ? dateGlobalizationServiceInterface
							.createDateTime(new Date().getTime())
							: dateGlobalizationServiceInterface
									.createDateTime(this.plannedEndDate
											.getTime()));
			createShopOrderRequest
					.setScheduledCompletionDate(this.scheduleEndDate == null ? null
							: dateGlobalizationServiceInterface
									.createDateTime(this.scheduleEndDate
											.getTime()));
			createShopOrderRequest
					.setScheduledStartDate(this.scheduleStartDate == null ? null
							: dateGlobalizationServiceInterface
									.createDateTime(this.scheduleStartDate
											.getTime()));
			createShopOrderRequest.setShopOrderType(ShopOrderType.PRODUCTION);
			createShopOrderRequest.setStatus(ShopOrderStatus.Releasable);
			// setting serial numbers
			List<ShopOrderPlan> shopOrderPlans = new ArrayList<ShopOrderPlan>();
			if (this.serialStart != null && this.serialEnd != null) {
				int serialNo = this.serialStart;
				for (; serialNo <= this.serialEnd; serialNo++) {
					if (serialNos.contains("" + serialNo)) {
						message = "Serial Number  "
								+ serialNo
								+ " already present , please select different range";
						setMessageBar(true, LSMessageType.ERROR);
						return;
					}
					ShopOrderPlan orderPlan = new ShopOrderPlan();
					orderPlan.setSerialNumber("" + serialNo);

					orderPlan
							.setState(com.sap.me.demand.ShopOrderSfcPlanStateEnum.NEW);
					orderPlan.setEnabled(true);
					shopOrderPlans.add(orderPlan);

				}
				createShopOrderRequest.setShopOrderPlanList(shopOrderPlans);
			}
			if(!createOrderForBomComp){
				//main order
				ShopOrderFullConfiguration shopOrdbasicConfiguration = createOrder(createShopOrderRequest);
				this.currshopOrderFullConfiguration = shopOrdbasicConfiguration;
				//this.builtQuantityDownloaded=this.builtQuantityDownloaded-this.builtQuantity;
				//update database
				updateShopOrderDownloads(getSelectedShopOrder());
				message = "Shop Order '" + this.shopOrder + "' Created Successfully ";
			}else{
				List<CreateShopOrderRequest> subassemblyOrders=saveCustomBom();
				if(subassemblyOrders.size()== getSelectedBomOrdersCount()){
					//if no error for any order , create all orders
					//main order
					ShopOrderFullConfiguration shopOrdbasicConfiguration = createOrder(createShopOrderRequest);
					this.currshopOrderFullConfiguration = shopOrdbasicConfiguration;
					if(shopOrdbasicConfiguration!=null){
						int i=0;
						//subassembly order
						String subassemblyShopOrders="";
						for(CreateShopOrderRequest orderRequest:subassemblyOrders){
							//setting parent id in custom data of subassembly shop order
							String parentId=this.currshopOrderFullConfiguration.getShopOrderRef();
							List<AttributeValue> customData=new ArrayList<AttributeValue>();
							AttributeValue attributeValue=new AttributeValue(PARENT_ID, parentId);
							customData.add(attributeValue);
							orderRequest.setCustomData(customData);
							//comma seperated subassembly shop order
							if(!StringUtils.isBlank(subassemblyShopOrders)){
								subassemblyShopOrders=subassemblyShopOrders+",";
							}
							subassemblyShopOrders=subassemblyShopOrders+orderRequest.getShopOrder();							
							//creating order for subassembly
							createOrder(orderRequest);								
							i++;
						}
						List<AttributeValue> attributeValues=this.currshopOrderFullConfiguration.getCustomData();
						AttributeValue attributeValue=new AttributeValue("CHILD_ORDERS", subassemblyShopOrders);
						attributeValues.add(attributeValue);
						this.currshopOrderFullConfiguration.setCustomData(attributeValues);
						updateOrder(this.currshopOrderFullConfiguration);
						message = "Shop Order '" + this.shopOrder + "' Created Successfully ";
						this.builtQuantityDownloaded=this.builtQuantityDownloaded-this.builtQuantity;
						//update database
						updateShopOrderDownloads(getSelectedShopOrder());
						
					}
					
				}else{
					message = "Error while creating Shop Order '" + this.shopOrder + "' : "+message;
				}
			}
			
			
			this.shopOrderEnable = false;
			this.materialEnable = false;
			this.shopOrderEnable = true;
			this.materialEnable = true;		
			this.renderBomSaveButton = true;				
			HtmlOutputText messageBar = (HtmlOutputText) findComponent(FacesUtility.getFacesContext().getViewRoot(), "orderCreationDataForm:messageBar");
			messageBar.setRendered(true);
			messageBar.setStyle("color:#000000;font-weight:bold;font-size:10pt;");
			
			
			
			
			
		} else {
			//UPDATE CODE
//			ShopOrderFullConfiguration fullConfiguration = shoporderService
//					.readShopOrder(new ObjectReference(basicConfiguration
//							.getShopOrderRef()));
//			List<String> currSerialNo = new ArrayList<String>();
//			for (ShopOrderPlan orderPlan : fullConfiguration
//					.getShopOrderPlanList()) {
//				currSerialNo.add(orderPlan.getSerialNumber());
//			}
//
//			if (!currSerialNo.contains("" + this.serialStart)
//					&& serialNos.contains("" + this.serialStart)) {
//				message = "Serial Number Start ";
//				message = message
//						+ "already present , please select different range";
//				setMessageBar(true, LSMessageType.ERROR);
//				return;
//			}
//			if (!currSerialNo.contains("" + this.serialEnd)
//					&& serialNos.contains("" + this.serialEnd)) {
//				message = "Serial Number End  "
//						+ " already present , please select different range";
//				setMessageBar(true, LSMessageType.ERROR);
//				return;
//			}
//			fullConfiguration
//					.setPlannedStartDate(this.plannedStartDate == null ? null
//							: dateGlobalizationServiceInterface
//									.createDateTime(this.plannedStartDate
//											.getTime()));
//
//			fullConfiguration
//					.setPlannedCompletionDate(this.plannedEndDate == null ? null
//							: dateGlobalizationServiceInterface
//									.createDateTime(this.plannedEndDate
//											.getTime()));
//			fullConfiguration
//					.setScheduledCompletionDate(this.scheduleEndDate == null ? null
//							: dateGlobalizationServiceInterface
//									.createDateTime(this.scheduleEndDate
//											.getTime()));
//			fullConfiguration
//					.setScheduledStartDate(this.scheduleStartDate == null ? null
//							: dateGlobalizationServiceInterface
//									.createDateTime(this.scheduleStartDate
//											.getTime()));
//			// built quantity
//			fullConfiguration.setQuantityToBuild(new BigDecimal(
//					this.builtQuantity));
//			// setting serial numbers
//			List<ShopOrderPlan> shopOrderPlans = new ArrayList<ShopOrderPlan>();
//			if (this.serialStart != null && this.serialEnd != null) {
//				int serialNo = this.serialStart;
//				for (; serialNo <= this.serialEnd; serialNo++) {
//					if (!currSerialNo.contains("" + "" + serialNo)
//							&& serialNos.contains("" + serialNo)) {
//						message = "Serial Number  "
//								+ serialNo
//								+ " already present , please select different range";
//						setMessageBar(true, LSMessageType.ERROR);
//						return;
//					}
//					ShopOrderPlan orderPlan = new ShopOrderPlan();
//					orderPlan.setSerialNumber("" + serialNo);
//					orderPlan
//							.setState(com.sap.me.demand.ShopOrderSfcPlanStateEnum.NEW);
//					orderPlan.setEnabled(true);
//					shopOrderPlans.add(orderPlan);
//
//				}
//				fullConfiguration.setShopOrderPlanList(shopOrderPlans);
//			}
//			updateOrder(fullConfiguration);
//			updateChildOrders();
//			this.currshopOrderFullConfiguration = fullConfiguration;
//			this.shopOrderEnable = false;
//			this.materialEnable = false;
//			this.shopOrderEnable = true;
//			this.materialEnable = true;		
//			this.renderBomSaveButton = true;
//			this.renderConfirmationPopup = false;
//			this.selectedPanelId = "orderCreationDataForm:orderCreation";
			message = "Shop Order '" + this.shopOrder + "' already present ";
			setMessageBar(true, LSMessageType.ERROR);
		}
		return;
	}

	private void updateChildOrders() {
		for(BOMCustomDataItem bomCustomDataItem:this.bomCustomDataItems){
			bomCustomDataItem
			.setQuantity(new BigDecimal(bomCustomDataItem.getQuantity())
					.multiply(
							new BigDecimal(builtQuantity))
					.intValue());
									
		}

	}
	private int getSelectedBomOrdersCount(){
		int i=0;
		for(BOMCustomDataItem bomCustomDataItem:this.bomCustomDataItems){
			if(bomCustomDataItem.isCreateOrder())
			i++;
		}
		return i;
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

	private ShopOrderFullConfiguration createOrder(
			CreateShopOrderRequest createShopOrderRequest) {
		ShopOrderFullConfiguration fullConfiguration = null;
		try {
			fullConfiguration = shoporderService
					.createShopOrder(createShopOrderRequest);			

		} catch (ShopOrderUsedException e) {
			message = this.shopOrder + "already exists";
			e.printStackTrace();
			setMessageBar(true, LSMessageType.ERROR);
		} catch (ShopOrderInputException e) {
			// TODO Auto-generated catch block
			message = e.getMessage();
			e.printStackTrace();
			setMessageBar(true, LSMessageType.ERROR);
		} catch (ValueMissingException e) {
			// TODO Auto-generated catch block
			message = e.getMessage();
			e.printStackTrace();
			setMessageBar(true, LSMessageType.ERROR);
		} catch (InvalidColumnValueException e) {
			// TODO Auto-generated catch block
			message = e.getMessage();
			e.printStackTrace();
			setMessageBar(true, LSMessageType.ERROR);
		} catch (RecordNotFoundException e) {
			// TODO Auto-generated catch block
			message = e.getMessage();
			e.printStackTrace();
			setMessageBar(true, LSMessageType.ERROR);
		} catch (SiteNonProductionException e) {
			// TODO Auto-generated catch block
			message = e.getMessage();
			e.printStackTrace();
			setMessageBar(true, LSMessageType.ERROR);
		} catch (WorkCenterPermitException e) {
			// TODO Auto-generated catch block
			message = e.getMessage();
			e.printStackTrace();
			setMessageBar(true, LSMessageType.ERROR);
		} catch (ShopOrderItemTypeException e) {
			// TODO Auto-generated catch block
			message = e.getMessage();
			e.printStackTrace();
			setMessageBar(true, LSMessageType.ERROR);
		} catch (StartBeforeEndException e) {
			// TODO Auto-generated catch block
			message = e.getMessage();
			e.printStackTrace();
			setMessageBar(true, LSMessageType.ERROR);
		} catch (ShopOrderItemQtyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CustomerOrderException e) {
			// TODO Auto-generated catch block
			message = e.getMessage();
			e.printStackTrace();
			setMessageBar(true, LSMessageType.ERROR);
		} catch (CustomerException e) {
			message = e.getMessage();
			e.printStackTrace();
			setMessageBar(true, LSMessageType.ERROR);
		} catch (SfcPlanSfcIsNotUniqueException e) {
			message = e.getMessage();
			// TODO Auto-generated catch block
			e.printStackTrace();
			setMessageBar(true, LSMessageType.ERROR);
		} catch (SfcPlanSerialNumberIsNotUniqueException e) {
			message = "Serial Number is not unique";
			// TODO Auto-generated catch block
			e.printStackTrace();
			setMessageBar(true, LSMessageType.ERROR);
		} catch (SfcPlanEntryIsEmptyException e) {
			message = e.getMessage();
			// TODO Auto-generated catch block
			e.printStackTrace();
			setMessageBar(true, LSMessageType.ERROR);
		} catch (SfcPlanShopOrderMaterialLotSizeIsNotOneException e) {
			message = "SFC/Serial Numbers cannot be specified for a Shop Order for a Material with a Lot Size greater than 1 (Message 13845)";
			// TODO Auto-generated catch block
			e.printStackTrace();
			setMessageBar(true, LSMessageType.ERROR);
		} catch (SfcPlanBuildQtyException e) {
			message = e.getMessage();
			// TODO Auto-generated catch block
			e.printStackTrace();
			setMessageBar(true, LSMessageType.ERROR);
		} catch (SfcPlanReleasedQtyException e) {
			message = e.getMessage();
			// TODO Auto-generated catch block
			e.printStackTrace();
			setMessageBar(true, LSMessageType.ERROR);
		} catch (SfcPlanUpdateException e) {
			message = e.getMessage();
			// TODO Auto-generated catch block
			e.printStackTrace();
			setMessageBar(true, LSMessageType.ERROR);
		} catch (ShopOrderSchedulePlannedQuantityExceedsShopOrderBuildQuantityException e) {
			message = e.getMessage();
			// TODO Auto-generated catch block
			e.printStackTrace();
			setMessageBar(true, LSMessageType.ERROR);
		} catch (OperationScheduleEndDateMustBeGreaterThanStartDateException e) {
			message = e.getMessage();
			// TODO Auto-generated catch block
			e.printStackTrace();
			setMessageBar(true, LSMessageType.ERROR);
		} catch (BusinessException e) {
			message = e.getMessage();
			// TODO Auto-generated catch block
			e.printStackTrace();
			setMessageBar(true, LSMessageType.ERROR);
		}
		return fullConfiguration;
	}

	public List<BOMCustomDataItem> readBOMForMaterial(String itemRef)
			throws BusinessException {
		List<BOMCustomDataItem> customDataItems = new ArrayList<BOMCustomDataItem>();
		ItemFullConfiguration fullConfiguration = itemConfigurationServiceInterface
				.readItem(new ObjectReference(itemRef));
		if (fullConfiguration.getBomRef() != null) {
			Collection<BomComponentProductionConfiguration> bomComponents = this.bomConfigurationServiceInterface
					.findAllBOMComponents(new BOMComponentRequest(
							fullConfiguration.getBomRef(), new BigDecimal(1)));
			for (BomComponentProductionConfiguration bomComponentProductionConfiguration : bomComponents) {
				ItemFullConfiguration itemKeyData = itemConfigurationServiceInterface
						.readItem(new ObjectReference(
								bomComponentProductionConfiguration
										.getComponentRef()));
				itemKeyData.getCustomData();
				// select only MANUFACTURED/MANUFACTURED_PURCHASED
				if (!(ItemType.MANUFACTURED.value().equals(
						itemKeyData.getItemType().value()) || ItemType.MANUFACTURED_PURCHASED
						.value().equals(itemKeyData.getItemType().value()))) {
					continue;
				}
				BOMCustomDataItem bomCustomDataItem = new BOMCustomDataItem();
				bomCustomDataItem.setShopOrder(formShopOrderName(
						this.shopOrder, itemKeyData.getItem()));
				String customeDataValLeadTime=getCustomData(itemKeyData.getCustomData(), LEAD_TIME);
				String customeDataValCreateOrder=getCustomData(itemKeyData.getCustomData(), CREATE_ORDER);
				if(StringUtils.isNumeric(customeDataValLeadTime)){
					bomCustomDataItem.setLeadTime(Integer.parseInt(customeDataValLeadTime));
				}
				if(customeDataValCreateOrder!=null){
					bomCustomDataItem.setCreateOrder(customeDataValCreateOrder.equals(YES));
				}
				
				bomCustomDataItem
						.setItemRef(bomComponentProductionConfiguration
								.getComponentRef());
				bomCustomDataItem.setMaterial(itemKeyData.getItem());
				bomCustomDataItem
						.setQuantity(bomComponentProductionConfiguration
								.getQuantity().multiply(
										new BigDecimal(builtQuantity))
								.intValue());
				bomCustomDataItem.setShopOrderEnable(true);
				customDataItems.add(bomCustomDataItem);

			}

		}
		return customDataItems;
	}
	private String getCustomData(List<CustomValue> customDataList,String customDataKey){
		for(CustomValue customValue:customDataList){
			if(customDataKey.equals(customValue.getName())){
				return customValue.getValue().toString();
			}
		}
		return null;
	}
	private String formShopOrderName(String parentShopOrd, String itemName) {
		String shopOrd = StringUtils.substring(parentShopOrd, 0, 3);
		java.util.Date date = new java.util.Date();
		String timestamp = StringUtils.deleteWhitespace(new Timestamp(date
				.getTime()).toString());
		timestamp=StringUtils.remove(timestamp, ":");
		timestamp=StringUtils.replace(timestamp, ".", "-");
		shopOrd += timestamp;
		return shopOrd;

	}

	public String crearData() {
		FacesUtility.removeSessionMapValue("orderCreationDataBeanVolvo");
		this.shopOrderEnable = true;
		this.materialEnable = true;
		this.renderConfirmationPopup=false;
		this.selectedPanelId = "orderCreation";
		this.plannedStartDate = new Date();
		this.plannedEndDate = new Date();
		// Clear message bar area
		this.message = null;
		setMessageBar(false, null);

		UIComponent tablePanel = findComponent(FacesUtility.getFacesContext()
				.getViewRoot(), "orderCreationDataForm:materialCustomDataTable");
		if (tablePanel != null) {
			// Adds UIComponent to the list of controls that are re-rendered in
			// the request
			FacesUtility.addControlUpdate(tablePanel);
		}
		return null;
	}
	public void showShopOrderBrowsePopup(ActionEvent actionEvent){
		//fetch the shop orders downloaded
		this.shopOrderList=findShopOrderDownloads();
		this.renderShopOrderBrowse=true;
	}
	
	public List<CreateShopOrderRequest> saveCustomBom() throws BusinessException {
		List<CreateShopOrderRequest> orderRequestsForBomCompOrders=new ArrayList<CreateShopOrderRequest>();
		int errorCount = 0;		
		for (BOMCustomDataItem bomCustomDataItem : this.bomCustomDataItems) {
			if (bomCustomDataItem.isCreateOrder()) {
				String errorMsg = "Errors: ";
				boolean isErr = false;
				if (StringUtils.isBlank(bomCustomDataItem.getShopOrder())) {
					errorMsg = errorMsg + "Shop Order can not be blank. ";
					isErr = true;

				}
				if (bomCustomDataItem.getQuantity() == 0) {
					errorMsg = errorMsg + "Built quantity can not be blank. ";
					isErr = true;
				}
				if (bomCustomDataItem.getQuantity() < 0) {
					errorMsg = errorMsg + "Built quantity can not be negative number. ";
					isErr = true;
				}
				if (bomCustomDataItem.getSerialEnd() < bomCustomDataItem
						.getSerialStart()) {
					errorMsg = errorMsg + "serial numbers are not proper. ";
					isErr = true;

				}
				
				if (bomCustomDataItem.getSerialStart() != 0
						&& bomCustomDataItem.getSerialEnd() != 0) {
					if ((bomCustomDataItem.getSerialEnd() - bomCustomDataItem
							.getSerialStart()) != bomCustomDataItem.getQuantity()) {
						errorMsg = errorMsg
								+ "Difference of serial number should be equal to built quantity . ";
						isErr = true;
					}
					List<String> serialNos = findAllSerialNumbers();
					if (serialNos.contains(""
							+ bomCustomDataItem.getSerialStart())) {
						errorMsg = errorMsg + "Serial Number Start ";
						if (serialNos.contains(""
								+ bomCustomDataItem.getSerialEnd())) {
							errorMsg = errorMsg + " and End ";
						}
						errorMsg = errorMsg
								+ "already present , please select different range. ";
						isErr = true;
					} else if (serialNos.contains(""
							+ bomCustomDataItem.getSerialEnd())) {
						errorMsg = errorMsg
								+ "Serial Number End  "
								+ " already present , please select different range. ";
						isErr = true;

					}
				}

				if (isErr) {
					bomCustomDataItem.setError(true);
					bomCustomDataItem.setSuccess(false);
					bomCustomDataItem.setErrorMessage(errorMsg);
					errorCount++;
					continue;
				} else {
					bomCustomDataItem.setError(false);
					bomCustomDataItem.setSuccess(true);
					bomCustomDataItem.setErrorMessage("");
				}

				CreateShopOrderRequest createShopOrderRequest = new CreateShopOrderRequest(
						bomCustomDataItem.getItemRef());
				createShopOrderRequest.setShopOrder(bomCustomDataItem
						.getShopOrder());
				createShopOrderRequest.setPriority(new BigDecimal(PRIORITY));
				createShopOrderRequest.setErpOrder(true);
				createShopOrderRequest.setQuantityToBuild(new BigDecimal(
						bomCustomDataItem.getQuantity()));
				// adding planned start date
				Calendar c = Calendar.getInstance();
				c.setTime(this.plannedStartDate); // Now use today date.
				c.add(Calendar.DATE, (bomCustomDataItem.getLeadTime() * -1));
				createShopOrderRequest
						.setPlannedStartDate(dateGlobalizationServiceInterface
								.createDateTime(c.getTimeInMillis()));
				c.setTime(this.plannedEndDate); // Now use today date.
				c.add(Calendar.DATE, (bomCustomDataItem.getLeadTime() * -1));
				createShopOrderRequest
						.setPlannedCompletionDate(dateGlobalizationServiceInterface
								.createDateTime(c.getTimeInMillis()));
				createShopOrderRequest
						.setShopOrderType(ShopOrderType.PRODUCTION);
				createShopOrderRequest.setStatus(ShopOrderStatus.Releasable);
				// setting serial numbers
				List<ShopOrderPlan> shopOrderPlans = new ArrayList<ShopOrderPlan>();
				if (bomCustomDataItem.getSerialStart() != 0
						&& bomCustomDataItem.getSerialEnd() != 0) {
					int serialNo = bomCustomDataItem.getSerialStart();
					for (; serialNo <= bomCustomDataItem.getSerialEnd(); serialNo++) {
						ShopOrderPlan orderPlan = new ShopOrderPlan();
						orderPlan.setSerialNumber("" + serialNo);
						orderPlan
								.setState(com.sap.me.demand.ShopOrderSfcPlanStateEnum.NEW);
						orderPlan.setEnabled(true);
						shopOrderPlans.add(orderPlan);

					}
					createShopOrderRequest.setShopOrderPlanList(shopOrderPlans);
				}				
				if(!isErr)
					orderRequestsForBomCompOrders.add(createShopOrderRequest);				
				
				bomCustomDataItem.setShopOrderEnable(false);
				
			}
			
		}
		
		if (this.bomCustomDataItems.size() >= errorCount) {
			message = "Error while creating shop orders for  Components";
			setMessageBar(true, LSMessageType.ERROR);
		}
		if (errorCount == 0) {
			message = "Successfully created shop orders for components";
			setMessageBar(true, LSMessageType.INFO);
		}

		UIComponent tablePanel = findComponent(FacesUtility
				.getFacesContext().getViewRoot(),
				"orderCreationDataForm:fieldButtonPanelVolvo");
		if (tablePanel != null) {
			FacesUtility.addControlUpdate(tablePanel);
		}
		return orderRequestsForBomCompOrders;
	}
	/**
	 * Method to get maximum lead time
	 * @param bomCustomDataItems
	 * @return
	 */
	public BOMCustomDataItem getMaxLeadTime(List<BOMCustomDataItem> bomCustomDataItems){
		BOMCustomDataItem maxLeadTime=null;
		if(bomCustomDataItems!=null && bomCustomDataItems.size()>0){
			maxLeadTime=bomCustomDataItems.get(0);
			for(BOMCustomDataItem bomCustomDataItem:bomCustomDataItems){
				if(maxLeadTime.getLeadTime()<bomCustomDataItem.getLeadTime()){
					maxLeadTime=bomCustomDataItem;
				}
				
			}
		}
		return maxLeadTime;
	}
	private boolean validateForMandatoryFiledsOfShopOrder(){
		message="";
		if (StringUtils.isBlank(this.shopOrderDownloaded)) {
			message = "Shop Order can not be blank";
			setMessageBar(true, LSMessageType.ERROR);
			return true;
		}
		if (StringUtils.isBlank(this.shopOrder)) {
			message = "Shop Order can not be blank";
			setMessageBar(true, LSMessageType.ERROR);
			return true;
		}
		
		if (StringUtils.isBlank(this.material)) {
			message = "Material can not be blank";
			setMessageBar(true, LSMessageType.ERROR);
			return true;
		}
		
		if (this.builtQuantity == null || this.builtQuantity == 0) {
			message = "Built quantity can not be blank";
			setMessageBar(true, LSMessageType.ERROR);
			return true;
		}
		
			
		if(this.builtQuantityDownloaded<this.builtQuantity){
				message = "There is no sufficient quantity to be built, available quantity = "+this.builtQuantityDownloaded;
				setMessageBar(true, LSMessageType.ERROR);
		}
		//check for whether entered material is valid
		MaterialItem itemSelected = null;
		for (MaterialItem item : this.materialList) {
			if (item.getSelected() || this.material.equals(item.getMaterial())) {
				item.setSelected(true);
				itemSelected = item;
				break;
			}
		}
		if (itemSelected == null) {
			message = "Material does not exists";
			setMessageBar(true, LSMessageType.ERROR);
			return true;
		}
		return false;
	}
	public boolean isShopOrderEnable() {
		return shopOrderEnable;
	}
	public List<ShopOrderDownloadVO> getShopOrderList() {
		return shopOrderList;
	}

	public void setShopOrderList(List<ShopOrderDownloadVO> shopOrderList) {
		this.shopOrderList = shopOrderList;
	}

	public boolean isCreateOrderForBomComp() {
		return createOrderForBomComp;
	}

	public void setCreateOrderForBomComp(boolean createOrderForBomComp) {
		this.createOrderForBomComp = createOrderForBomComp;
	}

	public boolean isMaterialEnable() {
		return materialEnable;
	}

	public String getConfirmationMessage() {
		return confirmationMessage;
	}

	public void setConfirmationMessage(String confirmationMessage) {
		this.confirmationMessage = confirmationMessage;
	}

	public boolean isRenderConfirmationPopup() {
		return renderConfirmationPopup;
	}

	public void setRenderConfirmationPopup(boolean renderConfirmationPopup) {
		this.renderConfirmationPopup = renderConfirmationPopup;
	}

	public void setMaterialEnable(boolean materialEnable) {
		this.materialEnable = materialEnable;
	}

	public void setShopOrderEnable(boolean shopOrderEnable) {
		this.shopOrderEnable = shopOrderEnable;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public boolean isRenderBomSaveButton() {
		return renderBomSaveButton;
	}

	public void setRenderBomSaveButton(boolean renderBomSaveButton) {
		this.renderBomSaveButton = renderBomSaveButton;
	}

	public String getSelectedPanelId() {
		return selectedPanelId;
	}

	public void setSelectedPanelId(String selectedPanelId) {

		this.selectedPanelId = selectedPanelId;
	}

	public String getMaterialToBeSearched() {
		return materialToBeSearched;
	}

	public void setMaterialToBeSearched(String materialToBeSearched) {
		this.materialToBeSearched = materialToBeSearched;
	}

	public List<MaterialItem> getMaterialList() {
		return materialList;
	}

	public List<BOMCustomDataItem> getBomCustomDataItems() {
		return bomCustomDataItems;
	}

	public void setBomCustomDataItems(List<BOMCustomDataItem> bomCustomDataItems) {
		this.bomCustomDataItems = bomCustomDataItems;
	}

	public void setMaterialList(List<MaterialItem> materialList) {
		this.materialList = materialList;
	}

	public List<MaterialCustomDataItem> getMaterialCustomDataList() {
		return materialCustomDataList;
	}

	public void setMaterialCustomDataList(
			List<MaterialCustomDataItem> materialCustomDataList) {
		this.materialCustomDataList = materialCustomDataList;
	}

	public Boolean getMaterialBrowseRendered() {
		return materialBrowseRendered;
	}

	public void setMaterialBrowseRendered(Boolean materialBrowseRendered) {
		this.materialBrowseRendered = materialBrowseRendered;
	}

	public String getMaterial() {
		return material;
	}

	public String getShopOrder() {
		return shopOrder;
	}

	public void setShopOrder(String shopOrder) {
		this.shopOrder = shopOrder;
	}

	public String getSite() {
		return site;
	}

	public void setSite(String site) {
		this.site = site;
	}

	public void setMaterial(String material) {
		this.material = material;
	}

	public Integer getBuiltQuantity() {
		return builtQuantity;
	}

	public void setBuiltQuantity(Integer builtQuantity) {
		this.builtQuantity = builtQuantity;
	}

	public Integer getSerialStart() {
		return serialStart;
	}

	public void setSerialStart(Integer serialStart) {
		this.serialStart = serialStart;
	}

	public Integer getSerialEnd() {
		return serialEnd;
	}

	public void setSerialEnd(Integer serialEnd) {
		this.serialEnd = serialEnd;
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

	public Date getPlannedStartDate() {
		return plannedStartDate;
	}

	public void setPlannedStartDate(Date plannedStartDate) {
		this.plannedStartDate = plannedStartDate;
	}

	public Date getPlannedEndDate() {
		return plannedEndDate;
	}

	public void setPlannedEndDate(Date plannedEndDate) {
		this.plannedEndDate = plannedEndDate;
	}

	public SystemBase getDbBase() {
		return dbBase;
	}
	public String getShopOrderDownloaded() {
		return shopOrderDownloaded;
	}

	public int getBuiltQuantityDownloaded() {
		return builtQuantityDownloaded;
	}

	public void setBuiltQuantityDownloaded(int builtQuantityDownloaded) {
		this.builtQuantityDownloaded = builtQuantityDownloaded;
	}

	public String getMaterialDownloaded() {
		return materialDownloaded;
	}

	public void setMaterialDownloaded(String materialDownloaded) {
		this.materialDownloaded = materialDownloaded;
	}

	public void setShopOrderDownloaded(String shopOrderDownloaded) {
		this.shopOrderDownloaded = shopOrderDownloaded;
	}

	/**************************** DB CODE ***********************/
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
	public List<MaterialItem> findAllMaterial(String material, String site)
			throws BusinessException {

		Connection con = null;
		String query = "select   ITEM,HANDLE from  ITEM WHERE ITEM_TYPE  in ('M','B') and ITEM IS NOT NULL AND SITE = '"
				+ site + "'";
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
				MaterialItem materialItem = new MaterialItem();
				materialItem.setMaterial("" + rs.getString("ITEM"));
				materialItem.setVersion("" + rs.getString("HANDLE"));
				materials.add(materialItem);
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

	public List<String> findAllSerialNumbers() {

		Connection con = null;
		String query = "select SERIAL_NUMBER from  SHOP_ORDER_SFC_PLAN ";
		PreparedStatement ps = null;
		List<String> serialNumbers = new ArrayList<String>();
		try {

			con = getConnection();
			ps = con.prepareStatement(query);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				serialNumbers.add(rs.getString("SERIAL_NUMBER"));

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
		return serialNumbers;
	}

	public void saveShopOrderMappings(BOMCustomDataItem customDataItem,
			ShopOrderFullConfiguration parentShopOrder) {

		String insertTableSQL = "INSERT INTO SHOP_ORDER_MAPPING (PARENT_SHOP_ORDER_BO,CHILD_SHOP_ORDER_BO,LEAD_TIME) VALUES (?,?,?)";
		Connection con = null;
		boolean isCreated = false;
		PreparedStatement preparedStatement = null;
		try {
			con = getConnection();
			preparedStatement = con.prepareStatement(insertTableSQL);
			preparedStatement.setString(2, customDataItem.getShopOrderRef());
			preparedStatement.setString(1, parentShopOrder.getShopOrderRef());
			preparedStatement.setInt(3, customDataItem.getLeadTime());
			// execute insert SQL stetement
			preparedStatement.executeUpdate();
			isCreated = true;
		} catch (SQLException e) {
			e.printStackTrace();
			isCreated = false;
		} finally {
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

	public List<Map<String, Integer>> findShopOrderMappings(
			String parentShopOrderRef) {

		Connection con = null;
		String query = "select CHILD_SHOP_ORDER_BO,LEAD_TIME from  SHOP_ORDER_MAPPING ";
		PreparedStatement ps = null;
		List<Map<String, Integer>> childOrders = new ArrayList<Map<String, Integer>>();
		try {

			con = getConnection();
			ps = con.prepareStatement(query);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				Map<String, Integer> map = new HashMap<String, Integer>();
				map.put(rs.getString("CHILD_SHOP_ORDER_BO"), rs
						.getInt("LEAD_TIME"));
				childOrders.add(map);
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
		return childOrders;
	}
	public int updateShopOrderDownloads(ShopOrderDownloadVO orderDownloadVO) {

		Connection con = null;
		String query = "update SHOP_ORDER_DOWNLOAD set BUILT_QUANTITY = ? where SHOP_ORDER = ?";
		PreparedStatement ps = null;
		try {

			con = getConnection();
			ps = con.prepareStatement(query);
			ps.setInt(0, orderDownloadVO.getQuantity());
			ps.setString(1, orderDownloadVO.getShopOrder());
			return ps.executeUpdate();
			

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
		return 0;
	}
	public List<ShopOrderDownloadVO> findShopOrderDownloads() {

		Connection con = null;
		String query = "select SHOP_ORDER,MATERIAL,BUILT_QUANTITY from  SHOP_ORDER_DOWNLOAD ";
		PreparedStatement ps = null;
		 List<ShopOrderDownloadVO> shopOrderDownloads = new ArrayList<ShopOrderDownloadVO>();
		try {

			con = getConnection();
			ps = con.prepareStatement(query);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				ShopOrderDownloadVO downloadVO=new ShopOrderDownloadVO();
				downloadVO.setShopOrder(rs.getString("SHOP_ORDER"));
				downloadVO.setMaterial(rs.getString("MATERIAL"));
				downloadVO.setQuantity(rs.getInt("BUILT_QUANTITY"));
				shopOrderDownloads.add(downloadVO);
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
		return shopOrderDownloads;
	}
	
}
