/* (c) Copyright 2010 SAP AG. All rights reserved. */
package com.atos.wpmf.web.podplugin.miitrx;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.sap.me.extension.Services;
import com.sap.me.frame.domain.BusinessException;
import com.sap.me.frame.service.CommonMethods;
import com.sap.me.integration.ExecuteMIITransactionRequest;
import com.sap.me.integration.ExecuteMIITransactionResponse;
import com.sap.me.integration.MIIParameterValue;
import com.sap.me.integration.MIITransactionException;
import com.sap.me.integration.MIITransactionLoopBackException;
import com.sap.me.integration.MIITransactionServiceInterface;
import com.sap.me.plant.ResourceBOHandle;
import com.sap.me.plant.ResourceKeyData;
import com.sap.me.productdefinition.OperationConfigurationServiceInterface;
import com.sap.me.productdefinition.OperationExtendedConfiguration;
import com.sap.me.productdefinition.OperationKeyData;
import com.sap.me.productdefinition.OperationSearchRequest;
import com.sap.me.productdefinition.OperationSearchResult;
import com.sap.me.production.podclient.BasePodPlugin;
import com.sap.me.production.podclient.PodSelectionModelInterface;
import com.sap.me.production.podclient.SfcSelection;
import com.sap.me.wpmf.MessageType;
import com.sap.me.wpmf.util.MessageHandler;


/**
 * Publisher for Notification Messages
 */
public class MIITrasactionPlugin extends BasePodPlugin {
    private static final long serialVersionUID = 1L;
    private String scannedData;
    private OperationConfigurationServiceInterface configurationServiceInterface = Services.getService("com.sap.me.productdefinition", "OperationConfigurationService");
    
    public String getScannedData() {
		return scannedData;
	}

	public void setScannedData(String scannedData) {
		this.scannedData = scannedData;
	}

	public MIITrasactionPlugin () {
        super();
    }

    public void closePlugin() {
       
    }
    public String executeTransaction()throws BusinessException{
    	PodSelectionModelInterface selectionModel = getPodSelectionModel();
    	OperationKeyData operationKeyData=selectionModel.getOperation();
    	ResourceKeyData resourceKeyData=selectionModel.getResource();
    	List<SfcSelection> sfcSelections=selectionModel.getSfcs();
    	String sfc="";
    	if(sfcSelections!=null && sfcSelections.size()>0){
    		sfc = sfcSelections.get(0).getInputId();
    	}
    	boolean returnVal = false;
    	List<MIIParameterValue> miiParameterList = new ArrayList<MIIParameterValue>();
		MIIParameterValue miiParameterValue1 = new MIIParameterValue();
		miiParameterValue1.setName("operation");
		miiParameterValue1.setValue(operationKeyData.getOperation());
		MIIParameterValue miiParameterValue2 = new MIIParameterValue();
		miiParameterValue2.setName("resource");
		miiParameterValue2.setValue(resourceKeyData.getResource());
		MIIParameterValue miiParameterValue3 = new MIIParameterValue();
		miiParameterValue3.setName("scannedData");
		miiParameterValue3.setValue(scannedData);
		MIIParameterValue miiParameterValue4 = new MIIParameterValue();
		miiParameterValue4.setName("sfc");
		miiParameterValue4.setValue(sfc);
		MIIParameterValue miiParameterValue5 = new MIIParameterValue();
		OperationSearchResult operationSearchResult=configurationServiceInterface.findOperationConfiguration(new OperationSearchRequest(operationKeyData.getOperation()));
		OperationExtendedConfiguration extendedConfiguration=null;
		for(OperationExtendedConfiguration extendedConfigurationTemp:operationSearchResult.getOperationList()){
			if(extendedConfigurationTemp.isCurrentRevision()){
				extendedConfiguration=extendedConfigurationTemp;
			}
		}		
		miiParameterValue5.setName("operationRef");
		miiParameterValue5.setValue(extendedConfiguration.getRef());
		MIIParameterValue miiParameterValue6 = new MIIParameterValue();
		miiParameterValue6.setName("resourceRef");
		ResourceBOHandle resourceBOHandle= new ResourceBOHandle(CommonMethods.getSite(),resourceKeyData.getResource() );
		miiParameterValue6.setValue(resourceBOHandle.getValue());
		MIIParameterValue miiParameterValue7 = new MIIParameterValue();
		miiParameterValue7.setName("userRef");
		miiParameterValue7.setValue(CommonMethods.getUserRef());
		MIIParameterValue miiParameterValue8 = new MIIParameterValue();
		miiParameterValue8.setName("site");
		miiParameterValue8.setValue(CommonMethods.getSite());
		miiParameterList.add(miiParameterValue1);
		miiParameterList.add(miiParameterValue2);
		miiParameterList.add(miiParameterValue3);
		miiParameterList.add(miiParameterValue4);
		miiParameterList.add(miiParameterValue5);
		miiParameterList.add(miiParameterValue6);
		miiParameterList.add(miiParameterValue7);
		miiParameterList.add(miiParameterValue8);

		ExecuteMIITransactionResponse executeMIITransactionResponse = new ExecuteMIITransactionResponse();
		
		ExecuteMIITransactionRequest executeMIITransactionRequest = new ExecuteMIITransactionRequest();
		executeMIITransactionRequest.setTransactionName("Default/PODServiceTrx");
		
		
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
		
		
			return "";
    	
    }
   

   
}
