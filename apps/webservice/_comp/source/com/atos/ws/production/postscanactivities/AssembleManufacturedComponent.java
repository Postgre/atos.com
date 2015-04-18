package com.atos.ws.production.postscanactivities;

import java.math.BigDecimal;
import java.util.List;

import com.sap.me.common.ObjectReference;
import com.sap.me.container.InvalidSFCException;
import com.sap.me.extension.Services;
import com.sap.me.frame.domain.BusinessException;
import com.sap.me.inventory.InvalidInventoryException;
import com.sap.me.inventory.InvalidTsmStateException;
import com.sap.me.inventory.Inventory;
import com.sap.me.inventory.InventoryConsumeRequest;
import com.sap.me.inventory.InventoryDataField;
import com.sap.me.inventory.InventoryIdentifier;
import com.sap.me.inventory.InventoryReservationException;
import com.sap.me.inventory.InventoryServiceInterface;
import com.sap.me.inventory.MaxUsageExceededException;
import com.sap.me.inventory.NotEnoughInventoryException;
import com.sap.me.inventory.OperationReservationException;
import com.sap.me.inventory.ResourceReservationException;
import com.sap.me.inventory.ShopOrderReservationException;
import com.sap.me.inventory.TsmFloorLifeDepletedException;
import com.sap.me.inventory.TsmShelfLifeDepletedException;
import com.sap.me.inventory.WorkCenterReservationException;
import com.sap.me.productdefinition.ItemConfigurationServiceInterface;
import com.sap.me.productdefinition.ItemKeyData;
import com.sap.me.production.SfcBasicData;

public class AssembleManufacturedComponent{

	private static final long serialVersionUID = 1L;
	
