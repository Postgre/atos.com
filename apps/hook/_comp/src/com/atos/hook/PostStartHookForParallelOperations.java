package com.atos.hook;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.atos.hook.postscanactivities.PODCustomException;
import com.atos.hook.postscanactivities.StartSfcDataVO;
import com.sap.me.common.ObjectReference;
import com.sap.me.extension.Services;
import com.sap.me.frame.domain.BusinessException;
import com.sap.me.plant.ResourceNotReadyException;
import com.sap.me.productdefinition.InvalidQuantityForMaterialException;
import com.sap.me.productdefinition.OperationConfigurationServiceInterface;
import com.sap.me.productdefinition.OperationFullConfiguration;
import com.sap.me.production.FindSfcsAtOperationRequest;
import com.sap.me.production.InvalidResourceException;
import com.sap.me.production.InvalidSfcException;
import com.sap.me.production.MissingOperationException;
import com.sap.me.production.MissingResourceException;
import com.sap.me.production.ResourceNotSetupForSFCException;
import com.sap.me.production.SfcBasicData;
import com.sap.me.production.SfcGroupStatusEnum;
import com.sap.me.production.SfcIdentifier;
import com.sap.me.production.SfcStartServiceInterface;
import com.sap.me.production.SfcStateServiceInterface;
import com.sap.me.production.SfcStep;
import com.sap.me.production.StartHookDTO;
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
import com.visiprise.frame.service.ext.ActivityInterface;

public class PostStartHookForParallelOperations implements ActivityInterface<StartHookDTO> {
	
	private static final long serialVersionUID = 1L;
	private SfcStateServiceInterface sfcStateService = null;
	private SfcStartServiceInterface sfcStartServiceInterface = null;
	private OperationConfigurationServiceInterface operationConfigurationServiceInterface=null;
	@Override
	public void execute(StartHookDTO dto) throws Exception 
	{
		
		
		try {
			SfcBasicData basicData=sfcStateService.findSfcDataByRef(new ObjectReference(dto.getSfcBO().getValue()));
			this.startSfc(basicData,dto.getOperationBO().getValue(),dto.getResourceBO().getValue());
		} catch (PODCustomException podCustomException) {
			// TODO Auto-generated catch block
			throw new Exception(podCustomException.getMessage());
		}
	}
	/**
	 * 
	 */
	

	private boolean startSfc(SfcBasicData sfcBasicData,String operationRef,String resourceRef) throws PODCustomException{
		
		boolean sfcInqueue=false;
			try {
				sfcStateService = (SfcStateServiceInterface) Services.getService("com.sap.me.production", "SfcStateService");
				sfcStartServiceInterface = (SfcStartServiceInterface)Services.getService("com.sap.me.production", "SfcStartService");
				operationConfigurationServiceInterface = (OperationConfigurationServiceInterface)Services.getService("com.sap.me.productdefinition", "OperationConfigurationService");
				
				SfcIdentifier sfcIdentifier=new SfcIdentifier(sfcBasicData.getSfcRef());
				Collection<SfcStep>  sfcSteps = sfcStateService.findStepsSFCIsInQueueFor(sfcIdentifier);
				List<StartSfcDataVO> dataVOs = new ArrayList<StartSfcDataVO>();
				if(sfcSteps !=null){
					for(SfcStep sfcStep:sfcSteps){
						ObjectReference objectReference=operationConfigurationServiceInterface.resolveCurrentRevision(new ObjectReference(sfcStep.getOperationRef()));
						OperationFullConfiguration operationFullConfiguration = operationConfigurationServiceInterface.readOperation(objectReference);
						StartSfcDataVO dataVO =new StartSfcDataVO();
						dataVO.setOperationRef(objectReference.getRef());
						dataVO.setResourceRef(operationFullConfiguration.getResourceTypeRef());
						dataVO.setSfcRef(sfcBasicData.getSfcRef());
						dataVOs.add(dataVO);
						sfcInqueue=true;
						
					}
				}	
				if(dataVOs.size()==0){
					FindSfcsAtOperationRequest sfcsAtOperationRequest2=new FindSfcsAtOperationRequest(operationRef);					
					List<SfcGroupStatusEnum> includedStatusCollection2 = new ArrayList<SfcGroupStatusEnum>();
					includedStatusCollection2.add(SfcGroupStatusEnum.IN_QUEUE);
					includedStatusCollection2.add(SfcGroupStatusEnum.NEW);
					sfcsAtOperationRequest2.setIncludedStatusCollection(includedStatusCollection2);
					Collection<SfcBasicData> basicDatasInqueue=sfcStateService.findSfcsAtOperation(sfcsAtOperationRequest2);
					for(SfcBasicData basicData:basicDatasInqueue){
						if(basicData.getSfc().equals(sfcBasicData.getSfc())){
							sfcInqueue=true;
							StartSfcDataVO dataVO =new StartSfcDataVO();
							dataVO.setOperationRef(operationRef);
							dataVO.setResourceRef(resourceRef);
							dataVO.setSfcRef(sfcBasicData.getSfcRef());
							dataVOs.add(dataVO);
						}
					}
				}
					
				
				if(sfcInqueue){
					for(StartSfcDataVO sfcDataVO:dataVOs){
						StartSfcRequest sfcRequest= new StartSfcRequest(sfcDataVO.getSfcRef());
						sfcRequest.setOperationRef(sfcDataVO.getOperationRef());
						sfcRequest.setResourceRef(sfcDataVO.getResourceRef());
						sfcStartServiceInterface.startSfc(sfcRequest);
					}
					 
                    
                    
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
			 
			
		return sfcInqueue;
	}
	
	
}
	

