package com.atos.genericpattern;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.faces.component.UIComponent;
import javax.faces.component.html.HtmlOutputText;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.lang3.StringUtils;

import com.sap.me.common.ItemType;
import com.sap.me.common.ObjectAliasEnum;
import com.sap.me.common.ObjectReference;
import com.sap.me.customdata.CustomDataConfigurationServiceInterface;
import com.sap.me.customdata.CustomFieldBasicConfiguration;
import com.sap.me.customdata.FindCustomFieldDefinitionRequest;
import com.sap.me.demand.ShopOrderType;
import com.sap.me.frame.domain.BusinessException;
import com.sap.me.frame.service.CommonMethods;
import com.sap.me.productdefinition.BOMComponentRequest;
import com.sap.me.productdefinition.BOMConfigurationException;
import com.sap.me.productdefinition.BOMConfigurationServiceInterface;
import com.sap.me.productdefinition.BomComponentProductionConfiguration;
import com.sap.me.productdefinition.FindRouterStepsRequest;
import com.sap.me.productdefinition.ItemConfigurationServiceInterface;
import com.sap.me.productdefinition.RouterConfigurationServiceInterface;
import com.sap.me.productdefinition.RouterStepConfiguration;
import com.sap.me.production.podclient.BasePodPlugin;
import com.sap.me.security.RunAsServiceLocator;
import com.sap.me.wpmf.TableConfigurator;
import com.sap.me.wpmf.util.FacesUtility;
import com.sap.tc.ls.api.enumerations.LSMessageType;
import com.sap.tc.ls.internal.faces.component.UIMessageBar;
import com.sap.ui.faces.component.sap.UICommandButton;
import com.sap.ui.faces.component.sap.UICommandInputText;
import com.visiprise.frame.configuration.ServiceReference;

public class GenericPatternDataController extends BasePodPlugin {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String patternName;
	private String patternType;
	private String patternTypeMaster;
	private String patternTypeValue;
	private String version;
	private Boolean materialBrowseRendered;
	private List<PatternItem> patternList = new ArrayList<PatternItem>();
	private List<PatternItem> patternAttributeList = new ArrayList<PatternItem>();
	private List<PatternItem> patternTypeMasterList = new ArrayList<PatternItem>();
	private List<PatternItem> patternTypeList = new ArrayList<PatternItem>();
	private List<PatternItem> patternTypeValueList = new ArrayList<PatternItem>();
	private List<PatternItem> oldPatternItemList = new ArrayList<PatternItem>();
	private final Map<String, List<PatternItem>> patternAttributeMap = new HashMap<String, List<PatternItem>>();
	private final Map<String, List<PatternItem>> attributeValuesMap = new HashMap<String, List<PatternItem>>();
	private PatternDaoMnanagerImpl patternDaoMnanagerImpl;
	private String message;
	private List<PatternCustomDataItem> patternCustomDataList;
	private List<SelectItem> patternTypes;
	private TableConfigurator tableConfigBean = null;
	private boolean disablePatternType;
	private boolean disablePatternTypeMaster;
	private boolean disablePatternTypeValue;
	private boolean renderAttributeValuePopup;
	private boolean renderPatternTypeMasterPopup;
	private boolean renderPatternTypePopup;
	private boolean renderPatternTypeValuePopup;
	private String showBrowseName;
	private static final String PATTERN_ATTRIBUTE = "showPatternAttributeBrowse";
	private static final String ATTRIBUTE_VALUE = "showAttributeValueBrowse";
	private static final String PATTERN_ATTRIBUTE_HEADING = "Pattern Attribute";
	private static final String ATTRIBUTE_VALUE_HEADING = "Attribute Value";
	private PatternCustomDataItem currePatternCustomDataItem = null;
	private BOMConfigurationServiceInterface bomConfigurationServiceInterface;
	private CustomDataConfigurationServiceInterface customDataConfigurationServiceInterface;
	private ItemConfigurationServiceInterface itemConfigurationServiceInterface;
	private RouterConfigurationServiceInterface routerConfigurationServiceInterface;
	private String user=CommonMethods.getUserId();

	public GenericPatternDataController() {
		ServiceReference bomConfigurationServiceRef = new ServiceReference(
				"com.sap.me.productdefinition", "BOMConfigurationService");
		bomConfigurationServiceInterface = RunAsServiceLocator.getService(
				bomConfigurationServiceRef,
				BOMConfigurationServiceInterface.class, user, CommonMethods
						.getSite(), null);
		ServiceReference customDataConfigurationServiceRef = new ServiceReference(
				"com.sap.me.customdata", "CustomDataConfigurationService");
		customDataConfigurationServiceInterface = RunAsServiceLocator
				.getService(customDataConfigurationServiceRef,
						CustomDataConfigurationServiceInterface.class, user,
						CommonMethods.getSite(), null);
		ServiceReference itemConfigurationServiceRef = new ServiceReference(
				"com.sap.me.productdefinition", "ItemConfigurationService");
		itemConfigurationServiceInterface = RunAsServiceLocator.getService(
				itemConfigurationServiceRef,
				ItemConfigurationServiceInterface.class, user, CommonMethods
						.getSite(), null);
		ServiceReference routerConfigurationServiceRef = new ServiceReference(
				"com.sap.me.productdefinition", "RouterConfigurationService");
		routerConfigurationServiceInterface = RunAsServiceLocator.getService(
				routerConfigurationServiceRef,
				RouterConfigurationServiceInterface.class, user,
				CommonMethods.getSite(), null);
		// Initialize Material Browse list
		initMaterialList();
	}

	public String clear() {
		// clear bean caches
		FacesUtility.removeSessionMapValue("genericPatternCustomDataBean");
		FacesUtility
				.removeSessionMapValue("customDataBrowseBean_currentBrowseComponentId");
		FacesUtility.removeSessionMapValue("customDataBrowseBean");
		// Clear message bar area
		this.message = null;
		setMessageBar(false, null);
		// findComponent() - Helper method to find a component given an input
		// parent component and id of component to find
		// getViewRoot() - Return the root component that is associated with
		// this request
		UIComponent tablePanel = findComponent(FacesUtility.getFacesContext()
				.getViewRoot(), "materialCustomDataForm:displayPanel");
		if (tablePanel != null) {
			// Adds UIComponent to the list of controls that are re-rendered in
			// the request
			FacesUtility.addControlUpdate(tablePanel);
		}
		return null;
	}
	public String onBlurMethod(){
		FacesUtility.addScriptCommand("alert('inside on blur'");
		return "";
	}
	private void clearAferDelete(String patternName) {
		// Clear message bar area
		this.message = patternName + " is deleted successfully";
		setMessageBar(true, LSMessageType.INFO);
		// findComponent() - Helper method to find a component given an input
		// parent component and id of component to find
		// getViewRoot() - Return the root component that is associated with
		// this request
		UIComponent tablePanel = findComponent(FacesUtility.getFacesContext()
				.getViewRoot(), "materialCustomDataForm:displayPanel");
		if (tablePanel != null) {
			// Adds UIComponent to the list of controls that are re-rendered in
			// the request
			FacesUtility.addControlUpdate(tablePanel);
		}

	}

