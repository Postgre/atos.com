
package com.atos.refreshpod;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import com.sap.me.demand.SFCBOHandle;
import com.sap.me.extension.Services;
import com.sap.me.frame.BOHandle;
import com.sap.me.frame.domain.BusinessException;
import com.sap.me.frame.service.CommonMethods;
import com.sap.me.integration.ExecuteMIITransactionRequest;
import com.sap.me.integration.ExecuteMIITransactionResponse;
import com.sap.me.integration.MIIParameterValue;
import com.sap.me.integration.MIITransactionException;
import com.sap.me.integration.MIITransactionLoopBackException;
import com.sap.me.integration.MIITransactionServiceInterface;
import com.sap.me.production.WorklistItem;
import com.sap.me.production.podclient.BasePodPlugin;
import com.sap.me.production.podclient.SfcChangeEvent;
import com.sap.me.production.podclient.SfcChangeListenerInterface;
import com.sap.me.production.podclient.SfcSelection;
import com.sap.me.wpmf.MessageType;
import com.sap.me.wpmf.util.FacesUtility;
import com.sap.me.wpmf.util.MessageHandler;

public class RefreshPlugin extends BasePodPlugin  {

    private static final long serialVersionUID = 1L;

    // Set default polling frequency to 60 sec.
    private String pollingPeriod = "5";
    private boolean enableTrigger = false;

    public RefreshPlugin() {
    }

    /**
     * Method to enable or disable refreshing.
     * @return boolean true to do POD refresh, else false
     */
    public boolean isEnableTrigger() {
        return enableTrigger;
    }

