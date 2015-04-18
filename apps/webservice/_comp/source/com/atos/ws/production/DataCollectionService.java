package com.atos.ws.production;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import com.atos.ws.production.postscanactivities.datacollection.DCGroupDataVO;
import com.atos.ws.production.postscanactivities.datacollection.DCParemeterDataVO;
import com.sap.engine.services.webservices.espbase.configuration.ann.dt.AuthenticationDT;
import com.sap.engine.services.webservices.espbase.configuration.ann.dt.AuthenticationEnumsAuthenticationLevel;
import com.sap.engine.services.webservices.espbase.configuration.ann.rt.AuthenticationRT;
import com.sap.me.datacollection.DCGroupRequest;
import com.sap.me.datacollection.DCGroupResponse;
import com.sap.me.datacollection.DCGroupsResponse;
import com.sap.me.datacollection.DCParameterResponse;
import com.sap.me.datacollection.DataCollectionServiceInterface;
import com.sap.me.datacollection.FindParametricMeasureDetailsForSfcsRequest;
import com.sap.me.datacollection.FindParametricMeasureDetailsForSfcsResponse;
import com.sap.me.datacollection.InvalidNumberException;
import com.sap.me.datacollection.InvalidParameterFormatException;
import com.sap.me.datacollection.InvalidParameterNameException;
import com.sap.me.datacollection.LogDCGroupRequest;
import com.sap.me.datacollection.LogSfcParameterValue;
import com.sap.me.datacollection.LogSfcValue;
import com.sap.me.datacollection.LowerLimitExceededException;
import com.sap.me.datacollection.MissingRequiredValueException;
import com.sap.me.datacollection.ParametricMeasureDetail;
import com.sap.me.datacollection.PerformOverridesException;
import com.sap.me.datacollection.UpperLimitExceededException;
import com.sap.me.datatype.DataFieldConfigurationServiceInterface;
import com.sap.me.demand.SFCBOHandle;
import com.sap.me.extension.Services;
import com.sap.me.frame.domain.BusinessException;
import com.sap.me.frame.service.CommonMethods;
import com.sap.me.production.SfcStateServiceInterface;



