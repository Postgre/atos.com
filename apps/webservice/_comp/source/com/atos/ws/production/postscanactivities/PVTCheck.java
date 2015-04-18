package com.atos.ws.production.postscanactivities;

import java.util.List;

import com.atos.ws.production.PodResponse;
import com.sap.me.common.ObjectReference;
import com.sap.me.container.InvalidSFCException;
import com.sap.me.datatype.DataFieldInfo;
import com.sap.me.datatype.DataTypeConfigurationServiceInterface;
import com.sap.me.datatype.DataTypeInfo;
import com.sap.me.demand.SFCBOHandle;
import com.sap.me.extension.Services;
import com.sap.me.frame.domain.BusinessException;
import com.sap.me.frame.service.CommonMethods;
import com.sap.me.productdefinition.ItemConfigurationServiceInterface;
import com.sap.me.productdefinition.ItemFullConfiguration;
import com.sap.me.production.SfcBasicData;
import com.sap.me.production.SfcStateServiceInterface;


public class PVTCheck  {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final String PVT_PATTERN="[^#]*#[^#]*#[^#]*";
	private SfcStateServiceInterface sfcStateService = null;
	DataTypeConfigurationServiceInterface dataTypeConfigurationServiceInterface = (DataTypeConfigurationServiceInterface) Services.getService("com.sap.me.datatype", "DataTypeConfigurationService");
	ItemConfigurationServiceInterface itemConfigurationServiceInterface=(ItemConfigurationServiceInterface) Services.getService("com.sap.me.productdefinition", "ItemConfigurationService");
	public void postScanOperations(String operationRef,String resourceRef,String barcode,PodResponse podResponse,String sfcRef)throws PODCustomException{
		if(operationRef==null || "".equals(operationRef)){
			throw new PODCustomException("Operation Not Selected");
		}
		if(resourceRef==null || "".equals(resourceRef)){
			throw new PODCustomException("Resource  Not Selected");
		}
		if(barcode==null){
			throw new PODCustomException("Invalid Bar Code");
		}
		if(barcode.trim().equals("")){
			throw new PODCustomException("Blank Bar Code");
		}
		
		// setting rule values for checking type of material
		
		if(barcode.matches(PVT_PATTERN)){	
			//its PVT
			//get P , V, T value
			String[] pvtValue=barcode.split("#");
			String product=null;
			String vendor=null;
			String quality=null;
			int i=0;
			for(String str:pvtValue){
				if(str.startsWith("P")&& str.length()>1){
					product = str.substring(1);
				}
				if(str.startsWith("V")&& str.length()>1){
					vendor =  str.substring(1);
				}
				if(str.startsWith("T")&& str.length()>1){
					quality =  str.substring(1);
				}
				i++;
			}
			
			if(pvtValue.length == 0){
				throw new PODCustomException("Invalid value for assembly : "+barcode);
				
			}	
			//check for required data
			//get itemRef
			try {
				SfcBasicData parentSfcBasicData=sfcStateService.findSfcDataByRef(new ObjectReference(sfcRef));
				ItemFullConfiguration fullConfiguration=itemConfigurationServiceInterface.readItem(new ObjectReference(parentSfcBasicData.getItemRef()));
				String assemblyDataTypeRef = fullConfiguration.getAssemblyDataTypeRef();
				DataTypeInfo dataTypeInfo = dataTypeConfigurationServiceInterface.readDataTypeInfoByRef(new ObjectReference(assemblyDataTypeRef));
				List<DataFieldInfo> dataFieldInfoList = dataTypeInfo.getDataFieldList();
				
				for(DataFieldInfo dataFieldInfo:dataFieldInfoList){
					if(dataFieldInfo.isRequired()){						
						if("VENDOR_INFO".equals(dataFieldInfo.getName())&& vendor == null){
							throw new PODCustomException("Vendor value is missing in barcode ");
						}
						if("QUALITY_INFO".equals(dataFieldInfo.getName())&& quality == null){
							throw new PODCustomException("Quality value is missing in barcode ");
						}
						
					}
				}
			} catch (BusinessException businessException) {
				// TODO Auto-generated catch block
				businessException.printStackTrace();
				throw new PODCustomException("Business Exception while assembling purchased component : "+businessException.getMessage());
			}
			List<AssemblyDataItem> assemblyDataItems=new AssemblePurchasedComponent().assemblePurcharedComponent(operationRef,resourceRef,barcode,product,vendor,quality);
			podResponse.setAssemblyData(assemblyDataItems);
		}else{
				
			//check whether valid sfc
			try {
				sfcStateService = (SfcStateServiceInterface) Services.getService("com.sap.me.production", "SfcStateService");
				SFCBOHandle sfcref = new SFCBOHandle(CommonMethods.getSite(), barcode);
				SfcBasicData sfcBasicDataFromSubAssy=sfcStateService.findSfcDataByRef(new ObjectReference(sfcref.getValue()));	
				//operator scans invalid SFC
				if(sfcBasicDataFromSubAssy==null){
					throw new PODCustomException("Invalid SubAssembly SFC");
				}
				List<AssemblyDataItem> assemblyDataItems=new AssembleManufacturedComponent().assembleManufacturedComponent(operationRef, resourceRef, sfcBasicDataFromSubAssy);
				podResponse.setAssemblyData(assemblyDataItems);
				
			} catch (InvalidSFCException invalidSFCException) {
				// TODO: handle exception
				invalidSFCException.printStackTrace();
				throw new PODCustomException("InvalidSFCException exception while assembling manufactured component"+invalidSFCException.getMessage());
			}catch(BusinessException businessException){
				throw new PODCustomException("Business Exception while assembling manufactured component : "+businessException.getMessage());
			}
			
			
		} 
	}
	
	
	
}
