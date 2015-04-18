package com.atos.ws.production.postscanactivities;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.sap.me.extension.Services;
import com.sap.me.frame.domain.BusinessException;
import com.sap.me.production.FindSfcsAtOperationRequest;
import com.sap.me.production.SfcBasicData;
import com.sap.me.production.SfcGroupStatusEnum;
import com.sap.me.production.SfcStateServiceInterface;

public class CheckActiveSfcForPvt {
	//get the active sfc for given operation , if no active sfc , throws exception
	public SfcBasicData checkActiveSfcForOperation(String operationRef, String resourceRef)throws PODCustomException{
		try {
			SfcStateServiceInterface sfcStateService = (SfcStateServiceInterface) Services.getService("com.sap.me.production", "SfcStateService");
			FindSfcsAtOperationRequest sfcsAtOperationRequest1=new FindSfcsAtOperationRequest(operationRef);					
			List<SfcGroupStatusEnum> includedStatusCollection1 = new ArrayList<SfcGroupStatusEnum>();
			includedStatusCollection1.add(SfcGroupStatusEnum.ACTIVE);
			sfcsAtOperationRequest1.setIncludedStatusCollection(includedStatusCollection1);
			Collection<SfcBasicData> basicDatas=sfcStateService.findSfcsAtOperation(sfcsAtOperationRequest1);	
			//there should be only one active sfc for given operation
			if(basicDatas.size()==1){
				for(SfcBasicData basicData:basicDatas){
					return basicData;
				} 
			}else if(basicDatas.size()==0){
				throw new PODCustomException("No Active SFC is present in queue");
			}
			
		} catch (BusinessException businessException) {
			// TODO: handle exception
			businessException.printStackTrace();
		}
		return null;
		
	}
}
