package com.atos.wpmf.web.podplugin.postscanactivities;

import javax.faces.event.AbortProcessingException;

import com.sap.me.common.ItemType;
import com.sap.me.common.ObjectReference;
import com.sap.me.extension.Services;
import com.sap.me.frame.domain.BusinessException;
import com.sap.me.productdefinition.ItemBasicConfiguration;
import com.sap.me.productdefinition.ItemConfigurationServiceInterface;
import com.sap.me.productdefinition.ItemSearchRequest;
import com.sap.me.productdefinition.ItemSearchResult;
import com.sap.me.production.SfcBasicData;
import com.sap.me.production.SfcStateServiceInterface;
import com.sap.me.production.podclient.BasePodPlugin;

public class AssemblePurchasedComponent  extends BasePodPlugin{

	private static final long serialVersionUID = 1L;
	private SfcStateServiceInterface sfcStateServiceInterface = (SfcStateServiceInterface)Services.getService("com.sap.me.production", "SfcStateService");
	ItemConfigurationServiceInterface itemConfigurationServiceInterface=(ItemConfigurationServiceInterface) Services.getService("com.sap.me.productdefinition", "ItemConfigurationService");
	public  void assemblePurcharedComponent(String operationRef,String resourceRef,String barcode,String product,String vendor,String quality,String sfcRef)throws PODCustomException{
		
		
		try {
			//valid component check			
			ItemSearchResult itemSearchResult=itemConfigurationServiceInterface.findItemConfiguration(new ItemSearchRequest(product));
			if(itemSearchResult==null || itemSearchResult.getItemList()==null || itemSearchResult.getItemList().size()==0){
				throw new PODCustomException("Scanned component is invalid component.");
			}
			
			for(ItemBasicConfiguration basicConfiguration:itemSearchResult.getItemList()){
				if(basicConfiguration.getItemType().compareTo(ItemType.PURCHASED)!=0){
					throw new PODCustomException("Purchased Component is expected, Scanned component is of type "+basicConfiguration.getItemType().value());
				}
				
			}
			//get active sfc
			//SfcBasicData basicData=new CheckActiveSfcForPvt().checkActiveSfcForOperation(operationRef, resourceRef);
			SfcBasicData basicData = sfcStateServiceInterface.findSfcDataByRef(new ObjectReference(sfcRef));
			//validate component's vendor before assembly
			new ValidVendorCheck().validateVendor(operationRef, basicData, product, vendor,quality);
			//assemble component
			new AssembleData().assembleComponent(basicData,resourceRef,operationRef,vendor,quality,product,null,null);
			
		}catch (PODCustomException e) {
			//Given value is not the sfc number
			throw new PODCustomException(e.getMessage());
		}  catch (AbortProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new PODCustomException("AbortProcessingException while assembling purchased product ");
		}catch (BusinessException e) {
			//Given value is not the sfc number
			e.printStackTrace(); 
			throw new PODCustomException("Exception while assembling purchased product");
			
		}
	}
	
}
	

