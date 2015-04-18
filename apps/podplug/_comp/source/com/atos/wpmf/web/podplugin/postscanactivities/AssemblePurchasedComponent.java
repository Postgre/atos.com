package com.atos.wpmf.web.podplugin.postscanactivities;

import javax.faces.event.AbortProcessingException;

import com.sap.me.frame.domain.BusinessException;
import com.sap.me.production.SfcBasicData;
import com.sap.me.production.podclient.BasePodPlugin;
import com.sap.me.wpmf.MessageType;
import com.sap.me.wpmf.util.MessageHandler;

public class AssemblePurchasedComponent  extends BasePodPlugin{

	private static final long serialVersionUID = 1L;

public  void assemblePurcharedComponent(String operationRef,String resourceRef,String barcode,String product,String vendor,String quality)throws PODCustomException{
		
		
		try {
			//get active sfc
			SfcBasicData basicData=new CheckActiveSfcForPvt().checkActiveSfcForOperation(operationRef, resourceRef);
			//validate component's vendor before assembly
			new ValidVendorCheck().validateVendor(operationRef, basicData, product, vendor,quality);
			//assemble component
			new AssembleData().assembleComponent(basicData,resourceRef,operationRef,vendor,quality,product,null);
			//update assembly status
			executePluginButtonActivityWithFeedback("Z_ASSEMBLY");
			
		}catch (PODCustomException e) {
			//Given value is not the sfc number
			throw new PODCustomException("Exception while assembling purchased product"+e.getMessage());
		}  catch (AbortProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new PODCustomException("Exception while assembling purchased product"+e.getMessage());
		} catch (BusinessException e) {
			//Given value is not the sfc number
			e.printStackTrace();
			throw new PODCustomException("Exception while assembling purchased product"+e.getMessage());
			
		}
	}
}
