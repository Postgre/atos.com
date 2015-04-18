package com.atos.assembly_screen;

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
import com.atos.assembly_screenEJB.AssemblyData;
import com.atos.assembly_screenEJB.AssemblyScreenInterface;
import com.sap.me.demand.SFCBOHandle;
import com.sap.me.frame.SystemBase;
import com.sap.me.frame.domain.BusinessException;
import com.sap.me.frame.service.CommonMethods;
import com.sap.me.production.podclient.BasePodPlugin;
import com.sap.me.wpmf.util.FacesUtility;
import com.sap.tc.ls.api.enumerations.LSMessageType;
import com.sap.ui.faces.component.sap.UICommandInputText;

public class AssemblyDataScreenController extends BasePodPlugin implements
		ActionListener {

	private static final long serialVersionUID = 1L;
	private boolean selectProductionLine;
	private boolean selectValidationField;
	private boolean selectMaterial;
	private String searchBy;
	private String productionLine;
	private String sfc;
	private Boolean productionLineBrowseRendered;
	private Boolean materialBrowseRendered;
	private boolean renderMaterialPopup;
	private Boolean sfcBrowseRendered;
	private Boolean disableBrowsePopup;
	private Boolean disableProductionLine;
	private List<MaterialItem> productionLineList = new ArrayList<MaterialItem>();
	private List<MaterialItem> sfcList = new ArrayList<MaterialItem>();
	private List<MaterialItem> materialList = new ArrayList<MaterialItem>();
	private String message;
	private String site = CommonMethods.getSite();
	private String user = CommonMethods.getUserId();
	private List<MaterialCustomDataItem> materialCustomDataList;
	private List<SelectItem> validateElementSelectItem = new ArrayList<SelectItem>();
	private List<SelectItem> radioList = new ArrayList<SelectItem>();

	private String sfcValue;
	private final SystemBase dbBase = SystemBase
			.createSystemBase("jdbc/jts/wipPool");
	private String validationField;
	private String workcenterString = "WORKCENTER";
	private String sfcString = "SFC";
	private MaterialCustomDataItem materialCustomDataItem = null;
	private List<SelectItem> operations;
	private List<SelectItem> operationsSelectItem = new ArrayList<SelectItem>();

	private Connection getConnection() {
		Connection con = null;

		con = dbBase.getDBConnection();

		return con;
	}

	@PostConstruct
	public void init() {
		this.radioList = showRadioButtonValue();
		this.validateElementSelectItem = showValidateSelectItem();
		this.operationsSelectItem = showAllOperation();
	}

	public String insertNewRow() {
		if (materialCustomDataList == null
				|| materialCustomDataList.size() == 0)
			materialCustomDataList = new ArrayList<MaterialCustomDataItem>();
		MaterialCustomDataItem customDataItem = new MaterialCustomDataItem();
		customDataItem.setEditable(true);
		materialCustomDataList.add(customDataItem);
		this.message = null;
		setMessageBar(false, LSMessageType.ERROR);
		setMessageBar(false, LSMessageType.INFO);
		UIComponent tablePanel = findComponent(FacesUtility.getFacesContext()
				.getViewRoot(), "assemblyDataScreenForm:displayPanel");
		if (tablePanel != null) {
			// Adds UIComponent to the list of controls that are re-rendered in
			// the request
			FacesUtility.addControlUpdate(tablePanel);
		}
		return null;
	}

	public String deletSelectedRow() {
		int selectCount = 0;
		for (MaterialCustomDataItem materialItem : this.materialCustomDataList) {
			if (materialItem.isSelect()) {
				selectCount++;
				boolean isDeleted = deleteRowInTable(this.searchBy,this.validationField, materialItem.getHost_operation(),materialItem.getSlave_operation(), materialItem.getValidate_element());
				if (isDeleted) {
					message = FacesUtility
							.getLocaleSpecificText(" Custom data successfully deleted . ");
					setMessageBar(true, LSMessageType.INFO);
				}
				if (this.materialCustomDataList.size() == 0)
				{
					return null;
				}
				List<MaterialCustomDataItem> assemblyCustomDataNewList = new ArrayList<MaterialCustomDataItem>();
				for (MaterialCustomDataItem materialCustomDataItem : this.materialCustomDataList) {
					if (materialCustomDataItem.isSelect()) 
					{
						continue;
					}
					assemblyCustomDataNewList.add(materialCustomDataItem);
				}
				this.materialCustomDataList = assemblyCustomDataNewList;
				UIComponent tablePanel = findComponent(FacesUtility
						.getFacesContext().getViewRoot(),
						"assemblyDataScreenForm:displayPanel");
				if (tablePanel != null) {
					// Adds UIComponent to the list of controls that are
					// re-rendered in
					// the request
					FacesUtility.addControlUpdate(tablePanel);
				}
			}
			if (selectCount == 0) {
				message = FacesUtility
						.getLocaleSpecificText(" Select row(s) to remove .");
				setMessageBar(true, LSMessageType.ERROR);

			}
		}
		return null;
	}

	public String readMaterialData() throws NamingException {
		this.message = null;
		setMessageBar(false, LSMessageType.ERROR);
		setMessageBar(false, LSMessageType.INFO);
		String materialData = null;
		String item = null;
		if (this.searchBy == null) {
			message = FacesUtility
					.getLocaleSpecificText(" Select Searchby value to retrieve data .");
			setMessageBar(true, LSMessageType.ERROR);
		} else {
			if (StringUtils.isNotEmpty(this.validationField)
					&& !"".equals(this.validationField)) {
				String validationFieldCheck = getValidationField(this.searchBy,
						this.validationField);
				if (validationFieldCheck == null) {
					message = FacesUtility.getLocaleSpecificText(" Invalid "
							+ this.searchBy + " : " + this.validationField
							+ ".");
					setMessageBar(true, LSMessageType.ERROR);
				} else {
					materialCustomDataList = new ArrayList<MaterialCustomDataItem>();
					
					Context ctx = new InitialContext();
					AssemblyScreenInterface assemblyDataEJB = (AssemblyScreenInterface) ctx
							.lookup("ejb:/appName=atos.com/apps~ear,jarName=atos.com~apps~ejb.jar, beanName=AseemblyDataScreenBean, interfaceName=com.atos.assembly_screenEJB.AssemblyScreenInterface");
					List<AssemblyData> AssemblyDataList = assemblyDataEJB.readCustomData(this.searchBy, this.validationField);
					for (AssemblyData assemblyData : AssemblyDataList) {
						MaterialCustomDataItem materialCustomDataItem = new MaterialCustomDataItem();
						materialCustomDataItem.setHandle(assemblyData
								.getHandle());
						materialCustomDataItem.setSfc_bo(assemblyData
								.getSfc_bo());
						materialCustomDataItem.setWorkcenter_bo(assemblyData
								.getWorkcenter_bo());
						materialCustomDataItem.setHost_operation(assemblyData
								.getHost_operation());
						materialCustomDataItem.setSlave_operation(assemblyData
								.getSlave_operation());
						materialCustomDataItem.setValidate_element(assemblyData
								.getValidate_element());

						String itemBO = getItem_BO(this.searchBy,this.validationField);
						item= getItem(itemBO);
						materialCustomDataItem.setMaterialItem(item);
						materialCustomDataList.add(materialCustomDataItem);
						materialData = materialCustomDataList.toString();
					}
				}
			} else {
				message = FacesUtility
						.getLocaleSpecificText(" Select valid SFC / WORKCENTER to retrieve data .");
				setMessageBar(true, LSMessageType.ERROR);
			}
		}

		UIComponent tablePanel = findComponent(FacesUtility.getFacesContext()
				.getViewRoot(), "assemblyDataScreenForm:displayPanel");
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

	public String saveMaterialData() throws NamingException
	{
		Context ctx = new InitialContext();
		AssemblyScreenInterface assemblyDataEJB = (AssemblyScreenInterface) ctx
				.lookup("ejb:/appName=atos.com/apps~ear,jarName=atos.com~apps~ejb.jar, beanName=AseemblyDataScreenBean, interfaceName=com.atos.assembly_screenEJB.AssemblyScreenInterface");
		int selectCount = 0;
		AssemblyData assemblyData = new AssemblyData();
		for (MaterialCustomDataItem updating : this.materialCustomDataList) {

			if (this.searchBy == null || "".equals(this.searchBy)) {
				message = FacesUtility
						.getLocaleSpecificText(" Select Searchby value to insert data .");
				setMessageBar(true, LSMessageType.ERROR);
			} else {
				if (StringUtils.isNotEmpty(this.validationField)
						&& !"".equals(this.validationField)) {

					if (updating.isSelect() == true) {
						selectCount++;
						assemblyData.setHost_operation(updating
								.getHost_operation());
						assemblyData.setSlave_operation(updating
								.getSlave_operation());
						assemblyData.setValidate_element(updating
								.getValidate_element());
						if (updating.getHost_operation() == null) {
							message = FacesUtility
									.getLocaleSpecificText(" Host Operation should not be empty .");
							setMessageBar(true, LSMessageType.ERROR);
						} else if (updating.getSlave_operation() == null) {
							message = FacesUtility
									.getLocaleSpecificText(" Slave Operation should not be empty .");
							setMessageBar(true, LSMessageType.ERROR);
						} else if (updating.getValidate_element() == null) {
							message = FacesUtility
									.getLocaleSpecificText(" Validate Element should not be empty .");
							setMessageBar(true, LSMessageType.ERROR);
						} else if(updating.getHost_operation().equals(updating.getSlave_operation()))
						{
							message = FacesUtility
							.getLocaleSpecificText(" Host Operation and Slave Operation should not be same operation .");
							setMessageBar(true, LSMessageType.ERROR);
						}else {
							String validationFieldCheck = getValidationField(
									this.searchBy, this.validationField);
							if (validationFieldCheck == null) {
								message = FacesUtility
										.getLocaleSpecificText(" Invalid "
												+ this.searchBy + " : "
												+ this.validationField + ".");
								setMessageBar(true, LSMessageType.ERROR);
							} else {
								String alreadyExist = checkExistValue(
										this.searchBy, this.validationField,
										updating.getHost_operation(), updating
												.getSlave_operation());
								if (alreadyExist == null) 
								{
									boolean isSuccess = assemblyDataEJB.saveMaterialData(assemblyData,this.searchBy,this.validationField);
									if (isSuccess) {
										message = FacesUtility
												.getLocaleSpecificText(" Custom data successfully inserted .");
										setMessageBar(true, LSMessageType.INFO);
									}
								}
								else {
									message = FacesUtility
											.getLocaleSpecificText(" Given data already exist in database .");
									setMessageBar(true, LSMessageType.ERROR);
								}
							}
						}

					}
				} else {
					message = FacesUtility
							.getLocaleSpecificText(" SFC / WORKCENTER field should not be empty .");
					setMessageBar(true, LSMessageType.ERROR);
				}
			}
		}
		if (selectCount == 0) {
			message = FacesUtility
					.getLocaleSpecificText(" Select row(s) to insert data .");
			setMessageBar(true, LSMessageType.ERROR);
		}
		return null;
	}

	public String checkExistValue(String searchby, String validationField,String op1, String op2) {
		String qry = null;
		String handleValue = null;
		Connection con = null;
		SFCBOHandle sfcBOHandle = new SFCBOHandle(site,validationField);
		String sfcBO = sfcBOHandle.getValue();
		String workcenterBO = "WORKCENTERBO:" + site + "," + validationField;

		qry = "SELECT HANDLE FROM Z_ASSEMBLY_DATA_VALIDATION WHERE ACTIVE = 1 AND HOST_OPERATION = ? AND SLAVE_OPERATION = ? "
				+ " ";

		if ("SFC".equals(searchby)) {
			qry = qry + " AND SFC_BO like '" + sfcBO + "' ";
		}

		if ("WORKCENTER".equals(searchby)) {
			qry = qry + " AND WORKCENTER_BO like '" + workcenterBO + "' ";
		}

		PreparedStatement ps = null;
		try {
			con = getConnection();
			ps = con.prepareStatement(qry);
			ps.setString(1, op1);
			ps.setString(2, op2);
			ResultSet rs = ps.executeQuery();
			if (rs != null) {
				while (rs.next()) {
					handleValue = rs.getString("HANDLE");
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
		return handleValue;
	}

	public String getValidationField(String searchby, String validationField) {
		Connection con = null;
		String qry = null;
		String dataFound = null;
		String attribute = "WORK_CENTER";
		qry = "SELECT " + " ";
		if ("SFC".equals(searchby) && validationField != null) {
			qry = qry + " SFC FROM SFC WHERE SFC like '" + validationField
					+ "' ";
		}

		if ("WORKCENTER".equals(searchby) && validationField != null) {
			qry = qry + " VALUE FROM CUSTOM_FIELDS WHERE ATTRIBUTE like '"
					+ attribute + "' and VALUE like '" + validationField + "' ";
		}

		PreparedStatement ps = null;
		try {
			con = getConnection();
			ps = con.prepareStatement(qry);
			ResultSet rs = ps.executeQuery();
			if (rs != null) {
				while (rs.next()) {
					if ("SFC".equals(searchby) && validationField != null) {
						dataFound = rs.getString("SFC");
					}
					if ("WORKCENTER".equals(searchby)
							&& validationField != null) {
						dataFound = rs.getString("VALUE");
					}
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
		return dataFound;
	}

	public String getItem_BO(String searchby, String validationField) {
		Connection con = null;
		String qry = null;
		String dataFound = null;
		qry = "SELECT " + " ";

		if ("SFC".equals(searchby) && validationField != null) {
			qry = qry + " ITEM_BO FROM SFC WHERE SFC like '" + validationField
					+ "' ";
		}

		if ("WORKCENTER".equals(searchby) && validationField != null) {
			qry = qry + " HANDLE FROM CUSTOM_FIELDS WHERE VALUE like '"
					+ validationField + "' ";
		}

		PreparedStatement ps = null;
		try {
			con = getConnection();
			ps = con.prepareStatement(qry);
			ResultSet rs = ps.executeQuery();
			if (rs != null) {
				while (rs.next()) {
					if ("SFC".equals(searchby) && validationField != null) {
						dataFound = rs.getString("ITEM_BO");
					}
					if ("WORKCENTER".equals(searchby)
							&& validationField != null) {
						dataFound = rs.getString("HANDLE");
					}
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
		return dataFound;
	}

	public String getItem(String itemBO) {
		Connection con = null;
		String qry = null;
		String dataFound = null;
		qry = "SELECT ITEM FROM ITEM WHERE HANDLE like '" + itemBO + "' ";

		PreparedStatement ps = null;
		try {
			con = getConnection();
			ps = con.prepareStatement(qry);
			ResultSet rs = ps.executeQuery();
			if (rs != null) {
				while (rs.next()) {
					dataFound = rs.getString("ITEM");
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
		return dataFound;
	}
	
	public List<MaterialCustomDataItem> getItemList(String itemBO) 
	{
		List<MaterialCustomDataItem> itemList = new ArrayList<MaterialCustomDataItem>();
		Connection con = null;
		String qry = null;
		qry = "SELECT DISTINCT ITEM FROM ITEM WHERE HANDLE like '" + itemBO + "' ";

		PreparedStatement ps = null;
		try {
			con = getConnection();
			ps = con.prepareStatement(qry);
			ResultSet rs = ps.executeQuery();
			if (rs != null) {
				while (rs.next()) 
				{
					MaterialCustomDataItem materialItem = new MaterialCustomDataItem();
					materialItem.setMaterialItem(""+rs.getString("ITEM"));
					itemList.add(materialItem);
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
		return itemList;
	}

	public boolean deleteRowInTable(String searchby, String validationField,
			String hostOperation, String slaveOperation, String validateElement) {
		String qry = null;
		Connection con = null;
		String sfcBO = "SFCBO:" + site + "," + validationField;
		String workcenterBO = "WORKCENTERBO:" + site + "," + validationField;
		boolean isDeleted = false;
		qry = "DELETE FROM Z_ASSEMBLY_DATA_VALIDATION WHERE HOST_OPERATION = ? AND SLAVE_OPERATION = ? AND VALIDATE_ELEMENT = ? "
				+ " ";
		if ("SFC".equals(searchby) && validationField != null) {
			qry = qry + " AND SFC_BO like '" + sfcBO + "' ";
		}
		if ("WORKCENTER".equals(searchby) && validationField != null) {
			qry = qry + " AND WORKCENTER_BO like '" + workcenterBO + "' ";
		}
		PreparedStatement ps = null;
		try {
			con = getConnection();
			ps = con.prepareStatement(qry);
			ps.setString(1, hostOperation);
			ps.setString(2, slaveOperation);
			ps.setString(3, validateElement);
			ps.executeUpdate();
			isDeleted = true;
		} catch (SQLException e) {
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
		return isDeleted;
	}

	public String clear() {
		this.validationField = null;
		this.validationField = "";
		this.message = null;
		FacesUtility.removeSessionMapValue("assemblyDataScreenController");
		setMessageBar(false, LSMessageType.ERROR);
		setMessageBar(false, LSMessageType.INFO);
		UIComponent tablePanel = findComponent(FacesUtility.getFacesContext()
				.getViewRoot(), "assemblyDataScreenForm:displayPanel");
		if (tablePanel != null) {
			FacesUtility.addControlUpdate(tablePanel);
		}
		return null;
	}

	public void radioButtonValueChange(ValueChangeEvent event) throws Exception {
		if (!"".equals(event.getNewValue())) {
			clear();
		}
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
				"assemblyDataScreenForm:messageBar");
		HtmlOutputText messageBarError = (HtmlOutputText) findComponent(FacesUtility
				.getFacesContext().getViewRoot(),
				"assemblyDataScreenForm:messageBar2");
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
				"assemblyDataScreenForm:fieldButtonPanel");
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
			this.validationField = selectedMaterial.getMaterial();
			// this.productionLine = selectedMaterial.getWorkcenter();
		}
		if (StringUtils.isNotEmpty(this.validationField)) {
			this.disableBrowsePopup = false;
		} else {
			this.disableBrowsePopup = true;
		}
	}

	public void rowSelectedSfc(ActionEvent event) {

		closeSfcBrowse(event);
		// set value for input field
		MaterialItem selectedMaterial = getSelectedSfc();
		if (selectedMaterial != null) {
			this.validationField = selectedMaterial.getMaterial();
			// this.productionLine = selectedMaterial.getWorkcenter();
		}
		if (StringUtils.isNotEmpty(this.validationField)) {
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

	private MaterialItem getSelectedSfc() {
		MaterialItem selectedMaterialItem = null;
		if (sfcList != null) {
			for (int i = 0; i < this.sfcList.size(); i++) {
				MaterialItem materialItem = sfcList.get(i);
				if (materialItem != null && materialItem.getSelected()) {
					selectedMaterialItem = materialItem;
					break;
				}

			}
		}
		return selectedMaterialItem;
	}

	public List<SelectItem> showRadioButtonValue() {
		clear();
		List<SelectItem> list = new ArrayList<SelectItem>();
		list.add(new SelectItem("SFC", "SFC"));
		list.add(new SelectItem("WORKCENTER", "WORKCENTER"));
		return list;

	}

	public List<SelectItem> showValidateSelectItem() {
		List<SelectItem> list = new ArrayList<SelectItem>();
		list.add(new SelectItem("VENDOR", "VENDOR"));
		list.add(new SelectItem("QUALITY", "QUALITY"));
		list.add(new SelectItem("ALL", "ALL"));
		return list;

	}

	public List<SelectItem> showAllOperation() {
		Connection con = null;
		String query = "SELECT DISTINCT OPERATION FROM OPERATION";
		PreparedStatement ps = null;
		operations = new ArrayList<SelectItem>();
		try {
			con = getConnection();
			ps = con.prepareStatement(query);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				String operation = rs.getString("OPERATION");
				operations.add(new SelectItem(operation));

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
		return operations;
	}

	public void showProductionLineBrowse(ActionEvent event) {
		this.productionLineBrowseRendered = true;
		try {
			this.productionLineList = workcenterBrowse(this.productionLine);

		} catch (BusinessException businessException) {
			message = FacesUtility
					.getLocaleSpecificText(" Business exception while finding work center .");
			setMessageBar(true, LSMessageType.INFO);
		}

		if (StringUtils.isNotEmpty(this.productionLine)) {
			this.disableBrowsePopup = false;
		} else {
			this.disableBrowsePopup = true;
		}
	}

	public void showMaterialBrowse(ActionEvent event) {
		for (MaterialCustomDataItem materialItem : this.materialCustomDataList) {
			try {
				this.materialList = findAllMaterial(materialItem
						.getMaterialItem());
			} catch (BusinessException exp) {
				// Display INFO message on GUI
				message = FacesUtility
						.getLocaleSpecificText("Business Exception while finding the Materials");
				setMessageBar(true, LSMessageType.ERROR);
			}

			this.materialBrowseRendered = true;
			this.productionLineBrowseRendered = false;
			if (StringUtils.isNotEmpty(materialItem.getMaterialItem())
					|| StringUtils.isNotEmpty(this.productionLine)) {
				this.disableBrowsePopup = false;
			} else {
				this.disableBrowsePopup = true;
			}
		}

	}

	public void rowSelectedMaterial(ActionEvent event) {
		closeMaterialBrowse(event);
		MaterialCustomDataItem currentRow = new MaterialCustomDataItem();
		MaterialItem selectedMaterial = getSelectedMaterial();
		if (selectedMaterial != null) {
			String value = selectedMaterial.getMaterial();
			currentRow.setMaterialItem(value);

		}
		if (StringUtils.isNotEmpty(currentRow.getMaterialItem())) {
			this.disableBrowsePopup = false;
		} else {
			this.disableBrowsePopup = true;
		}
	}

	public void closeMaterialBrowse(ActionEvent event) {

		this.materialBrowseRendered = false;
	}

	public List<MaterialItem> findAllMaterial(String material)
			throws BusinessException 
	{

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

	public void showValidationBrowse(ActionEvent event)
	{
		this.message = null;
		setMessageBar(false, LSMessageType.INFO);
		setMessageBar(false, LSMessageType.ERROR);
		if (sfcString.equals(this.searchBy)) {
			try {
				this.sfcList = sfcBrowse(this.validationField);
				if(this.sfcList.size()!=0){
					this.sfcBrowseRendered = true;
				}
				else{
					message = FacesUtility.getLocaleSpecificText(" Your query returned no data .");
					setMessageBar(true, LSMessageType.ERROR);
				}

			} catch (BusinessException businessException) {
				message = FacesUtility
						.getLocaleSpecificText(" Business exception while finding sfc .");
				setMessageBar(true, LSMessageType.INFO);
			}
			if (StringUtils.isNotEmpty(this.validationField)) {
				this.disableBrowsePopup = false;
			} else {
				this.disableBrowsePopup = true;
			}
		} else if (workcenterString.equals(this.searchBy)) {
			
			try {
				this.productionLineList = workcenterBrowse(this.validationField);
				if(this.productionLineList.size()!=0){
					this.productionLineBrowseRendered = true;
				}
				else{
					message = FacesUtility.getLocaleSpecificText(" Your query returned no data .");
					setMessageBar(true, LSMessageType.ERROR);
				}
			} catch (BusinessException businessException) {
				message = FacesUtility
						.getLocaleSpecificText(" Business exception while finding Workcenter .");
				setMessageBar(true, LSMessageType.INFO);
			}
			if (StringUtils.isNotEmpty(this.validationField)) {
				this.disableBrowsePopup = false;
			} else {
				this.disableBrowsePopup = true;
			}
		} else {
			message = FacesUtility
					.getLocaleSpecificText(" Select Searchby value to browse validation field .");
			setMessageBar(true, LSMessageType.ERROR);
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

	public void closeSfcBrowse(ActionEvent event) {

		this.sfcBrowseRendered = false;
	}

	public void setSelectValidationField(boolean selectValidationField) {
		Map<String, Object> requestMap = FacesContext.getCurrentInstance()
				.getExternalContext().getRequestMap();
		MaterialItem currentRow = (MaterialItem) requestMap
				.get("validationFieldBrowse");

		currentRow.setSelected(new Boolean(selectValidationField));
	}

	public void setSelectValue(boolean checked) {
		Map<String, Object> requestMap = FacesContext.getCurrentInstance()
				.getExternalContext().getRequestMap();
		MaterialCustomDataItem currentRow = (MaterialCustomDataItem) requestMap
				.get("rows");
		currentRow.setSelect(checked);

	}

	public boolean getSelectValidationField() {
		Map<String, Object> requestMap = FacesContext.getCurrentInstance()
				.getExternalContext().getRequestMap();
		MaterialItem currentRow = (MaterialItem) requestMap
				.get("validationFieldBrowse");

		if (currentRow != null) {
			return currentRow.getSelected().booleanValue();
		}
		return false;
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

	public void setSelectMatarial(boolean selectMaterial) {
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

	public List<MaterialItem> workcenterBrowse(String productionLine)
			throws BusinessException {
		Connection con = null;
		String query = "select DISTINCT VALUE FROM CUSTOM_FIELDS WHERE ATTRIBUTE = ? "
				+ " ";
		if (StringUtils.isNotBlank(productionLine)) {
			query = query + "  and VALUE like '" + productionLine + "%'";
		}
		PreparedStatement ps = null;
		List<MaterialItem> user = new ArrayList<MaterialItem>();
		try {
			con = getConnection();
			ps = con.prepareStatement(query);
			ps.setString(1, "WORK_CENTER");
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				MaterialItem downloadVO = new MaterialItem();
				downloadVO.setMaterial("" + rs.getString("VALUE"));
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

	public List<MaterialItem> sfcBrowse(String validationField)
			throws BusinessException {
		Connection con = null;
		String query = "SELECT DISTINCT SFC FROM SFC WHERE SFC IS NOT NULL ";
		if (StringUtils.isNotBlank(validationField)) {
			query = query + "  and SFC like '" + validationField + "%'";
		}
		PreparedStatement ps = null;
		List<MaterialItem> user = new ArrayList<MaterialItem>();
		try {
			con = getConnection();
			ps = con.prepareStatement(query);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				MaterialItem downloadVO = new MaterialItem();
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

	public String getSfcValue() {
		return sfcValue;
	}

	public void setSfcValue(String sfcValue) {
		this.sfcValue = sfcValue;
	}

	public String getSearchBy() {
		return searchBy;
	}

	public void setSearchBy(String searchBy) {
		this.searchBy = searchBy;
	}

	public String getValidationField() {
		return validationField;
	}

	public void setValidationField(String validationField) {
		this.validationField = validationField;
	}

	public Boolean getSfcBrowseRendered() {
		return sfcBrowseRendered;
	}

	public void setSfcBrowseRendered(Boolean sfcBrowseRendered) {
		this.sfcBrowseRendered = sfcBrowseRendered;
	}

	public List<MaterialItem> getSfcList() {
		return sfcList;
	}

	public void setSfcList(List<MaterialItem> sfcList) {
		this.sfcList = sfcList;
	}

	public String getSfc() {
		return sfc;
	}

	public void setSfc(String sfc) {
		this.sfc = sfc;
	}

	public List<SelectItem> getRadioList() {
		return radioList;
	}

	public void setRadioList(List<SelectItem> radioList) {
		this.radioList = radioList;
	}

	public List<SelectItem> getValidateElementSelectItem() {
		return validateElementSelectItem;
	}

	public void setValidateElementSelectItem(
			List<SelectItem> validateElementSelectItem) {
		this.validateElementSelectItem = validateElementSelectItem;
	}

	public Boolean getMaterialBrowseRendered() {
		return materialBrowseRendered;
	}

	public void setMaterialBrowseRendered(Boolean materialBrowseRendered) {
		this.materialBrowseRendered = materialBrowseRendered;
	}

	public List<MaterialItem> getMaterialList() {
		return materialList;
	}

	public void setMaterialList(List<MaterialItem> materialList) {
		this.materialList = materialList;
	}

	public boolean isRenderMaterialPopup() {
		return renderMaterialPopup;
	}

	public void setRenderMaterialPopup(boolean renderMaterialPopup) {
		this.renderMaterialPopup = renderMaterialPopup;
	}

	public MaterialCustomDataItem getMaterialCustomDataItem() {
		return materialCustomDataItem;
	}

	public void setMaterialCustomDataItem(
			MaterialCustomDataItem materialCustomDataItem) {
		this.materialCustomDataItem = materialCustomDataItem;
	}

	public List<SelectItem> getOperations() {
		return operations;
	}

	public void setOperations(List<SelectItem> operations) {
		this.operations = operations;
	}

	public List<SelectItem> getOperationsSelectItem() {
		return operationsSelectItem;
	}

	public void setOperationsSelectItem(List<SelectItem> operationsSelectItem) {
		this.operationsSelectItem = operationsSelectItem;
	}

}
