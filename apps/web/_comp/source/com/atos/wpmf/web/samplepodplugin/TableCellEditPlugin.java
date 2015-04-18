package com.atos.wpmf.web.samplepodplugin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.el.ValueExpression;
import javax.faces.component.EditableValueHolder;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ActionListener;

import com.sap.me.common.AttributeValue;
import com.sap.me.extension.Services;
import com.sap.me.frame.domain.DomainServiceFactory;
import com.sap.me.frame.domain.DomainServiceInterface;
import com.sap.me.frame.service.CommonMethods;
import com.sap.me.production.domain.SfcDO;
import com.sap.me.production.podclient.BasePodPlugin;
import com.sap.me.production.podclient.SfcChangeEvent;
import com.sap.me.production.podclient.SfcChangeListenerInterface;
import com.sap.me.production.podclient.SfcSelection;
import com.sap.me.status.StatusServiceInterface;
import com.sap.me.wpmf.BrowseSelectionEventListener;
import com.sap.me.wpmf.IconGroup;
import com.sap.me.wpmf.TableConfigurator;
import com.sap.me.wpmf.browse.BrowseListener;
import com.sap.me.wpmf.util.FacesUtility;
import com.sap.ui.faces.component.UISelectItem;
import com.sap.ui.faces.component.html.HtmlSelectOneMenu;
import com.sap.ui.faces.component.sap.HtmlCommandBooleanCheckbox;
import com.sap.ui.faces.component.sap.UICommandInputText;
import com.visiprise.globalization.DateGlobalizationServiceInterface;
import com.visiprise.globalization.GlobalizationService;
import com.visiprise.globalization.KnownGlobalizationServices;

public class TableCellEditPlugin extends BasePodPlugin implements ActionListener, BrowseSelectionEventListener {
	private static final long serialVersionUID = 1L;
	private List<CellEditDTO> cellEditList;
	private TableConfigurator configBean = null;
	private UIComponent currentBrowseComponent = null;
	int curRowIndex = -99;
	private CellEditDTO currentRow = null;
	DateGlobalizationServiceInterface dateService = GlobalizationService.getUserService(KnownGlobalizationServices.DATE);
	DomainServiceInterface<SfcDO> sfcDOService = DomainServiceFactory.getServiceByClass(SfcDO.class);
	StatusServiceInterface statusService = Services.getService("com.sap.me.status", "StatusService");

	@Override
	public void beforeLoad() throws Exception {

		TableConfigurator table = (TableConfigurator) FacesUtility.resolveExpression("#{tableCellEditConfigurator}");
		setConfigBean(table);
		// Build data to display
		SfcDO sfcDOExample = new SfcDO();
		sfcDOExample.setSite(CommonMethods.getSite());
		Collection<SfcDO> sfcList = sfcDOService.readByExample(sfcDOExample);
		this.cellEditList = new ArrayList<CellEditDTO>();
		int i = 0;
		for (SfcDO sfcDO : sfcList) {
			CellEditDTO dto = new CellEditDTO();
			dto.setSfc(sfcDO.getSfc());
			dto.setQty(sfcDO.getQty());
			String shopOrder = parseHandle(sfcDO.getShopOrderRef(), 2);
			dto.setShopOrder(shopOrder);
			String item = parseHandle(sfcDO.getItemRef(), 2);
			dto.setMaterial(item);
			dto.setActive(true);
			dto.setStatus("");
			dto.setSfcData("");
			if (i % 2 == 0) {
				dto.setLogComment(true);
				dto.setCreateMessage(true);
			} else {
				dto.setCreateMessage(true);
			}
			cellEditList.add(dto);
			i++;
		}
	}

	public TableCellEditPlugin() {
		super();
		// Adds the implemented listeners to the framework
		getPluginEventManager().addPluginListeners(this.getClass());
		savePluginInSession();
		complete();
	}

	private TableCellEditPlugin findPluginInSession() {
		TableCellEditPlugin sessionPlugin = (TableCellEditPlugin) FacesUtility.getSessionMapValue("tableCellEditPlugin");
		return sessionPlugin;
	}

	private void savePluginInSession() {
		TableCellEditPlugin sessionPlugin = findPluginInSession();
		if (this != sessionPlugin) {
			FacesUtility.setSessionMapValue("tableCellEditPlugin", this);
		}
	}
	
	/**
	 * @return the configBean
	 */
	public TableConfigurator getConfigBean() {
		return configBean;
	}

