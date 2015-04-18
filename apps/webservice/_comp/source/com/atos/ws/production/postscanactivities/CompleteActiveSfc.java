package com.atos.ws.production.postscanactivities;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.atos.ws.production.PodResponse;
import com.sap.me.extension.Services;
import com.sap.me.frame.domain.BusinessException;
import com.sap.me.frame.domain.InvalidObjectVersionException;
import com.sap.me.plant.ResourceNotReadyException;
import com.sap.me.productdefinition.InvalidQuantityForMaterialException;
import com.sap.me.production.CompleteSfcRequest;
import com.sap.me.production.FindSfcsAtOperationRequest;
import com.sap.me.production.InvalidOperationException;
import com.sap.me.production.InvalidResourceException;
import com.sap.me.production.InvalidSfcException;
import com.sap.me.production.OperationNotOnRoutingException;
import com.sap.me.production.SelectedSfcNotInWorkAtOperationException;
import com.sap.me.production.SfcBasicData;
import com.sap.me.production.SfcCannotInvokeNextStepException;
import com.sap.me.production.SfcCompleteServiceInterface;
import com.sap.me.production.SfcGroupStatusEnum;
import com.sap.me.production.SfcInvalidQuantityInWorkException;
import com.sap.me.production.SfcInvalidQuantityToCompleteException;
import com.sap.me.production.SfcNotInWorkAtAnyOperationException;
import com.sap.me.production.SfcPassCannotBePerformedException;
import com.sap.me.production.SfcStateServiceInterface;
import com.sap.me.production.UserDoesNotHaveSfcInWorkException;
import com.sap.me.production.UserNotEnabledException;
import com.sap.me.production.exception.completeservice.MissingCompleteDataException;
import com.sap.me.production.exception.completeservice.OpenNcOnSfcException;
import com.sap.me.production.exception.completeservice.SfcTransferCompleteException;
import com.sap.me.production.exception.startservice.InvalidSFCStatusException;
import com.sap.me.production.exception.startservice.SFCNotInQueueAtResourceException;

public class CompleteActiveSfc   {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private SfcStateServiceInterface sfcStateService = null;
	private SfcCompleteServiceInterface completeServiceInterface=null;
	