	private InventoryServiceInterface inventoryServiceInterface= null;

public  List<AssemblyDataItem> assembleManufacturedComponent(String operationRef,String resourceRef,SfcBasicData sfcBasicDataSubAssy)throws PODCustomException{
		
		
	//check whether valid sfc
	try {
		//geting parent sfc ( active sfc is considered parent sfc)
		SfcBasicData basicDataForParent=new CheckActiveSfcForPvt().checkActiveSfcForOperation(operationRef, resourceRef);				
		//if valid sfc get the chasis data value from sfc  field of inventory
		inventoryServiceInterface= (InventoryServiceInterface) Services.getService("com.sap.me.inventory", "InventoryService");
		InventoryIdentifier inventoryIdentifier = new InventoryIdentifier(sfcBasicDataSubAssy.getSfc());
		Inventory inventory=inventoryServiceInterface.findInventory(inventoryIdentifier);
		String chasisNo=null;
		for(InventoryDataField inventoryDataField:inventory.getAssemblyDataList()){
			if(inventoryDataField.getAttribute().equals("CHASIS")){
				chasisNo = inventoryDataField.getValue();
			}
		}
		if(chasisNo == null){
			throw new PODCustomException("No chasis data added to sub assembly sfc "+sfcBasicDataSubAssy.getSfc());
		}
		if(!basicDataForParent.getSfc().equals(chasisNo) ){
			throw new PODCustomException("Chasis no for subassembly "+chasisNo+" is not maching with parent assembly sfc"+basicDataForParent.getSfc());
		}
		boolean isUsed = inventoryServiceInterface.isInventoryUsed(new ObjectReference(inventory.getRef()));
		if(isUsed){
			throw new PODCustomException("Subassembly "+sfcBasicDataSubAssy.getSfc()+" is consumed.");
		}
		//assembling manufactured product
		ItemConfigurationServiceInterface itemConfigurationServiceInterface=(ItemConfigurationServiceInterface) Services.getService("com.sap.me.productdefinition", "ItemConfigurationService");
		ItemKeyData itemKeyData =  itemConfigurationServiceInterface.findItemKeyDataByRef(new ObjectReference(sfcBasicDataSubAssy.getItemRef()));
		new AssembleData().assembleComponent(basicDataForParent,resourceRef,operationRef,null,null,itemKeyData.getItem(),basicDataForParent.getSfc(),sfcBasicDataSubAssy);
		//consume the sfc 
		InventoryConsumeRequest inventoryConsumeRequest=new InventoryConsumeRequest();
		inventoryConsumeRequest.setSfcRef(basicDataForParent.getSfcRef());
		inventoryConsumeRequest.setComponentRef(itemKeyData.getRef());
		inventoryConsumeRequest.setResourceRef(resourceRef);
		inventoryConsumeRequest.setOperationRef(operationRef);
		inventoryConsumeRequest.setQty(new BigDecimal(1));
		inventoryConsumeRequest.setInventoryRef(inventory.getRef());
		consumeSfc(inventoryConsumeRequest);
		//update assembly status
		List<AssemblyDataItem>  assemblyDataItems=new AssemblyStatus().getAssemblyStatus(basicDataForParent.getSfcRef(), operationRef);
		return assemblyDataItems;
//		executePluginButtonActivityWithFeedback("Z_ASSEMBLY");
//		executePluginButtonActivityWithFeedback("Z_LABOR_ON_OFF");
		
		
	} catch (InvalidSFCException invalidSFCException) {
		// TODO: handle exception
		invalidSFCException.printStackTrace();
		throw new PODCustomException(invalidSFCException.getMessage());
	}catch(BusinessException businessException){
		throw new PODCustomException(businessException.getMessage());
	}
	
	

	}
private void consumeSfc(InventoryConsumeRequest inventoryConsumeRequest)throws PODCustomException{
	try {
		inventoryServiceInterface.consume(inventoryConsumeRequest);
	} catch (ShopOrderReservationException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		throw new PODCustomException("ShopOrderReservationException exception while consuming sfc "+inventoryConsumeRequest.getSfcRef()+" for sub assembly "+inventoryConsumeRequest.getComponentRef()+" : "+e.getMessage());
	} catch (OperationReservationException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		throw new PODCustomException("OperationReservationException exception while consuming sfc "+inventoryConsumeRequest.getSfcRef()+" for sub assembly "+inventoryConsumeRequest.getComponentRef()+" : "+e.getMessage());
	} catch (ResourceReservationException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		throw new PODCustomException("ResourceReservationException exception while consuming sfc "+inventoryConsumeRequest.getSfcRef()+" for sub assembly "+inventoryConsumeRequest.getComponentRef()+" : "+e.getMessage());
	} catch (WorkCenterReservationException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		throw new PODCustomException("WorkCenterReservationException exception while consuming sfc "+inventoryConsumeRequest.getSfcRef()+" for sub assembly "+inventoryConsumeRequest.getComponentRef()+" : "+e.getMessage());
	} catch (InvalidInventoryException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		throw new PODCustomException("InvalidInventoryException exception while consuming sfc "+inventoryConsumeRequest.getSfcRef()+" for sub assembly "+inventoryConsumeRequest.getComponentRef()+" : "+e.getMessage());
	} catch (InventoryReservationException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		throw new PODCustomException("InventoryReservationException exception while consuming sfc "+inventoryConsumeRequest.getSfcRef()+" for sub assembly "+inventoryConsumeRequest.getComponentRef()+" : "+e.getMessage());
	} catch (TsmFloorLifeDepletedException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		throw new PODCustomException("TsmFloorLifeDepletedException exception while consuming sfc "+inventoryConsumeRequest.getSfcRef()+" for sub assembly "+inventoryConsumeRequest.getComponentRef()+" : "+e.getMessage());
	} catch (TsmShelfLifeDepletedException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		throw new PODCustomException("TsmShelfLifeDepletedException exception while consuming sfc "+inventoryConsumeRequest.getSfcRef()+" for sub assembly "+inventoryConsumeRequest.getComponentRef()+" : "+e.getMessage());
	} catch (NotEnoughInventoryException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		throw new PODCustomException("NotEnoughInventoryException exception while consuming sfc "+inventoryConsumeRequest.getSfcRef()+" for sub assembly "+inventoryConsumeRequest.getComponentRef()+" : "+e.getMessage());
	} catch (MaxUsageExceededException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		throw new PODCustomException("MaxUsageExceededException exception while consuming sfc "+inventoryConsumeRequest.getSfcRef()+" for sub assembly "+inventoryConsumeRequest.getComponentRef()+" : "+e.getMessage());
	} catch (InvalidTsmStateException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		throw new PODCustomException("InvalidTsmStateException exception while consuming sfc "+inventoryConsumeRequest.getSfcRef()+" for sub assembly "+inventoryConsumeRequest.getComponentRef()+" : "+e.getMessage());
	} catch (BusinessException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		throw new PODCustomException("BusinessException exception while consuming sfc "+inventoryConsumeRequest.getSfcRef()+" for sub assembly "+inventoryConsumeRequest.getComponentRef()+" : "+e.getMessage());
	}
}

}
