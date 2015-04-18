package com.atos.kitting;

import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.component.UIComponent;
import javax.faces.component.html.HtmlOutputText;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ActionListener;
import javax.faces.model.SelectItem;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import com.atos.kittingEJB.KittingAssignmentScreenInterface;
import com.atos.kittingEJB.KittingDefinitionData;
import com.atos.kittingEJB.KittingDefinitionInterface;
import com.atos.kittingEJB.MaterialCustomDataItem;
import com.atos.kittingEJB.MaterialItem;
import com.sap.me.frame.SystemBase;
import com.sap.me.frame.domain.BusinessException;
import com.sap.me.frame.service.CommonMethods;
import com.sap.me.production.podclient.BasePodPlugin;
import com.sap.me.wpmf.util.FacesUtility;
import com.sap.tc.ls.api.enumerations.LSMessageType;


public class KitDefinitionScreenConttroller extends BasePodPlugin implements
		ActionListener {

	private static final long serialVersionUID = 1L;
	private String kitno;
	private String kittype;
	private String status;
	private boolean disableKitno;
	private boolean disableKittype;
	private boolean disableStatus;
	private String message;
	private int dataFound;
	private Integer kitNoFromTable;
	private String site = CommonMethods.getSite();
	private String user = CommonMethods.getUserId();
	private List<MaterialCustomDataItem> materialCustomDataList;
	private Boolean kitNumberBrowseRendered;
	private List<MaterialItem> kitNoList = new ArrayList<MaterialItem>();
	private Boolean disableKitNUMBER;
	private boolean disableBrowsePopup;
	private List<SelectItem> kitStatus;
	private List<SelectItem> kitStatusListSelectItem = new ArrayList<SelectItem>();
	private List<SelectItem> kitType;
	private List<SelectItem> kitTypeListSelectItem = new ArrayList<SelectItem>();
	private boolean renderConfirmationPopup;
	private final SystemBase dbBase = SystemBase
			.createSystemBase("jdbc/jts/wipPool");

	private Connection getConnection() {
		Connection con = null;
		con = dbBase.getDBConnection();
		return con;
	}

	@PostConstruct
	public void init()
	{
		try
		{
		Context ctx = new InitialContext();
		KittingDefinitionInterface kittingEjb = (KittingDefinitionInterface) ctx
				.lookup("ejb:/appName=atos.com/apps~ear,jarName=atos.com~apps~ejb.jar, beanName=KittingDefinitionControllerBean, interfaceName=com.atos.kittingEJB.KittingDefinitionInterface");
		this.kitStatusListSelectItem = kittingEjb.kitStatusBrowse();
		this.kitTypeListSelectItem = kittingEjb.kitTypeBrowse();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}

	public void clear() {
		boolean isKitno = this.disableKitno;
		boolean isKittype = this.disableKittype;
		boolean isStatus = this.disableStatus;
		FacesUtility.removeSessionMapValue("kitDefinitionScreenControllerBean");
		this.message = null;
		setMessageBar(false, LSMessageType.ERROR);
		setMessageBar(false, LSMessageType.INFO);
		this.disableKitno = isKitno;
		this.disableKittype = isKittype;
		this.disableStatus = isStatus;
		UIComponent tablePanel = findComponent(FacesUtility.getFacesContext()
				.getViewRoot(), "kittingDefinitionForm:displayPanel");
		if (tablePanel != null) {
			FacesUtility.addControlUpdate(tablePanel);
		}
	}

	
	
	
	
	public void readMaterialData() throws NamingException
	{
		this.message = null;
		setMessageBar(false, LSMessageType.ERROR);
		setMessageBar(false, LSMessageType.INFO);
		if ((this.kitno != null) && (!"".equals(this.kitno))) {
			materialCustomDataList = new ArrayList<MaterialCustomDataItem>();
			try
			{
				Context ctx = new InitialContext();
				KittingDefinitionInterface kittingEjb = (KittingDefinitionInterface) ctx
						.lookup("ejb:/appName=atos.com/apps~ear,jarName=atos.com~apps~ejb.jar, beanName=KittingDefinitionControllerBean, interfaceName=com.atos.kittingEJB.KittingDefinitionInterface");
				List<KittingDefinitionData> kittinDataList =kittingEjb.readCustomData(this.kitno);
				for (KittingDefinitionData kittingDefinitionData : kittinDataList) {
				MaterialCustomDataItem materialCustomDataItem = new MaterialCustomDataItem();
				materialCustomDataItem.setKitno(kittingDefinitionData.getKitno());
				materialCustomDataItem
						.setKitnoAltBeforeUpdate(kittingDefinitionData
								.getKitno());
				materialCustomDataItem.setKittype(kittingDefinitionData
						.getKittype());
				materialCustomDataItem.setStatus(kittingDefinitionData
						.getStatus());
				materialCustomDataList.add(materialCustomDataItem);
				this.kitno = materialCustomDataItem.getKitno();
				this.kittype = materialCustomDataItem.getKittype();
				this.status = materialCustomDataItem.getStatus();
			}
		} 
			catch(Exception e)
			{
				message = FacesUtility
				.getLocaleSpecificText(e.getMessage());
				setMessageBar(true, LSMessageType.ERROR);
			}
		}
		
		else 
		{
			message = FacesUtility
					.getLocaleSpecificText(" Select Kit Number to retrieve data .");
			setMessageBar(true, LSMessageType.ERROR);
		}

		if (materialCustomDataList.size() == 0) {
			this.kitno = null;
			this.kittype = null;
			this.status = null;
			message = FacesUtility
					.getLocaleSpecificText(" Your query returned no data .");
			setMessageBar(true, LSMessageType.ERROR);
		}
	}
	

	
	
	public void saveMaterialData(ActionEvent event) throws BusinessException,
			ParseException, NamingException 
		{
		Context ctx = new InitialContext();
		KittingDefinitionInterface kittingEjb = (KittingDefinitionInterface) ctx
				.lookup("ejb:/appName=atos.com/apps~ear,jarName=atos.com~apps~ejb.jar, beanName=KittingDefinitionControllerBean, interfaceName=com.atos.kittingEJB.KittingDefinitionInterface");
		boolean successfullyDone = false;
		if (this.kitno != null && !"".equals(this.kitno)
				&& this.kittype != null && !"".equals(this.kittype)
				&& this.status != null && !"".equals(this.status)) {
			String datafound = kittingEjb.getAllKitNoFromTable(this.kitno);
			if (datafound != null && !"".equals(datafound)) 
			{
				this.renderConfirmationPopup = true;
			} 
			else {
				kittingEjb.insertData(this.kitno, this.kittype, this.status);
				successfullyDone = true;
				if (successfullyDone == true) {
					message = FacesUtility
							.getLocaleSpecificText(" Data loaded for Kit Number  "
									+ this.kitno+ " .");
					setMessageBar(true, LSMessageType.INFO);
				}
			}
		} else {
			message = FacesUtility
					.getLocaleSpecificText(" Provide mantatory fields to insert data .");
			setMessageBar(true, LSMessageType.ERROR);
		}
		UIComponent tablePanel = findComponent(FacesUtility.getFacesContext()
				.getViewRoot(), "kittingDefinitionForm:displayPanel");
		if (tablePanel != null) {
			FacesUtility.addControlUpdate(tablePanel);
		}
		if ("".equals(this.message)) {
			// refresh message bar
			setMessageBar(false, LSMessageType.INFO);
		}
	}

	
	public void setMessageBar(boolean render, LSMessageType messageType) {
		HtmlOutputText messageBarInfo = (HtmlOutputText) findComponent(FacesUtility
				.getFacesContext().getViewRoot(),
				"kittingDefinitionForm:messageBar");
		HtmlOutputText messageBarError = (HtmlOutputText) findComponent(FacesUtility
				.getFacesContext().getViewRoot(),
				"kittingDefinitionForm:messageBar2");
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
				"kittingDefinitionForm:fieldButtonPanel");
		if (fieldButtonPanel != null) {
			FacesUtility.addControlUpdate(fieldButtonPanel);
		}
	}


	private MaterialItem getSelectedKitNo() {
		MaterialItem selectedMaterialItem = null;
		if (kitNoList != null) {
			for (int i = 0; i < this.kitNoList.size(); i++) {
				MaterialItem materialItem = kitNoList.get(i);
				if (materialItem != null && materialItem.getSelected()) {
					selectedMaterialItem = materialItem;
					break;
				}

			}
		}
		return selectedMaterialItem;
	}

	

	public void showKitNumberBrowse(ActionEvent event) throws NamingException {
		this.kitNumberBrowseRendered = true;
		try 
		{
			Context ctx = new InitialContext();
			KittingDefinitionInterface kittingEjb = (KittingDefinitionInterface) ctx
					.lookup("ejb:/appName=atos.com/apps~ear,jarName=atos.com~apps~ejb.jar, beanName=KittingDefinitionControllerBean, interfaceName=com.atos.kittingEJB.KittingDefinitionInterface");
			// this.productionLineList =
			// findAllWorkCenters(this.productionLine);
			this.kitNoList = kittingEjb.kitNumberBrowse(this.kitno);
		} catch (BusinessException businessException) {
			message = FacesUtility
					.getLocaleSpecificText(" Business exception while finding Kit Number .");
			setMessageBar(true, LSMessageType.INFO);
		}

		if ((this.kitno != null) && !"".equals(this.kitno)) {
			this.disableBrowsePopup = false;
		} else {
			this.disableBrowsePopup = true;
		}
	}

	public void closeKitNumberBrowse(ActionEvent event) {

		this.kitNumberBrowseRendered = false;
	}

	public void rowSelectedKitNumber(ActionEvent event) {

		closeKitNumberBrowse(event);
		// set value for input field
		MaterialItem selectedMaterial = getSelectedKitNumber();
		if (selectedMaterial != null) {
			this.kitno = selectedMaterial.getKitno();
			// this.productionLine = selectedMaterial.getWorkcenter();
		}
		if ((this.kitno != null) && !"".equals(this.kitno)) {
			this.disableBrowsePopup = false;
		} else {
			this.disableBrowsePopup = true;
		}
	}

	private MaterialItem getSelectedKitNumber() {
		MaterialItem selectedMaterialItem = null;
		if (this.kitNoList != null) {
			for (int i = 0; i < this.kitNoList.size(); i++) {
				MaterialItem materialItem = kitNoList.get(i);
				if (materialItem != null && materialItem.getSelected()) {
					selectedMaterialItem = materialItem;
					break;
				}

			}
		}
		return selectedMaterialItem;
	}

	@Override
	public void processAction(ActionEvent arg0) throws AbortProcessingException {
		// TODO Auto-generated method stub

	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getSite() {
		return site;
	}

	public void setSite(String site) {
		this.site = site;
	}

	public String getMessage() {
		return message;
	}

	public List<SelectItem> getKitStatus() {
		return kitStatus;
	}

	public void setKitStatus(List<SelectItem> kitStatus) {
		this.kitStatus = kitStatus;
	}

	public List<SelectItem> getKitStatusListSelectItem() {
		return kitStatusListSelectItem;
	}

	public void setKitStatusListSelectItem(
			List<SelectItem> kitStatusListSelectItem) {
		this.kitStatusListSelectItem = kitStatusListSelectItem;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	

	public String getKitno() {
		return kitno;
	}

	public void setKitno(String kitno) 
	{
			this.kitno = kitno;
	}

	public String getKittype() {
		return kittype;
	}

	public void setKittype(String kittype) {
		this.kittype = kittype;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public boolean isDisableKitno() {
		return disableKitno;
	}

	public void setDisableKitno(boolean disableKitno) {
		this.disableKitno = disableKitno;
	}

	public boolean isDisableKittype() {
		return disableKittype;
	}

	public void setDisableKittype(boolean disableKittype) {
		this.disableKittype = disableKittype;
	}

	public boolean isDisableStatus() {
		return disableStatus;
	}

	public void setDisableStatus(boolean disableStatus) {
		this.disableStatus = disableStatus;
	}

	public Boolean getKitNumberBrowseRendered() {
		return kitNumberBrowseRendered;
	}

	public void setKitNumberBrowseRendered(Boolean kitNumberBrowseRendered) {
		this.kitNumberBrowseRendered = kitNumberBrowseRendered;
	}

	public List<MaterialItem> getKitNoList() {
		return kitNoList;
	}

	public void setKitNoList(List<MaterialItem> kitNoList) {
		this.kitNoList = kitNoList;
	}

	public List<SelectItem> getKitYype() {
		return kitType;
	}

	public void setKitType(List<SelectItem> kitType) {
		this.kitType = kitType;
	}

	public List<SelectItem> getKitTypeListSelectItem() {
		return kitTypeListSelectItem;
	}

	public void setKitTypeListSelectItem(List<SelectItem> kitTypeListSelectItem) {
		this.kitTypeListSelectItem = kitTypeListSelectItem;
	}

	public List<MaterialCustomDataItem> getMaterialCustomDataList() {
		return materialCustomDataList;
	}

	public void setMaterialCustomDataList(
			List<MaterialCustomDataItem> materialCustomDataList) {
		this.materialCustomDataList = materialCustomDataList;
	}

	public int getDataFound() {
		return dataFound;
	}

	public void setDataFound(int dataFound) {
		this.dataFound = dataFound;
	}

	public Integer getKitNoFromTable() {
		return kitNoFromTable;
	}

	public void setKitNoFromTable(Integer kitNoFromTable) {
		this.kitNoFromTable = kitNoFromTable;
	}

	public boolean isDisableBrowsePopup() {
		return disableBrowsePopup;
	}

	public void setDisableBrowsePopup(boolean disableBrowsePopup) {
		this.disableBrowsePopup = disableBrowsePopup;
	}

	public Boolean getDisableKitNUMBER() {
		return disableKitNUMBER;
	}

	public void setDisableKitNUMBER(Boolean disableKitNUMBER) {
		this.disableKitNUMBER = disableKitNUMBER;
	}
	

	public boolean isRenderConfirmationPopup() {
		return renderConfirmationPopup;
	}

	public void setRenderConfirmationPopup(boolean renderConfirmationPopup) {
		this.renderConfirmationPopup = renderConfirmationPopup;
	}
	
	public void closeConfirmationPopup(ActionEvent event) {

		this.renderConfirmationPopup = false;
		// Make sure you add the table to the list of control updates so that
		// the new model value will be shown on the UI.
		UIComponent tablePanel = findComponent(FacesUtility.getFacesContext()
				.getViewRoot(), "kittingDefinitionForm:displayPanel");
		if (tablePanel != null) {
			FacesUtility.addControlUpdate(tablePanel);
		}
	}
	
	public void confirmYes(ActionEvent event) throws NamingException
	{
		Context ctx = new InitialContext();
		KittingDefinitionInterface kittingEjb = (KittingDefinitionInterface) ctx
				.lookup("ejb:/appName=atos.com/apps~ear,jarName=atos.com~apps~ejb.jar, beanName=KittingDefinitionControllerBean, interfaceName=com.atos.kittingEJB.KittingDefinitionInterface");
		boolean isStatusUpdated = kittingEjb.updateData(this.kitno, this.kittype, this.status);
		
		if(isStatusUpdated)
		{
			this.renderConfirmationPopup = false;
			message = FacesUtility
			.getLocaleSpecificText(" Custom data successfully updated .");
			setMessageBar(true, LSMessageType.INFO);
			
		}
	}
	
	public void confirmNo(ActionEvent event) throws NamingException 
	{
	Context ctx = new InitialContext();
	KittingDefinitionInterface kittingEjb = (KittingDefinitionInterface) ctx
	.lookup("ejb:/appName=atos.com/apps~ear,jarName=atos.com~apps~ejb.jar, beanName=KittingDefinitionControllerBean, interfaceName=com.atos.kittingEJB.KittingDefinitionInterface");
	closeConfirmationPopup(event);
	kittingEjb.readCustomData(this.kitno);
	}

	public void setSelectKitType(boolean selectKitType) {
		Map<String, Object> requestMap = FacesContext.getCurrentInstance()
				.getExternalContext().getRequestMap();
		MaterialItem currentRow = (MaterialItem) requestMap
				.get("kitTypeBrowseVar");

		currentRow.setSelected(new Boolean(selectKitType));
	}

	public boolean getSelectKitType() {
		Map<String, Object> requestMap = FacesContext.getCurrentInstance()
				.getExternalContext().getRequestMap();
		MaterialItem currentRow = (MaterialItem) requestMap
				.get("kitTypeBrowseVar");

		if (currentRow != null) {
			return currentRow.getSelected().booleanValue();
		}
		return false;
	}

	public void setSelectKitStatus(boolean selectKitStatus) {
		Map<String, Object> requestMap = FacesContext.getCurrentInstance()
				.getExternalContext().getRequestMap();
		MaterialItem currentRow = (MaterialItem) requestMap
				.get("kitstatusBrowseVar");

		currentRow.setSelected(new Boolean(selectKitStatus));
	}

	public boolean getSelectKitStatus() {
		Map<String, Object> requestMap = FacesContext.getCurrentInstance()
				.getExternalContext().getRequestMap();
		MaterialItem currentRow = (MaterialItem) requestMap
				.get("kitstatusBrowseVar");

		if (currentRow != null) {
			return currentRow.getSelected().booleanValue();
		}
		return false;
	}

	public void setSelectKitNumber(boolean selectKitNumber) {
		Map<String, Object> requestMap = FacesContext.getCurrentInstance()
				.getExternalContext().getRequestMap();
		MaterialItem currentRow = (MaterialItem) requestMap
				.get("kitnumberBrowseVar");

		currentRow.setSelected(new Boolean(selectKitNumber));
	}

	public boolean getSelectKitNumber() {
		Map<String, Object> requestMap = FacesContext.getCurrentInstance()
				.getExternalContext().getRequestMap();
		MaterialItem currentRow = (MaterialItem) requestMap
				.get("kitnumberBrowseVar");

		if (currentRow != null) {
			return currentRow.getSelected().booleanValue();
		}
		return false;
	}
	



}