	/**
	 * @param configBean
	 *            the configBean to set
	 */
	public void setConfigBean(TableConfigurator config) {

		if (config == null) {
			configBean = new TableConfigurator();
		} else {
			configBean = config;
		}
		configBean.setListName(null);
		configBean.setAllowSelections(false);
		String[] cnames = { "SFC", "SHOP_ORDER", "QTY", "ACTIVE", "ICON_HOLDER", "MATERIAL", "STATUS", "SFC_DATA" };
		configBean.setListColumnNames(cnames);
		Map<String, String> bindings = new HashMap<String, String>();
		bindings.put("SFC", "sfc;cellEdit.SFC.LABEL");
		bindings.put("SHOP_ORDER", "shopOrder;cellEdit.shopOrder.LABEL");
		bindings.put("MATERIAL", "material;cellEdit.material.LABEL");
		bindings.put("QTY", "qty;cellEdit.qty.LABEL");
		bindings.put("ACTIVE", "active;cellEdit.active.LABEL");
		bindings.put("ICON_HOLDER", "iconHolder;cellEdit.iconHolder.LABEL");
		bindings.put("STATUS", "status;cellEdit.status.LABEL");
		bindings.put("SFC_DATA", "sfcData;cellEdit.sfcData.LABEL");

		configBean.setColumnBindings(bindings);
		configBean.setColumnWidth("SFC", "15em");
		configBean.setColumnWidth("SHOP_ORDER", "10em");
		configBean.setColumnWidth("MATERIAL", "10em");
		configBean.setColumnWidth("QTY", "3em");
		configBean.setColumnWidth("ACTIVE", "3em");
		configBean.setColumnWidth("ICON_HOLDER", "3em");
		configBean.setColumnWidth("STATUS", "13em");
		configBean.setColumnWidth("SFC_DATA", "10em");

		configBean.setColumnPosition("STATUS", 3);
		configBean.setColumnPosition("SFC_DATA", 4);
		configBean.setColumnPosition("MATERIAL", 5);

		ArrayList<String> editCols = new ArrayList<String>();
		// make these columns editable
		editCols.add("MATERIAL");
		editCols.add("SHOP_ORDER");
		editCols.add("QTY");
		editCols.add("ACTIVE");
		editCols.add("STATUS");
		editCols.add("SFC_DATA");

		configBean.setEditableColumns(editCols);

		// Lets try a check box for the boolean 'Active' column
		configBean.setCellEditor("ACTIVE", new HtmlCommandBooleanCheckbox());

		// let's add event handlers to these.
		UICommandInputText qtyCell = new UICommandInputText();
		qtyCell.setSubmitOnChange(true);
		configBean.setCellEditor("QTY", qtyCell);

		UICommandInputText shopOrderCell = new UICommandInputText();
		shopOrderCell.setSubmitOnChange(false);
		configBean.setCellEditor("SHOP_ORDER", shopOrderCell);

		// this column is setup with the F4Lookup active; that is the you can do browse on it.
		configBean.setCellEditor("MATERIAL", new UICommandInputText());
		configureCellEditor("MATERIAL");

		// this column is setup with the F4Lookup active; that is the you can do browse on it.
		UICommandInputText customBrowse = new UICommandInputText();
		configBean.setCellEditor("SFC_DATA", customBrowse);
		configureCellEditor("SFC_DATA");
        // build status combo-box
		String[] statusItems = { "", "Done", "New", "InQueue" };
		HtmlSelectOneMenu statusComp = new HtmlSelectOneMenu();
		for (int j = 0; j < statusItems.length; j++) {
			UISelectItem selectItem = new UISelectItem();
			selectItem = new UISelectItem();
			selectItem.setItemValue(statusItems[j]);
			selectItem.setItemLabel(statusItems[j]);
			statusComp.getChildren().add(selectItem);
		}
		configBean.setCellEditor("STATUS", statusComp);

		// use the IconGroup as the cell renderer for the multi icon column.
		IconGroup iconGroup = new IconGroup();
		// Add an Icon to the group
		// public void addIcon(String iconName,
		// String imageUrl,
		// String alt,
		// String actionExpression,
		// String renderExpression)
		// true or false -createMessage property in CellEditDTO
		String renderExpression = "#{row.createMessage}";
		iconGroup.addIcon("SDE", "/com/atos/icons/icon_create_message.gif", "Create Message", "#{tableCellEditPlugin.processCreateMessage}", renderExpression);
		// true or false - logComment property in CellEditDTO
		renderExpression = "#{row.logComment}";
		iconGroup.addIcon("SPH", "/com/atos/icons/icon_log_comment.gif", "Log Comment", "#{tableCellEditPlugin.processLogComment}", renderExpression);
		configBean.setCellRenderer("ICON_HOLDER", iconGroup);
		configBean.setAllowSelections(true);
		configBean.setMultiSelectType(false);
	}

