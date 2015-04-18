package com.atos.serialization;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;

import com.atos.wpmf.web.podplugin.postscanactivities.PODCustomException;
import com.sap.me.common.ObjectReference;
import com.sap.me.demand.SFCBOHandle;
import com.sap.me.extension.Services;
import com.sap.me.frame.SystemBase;
import com.sap.me.frame.domain.BusinessException;
import com.sap.me.frame.domain.InvalidInputException;
import com.sap.me.frame.service.CommonMethods;
import com.sap.me.productdefinition.InvalidRouterOperationException;
import com.sap.me.productdefinition.ItemConfigurationServiceInterface;
import com.sap.me.productdefinition.SingleQuantityRestrictionException;
import com.sap.me.productdefinition.WholeQuantityRestrictionException;
import com.sap.me.production.FindSfcByNameRequest;
import com.sap.me.production.FindSfcDataBySfcRequest;
import com.sap.me.production.FindSfcDataBySfcResponse;
import com.sap.me.production.InvalidQuantityForNewSfcWithLocationException;
import com.sap.me.production.InvalidQuantityValueException;
import com.sap.me.production.InvalidSfcException;
import com.sap.me.production.InvalidSfcOwnerShipException;
import com.sap.me.production.InvalidSfcRoutingException;
import com.sap.me.production.MissingSfcLocationToSerializeException;
import com.sap.me.production.NonUniqueSfcException;
import com.sap.me.production.NotEnoughQuantityException;
import com.sap.me.production.SerializeSfcDetail;
import com.sap.me.production.SerializeSfcRequest;
import com.sap.me.production.SerializeSfcResponse;
import com.sap.me.production.SfcBasicData;
import com.sap.me.production.SfcConfiguration;
import com.sap.me.production.SfcDataField;
import com.sap.me.production.SfcDataServiceInterface;
import com.sap.me.production.SfcDisabledException;
import com.sap.me.production.SfcIdHistory;
import com.sap.me.production.SfcKeyData;
import com.sap.me.production.SfcLocationOnHoldException;
import com.sap.me.production.SfcLocationScrappedException;
import com.sap.me.production.SfcLocationSerializedException;
import com.sap.me.production.SfcStateServiceInterface;
import com.sap.me.production.SplitSerializeServiceInterface;
import com.sap.me.production.SplitSfcResponse;
import com.sap.me.production.podclient.BasePodPlugin;
import com.sap.me.production.podclient.PodSelectionModelInterface;
import com.sap.me.production.podclient.SfcChangeEvent;
import com.sap.me.production.podclient.SfcChangeListenerInterface;
import com.sap.me.production.podclient.SfcSelection;
import com.sap.me.security.RunAsServiceLocator;
import com.sap.me.wpmf.MessageType;
import com.sap.me.wpmf.util.FacesUtility;
import com.sap.me.wpmf.util.MessageHandler;
import com.sap.tc.ls.api.enumerations.LSMessageType;
import com.visiprise.frame.configuration.ServiceReference;
import com.visiprise.model.SplitRequestSFC;

public class SFCSerializationNoLimit extends BasePodPlugin {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String serializedSFCvalue;
	private String serializeValue;
	private String actualSfc;
	private SplitSerializeServiceInterface serializeServiceInterface;
	private ItemConfigurationServiceInterface itemConfigurationServiceInterface;
	private SfcStateServiceInterface sfcStateService;
	private SfcDataServiceInterface sfcDataServiceInterface;;
	private String site;
	private String user;
	private List<SfcSelection> sfcList;
	private String sfcRef;
	private static Logger log = Logger.getLogger(SFCSerializationNoLimit.class
			.getName());
	private final SystemBase dbBase = SystemBase
			.createSystemBase("jdbc/jts/wipPool");

	private Connection getConnection() {
		Connection con = null;
		con = dbBase.getDBConnection();
		return con;
	}

	public SFCSerializationNoLimit() {
		super();
		this.site = CommonMethods.getSite();
		this.user = CommonMethods.getUserId();
		initServices();
	}

