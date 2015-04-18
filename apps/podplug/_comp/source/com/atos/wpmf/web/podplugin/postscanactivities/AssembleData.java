package com.atos.wpmf.web.podplugin.postscanactivities;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.sap.me.activity.ActivityLogBasicConfiguration;
import com.sap.me.activity.ActivityLogConfigurationServiceInterface;
import com.sap.me.activity.FindActivityLogConfigurationByKeyFieldsRequest;
import com.sap.me.common.ObjectReference;
import com.sap.me.extension.Services;
import com.sap.me.frame.domain.BusinessException;
import com.sap.me.frame.domain.BusinessValidationException;
import com.sap.me.inventory.Inventory;
import com.sap.me.inventory.InventoryIdentifier;
import com.sap.me.inventory.InventoryServiceInterface;
import com.sap.me.productdefinition.BOMComponentRequest;
import com.sap.me.productdefinition.BOMConfigurationServiceInterface;
import com.sap.me.productdefinition.BomComponentProductionConfiguration;
import com.sap.me.productdefinition.ItemBasicConfiguration;
import com.sap.me.productdefinition.ItemConfigurationServiceInterface;
import com.sap.me.productdefinition.ItemFullConfiguration;
import com.sap.me.productdefinition.OperationBOHandle;
import com.sap.me.production.AllComponentsAlreadyAssembledException;
import com.sap.me.production.AssembleAsBuiltComponentsRequest;
import com.sap.me.production.AssembleComponentsResponse;
import com.sap.me.production.AssemblyComponent;
import com.sap.me.production.AssemblyDataField;
import com.sap.me.production.AssemblyDataServiceInterface;
import com.sap.me.production.AssemblyDataValidationRequest;
import com.sap.me.production.AssemblyServiceInterface;
import com.sap.me.production.InvalidAssemblyDataException;
import com.sap.me.production.MissingOperationException;
import com.sap.me.production.NoComponentsToAssembleException;
import com.sap.me.production.NotEnoughUnfilledQuantityException;
import com.sap.me.production.SfcBasicData;

public class AssembleData {

