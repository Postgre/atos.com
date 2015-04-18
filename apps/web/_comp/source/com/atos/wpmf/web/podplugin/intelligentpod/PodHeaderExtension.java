package com.atos.wpmf.web.podplugin.intelligentpod;

import java.util.ArrayList;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.servlet.http.HttpSession;

import com.atos.wpmf.web.podplugin.intelligentpod.postscanactivities.CompleteActiveSfc;
import com.atos.wpmf.web.podplugin.intelligentpod.postscanactivities.PODCustomException;
import com.atos.wpmf.web.podplugin.intelligentpod.postscanactivities.PVTCheck;
import com.atos.wpmf.web.podplugin.intelligentpod.postscanactivities.StartInQueueSfc;
import com.sap.me.activity.ActivityConfigurationServiceInterface;
import com.sap.me.activity.ActivityOption;
import com.sap.me.activity.FindActivityOptionsRequest;
import com.sap.me.common.ObjectReference;
import com.sap.me.container.InvalidSFCException;
import com.sap.me.demand.SFCBOHandle;
import com.sap.me.extension.Services;
import com.sap.me.frame.domain.BusinessException;
import com.sap.me.frame.service.CommonMethods;
import com.sap.me.plant.ResourceKeyData;
import com.sap.me.productdefinition.InvalidOperationException;
import com.sap.me.productdefinition.OperationBOHandle;
import com.sap.me.productdefinition.OperationKeyData;
import com.sap.me.production.SfcBasicData;
import com.sap.me.production.SfcStateServiceInterface;
import com.sap.me.production.podclient.BasePodPlugin;
import com.sap.me.production.podclient.PodSelectionModelInterface;
import com.sap.me.production.podclient.SfcChangeEvent;
import com.sap.me.production.podclient.SfcChangeListenerInterface;
import com.sap.me.production.podclient.SfcSelection;
import com.sap.me.status.StatusBasicConfiguration;
import com.sap.me.status.StatusServiceInterface;
import com.sap.me.wpmf.CancelProcessingException;
import com.sap.me.wpmf.MessageType;
import com.sap.me.wpmf.util.FacesUtility;
import com.sap.me.wpmf.util.MessageHandler;

public class PodHeaderExtension extends BasePodPlugin {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public PodHeaderExtension() {
		super();
		// TODO Auto-generated constructor stub
		
	}

