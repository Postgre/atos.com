package com.atos.hook;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import com.atos.hook.customDataEnums.CheckDigit;
import com.atos.hook.customDataEnums.Driver;
import com.atos.hook.customDataEnums.Version;
import com.sap.me.common.AttributeValue;
import com.sap.me.common.CustomValue;
import com.sap.me.common.ObjectReference;
import com.sap.me.demand.ShopOrderFullConfiguration;
import com.sap.me.demand.ShopOrderReleaseHookDTO;
import com.sap.me.demand.ShopOrderServiceInterface;
import com.sap.me.frame.SystemBase;
import com.sap.me.frame.domain.BusinessException;
import com.sap.me.frame.service.CommonMethods;
import com.sap.me.labor.LaborChargeCodeBasicConfiguration;
import com.sap.me.labor.LaborChargeCodeConfigurationServiceInterface;
import com.sap.me.plant.WorkCenterConfigurationServiceInterface;
import com.sap.me.plant.WorkCenterKeyData;
import com.sap.me.productdefinition.BOMComponentConfiguration;
import com.sap.me.productdefinition.BOMConfigurationServiceInterface;
import com.sap.me.productdefinition.BOMFullConfiguration;
import com.sap.me.productdefinition.ItemConfigurationServiceInterface;
import com.sap.me.productdefinition.ItemFullConfiguration;
import com.sap.me.productdefinition.OperationConfigurationServiceInterface;
import com.sap.me.productdefinition.ReadBOMRequest;
import com.sap.me.productdefinition.RouterConfigurationServiceInterface;
import com.sap.me.productdefinition.RouterFullConfiguration;
import com.sap.me.productdefinition.RouterStep;
import com.sap.me.production.SerializeSfcDetail;
import com.sap.me.production.SerializeSfcRequest;
import com.sap.me.production.SfcBasicData;
import com.sap.me.production.SfcStateServiceInterface;
import com.sap.me.production.SplitSerializeServiceInterface;
import com.sap.me.security.RunAsServiceLocator;
import com.visiprise.frame.configuration.ServiceReference;
import com.visiprise.frame.service.ext.ActivityInterface;

/**
 * @author Oksana Zubchenko
 * 
 */