	public Collection<CellEditDTO> getCellEditList() {
		return cellEditList;
	}

	public void processCreateMessage() {
		// Convenience method for Plugin's to execute a button activity.
		executePluginButtonActivity("CREATE_MESS_PLUGIN");
	}

	public void processLogComment() {
		CellEditDTO rowData = (CellEditDTO) configBean.getTable().getRowData();
		List<SfcSelection> sfcsToProcess = new ArrayList<SfcSelection>();
		SfcSelection sfcSelect = new SfcSelection();
		sfcSelect.setInputId(rowData.getSfc());
		sfcsToProcess.add(sfcSelect);
		// Update regular model and fire event
		getPodSelectionModel().setSfcs(sfcsToProcess);
		SfcChangeEvent sfcChangeEvent = new SfcChangeEvent(this);
		// com.sap.me.production.podclient in Java Doc
		fireEvent(sfcChangeEvent, SfcChangeListenerInterface.class, "processSfcChange");
		// Convenience method for Plugin's to execute a button activity.
		executePluginButtonActivity("LOG_COMMENT");
	}

	public void closePlugin() {
		// Closes the current plugin, clears messages in the global area
		closeCurrentPlugin();
		FacesUtility.setSessionMapValue("tableCellEditPlugin", null);
		FacesUtility.setSessionMapValue("tableCellEditConfigurator", null);

	}

	/**
	 * Call back method executed by the browse dialog after it is closed to notify that it is closed. This is where we need to update the model with the new browse value, otherwise it reverts back to what the client is sending across. This also ensures that the focus is set back on the browse component.
	 */
	public void processBrowseCallBack() {
		Object val = this.getLastBrowseFieldValue();
		ValueExpression valexpr = ((UIComponent) currentBrowseComponent).getValueExpression("value");
		// set value selected in browse to table's cell
		valexpr.setValue(FacesUtility.getFacesContext().getELContext(), val);
		// Make sure you add the table to the list of control updates so that
		// the new model value will be shown on the UI.
		FacesUtility.addControlUpdate(configBean.getTable());

		// Steps to set focus back to cell
		// example templateForm:tcArea:threepanelh:areaB:EditCellView:test_editcell_list_table:j_id172
		String clientId = currentBrowseComponent.getClientId(FacesContext.getCurrentInstance());
		// example j_id172
		String id = currentBrowseComponent.getId();
		// Compiles the given regular expression into a pattern.
		Pattern pat = Pattern.compile(id);
		// Creates a matcher that will match the given input against this pattern.
		Matcher mat = pat.matcher(clientId);
		// this.curRowIndex + ":" + id = 0:j_id172
		// clientId = templateForm:tcArea:threepanelh:areaB:EditCellView:test_editcell_list_table:0:j_id172
		clientId = mat.replaceAll(this.curRowIndex + ":" + id);
		// Will set focus on a component with the input client ID (full component ID)
		setComponentFocus(clientId);
		FacesUtility.removeSessionMapValue("row");
	}