	@PostConstruct
	public void init() {
		this.actualSfc = getPodSFCFieldData();
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

	public void updateSerializedSFC(String serializedSFC, String actualSfc) {
		Connection con = null;
		String qry = "UPDATE Z_WIP_VISIBILITY set SFC = ? where SFC = ?";
		PreparedStatement ps = null;
		try {
			con = getConnection();
			ps = con.prepareStatement(qry);
			ps.setString(1, serializedSFC);
			ps.setString(2, actualSfc);
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

	public boolean isSfcStarted() throws BusinessException {
		boolean isStarted = false;
		try {
			initServices();
			SfcBasicData sfcBasicData = new SfcBasicData();
			SFCBOHandle sfcBOHandle = new SFCBOHandle(site, this.actualSfc);
			String sfcReference = sfcBOHandle.getValue();
			sfcBasicData = sfcStateService
					.findSfcDataByRef(new ObjectReference(sfcReference));
			isStarted = sfcBasicData.getActive();
		} catch (NullPointerException ne) {
			ne.getMessage();
			MessageHandler.handle("SFC " + this.actualSfc
					+ " is not in Active status.", null, MessageType.ERROR,
					this);
		}
		return isStarted;
	}

	public void serializeSFC() throws BusinessException

	{
		String item = null;
		String itemRef = null;
		String sfcFound = null;
		if (this.actualSfc == null) 
		{
			MessageHandler.handle("Select atlease one SFC to perform serialize operation .", null,
					MessageType.ERROR, this);
		} else {
			SFCBOHandle sfcBOHandle = new SFCBOHandle(site, this.actualSfc);
			String sfcReference = sfcBOHandle.getValue();
			try {
				initServices();
				
				SfcBasicData sfcBasicData = sfcStateService.findSfcDataByRef(new ObjectReference(sfcReference));
				sfcFound = sfcBasicData.getSfc();
				itemRef = sfcBasicData.getItemRef();
				item=itemConfigurationServiceInterface.findItemKeyDataByRef(new ObjectReference(itemRef)).getItem();
				//serializeValue = "P"+item+"#T"+newDate+serializedSFCvalue+"#VVECV#";
				
			} catch (Exception e) {
				MessageHandler.handle(
						" Given SFC value is invalid. Provide valid SFC . ",
						null, MessageType.ERROR, this);
			}
			if (sfcFound == null || "".equals(sfcFound)) {
				MessageHandler.handle(
						" Given SFC value is invalid. Provide valid SFC . ",
						null, MessageType.ERROR, this);
			} else {
				boolean isStarted = isSfcStarted();
				if (isStarted) {
					MessageHandler.clear(this);
					try {
						sfcList = getPodSelectionModel().getResolvedSfcs();
						if (sfcList == null || sfcList.size() == 0) {
							MessageHandler.handle(
									"Select atlease one SFC to perform serialize operation .",
									null, MessageType.ERROR, this);
							return;
						}
						if (sfcList.size() > 1) {
							MessageHandler.handle(
									"Select only one SFC to to perform serialize operation .", null,
									MessageType.ERROR, this);
							return;
						}
						if (this.serializedSFCvalue.equals("")) {
							MessageHandler.handle("Enter EIN to to perform serialize operation .",
									null, MessageType.ERROR, this);
							return;
						}
						if (this.serializedSFCvalue.contains(" ")) {
							MessageHandler.handle(
									"EIN should not contain spaces .", null,
									MessageType.ERROR, this);
							return;
						}
						if (this.serializedSFCvalue.length() != this.serializedSFCvalue
								.trim().length()) {
							MessageHandler.handle(
									"EIN should not contain spaces .", null,
									MessageType.ERROR, this);
							return;
						}
						Date currDate = new java.util.Date();
						String DATE_FORMAT = "MMddyyyy";
					    SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
					    String newDate = sdf.format(currDate);
						serializeValue = "P"+item+"#T"+newDate+serializedSFCvalue+"#VVECV#";
						FindSfcByNameRequest sfcNameRequest = new FindSfcByNameRequest();
						sfcNameRequest.setSfc(serializeValue);
						SfcBasicData sfcbasicdata = new SfcBasicData();
						sfcbasicdata = sfcStateService
								.findSfcByName(sfcNameRequest);
						if (sfcbasicdata != null) {
							MessageHandler.handle(
									"Given EIN " +serializeValue+ "is already serialized .", null,
									MessageType.ERROR, this);
							return;
						}

						SfcKeyData sfcKeyData = sfcList.get(0).getSfc();
						sfcRef = sfcKeyData.getSfcRef();
						SerializeSfcDetail serializeSfcDetail = new SerializeSfcDetail();
						serializeSfcDetail.setSfc(serializeValue);
						List<SerializeSfcDetail> newSfcList = new ArrayList<SerializeSfcDetail>();
						newSfcList.add(serializeSfcDetail);
						SerializeSfcRequest serializeSfcRequest = new SerializeSfcRequest(sfcRef);
						serializeSfcRequest.setNewSfcList(newSfcList);
						serializeSfcRequest.setQuantityToSerialize(new BigDecimal(1));
						Collection<SerializeSfcResponse> serializeResponse = serializeServiceInterface.serializeSfc(serializeSfcRequest);
						closePlugin();
						if (serializeResponse.size() > 0) 
						{
							List<SfcSelection> sfcsSerialized = new ArrayList<SfcSelection>();
							SfcSelection sfcSelection = new SfcSelection();
							sfcSelection.setInputId(serializeValue);
							sfcsSerialized.add(sfcSelection);
							getPodSelectionModel().setSfcs(sfcsSerialized);
							SfcChangeEvent sfcChangeEvent = new SfcChangeEvent(this);
							this.fireEvent(sfcChangeEvent,SfcChangeListenerInterface.class,"processSfcChange");
						}
					} catch (BusinessException e) {
						// TODO Auto-generated catch block
						log
								.severe("Error during SFC serialization serializeSFC(): "
										+ e.toString());
						MessageHandler.handle(e.getMessage(), null,
								MessageType.ERROR, this);

					} catch (Exception e) {
						log
								.severe("Error during SFC serialization serializeSFC(): "
										+ e.toString());
						MessageHandler.handle(e.getMessage(), null,
								MessageType.ERROR, this);
					}
				}
			}
		}

	}

	public void initServices() {
		ServiceReference sfcStateServiceRef = new ServiceReference(
				"com.sap.me.production", "SfcStateService");
		sfcStateService = RunAsServiceLocator.getService(sfcStateServiceRef,
				SfcStateServiceInterface.class, this.user, this.site, null);
		ServiceReference serializeServiceRef = new ServiceReference(
				"com.sap.me.production", "SplitSerializeService");
		serializeServiceInterface = RunAsServiceLocator.getService(
				serializeServiceRef, SplitSerializeServiceInterface.class,
				this.user, this.site, null);
		ServiceReference itemServiceRef = new ServiceReference(
				"com.sap.me.productdefinition", "ItemConfigurationService");
		itemConfigurationServiceInterface = RunAsServiceLocator.getService(
				itemServiceRef, ItemConfigurationServiceInterface.class,
				user, site, null);
	}

	public void closePlugin() {
		// Closes the current plugin, clears messages in the global area
		MessageHandler.clear(this);
		closeCurrentPlugin();

		FacesUtility.setSessionMapValue("serializationPlugin2", null);

	}

	public String getSerializedSFCvalue() {
		return serializedSFCvalue;
	}

	public void setSerializedSFCvalue(String serializedSFCvalue) {
		this.serializedSFCvalue = serializedSFCvalue;
	}

	public String getActualSfc() {
		return actualSfc;
	}

	public void setActualSfc(String actualSfc) {
		this.actualSfc = actualSfc;
	}

	public String getSerializeValue() {
		return serializeValue;
	}

	public void setSerializeValue(String serializeValue) {
		this.serializeValue = serializeValue;
	}
	
}