public class GenericPatternHook implements
		ActivityInterface<ShopOrderReleaseHookDTO> {

	private SfcStateServiceInterface sfcStateServiceInterface;
	private ItemConfigurationServiceInterface itemConfigurationServiceInterface;
	private SplitSerializeServiceInterface serializeServiceInterface;
	private ShopOrderServiceInterface shoporderService;
	private String site = CommonMethods.getSite();
	private String user=CommonMethods.getUserId();
	private final SystemBase dbBase = SystemBase
			.createSystemBase("jdbc/jts/wipPool");
	private static final String HASH = "#";
	private static final String MC2="MC2";
	private static final String VERSION = "VERSION";
	private static final String CHECK_DIGIT = "CHECK_DIGIT";
	private static final String DRIVER = "DRIVER";
	private static final String DATE_FORMAT = "yyyyMMdd";
	public static final String[] monthAlphabetMap = {"A","B","C","D","E","F","G","H","I","J","K","L"};
	
	private Connection getConnection() {
		Connection con = null;
		

		con = dbBase.getDBConnection();

		return con;
	}

	@Override
	public void execute(ShopOrderReleaseHookDTO shopOrderReleaseHookDTO)
			throws Exception {
		ServiceReference sfcServiceRef = new ServiceReference(
				"com.sap.me.production", "SfcStateService");
		this.sfcStateServiceInterface = RunAsServiceLocator.getService(
				sfcServiceRef, SfcStateServiceInterface.class,user, site,
				null);
		ServiceReference itemServiceRef = new ServiceReference(
				"com.sap.me.productdefinition", "ItemConfigurationService");
		itemConfigurationServiceInterface = RunAsServiceLocator.getService(
				itemServiceRef, ItemConfigurationServiceInterface.class,
				"MESYS", site, null);
		ServiceReference shoporderServiceRef = new ServiceReference(
				"com.sap.me.demand", "ShopOrderService");
		shoporderService = RunAsServiceLocator.getService(shoporderServiceRef,
				ShopOrderServiceInterface.class,user, site, null);
		ServiceReference serializeServiceRef = new ServiceReference(
				"com.sap.me.production", "SplitSerializeService");
		serializeServiceInterface = RunAsServiceLocator.getService(serializeServiceRef,
				SplitSerializeServiceInterface.class,user, site, null);
		//(#[^#]*#)
		Pattern pattern = Pattern.compile("(" + HASH + "[^" + HASH + "]*"
				+ HASH + ")");
		
		List<ShopOrderReleaseHookDTO.AdditionalInfoList> additionalInfoList = shopOrderReleaseHookDTO
				.getAdditionalInfoList();
		for (ShopOrderReleaseHookDTO.AdditionalInfoList additionalInfo : additionalInfoList) {
			SfcBasicData basicData = sfcStateServiceInterface
					.findSfcDataByRef(new ObjectReference(additionalInfo
							.getSfcBO().getValue()));
			Matcher matcher = pattern.matcher(basicData.getSfc());
			int countHash = StringUtils.countMatches(basicData.getSfc(), HASH);
			if (countHash % 2 != 0) {
				throw new Exception("Invalid pattern " + HASH
						+ " is missing in some pattern");
			}
			Set<String> patterns = new HashSet<String>();
			while (matcher.find()) {
				patterns.add(matcher.group(1));
			}
			String replacedSFC = basicData.getSfc();
			for (String pat : patterns) {
				String patternTemp = StringUtils.substringBetween(pat, HASH,
						HASH);
				com.atos.hook.Pattern patternObj=findPattern(patternTemp);
				if(patternObj==null)
					continue;
				List<PatternSequence> patternSequences = findPatternSequence("PatternBO:"
						+ this.site + "," + patternTemp);
				String patternToReplace = "";				
				patternToReplace = findPatternSequenceAttributeValues(patternObj,additionalInfo,shopOrderReleaseHookDTO,patternSequences);	
				//Concatenating current system date in "yyyyMMdd" format
				patternToReplace=patternToReplace+geCurrentMonth();
				if (!StringUtils.isBlank(patternToReplace)) {
					replacedSFC = StringUtils.replace(replacedSFC, pat,
							patternToReplace);
				}
				

			}		
			if(patterns.size()!=0){
				SerializeSfcDetail serializeSfcDetail=new SerializeSfcDetail();
				serializeSfcDetail.setSfc(replacedSFC);
				List<SerializeSfcDetail> newSfcList=new ArrayList<SerializeSfcDetail>();
				newSfcList.add(serializeSfcDetail);
				SerializeSfcRequest serializeSfcRequest=new SerializeSfcRequest("SFCBO:"+CommonMethods.getSite()+","+basicData.getSfc());
				serializeSfcRequest.setNewSfcList(newSfcList);
				serializeSfcRequest.setQuantityToSerialize(new BigDecimal(1));
				serializeServiceInterface.serializeSfc(serializeSfcRequest);
			}
			

		}

	}

	public String  findPatternSequenceAttributeValues(com.atos.hook.Pattern pattern,ShopOrderReleaseHookDTO.AdditionalInfoList additionalInfoList,ShopOrderReleaseHookDTO shopOrderReleaseHookDTO,List<PatternSequence> patternSequences)throws BusinessException {
		String patternType=pattern.getPatternType();
		Map<String,String[]> attributes = null;
		ShopOrderFullConfiguration shopOrderFullConfiguration=this.shoporderService.readShopOrder(new ObjectReference(shopOrderReleaseHookDTO.getShopOrderBO().getValue()));
		String stringToReplace="";
		if(AttributeValueConstants.MATERIAL.equals(patternType)){
			attributes=new HashMap<String,String[]>();
			ItemFullConfiguration fullConfiguration=itemConfigurationServiceInterface.readItem(new ObjectReference(additionalInfoList.getItemBO().getValue()));
			
			String customData="";
			for(CustomValue value:fullConfiguration.getCustomData()){
				if(!"".equals(customData)){
					customData=customData+",";
				}
				customData=customData+value.getName()+"="+value.getValue();
			}		
			attributes.put(AttributeValueConstants.VERSION, new String[]{fullConfiguration.getRevision(),null});
			attributes.put(AttributeValueConstants.TYPE, new String[]{fullConfiguration.getItemType().value(),fullConfiguration.getItemType().name()});
			attributes.put(AttributeValueConstants.ORDER_TYPE, new String[]{shopOrderFullConfiguration.getShopOrderType().value(),shopOrderFullConfiguration.getShopOrderType().name()});
			attributes.put(AttributeValueConstants.LOT_SIZE,  new String[]{fullConfiguration.getLotSize().toString(),null});
			attributes.put(AttributeValueConstants.DRAWING_NAME, new String[]{fullConfiguration.getDrawingName(),null});
			attributes.put(AttributeValueConstants.PANEL, new String[]{fullConfiguration.getPanel()?"P":"",null});
			attributes.put(AttributeValueConstants.TSM_VAL, new String[]{fullConfiguration.isTimeSensitive()?"TSM":"",null});
			attributes.put(AttributeValueConstants.COLLECTOR, new String[]{fullConfiguration.getCollector()?"COLLECTOR":"",null});
			attributes.put(AttributeValueConstants.CERTIFICATION, new String[]{"",null});
			attributes.put(AttributeValueConstants.CUSTOM_DATA, new String[]{customData,null});
		}else if(AttributeValueConstants.SHOP_ORDER.equals(patternType)){
			attributes=new HashMap<String,String[]>();
			ServiceReference laborChargeCodeServiceRef = new ServiceReference(
					"com.sap.me.labor", "LaborChargeCodeConfigurationService");
			LaborChargeCodeConfigurationServiceInterface laborChargeCodeConfigurationServiceInterface = RunAsServiceLocator
					.getService(laborChargeCodeServiceRef,
							LaborChargeCodeConfigurationServiceInterface.class,
							"MESYS", site, null);
			
			LaborChargeCodeBasicConfiguration basicConfiguration=laborChargeCodeConfigurationServiceInterface.findLaborChargeCodeByRef(new ObjectReference(shopOrderFullConfiguration.getLaborChargeCodeRef()));
			String customData="";
			for(AttributeValue value:shopOrderFullConfiguration.getCustomData()){
				customData=customData+value.getValue();
			}
			attributes.put(AttributeValueConstants.ORDER_TYPE, new String[]{shopOrderFullConfiguration.getShopOrderType().value(),shopOrderFullConfiguration.getShopOrderType().name()});
			attributes.put(AttributeValueConstants.LCC, new String[]{basicConfiguration.getLaborChargeCode(),null});
			attributes.put(AttributeValueConstants.PRIORITY, new String[]{shopOrderFullConfiguration.getPriority()==null?"":shopOrderFullConfiguration.getPriority().toString(),null});
			attributes.put(AttributeValueConstants.BUILT_QTY, new String[]{shopOrderFullConfiguration.getQuantityToBuild()==null?"":shopOrderFullConfiguration.getQuantityToBuild().toString(),null});
			attributes.put(AttributeValueConstants.CUSTOM_DATA, new String[]{customData,null});
			
			
		}else if(AttributeValueConstants.BOM.equals(patternType)){
			attributes=new HashMap<String,String[]>();
			
			ServiceReference BOMServiceRef = new ServiceReference(
					"com.sap.me.productdefinition", "BOMConfigurationService");
			BOMConfigurationServiceInterface bomConfigurationServiceInterface = RunAsServiceLocator
					.getService(BOMServiceRef,
							BOMConfigurationServiceInterface.class,
							"MESYS", site, null);
			BOMFullConfiguration bomFullConfiguration=bomConfigurationServiceInterface.readBOM(new ReadBOMRequest(shopOrderFullConfiguration.getBomRef()));
			String customData="";
			for(CustomValue value:bomFullConfiguration.getCustomData()){
				if(!"".equals(customData)){
					customData=customData+",";
				}
				customData=customData+value.getName()+"="+value.getValue();
			}
			String components="";
			for(BOMComponentConfiguration value:bomFullConfiguration.getBomComponentList()){
				ItemFullConfiguration fullConfiguration=itemConfigurationServiceInterface.readItem(new ObjectReference(value.getComponentContext()));
				components=customData+fullConfiguration.getItem();
			}
			attributes.put(AttributeValueConstants.COMPONENT_VALUE, new String[]{components,components});
			attributes.put(AttributeValueConstants.ERP_BOM,new String[]{ bomFullConfiguration.getErpBom(),bomFullConfiguration.getErpBom()});
			attributes.put(AttributeValueConstants.VERSION, new String[]{bomFullConfiguration.getRevision(),bomFullConfiguration.getRevision()});
			attributes.put(AttributeValueConstants.CUSTOM_DATA, new String[]{customData,customData});
			
			
		}else if(AttributeValueConstants.ROUTING.equals(patternType)){
			attributes=new HashMap<String,String[]>();			
			ServiceReference RouterServiceRef = new ServiceReference(
					"com.sap.me.productdefinition", "RouterConfigurationService");
			RouterConfigurationServiceInterface routerConfigurationServiceInterface = RunAsServiceLocator
					.getService(RouterServiceRef,
							RouterConfigurationServiceInterface.class,
							"MESYS", site, null);
			RouterFullConfiguration routerFullConfiguration=routerConfigurationServiceInterface.readRouter(new ObjectReference(shopOrderFullConfiguration.getRouterRef()));
			String customData="";
			for(CustomValue value:routerFullConfiguration.getCustomData()){
				if(!"".equals(customData)){
					customData=customData+",";
				}
				customData=customData+value.getName()+"="+value.getValue();
			}
			
			attributes.put(AttributeValueConstants.ROUTING_TYPE, new String[]{routerFullConfiguration.getRouterType().value(),routerFullConfiguration.getRouterType().name()});
			attributes.put(AttributeValueConstants.CUSTOM_DATA, new String[]{customData,customData});
			
			
		}else if(AttributeValueConstants.BOM_COMPONENT.equals(patternType)){
			attributes=new HashMap<String,String[]>();
			
			ServiceReference BOMServiceRef = new ServiceReference(
					"com.sap.me.productdefinition", "BOMConfigurationService");
			BOMConfigurationServiceInterface bomConfigurationServiceInterface = RunAsServiceLocator
					.getService(BOMServiceRef,
							BOMConfigurationServiceInterface.class,
							"MESYS", site, null);
			ServiceReference operationServiceRef = new ServiceReference(
					"com.sap.me.productdefinition", "OperationConfigurationService");
			OperationConfigurationServiceInterface operationConfigurationServiceInterface = RunAsServiceLocator
					.getService(operationServiceRef,
							OperationConfigurationServiceInterface.class,user,
							site, null);
			BOMFullConfiguration bomFullConfiguration=bomConfigurationServiceInterface.readBOM(new ReadBOMRequest(shopOrderFullConfiguration.getBomRef()));
			BOMComponentConfiguration bomComponent=null;
			
			for(BOMComponentConfiguration bomComponentConfiguration:bomFullConfiguration.getBomComponentList()){
				if(pattern.getPatternTypeValue().equals(itemConfigurationServiceInterface.readItem(new ObjectReference(bomComponentConfiguration.getComponentContext())).getItem())){
					bomComponent=bomComponentConfiguration;	
					break;
				}
			}
			if(bomComponent!=null){
				String customData="";
				for(CustomValue value:bomComponent.getCustomData()){
					if(!"".equals(customData)){
						customData=customData+",";
					}
					customData=customData+value.getName()+"="+value.getValue();
			
				}
				attributes.put(AttributeValueConstants.ASSEMBLY_SEQUENCE, new String[]{ bomComponent.getSequence().toString(),bomComponent.getSequence().toString()});
				attributes.put(AttributeValueConstants.COMPONENT_NAME, new String[]{ itemConfigurationServiceInterface.readItem(new ObjectReference(bomComponent.getComponentContext())).getItem(),itemConfigurationServiceInterface.readItem(new ObjectReference(bomComponent.getComponentContext())).getItem()});
				String operation=operationConfigurationServiceInterface.findOperationConfigurationByRef(new ObjectReference(bomComponent.getOperationRef())).getOperation();
				attributes.put(AttributeValueConstants.ASSEMBLY_OPERATION,  new String[]{operation,operation});
				attributes.put(AttributeValueConstants.COMPONENT_TYPE, new String[]{ bomComponent.getBomComponentType().value(),bomComponent.getBomComponentType().name()});
				attributes.put(AttributeValueConstants.MAX_USAGE,new String[]{bomComponent.getMaximumUsage().toString(),bomComponent.getMaximumUsage().toString()});
				attributes.put(AttributeValueConstants.COMPONENT_VERSION,new String[]{"",""});
				attributes.put(AttributeValueConstants.CUSTOM_DATA,new String[]{ customData,customData});
			}
			
		}else if(AttributeValueConstants.ROUTING_STEP.equals(patternType)){
			attributes=new HashMap<String,String[]>();
			
			ServiceReference RouterServiceRef = new ServiceReference(
					"com.sap.me.productdefinition", "RouterConfigurationService");
			RouterConfigurationServiceInterface routerConfigurationServiceInterface = RunAsServiceLocator
					.getService(RouterServiceRef,
							RouterConfigurationServiceInterface.class,
							user, site, null);
			ServiceReference workCenterServiceRef = new ServiceReference(
					"com.sap.me.plant", "WorkCenterConfigurationService");
			WorkCenterConfigurationServiceInterface workCenterConfigurationService = RunAsServiceLocator.getService(
					workCenterServiceRef,
					WorkCenterConfigurationServiceInterface.class,user, site,
					null);
			RouterFullConfiguration routerFullConfiguration=routerConfigurationServiceInterface.readRouter(new ObjectReference(shopOrderFullConfiguration.getRouterRef()));
			RouterStep selectedRouterStep=null;
			
			for(RouterStep routerStep:routerFullConfiguration.getRouterStepList()){
				if(pattern.getPatternTypeMaster().equals(routerStep.getStepId())){
					selectedRouterStep=routerStep;	
					break;
				}
			}
			if(selectedRouterStep!=null){
//				String customData="";
//				for(CustomValue value:selectedRouterStep.get){
//					if(!"".equals(customData)){
//						customData=customData+",";
//					}
//					customData=customData+value.getValue();
//			
//				}
				WorkCenterKeyData workCenterKeyData=workCenterConfigurationService.findWorkCenterKeyDataByRef(new ObjectReference(selectedRouterStep.getErpWorkCenterRef()));
				attributes.put(AttributeValueConstants.WORK_CENTER, new String[]{"",""});
				attributes.put(AttributeValueConstants.ERP_WORKCENTER,new String[]{workCenterKeyData.getWorkCenter(),workCenterKeyData.getWorkCenter()});
				attributes.put(AttributeValueConstants.ERP_SEQUENCE, new String[]{selectedRouterStep.getErpSequence(),selectedRouterStep.getErpSequence()});
				attributes.put(AttributeValueConstants.ERP_CONTROL_KEY, new String[]{selectedRouterStep.getControlKeyRef(),selectedRouterStep.getControlKeyRef()});
				attributes.put(AttributeValueConstants.CUSTOM_DATA,  new String[]{"",""});
			}
			
		}
		for(PatternSequence patternSequence: patternSequences){
			String[] attributesValue=null;
			if(attributes.get(patternSequence.getPatternAttribute())!=null){
				 attributesValue=attributes.get(patternSequence.getPatternAttribute());
				
			}
			
			if(attributesValue!=null && attributesValue.length>=2){
				String[] patternValues=patternSequence.getPatternValue().split(",");
				boolean patternValueFound=false;
				for(String patrn:patternValues){
					if(patternSequence.getPatternAttribute().equals(AttributeValueConstants.CUSTOM_DATA)){
						//custom data string example WORK_CENTER=HDLINE,MODEL_CODE=K1
						String[] customDataValues=attributesValue[0].split(",");
						for(String str:customDataValues){
							if(str.split("=").length>0 && str.split("=")[0].equals(patrn)){
								if(str.split("=").length>=2 ){
									//finding enum values for VERSION, DRIVER, CHECK DIGIT
									String attrValueTemp = getValueForCustomData(str.split("=")[0],str.split("=")[1]);
									stringToReplace=stringToReplace+(attrValueTemp!=null?attrValueTemp:str.split("=")[1]);
								}else
									stringToReplace=stringToReplace+"";
								patternValueFound=true;
								break;
							}
						}
						
					}else if(patrn.split(":").length>0){
						if(patrn.split(":")[0].equalsIgnoreCase(attributesValue[1])){
							if(patrn.split(":").length>=2)
								stringToReplace=stringToReplace+patrn.split(":")[1];
							else
								stringToReplace=stringToReplace+"";
							patternValueFound=true;
							break;
						}
					}
				}
				if(!patternValueFound){
					stringToReplace=stringToReplace+attributesValue[0];
				}
				
			}else if(attributesValue!=null && attributesValue.length>=2){
					stringToReplace=stringToReplace+attributesValue[0];
			}else if(attributesValue == null){
				// attribute is custom attribute
				stringToReplace=stringToReplace+patternSequence.getPatternValue();
			}
			
		}
		return stringToReplace;			
		
	}
	private String getValueForCustomData(String attributeName,String attributeValue){
		String data=null;
		try {
			if(VERSION.equals(attributeName)){
				data=Version.valueOf(attributeValue)==null?"":Version.valueOf(attributeValue).getVersionVal();
			}else if(CHECK_DIGIT.equals(attributeName)){
				data=CheckDigit.valueOf(attributeValue)==null?"":CheckDigit.valueOf(attributeValue).getCheckDigitVal();
			}else if(DRIVER.equals(attributeName)){					
				data=Driver.valueOf(attributeValue)==null?"":Driver.valueOf(attributeValue).getdriverCode();
			}
		} catch (IllegalArgumentException e) {
			return null;
		}
		return data;
	}
	private String geCurrentMonth(){
		Calendar calendar = Calendar.getInstance();
		int currentMonth=calendar.get(Calendar.MONTH);
		if(currentMonth<this.monthAlphabetMap.length){
			return this.monthAlphabetMap[currentMonth];
		}
		
		return "";
	}
	private String getYearAndMonthString(String format){
		Calendar calendar = Calendar.getInstance();
		int currentMonth=calendar.get(Calendar.MONTH)+1;
		
		//"yyyy-MM-dd"
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
		String dateString = simpleDateFormat.format( new Date()   );
		return dateString;
	}
	public com.atos.hook.Pattern findPattern(String patternName) {
		Connection con = null;
		String query = "select PATTERN, PATTERN_TYPE, PATTERN_TYPE_MASTER, PATTERN_TYPE_VALUE,CREATED_DATE_TIME,CREATED_BY,HANDLE from  Z_PATTERN  WHERE PATTERN = ?";
		PreparedStatement ps = null;
		com.atos.hook.Pattern pattern = null;
		try {

			con = getConnection();
			ps = con.prepareStatement(query);
			ps.setString(1, patternName);
			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				pattern = new com.atos.hook.Pattern();
				pattern.setCreatedBy(rs.getString("CREATED_BY"));
				pattern.setCreatedDate(rs.getDate("CREATED_DATE_TIME"));
				pattern.setPatternName(rs.getString("PATTERN"));
				pattern.setPatternType(rs.getString("PATTERN_TYPE"));
				pattern.setPatternTypeMaster(rs
						.getString("PATTERN_TYPE_MASTER"));
				pattern.setPatternTypeValue(rs.getString("PATTERN_TYPE_VALUE"));
				pattern.setHandle(rs.getString("HANDLE"));

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
		return pattern;

	}

	public List<PatternSequence> findPatternSequence(String patternBO) {
		Connection con = null;
		String query = "select PATTERN_BO, PATTERN_ATTRIBUTE, PATTERN_VALUE, CURRENT_VERSION,CREATED_DATE_TIME,CREATED_BY,HANDLE,SEQUENCE_NO,ATTRIBUTE_VALUE_TYPE from  Z_PATTERN_SEQUENCE  WHERE PATTERN_BO = ?";
		PreparedStatement ps = null;
		List<PatternSequence> patternSequences = new ArrayList<PatternSequence>();
		try {

			con = getConnection();
			ps = con.prepareStatement(query);
			ps.setString(1, patternBO);
			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				PatternSequence patternSequence = new PatternSequence();
				patternSequence.setCreatedBy(rs.getString("CREATED_BY"));
				patternSequence.setCreatedOn(rs.getDate("CREATED_DATE_TIME"));
				patternSequence.setPatternBo(rs.getString("PATTERN_BO"));
				patternSequence.setPatternAttribute(rs
						.getString("PATTERN_ATTRIBUTE"));
				patternSequence.setPatternValue(rs.getString("PATTERN_VALUE"));
				patternSequence.setCurrentVersion(rs
						.getString("CURRENT_VERSION"));
				patternSequence.setHandle(rs.getString("HANDLE"));
				patternSequence.setSequenceNo(rs.getInt("SEQUENCE_NO"));
				patternSequence.setPatternValueType(rs
						.getString("ATTRIBUTE_VALUE_TYPE"));
				patternSequences.add(patternSequence);

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
		return patternSequences;

	}

}