	// Activity closed , window unload
	public void processWindowClosed() {
		FacesUtility.removeSessionMapValue("patternCustomDataBean");
		FacesUtility.removeSessionMapValue("customDataBrowseBean");
		FacesUtility
				.removeSessionMapValue("customDataBrowseBean_currentBrowseComponentId");
	}

	public TableConfigurator getTableConfigBean() {
		return tableConfigBean;
	}

	public void setTableConfigBean(TableConfigurator config) {

	}

	public String insertNewRow() {
		if (patternCustomDataList == null || patternCustomDataList.size() == 0)
			patternCustomDataList = new ArrayList<PatternCustomDataItem>();
		PatternCustomDataItem customDataItem = new PatternCustomDataItem();
		customDataItem.setEditable(true);
		patternCustomDataList.add(customDataItem);
		// clean message bar
		this.message = null;
		setMessageBar(false, null);
		UIComponent tablePanel = findComponent(FacesUtility.getFacesContext()
				.getViewRoot(), "materialCustomDataForm:displayPanel");
		if (tablePanel != null) {
			// Adds UIComponent to the list of controls that are re-rendered in
			// the request
			FacesUtility.addControlUpdate(tablePanel);
		}
		return null;
	}

	public String insertNewRowBefore() {
		if (patternCustomDataList == null || patternCustomDataList.size() == 0)
			patternCustomDataList = new ArrayList<PatternCustomDataItem>();
		int index = 0;
		for (PatternCustomDataItem customDataItem : patternCustomDataList) {
			if (customDataItem.isSelect()) {
				PatternCustomDataItem customDataItemTemp = new PatternCustomDataItem();
				customDataItemTemp.setEditable(true);
				patternCustomDataList.add(index, customDataItemTemp);
				break;
			}
			index++;
		}
		// clean message bar
		this.message = null;
		setMessageBar(false, null);
		UIComponent tablePanel = findComponent(FacesUtility.getFacesContext()
				.getViewRoot(), "materialCustomDataForm:displayPanel");
		if (tablePanel != null) {
			// Adds UIComponent to the list of controls that are re-rendered in
			// the request
			FacesUtility.addControlUpdate(tablePanel);
		}
		return null;
	}

	public String insertNewRowAfter() {
		if (patternCustomDataList == null || patternCustomDataList.size() == 0)
			patternCustomDataList = new ArrayList<PatternCustomDataItem>();
		int index = 0;
		for (PatternCustomDataItem customDataItem : patternCustomDataList) {
			if (customDataItem.isSelect()) {
				PatternCustomDataItem customDataItemTemp = new PatternCustomDataItem();
				customDataItemTemp.setEditable(true);
				patternCustomDataList.add(index + 1, customDataItemTemp);
				break;
			}
			index++;
		}
		// clean message bar
		this.message = null;
		setMessageBar(false, null);
		UIComponent tablePanel = findComponent(FacesUtility.getFacesContext()
				.getViewRoot(), "materialCustomDataForm:displayPanel");
		if (tablePanel != null) {
			// Adds UIComponent to the list of controls that are re-rendered in
			// the request
			FacesUtility.addControlUpdate(tablePanel);
		}
		return null;
	}

	public String deletSelectedRow() {
		this.message = null;
		setMessageBar(false, null);
		if (this.patternCustomDataList == null
				|| this.patternCustomDataList.size() == 0)
			return null;
		List<PatternCustomDataItem> patternCustomDataNewList = new ArrayList<PatternCustomDataItem>();
		for (PatternCustomDataItem materialCustomDataItem : this.patternCustomDataList) {
			if (materialCustomDataItem.isSelect()) {
				continue;
			}
			patternCustomDataNewList.add(materialCustomDataItem);
		}
		this.patternCustomDataList = patternCustomDataNewList;
		UIComponent tablePanel = findComponent(FacesUtility.getFacesContext()
				.getViewRoot(), "materialCustomDataForm:displayPanel");
		if (tablePanel != null) {
			// Adds UIComponent to the list of controls that are re-rendered in
			// the request
			FacesUtility.addControlUpdate(tablePanel);
		}
		return null;
	}

	public String deleteAll() {
		this.patternCustomDataList = new ArrayList<PatternCustomDataItem>();
		this.currePatternCustomDataItem = null;
		UIComponent tablePanel = findComponent(FacesUtility.getFacesContext()
				.getViewRoot(), "materialCustomDataForm:displayPanel");
		if (tablePanel != null) {
			// Adds UIComponent to the list of controls that are re-rendered in
			// the request
			FacesUtility.addControlUpdate(tablePanel);
		}
		return null;
	}

	public String deletePattern() {
		Pattern pattern = this.patternDaoMnanagerImpl
				.findPattern(this.patternName);
		if(StringUtils.isBlank(this.patternName)){
			this.message = "PatternName can not be empty.";
			setMessageBar(true, LSMessageType.ERROR);
			return null;
		}
		if (pattern == null) {
			this.message = "Pattern does not exists.";
			setMessageBar(true, LSMessageType.ERROR);
			return null;
		}

		this.patternDaoMnanagerImpl.deletePattern(pattern);
		String patternName = this.patternName;
		this.clearAferDelete(patternName);
		return null;
	}


	public void setMessageBar(boolean render, LSMessageType messageType) {
		HtmlOutputText messageBar = (HtmlOutputText) findComponent(FacesUtility
				.getFacesContext().getViewRoot(),
				"materialCustomDataForm:messageBar");
		messageBar.setRendered(render);
		messageBar.setStyle("font-size:10pt;");
		if(messageType!=null&&messageType.equals(LSMessageType.ERROR))
		{
			messageBar.setStyle("color:#FF1500;font-size:10pt;");
		}
		UIComponent fieldButtonPanel = findComponent(FacesUtility
				.getFacesContext().getViewRoot(),
				"materialCustomDataForm:fieldButtonPanel");
		if (fieldButtonPanel != null) {
			FacesUtility.addControlUpdate(fieldButtonPanel);
		}
	}

	public String readMaterialCustomData() {
		if (StringUtils.isBlank(this.patternName)) {
			message = FacesUtility
					.getLocaleSpecificText("Pattern name can not be blank");
			setMessageBar(true, LSMessageType.INFO);
			return null;
		}
		this.message = null;
		setMessageBar(false, null);
		patternCustomDataList = new ArrayList<PatternCustomDataItem>();

		try {

			this.patternCustomDataList = this.fetchPatternDataList();
			if (patternCustomDataList.size() == 0) {
				// Display INFO message on GUI
				message = FacesUtility
						.getLocaleSpecificText("noRecords.MESSAGE");
				setMessageBar(true, LSMessageType.INFO);
			}
		} catch (Exception ex) {
			// Display error message on GUI
			message = "Runtime Exception: " + ex.getLocalizedMessage();
			setMessageBar(true, LSMessageType.ERROR);
		}
		// Make sure you add the table to the list of control updates so that
		// the new model value will be shown on the UI.
		UIComponent tablePanel = findComponent(FacesUtility.getFacesContext()
				.getViewRoot(), "materialCustomDataForm:displayPanel");
		if (tablePanel != null) {
			FacesUtility.addControlUpdate(tablePanel);
		}
		return null;
	}