    /**
     * Action method to perform refresh processing
     * @param event ActionEvent
     */
    public void processRefresh(ActionEvent event) {
    /*	try {
			List<SfcSelection> sfcSelected ;
			PodSelectionModelInterface selectionModel = getPodSelectionModel();
			if (selectionModel == null) {
				return;
			}
			List<OperationKeyData> operationList = selectionModel.getRouterStepsOrOperation();
			String operationRef = null;
			if (operationList != null) {
				operationRef = operationList.get(0).getRef();
			}
			// If operation is null, simply exit.
			if (operationRef == null) {
				return;
			}
			String resourceRef = null;
			ResourceKeyData resourceKeydata = selectionModel.getResolvedResource();
			if (resourceKeydata != null) {
				resourceRef = resourceKeydata.getRef();
			}
			if (resourceRef == null) {
				return;
			}
			try{
			sfcSelected = selectionModel.getResolvedSfcs();
			} catch (Exception e1){
				return;
			}
			if(checkAndSetPlcError()){
				return;
			}else{
				MessageHandler.clear();
			}
			
			WorklistServiceInterface worklistServiceInterface = (WorklistServiceInterface) Services.getService("com.sap.me.production", "WorklistService");
			RetrieveWorklistItemsRequest retrieveWorklistItemsRequest = new RetrieveWorklistItemsRequest();
			retrieveWorklistItemsRequest.setOperationRef(operationRef);
			retrieveWorklistItemsRequest.setResourceRef(resourceRef);
			List<WorklistStatusEnum> statusFilterList = new ArrayList<WorklistStatusEnum>();
			statusFilterList.add(WorklistStatusEnum.NEW);
			statusFilterList.add(WorklistStatusEnum.IN_QUEUE);
			//statusFilterList.add(WorklistStatusEnum.ACTIVE);
			retrieveWorklistItemsRequest.setStatusList(statusFilterList);
			List<WorklistColumnEnum> columns = new ArrayList<WorklistColumnEnum>();
			columns.add(WorklistColumnEnum.SFC);
			columns.add(WorklistColumnEnum.DATE_QUEUED);
			retrieveWorklistItemsRequest.setSelectionList(columns);
			List<WorklistColumnEnum> orderList = new ArrayList<WorklistColumnEnum>();
			orderList.add(WorklistColumnEnum.DATE_QUEUED);
			retrieveWorklistItemsRequest.setSortList(orderList);			
			retrieveWorklistItemsRequest.setUserRef(CommonMethods.getUserRef());
			List<WorklistItem> inQwlItems = worklistServiceInterface.findWorklistItems(retrieveWorklistItemsRequest);
			
			
			
			RetrieveWorklistItemsRequest retrieveWorklistItemsRequest2 = new RetrieveWorklistItemsRequest();
			retrieveWorklistItemsRequest2.setOperationRef(operationRef);
			retrieveWorklistItemsRequest2.setResourceRef(resourceRef);
			List<WorklistStatusEnum> statusFilterList2 = new ArrayList<WorklistStatusEnum>();
			statusFilterList2.add(WorklistStatusEnum.ACTIVE);
			//statusFilterList2.add(WorklistStatusEnum.IN_QUEUE);
			//statusFilterList.add(WorklistStatusEnum.ACTIVE);
			retrieveWorklistItemsRequest2.setStatusList(statusFilterList2);
			List<WorklistColumnEnum> columns2 = new ArrayList<WorklistColumnEnum>();
			columns2.add(WorklistColumnEnum.SFC);
			columns2.add(WorklistColumnEnum.DATE_QUEUED);
			retrieveWorklistItemsRequest2.setSelectionList(columns2);
			List<WorklistColumnEnum> orderList2 = new ArrayList<WorklistColumnEnum>();
			orderList2.add(WorklistColumnEnum.DATE_QUEUED);
			retrieveWorklistItemsRequest2.setSortList(orderList2);			
			retrieveWorklistItemsRequest2.setUserRef(CommonMethods.getUserRef());
			List<WorklistItem> activewlItems = worklistServiceInterface.findWorklistItems(retrieveWorklistItemsRequest2);
			

			int checkVal = 0;
			int inQSize = 0;
			int activeSize = 0;
			//boolean wiOpen = false;
			if(sfcSelected!= null){
				if(sfcSelected.size()==1){
					checkVal=1;
				}
			}
			if(inQwlItems!=null){
				if(inQwlItems.size()==1){
					inQSize = 1;
				}
			}
			if(activewlItems!=null){
				if(activewlItems.size()==1){
					activeSize=1;
				}
			}
			

			if(checkVal==1){
				if(activeSize==1){
					// some sfc in sfc field and active sfc list have one sfc - check for the selected sfc and active sfc same or not. IF same no refresh.else change ther refresh
					
					if(!activewlItems.get(0).getSfcRef().equalsIgnoreCase(sfcSelected.get(0).getSfc().getSfcRef())){
						SfcasSelectedSfc(activewlItems);
						//executePluginButtonActivity("WI500");
						//this.fireEvent(new RefreshWorklistEvent(this), RefreshWorklistListenerInterface.class, "processRefreshWorklist");
					}
				}else if (activeSize==0){
					//sfc field is there and no active sfc
					MessageHandler.clear();
					this.getPodSelectionModel().setSfcs(null);
					//List<SfcSelection> sfcsToProcess_2 = new ArrayList<SfcSelection>();
					//SfcSelection sfcSelect_2 = new SfcSelection();
					//sfcsToProcess_2.add(sfcSelect_2);
					//selectionModel.setSfcs(sfcsToProcess_2);
					SfcChangeEvent sfcChangeEvent2 = new SfcChangeEvent(this);
					this.fireEvent(sfcChangeEvent2, SfcChangeListenerInterface.class, "processSfcChange");
					//this.fireEvent(new RefreshWorklistEvent(this), RefreshWorklistListenerInterface.class, "processRefreshWorklist");
					
					if(inQSize == 1){
						//checkAndClearSfc(inQwlItems, sfcSelected);	
						Map<String, Object> messageMap = new HashMap<String, Object>();
			    		messageMap.put("%SFCS%", inQwlItems.get(0).getSfcRef().toString()); 
			    		MessageHandler.handle("example.inqueue.default", messageMap, MessageType.SUCCESS);  // display SFC completed message in POD
					}
				}
			}else if(checkVal == 0){
				if(activeSize == 1){
					// SFC field is empty and there is one sfc started on the particular operation
					SfcasSelectedSfc(activewlItems);
					this.fireEvent(new RefreshWorklistEvent(this), RefreshWorklistListenerInterface.class, "processRefreshWorklist");
					executePluginButtonActivity("WI500");
					//this.fireEvent(new RefreshWorklistEvent(this), RefreshWorklistListenerInterface.class, "processRefreshWorklist");
				}else if(activeSize ==0 ){
					// SFC field is empty and no active sfc for the operation
					if(inQSize == 1){
						//sfc field is empty and no active sfc and only one inqueue sfc
						//SfcasSelectedSfc(inQwlItems);
						MessageHandler.clear();
						Map<String, Object> messageMap = new HashMap<String, Object>();
			    		messageMap.put("%SFCS%", inQwlItems.get(0).getSfcRef().toString()); 
			    		MessageHandler.handle("example.inqueue.default", messageMap, MessageType.SUCCESS);  // display SFC completed message in POD
			    		//this.fireEvent(new RefreshWorklistEvent(this), RefreshWorklistListenerInterface.class, "processRefreshWorklist");
					}
		
				}
			}
			
			
			if (inQwlItems.size() == 1) {
				WorklistItem worklistItem = inQwlItems.get(inQwlItems.size()-1);
				List<SfcSelection> sfcsToProcess = new ArrayList<SfcSelection>();
				SfcSelection sfcSelect = new SfcSelection();
				SFCBOHandle sfcHandle = SFCBOHandle.convert(new BOHandle(worklistItem.getSfcRef()));
				sfcSelect.setInputId(sfcHandle.getSFC());
				sfcsToProcess.add(sfcSelect);
				// Sets the current SFC selection criteria and fire event
				//if(!getPodSelectionModel().getResolvedSfcs().get(0).getSfc().getSfcRef().toString().equalsIgnoreCase(worklistItem.getSfcRef().toString())){
				getPodSelectionModel().setSfcs(sfcsToProcess);
				SfcChangeEvent sfcChangeEvent = new SfcChangeEvent(this);
				this.fireEvent(sfcChangeEvent, SfcChangeListenerInterface.class, "processSfcChange");//}
			}
			this.fireEvent(new RefreshWorklistEvent(this), RefreshWorklistListenerInterface.class, "processRefreshWorklist");
			
			UIComponent comp = null;
			UIComponent panel = getContainer();
			panel = findComponent(FacesUtility.getFacesContext().getViewRoot(),"podSelection");
			List<String> excludedIdList = new ArrayList<String>();
			excludedIdList.add("INPUT_ID_Label");
			comp = findComponent(panel, "OperationConfrm", excludedIdList);
			setComponentFocus(comp.getClientId(FacesContext.getCurrentInstance()));

			
			complete();
		} catch (Exception e) {
			ExceptionHandler.handle(e, this);
		}*/
        UIComponent comp = null;
		UIComponent panel = getContainer();
		panel = findComponent(FacesUtility.getFacesContext().getViewRoot(),"podSelection");
		List<String> excludedIdList = new ArrayList<String>();
	//	excludedIdList.add("INPUT_ID_Label");
		excludedIdList.add("CustomLabel1");
		comp = findComponent(panel, "OperationConfrm", excludedIdList);
		setComponentFocus(comp.getClientId(FacesContext.getCurrentInstance()));
    }
 

