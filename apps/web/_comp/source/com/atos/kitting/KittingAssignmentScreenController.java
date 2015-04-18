package com.atos.kitting;

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

import javax.annotation.PostConstruct;
import javax.faces.component.UIComponent;
import javax.faces.component.html.HtmlOutputText;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ActionListener;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.commons.lang3.StringUtils;
import com.atos.kittingEJB.KittingAssignmentScreenInterface;
import com.atos.kittingEJB.KittingDefinitionData;
import com.atos.kittingEJB.MaterialCustomDataItem;
import com.atos.kittingEJB.MaterialItem;
import com.sap.me.demand.SFCBOHandle;
import com.sap.me.frame.SystemBase;
import com.sap.me.frame.domain.BusinessException;
import com.sap.me.frame.service.CommonMethods;
import com.sap.me.production.podclient.BasePodPlugin;
import com.sap.me.wpmf.util.FacesUtility;
import com.sap.tc.ls.api.enumerations.LSMessageType;
import com.sap.ui.faces.component.sap.UICommandInputText;

public class KittingAssignmentScreenController extends BasePodPlugin implements
		ActionListener {

	private static final long serialVersionUID = 1L;
	private boolean selectProductionLine;
	private Date scheduleStartDate;
	private String productionLine;
	private Boolean productionLineBrowseRendered;
	private Boolean disableBrowsePopup;
	private Boolean disableProductionLine;
	private Boolean disableSscheduleStartDate;
	private Integer kitnoAlt = null;
	private Boolean kitNumberBrowseRendered;
	private List<MaterialCustomDataItem> kitNoList = new ArrayList<MaterialCustomDataItem>();
	private List<MaterialItem> productionLineList = new ArrayList<MaterialItem>();
	private String message;
	private String site = CommonMethods.getSite();
	private String user = CommonMethods.getUserId();
	private boolean selectKitNumber;
	private boolean isAssigned;
	private List<MaterialCustomDataItem> materialCustomDataList;
	private boolean renderConfirmationPopup;
	private String confirmationMessage;
	private String selectedPanelId;
	private Date systemDate = new Date();
	private List<SelectItem> kitnumber;
	private List<SelectItem> kitNumberListSelectItem = new ArrayList<SelectItem>();
	private String sfcValue;
	private final SystemBase dbBase = SystemBase
			.createSystemBase("jdbc/jts/wipPool");
	private Context contx;

	private Connection getConnection() {
		Connection con = null;

		con = dbBase.getDBConnection();

		return con;
	}

	@PostConstruct
	public void init()
	{
		this.kitNumberListSelectItem = kitNumberBrowse();
	}

	public void clear() {
		this.productionLine = null;
		this.productionLine = "";
		this.renderConfirmationPopup = false;
		this.scheduleStartDate = null;
		FacesUtility.removeSessionMapValue("kitAssignmentScreenControllerBean");
		this.message = null;
		setMessageBar(false, LSMessageType.ERROR);
		setMessageBar(false, LSMessageType.INFO);
		UIComponent tablePanel = findComponent(FacesUtility.getFacesContext()
				.getViewRoot(), "kittingAssignmentForm:displayPanel");
		if (tablePanel != null) {
			FacesUtility.addControlUpdate(tablePanel);
		}
	}

	public void readMaterialData() throws NamingException 
	{
		if (StringUtils.isNotEmpty(this.productionLine)
				&& !"".equals(this.productionLine)
				&& this.scheduleStartDate != null
				&& !"".equals(this.scheduleStartDate)) {
			this.message = null;
			setMessageBar(false, LSMessageType.ERROR);
			setMessageBar(false, LSMessageType.INFO);
			materialCustomDataList = new ArrayList<MaterialCustomDataItem>();
			Context ctx = new InitialContext();
			KittingAssignmentScreenInterface kittingassignmentEjb = (KittingAssignmentScreenInterface) ctx
					.lookup("ejb:/appName=atos.com/apps~ear,jarName=atos.com~apps~ejb.jar, beanName=KittingAssignmentControllerBean, interfaceName=com.atos.kittingEJB.KittingAssignmentScreenInterface");
			List<KittingDefinitionData> UserDataList = kittingassignmentEjb.readCustomData(this.productionLine, this.scheduleStartDate);
			for (KittingDefinitionData kittingDefinitionData : UserDataList) {
				MaterialCustomDataItem materialCustomDataItem = new MaterialCustomDataItem();
				materialCustomDataItem.setSfcfromPPC(kittingDefinitionData
						.getSfcfromPPC());
				materialCustomDataItem.setPriorityFromPPC(kittingDefinitionData
						.getPriorityFromPPC());
				materialCustomDataItem
						.setPlannedStartDateFromPPC(sqltoUtil(kittingDefinitionData
								.getPlannedStartDateFromPPCDate()));
				materialCustomDataItem.setWorkcenter(kittingDefinitionData
						.getWorkcenter());
				materialCustomDataItem.setKitno(kittingDefinitionData
						.getKitno());
				materialCustomDataList.add(materialCustomDataItem);
			}
		} else {

			message = FacesUtility
					.getLocaleSpecificText(" Select Workcenter and Planned start date inorder to retrieve data .");
			setMessageBar(true, LSMessageType.ERROR);
		}

		UIComponent tablePanel = findComponent(FacesUtility.getFacesContext()
				.getViewRoot(), "kittingAssignmentForm:displayPanel");
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
					.getLocaleSpecificText(" 	Your query returned no data .");
			setMessageBar(true, LSMessageType.ERROR);

		}
		
	}



	public void assignKit(ActionEvent event) throws BusinessException,
			ParseException, NamingException
			
	{
		Context ctx = new InitialContext();
		KittingAssignmentScreenInterface kittingEjb = (KittingAssignmentScreenInterface) ctx
				.lookup("ejb:/appName=atos.com/apps~ear,jarName=atos.com~apps~ejb.jar, beanName=KittingAssignmentControllerBean, interfaceName=com.atos.kittingEJB.KittingAssignmentScreenInterface");
		this.message = "";
		this.sfcValue = sfcValue();
		int selectCount = 0;
		for (MaterialCustomDataItem updating : this.materialCustomDataList) {
			if (updating.isSelect() == true) {
				selectCount++;
				if (stringToUtilDate(updating.getPlannedStartDateFromPPC())
						.before(getZeroTimeDate(systemDate))) {
					message = FacesUtility
							.getLocaleSpecificText(" 	Planned Start Date of the SFC is before todays's date, can not assign the kit");
					setMessageBar(true, LSMessageType.ERROR);
					break;
				} else {
					if (updating.getKitno() != null
							&& !"".equals(updating.getKitno())) {
						String datafound = kittingEjb.findKitNo(updating
								.getKitno());
						if (datafound != null && !"".equals(datafound)) 
						{
							SFCBOHandle sfcBOHandle = new SFCBOHandle(site, updating.getSfcfromPPC());
							String sfcReference = sfcBOHandle.getValue();
							String sfcBOFound = kittingEjb.findSFCBO_Assignment(sfcReference);
							if (sfcBOFound != null && !"".equals(sfcBOFound)) {
								this.renderConfirmationPopup = true;
							} else {
								isAssigned = kittingEjb.updateKitDef(updating
										.getKitno());
								saveMaterialData();
							}
						} else {
							message = FacesUtility
									.getLocaleSpecificText(" 	Kit Number "
											+ updating.getKitno()
											+ " is not in available status, can not assign the kit");
							setMessageBar(true, LSMessageType.ERROR);
						}
					} else {
						message = FacesUtility
								.getLocaleSpecificText(" 	Kit Number should not be empty .");
						setMessageBar(true, LSMessageType.ERROR);
					}
				}
			}
		}
		if (selectCount == 0) {
			message = FacesUtility
					.getLocaleSpecificText("	Select row to assign the kit .");
			setMessageBar(true, LSMessageType.ERROR);
		}

		UIComponent tablePanel = findComponent(FacesUtility.getFacesContext()
				.getViewRoot(), "kittingAssignmentForm:displayPanel");
		if (tablePanel != null) {
			FacesUtility.addControlUpdate(tablePanel);
		}
		if ("".equals(this.message)) {
			// refresh message bar
			setMessageBar(false, LSMessageType.INFO);
		}

	}

	public void saveMaterialData() throws ParseException, NamingException 
	{
		Context ctx = new InitialContext();
		KittingAssignmentScreenInterface kittingEjb = (KittingAssignmentScreenInterface) ctx
				.lookup("ejb:/appName=atos.com/apps~ear,jarName=atos.com~apps~ejb.jar, beanName=KittingAssignmentControllerBean, interfaceName=com.atos.kittingEJB.KittingAssignmentScreenInterface");
		int selectCount = 0;
		for (MaterialCustomDataItem updating : this.materialCustomDataList) {
			if (updating.isSelect() == true) {
				String kitDefBO = kittingEjb.findKitDefBO(updating.getKitno());
				boolean isInserted = false;
				selectCount++;
				String sfcBOCompare = kittingEjb.findSFCBO_Assignment(kittingEjb.findSFCBO_SFC(updating
						.getSfcfromPPC()));
				String kitDefBOTemp = "KitDefBO:" + site + ","
						+ updating.getKitno() + ",KitTypeBO:" + site + ",%";
				String kitDefBOfromAssignment = kittingEjb.findKitDefBO_KitAssignment(kitDefBOTemp);
				if (sfcBOCompare != null && !"".equals(sfcBOCompare)) {
					message = FacesUtility.getLocaleSpecificText("  SFCBO "
							+ sfcBOCompare
							+ " is already exist in database .");
					setMessageBar(true, LSMessageType.ERROR);
				} else if (kitDefBOfromAssignment != null
						&& !"".equals(kitDefBOfromAssignment)) {
					message = FacesUtility.getLocaleSpecificText("  KitDefBO "
							+ kitDefBOfromAssignment
							+ " is already exist in database .");
					setMessageBar(true, LSMessageType.ERROR);
				} else {
					if (kitDefBO != null && !"".equals(kitDefBO)) {
						kittingEjb.insertData(kittingEjb.findSFCBO_SFC(updating
								.getSfcfromPPC()), kitDefBO, updating
								.getPriorityFromPPC(), stringToDate(updating
								.getPlannedStartDateFromPPC()), updating
								.getWorkcenter());
						isInserted = true;
						if (isInserted == true) {
							message = FacesUtility
									.getLocaleSpecificText(" 	Kit assignment is successfully done .");
							setMessageBar(true, LSMessageType.INFO);
						}
					} else {
						message = FacesUtility
								.getLocaleSpecificText(" 	Given Kit Number "
										+ updating.getKitno()
										+ " is not Available status or not available in database .");
						setMessageBar(true, LSMessageType.ERROR);
					}
				}
			}
		}
		if (selectCount == 0) {
			message = FacesUtility
					.getLocaleSpecificText(" 	Select row to assign the Kit .");
			setMessageBar(true, LSMessageType.ERROR);
		}
		UIComponent tablePanel = findComponent(FacesUtility.getFacesContext()
				.getViewRoot(), "kittingAssignmentForm:displayPanel");
		if (tablePanel != null) {
			FacesUtility.addControlUpdate(tablePanel);
		}
		if ("".equals(this.message)) {
			// refresh message bar
			setMessageBar(false, LSMessageType.INFO);
		}
	}

	public String sfcValue() {
		int selectCount = 0;
		String sfcFromDataTable = null;
		for (MaterialCustomDataItem updating : this.materialCustomDataList) {
			if (updating.isSelect()) {
				selectCount++;
				sfcFromDataTable = updating.getSfcfromPPC();
			}
		}
		return sfcFromDataTable;
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
				"kittingAssignmentForm:messageBar");
		HtmlOutputText messageBarError = (HtmlOutputText) findComponent(FacesUtility
				.getFacesContext().getViewRoot(),
				"kittingAssignmentForm:messageBar2");
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
				"kittingAssignmentForm:fieldButtonPanel");
		if (fieldButtonPanel != null) {
			FacesUtility.addControlUpdate(fieldButtonPanel);
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

	public java.sql.Date stringToDate(String dateString) throws ParseException {
		if (dateString != null && !"".equals(dateString)) {
			SimpleDateFormat formatter = new SimpleDateFormat("MMM d, yyyy");
			Date date = formatter.parse(dateString);
			java.sql.Date sqlDate = new java.sql.Date(date.getTime());
			return sqlDate;
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

	public Date stringToUtilDate(String format) throws ParseException {
		if (format != null && !"".equals(format)) {
			SimpleDateFormat df = new SimpleDateFormat("MMM d, yyyy");
			Date date = df.parse(format);
			return date;
		} else {
			return null;
		}
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

	public void rowSelectedProductionLine(ActionEvent event) {

		closeProductionLineBrowse(event);
		// set value for input field
		MaterialItem selectedMaterial = getSelectedProductionLine();
		if (selectedMaterial != null) {
			this.productionLine = selectedMaterial.getMaterial();
			// this.productionLine = selectedMaterial.getWorkcenter();
		}
		if (StringUtils.isNotEmpty(this.productionLine)) {
			this.disableBrowsePopup = false;
		} else {
			this.disableBrowsePopup = true;
		}
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

	public void showProductionLineBrowse(ActionEvent event) {
		this.productionLineBrowseRendered = true;
		try {
			this.productionLineList = workcenterBrowse(this.productionLine);

		} catch (BusinessException businessException) {
			message = FacesUtility
					.getLocaleSpecificText(" Business exception while finding work center");
			setMessageBar(true, LSMessageType.INFO);
		}

		if (StringUtils.isNotEmpty(this.productionLine)) {
			this.disableBrowsePopup = false;
		} else {
			this.disableBrowsePopup = true;
		}
	}

	public void activateOnProductionLineSelect(ActionEvent event) {
		if (StringUtils.isNotEmpty(this.productionLine)) {
			this.disableBrowsePopup = false;
		} else {
			this.disableBrowsePopup = true;
		}
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

	public List<MaterialCustomDataItem> kitNumberBrowse(Integer kitno)
			throws BusinessException {
		Connection con = null;
		String query = "SELECT  KIT_NO from Z_KITDEF where KIT_NO IS NOT NULL ";
		if ((kitno != null) && !"".equals(kitno)) {
			query = query + "  and KIT_NO like '" + kitno + "%'";
		}
		PreparedStatement ps = null;
		List<MaterialCustomDataItem> user = new ArrayList<MaterialCustomDataItem>();
		try {
			con = getConnection();
			ps = con.prepareStatement(query);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				MaterialCustomDataItem downloadVO = new MaterialCustomDataItem();
				downloadVO.setKitno(""+rs.getString("KIT_NO"));
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

	public void closeConfirmationPopup(ActionEvent event) {

		this.renderConfirmationPopup = false;
		// Make sure you add the table to the list of control updates so that
		// the new model value will be shown on the UI.
		UIComponent tablePanel = findComponent(FacesUtility.getFacesContext()
				.getViewRoot(), "kittingAssignmentForm:reportsPanel2");
		if (tablePanel != null) {
			FacesUtility.addControlUpdate(tablePanel);
		}
		// updating message bar
		UIComponent fieldButtonPanel = findComponent(FacesUtility
				.getFacesContext().getViewRoot(),
				"kittingAssignmentForm:fieldButtonPanel");
		if (fieldButtonPanel != null) {
			FacesUtility.addControlUpdate(fieldButtonPanel);
		}
	}

	public void confirmYes(ActionEvent event) throws NamingException
	{
		boolean isStatusUpdated = updateKitStatus();
		for (MaterialCustomDataItem updating : this.materialCustomDataList) {
			if (isStatusUpdated == true) {
				boolean isAssignmentUpdated = updateAssignmentTable();
				if (isAssignmentUpdated == true)
				{
					Context ctx = new InitialContext();
					KittingAssignmentScreenInterface kittingEjb = (KittingAssignmentScreenInterface) ctx
							.lookup("ejb:/appName=atos.com/apps~ear,jarName=atos.com~apps~ejb.jar, beanName=KittingAssignmentControllerBean, interfaceName=com.atos.kittingEJB.KittingAssignmentScreenInterface");
					kittingEjb.updateKitDef(updating.getKitno());
					message = FacesUtility
							.getLocaleSpecificText(" 	Kit assignment is successfully done .");
					setMessageBar(true, LSMessageType.INFO);
					this.renderConfirmationPopup = false;
				}
			}
		}
	}

	public void confirmNo(ActionEvent event) {
		closeConfirmationPopup(event);
	}

	public boolean updateKitStatus() throws NamingException
	{
		Context ctx = new InitialContext();
		KittingAssignmentScreenInterface kittingEjb = (KittingAssignmentScreenInterface) ctx
				.lookup("ejb:/appName=atos.com/apps~ear,jarName=atos.com~apps~ejb.jar, beanName=KittingAssignmentControllerBean, interfaceName=com.atos.kittingEJB.KittingAssignmentScreenInterface");
		boolean isUpdated = false;

		for (MaterialCustomDataItem updating : this.materialCustomDataList) {
			Connection con = null;
			// KittingDefinitionData kittingDefinition=new
			// KittingDefinitionData();
			String qry = "UPDATE Z_KITDEF SET KIT_STATUS = ?,KIT_STATUS_BO = ? WHERE HANDLE = ?";
			PreparedStatement ps = null;
			try {
				con = getConnection();
				ps = con.prepareStatement(qry);
				ps.setString(1, "AVAILABLE");
				ps.setString(2, "KitStatusBO:" + site + ",AVAILABLE");
				String sfcBOTemp = kittingEjb.findSFCBO_SFC(updating
						.getSfcfromPPC());
				String sfcBOFound = kittingEjb.findSFCBO_Assignment(sfcBOTemp);
				ps.setString(3,
						kittingEjb.findKitDefBObySFC(sfcBOFound));
				ps.executeUpdate();
				isUpdated = true;
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
		}
		return isUpdated;
	}

	public boolean updateAssignmentTable() throws NamingException 
	{
		Context ctx = new InitialContext();
		KittingAssignmentScreenInterface kittingEjb = (KittingAssignmentScreenInterface) ctx
				.lookup("ejb:/appName=atos.com/apps~ear,jarName=atos.com~apps~ejb.jar, beanName=KittingAssignmentControllerBean, interfaceName=com.atos.kittingEJB.KittingAssignmentScreenInterface");
		boolean isUpdated = false;
		for (MaterialCustomDataItem updating : this.materialCustomDataList) {
			Connection con = null;
			// KittingDefinitionData kittingDefinition=new
			// KittingDefinitionData();
			String qry = "UPDATE Z_KIT_ASSIGNMENT SET KITDEF_BO = ?,UPDATED_BY = ?,UPDATED_DATE = ? WHERE SFC_BO = ?";
			PreparedStatement ps = null;
			try {
				con = getConnection();
				ps = con.prepareStatement(qry);
				ps.setString(1, kittingEjb.findKitDefBO(updating.getKitno()));
				ps.setString(2, user);
				ps.setDate(3, utilDatetoSqlDate(new java.util.Date()));
				String sfcBOTemp = kittingEjb.findSFCBO_SFC(updating
						.getSfcfromPPC());
				String sfcBOFound = kittingEjb.findSFCBO_Assignment(sfcBOTemp);
				ps.setString(4, sfcBOFound);
				ps.executeUpdate();
				isUpdated = true;
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
		}
		return isUpdated;
	}

	public List<SelectItem> kitNumberBrowse() {
		Connection con = null;
		String query = "SELECT  KIT_NO from Z_KITDEF WHERE ACTIVE = 1 ";
		PreparedStatement ps = null;
		kitnumber = new ArrayList<SelectItem>();
		try {
			con = getConnection();
			ps = con.prepareStatement(query);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				/* MaterialItem downloadVO=new MaterialItem(); */
				String kitNO = rs.getString("KIT_NO");
				kitnumber.add(new SelectItem(kitNO));

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
		return kitnumber;
	}

	public void showConfirmationPopup(ActionEvent actionEvent)
			throws BusinessException {
		this.renderConfirmationPopup = true;
	}

	public void showModalPopupPanel(ActionEvent event) {

		this.renderConfirmationPopup = true;
		// Make sure you add the table to the list of control updates so that
		// the new model value will be shown on the UI.

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

	public Date getScheduleStartDate() {
		return scheduleStartDate;
	}

	public void setScheduleStartDate(Date scheduleStartDate) {
		this.scheduleStartDate = scheduleStartDate;
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

	public void setMaterialCustomDataList(
			List<MaterialCustomDataItem> materialCustomDataList) {
		this.materialCustomDataList = materialCustomDataList;
	}

	public List<MaterialCustomDataItem> getMaterialCustomDataList() {
		return materialCustomDataList;
	}

	public String getMessage() {

		return message;
	}

	public boolean isAssigned() {
		return isAssigned;
	}

	public void setAssigned(boolean isAssigned) {
		this.isAssigned = isAssigned;
	}

	public void setMessage(String message) {

		this.message = message;
	}

	public List<MaterialItem> getProductionLineList() {
		return productionLineList;
	}

	public void setProductionLineList(List<MaterialItem> rcTypeList) {
		this.productionLineList = rcTypeList;
	}

	public Boolean getDisableProductionLine() {
		return disableProductionLine;
	}

	public void setDisableProductionLine(Boolean disableProductionLine) {
		this.disableProductionLine = disableProductionLine;
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

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public Integer getKitnoAlt() {
		return kitnoAlt;
	}

	public void setKitnoAlt(Integer kitnoAlt) {
		this.kitnoAlt = kitnoAlt;
	}

	public Boolean getKitNumberBrowseRendered() {
		return kitNumberBrowseRendered;
	}

	public void setKitNumberBrowseRendered(Boolean kitNumberBrowseRendered) {
		this.kitNumberBrowseRendered = kitNumberBrowseRendered;
	}

	public List<MaterialCustomDataItem> getKitNoList() {
		return kitNoList;
	}

	public void setKitNoList(List<MaterialCustomDataItem> kitNoList) {
		this.kitNoList = kitNoList;
	}

	public Boolean getDisableSscheduleStartDate() {
		return disableSscheduleStartDate;
	}

	public void setDisableSscheduleStartDate(Boolean disableSscheduleStartDate) {
		this.disableSscheduleStartDate = disableSscheduleStartDate;
	}

	public boolean isRenderConfirmationPopup() {
		return renderConfirmationPopup;
	}

	public void setRenderConfirmationPopup(boolean renderConfirmationPopup) {
		this.renderConfirmationPopup = renderConfirmationPopup;
	}

	public String getConfirmationMessage() {
		return confirmationMessage;
	}

	public void setConfirmationMessage(String confirmationMessage) {
		this.confirmationMessage = confirmationMessage;
	}

	public String getSelectedPanelId() {
		return selectedPanelId;
	}

	public void setSelectedPanelId(String selectedPanelId) {
		this.selectedPanelId = selectedPanelId;
	}

	public List<SelectItem> getKitnumber() {
		return kitnumber;
	}

	public void setKitnumber(List<SelectItem> kitnumber) {
		this.kitnumber = kitnumber;
	}

	public List<SelectItem> getKitNumberListSelectItem() {
		return kitNumberListSelectItem;
	}

	public void setKitNumberListSelectItem(
			List<SelectItem> kitNumberListSelectItem) {
		this.kitNumberListSelectItem = kitNumberListSelectItem;
	}

	public String getSfcValue() {
		return sfcValue;
	}

	public void setSfcValue(String sfcValue) {
		this.sfcValue = sfcValue;
	}

}