	public void assembleComponent(SfcBasicData basicData,String resourceRef,String operationRef,String vendor,String quality,String product,String chasis)throws PODCustomException{
		if(basicData !=null){					
			try {
				OperationBOHandle currentOperation = new OperationBOHandle(operationRef);
				AssemblyServiceInterface assemblyServiceInterface= (AssemblyServiceInterface) Services.getService("com.sap.me.production", "AssemblyService");
				AssemblyDataServiceInterface assemblyDataServiceInterface = (AssemblyDataServiceInterface) Services.getService("com.sap.me.production", "AssemblyDataService");
				ActivityLogConfigurationServiceInterface activityLogConfigurationServiceInterface = (ActivityLogConfigurationServiceInterface) Services.getService("com.sap.me.activity", "ActivityLogConfigurationService");
				//assembly service
				//assemble components
				BOMConfigurationServiceInterface bomConfigurationServiceInterface = (BOMConfigurationServiceInterface) Services.getService("com.sap.me.productdefinition", "BOMConfigurationService");
				ItemConfigurationServiceInterface itemConfigurationServiceInterface=(ItemConfigurationServiceInterface) Services.getService("com.sap.me.productdefinition", "ItemConfigurationService");
				ItemFullConfiguration itemFullConfiguration=itemConfigurationServiceInterface.readItem(new ObjectReference(basicData.getItemRef()));
				Collection<BomComponentProductionConfiguration> bomComponentProductionConfigurations= bomConfigurationServiceInterface.findAllBOMComponents(new BOMComponentRequest(itemFullConfiguration.getBomRef(), new BigDecimal(1)));
				List<AssemblyComponent> assemblyComponents = new ArrayList<AssemblyComponent>();
				for(BomComponentProductionConfiguration bomComp:bomComponentProductionConfigurations){
					OperationBOHandle bomOperation = new OperationBOHandle(bomComp.getOperationRef());
					ItemBasicConfiguration basicConfiguration=itemConfigurationServiceInterface.findItemConfigurationByRef(new ObjectReference(bomComp.getComponentRef()));
					if(product.equals(basicConfiguration.getItem())&& bomOperation.getOperation().equals(currentOperation.getOperation()) ){
						List<AssemblyDataField> assemblyDataFields  = new ArrayList<AssemblyDataField>();
						AssemblyComponent assemblyComponentTemp=new AssemblyComponent();
						assemblyComponentTemp.setBomComponentRef(bomComp.getBomComponentRef());
						assemblyComponentTemp.setActualComponentRef(bomComp.getComponentRef());
						assemblyComponentTemp.setQty(new BigDecimal(1));
						//vendor assembly data
						if(product != null){
							AssemblyDataField assemblyDataField4 = new AssemblyDataField();
							assemblyDataField4.setAttribute("PRODUCT_INFO");
							assemblyDataField4.setValue(product);
							assemblyDataField4.setSequence(new BigDecimal(40));
							assemblyDataFields.add(assemblyDataField4);
						}
						if(vendor != null){
							AssemblyDataField assemblyDataField1 = new AssemblyDataField();
							assemblyDataField1.setAttribute("VENDOR_INFO");
							assemblyDataField1.setValue(vendor);
							assemblyDataField1.setSequence(new BigDecimal(10));
							assemblyDataFields.add(assemblyDataField1);
						}
						if(quality !=null){
							AssemblyDataField assemblyDataField2 = new AssemblyDataField();
							assemblyDataField2.setAttribute("QUALITY_INFO");
							assemblyDataField2.setSequence(new BigDecimal(20));
							assemblyDataField2.setValue(quality);
							assemblyDataFields.add(assemblyDataField2);
						}
						if(chasis !=null){
							AssemblyDataField assemblyDataField3 = new AssemblyDataField();
							assemblyDataField3.setAttribute("SFC_INFO");
							assemblyDataField3.setSequence(new BigDecimal(30));
							assemblyDataField3.setValue(chasis);
							assemblyDataFields.add(assemblyDataField3);
						}
						AssemblyDataValidationRequest assemblyDataValidationRequest =new AssemblyDataValidationRequest();
						assemblyDataValidationRequest.setAssemblyDataFields(assemblyDataFields);
						
//						assemblyDataValidationRequest.setAssemblyDataTypeRef("");
//						assemblyDataValidationRequest.setItemRef(value);
//						assemblyDataValidationRequest.setItemType(value);
//						assemblyDataValidationRequest.setSfcRef(operationRef);
//						assemblyDataServiceInterface.validateAssemblyFields()
						assemblyComponentTemp.setAssemblyDataFields(assemblyDataFields);					
						assemblyComponents.add(assemblyComponentTemp);
					}
					
				}
				//finding event to set in assembly request
				FindActivityLogConfigurationByKeyFieldsRequest fieldsRequest = new FindActivityLogConfigurationByKeyFieldsRequest();
				fieldsRequest.setActionCode("ASSYPT");
				Collection<ActivityLogBasicConfiguration>  basicConfigurations=activityLogConfigurationServiceInterface.findActivityLogConfigurationByKeyFields(fieldsRequest);
				AssembleAsBuiltComponentsRequest asBuiltComponentsRequest=null;				
				for(ActivityLogBasicConfiguration activityLogBasicConfiguration:basicConfigurations){
					if(activityLogBasicConfiguration.getEvent()!=null && (!"".equals(activityLogBasicConfiguration.getEvent()))){
						asBuiltComponentsRequest = new AssembleAsBuiltComponentsRequest(activityLogBasicConfiguration.getEvent());
						break;
					}					
				}
				if(asBuiltComponentsRequest==null){
					asBuiltComponentsRequest = new AssembleAsBuiltComponentsRequest("baseFinished:AssemblyPoint");
				}
				asBuiltComponentsRequest.setComponentList(assemblyComponents);
				asBuiltComponentsRequest.setOperationRef(operationRef);
				asBuiltComponentsRequest.setResourceRef(resourceRef);
				asBuiltComponentsRequest.setSfcRef(basicData.getSfcRef());
				
				//assembleComponentsRequest.setEvent("baseFinished:AssemblyPoint");
				AssembleComponentsResponse assembleComponentsResponse= assemblyServiceInterface.assembleAsBuiltComponents(asBuiltComponentsRequest);
			
				assembleComponentsResponse.getAssembledComponentList();
			}catch(AllComponentsAlreadyAssembledException e){
				throw new PODCustomException("AllComponentsAlreadyAssembledException during assembly :"+e.getMessage());
			}catch (MissingOperationException e) {
				e.printStackTrace();
				throw new PODCustomException("MissingOperationException during assembly :"+e.getMessage());
				
			} catch (NotEnoughUnfilledQuantityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new PODCustomException("NotEnoughUnfilledQuantityException during assembly :"+e.getMessage());
			} catch (NoComponentsToAssembleException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new PODCustomException("NoComponentsToAssembleException during assembly :"+e.getMessage());
			} catch (InvalidAssemblyDataException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new PODCustomException("InvalidAssemblyDataException during assembly :"+e.getMessage());
			} catch (BusinessValidationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new PODCustomException("BusinessValidationException during assembly :"+e.getMessage());
			} catch (BusinessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new PODCustomException("BusinessException during assembly :"+e.getMessage());
			}
	
	}
	}
}