	// Read all materials for current site
	public void initMaterialList() {
		this.patternDaoMnanagerImpl = new PatternDaoMnanagerImpl();
		this.disablePatternTypeMaster = true;
		this.disablePatternTypeValue = true;
		// initialising pattern types
		this.patternTypes = this.getPatternTypesList();
		this.patternList = new ArrayList<PatternItem>();
		// list of Material pattern attributes
		List<PatternItem> materialPatternItems = new ArrayList<PatternItem>();
		materialPatternItems.add(new PatternItem(
				AttributeValueConstants.VERSION));
		materialPatternItems.add(new PatternItem(AttributeValueConstants.TYPE));
		materialPatternItems.add(new PatternItem(
				AttributeValueConstants.ORDER_TYPE));
		materialPatternItems.add(new PatternItem(
				AttributeValueConstants.LOT_SIZE));
		materialPatternItems.add(new PatternItem(
				AttributeValueConstants.DRAWING_NAME));
		materialPatternItems
				.add(new PatternItem(AttributeValueConstants.PANEL));
		materialPatternItems.add(new PatternItem(
				AttributeValueConstants.TSM_VAL));
		materialPatternItems.add(new PatternItem(
				AttributeValueConstants.COLLECTOR));
		materialPatternItems.add(new PatternItem(
				AttributeValueConstants.CERTIFICATION));
		materialPatternItems.add(new PatternItem(
				AttributeValueConstants.CUSTOM_DATA));
		this.patternAttributeMap.put(AttributeValueConstants.MATERIAL,
				materialPatternItems);
		// list of shop order pattern attributes
		materialPatternItems = new ArrayList<PatternItem>();
		materialPatternItems.add(new PatternItem(
				AttributeValueConstants.ORDER_TYPE));
		materialPatternItems.add(new PatternItem(AttributeValueConstants.LCC));
		materialPatternItems.add(new PatternItem(
				AttributeValueConstants.PRIORITY));
		materialPatternItems.add(new PatternItem(
				AttributeValueConstants.BUILT_QTY));
		materialPatternItems.add(new PatternItem(
				AttributeValueConstants.CUSTOM_DATA));
		this.patternAttributeMap.put(AttributeValueConstants.SHOP_ORDER,
				materialPatternItems);
		// list of BOM pattern attributes
		materialPatternItems = new ArrayList<PatternItem>();
		materialPatternItems.add(new PatternItem(
				AttributeValueConstants.COMPONENT_VALUE));
		materialPatternItems.add(new PatternItem(
				AttributeValueConstants.ERP_BOM));
		materialPatternItems.add(new PatternItem(
				AttributeValueConstants.CUSTOM_DATA));
		materialPatternItems.add(new PatternItem(
				AttributeValueConstants.VERSION));
		this.patternAttributeMap.put(AttributeValueConstants.BOM,
				materialPatternItems);
		// list of BOM Component pattern attributes
		materialPatternItems = new ArrayList<PatternItem>();
		materialPatternItems.add(new PatternItem(
				AttributeValueConstants.ASSEMBLY_SEQUENCE));
		materialPatternItems.add(new PatternItem(
				AttributeValueConstants.COMPONENT_NAME));
		materialPatternItems.add(new PatternItem(
				AttributeValueConstants.ASSEMBLY_OPERATION));
		materialPatternItems.add(new PatternItem(
				AttributeValueConstants.COMPONENT_TYPE));
		materialPatternItems.add(new PatternItem(
				AttributeValueConstants.MAX_USAGE));
		materialPatternItems.add(new PatternItem(
				AttributeValueConstants.COMPONENT_VERSION));
		materialPatternItems.add(new PatternItem(
				AttributeValueConstants.CUSTOM_DATA));
		this.patternAttributeMap.put(AttributeValueConstants.BOM_COMPONENT,
				materialPatternItems);
		// list of Routing pattern attributes
		materialPatternItems = new ArrayList<PatternItem>();
		materialPatternItems.add(new PatternItem(
				AttributeValueConstants.ROUTING_TYPE));
		materialPatternItems.add(new PatternItem(
				AttributeValueConstants.CUSTOM_DATA));
		this.patternAttributeMap.put(AttributeValueConstants.ROUTING,
				materialPatternItems);
		// list of routing step attributes
		materialPatternItems = new ArrayList<PatternItem>();
		materialPatternItems.add(new PatternItem(
				AttributeValueConstants.CUSTOM_DATA));
		materialPatternItems.add(new PatternItem(
				AttributeValueConstants.WORK_CENTER));
		materialPatternItems.add(new PatternItem(
				AttributeValueConstants.ERP_WORKCENTER));
		materialPatternItems.add(new PatternItem(
				AttributeValueConstants.ERP_SEQUENCE));
		materialPatternItems.add(new PatternItem(
				AttributeValueConstants.ERP_CONTROL_KEY));
		this.patternAttributeMap.put(AttributeValueConstants.ROUTING_STEP,
				materialPatternItems);
		// list of Material attributes values type
		List<PatternItem> materialAttributeValueItems = new ArrayList<PatternItem>();
		materialAttributeValueItems.add(new PatternItem(
				AttributeValueConstants.MATERIAL_TYPE_MANUFACTURED,ItemType.MANUFACTURED.value()));
		materialAttributeValueItems.add(new PatternItem(
				AttributeValueConstants.MATERIAL_TYPE_MANUFACTURED_PURCHASED,ItemType.MANUFACTURED_PURCHASED.value()));
		materialAttributeValueItems.add(new PatternItem(
				AttributeValueConstants.MATERIAL_TYPE_PURCHASED,ItemType.PURCHASED.value()));
		materialAttributeValueItems.add(new PatternItem(
				AttributeValueConstants.MATERIAL_TYPE_INSTALLATION,ItemType.INSTALLATION.value()));
		this.attributeValuesMap.put(AttributeValueConstants.MATERIAL + ":"
				+ AttributeValueConstants.TYPE, materialAttributeValueItems);
		// list of Material attributes values ORDER_TYPE
		materialAttributeValueItems = new ArrayList<PatternItem>();
		materialAttributeValueItems.add(new PatternItem(
				AttributeValueConstants.MATERIAL_ORDER_TYPE_PRODUCTION,ShopOrderType.PRODUCTION.value()));
		materialAttributeValueItems.add(new PatternItem(
				AttributeValueConstants.MATERIAL_ORDER_TYPE_ENGINEERING,ShopOrderType.ENGINEERING.value()));
		materialAttributeValueItems.add(new PatternItem(
				AttributeValueConstants.MATERIAL_ORDER_TYPE_INSPECTION,ShopOrderType.INSPECTION.value()));
		materialAttributeValueItems.add(new PatternItem(
				AttributeValueConstants.MATERIAL_ORDER_TYPE_REWORK,ShopOrderType.REWORK.value()));
		materialAttributeValueItems.add(new PatternItem(
				AttributeValueConstants.MATERIAL_ORDER_TYPE_REPETITIVE,ShopOrderType.REPETITIVE.value()));
		materialAttributeValueItems.add(new PatternItem(
				AttributeValueConstants.MATERIAL_ORDER_TYPE_RMA,ShopOrderType.RMA.value()));
		materialAttributeValueItems.add(new PatternItem(
				AttributeValueConstants.MATERIAL_ORDER_TYPE_TOOLING,ShopOrderType.TOOLING.value()));
		materialAttributeValueItems.add(new PatternItem(
				AttributeValueConstants.MATERIAL_ORDER_TYPE_SPARE,ShopOrderType.SPARE.value()));
		materialAttributeValueItems.add(new PatternItem(
				AttributeValueConstants.MATERIAL_ORDER_TYPE_INSTALLATION,ShopOrderType.INSTALLATION.value()));
		this.attributeValuesMap.put(AttributeValueConstants.MATERIAL + ":"
				+ AttributeValueConstants.ORDER_TYPE,
				materialAttributeValueItems);
		this.attributeValuesMap.put(AttributeValueConstants.SHOP_ORDER + ":"
				+ AttributeValueConstants.ORDER_TYPE,
				materialAttributeValueItems);
		// list of Material attributes values Cutom_data
		materialAttributeValueItems = new ArrayList<PatternItem>();
		materialAttributeValueItems
				.add(new PatternItem(
						AttributeValueConstants.ROUTING_ROUTING_TYPE_DISPOSITION_ROUTING));
		materialAttributeValueItems.add(new PatternItem(
				AttributeValueConstants.ROUTING_ROUTING_TYPE_NC_ROUTING));
		materialAttributeValueItems.add(new PatternItem(
				AttributeValueConstants.ROUTING_ROUTING_TYPE_SPECIAL_ROUTING));
		materialAttributeValueItems
				.add(new PatternItem(
						AttributeValueConstants.ROUTING_ROUTING_TYPE_DISPOSITION_ROUTING));
		materialAttributeValueItems
				.add(new PatternItem(
						AttributeValueConstants.ROUTING_ROUTING_TYPE_SFC_SPECIFIC_ROUTING));
		materialAttributeValueItems
				.add(new PatternItem(
						AttributeValueConstants.ROUTING_ROUTING_TYPE_ORDER_SPECIFIC_ROUTING));
		materialAttributeValueItems.add(new PatternItem(
				AttributeValueConstants.ROUTING_ROUTING_TYPE_SAMPLE_ROUTING));
		this.attributeValuesMap.put(AttributeValueConstants.ROUTING + ":"
				+ AttributeValueConstants.ROUTING_TYPE,
				materialAttributeValueItems);

		try {
			materialAttributeValueItems = new ArrayList<PatternItem>();
			Collection<CustomFieldBasicConfiguration> customFieldBasicConfigurations = customDataConfigurationServiceInterface
					.findSiteSpecificCustomFieldConfigurationsByCategory(new FindCustomFieldDefinitionRequest(
							ObjectAliasEnum.ITEM.toString()));
			for (CustomFieldBasicConfiguration basicConfiguration : customFieldBasicConfigurations) {
				materialAttributeValueItems.add(new PatternItem(
						basicConfiguration.getFieldName()));
			}
			this.attributeValuesMap.put(AttributeValueConstants.MATERIAL + ":"
					+ AttributeValueConstants.CUSTOM_DATA,
					materialAttributeValueItems);
			// SHOP ORDER
			materialAttributeValueItems = new ArrayList<PatternItem>();
			customFieldBasicConfigurations = customDataConfigurationServiceInterface
					.findSiteSpecificCustomFieldConfigurationsByCategory(new FindCustomFieldDefinitionRequest(
							ObjectAliasEnum.SHOP_ORDER.toString()));
			for (CustomFieldBasicConfiguration basicConfiguration : customFieldBasicConfigurations) {
				materialAttributeValueItems.add(new PatternItem(
						basicConfiguration.getFieldName()));
			}
			this.attributeValuesMap.put(AttributeValueConstants.SHOP_ORDER
					+ ":" + AttributeValueConstants.CUSTOM_DATA,
					materialAttributeValueItems);
			// BOM
			materialAttributeValueItems = new ArrayList<PatternItem>();
			customFieldBasicConfigurations = customDataConfigurationServiceInterface
					.findSiteSpecificCustomFieldConfigurationsByCategory(new FindCustomFieldDefinitionRequest(
							ObjectAliasEnum.BOM.toString()));
			for (CustomFieldBasicConfiguration basicConfiguration : customFieldBasicConfigurations) {
				materialAttributeValueItems.add(new PatternItem(
						basicConfiguration.getFieldName()));
			}
			this.attributeValuesMap.put(AttributeValueConstants.BOM + ":"
					+ AttributeValueConstants.CUSTOM_DATA,
					materialAttributeValueItems);
			// BOM COMPONENT
			materialAttributeValueItems = new ArrayList<PatternItem>();
			customFieldBasicConfigurations = customDataConfigurationServiceInterface
					.findSiteSpecificCustomFieldConfigurationsByCategory(new FindCustomFieldDefinitionRequest(
							ObjectAliasEnum.BOM_COMPONENT.toString()));
			for (CustomFieldBasicConfiguration basicConfiguration : customFieldBasicConfigurations) {
				materialAttributeValueItems.add(new PatternItem(
						basicConfiguration.getFieldName()));
			}
			this.attributeValuesMap.put(AttributeValueConstants.BOM_COMPONENT
					+ ":" + AttributeValueConstants.CUSTOM_DATA,
					materialAttributeValueItems);
			// ROUTING
			materialAttributeValueItems = new ArrayList<PatternItem>();
			customFieldBasicConfigurations = customDataConfigurationServiceInterface
					.findSiteSpecificCustomFieldConfigurationsByCategory(new FindCustomFieldDefinitionRequest(
							ObjectAliasEnum.ROUTER.toString()));
			for (CustomFieldBasicConfiguration basicConfiguration : customFieldBasicConfigurations) {
				materialAttributeValueItems.add(new PatternItem(
						basicConfiguration.getFieldName()));
			}
			this.attributeValuesMap.put(AttributeValueConstants.ROUTING + ":"
					+ AttributeValueConstants.CUSTOM_DATA,
					materialAttributeValueItems);
			// ROUTING STEP
			materialAttributeValueItems = new ArrayList<PatternItem>();
			customFieldBasicConfigurations = customDataConfigurationServiceInterface
					.findSiteSpecificCustomFieldConfigurationsByCategory(new FindCustomFieldDefinitionRequest(
							ObjectAliasEnum.ROUTER_STEP.toString()));
			for (CustomFieldBasicConfiguration basicConfiguration : customFieldBasicConfigurations) {
				materialAttributeValueItems.add(new PatternItem(
						basicConfiguration.getFieldName()));
			}
			this.attributeValuesMap.put(AttributeValueConstants.ROUTING_STEP
					+ ":" + AttributeValueConstants.CUSTOM_DATA,
					materialAttributeValueItems);
		} catch (BusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public boolean isSelected() {

		PatternItem currentRow = getCurrentSelectedRowMaterailBrowseTable();
		if (currentRow != null) {
			return currentRow.getSelected().booleanValue();
		}
		return false;
	}

	public boolean isPatternTypeValueSelected() {

		Map<String, Object> requestMap = FacesContext.getCurrentInstance()
				.getExternalContext().getRequestMap();
		PatternItem currentRow = (PatternItem) requestMap
				.get("patternTypeValueBrowseVar");
		if (currentRow != null) {
			return currentRow.getSelected().booleanValue();
		}
		return false;
	}

	public boolean isPatternTypeMasterSelected() {

		Map<String, Object> requestMap = FacesContext.getCurrentInstance()
				.getExternalContext().getRequestMap();
		PatternItem currentRow = (PatternItem) requestMap
				.get("patternTypeMasterBrowseVar");
		if (currentRow != null) {
			return currentRow.getSelected().booleanValue();
		}
		return false;
	}

	public boolean isAttributeSelected() {

		Map<String, Object> requestMap = FacesContext.getCurrentInstance()
				.getExternalContext().getRequestMap();
		PatternItem currentRow = (PatternItem) requestMap
				.get("patternAttributeBrowseVar");
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
		PatternItem currentRow = getCurrentSelectedRowMaterailBrowseTable();
		currentRow.setSelected(new Boolean(selected));
	}

	/**
	 * @nooverride
	 * @param selected
	 *            set the selection of the current row
	 */
	public void setPatternTypeValueSelected(boolean selected) {
		Map<String, Object> requestMap = FacesContext.getCurrentInstance()
				.getExternalContext().getRequestMap();
		PatternItem currentRow = (PatternItem) requestMap
				.get("patternTypeValueBrowseVar");
		currentRow.setSelected(new Boolean(selected));
	}

	/**
	 * @nooverride
	 * @param selected
	 *            set the selection of the current row
	 */
	public void setPatternTypeMasterSelected(boolean selected) {
		Map<String, Object> requestMap = FacesContext.getCurrentInstance()
				.getExternalContext().getRequestMap();
		PatternItem currentRow = (PatternItem) requestMap
				.get("patternTypeMasterBrowseVar");
		currentRow.setSelected(new Boolean(selected));
	}

	/**
	 * @nooverride
	 * @param selected
	 *            set the selection of the current row
	 */
	public void setAttributeSelected(boolean selected) {
		Map<String, Object> requestMap = FacesContext.getCurrentInstance()
				.getExternalContext().getRequestMap();
		PatternItem currentRow = (PatternItem) requestMap
				.get("patternAttributeBrowseVar");
		currentRow.setSelected(new Boolean(selected));
	}

	private PatternItem getCurrentSelectedRowMaterailBrowseTable() {

		Map<String, Object> requestMap = FacesContext.getCurrentInstance()
				.getExternalContext().getRequestMap();
		PatternItem currentRow = (PatternItem) requestMap
				.get("materialBrowseVar");
		return currentRow;
	}

	public void rowSelected(ActionEvent event) {

		closeMaterialBrowse(event);
		// set value for input field
		PatternItem selectedMaterial = getSelectedMaterial();
		if (selectedMaterial != null) {
			this.patternName = selectedMaterial.getValue();
			this.version = selectedMaterial.getVersion();
		}
	}

	public void rowSelectedPatternTypeMasterPopup(ActionEvent event) {

		closePatternTypeMasterBrowse(event);
		// set value for input field
		PatternItem selectedMaterial = getSelectedPatternTypeMaster();
		if (selectedMaterial != null) {
			this.patternTypeMaster = selectedMaterial.getValue();
		}
	}

	public void rowSelectedPatternTypePopup(ActionEvent event) {

		closePatternTypeBrowse(event);
		// set value for input field
		PatternItem selectedMaterial = getSelectedPatternType();
		if (selectedMaterial != null) {
			this.patternType = selectedMaterial.getValue();
		}
		if (AttributeValueConstants.BOM_COMPONENT.equals(selectedMaterial
				.getValue())) {
			this.disablePatternTypeMaster = false;
			this.disablePatternTypeValue = false;
		} else if(AttributeValueConstants.ROUTING_STEP.equals(selectedMaterial
				.getValue())){
			this.disablePatternTypeMaster = false;
			this.disablePatternTypeValue = true;
		}else {
			this.disablePatternTypeMaster = true;
			this.disablePatternTypeValue = true;
		}
		if (this.patternCustomDataList != null) {
			for (PatternCustomDataItem customDataItem : this.patternCustomDataList) {
				if (customDataItem.isEditable()) {
					customDataItem.setPatternAttribute("");
					customDataItem.setAttributeValue("");
				}
			}
		}

	}

	public void rowSelectedPatternCustomItemDataTable(ActionEvent event) {

		// set value for input field
		PatternCustomDataItem selectedCustomDataItem = getSelectedCustomDataItem();
		if (selectedCustomDataItem != null) {
			this.currePatternCustomDataItem = selectedCustomDataItem;
		}
	}

	public void rowSelectedPatternTypeValuePopup(ActionEvent event) {

		closePatternTypeValueBrowse(event);
		// set value for input field
		PatternItem selectedMaterial = getSelectedPatternTypeValue();
		if (selectedMaterial != null) {
			this.patternTypeValue = selectedMaterial.getValue();
		}

	}

	public void rowSelectedAttributePanelPopup(ActionEvent event) {
		closeAttributeBrowse(event);
		// set value for input field
		PatternItem selectedAttribute = getSelectedAttribute();
		if (selectedAttribute != null) {
			if ("showAttributeValueBrowse".equals(this.showBrowseName)) {
				this.currePatternCustomDataItem
						.setAttributeValue(selectedAttribute.getValue());
				this.currePatternCustomDataItem.setAttributeValueDb(selectedAttribute.getDbValue());
			} else if ("showPatternAttributeBrowse".equals(this.showBrowseName)) {
				this.currePatternCustomDataItem
						.setPatternAttribute(selectedAttribute.getValue());
				String key = this.patternType + ":"
						+ selectedAttribute.getValue();
				if (this.attributeValuesMap.get(key) == null) {
					this.currePatternCustomDataItem
							.setAttributeValueEnable(true);
				} else {
					if (this.attributeValuesMap.get(key).size() == 0) {
						this.currePatternCustomDataItem
								.setAttributeValueEnable(true);
					} else {
						this.currePatternCustomDataItem
								.setAttributeValueEnable(false);
					}

				}

			}

		}

	}

	private PatternItem getSelectedAttribute() {
		PatternItem selectedMaterialItem = null;
		if (this.patternAttributeList != null) {
			for (int i = 0; i < this.patternAttributeList.size(); i++) {
				PatternItem patternItem = patternAttributeList.get(i);
				if (patternItem != null && patternItem.getSelected()) {
					selectedMaterialItem = patternItem;
					break;
				}
			}
		}
		return selectedMaterialItem;
	}

	private PatternItem getSelectedPatternTypeMaster() {
		PatternItem patternTypeMaster = null;
		if (this.patternTypeMasterList != null) {
			for (int i = 0; i < this.patternTypeMasterList.size(); i++) {
				PatternItem patternItem = patternTypeMasterList.get(i);
				if (patternItem != null && patternItem.getSelected()) {
					patternTypeMaster = patternItem;
					break;
				}
			}
		}
		return patternTypeMaster;
	}

	private PatternItem getSelectedPatternType() {
		PatternItem patternType = null;
		if (this.patternTypeList != null) {
			for (int i = 0; i < this.patternTypeList.size(); i++) {
				PatternItem patternItem = patternTypeList.get(i);
				if (patternItem != null && patternItem.getSelected()) {
					patternType = patternItem;
					break;
				}
			}
		}
		return patternType;
	}

	private PatternCustomDataItem getSelectedCustomDataItem() {
		PatternCustomDataItem patternCustomDataItem = null;
		if (this.patternCustomDataList != null) {
			for (int i = 0; i < this.patternCustomDataList.size(); i++) {
				PatternCustomDataItem patternCustomDataItemTemp = this.patternCustomDataList
						.get(i);
				if (patternCustomDataItemTemp != null
						&& patternCustomDataItemTemp.isSelect()) {
					patternCustomDataItem = patternCustomDataItemTemp;
					break;
				}
			}
		}
		return patternCustomDataItem;
	}

	private PatternItem getSelectedPatternTypeValue() {
		PatternItem patternTypeValue = null;
		if (this.patternTypeValueList != null) {
			for (int i = 0; i < this.patternTypeValueList.size(); i++) {
				PatternItem patternItem = patternTypeValueList.get(i);
				if (patternItem != null && patternItem.getSelected()) {
					patternTypeValue = patternItem;
					break;
				}
			}
		}
		return patternTypeValue;
	}

	private PatternItem getSelectedMaterial() {
		PatternItem selectedMaterialItem = null;
		if (patternList != null) {
			for (int i = 0; i < this.patternList.size(); i++) {
				PatternItem materialItem = patternList.get(i);
				if (materialItem != null && materialItem.getSelected()) {
					selectedMaterialItem = materialItem;
					break;
				}
			}
		}
		return selectedMaterialItem;
	}
	public void activateOnPatternType(ActionEvent event) {
		if (AttributeValueConstants.BOM_COMPONENT.equalsIgnoreCase(this.patternType)
				|| AttributeValueConstants.ROUTING_STEP.equalsIgnoreCase(this.patternType)) {
			this.disablePatternTypeMaster = false;
			
			if(AttributeValueConstants.BOM_COMPONENT.equalsIgnoreCase(this.patternType)){
				this.patternType=AttributeValueConstants.BOM_COMPONENT;
				this.disablePatternTypeValue = false;
			}else{
				this.patternType=AttributeValueConstants.ROUTING_STEP;
			}
		} else {
			this.disablePatternTypeMaster = true;
			this.disablePatternTypeValue = true;
		}
	}
	public void setPatternCustomDataList(
			List<PatternCustomDataItem> patternCustomDataList) {
		this.patternCustomDataList = patternCustomDataList;
	}

	public List<PatternCustomDataItem> getPatternCustomDataList() {
		return patternCustomDataList;
	}

	public String getPatternName() {

		return patternName;
	}

	public void setPatternName(String patternName) {
		this.patternName = patternName;
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

	public String getMessage() {

		return message;
	}

	public void setMessage(String message) {

		this.message = message;
	}

	public List<PatternItem> getPatternList() {
		return this.patternList;
	}
	
	public void onBlurEvent() {
		FacesUtility.addScriptCommand("window.myFunction();");	
		
	}

	public void showMaterialBrowse(ActionEvent event) {
		this.patternList = this.fetchPatternNames(this.patternName);
		this.materialBrowseRendered = true;
	}

	public void valueChanged(ValueChangeEvent event) {
		this.disablePatternTypeMaster = false;
	}

	public void showPatternTypeMasterBrowse(ActionEvent event) {
		this.patternTypeMasterList = this
				.fetchPatternTypeMasterList(this.patternTypeMaster);
		this.renderPatternTypeMasterPopup = true;
	}

	public void showPatternTypeBrowse(ActionEvent event) {
		this.patternTypeList = this.fetchPatternTypes(this.patternType);
		this.renderPatternTypePopup = true;
	}

	public void showPatternTypeValueBrowse(ActionEvent event) {
		this.patternTypeValueList = this
				.fetchPatternTypeValueList(this.patternTypeValue);
		this.renderPatternTypeValuePopup = true;
	}

	public void showPatternAttributeBrowse(ActionEvent event) {
		this.showBrowseName = "showPatternAttributeBrowse";
		String value = null;
		if (((UICommandInputText) event.getComponent()).getValue() != null) {
			value = "" + ((UICommandInputText) event.getComponent()).getValue();
		}
		this.patternAttributeList = this.fetchPatternAttributeList(value);
		this.renderAttributeValuePopup = true;
		Map<String, Object> requestMap = FacesContext.getCurrentInstance()
				.getExternalContext().getRequestMap();
		PatternCustomDataItem currentRow = (PatternCustomDataItem) requestMap
				.get("rows");
		this.currePatternCustomDataItem = currentRow;

	}

	public void showAttributeValueBrowse(ActionEvent event) {
		this.showBrowseName = "showAttributeValueBrowse";
		String value = null;
		if (((UICommandInputText) event.getComponent()).getValue() != null) {
			value = "" + ((UICommandInputText) event.getComponent()).getValue();
		}

		this.renderAttributeValuePopup = true;
		Map<String, Object> requestMap = FacesContext.getCurrentInstance()
				.getExternalContext().getRequestMap();
		PatternCustomDataItem currentRow = (PatternCustomDataItem) requestMap
				.get("rows");
		this.currePatternCustomDataItem = currentRow;
		this.patternAttributeList = this.fetchPatternAttributeValueList(value,
				currentRow.getPatternAttribute());

	}

	public String savePattern() {
		if (StringUtils.isBlank(this.patternName)) {
			// Display error message on GUI
			message = "Pattern Name can not be blank";
			setMessageBar(true, LSMessageType.ERROR);
			return null;
		}
		if (StringUtils.isBlank(this.patternType)) {
			// Display error message on GUI
			message = "Pattern Type can not be blank";
			setMessageBar(true, LSMessageType.ERROR);
			return null;
		}

		if (AttributeValueConstants.BOM_COMPONENT.equals(this.patternType)
				|| AttributeValueConstants.ROUTING_STEP
						.equals(this.patternType)) {
			// Display error message on GUI
			if (StringUtils.isBlank(this.patternTypeMaster)
					|| StringUtils.isBlank(this.patternTypeValue)) {
				message = "Pattern Type master & Pattern Type Value can not be blank";
				setMessageBar(true, LSMessageType.ERROR);
				return null;
			}

		}
		Pattern pattern = this.patternDaoMnanagerImpl.findPattern(patternName);
		boolean isPatternAreadyPresent = false;
		if (pattern == null) {
			pattern = new Pattern();
			pattern.setCreatedBy(user);
			pattern.setCreatedDate(new Date(new java.util.Date().getTime()));
			pattern.setPatternName(this.patternName);
			pattern.setPatternType(this.patternType);
			pattern.setPatternTypeMaster(this.patternTypeMaster);
			pattern.setPatternTypeValue(this.patternTypeValue);
			pattern.setHandle("PatternBO:" + CommonMethods.getSite() + ","
					+ this.patternName);
			message = "Pattern is saved ";
			setMessageBar(true, LSMessageType.INFO);

		} else {
			isPatternAreadyPresent = true;
		}

		int nSequencesToBesaved = 0;
		int errorRecords = 0;
		this.patternDaoMnanagerImpl.createPattern(pattern);
		this.disablePatternType = true;
		Set<Integer> sequenceNo=new HashSet<Integer>();
		for (PatternCustomDataItem customDataItem : this.patternCustomDataList) {
			
			if (customDataItem.isEditable()) {
				customDataItem.setError(false);
				customDataItem.setErrorMessage("");
				
				if (customDataItem.getSequenceNo() != null
						&& customDataItem.getSequenceNo() <= 0) {
					customDataItem.setError(true);
					customDataItem
							.setErrorMessage("Sequence number is not valid");
					errorRecords++;
					continue;
				}
				if (customDataItem.getSequenceNo() == null) {
					customDataItem.setError(true);
					customDataItem
							.setErrorMessage("Sequence number is can not be blank");
					errorRecords++;
					continue;
				}
				if(!sequenceNo.add(customDataItem.getSequenceNo())){
					customDataItem.setError(true);
					customDataItem
							.setErrorMessage("Sequence number should be unique for given pattern");
					errorRecords++;
					continue;
				}
				if (StringUtils.isBlank(customDataItem.getPatternAttribute())) {
					customDataItem.setError(true);
					customDataItem
							.setErrorMessage("Pattern Attribute can not be blank");
					errorRecords++;
					continue;
				}
				PatternSequence patternSequence = new PatternSequence();
				patternSequence.setCreatedBy(user);
				patternSequence.setPatternBo(pattern.getHandle());
				patternSequence.setPatternAttribute(customDataItem
						.getPatternAttribute());
				patternSequence.setPatternValue(customDataItem
						.getAttributeValue());
				patternSequence.setPatternValueType(customDataItem.getAttributeValueDb());
				patternSequence.setSequenceNo(customDataItem.getSequenceNo());
				patternSequence.setHandle("PatternSequenceBO:"
						+ pattern.getHandle() + ","
						+ customDataItem.getSequenceNo());
				patternSequence.setCreatedOn(new Date(new java.util.Date()
						.getTime()));
				patternSequence.setCurrentVersion(customDataItem
						.isCurrentVesion() ? "YES" : "NO");
				if (this.patternDaoMnanagerImpl
						.createPatternSequence(patternSequence)) {
					customDataItem.setEditable(false);
				}
				nSequencesToBesaved++;
			}else{
				sequenceNo.add(customDataItem.getSequenceNo());
			}
		}
		
		if ((!isPatternAreadyPresent) && errorRecords > 0) {
			// Display error message on GUI
			message = message + ", Error in pattern attribute";
			setMessageBar(true, LSMessageType.ERROR);
			return null;
		}
		if ((isPatternAreadyPresent) && errorRecords > 0) {
			// Display error message on GUI
			message = "Error in pattern attributes";
			setMessageBar(true, LSMessageType.ERROR);
			return null;
		}
		if (isPatternAreadyPresent && nSequencesToBesaved == 0) {
			// Display error message on GUI
			message = "No data to save";
			setMessageBar(true, LSMessageType.ERROR);
			setMessageBar(true, LSMessageType.ERROR);
			return null;

		}

		// Make sure you add the table to the list of control updates so that
		// the new model value will be shown on the UI.
		UIComponent tablePanel = findComponent(FacesUtility.getFacesContext()
				.getViewRoot(), "materialCustomDataForm:displayPanel");
		if (tablePanel != null) {
			FacesUtility.addControlUpdate(tablePanel);
		}
		return null;

	}

	public void closeMaterialBrowse(ActionEvent event) {

		this.materialBrowseRendered = false;
	}

	public void closePatternTypeMasterBrowse(ActionEvent event) {

		this.renderPatternTypeMasterPopup = false;
	}

	public void closePatternTypeBrowse(ActionEvent event) {

		this.renderPatternTypePopup = false;
	}

	public void closePatternTypeValueBrowse(ActionEvent event) {

		this.renderPatternTypeValuePopup = false;
	}

	public void closeAttributeBrowse(ActionEvent event) {

		this.renderAttributeValuePopup = false;
	}

	public boolean isDisablePatternTypeMaster() {
		return (disablePatternTypeMaster);
	}

	public void setDisablePatternTypeMaster(boolean disablePatternTypeMaster) {
		this.disablePatternTypeMaster = disablePatternTypeMaster;
	}

	public boolean isDisablePatternTypeValue() {
		return (disablePatternTypeValue);
	}

	public void setDisablePatternTypeValue(boolean disablePatternTypeValue) {
		this.disablePatternTypeValue = disablePatternTypeValue;
	}

	public boolean isRenderAttributeValuePopup() {
		return renderAttributeValuePopup;
	}

	public void setRenderAttributeValuePopup(boolean renderAttributeValuePopup) {
		this.renderAttributeValuePopup = renderAttributeValuePopup;
	}

	public void processAction(ActionEvent arg0) throws AbortProcessingException {
		this.renderAttributeValuePopup = true;
	}

	public List<SelectItem> getPatternTypes() {
		return patternTypes;
	}

	public void setPatternTypes(List<SelectItem> patternTypes) {
		this.patternTypes = patternTypes;
	}

	public String getPatternType() {
		return patternType;
	}

	public void setPatternType(String patternType) {
		this.patternType = patternType;
	}

	public String getPatternTypeMaster() {
		return patternTypeMaster;
	}

	public void setPatternTypeMaster(String patternTypeMaster) {
		this.patternTypeMaster = patternTypeMaster;
	}

	public String getPatternTypeValue() {
		return patternTypeValue;
	}

	public void setPatternTypeValue(String patternTypeValue) {
		this.patternTypeValue = patternTypeValue;
	}

	public List<PatternItem> getPatternAttributeList() {
		return patternAttributeList;
	}

	public void setPatternAttributeList(List<PatternItem> patternAttributeList) {
		this.patternAttributeList = patternAttributeList;
	}

	public String getColumnHeading() {
		if (PATTERN_ATTRIBUTE.equals(this.showBrowseName)) {
			return PATTERN_ATTRIBUTE_HEADING;
		}
		if (ATTRIBUTE_VALUE.equals(this.showBrowseName)) {
			return ATTRIBUTE_VALUE_HEADING;
		}
		return "";
	}

	public List<PatternItem> getPatternTypeMasterList() {
		return patternTypeMasterList;
	}

	public void setPatternTypeMasterList(List<PatternItem> patternTypeMasterList) {
		this.patternTypeMasterList = patternTypeMasterList;
	}

	public List<PatternItem> getPatternTypeValueList() {
		return patternTypeValueList;
	}

	public void setPatternTypeValueList(List<PatternItem> patternTypeValueList) {
		this.patternTypeValueList = patternTypeValueList;
	}

	/**
	 * method fetches pattern list
	 * 
	 * @return
	 */
	public List<SelectItem> getPatternTypesList() {
		List<SelectItem> patternTypes = new ArrayList<SelectItem>();
		patternTypes.add(new SelectItem("Material", "Material"));
		patternTypes.add(new SelectItem("Shop Order", "Shop Order"));
		patternTypes.add(new SelectItem("BOM", "BOM"));
		patternTypes.add(new SelectItem("BOM Component", "BOM Component"));
		patternTypes.add(new SelectItem("Routing", "Routing"));
		patternTypes.add(new SelectItem("Routing Step", "Routing Step"));
		return patternTypes;

	}

	public boolean isRenderPatternTypeMasterPopup() {
		return renderPatternTypeMasterPopup;
	}

	public void setRenderPatternTypeMasterPopup(
			boolean renderPatternTypeMasterPopup) {
		this.renderPatternTypeMasterPopup = renderPatternTypeMasterPopup;
	}

	public boolean isRenderPatternTypeValuePopup() {
		return renderPatternTypeValuePopup;
	}

	public List<PatternItem> getPatternTypeList() {
		return patternTypeList;
	}

	public void setPatternTypeList(List<PatternItem> patternTypeList) {
		this.patternTypeList = patternTypeList;
	}

	public boolean isRenderPatternTypePopup() {
		return renderPatternTypePopup;
	}

	public void setRenderPatternTypePopup(boolean renderPatternTypePopup) {
		this.renderPatternTypePopup = renderPatternTypePopup;
	}

	public void setRenderPatternTypeValuePopup(
			boolean renderPatternTypeValuePopup) {
		this.renderPatternTypeValuePopup = renderPatternTypeValuePopup;
	}

	public void setPatternList(List<PatternItem> patternList) {
		this.patternList = patternList;
	}

	public boolean isDisablePatternType() {
		return disablePatternType;
	}

	public void setDisablePatternType(boolean disablePatternType) {
		this.disablePatternType = disablePatternType;
	}

	public List<PatternItem> fetchPatternTypes(String patternName) {
		List<PatternItem> patternItems = new ArrayList<PatternItem>();
		PatternItem item1 = new PatternItem();
		item1.setValue(AttributeValueConstants.MATERIAL);
		patternItems.add(item1);
		PatternItem item2 = new PatternItem();
		item2.setValue(AttributeValueConstants.SHOP_ORDER);
		patternItems.add(item2);
		PatternItem item3 = new PatternItem();
		item3.setValue(AttributeValueConstants.BOM);
		patternItems.add(item3);
		PatternItem item4 = new PatternItem();
		item4.setValue(AttributeValueConstants.BOM_COMPONENT);
		patternItems.add(item4);
		PatternItem item5 = new PatternItem();
		item5.setValue(AttributeValueConstants.ROUTING);
		patternItems.add(item5);
		PatternItem item6 = new PatternItem();
		item6.setValue(AttributeValueConstants.ROUTING_STEP);
		patternItems.add(item6);
		return patternItems;

	}

	public List<PatternItem> fetchPatternNames(String patternName) {
		List<PatternItem> patternItems = this.patternDaoMnanagerImpl
				.findAllPatterns(patternName);
		return patternItems;
	}

	public List<PatternItem> fetchPatternTypeMasterList(String patternTypeMaster) {
		List<PatternItem> patternItems=null;
		if(AttributeValueConstants.BOM_COMPONENT.equalsIgnoreCase(this.patternType)){
			patternItems = this.patternDaoMnanagerImpl
			.findAllBOM(patternTypeMaster);
		}else if(AttributeValueConstants.ROUTING_STEP.equalsIgnoreCase(this.patternType)){
			patternItems = this.patternDaoMnanagerImpl
			.findAllRouter(patternTypeMaster,CommonMethods.getSite());
		}
		
		return patternItems;
	}

	public List<PatternItem> fetchPatternTypeValueList(String patternValue) {
		List<PatternItem> patternItems = new ArrayList<PatternItem>();
		PatternItem patternItem = null;
		for (PatternItem item : this.patternTypeMasterList) {
			if (item.getSelected()|| item.getValue().equals(this.patternTypeMaster)) {
				item.setSelected(true);
				patternItem = item;
				break;
			}
		}
		if(patternItem==null){
			return patternItems;
		}
		try {
			if (AttributeValueConstants.BOM_COMPONENT.equals(this.patternType)) {
				Collection<BomComponentProductionConfiguration> bomComponents = this.bomConfigurationServiceInterface
						.findAllBOMComponents(new BOMComponentRequest(patternItem
								.getVersion(), new BigDecimal(1)));
				for (BomComponentProductionConfiguration bomComponentProductionConfiguration : bomComponents) {
					patternItems
							.add(new PatternItem(
									itemConfigurationServiceInterface
											.findItemKeyDataByRef(
													new ObjectReference(
															bomComponentProductionConfiguration
																	.getComponentRef()))
											.getItem()));
				}
			} else if (AttributeValueConstants.ROUTING_STEP.equals(this.patternType)) {
//				FindRouterStepsRequest findRouterStepsRequest=new FindRouterStepsRequest();
//				findRouterStepsRequest.setRevision(patternItem.getRouterRef().getRevision());
//				findRouterStepsRequest.setRouter(patternItem.getRouterRef().getRouter());
//				findRouterStepsRequest.setRouterType(patternItem.getRouterRef().getRouterType());
//				Collection<RouterStepConfiguration> routingSteps = this.routerConfigurationServiceInterface.findRouterSteps(findRouterStepsRequest);
//					for (RouterStepConfiguration routerStepConfiguration : routingSteps) {
//					patternItems
//							.add(new PatternItem(routerStepConfiguration.getDescription()));
//				}
				patternItems=this.patternDaoMnanagerImpl.findAllRoutingSteps(patternItem.getRouterRef().getHandle(), this.patternTypeValue);
			}

		} catch (BOMConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return patternItems;
	}

	public List<PatternItem> fetchPatternAttributeList(String attributeVal) {
		List<PatternItem> patternItems = this.patternAttributeMap
				.get(this.patternType);
		return patternItems;
	}

	public List<PatternItem> fetchPatternAttributeValueList(
			String attributrVal, String paternAttr) {
		List<PatternItem> patternItems = this.attributeValuesMap
				.get(this.patternType + ":" + paternAttr);
		return patternItems;

	}

	public List<PatternCustomDataItem> fetchPatternDataList() {
		List<PatternCustomDataItem> customDataItems = new ArrayList<PatternCustomDataItem>();
		Pattern pattern = this.patternDaoMnanagerImpl
				.findPattern(this.patternName);
		if(pattern == null){
			return customDataItems;
		}
		this.patternType = pattern.getPatternType();
		this.patternTypeMaster = pattern.getPatternTypeMaster() == null ? ""
				: pattern.getPatternTypeMaster();
		this.patternTypeValue = pattern.getPatternTypeValue() == null ? ""
				: pattern.getPatternTypeValue();
		this.disablePatternType = true;
		for (PatternSequence patternSequence : this.patternDaoMnanagerImpl
				.findPatternSequence(pattern.getHandle())) {
			PatternCustomDataItem dataItem = new PatternCustomDataItem();
			dataItem.setAttributeValue(patternSequence.getPatternValue());
			dataItem.setPatternAttribute(patternSequence.getPatternAttribute());
			dataItem.setSequenceNo(patternSequence.getSequenceNo());
			dataItem
					.setCurrentVesion(patternSequence.getCurrentVersion() == "YES" ? true
							: false);
			dataItem.setEditable(false);
			customDataItems.add(dataItem);
		}
		return customDataItems;

	}

}
