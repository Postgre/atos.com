package com.atos.wpmf.web.podplugin.intelligentpod.postscanactivities;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import com.sap.me.common.ObjectReference;
import com.sap.me.extension.Services;
import com.sap.me.frame.domain.BusinessException;
import com.sap.me.plant.ResourceBOHandle;
import com.sap.me.plant.ResourceNotReadyException;
import com.sap.me.productdefinition.InvalidQuantityForMaterialException;
import com.sap.me.productdefinition.OperationBOHandle;
import com.sap.me.productdefinition.OperationConfigurationServiceInterface;
import com.sap.me.productdefinition.OperationFullConfiguration;
import com.sap.me.productdefinition.OperationKeyData;
import com.sap.me.productdefinition.OperationSearchRequest;
import com.sap.me.production.FindSfcStepsByRefsRequest;
import com.sap.me.production.FindSfcStepsByRefsResponse;
import com.sap.me.production.FindSfcsAtOperationRequest;
import com.sap.me.production.InvalidResourceException;
import com.sap.me.production.InvalidSfcException;
import com.sap.me.production.MissingOperationException;
import com.sap.me.production.MissingResourceException;
import com.sap.me.production.ResourceNotSetupForSFCException;
import com.sap.me.production.SfcBasicData;
import com.sap.me.production.SfcGroupStatusEnum;
import com.sap.me.production.SfcStartServiceInterface;
import com.sap.me.production.SfcStateServiceInterface;
import com.sap.me.production.SfcStep;
import com.sap.me.production.SfcSteps;
import com.sap.me.production.StartSfcRequest;
import com.sap.me.production.WorkCenterAssigmentConfirmationRequiredException;
import com.sap.me.production.exception.startservice.CannotRestartDoneSFCException;
import com.sap.me.production.exception.startservice.InvalidInputValueException;
import com.sap.me.production.exception.startservice.InvalidResourceTypeForOperExecption;
import com.sap.me.production.exception.startservice.InvalidSFCStatusException;
import com.sap.me.production.exception.startservice.OperationNotEnabledException;
import com.sap.me.production.exception.startservice.RequiredValueMissingException;
import com.sap.me.production.exception.startservice.SFCInQueueForWorkCenterException;
import com.sap.me.production.exception.startservice.SFCMaxNumberOfReworkException;
import com.sap.me.production.exception.startservice.SFCNotEnoughQuantityException;
import com.sap.me.production.exception.startservice.SFCNotInQueueAtResourceException;
import com.sap.me.production.exception.startservice.SFCNotQueuedForOperationException;
import com.sap.me.production.exception.startservice.SFCScrappedException;
import com.sap.me.production.exception.startservice.UserHasTooManySFCsException;
import com.sap.me.production.exception.startservice.UserIsNotAssignedToOperationException;
import com.sap.me.production.exception.startservice.UserItemCertificationException;
import com.sap.me.production.exception.startservice.UserOperationCertificationException;
import com.sap.me.production.exception.startservice.UserResourceCertificationException;
import com.sap.me.production.exception.startservice.UserShopOrderCertificationException;
import com.sap.me.production.podclient.BasePodPlugin;
import com.sap.me.production.podclient.SfcChangeEvent;
import com.sap.me.production.podclient.SfcChangeListenerInterface;
import com.sap.me.production.podclient.SfcSelection;
import com.sap.me.status.StatusBOHandle;
import com.sap.me.wpmf.util.FacesUtility;

public class StartInQueueSfc extends BasePodPlugin {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private SfcStateServiceInterface sfcStateService = null;
	private SfcStartServiceInterface sfcStartServiceInterface = null;
	private OperationConfigurationServiceInterface operationConfigurationServiceInterface=null;
	private static final String IN_QUEUE = "402";
	private static final String NEW = "401";

