package com.atos.ws.production.postscanactivities;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.sap.me.common.ObjectReference;
import com.sap.me.extension.Services;
import com.sap.me.frame.domain.BusinessException;
import com.sap.me.productdefinition.BOMComponentRequest;
import com.sap.me.productdefinition.BOMConfigurationServiceInterface;
import com.sap.me.productdefinition.BomComponentProductionConfiguration;
import com.sap.me.productdefinition.ItemConfigurationServiceInterface;
import com.sap.me.productdefinition.ItemFullConfiguration;
import com.sap.me.productdefinition.OperationBOHandle;
import com.sap.me.production.AssembledComponent;
import com.sap.me.production.AssembledComponentsResponse;
import com.sap.me.production.GroupAssembledAsBuiltComponentsRequest;
import com.sap.me.production.RetrieveComponentsServiceInterface;
import com.sap.me.production.SfcBasicData;
import com.sap.me.production.SfcIdentifier;
import com.sap.me.production.SfcStateServiceInterface;

public class AssemblyStatus {
	
	private ItemConfigurationServiceInterface itemConfigurationServiceInterface=(ItemConfigurationServiceInterface) Services.getService("com.sap.me.productdefinition", "ItemConfigurationService");
	private BOMConfigurationServiceInterface bomConfigurationServiceInterface = (BOMConfigurationServiceInterface) Services.getService("com.sap.me.productdefinition", "BOMConfigurationService");
	private SfcStateServiceInterface sfcStateServiceInterface = (SfcStateServiceInterface) Services.getService("com.sap.me.production", "SfcStateService");
	private RetrieveComponentsServiceInterface componentsServiceInterface = (RetrieveComponentsServiceInterface) Services.getService("com.sap.me.production", "RetrieveComponentsService");

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	

	
	public List<AssemblyDataItem> getAssemblyStatus(String sfcRef,String operationRef) throws BusinessException{
		List<AssemblyDataItem> assemblyDataItems = new ArrayList<AssemblyDataItem>();
		//check if its SFC / not			
		OperationBOHandle currentOperation = new OperationBOHandle(operationRef);
		SfcBasicData basicData = sfcStateServiceInterface.findSfcDataByRef(new ObjectReference(sfcRef));
		ItemFullConfiguration itemFullConfiguration=itemConfigurationServiceInterface.readItem(new ObjectReference(basicData.getItemRef()));
		if(itemFullConfiguration.getBomRef()==null){
			return assemblyDataItems;
		}
		Collection<BomComponentProductionConfiguration> bomComponentProductionConfigurations= bomConfigurationServiceInterface.findAllBOMComponents(new BOMComponentRequest(itemFullConfiguration.getBomRef(), new BigDecimal(1)));
		//finding assembled components
		
		GroupAssembledAsBuiltComponentsRequest asBuiltComponentsRequest = new GroupAssembledAsBuiltComponentsRequest();		
		List<SfcIdentifier> identifiers = new ArrayList<SfcIdentifier>();
		identifiers.add(new SfcIdentifier(sfcRef));		
		asBuiltComponentsRequest.setOperationRef(operationRef);
		asBuiltComponentsRequest.setSfcList(identifiers);
		
		//finding as built components
		AssembledComponentsResponse assembledComponentsResponse=componentsServiceInterface.findAssembledAsBuiltComponents(asBuiltComponentsRequest);
		List<AssembledComponent> assembledComponentList =assembledComponentsResponse.getAssembledComponentsList();
		for(BomComponentProductionConfiguration bomComponentConfiguration:bomComponentProductionConfigurations){
			OperationBOHandle bomOperation = new OperationBOHandle(bomComponentConfiguration.getOperationRef());
			if(!currentOperation.getOperation().equals(bomOperation.getOperation())){
				continue;
			}
			AssemblyDataItem dataItem = new AssemblyDataItem();	
			//find the whether assembly components are assembled
			String assemblyStatus="NO";
			boolean assembled = false;
			//counting assembled components from BOM
			int asBuiltCompCount=0;
			for(AssembledComponent assembledComponent:assembledComponentList){
				if(bomComponentConfiguration.getComponentRef()!=null && assembledComponent.getComponentRef()!=null&& bomComponentConfiguration.getOperationRef()!=null){
					if(bomComponentConfiguration.getComponentRef().equals(assembledComponent.getComponentRef())){						
							asBuiltCompCount++;											
					}
				}
			}
			if(asBuiltCompCount==bomComponentConfiguration.getQuantity().intValue()){
				assembled=true;
				assemblyStatus="YES";
			}
			dataItem.setAssemblyQuantity(asBuiltCompCount);
			dataItem.setAssembled(assembled);
			dataItem.setAssemblyStatus(assemblyStatus);
			ItemFullConfiguration fullConfiguration= itemConfigurationServiceInterface.readItem(new ObjectReference(bomComponentConfiguration.getComponentRef()));
			dataItem.setBomQuantity(bomComponentConfiguration.getQuantity()==null?0:new Integer(""+bomComponentConfiguration.getQuantity()));
			dataItem.setAssemblyName(fullConfiguration.getItem());			
			dataItem.setAssemblyType(fullConfiguration.getItemType().value());
			assemblyDataItems.add(dataItem);
		}
		return assemblyDataItems;
	}
	
	
}




