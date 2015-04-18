package com.atos.wpmf.web.podplugin.postscanactivities;

import java.util.List;

import com.sap.me.common.ObjectReference;
import com.sap.me.datatype.DataFieldInfo;
import com.sap.me.datatype.DataTypeConfigurationServiceInterface;
import com.sap.me.datatype.DataTypeInfo;
import com.sap.me.demand.SFCBOHandle;
import com.sap.me.extension.Services;
import com.sap.me.frame.domain.BusinessException;
import com.sap.me.frame.service.CommonMethods;
import com.sap.me.productdefinition.ItemConfigurationServiceInterface;
import com.sap.me.productdefinition.ItemFullConfiguration;
import com.sap.me.production.InvalidSfcException;
import com.sap.me.production.SfcBasicData;
import com.sap.me.production.SfcStateServiceInterface;
import com.sap.me.production.podclient.BasePodPlugin;


public class PVTCheck  extends BasePodPlugin {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final String PVT_PATTERN="[^#]*#[^#]*#[^#]*#";
	private SfcStateServiceInterface sfcStateService = (SfcStateServiceInterface) Services.getService("com.sap.me.production", "SfcStateService");
	ItemConfigurationServiceInterface itemConfigurationServiceInterface=(ItemConfigurationServiceInterface) Services.getService("com.sap.me.productdefinition", "ItemConfigurationService");
	DataTypeConfigurationServiceInterface dataTypeConfigurationServiceInterface = (DataTypeConfigurationServiceInterface) Services.getService("com.sap.me.datatype", "DataTypeConfigurationService");
	public void postScanOperations(String operationRef,String resourceRef,String barcode,String sfcRef)throws PODCustomException{
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
		if(sfcRef == null || "".equals(sfcRef)){
			throw new PODCustomException("Select parent sfc for scanning sub assembly");
		}
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
			if(product==null){
				throw new PODCustomException("Invalid value for assembly, product is missing : "+barcode);
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
			new AssemblePurchasedComponent().assemblePurcharedComponent(operationRef,resourceRef,barcode,product,vendor,quality,sfcRef);
			
		}else{
				
			//check whether valid sfc
			try {
				
				SFCBOHandle sfcref = new SFCBOHandle(CommonMethods.getSite(), barcode);
				SfcBasicData sfcBasicDataFromSubAssy=sfcStateService.findSfcDataByRef(new ObjectReference(sfcref.getValue()));	
				//operator scans invalid SFC
				if(sfcBasicDataFromSubAssy==null){
					throw new PODCustomException("Invalid value for assembly");
				}
				new AssembleManufacturedComponent().assembleManufacturedComponent(operationRef, resourceRef, sfcBasicDataFromSubAssy,sfcRef);
				
				
			} catch(InvalidSfcException  invalidSfcException){
				throw new PODCustomException("Scanned Sfc / barcode  is invalid");
			}	catch(BusinessException businessException){
				throw new PODCustomException("Business Exception while assembling manufactured component : "+businessException.getMessage());
			}
			
			
		} 
	}
	
	/***************************************************************************************?
	 * *******************************DatabaseCode*******************************************/
	 
//			private final SystemBase dbBase = SystemBase.createSystemBase("jdbc/jts/wipPool");
//		
//			private Connection getConnection() {
//					Connection con = null;
//					
//					con = dbBase.getDBConnection();
//					
//					return con;
//			}
//			public List<DataFieldVO> findAssemblyDataFields(String dataTypeBo) {
//
//				Connection con = null;
//				String query = "select HANDLE, DATA_FIELD_BO,REQUIRED from DATA_TYPE_FIELD where where DATA_TYPE_BO = ?";				
//				PreparedStatement ps = null;
//				 List<DataFieldVO> dataFieldVOs = new ArrayList<DataFieldVO>();
//				try {
//
//					con = getConnection();
//					ps = con.prepareStatement(query);
//					//ps.setString(1, operation);
//					ps.setString(1, dataTypeBo);
//					ResultSet rs = ps.executeQuery();
//					while (rs.next()) {
//						DataFieldVO dataFieldVO = new DataFieldVO();
//						dataFieldVO.setDataFieldBO(rs.getString("DATA_FIELD_BO"));
//						dataFieldVO.setHandle(rs.getString("HANDLE"));
//						dataFieldVO.setRequired(rs.getBoolean("REQUIRED"));						
//						dataFieldVOs.add(dataFieldVO);
//					}
//
//				} catch (SQLException e) {
//					e.printStackTrace();
//				} finally {
//					try {
//						if (ps != null) {
//
//							ps.close();
//						}
//						if (con != null) {
//							con.close();
//						}
//					} catch (Exception e2) {
//						e2.printStackTrace();
//					}
//
//				}
//				return dataFieldVOs;
//			}
	
}
