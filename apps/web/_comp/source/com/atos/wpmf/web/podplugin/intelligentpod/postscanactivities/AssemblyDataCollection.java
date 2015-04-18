package com.atos.wpmf.web.podplugin.intelligentpod.postscanactivities;

import java.util.ArrayList;
import java.util.List;

import com.sap.me.common.ObjectReference;
import com.sap.me.datacollection.CollectDataAt;
import com.sap.me.datacollection.DataCollectionServiceInterface;
import com.sap.me.datacollection.OperationRequest;
import com.sap.me.datacollection.OperationSfc;
import com.sap.me.extension.Services;
import com.sap.me.frame.domain.BusinessException;
import com.sap.me.productdefinition.AttachedToContext;
import com.sap.me.productdefinition.AttachmentConfigurationServiceInterface;
import com.sap.me.productdefinition.AttachmentType;
import com.sap.me.productdefinition.FindAttachmentByAttachedToContextRequest;
import com.sap.me.productdefinition.FindAttachmentByAttachedToContextResponse;
import com.sap.me.productdefinition.FoundReferencesResponse;
import com.sap.me.productdefinition.WorkInstructionFullConfiguration;


public class AssemblyDataCollection {
	public void collectData(String product,String vendor,String quality,String operationRef,String resourceRef,String sfcRef){
		
		
	}
	private List<String> findDataCollectionGroup(AttachedToContext attachedToContext){
		List<String> dcGroups = new ArrayList<String>();
		try {
			AttachmentConfigurationServiceInterface attachmentConfigurationService = Services
			.getService("com.sap.me.productdefinition",
					"AttachmentConfigurationService");
			FindAttachmentByAttachedToContextRequest findAttachmentByAttachedToContextRequest = new FindAttachmentByAttachedToContextRequest(
					attachedToContext, false, false, AttachmentType.DATACOLLECTIONGROUP);
			FindAttachmentByAttachedToContextResponse findAttachmentByAttachedToContextResponse;
			findAttachmentByAttachedToContextResponse = attachmentConfigurationService
					.findAttachmentByAttacheds(findAttachmentByAttachedToContextRequest);
			List<FoundReferencesResponse> foundReferencesResponseList = findAttachmentByAttachedToContextResponse
			.getFoundReferencesResponseList();
	
			for(FoundReferencesResponse  foundReferencesResponse:foundReferencesResponseList){
				for(String dcGrp:foundReferencesResponse.getRefList()){
					dcGroups.add(dcGrp);
				}
				
			}
		} catch (BusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return dcGroups;
	
	}
}
