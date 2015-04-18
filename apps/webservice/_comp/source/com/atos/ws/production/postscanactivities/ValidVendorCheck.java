package com.atos.ws.production.postscanactivities;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.sap.me.extension.Services;
import com.sap.me.frame.SystemBase;
import com.sap.me.frame.domain.BusinessException;
import com.sap.me.productdefinition.OperationBOHandle;
import com.sap.me.production.AssembledComponent;
import com.sap.me.production.AssembledComponentsResponse;
import com.sap.me.production.AssemblyDataField;
import com.sap.me.production.GroupAssembledAsBuiltComponentsRequest;
import com.sap.me.production.RetrieveComponentsServiceInterface;
import com.sap.me.production.SfcBasicData;
import com.sap.me.production.SfcIdentifier;

public class ValidVendorCheck {
	private RetrieveComponentsServiceInterface componentsServiceInterface = (RetrieveComponentsServiceInterface) Services.getService("com.sap.me.production", "RetrieveComponentsService");
	
	
	private static String VENDOR = "VENDOR_INFO";
	private static String QUALITY = "QUALITY_INFO";
	private static String SFC = "SFC";
	private static String ALL = "ALL";
	public boolean validateVendor(String operationRef,SfcBasicData basicData,String product,String vendor,String quality) throws PODCustomException{
		//get the item
		try{
			//get the validation parameters from ASSEMBLY_DATA_VALIDATIONS
			OperationBOHandle boHandle = new OperationBOHandle(operationRef);
			List<AssemblyDataValidationsEntity> assemblyDataValidationsEntities = findAssemblyValidations(boHandle.getOperation(),product);				
			// get as built summary data for given product
				
				boolean validAssyData = true;				
				String errorMessage = "";
				//get assembledComponents for curr operation 
				List<AssembledComponent> assembledComponentsForCurrOperation = getAssembledComponent(product, operationRef, basicData);
				//get max assemblyId
				Integer nextIndex=assembledComponentsForCurrOperation.size();				
				//check  for vendor
				
				//iterating assembly validation field data
				for(AssemblyDataValidationsEntity assemblyDataValidationsEntity:assemblyDataValidationsEntities){	
					if(assemblyDataValidationsEntity.getValidateFor().equals(SFC)){
						if(!basicData.getSfc().equals(assemblyDataValidationsEntity.getSfc())){
							continue;
						}
					}
					String invalidField = "";
					String invalidFieldValue = "";
					List<AssembledComponent> assembledComponents=getAssembledComponent(product,assemblyDataValidationsEntity.getCheckOperation(),basicData);
					//skip check if validation data is not present
					if(assembledComponents.size()<(nextIndex+1) || assembledComponents.size()==0){
						continue;
					}
					//if data is present retrieve and check data
					Collections.sort(assembledComponents, new CompareAssemblyComp());
					AssembledComponent assembledComponentForValidation = assembledComponents.get(nextIndex);
					String assemblyCheckField = assemblyDataValidationsEntity.getCheckField();
					 //iterating data fields of already assembled components							
					for(AssemblyDataField assemblyDataField:assembledComponentForValidation.getAssemblyDataFields()){
										
										String assembledCompDataField = assemblyDataField.getAttribute();
										// if field is vendor
										if(assembledCompDataField.equals(assemblyCheckField) && assemblyCheckField.equals(VENDOR)){
											if(!assemblyDataField.getValue().equals(vendor)){
												validAssyData = false;
												invalidField =assemblyDataValidationsEntity.getCheckField();
												invalidFieldValue = assemblyDataField.getValue();
												break;
											}
										}
										if(assembledCompDataField.equals(assemblyCheckField) && assemblyCheckField.equals(QUALITY)){
											if(!assemblyDataField.getValue().equals(quality)){
												validAssyData = false;
												invalidField = assemblyDataValidationsEntity.getCheckField();
												invalidFieldValue = assemblyDataField.getValue();
												break;
											}
										}
						}
								
								
						if(!"".equals(errorMessage)){
								errorMessage = errorMessage+",";
						}
						if(!"".equals(invalidField)&&(!"".equals(invalidFieldValue))){
								errorMessage = errorMessage+invalidField+" = "+invalidFieldValue;
						}
							
						}
							
						
						if(!validAssyData){
							throw new PODCustomException(" Assembly data  doesn't match with other assmbly data "+errorMessage+" of given product: "+product);
						}			
			
		}catch(BusinessException exp){
			throw new PODCustomException("Business exception while validating vendor : "+exp.getMessage());
		}
		
		return true;
	}
	private List<AssembledComponent>  getAssembledComponent(String compName,String operationRef,SfcBasicData basicData)throws BusinessException{
		GroupAssembledAsBuiltComponentsRequest asBuiltComponentsRequest = new GroupAssembledAsBuiltComponentsRequest();		
		asBuiltComponentsRequest.setOperationRef(operationRef);
		asBuiltComponentsRequest.setComponentName(compName);
		List<SfcIdentifier> identifiers = new ArrayList<SfcIdentifier>();
		identifiers.add(new SfcIdentifier(basicData.getSfcRef()));	
		asBuiltComponentsRequest.setSfcList(identifiers);
		//identifiers.add(new SfcIdentifier("SFCBO:1780,1780382"));
		asBuiltComponentsRequest.setSfcList(identifiers);
		//finding as built components
		AssembledComponentsResponse assembledComponentsResponse=componentsServiceInterface.findAssembledAsBuiltComponents(asBuiltComponentsRequest);
		List<AssembledComponent> assembledComponents = assembledComponentsResponse.getAssembledComponentsList();
		return assembledComponents;
	}
	/***************************************************************************************?
	 * *******************************DatabaseCode*******************************************/
	 