	/**
     * gets the polling frequency.
     * @return polling period in seconds
     */
    public String getPollingPeriod() {
        return pollingPeriod;
    }
    
    public void SfcasSelectedSfc(List<WorklistItem> worklistItem){
    	WorklistItem worklistItem1 = worklistItem.get(worklistItem.size()-1);
		List<SfcSelection> sfcsToProcess = new ArrayList<SfcSelection>();
		SfcSelection sfcSelect = new SfcSelection();
		SFCBOHandle sfcHandle = SFCBOHandle.convert(new BOHandle(worklistItem1.getSfcRef()));
		sfcSelect.setInputId(sfcHandle.getSFC());
		sfcsToProcess.add(sfcSelect);
		// Sets the current SFC selection criteria and fire event
		//if(!getPodSelectionModel().getResolvedSfcs().get(0).getSfc().getSfcRef().toString().equalsIgnoreCase(worklistItem.getSfcRef().toString())){
		getPodSelectionModel().setSfcs(sfcsToProcess);
		SfcChangeEvent sfcChangeEvent = new SfcChangeEvent(this);
		this.fireEvent(sfcChangeEvent, SfcChangeListenerInterface.class, "processSfcChange");
		//executePluginButtonActivity("WI500");
    }
    
    public void checkAndClearSfc (List<WorklistItem> worklistItem,List<SfcSelection> sfcSelc){
    	if(!worklistItem.get(0).getSfcRef().equalsIgnoreCase(sfcSelc.get(0).getSfc().getSfcRef())){
    	/*	List<SfcSelection> sfcsToProcess_1 = new ArrayList<SfcSelection>();
			SfcSelection sfcSelect_1 = new SfcSelection();
			sfcsToProcess_1.add(sfcSelect_1);*/
			// Sets the current SFC selection criteria and fire event
			//if(!getPodSelectionModel().getResolvedSfcs().get(0).getSfc().getSfcRef().toString().equalsIgnoreCase(worklistItem.getSfcRef().toString())){
			getPodSelectionModel().setSfcs(null);
			SfcChangeEvent sfcChangeEvent = new SfcChangeEvent(this);
			this.fireEvent(sfcChangeEvent, SfcChangeListenerInterface.class, "processSfcChange");
			}
    }
    
