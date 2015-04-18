package com.atos.wpmf.web.podplugin;

import java.util.ArrayList;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import com.sap.me.common.ObjectReference;
import com.sap.me.demand.SFCBOHandle;
import com.sap.me.extension.Services;
import com.sap.me.frame.domain.BusinessException;
import com.sap.me.frame.service.CommonMethods;
import com.sap.me.labor.LaborLogServiceInterface;
import com.sap.me.productdefinition.AttachedToContext;
import com.sap.me.productdefinition.AttachmentConfigurationServiceInterface;
import com.sap.me.productdefinition.AttachmentType;
import com.sap.me.productdefinition.FindAttachmentByAttachedToContextRequest;
import com.sap.me.productdefinition.FindAttachmentByAttachedToContextResponse;
import com.sap.me.productdefinition.FoundReferencesResponse;
import com.sap.me.productdefinition.InvalidOperationException;
import com.sap.me.productdefinition.OperationBOHandle;
import com.sap.me.productdefinition.OperationKeyData;
import com.sap.me.productdefinition.WorkInstructionConfigurationServiceInterface;
import com.sap.me.productdefinition.WorkInstructionFullConfiguration;
import com.sap.me.productdefinition.client.WorkInstructionDTO;
import com.sap.me.production.FindSfcByNameRequest;
import com.sap.me.production.SfcBasicData;
import com.sap.me.production.SfcStateServiceInterface;
import com.sap.me.production.podclient.BasePodPlugin;
import com.sap.me.production.podclient.PodSelectionModelInterface;
import com.sap.me.user.UserConfigurationServiceInterface;
import com.sap.me.wpmf.CancelProcessingException;
import com.sap.me.wpmf.MessageType;
import com.sap.me.wpmf.util.FacesUtility;
import com.sap.me.wpmf.util.MessageHandler;
import com.sap.ui.faces.component.sap.UIInputText;

public class LaborOnOff extends BasePodPlugin {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private boolean defaultPlugin = false;
	private String sfcStarted;
	LaborLogServiceInterface laborLogService;
	UserConfigurationServiceInterface userConfigService;
	
	private AttachmentConfigurationServiceInterface attachmentConfigurationService = Services
			.getService("com.sap.me.productdefinition",
					"AttachmentConfigurationService");
	
			
	private WorkInstructionConfigurationServiceInterface wiConfigurationService = Services
			.getService("com.sap.me.productdefinition",
					"WorkInstructionConfigurationService");
	private SfcStateServiceInterface sfcStateServiceInterface = Services
			.getService("com.sap.me.production", "SfcStateService");
	
	
	WorkInstructionDTO wIDTo = new WorkInstructionDTO();

	
	
	public static final String LEGACY_PLUGIN_APP_CONTEXT = "/manufacturing";

	
	private String reportUrl = null;
	private boolean renderWI = false;

	public boolean isRenderWI() {
		return renderWI;
	}

	public void setRenderWI(boolean renderWI) {
		this.renderWI = renderWI;
	}
	
