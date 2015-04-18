package com.atos.wpmf.web.podplugin.intelligentpod.postscanactivities;

import com.sap.me.common.ObjectReference;
import com.sap.me.container.InvalidSFCException;
import com.sap.me.demand.SFCBOHandle;
import com.sap.me.extension.Services;
import com.sap.me.frame.domain.BusinessException;
import com.sap.me.frame.service.CommonMethods;
import com.sap.me.productdefinition.ItemBOHandle;
import com.sap.me.productdefinition.ItemConfigurationServiceInterface;
import com.sap.me.productdefinition.ItemSearchRequest;
import com.sap.me.productdefinition.ItemSearchResult;
import com.sap.me.production.SfcBasicData;
import com.sap.me.production.SfcStateServiceInterface;
import com.sap.me.production.podclient.BasePodPlugin;
import com.sap.me.wpmf.util.FacesUtility;


public class PVTCheck  extends BasePodPlugin {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final String PVT_PATTERN="[^#]*#[^#]*#[^#]*";
	private SfcStateServiceInterface sfcStateService = null;
	public void postScanOperations(String operationRef,String resourceRef,String barcode)throws PODCustomException{
		if(barcode == null ){
			return;
		}
		if(barcode.matches(PVT_PATTERN)){	
			//its PVT
			//get P , V, T value
			String[] pvtValue=barcode.split("#");
			String product=null;
			String vendor=null;
			String quality=null;
			if(pvtValue.length == 3){
				product = pvtValue[0];
				vendor = pvtValue[1];
				quality = pvtValue[2];
			}else{
				throw new PODCustomException("Invalid value for assembly : "+barcode);
			}
			
			new AssemblePurchasedComponent().assemblePurcharedComponent(operationRef,resourceRef,barcode,product,vendor,quality);
			
		}else{
				
			//check whether valid sfc
			try {
				sfcStateService = (SfcStateServiceInterface) Services.getService("com.sap.me.production", "SfcStateService");
				SFCBOHandle sfcref = new SFCBOHandle(CommonMethods.getSite(), barcode);
				SfcBasicData sfcBasicDataFromSubAssy=sfcStateService.findSfcDataByRef(new ObjectReference(sfcref.getValue()));	
				//operator scans invalid SFC
				if(sfcBasicDataFromSubAssy==null){
					throw new PODCustomException("Invalid value for assembly");
				}
				new AssembleManufacturedComponent().assembleManufacturedComponent(operationRef, resourceRef, sfcBasicDataFromSubAssy);
				
				
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