    public boolean checkAndSetPlcError(){
 
    	boolean returnVal = false;
    	List<MIIParameterValue> miiParameterList = new ArrayList<MIIParameterValue>();
		MIIParameterValue miiParameterValue = new MIIParameterValue();
		String isError = "isError";
		String isErrorVal=null;
		String ErrorStr = "ErrorStr";
		String ErrorStrVal=null;

		ExecuteMIITransactionResponse executeMIITransactionResponse = new ExecuteMIITransactionResponse();
		
		ExecuteMIITransactionRequest executeMIITransactionRequest = new ExecuteMIITransactionRequest();
		executeMIITransactionRequest.setTransactionName("BajajDemo/Transaction/ErrorCheck");
		
		executeMIITransactionRequest.setParameters(miiParameterList);
		try{
			MIITransactionServiceInterface miiTransactionService = Services.getService("com.sap.me.integration", "MIITransactionService");
			executeMIITransactionResponse = miiTransactionService.executeMIITransaction(executeMIITransactionRequest);
			List<MIIParameterValue> MIIOutputParameterList = executeMIITransactionResponse.getParameters();
					
			
			try {
			Iterator it = MIIOutputParameterList.iterator();
			while(it.hasNext()){
				
				MIIParameterValue outputParamterObj = new MIIParameterValue();
				outputParamterObj = (MIIParameterValue) it.next();
				if(isError.equalsIgnoreCase(outputParamterObj.getName())){
					isErrorVal = outputParamterObj.getValue().toString();
				}
				if( ErrorStr.equalsIgnoreCase(outputParamterObj.getName())){
					ErrorStrVal = outputParamterObj.getValue().toString();
				}
			
			}
			
			if(isErrorVal.equalsIgnoreCase("1")){
				returnVal = true;
			} else if(isErrorVal.equalsIgnoreCase("0")){
				returnVal = false;
			}
			
			if(ErrorStrVal!= null && returnVal){
				MessageHandler.clear();
	    		MessageHandler.handle("PLC Error ON - " + ErrorStrVal, null, MessageType.ERROR);
			}
			
			
			//return returnVal;
			}catch(Exception e){
				MessageHandler.handle("MII CALL EXCEPTION :"+e.toString(), null, MessageType.ERROR, this);
			}
				
			//MessageHandler.handle("MII CALL ", null, MessageType.SUCCESS, this);
				//sendOutBoundMessage(recordID,site);
		
		}catch(MIITransactionException e){
			MessageHandler.handle("Exception:" + e.toString()+":"+e.getErrorCode(), null, MessageType.ERROR, this);
					}
		catch(MIITransactionLoopBackException e){
			MessageHandler.handle("Exception:" + e.toString()+":"+e.getErrorCode(), null, MessageType.ERROR, this);
				}catch(BusinessException e){
			MessageHandler.handle("Exception:" + e.toString()+":"+e.getErrorCode(), null, MessageType.ERROR, this);
			}catch(Exception e){			
			MessageHandler.handle("Exception:" + e.toString(), null, MessageType.ERROR, this);
			}
		return returnVal;
		
			
    	
    }
}

