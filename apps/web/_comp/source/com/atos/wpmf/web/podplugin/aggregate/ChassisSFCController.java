package com.atos.wpmf.web.podplugin.aggregate;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.component.UIComponent;
import javax.faces.component.html.HtmlOutputText;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import org.apache.commons.lang3.StringUtils;
import com.sap.me.common.AttributeValue;
import com.sap.me.common.ObjectReference;
import com.sap.me.datatype.DataFieldValuePair;
import com.sap.me.demand.SFCBOHandle;
import com.sap.me.extension.Services;
import com.sap.me.frame.SystemBase;
import com.sap.me.frame.domain.BusinessException;
import com.sap.me.frame.service.CommonMethods;
import com.sap.me.inventory.Inventory;
import com.sap.me.inventory.InventoryDataField;
import com.sap.me.inventory.InventoryIdentifier;
import com.sap.me.inventory.InventoryServiceInterface;
import com.sap.me.inventory.InventoryValidateAndUpdateRequest;
import com.sap.me.production.InvalidSfcException;
import com.sap.me.production.SfcBasicData;
import com.sap.me.production.SfcStateServiceInterface;
import com.sap.me.production.podclient.BasePodPlugin;
import com.sap.me.production.podclient.PodSelectionModelInterface;
import com.sap.me.security.RunAsServiceLocator;
import com.sap.me.wpmf.MessageType;
import com.sap.me.wpmf.util.FacesUtility;
import com.sap.me.wpmf.util.MessageHandler;
import com.sap.tc.ls.api.enumerations.LSMessageType;
import com.visiprise.frame.configuration.ServiceReference;
import com.visiprise.frame.metadata.KeyField;

public class ChassisSFCController extends BasePodPlugin

{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String chassisno;
	private String sfcValue;
	private String message;
	private Boolean sfcBrowseRendered;
	private boolean disableBrowsePopup;
	private Boolean disableSfc;
	private boolean renderPlugin = false;
	private SfcStateServiceInterface sfcStateService;
	private SfcBasicData sfcBasicData;
	private Date systemDate = new Date();
	private String user = CommonMethods.getUserId();
	private String site = CommonMethods.getSite();
	private boolean renderConfirmationPopup;
	@KeyField
	String chassis = "CHASSIS";
	private List<ChassisSFC> sfcList = new ArrayList<ChassisSFC>();
	private final SystemBase dbBase = SystemBase
			.createSystemBase("jdbc/jts/wipPool");
	private InventoryServiceInterface inventoryServiceInterface = (InventoryServiceInterface) Services
			.getService("com.sap.me.inventory", "InventoryService");
	PodSelectionModelInterface selectionModel = getPodSelectionModel();

	private Connection getConnection() {
		Connection con = null;
		con = dbBase.getDBConnection();
		return con;
	}

	@PostConstruct
	public void init() {
		setMessageBar(false, LSMessageType.INFO);
		setMessageBar(false, LSMessageType.ERROR);
		this.sfcValue = getPodSFCFieldData();
	}

	public void initServices() {
		ServiceReference sfcStateServiceRef = new ServiceReference(
				"com.sap.me.production", "SfcStateService");
		sfcStateService = RunAsServiceLocator.getService(sfcStateServiceRef,
				SfcStateServiceInterface.class, this.user, this.site, null);
	}

	private String getPodSFCFieldData() {
		PodSelectionModelInterface selectionModel = getPodSelectionModel();
		if (selectionModel == null) {
			return "";
		}
		String sfcRef = "";

		if (selectionModel.getSfcs() != null) {
			sfcRef = selectionModel.getSfcs().get(0).getInputId();
			SFCBOHandle sfcboHandle = new SFCBOHandle(CommonMethods.getSite(),
					sfcRef);
			sfcRef = sfcboHandle.getSFC();
		}
		if (selectionModel.getSfcs() == null) {
			sfcRef = null;
		}
		return sfcRef;
	}

