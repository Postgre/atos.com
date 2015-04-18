package com.atos.ws.production;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import com.atos.ws.production.postscanactivities.AssemblyDataCollection;
import com.atos.ws.production.postscanactivities.CompleteActiveSfc;
import com.atos.ws.production.postscanactivities.PODCustomException;
import com.atos.ws.production.postscanactivities.PVTCheck;
import com.atos.ws.production.postscanactivities.StartInQueueSfc;
import com.atos.ws.production.postscanactivities.datacollection.DCGroupDataVO;
import com.sap.engine.services.webservices.espbase.configuration.ann.dt.AuthenticationDT;
import com.sap.engine.services.webservices.espbase.configuration.ann.dt.AuthenticationEnumsAuthenticationLevel;
import com.sap.engine.services.webservices.espbase.configuration.ann.rt.AuthenticationRT;
import com.sap.me.common.ObjectReference;
import com.sap.me.container.InvalidSFCException;
import com.sap.me.demand.SFCBOHandle;
import com.sap.me.extension.Services;
import com.sap.me.frame.domain.BusinessException;
import com.sap.me.frame.service.CommonMethods;
import com.sap.me.inventory.Inventory;
import com.sap.me.inventory.InventoryDataField;
import com.sap.me.inventory.InventoryIdentifier;
import com.sap.me.inventory.InventoryServiceInterface;
import com.sap.me.inventory.InventoryValidateAndUpdateRequest;
import com.sap.me.plant.ResourceBOHandle;
import com.sap.me.productdefinition.OperationBOHandle;
import com.sap.me.production.SfcBasicData;
import com.sap.me.production.SfcStateServiceInterface;
import com.sap.me.status.StatusBOHandle;
import com.visiprise.globalization.DateGlobalizationServiceInterface;
import com.visiprise.globalization.GlobalizationService;
import com.visiprise.globalization.KnownGlobalizationServices;



@AuthenticationDT(authenticationLevel = AuthenticationEnumsAuthenticationLevel.BASIC)
@AuthenticationRT(AuthenticationMethod = "sapsp:HTTPBasic")
@WebService
public class PodService {
	@WebMethod
	public PodResponse podOperatns(@WebParam(name = "podRequest") PodRequest podRequest) throws PodException,BusinessException {		
	PodResponse response = new PodResponse();
	
    SfcStateServiceInterface sfcStateService = (SfcStateServiceInterface) Services.getService("com.sap.me.production", "SfcStateService",podRequest.getSite());
	try {
			//check if its SFC / not
			SFCBOHandle sfcref = new SFCBOHandle(CommonMethods.getSite(),podRequest.getScannedData());
			SfcBasicData sfcBasicData = null;
			String operationRef = new OperationBOHandle(CommonMethods.getSite(), podRequest.getOperation(), "#").getValue();
			String resourceRef = new ResourceBOHandle(CommonMethods.getSite(), podRequest.getResource()).getValue();			
			response.setOperationRef( podRequest.getOperationRef());
			response.setResourceRef(podRequest.getResourceRef());
			response.setUserRef(podRequest.getUserRef());
			response.setSite(podRequest.getSite());
			try {
				sfcBasicData=sfcStateService.findSfcDataByRef(new ObjectReference(sfcref.getValue()));
			} catch (InvalidSFCException invalidSFCException) {
				// TODO: handle exception
				invalidSFCException.printStackTrace();
			}catch(BusinessException businessException){
				businessException.printStackTrace();
			}
			StatusBOHandle boHandle=null;
			if(sfcBasicData !=null){
				boHandle=new StatusBOHandle(sfcBasicData.getStatusRef());
			}
			if(sfcBasicData ==null || boHandle.getStatus().equals("405")){
				new PVTCheck().postScanOperations(operationRef,resourceRef,podRequest.getScannedData(),response,podRequest.getSfc());
				List<DCGroupDataVO> dcGroupDataVOList = new AssemblyDataCollection().collectData(operationRef, resourceRef, sfcBasicData.getSfcRef());
				response.setDcGroupDataVOList(dcGroupDataVOList);				
			}else{
				//set the sfc to sfc field of POD if given barcode is sfc (not PVT)
				
				//logic for SFC				
				//check the status of given sfc
				
				//hold/scrap
				if(boHandle.getStatus().equals("407") ){					
					throw new PODCustomException("SFC status is hold/scrap");
				}else{		
					
					//check and complete active sfcs for given operation
					
						 new CompleteActiveSfc().completeSfc(sfcBasicData, operationRef,resourceRef,response);						
					
					//Check given sfc is in queue					
						boolean startSfc=new StartInQueueSfc().startSfc(sfcBasicData, operationRef,resourceRef,response);
						if(!startSfc){
								throw new BusinessException(1, "No sfc in queue to start");						
							
						} 	
						List<DCGroupDataVO> dcGroupDataVOList = new AssemblyDataCollection().collectData(operationRef, resourceRef, sfcBasicData.getSfcRef());
						response.setDcGroupDataVOList(dcGroupDataVOList);
				}			
			}
			
		}catch (BusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new PodException(e.getMessage());
			
		}catch(PODCustomException customException){
			throw new PodException(customException.getMessage());
		}
		return response;

	}
	

}