	private String operationConfirmStr = "Operation Confirmation";
	private String operationConfirmation;
	private SfcStateServiceInterface sfcStateService = null;
	private StatusServiceInterface statusService = null;
	private String siteVal = CommonMethods.getSite();
	private String operationRef;
	private String resourceRef;
	public String getOperationConfirmStr() {
		return operationConfirmStr;
	}
	public void setOperationConfirmStr(String operationConfirmStr) {
		this.operationConfirmStr = operationConfirmStr;
	}
	public String getOperationConfirmation() {
		return operationConfirmation;
	}
	public void setOperationConfirmation(String operationConfirmation) {
		this.operationConfirmation = operationConfirmation;		
	}
	public String postScanOperation(){
		if(this.operationConfirmation.equals("")){
			 	UIComponent comp = null;
				UIComponent panel = getContainer();
				panel = findComponent(FacesUtility.getFacesContext().getViewRoot(),"podSelection");
				List<String> excludedIdList = new ArrayList<String>();
			//	excludedIdList.add("INPUT_ID_Label");
				excludedIdList.add("CustomLabel1");
				comp = findComponent(panel, "OperationConfrm", excludedIdList);
				setComponentFocus(comp.getClientId(FacesContext.getCurrentInstance()));
			return "";
		}
		init();		
		PodSelectionModelInterface selectionModel = getPodSelectionModel();
		if (selectionModel == null) {
			
			return "";
		}
		List<OperationKeyData> operationList=null;
		try {
			operationList = selectionModel.getRouterStepsOrOperation();
			//fetch the operation selected in POD
			if (operationList != null) {
				
				OperationBOHandle operBO = new OperationBOHandle(CommonMethods.getSite(), operationList.get(0).getOperation(), "#");
				operationRef=operBO.getValue();
			}
			// If operation is null, simply exit.
			if (operationRef == null) {
				return "";
			}
			//fetch the resource selected in POD
			ResourceKeyData resourceKeydata = selectionModel.getResolvedResource();
			if (resourceKeydata != null) {
				resourceRef = resourceKeydata.getRef();
			}
			if (resourceRef == null) {
				return "";
			}
			
		
		} catch (InvalidOperationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (BusinessException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		//get operation and barcode		
		try {
			//check if its SFC / not
			SFCBOHandle sfcref = new SFCBOHandle(siteVal,operationConfirmation);
			SfcBasicData sfcBasicData = null;
			try {
				sfcBasicData=sfcStateService.findSfcDataByRef(new ObjectReference(sfcref.getValue()));
			} catch (InvalidSFCException invalidSFCException) {
				// TODO: handle exception
				invalidSFCException.printStackTrace();
			}catch(BusinessException businessException){
				businessException.printStackTrace();
			}			
			StatusBasicConfiguration basicConfiguration=null;
			if(sfcBasicData !=null){
				basicConfiguration=statusService.findStatusByRef(new ObjectReference(sfcBasicData.getStatusRef()));
			}
			if(sfcBasicData ==null || basicConfiguration.getStatus().equals("405")){
				new PVTCheck().postScanOperations(this.operationRef,this.resourceRef,this.operationConfirmation);
			}else{
				//set the sfc to sfc field of POD if given barcode is sfc (not PVT)
				
				//logic for SFC				
				//check the status of given sfc
				
				//hold/scrap
				if(basicConfiguration.getStatus().equals("407") ){
					throw new PODCustomException("SFC status is hold/scrap");
				}else{						
					
					//check and complete active sfcs for given operation
						new CompleteActiveSfc().completeSfc(sfcBasicData, this.operationRef,this.resourceRef);						
					
					//Check given sfc is in queue					
						boolean startSfc=new StartInQueueSfc().startSfc(sfcBasicData, this.operationRef,this.resourceRef,operationList.get(0).getOperation());
						if(!startSfc){
								throw new BusinessException(1, "No sfc in queue to start");
						
							
						}	
						
						
				}			
			}
			//sfcChange(operationConfirmation);
			
		}catch (BusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			MessageHandler.clear();
    		MessageHandler.handle(e.getMessage() , null, MessageType.ERROR);
		}catch(PODCustomException customException){
			// TODO Auto-generated catch block
			customException.printStackTrace();
			MessageHandler.clear();
    		MessageHandler.handle(customException.getMessage() , null, MessageType.ERROR);
		}		
        UIComponent comp = null;
		UIComponent panel = getContainer();
		panel = findComponent(FacesUtility.getFacesContext().getViewRoot(),"podSelection");
		List<String> excludedIdList = new ArrayList<String>();
	//	excludedIdList.add("INPUT_ID_Label");
		excludedIdList.add("CustomLabel1");
		comp = findComponent(panel, "OperationConfrm", excludedIdList);
		setComponentFocus(comp.getClientId(FacesContext.getCurrentInstance()));
		clearOprSearch();
		return "";
		
	}
	
	public void clearOprSearch(){

		this.operationConfirmation = "";
        UIComponent comp = null;
		UIComponent panel = getContainer();
		panel = findComponent(FacesUtility.getFacesContext().getViewRoot(),"podSelection");
		List<String> excludedIdList = new ArrayList<String>();
	//	excludedIdList.add("INPUT_ID_Label");
		excludedIdList.add("CustomLabel1");
		comp = findComponent(panel, "OperationConfrm", excludedIdList);
		setComponentFocus(comp.getClientId(FacesContext.getCurrentInstance()));
	
	}
	
	
	public void sfcChange(String sfcRef){
		List<SfcSelection> sfcsToProcess = new ArrayList<SfcSelection>();
    	SfcSelection sfcSelect = new SfcSelection();
    	sfcSelect.setInputId(sfcRef);
    	sfcsToProcess.add(sfcSelect);
        // Sets the current SFC selection criteria and fire event
       getPodSelectionModel().setSfcs(sfcsToProcess);
       SfcChangeEvent sfcChangeEvent = new SfcChangeEvent(this);
       fireEvent(sfcChangeEvent, SfcChangeListenerInterface.class, "processSfcChange");
	}
	

	 
	 public void init(){
		 sfcStateService = (SfcStateServiceInterface) Services.getService("com.sap.me.production", "SfcStateService");
		 statusService = (StatusServiceInterface) Services.getService("com.sap.me.status", "StatusService");
		 
		
	 }
	 
	 public void processPluginLoaded(){
			
	try {
				//executePluginButtonActivityWithFeedback("Z_CURSOR_POS");
				//templateForm:reservedArea:podSelectView:INPUT_ID
				//FacesUtility.addScriptCommand("setTimeout(function(){document.getElementById('templateForm:reservedArea:podSelectView:OperationConfrm').focus()},5000); ");
				UIComponent comp = null;
				UIComponent panel = getContainer();
				panel = findComponent(FacesUtility.getFacesContext().getViewRoot(),"podSelection");
				List<String> excludedIdList = new ArrayList<String>();
			//	excludedIdList.add("INPUT_ID_Label");
				excludedIdList.add("CustomLabel1");
				comp = findComponent(panel, "OperationConfrm", excludedIdList);
				setComponentFocus(comp.getClientId(FacesContext.getCurrentInstance()));
		
				
			} catch (CancelProcessingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (AbortProcessingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			
		}
	 public void setActivityRuleValues() throws BusinessException {
		  ActivityConfigurationServiceInterface activityConfigService = Services.getService("com.sap.me.activity",
			"ActivityConfigurationService");
			String activity = CommonMethods.getActivityID();
			if (activity != null) {
				FindActivityOptionsRequest optionsReq = new FindActivityOptionsRequest();
				optionsReq.setActivity(activity);
				List<ActivityOption> activityOptions = activityConfigService 
						.findActivityOptions(optionsReq);				
				for (ActivityOption activityOption : activityOptions) {
					String optionName = activityOption.getExecUnitOption();
					String optionValue = activityOption.getSetting();
					if (optionName == null) {
						continue;
					}
					if (optionName.equals("SUB_ASSEMBLY_TYPE_CHK")) {
						HttpSession httpSession = FacesUtility.getHttpSession();
						httpSession.setAttribute("SUB_ASSEMBLY_TYPE_CHK", optionValue);	

					}
				}
				
			}

		}
}
