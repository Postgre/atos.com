package com.atos.ws.production.postscanactivities;

import java.util.List;

import javax.faces.event.AbortProcessingException;

import com.sap.me.frame.domain.BusinessException;
import com.sap.me.production.SfcBasicData;

public class AssemblePurchasedComponent{

	private static final long serialVersionUID = 1L;

public  List<AssemblyDataItem> assemblePurcharedComponent(String operationRef,String resourceRef,String barcode,String product,String vendor,String quality)throws PODCustomException{
		
		
		try {
			//get active sfc
			SfcBasicData basicData=new CheckActiveSfcForPvt().checkActiveSfcForOperation(operationRef, resourceRef);
			//validate component's vendor before assembly
			new ValidVendorCheck().validateVendor(operationRef, basicData, product, vendor,quality);
			//assemble component
			new AssembleData().assembleComponent(basicData,resourceRef,operationRef,vendor,quality,product,null,null);
			List<AssemblyDataItem>  assemblyDataItems=new AssemblyStatus().getAssemblyStatus(basicData.getSfcRef(), operationRef);
			return assemblyDataItems;
			//update assembly status
//			executePluginButtonActivityWithFeedback("Z_ASSEMBLY");
//			executePluginButtonActivityWithFeedback("Z_LABOR_ON_OFF");
			
		}catch (PODCustomException e) {
			//Given value is not the sfc number
			throw new PODCustomException("Exception while assembling purchased product"+e.getMessage());
		}  catch (AbortProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new PODCustomException("Exception while assembling purchased product"+e.getMessage());
		} catch (BusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new PODCustomException("Business Exception : "+e.getMessage());
		} 
	}
}