	public boolean startSfc(SfcBasicData sfcBasicData,String operationCurrRef,String resourceRef,String operationRef) throws PODCustomException{
		
		boolean sfcInqueue=false;
			try {
				
				sfcStateService = (SfcStateServiceInterface) Services.getService("com.sap.me.production", "SfcStateService");
				sfcStartServiceInterface = (SfcStartServiceInterface)Services.getService("com.sap.me.production", "SfcStartService");
				operationConfigurationServiceInterface = (OperationConfigurationServiceInterface)Services.getService("com.sap.me.productdefinition", "OperationConfigurationService");
				FindSfcStepsByRefsRequest refsRequest=new FindSfcStepsByRefsRequest();
				List<String> sfcRefs = new ArrayList<String>();
				sfcRefs.add(sfcBasicData.getSfcRef());
				refsRequest.setSfcRefList(sfcRefs);
				FindSfcStepsByRefsResponse stepsByRefsResponse=sfcStateService.findSfcStepsByRefs(refsRequest);
				List<SfcSteps> sfcSteps=stepsByRefsResponse.getSfcRefList();
				List<HashMap<String,String>> operations = new ArrayList<HashMap<String,String>>();
				if(sfcSteps == null || sfcSteps.size()==0){
					//for sequencial operation
					FindSfcsAtOperationRequest sfcsAtOperationRequest2=new FindSfcsAtOperationRequest(operationCurrRef);
					List<SfcGroupStatusEnum> includedStatusCollection2 = new ArrayList<SfcGroupStatusEnum>();
					includedStatusCollection2.add(SfcGroupStatusEnum.IN_QUEUE);
					includedStatusCollection2.add(SfcGroupStatusEnum.NEW);
					sfcsAtOperationRequest2.setIncludedStatusCollection(includedStatusCollection2);
					Collection<SfcBasicData> basicDatasInqueue=sfcStateService.findSfcsAtOperation(sfcsAtOperationRequest2);
					for(SfcBasicData basicData:basicDatasInqueue){
						if(basicData.getSfcRef().equals(sfcBasicData.getSfcRef())){
							StatusBOHandle statusBOHandle = new StatusBOHandle(sfcBasicData.getStatusRef());
							if(IN_QUEUE.equals(statusBOHandle.getStatus())||NEW.equals(statusBOHandle.getStatus())){
								HashMap<String,String> operationMap = new HashMap<String, String>();
								operationMap.put(operationRef,resourceRef);
								operations.add(operationMap);
								sfcInqueue=true;
								break;
							}
							
						}
					}
				}else{
					//for parallel operation
					boolean isCurrOpPresent = false;
					HashMap<String,String> operationMap = new HashMap<String, String>();
					for(SfcSteps sfcStepsTemp:sfcSteps){
						for(SfcStep sfcStep:sfcStepsTemp.getStepList()){
							if(sfcStep.getQuantityInQueue().compareTo(new BigDecimal(1))>=0){
								OperationFullConfiguration operationFullConfiguration=operationConfigurationServiceInterface.readOperation(new ObjectReference(sfcStep.getOperationRef()));
								ResourceBOHandle boHandle= new ResourceBOHandle(operationFullConfiguration.getResourceTypeRef());
								operationMap.put(sfcStep.getOperationRef(),boHandle.getResource());								
								if(new OperationBOHandle(operationRef).getOperation().equals(sfcStep.getOperation())){
									isCurrOpPresent=true;
								}
							}
						}
					}
					if(operationMap.size()>0 && isCurrOpPresent){
						operations.add(operationMap);
						sfcInqueue = true;
					}
				}
				
				if(sfcInqueue){
					for(HashMap<String,String> operationsTemp:operations){
						for(String operationRefTemp:operationsTemp.keySet()){
							StartSfcRequest sfcRequest= new StartSfcRequest(sfcBasicData.getSfcRef());
							sfcRequest.setOperationRef(operationRefTemp);
							sfcRequest.setResourceRef(operationsTemp.get(operationRefTemp));
							sfcStartServiceInterface.startSfc(sfcRequest);
						}
						
					}
					
					sfcChange(sfcBasicData.getSfc());					
					//call to start activity / function	
					executePluginButtonActivityWithFeedback("WORKLIST_DISPLAY");
					executePluginButtonActivityWithFeedback("Z_ASSEMBLY_INTELLPOD");					
                    executePluginButtonActivityWithFeedback("Z_INTTELPOD_WORK_INS");  
                    
                    
				}
			   
			} catch (InvalidInputValueException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new PODCustomException("InvalidInputValueException while starting the SFC: "+e.getMessage());
			} catch (InvalidResourceTypeForOperExecption e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new PODCustomException("InvalidResourceTypeForOperExecption while starting the SFC: "+e.getMessage());
			} catch (InvalidSFCStatusException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new PODCustomException("InvalidSFCStatusException while starting the SFC: "+e.getMessage());
			} catch (OperationNotEnabledException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new PODCustomException("OperationNotEnabledException while starting the SFC: "+e.getMessage());
			} catch (RequiredValueMissingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new PODCustomException("RequiredValueMissingException while starting the SFC: "+e.getMessage());
			} catch (ResourceNotReadyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new PODCustomException("ResourceNotReadyException while starting the SFC: "+e.getMessage());
			} catch (ResourceNotSetupForSFCException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new PODCustomException("ResourceNotSetupForSFCException while starting the SFC: "+e.getMessage());
			} catch (CannotRestartDoneSFCException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new PODCustomException("CannotRestartDoneSFCException while starting the SFC: "+e.getMessage());
			} catch (SFCInQueueForWorkCenterException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new PODCustomException("SFCInQueueForWorkCenterException while starting the SFC: "+e.getMessage());
			} catch (SFCMaxNumberOfReworkException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new PODCustomException("SFCMaxNumberOfReworkException while starting the SFC: "+e.getMessage());
			} catch (SFCNotInQueueAtResourceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new PODCustomException("SFCMaxNumberOfReworkException while starting the SFC: "+e.getMessage());
			} catch (SFCNotQueuedForOperationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new PODCustomException("SFCNotQueuedForOperationException while starting the SFC: "+e.getMessage());
			}catch (SFCNotEnoughQuantityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new PODCustomException("SFCNotEnoughQuantityException while starting the SFC: "+e.getMessage());
			} catch (SFCScrappedException e) {
				// TODO Auto-generated catch block				
				e.printStackTrace();
				throw new PODCustomException("SFCScrappedException while starting the SFC: "+e.getMessage());
			} catch (UserHasTooManySFCsException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new PODCustomException("UserHasTooManySFCsException while starting the SFC: "+e.getMessage());
			} catch (UserItemCertificationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new PODCustomException("UserItemCertificationException while starting the SFC: "+e.getMessage());
			} catch (UserOperationCertificationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new PODCustomException("UserOperationCertificationException while starting the SFC: "+e.getMessage());
			} catch (UserResourceCertificationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new PODCustomException("UserResourceCertificationException while starting the SFC: "+e.getMessage());
			} catch (UserShopOrderCertificationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new PODCustomException("UserShopOrderCertificationException while starting the SFC: "+e.getMessage());
			} catch (MissingOperationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new PODCustomException("MissingOperationException while starting the SFC: "+e.getMessage());
			} catch (MissingResourceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new PODCustomException("MissingResourceException while starting the SFC: "+e.getMessage());
			} catch (InvalidSfcException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new PODCustomException("InvalidSfcException while starting the SFC: "+e.getMessage());
			} catch (InvalidQuantityForMaterialException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new PODCustomException("InvalidQuantityForMaterialException while starting the SFC: "+e.getMessage());
			} catch (InvalidResourceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new PODCustomException("InvalidResourceException while starting the SFC: "+e.getMessage());
			} catch (WorkCenterAssigmentConfirmationRequiredException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new PODCustomException("WorkCenterAssigmentConfirmationRequiredException while starting the SFC: "+e.getMessage());
			} catch (UserIsNotAssignedToOperationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new PODCustomException("UserIsNotAssignedToOperationException while starting the SFC: "+e.getMessage());
			} catch (BusinessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new PODCustomException("BusinessException while starting the SFC: "+e.getMessage());
			}
			 
			//cursor positioning logic 
			//try {
				//executePluginButtonActivityWithFeedback("Z_CURSOR_POS");
				UIComponent comp = null;
				UIComponent panel = getContainer();
				panel = findComponent(FacesUtility.getFacesContext().getViewRoot(),"podSelection");
				List<String> excludedIdList = new ArrayList<String>();
			//	excludedIdList.add("INPUT_ID_Label");
				excludedIdList.add("CustomLabel1");
				comp = findComponent(panel, "OperationConfrm", excludedIdList);
				setComponentFocus(comp.getClientId(FacesContext.getCurrentInstance()));
		
			/*	
			} catch (CancelProcessingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (AbortProcessingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (BusinessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
		return sfcInqueue;
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
}