			private final SystemBase dbBase = SystemBase.createSystemBase("jdbc/jts/wipPool");
		
			private Connection getConnection() {
					Connection con = null;
					
					con = dbBase.getDBConnection();
					
					return con;
			}
			public List<AssemblyDataValidationsEntity> findAssemblyValidations(String operation, String material) {

				Connection con = null;
				String query = "SELECT FIRST_OPERATION , MATERIAL , CHECK_OPERATION , CHECK_FIELD ,VALIDATE , VALIDATE_FOR  , SFC FROM ASSEMBLY_VALIDATIONS_DATA where FIRST_OPERATION like '%"+operation+"%' and MATERIAL = ? and VALIDATE = 'YES'";
				
				PreparedStatement ps = null;
				 List<AssemblyDataValidationsEntity> assemblyDataValidationsEntities = new ArrayList<AssemblyDataValidationsEntity>();
				try {

					con = getConnection();
					ps = con.prepareStatement(query);
					//ps.setString(1, operation);
					ps.setString(1, material);
					ResultSet rs = ps.executeQuery();
					while (rs.next()) {
						AssemblyDataValidationsEntity assemblyDataValidationsEntity=new AssemblyDataValidationsEntity();
						assemblyDataValidationsEntity.setCheckOperation(rs.getString("CHECK_OPERATION"));
						assemblyDataValidationsEntity.setFirstOperation(rs.getString("FIRST_OPERATION"));
						assemblyDataValidationsEntity.setMaterial(rs.getString("MATERIAL"));
						assemblyDataValidationsEntity.setCheckField(rs.getString("CHECK_FIELD"));
						assemblyDataValidationsEntity.setValidateFor(rs.getString("VALIDATE_FOR"));
						assemblyDataValidationsEntity.setSfc(rs.getString("SFC"));
						if(rs.getString("VALIDATE")!=null)
							assemblyDataValidationsEntity.setValidate(rs.getString("VALIDATE").equals("YES")?true:false);
						assemblyDataValidationsEntities.add(assemblyDataValidationsEntity);
					}

				} catch (SQLException e) {
					e.printStackTrace();
				} finally {
					try {
						if (ps != null) {

							ps.close();
						}
						if (con != null) {
							con.close();
						}
					} catch (Exception e2) {
						e2.printStackTrace();
					}

				}
				return assemblyDataValidationsEntities;
			}
	}
