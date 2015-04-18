package com.atos.ws.production.postscanactivities;

import java.util.ArrayList;
import java.util.List;

import com.atos.ws.production.postscanactivities.datacollection.DCGroupDataVO;
import com.atos.ws.production.postscanactivities.datacollection.DCParemeterDataVO;
import com.sap.me.datacollection.DCParameterResponse;
import com.sap.me.datacollection.DataCollectionServiceInterface;
import com.sap.me.datacollection.DcGroupForSfcsResponse;
import com.sap.me.datacollection.DcGroupsForSfcsRequest;
import com.sap.me.datacollection.DcGroupsForSfcsResponse;
import com.sap.me.datacollection.OperationSfc;
import com.sap.me.extension.Services;
import com.sap.me.frame.domain.BusinessException;


public class AssemblyDataCollection {
	DataCollectionServiceInterface dataCollectionServiceInterface = (DataCollectionServiceInterface) Services.getService("com.sap.me.datacollection", "DataCollectionService");

	

	public List<DCGroupDataVO> collectData(String operationRef,String resourceRef,String sfcRef) throws PODCustomException{
		DcGroupsForSfcsRequest dcGroupsForSfcsRequest=new DcGroupsForSfcsRequest(operationRef,resourceRef);
		OperationSfc operationSfc = new OperationSfc(sfcRef);
		List<OperationSfc> sfcList = new ArrayList<OperationSfc>();
		sfcList.add(operationSfc);
		dcGroupsForSfcsRequest.setSfcList(sfcList);
		List<DCGroupDataVO> dcGroupDataVOs = new ArrayList<DCGroupDataVO>();
		try {
			DcGroupsForSfcsResponse dcGroupsForSfcsResponse=dataCollectionServiceInterface.findDcGroupsForSfcs(dcGroupsForSfcsRequest);
			for(DcGroupForSfcsResponse response:dcGroupsForSfcsResponse.getDcGroupList()){
				DCGroupDataVO dcGroupDataVO = new DCGroupDataVO();
				dcGroupDataVO.setDcGroupName(response.getDcGroup());
				dcGroupDataVO.setDgGroupRef(response.getDcGroupRef());
				List<DCParemeterDataVO> parameterList = new ArrayList<DCParemeterDataVO>();
				for(DCParameterResponse parameterResponse:response.getDcParameterList()){
					DCParemeterDataVO paremeterDataVO = new DCParemeterDataVO();
					paremeterDataVO.setParameterName(parameterResponse.getParameterName());
					paremeterDataVO.setParameterRef(parameterResponse.getRef());
					paremeterDataVO.setParameterStatus(parameterResponse.getStatus());
					paremeterDataVO.setMaxValue(parameterResponse.getMaxValue().toString());
					paremeterDataVO.setMinValue(parameterResponse.getMinValue().toString());
					parameterList.add(paremeterDataVO);
				}
				dcGroupDataVO.setDcParemeterDataVO(parameterList);
				dcGroupDataVOs.add(dcGroupDataVO);	
				
			}
		} catch (BusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new PODCustomException("Business exception while fetching the dcgroup for sfc "+sfcRef);
		}
		
        return dcGroupDataVOs;
		
	}
	
}
