package com.atos.hook;

import java.util.HashMap;

import com.sap.me.extension.Services;
import com.sap.me.productdefinition.OperationConfiguration;
import com.sap.me.productdefinition.OperationConfigurationServiceInterface;
import com.sap.me.productdefinition.OperationType;
import com.sap.me.production.PostCompleteHookDTO;
import com.sap.me.status.StatusBasicConfiguration;
import com.sap.me.status.StatusGroup;
import com.sap.me.status.StatusSearchByDescriptionRequest;
import com.sap.me.status.StatusServiceInterface;
import com.sap.me.wpmf.MessageType;
import com.sap.me.wpmf.util.MessageHandler;
import com.visiprise.frame.service.ext.ActivityInterface;


public class CreateOperationHook implements ActivityInterface<PostCompleteHookDTO> {

	private static final long serialVersionUID = 1L;
	
	private OperationConfigurationServiceInterface operationConfigurationService;
	private StatusServiceInterface statusService;

	public void execute(PostCompleteHookDTO dto) throws Exception {
		//Initialization of services 
		initServices();
		// Create OperationConfiguration request 
		OperationConfiguration operationConfigRequest = new OperationConfiguration();
		// Generate unique name for the new operation 
		String operation = "OPER" + dto.getDateTime().getTimeInMillis() ;
		// Set Operation name
		operationConfigRequest.setOperation(operation);
		// Set description
		operationConfigRequest.setDescription("Congratulations, first hook is done");
		// Set operation type
		operationConfigRequest.setOperationType(OperationType.NORMAL);
		// Version for Operation
		operationConfigRequest.setRevision("A");
		// Current Version set to 'true'
		operationConfigRequest.setCurrentRevision(true);
		// Build Resource Type Reference
		String resourceTypeRef = buildResourceTypeBOHandle(dto.getSite(),"TRAINING_RT");
		operationConfigRequest.setResourceTypeRef(resourceTypeRef);
		// Find Status Reference for 'Releasable' status description
		StatusSearchByDescriptionRequest findStatusRequest = new StatusSearchByDescriptionRequest();
		findStatusRequest.setStatusDescription("Releasable");
		findStatusRequest.setStatusGroup(StatusGroup.PRODUCT_DEFINITION);
		//Find a unique status corresponding to the passed description 
		StatusBasicConfiguration response = statusService.findUniqueStatusByDescription(findStatusRequest);
		//Sets the value of the statusRef property. The status of this operation. 
		//Status is provided by the Product Definition Status Group. Valid values include StatusGroup.PRODUCT_DEFINITION
		operationConfigRequest.setStatusRef(response.getRef());
		// createOperation() method creates a new operation by given configuration and returns the full configuration of the created object
		operationConfigurationService.createOperation(operationConfigRequest);
        // Replaceable parameter OPERATION 
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("%OPERATION%", operation);
		// Set success message on the POD that operation was created 
		// message.success.operation.hook  = Create Operation Hook was successfully executed and created Operation %OPERATION%
		MessageHandler.handle("message.success.operation.hook", map, MessageType.SUCCESS);
		
	}

/**
	 *	Initialization of services
	 */
	private void initServices() {
		operationConfigurationService = Services.getService("com.sap.me.productdefinition", "OperationConfigurationService");
		statusService = Services.getService("com.sap.me.status", "StatusService");

	}
	/**
	 *	Build resource type handle.
	 */
	private String  buildResourceTypeBOHandle(String site,String resourceType) {
	    StringBuffer sb = new StringBuffer();
	    sb.append("ResourceTypeBO").append(':').append(site).append(',');
	    sb.append(resourceType);
	    return sb.toString();
	  }
	

}