	public void beforeLoad() throws Exception {
		this.renderWI=true;
		PodSelectionModelInterface selectionModel = getPodSelectionModel();
		  List<OperationKeyData> operationList =null;
		  try {
			//Find the work Instructions attached to operation
			  operationList=selectionModel.getRouterStepsOrOperation();
			  String operationRef = null;
			  String materialRef = null;
				if (operationList != null && operationList.size()>0 ) {
					operationRef = operationList.get(0).getRef();
				}
				if(operationRef == null){
					reportUrl= "";
					return;
				}
				if(selectionModel.getSfcs()!=null && selectionModel.getSfcs().size()>0){
					this.sfcStarted=selectionModel.getSfcs().get(0).getInputId();
				}
				
			  AttachedToContext attachedToContext=new AttachedToContext();
			  attachedToContext.setOperationRef(operationRef);
			  WorkInstructionFullConfiguration workInstr=findWorkInstruction(attachedToContext);
			  
			  //Find the work Instructions attached to item if not attached to operation 
			  if(workInstr==null){ 
				  //finding component to retrieve sfc value				 
				  if(getPodSFCFieldData() != null || (!getPodSFCFieldData().equals(""))){					 
						 String sfc = getPodSFCFieldData();
						 SfcBasicData basicData= sfcStateServiceInterface.findSfcByName(new FindSfcByNameRequest(sfc));
						 materialRef = basicData.getItemRef();
				   }				  
				  attachedToContext=new AttachedToContext();
				  attachedToContext.setItemRef(materialRef);
				  workInstr=findWorkInstruction(attachedToContext);
			  }				  
			  if(workInstr!=null)
				  reportUrl=workInstr.getUrl(); 
			 } catch (InvalidOperationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (BusinessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		

	}
	
	public void setReportUrl(String reportUrl) {
		// HttpServletRequest httpRequest = (HttpServletRequest)
		// FacesContext.getCurrentInstance().getExternalContext().getRequest();
		// String requestUrl = httpRequest.getRequestURL().toString();
		// String requestUrlFirstPart =
		// requestUrl.split(httpRequest.getContextPath())[0];
		this.reportUrl = reportUrl;
	}

	
	/**
	 * Returns the SfcStep list
	 * 
	 * @return sfcStepList
	 */
	public String getReportUrl() {
//			HttpServletRequest httpRequest = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
//			  String requestUrl = httpRequest.getRequestURL().toString();
//			  String requestUrlFirstPart = requestUrl.split(httpRequest.getContextPath())[0];
//			  reportUrl = requestUrlFirstPart + LEGACY_PLUGIN_APP_CONTEXT + "/index.jsp";
//			  reportUrl="http://www.w3schools.com";
//			  reportUrl= "file://Winsrv2k8r2/wi/20_16_CABIN_CHECK_SHEET.pdf";
		
			  return reportUrl;
		}

	private WorkInstructionFullConfiguration findWorkInstruction(
			AttachedToContext attachedToContext) throws BusinessException {
		FindAttachmentByAttachedToContextRequest findAttachmentByAttachedToContextRequest = new FindAttachmentByAttachedToContextRequest(
				attachedToContext, false, false, AttachmentType.WORKINSTRUCTION);
		FindAttachmentByAttachedToContextResponse findAttachmentByAttachedToContextResponse = attachmentConfigurationService
				.findAttachmentByAttacheds(findAttachmentByAttachedToContextRequest);
		List<FoundReferencesResponse> foundReferencesResponseList = findAttachmentByAttachedToContextResponse
				.getFoundReferencesResponseList();
		WorkInstructionFullConfiguration workInstr = null;
		for (FoundReferencesResponse response : foundReferencesResponseList) {
			for (String ref : response.getRefList()) {
				workInstr = this.wiConfigurationService
						.readWorkInstruction(new ObjectReference(ref));
			}

		}
		return workInstr;
	}

	public String getAftrDwnload() {
		System.out.println("inside afterdwnload method ....");
		try {
			executePluginButtonActivityWithFeedback("Z_CURSOR_POS");
			} catch (CancelProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (AbortProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}

	public void setAftrDwnload() {

	}

	public LaborOnOff() {
		super();
		
	}

	public String afterLoad() {
		System.out.println("inside after load");
		return null;
	}

	private String userid;

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String showPluginHelp1() {
		// Returns the absolute url by appending the input partial url
		String helpUrl = FacesUtility
				.getAbsoluteUrl("/com/atos/wpmf/podplugin/SamplePluginHelp.html");
		// Displays the input URL in a Internet Explorer window.
		postWindow(helpUrl, null, 0, 0, 0, 0, false, true, true, false);
		return "";
	}

	public void closePlugin(ActionEvent event) {
		// Closes the current plugin, clears messages in the global area
		MessageHandler.clear(this);
		closeCurrentPlugin();
		this.reportUrl="";
		FacesUtility.setSessionMapValue("laboronPlugin", null);
		this.renderWI=false;

	}

	public boolean isDefaultPlugin() {
		return defaultPlugin;
	}






	public void initServices() {
		laborLogService = Services.getService("com.sap.me.labor",
				"LaborLogService");
		userConfigService = Services.getService("com.sap.me.user",
				"UserConfigurationService");

	}
	private String getPodSFCFieldData(){
		PodSelectionModelInterface selectionModel = getPodSelectionModel();
		if (selectionModel == null) {
			return "";
		}
		String sfcRef = "";
		
			if(selectionModel.getSfcs()!=null){
				sfcRef=selectionModel.getSfcs().get(0).getInputId();
				SFCBOHandle sfcboHandle = new SFCBOHandle(CommonMethods.getSite(),sfcRef);
				sfcRef=sfcboHandle.getValue();
			}
		return sfcRef;
	}

	
	public void processPluginLoaded() {

		
	}

}