	private void configureCellEditor(String colName) {
		UIComponent editCell = configBean.getCellEditor(colName);
		if ("MATERIAL".equals(colName) && editCell instanceof UICommandInputText) {
			UICommandInputText inputBrowse = (UICommandInputText) editCell;
			inputBrowse.setSubmitOnFieldHelp(true);
			inputBrowse.setSubmitOnChange(false);
			// setup the browse attributes as any other standard browse.
			inputBrowse.getAttributes().put("browseable", "true");
			inputBrowse.getAttributes().put("browseId", "ITEM");
			inputBrowse.getAttributes().put("browseCustom", "standard");
			// this is the call back that the browse plugin will use to call on this plugin. (important).
			inputBrowse.getAttributes().put("browseCallBack", "tableCellEditPlugin.processBrowseCallBack");
			inputBrowse.getAttributes().put("legacyBrowse", "true");
			inputBrowse.getAttributes().put("browseSelectionModel", "single");
			inputBrowse.getAttributes().put("browseDialogWidth", "25em");
			inputBrowse.getAttributes().put("browseDialogHeight", "20em");
			inputBrowse.getAttributes().put("upperCase", "true");
			inputBrowse.addActionListener(this);
		}

		if ("SFC_DATA".equals(colName) && editCell instanceof UICommandInputText) {
			UICommandInputText inputBrowse = (UICommandInputText) editCell;
			inputBrowse.setSubmitOnFieldHelp(true);
			inputBrowse.setSubmitOnChange(false);
			//This is used to define the plugin bean name that is requesting a browse to be displayed
			inputBrowse.getAttributes().put("REQUESTING_PLUGIN_NAME", "tableCellEditPlugin");
			//This is used to define a JSF method expression name that returns the browse criteria
			inputBrowse.getAttributes().put("SEARCH_METHOD_NAME", "tableCellEditPlugin.getCriteria");
			//This is used to define the back end module id the browse service. This is required
			inputBrowse.getAttributes().put("SERVICE_MODULE", "com.vendorID.service");
			//This is used to define the back end service name. This is required
			inputBrowse.getAttributes().put("SERVICE_NAME", "CustomSfcDataBrowseService");
			//This is used to define the browse plugin bean name. This is only used for custom browses. This is optional
			inputBrowse.getAttributes().put("BROWSE_PLUGIN_NAME", "customBrowsePlugin");
			//This defines if the browse supports a multiple selection model
			inputBrowse.getAttributes().put("IS_MULTI_SELECT", "false");
			// this is the call back that the browse plugin will use to call on this plugin. (important).
			inputBrowse.getAttributes().put("BROWSE_CALLBACK", "tableCellEditPlugin.processBrowseCallBack");
			//The browse window width in px or em.This is optional and If omitted, a default value is used.
			inputBrowse.getAttributes().put("BROWSE_DIALOG_WIDTH", "25em");
			//The browse window height in px or em. This is optional and If omitted, a default value is used.
			inputBrowse.getAttributes().put("BROWSE_DIALOG_HEIGHT", "20em");
			inputBrowse.getAttributes().put("upperCase", "true");
			inputBrowse.addActionListener(new BrowseListener());
		}

	}

	// Search method for custom SFC Data browse
	//The method signature should be:
	//public List<AttributeValue> anyMethodName( EditableValueHolder comp)
	public List<AttributeValue> getCriteria(EditableValueHolder comp) {
		currentRow = (CellEditDTO) configBean.getTable().getRowData();
		FacesUtility.setSessionMapValue("row", currentRow);
		curRowIndex = configBean.getTable().getRowIndex();
		currentBrowseComponent = (UIComponent) comp;

		String site = CommonMethods.getSite();
		String sfcRef = buildSFCBOHandle(site, currentRow.getSfc());
		String primSrchVal = (String) comp.getValue();
		ArrayList<AttributeValue> result = new ArrayList<AttributeValue>();
		AttributeValue attValue = new AttributeValue("attribute", primSrchVal);
		result.add(attValue);
		attValue = new AttributeValue("sfcRef", sfcRef);
		result.add(attValue);
		return result;
	}

	// action handler for core material Browse
	public void processAction(ActionEvent event) throws AbortProcessingException {
		// Return the source UIComponent that sent this event.
		currentBrowseComponent = event.getComponent();
		// Return the data object representing the data for the currently selected row index
		currentRow = (CellEditDTO) configBean.getTable().getRowData();
		FacesUtility.setSessionMapValue("row", currentRow);
		// Return the zero-relative index of the currently selected row
		curRowIndex = configBean.getTable().getRowIndex();
		// Sub class plugins should call the super class Plugin's method to finally invoke the BrowseDialog event.
		super.browseActionListener(event);
		// Signal the JavaServer faces implementation that, as soon as the current phase of the request processing lifecycle has been completed,
		// control should be passed to the Render Response phase, bypassing any phases that have not been executed yet
		FacesUtility.getFacesContext().renderResponse();

	}

	private String parseHandle(String input, int comp) {
		ArrayList<String> list = new ArrayList<String>();
		StringTokenizer t = new StringTokenizer(input, ":,");
		// Get all of the pieces
		while (t.hasMoreTokens()) {
			// Add them to a temp list
			list.add(t.nextToken());
		}
		return (String) list.get(comp);
	}

	public String buildSFCBOHandle(String site, String sfc) {
		StringBuffer sb = new StringBuffer();
		sb.append("SFCBO").append(':').append(site).append(',');
		sb.append(sfc);
		return sb.toString();
	}

}