@AuthenticationDT(authenticationLevel = AuthenticationEnumsAuthenticationLevel.BASIC)
@AuthenticationRT(AuthenticationMethod = "sapsp:HTTPBasic")
@WebService
public class DataCollectionService {
	public SfcStateServiceInterface sfcStateService = null;
	public DataCollectionServiceInterface dataCollectionServiceInterface = null;
	public DataFieldConfigurationServiceInterface fieldConfigurationServiceInterface = null;
	
	
	@WebMethod
	public DataCollectionResponse dataCollectionOperatns(@WebParam(name = "dataCollectionRequest") DataCollectionRequest dataCollectionRequest) throws DCCollectionException,BusinessException {
		
	
	DataCollectionResponse response = new DataCollectionResponse();
    if(dataCollectionRequest.getDcGroupDataVOList()==null || dataCollectionRequest.getDcGroupDataVOList().size()==0){
    	//throw exception if DGGroup is not present
		throw new DCCollectionException("DC Group is not present");
	}
    //Initialising services
    sfcStateService = (SfcStateServiceInterface) Services.getService("com.sap.me.production", "SfcStateService",dataCollectionRequest.getSite());
    dataCollectionServiceInterface = (DataCollectionServiceInterface) Services.getService("com.sap.me.datacollection", "DataCollectionService",dataCollectionRequest.getSite());
    fieldConfigurationServiceInterface = (DataFieldConfigurationServiceInterface) Services.getService("com.sap.me.datatype", "DataFieldConfigurationService",dataCollectionRequest.getSite());
    
     
	List<DCGroupDataVO> dcGroupDataVOListForResponse = new ArrayList<DCGroupDataVO>();
    
	for(DCGroupDataVO dcGroupDataVO:dataCollectionRequest.getDcGroupDataVOList()){
		//DC Groups for loop
		List<LogSfcValue> logSfcValueList = new ArrayList<LogSfcValue>();
		DCGroupResponse dgGrpResponse = getDCGroupdetails(dcGroupDataVO.getDcGroupName());
		if(dgGrpResponse==null){
			//throw exception if parameter not present in db
			throw new DCCollectionException("DC Group does not exists");
		}
		for(DCParemeterDataVO dcParemeterDataVO:dcGroupDataVO.getDcParemeterDataVO()){
			//DC group parameters for loop
			DCParameterResponse dcParameterDetails = null;
			for(DCParameterResponse dcParameterResponse:dgGrpResponse.getDcParameterList()){
				//for loop for finding parameter details
				if(dcParameterResponse.getParameterName().equals(dcParemeterDataVO.getParameterName())){
					dcParameterDetails = dcParameterResponse;
					dcParemeterDataVO.setMinValue(dcParameterResponse.getMinValue()==null?"":dcParameterResponse.getMinValue().toString());
					dcParemeterDataVO.setMaxValue(dcParameterResponse.getMaxValue()==null?"":dcParameterResponse.getMaxValue().toString());
					break;
				}
			}
			if(dcParameterDetails == null){
				//throw exception if parameter not present in db
				throw new DCCollectionException("Invalid parameter : "+dcParemeterDataVO.getParameterName()+"for DC Group : "+dcGroupDataVO.getDcGroupName());
			}
			List<LogSfcParameterValue> logParameterValues = new ArrayList<LogSfcParameterValue>();
			LogSfcParameterValue logParameterValue = new LogSfcParameterValue();
			logParameterValue.setDcComment(dcParemeterDataVO.getDcComment());
			logParameterValue.setName(dcParemeterDataVO.getParameterName());
			logParameterValue.setValue(dcParemeterDataVO.getActualValue());
			logParameterValue.setDataType(dcParameterDetails.getDataType());
			logParameterValue.setOverrideDone(dcParameterDetails.isOverrideDone());
			if(dcParameterDetails.isOverrideDone()){
				logParameterValue.setOverrideDoneUser("UserBO:1000,SITE_ADMIN");				
			}			
			logParameterValue.setOverrideMinMax(dcParameterDetails.isOverrideMinMax());
			logParameterValue.setUdf(false);
			logParameterValues.add(logParameterValue);
			LogSfcValue logSfcValue = new LogSfcValue();
			logSfcValue.setValueList(logParameterValues);
			logSfcValue.setParametricRef(dcParameterDetails.getRef());
			SFCBOHandle sfcboHandle= new SFCBOHandle(CommonMethods.getSite(), dataCollectionRequest.getSfc());
			logSfcValue.setSfcRef(sfcboHandle.getValue());
			logSfcValue.setTimesProcessed(new BigDecimal(1));
			logSfcValueList.add(logSfcValue);
		}	
	LogDCGroupRequest logSfcDCGroupRequest = new LogDCGroupRequest();
	logSfcDCGroupRequest.setActivityId("DC500");
	logSfcDCGroupRequest.setDcGroupRef(dgGrpResponse.getDcGroupRef());
	logSfcDCGroupRequest.setOperationRef(dataCollectionRequest.getOperationRef());
	logSfcDCGroupRequest.setSfcValueList(logSfcValueList);	
	logSfcDCGroupRequest.setUserRef("UserBO:1000,SITE_ADMIN");
	logSfcDCGroupRequest.setResourceRef(dataCollectionRequest.getResourceRef());	
	try {
		dataCollectionServiceInterface.logDCGroup(logSfcDCGroupRequest);
		//setting response
		List<DCParemeterDataVO> parametricMeasureDetailsForResponse = new ArrayList<DCParemeterDataVO>();
		for(DCParemeterDataVO dcParemeterDataVO:dcGroupDataVO.getDcParemeterDataVO()){
			//String DCGroupRef,String OperationRef,String parametricName,List<String> sfcs
			List<String> sfcs = new ArrayList<String>();
			SFCBOHandle sfcboHandle= new SFCBOHandle(CommonMethods.getSite(), dataCollectionRequest.getSfc());
			sfcs.add(sfcboHandle.getValue());
			ParametricMeasureDetail measureDetail = getParametricMeasure(dgGrpResponse.getDcGroupRef(),dataCollectionRequest.getOperationRef(),dcParemeterDataVO.getParameterName(),sfcs);
			DCParemeterDataVO paremeterDataVO = new DCParemeterDataVO();
			paremeterDataVO.setMaxValue(dcParemeterDataVO.getMaxValue());
			paremeterDataVO.setMinValue(dcParemeterDataVO.getMinValue());
			paremeterDataVO.setDcComment(measureDetail.getDcComment());
			paremeterDataVO.setActualValue(measureDetail.getActual());
			paremeterDataVO.setParameterName(measureDetail.getMeasureName());
			paremeterDataVO.setParameterStatus(measureDetail.getMeasureStatus().value());
			paremeterDataVO.setParameterRef(measureDetail.getParametricRef());
			paremeterDataVO.setOriginalComment(measureDetail.getOriginalDCComment());
			paremeterDataVO.setActualNumber(measureDetail.getActualNumber()==null?"":measureDetail.getActualNumber().toString());
			parametricMeasureDetailsForResponse.add(paremeterDataVO);
		}
		DCGroupDataVO dcGroupDataVOForResponse = new DCGroupDataVO();
		dcGroupDataVOForResponse.setDcGroupName(dgGrpResponse.getDcGroup());
		dcGroupDataVOForResponse.setDgGroupRef(dgGrpResponse.getDcGroupRef());
		dcGroupDataVOForResponse.setDcParemeterDataVO(parametricMeasureDetailsForResponse);
		dcGroupDataVOListForResponse.add(dcGroupDataVOForResponse);
		
		
	} catch (MissingRequiredValueException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (InvalidNumberException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (InvalidParameterNameException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (InvalidParameterFormatException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (LowerLimitExceededException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (UpperLimitExceededException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (PerformOverridesException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (BusinessException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	}

	response.setDcGroupDataVOList(dcGroupDataVOListForResponse);
		return response;

	}
	//finding dc group ref
	private DCGroupResponse getDCGroupdetails(String dcGroupName) throws BusinessException{
		DCGroupsResponse groupResponse=dataCollectionServiceInterface.findDcGroupsByName(new DCGroupRequest(dcGroupName));
		for(DCGroupResponse response: groupResponse.getDcGroupList()){
			if(response.isCurrentRevision()){
				return response;
			}
		}
		return null;
	}
	private ParametricMeasureDetail getParametricMeasure(String DCGroupRef,String OperationRef,String parametricName,List<String> sfcs) throws BusinessException{
		FindParametricMeasureDetailsForSfcsRequest detailsForSfcsRequest=new FindParametricMeasureDetailsForSfcsRequest();
		detailsForSfcsRequest.setDcGroupRef(DCGroupRef);
		detailsForSfcsRequest.setOperationRef(OperationRef);
		detailsForSfcsRequest.setParametricMeasureName(parametricName);
		detailsForSfcsRequest.setSfcRefList(sfcs);
		ParametricMeasureDetail measureDetailLatest= null;
		FindParametricMeasureDetailsForSfcsResponse measureDetailsForSfcsResponse=dataCollectionServiceInterface.findParametricMeasureDetailsForSfcs(detailsForSfcsRequest);
		for(ParametricMeasureDetail measureDetail:measureDetailsForSfcsResponse.getParametricMeasureDetailsList()){
			if(measureDetailLatest!=null){
				//logic to find latest parametric measure
				if(measureDetail.getTestDateTime().compareTo(measureDetailLatest.getTestDateTime())>0){
					measureDetailLatest = measureDetail;
				}			
			}else{
				measureDetailLatest=measureDetail;
			}
			
		}
		return measureDetailLatest;
	}

	

}