	public void completeSfc(SfcBasicData sfcBasisData,String operationRef,String resourceRef,PodResponse podResponse) throws PODCustomException{
		
				//call to complete Sfc activity / function
				
				try {
					sfcStateService = (SfcStateServiceInterface) Services.getService("com.sap.me.production", "SfcStateService");
					completeServiceInterface= (SfcCompleteServiceInterface) Services.getService("com.sap.me.production", "SfcCompleteService");
					FindSfcsAtOperationRequest sfcsAtOperationRequest1=new FindSfcsAtOperationRequest(operationRef);					
					List<SfcGroupStatusEnum> includedStatusCollection1 = new ArrayList<SfcGroupStatusEnum>();
					includedStatusCollection1.add(SfcGroupStatusEnum.ACTIVE);
					sfcsAtOperationRequest1.setIncludedStatusCollection(includedStatusCollection1);
					Collection<SfcBasicData> basicDatas=sfcStateService.findSfcsAtOperation(sfcsAtOperationRequest1);
					String completedSfc = "";
					for(SfcBasicData basicData:basicDatas){
						if(!basicData.getSfc().equals(sfcBasisData.getSfc())){
							completeServiceInterface.completeSfc(new CompleteSfcRequest(basicData.getSfcRef(), operationRef, resourceRef));
							if(!"".equals(completedSfc)){
								completedSfc = completedSfc+",";
							}
							completedSfc = completedSfc+basicData.getSfcRef();
						}
					}
					podResponse.setCompletedSfc(completedSfc);
					
				}catch (MissingCompleteDataException e) {
					// TODO Auto-generated catch block
					new LogNC().logNC(sfcBasisData);
					throw new PODCustomException("MissingCompleteDataException :"+e.getMessage());
				} catch (SfcTransferCompleteException e) {
					// TODO Auto-generated catch block
					new LogNC().logNC(sfcBasisData);
					throw new PODCustomException("SfcTransferCompleteException :"+e.getMessage());
				} catch (OpenNcOnSfcException e) {
					// TODO Auto-generated catch block
					new LogNC().logNC(sfcBasisData);
					throw new PODCustomException("OpenNcOnSfcException :"+e.getMessage());
				} catch (InvalidOperationException e) {
					// TODO Auto-generated catch block
					new LogNC().logNC(sfcBasisData);
					throw new PODCustomException("InvalidOperationException :"+e.getMessage());
				} catch (SfcInvalidQuantityInWorkException e) {
					// TODO Auto-generated catch block
					new LogNC().logNC(sfcBasisData);
					throw new PODCustomException("SfcInvalidQuantityInWorkException :"+e.getMessage());
				} catch (UserDoesNotHaveSfcInWorkException e) {
					// TODO Auto-generated catch block
					new LogNC().logNC(sfcBasisData);
					throw new PODCustomException("UserDoesNotHaveSfcInWorkException :"+e.getMessage());
				} catch (SfcNotInWorkAtAnyOperationException e) {
					// TODO Auto-generated catch block
					new LogNC().logNC(sfcBasisData);
					throw new PODCustomException("SfcNotInWorkAtAnyOperationException :"+e.getMessage());
				} catch (OperationNotOnRoutingException e) {
					// TODO Auto-generated catch block
					new LogNC().logNC(sfcBasisData);
					throw new PODCustomException("OperationNotOnRoutingException :"+e.getMessage());
				} catch (SfcPassCannotBePerformedException e) {
					// TODO Auto-generated catch block
					new LogNC().logNC(sfcBasisData);
					throw new PODCustomException("SfcPassCannotBePerformedException :"+e.getMessage());
				} catch (InvalidQuantityForMaterialException e) {
					// TODO Auto-generated catch block
					new LogNC().logNC(sfcBasisData);
					throw new PODCustomException("InvalidQuantityForMaterialException :"+e.getMessage());
				} catch (InvalidObjectVersionException e) {
					// TODO Auto-generated catch block
					new LogNC().logNC(sfcBasisData);
					throw new PODCustomException("InvalidObjectVersionException :"+e.getMessage());
				} catch (SfcCannotInvokeNextStepException e) {
					// TODO Auto-generated catch block
					new LogNC().logNC(sfcBasisData);
					throw new PODCustomException("SfcCannotInvokeNextStepException :"+e.getMessage());
				} catch (SfcInvalidQuantityToCompleteException e) {
					// TODO Auto-generated catch block
					new LogNC().logNC(sfcBasisData);
					throw new PODCustomException("SfcInvalidQuantityToCompleteException :"+e.getMessage());
				} catch (SelectedSfcNotInWorkAtOperationException e) {
					// TODO Auto-generated catch block
					new LogNC().logNC(sfcBasisData);
					throw new PODCustomException("SelectedSfcNotInWorkAtOperationException :"+e.getMessage());
				} catch (InvalidSfcException e) {
					// TODO Auto-generated catch block
					new LogNC().logNC(sfcBasisData);
					throw new PODCustomException("InvalidSfcException :"+e.getMessage());
				} catch (InvalidSFCStatusException e) {
					// TODO Auto-generated catch block
					new LogNC().logNC(sfcBasisData);
					throw new PODCustomException("InvalidSFCStatusException :"+e.getMessage());
				} catch (ResourceNotReadyException e) {
					// TODO Auto-generated catch block
					new LogNC().logNC(sfcBasisData);
					throw new PODCustomException("ResourceNotReadyException :"+e.getMessage());
				} catch (InvalidResourceException e) {
					// TODO Auto-generated catch block
					new LogNC().logNC(sfcBasisData);
					throw new PODCustomException("InvalidResourceException :"+e.getMessage());
				} catch (UserNotEnabledException e) {
					// TODO Auto-generated catch block
					new LogNC().logNC(sfcBasisData);
					throw new PODCustomException("UserNotEnabledException :"+e.getMessage());
				} catch (SFCNotInQueueAtResourceException e) {
					// TODO Auto-generated catch block
					new LogNC().logNC(sfcBasisData);
					throw new PODCustomException("SFCNotInQueueAtResourceException :"+e.getMessage());
				} catch (BusinessException e) {
					// TODO Auto-generated catch block
					new LogNC().logNC(sfcBasisData);
					throw new PODCustomException("BusinessException :"+e.getMessage());
				}
				
				
		
		}
	
}


