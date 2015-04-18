package com.atos.ws.production;

import java.util.List;

import com.sap.me.common.ObjectReference;
import com.sap.me.extension.Services;
import com.sap.me.frame.domain.BusinessException;
import com.sap.me.labor.LaborLogServiceInterface;
import com.sap.me.productdefinition.AttachedToContext;
import com.sap.me.productdefinition.AttachmentConfigurationServiceInterface;
import com.sap.me.productdefinition.AttachmentType;
import com.sap.me.productdefinition.FindAttachmentByAttachedToContextRequest;
import com.sap.me.productdefinition.FindAttachmentByAttachedToContextResponse;
import com.sap.me.productdefinition.FoundReferencesResponse;
import com.sap.me.productdefinition.InvalidOperationException;
import com.sap.me.productdefinition.OperationKeyData;
import com.sap.me.productdefinition.WorkInstructionConfigurationServiceInterface;
import com.sap.me.productdefinition.WorkInstructionFullConfiguration;
import com.sap.me.production.FindSfcByNameRequest;
import com.sap.me.production.SfcBasicData;
import com.sap.me.production.SfcStateServiceInterface;
import com.sap.me.user.UserConfigurationServiceInterface;

public class LaborOnOff{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private boolean defaultPlugin = false;
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
		
	
	public String getWorkInstruction(String operationRef,String resourceRef,String sfcRef){
		
		  List<OperationKeyData> operationList =null;
		  try {
			//Find the work Instructions attached to operation		 
			  String materialRef = null;
			  if(operationRef == null){					
					return "";
			   }
			  AttachedToContext attachedToContext=new AttachedToContext();
			  attachedToContext.setOperationRef(operationRef);
			  WorkInstructionFullConfiguration workInstr=findWorkInstruction(attachedToContext);
			  
			  //Find the work Instructions attached to item if not attached to operation 
			  if(workInstr==null){ 
				  //finding component to retrieve sfc value
				  SfcBasicData basicData= sfcStateServiceInterface.findSfcByName(new FindSfcByNameRequest(sfcRef));
				  materialRef = basicData.getItemRef();					  
				  attachedToContext=new AttachedToContext();
				  attachedToContext.setItemRef(materialRef);
				  workInstr=findWorkInstruction(attachedToContext);
			  }				  
			  if(workInstr!=null)
				  return workInstr.getUrl(); 
			 } catch (InvalidOperationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (BusinessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
			return "";
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

	
	
	
	

}