	public void setMessageBar(boolean render, LSMessageType messageType) {
		HtmlOutputText messageBarInfo = (HtmlOutputText) findComponent(
				FacesUtility.getFacesContext().getViewRoot(),
				"assignaggPlugin:messageBar");
		HtmlOutputText messageBarError = (HtmlOutputText) findComponent(
				FacesUtility.getFacesContext().getViewRoot(),
				"assignaggPlugin:messageBar2");
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
				"assignaggPlugin:fieldButtonPanel");
		if (fieldButtonPanel != null) {
			FacesUtility.addControlUpdate(fieldButtonPanel);
		}
	}

	public void showSfcBrowse(ActionEvent event) {
		setMessageBar(false, LSMessageType.INFO);
		setMessageBar(false, LSMessageType.ERROR);
		if (StringUtils.isEmpty(this.sfcValue) && "".equals(this.sfcValue)) {
			message = FacesUtility
					.getLocaleSpecificText(" SFC value should not be empty .");
			setMessageBar(true, LSMessageType.ERROR);
		} else {

			try {

				SFCBOHandle sfcBOHandle = new SFCBOHandle(site, this.sfcValue);
				String sfcReference = sfcBOHandle.getValue();
				this.sfcList = sfcBrowse(getPreviousSFC(sfcReference),
						this.chassisno);
				if (this.sfcList.size() != 0) {
					this.sfcBrowseRendered = true;
				} else {
					message = FacesUtility
							.getLocaleSpecificText(" Your query returned no data .");
					setMessageBar(true, LSMessageType.ERROR);
				}
			} catch (BusinessException businessException) {
				businessException.printStackTrace();
			}

			if ((this.chassisno != null) && !"".equals(this.chassisno)) {
				this.disableBrowsePopup = false;
			} else {
				this.disableBrowsePopup = true;
			}
		}
	}

	public void rowSelectedSfc(ActionEvent event) {

		closeSfcBrowse(event);
		// set value for input field
		ChassisSFC selectedMaterial = getSelectedSfc();
		if (selectedMaterial != null) {
			this.chassisno = selectedMaterial.getMaterial();
			// this.productionLine = selectedMaterial.getWorkcenter();
		}
		if ((this.chassisno != null) && !"".equals(this.chassisno)) {
			this.disableBrowsePopup = false;
		} else {
			this.disableBrowsePopup = true;
		}
	}

	private ChassisSFC getSelectedSfc() {
		ChassisSFC selectedMaterialItem = null;
		if (this.sfcList != null) {
			for (int i = 0; i < this.sfcList.size(); i++) {
				ChassisSFC materialItem = sfcList.get(i);
				if (materialItem != null && materialItem.getSelected()) {
					selectedMaterialItem = materialItem;
					break;
				}

			}
		}
		return selectedMaterialItem;
	}

	public void closeSfcBrowse(ActionEvent event) {

		this.sfcBrowseRendered = false;
	}

	public void setSelectSfc(boolean selectSfc) {
		Map<String, Object> requestMap = FacesContext.getCurrentInstance()
				.getExternalContext().getRequestMap();
		ChassisSFC currentRow = (ChassisSFC) requestMap.get("sfcBrowseVar");

		currentRow.setSelected(new Boolean(selectSfc));
	}

	public boolean getSelectSfc() {
		Map<String, Object> requestMap = FacesContext.getCurrentInstance()
				.getExternalContext().getRequestMap();
		ChassisSFC currentRow = (ChassisSFC) requestMap.get("sfcBrowseVar");

		if (currentRow != null) {
			return currentRow.getSelected().booleanValue();
		}
		return false;
	}

	public String getParentOrder(String sfc) throws BusinessException {
		Connection con = null;
		String parentOrder = null;
		String query = "SELECT PARENT_ORDERNO FROM Z_ORDERSEQUENCE WHERE SFC = ?";
		PreparedStatement ps = null;
		try {
			con = getConnection();
			ps = con.prepareStatement(query);
			ps.setString(1, sfc);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				parentOrder = rs.getString("PARENT_ORDERNO");
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
		return parentOrder;
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

	public List<ChassisSFC> sfcBrowse(String sfc, String chassisno)
			throws BusinessException {
		Connection con = null;
		String query = "SELECT DISTINCT SFC FROM Z_ORDERSEQUENCE WHERE ORDERNO = ? AND (ACTIVE = 1 OR STATUS = 999)"
				+ "";
		if (chassisno != null && !"".equals(chassisno)) {
			query = query + " AND SFC like '" + chassisno + "%' ";
		}
		PreparedStatement ps = null;
		List<ChassisSFC> user = new ArrayList<ChassisSFC>();
		try {
			con = getConnection();
			ps = con.prepareStatement(query);
			ps.setString(1, getParentOrder(sfc));
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				ChassisSFC downloadVO = new ChassisSFC();
				downloadVO.setMaterial("" + rs.getString("SFC"));
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

	public List<ChassisSFC> sfcData(String chassisno) throws BusinessException {
		Connection con = null;
		String query = "SELECT * FROM Z_ORDERSEQUENCE WHERE (ACTIVE = 1 OR STATUS = 999) AND SFC like '"
				+ chassisno + "%'";
		PreparedStatement ps = null;
		List<ChassisSFC> chassisDataList = new ArrayList<ChassisSFC>();

		try {
			con = getConnection();
			ps = con.prepareStatement(query);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				ChassisSFC chassisSfcData = new ChassisSFC();
				chassisSfcData.setHandle("" + rs.getString("HANDLE"));
				chassisSfcData.setItem("" + rs.getString("ITEM"));
				chassisSfcData.setSfc("" + rs.getString("SFC"));
				chassisSfcData.setStatus(rs.getInt("STATUS"));
				chassisSfcData.setActive(rs.getInt("ACTIVE"));
				chassisSfcData.setPlannedstartdate(sqltoUtilDate(rs
						.getDate("PLANNED_START_DATE")));
				chassisDataList.add(chassisSfcData);
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
		return chassisDataList;
	}

	public Date sqltoUtilDate(java.sql.Date date) {
		if (date != null) {
			java.util.Date date1 = new java.util.Date(date.getTime());
			return date1;
		} else {
			return null;
		}
	}

	public boolean isSfcCompleted() throws BusinessException {
		boolean isDone = false;
		try {
			initServices();
			SFCBOHandle sfcBOHandle = new SFCBOHandle(site, this.sfcValue);
			String sfcReference = sfcBOHandle.getValue();
			SfcBasicData sfcBasicData = sfcStateService
					.findSfcDataByRef(new ObjectReference(sfcReference));
			isDone = sfcBasicData.getDone();
		} catch (NullPointerException ne) {
			ne.getMessage();
			message = FacesUtility.getLocaleSpecificText("SFC " + this.sfcValue
					+ " is not in Complete status. Can not assign .");
			setMessageBar(true, LSMessageType.ERROR);

		}
		return isDone;
	}

	public void assign() throws BusinessException
	{
		String sfcFound = null;
		String chassisNoFound = null;
		SFCBOHandle sfcBOHandle = new SFCBOHandle(site, this.sfcValue);
		String sfcReference = sfcBOHandle.getValue();
		SFCBOHandle chassisNoBOHandle = new SFCBOHandle(site, this.chassisno);
		String sfcReference2 = chassisNoBOHandle.getValue();
		try {
			initServices();
			SfcBasicData sfcBasicData = sfcStateService
					.findSfcDataByRef(new ObjectReference(sfcReference));
			sfcFound = sfcBasicData.getSfc();
		} catch (Exception e) {
			message = FacesUtility
					.getLocaleSpecificText(" Given SFC value is invalid, provide valid SFC . ");
			setMessageBar(true, LSMessageType.ERROR);
		}
		try {
			initServices();
			SfcBasicData sfcBasicData = sfcStateService
					.findSfcDataByRef(new ObjectReference(sfcReference2));
			chassisNoFound = sfcBasicData.getSfc();
		} catch (Exception e) {
			message = FacesUtility
					.getLocaleSpecificText(" Given Chassis No is invalid, provide valid Chassis No . ");
			setMessageBar(true, LSMessageType.ERROR);
		}
		if (this.sfcValue == null || "".equals(this.sfcValue)) {
			message = FacesUtility
					.getLocaleSpecificText(" Select atlease one SFC to perform assign operation . ");
			setMessageBar(true, LSMessageType.ERROR);
		} else if (sfcFound == null || "".equals(sfcFound)) {
			message = FacesUtility
					.getLocaleSpecificText(" Given SFC value is invalid, provide valid SFC . ");
			setMessageBar(true, LSMessageType.ERROR);
		} else if (this.chassisno == null || "".equals(this.chassisno)) {
			message = FacesUtility
			.getLocaleSpecificText(" Chassis No should not be empty . ");
			setMessageBar(true, LSMessageType.ERROR);
		}
		else if (chassisNoFound == null || "".equals(chassisNoFound)) {
			message = FacesUtility
					.getLocaleSpecificText(" Given Chassis No is invalid, provide valid Chassis No . ");
			setMessageBar(true, LSMessageType.ERROR);
		}

		else {
			boolean isCompleted = isSfcCompleted();
			if (isCompleted) {
				try {
					InventoryIdentifier inventoryIdentifier = new InventoryIdentifier(
							this.sfcValue);
					Inventory inventory;
					inventory = inventoryServiceInterface
							.findInventory(inventoryIdentifier);
					List<InventoryDataField> inventoryDataList;
					inventoryDataList = inventory.getAssemblyDataList();
					List<InventoryDataField> assemblyDataList = new ArrayList<InventoryDataField>();
					if (inventoryDataList.size() > 0) {
						for (InventoryDataField inventoryDataField : inventoryDataList) {
							if (inventoryDataField.getAttribute().equals(
									"CHASSIS")) {
								inventoryDataField.setValue(this.chassisno);
							}
							assemblyDataList.add(inventoryDataField);
						}
					} else {
						InventoryDataField inventoryDataField = new InventoryDataField();
						DataFieldValuePair datapair = new DataFieldValuePair(
								"DataFieldBO:1000,CHASSIS");
						datapair.setValue(this.chassisno);
						AttributeValue attval = new AttributeValue(chassis,
								datapair.getValue());
						inventoryDataField.setAttribute(attval.getAttribute());
						inventoryDataField.setValue(attval.getValue());
						assemblyDataList.add(inventoryDataField);
					}
					List<ChassisSFC> sfcDataList = sfcData(this.chassisno);
					Date plannedStartDate = sfcDataList.get(0)
							.getPlannedstartdate();
					Calendar cal = Calendar.getInstance();
					cal.add(Calendar.DATE, 7);
					Date afterOneWeekDate = cal.getTime();
					String assignedSFCFound = getAssignedSFC(this.chassisno);
					if (plannedStartDate == null) {
						message = FacesUtility
								.getLocaleSpecificText(" Planned Start Date of Chassis No "
										+ this.chassisno
										+ " should not be Null .");
						setMessageBar(true, LSMessageType.ERROR);
					} else if (plannedStartDate
							.before(getZeroTimeDate(systemDate))
							|| plannedStartDate
									.after(getZeroTimeDate(afterOneWeekDate))) {
						message = FacesUtility
								.getLocaleSpecificText(" Planned Start Date of Chassis No "
										+ this.chassisno
										+ " should be between : "
										+ getZeroTimeDate(systemDate))
								+ "  and  "
								+ (getZeroTimeDate(afterOneWeekDate)+" .");
						setMessageBar(true, LSMessageType.ERROR);
					} else if (inventoryDataList.size() != 0) {
						this.renderConfirmationPopup = true;
					} else if (assignedSFCFound != null) {
						message = FacesUtility
								.getLocaleSpecificText(" Chassis No "
										+ this.chassisno
										+ " is already assigned to another Inventory .");
						setMessageBar(true, LSMessageType.ERROR);
					} else {
						assigntoChassis();
					}
				} catch (BusinessException e) {
					MessageHandler.handle(e.getMessage(), null,
							MessageType.ERROR, this);

				} catch (Exception e) {
					MessageHandler.handle(e.getMessage(), null,
							MessageType.ERROR);
				}
			}
		}
	}

	public void assigntoChassis() throws BusinessException {
		initServices();
		try {
			SFCBOHandle sfcBOHandle = new SFCBOHandle(site, this.sfcValue);
			String sfcReference = sfcBOHandle.getValue();
			if (StringUtils.isNotEmpty(this.sfcValue)
					&& !"".equals(this.sfcValue)
					&& StringUtils.isNotEmpty(this.chassisno)
					&& !"".equals(this.chassisno)) {
				InventoryIdentifier inventoryIdentifier = new InventoryIdentifier(
						this.sfcValue);
				Inventory inventory;
				try {
					inventory = inventoryServiceInterface
							.findInventory(inventoryIdentifier);
					List<InventoryDataField> inventoryDataList;
					inventoryDataList = inventory.getAssemblyDataList();
					List<InventoryDataField> assemblyDataList = new ArrayList<InventoryDataField>();
					if (inventoryDataList.size() > 0) {
						for (InventoryDataField inventoryDataField : inventoryDataList) {
							if (inventoryDataField.getAttribute().equals(
									"CHASSIS")) {
								inventoryDataField.setValue(this.chassisno);
							}
							assemblyDataList.add(inventoryDataField);
						}
					} else {
						InventoryDataField inventoryDataField = new InventoryDataField();
						DataFieldValuePair datapair = new DataFieldValuePair(
								"DataFieldBO:1000,CHASSIS");
						datapair.setValue(this.chassisno);
						AttributeValue attval = new AttributeValue(chassis,
								datapair.getValue());
						inventoryDataField.setAttribute(attval.getAttribute());
						inventoryDataField.setValue(attval.getValue());
						assemblyDataList.add(inventoryDataField);
					}
					InventoryValidateAndUpdateRequest inventoryRequest = new InventoryValidateAndUpdateRequest(
							inventory.getInventoryId(), new BigDecimal(1),
							inventory.getItemRef(), inventory
									.getModifiedDateTime());
					inventoryRequest.setInventoryId(inventory.getInventoryId());
					inventoryRequest.setAssemblyDataList(assemblyDataList);
					inventoryRequest.setAssyDataTypeRef(inventory
							.getAssyDataTypeRef());
					inventoryServiceInterface
							.validateAndUpdate(inventoryRequest);
					message = FacesUtility
							.getLocaleSpecificText(" Inventory " +this.sfcValue+ " is successfully created . ");
					setMessageBar(true, LSMessageType.INFO);
					updateAssignedSFC(this.chassisno,
							getPreviousSFC(sfcReference));

				} catch (Exception e) {
					MessageHandler.handle(e.getMessage()
							+ e.getStackTrace().toString(), null,
							MessageType.ERROR);
					return;
				}
			}

			else {
				message = FacesUtility
						.getLocaleSpecificText(" SFC / Chassis No should not be empty .");
				setMessageBar(true, LSMessageType.ERROR);
			}
		} catch (Exception e) {
			MessageHandler
					.handle(e.getMessage(), null, MessageType.ERROR, this);
		}
	}

	public void updateAssignedSFC(String assignedSFC, String actualSFC) {
		Connection con = null;
		String qry = "UPDATE Z_WIP_VISIBILITY set ASSIGNED_SFC = ? where SFC = ?";
		PreparedStatement ps = null;
		try {
			con = getConnection();
			ps = con.prepareStatement(qry);
			ps.setString(1, assignedSFC);
			ps.setString(2, actualSFC);
			ps.executeUpdate();
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

	public String getAssignedSFC(String chassisno) {
		String assignedSFC = null;
		Connection con = null;
		String query = "SELECT ASSIGNED_SFC FROM Z_WIP_VISIBILITY WHERE ASSIGNED_SFC like  '"
				+ chassisno + "'";
		PreparedStatement ps = null;
		try {
			con = getConnection();
			ps = con.prepareStatement(query);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				assignedSFC = rs.getString("ASSIGNED_SFC");
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
		return assignedSFC;
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

	public void closePlugin(ActionEvent event) {
		setMessageBar(false, LSMessageType.INFO);
		setMessageBar(false, LSMessageType.ERROR);
		MessageHandler.clear(this);
		closeCurrentPlugin();
		FacesUtility.setSessionMapValue("chassisassignbean", null);
		this.renderPlugin = false;

	}

	public void clear() {
		// clear bean caches
		FacesUtility.removeSessionMapValue("chassisassignbean");
		// Clear message bar area
		this.message = null;
		setMessageBar(false, LSMessageType.ERROR);
		setMessageBar(false, LSMessageType.INFO);
		UIComponent tablePanel = findComponent(FacesUtility.getFacesContext()
				.getViewRoot(), "assignaggPlugin:fieldButtonPanel");
		if (tablePanel != null) {
			FacesUtility.addControlUpdate(tablePanel);
		}
	}

	public void closeConfirmationPopup(ActionEvent event) {

		this.renderConfirmationPopup = false;
		UIComponent fieldButtonPanel = findComponent(FacesUtility
				.getFacesContext().getViewRoot(),
				"assignaggPlugin:fieldButtonPanel");
		if (fieldButtonPanel != null) {
			FacesUtility.addControlUpdate(fieldButtonPanel);
		}
	}

	public void confirmYes(ActionEvent event) throws BusinessException {
		assigntoChassis();
		this.renderConfirmationPopup = false;
	}

	public void confirmNo(ActionEvent event) {
		closeConfirmationPopup(event);
		this.message = null;
		setMessageBar(false, LSMessageType.INFO);
		setMessageBar(false, LSMessageType.ERROR);

	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getChassisno() {
		return chassisno;
	}

	public void setChassisno(String chassisno) {
		this.chassisno = chassisno;
	}

	public Boolean getDisableSfc() {
		return disableSfc;
	}

	public void setDisableSfc(Boolean disableSfc) {
		this.disableSfc = disableSfc;
	}

	public boolean isDisableBrowsePopup() {
		return disableBrowsePopup;
	}

	public void setDisableBrowsePopup(boolean disableBrowsePopup) {
		this.disableBrowsePopup = disableBrowsePopup;
	}

	public List<ChassisSFC> getSfcList() {
		return sfcList;
	}

	public void setSfcList(List<ChassisSFC> sfcList) {
		this.sfcList = sfcList;
	}

	public Boolean getSfcBrowseRendered() {
		return sfcBrowseRendered;
	}

	public void setSfcBrowseRendered(Boolean sfcBrowseRendered) {
		this.sfcBrowseRendered = sfcBrowseRendered;
	}

	public String getSfcValue() {
		return sfcValue;
	}

	public void setSfcValue(String sfcValue) {
		this.sfcValue = sfcValue;
	}

	public boolean isRenderPlugin() {
		return renderPlugin;
	}

	public void setRenderPlugin(boolean renderPlugin) {
		this.renderPlugin = renderPlugin;
	}

	public boolean isRenderConfirmationPopup() {
		return renderConfirmationPopup;
	}

	public void setRenderConfirmationPopup(boolean renderConfirmationPopup) {
		this.renderConfirmationPopup = renderConfirmationPopup;
	}

}
