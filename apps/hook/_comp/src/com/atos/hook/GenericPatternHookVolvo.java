package com.atos.hook;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

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
import com.sap.me.numbering.GenerateNextNumberRequest;
import com.sap.me.numbering.GenerateNextNumberResponse;
import com.sap.me.numbering.NextNumberTypeEnum;
import com.sap.me.numbering.NumberingServiceInterface;
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
public class GenericPatternHookVolvo implements
		ActivityInterface<ShopOrderReleaseHookDTO> {

	private SfcStateServiceInterface sfcStateServiceInterface;
	private ItemConfigurationServiceInterface itemConfigurationServiceInterface;
	private SplitSerializeServiceInterface serializeServiceInterface;
	private ShopOrderServiceInterface shoporderService;
	private String site = CommonMethods.getSite();
	private final SystemBase dbBase = SystemBase
			.createSystemBase("jdbc/jts/wipPool");
	private static final String HASH = "#";
	private static final String SALE_TYPE = "SALE_TYPE";
	private static final String MODEL = "MODEL";
	private static final String WHEEL_BASE_CODE = "WHEEL_BASE_CODE";
	private static final String SEPERATOR = "-";
	private String user=CommonMethods.getUserId();
	
	private Connection getConnection() {
		Connection con = null;

		con = dbBase.getDBConnection();

		return con;
	}

	@Override
	public void execute(ShopOrderReleaseHookDTO shopOrderReleaseHookDTO)
			throws Exception {
//		ServiceReference sfcServiceRef = new ServiceReference(
//				"com.sap.me.production", "SfcStateService");
//		this.sfcStateServiceInterface = RunAsServiceLocator.getService(
//				sfcServiceRef, SfcStateServiceInterface.class,user, site,
//				null);
//		ServiceReference itemServiceRef = new ServiceReference(
//				"com.sap.me.productdefinition", "ItemConfigurationService");
//		itemConfigurationServiceInterface = RunAsServiceLocator.getService(
//				itemServiceRef, ItemConfigurationServiceInterface.class,
//				"MESYS", site, null);
//		ServiceReference shoporderServiceRef = new ServiceReference(
//				"com.sap.me.demand", "ShopOrderService");
//		shoporderService = RunAsServiceLocator.getService(shoporderServiceRef,
//				ShopOrderServiceInterface.class,user, site, null);
//		ServiceReference serializeServiceRef = new ServiceReference(
//				"com.sap.me.production", "SplitSerializeService");
//		serializeServiceInterface = RunAsServiceLocator.getService(serializeServiceRef,
//				SplitSerializeServiceInterface.class,user, site, null);
//		Pattern pattern = Pattern.compile("(" + HASH + "[^" + HASH + "]*"
//				+ HASH + ")");
//
//		List<ShopOrderReleaseHookDTO.AdditionalInfoList> additionalInfoList = shopOrderReleaseHookDTO
//				.getAdditionalInfoList();
//		for (ShopOrderReleaseHookDTO.AdditionalInfoList additionalInfo : additionalInfoList) {
//			SfcBasicData basicData = sfcStateServiceInterface
//					.findSfcDataByRef(new ObjectReference(additionalInfo
//							.getSfcBO().getValue()));
//			Matcher matcher = pattern.matcher(basicData.getSfc());
//			int countHash = StringUtils.countMatches(basicData.getSfc(), HASH);
//			if (countHash % 2 != 0) {
//				throw new Exception("Invalid pattern " + HASH
//						+ " is missing in some pattern");
//			}
//			Set<String> patterns = new HashSet<String>();
//			while (matcher.find()) {
//				patterns.add(matcher.group(1));
//			}
//			String replacedSFC = basicData.getSfc();
//			for (String pat : patterns) {
//				String patternTemp = StringUtils.substringBetween(pat, HASH,
//						HASH);
//				com.atos.hook.Pattern patternObj=findPattern(patternTemp);
//				if(patternObj==null)
//					continue;
//				List<PatternSequence> patternSequences = findPatternSequence("PatternBO:"
//						+ this.site + "," + patternTemp);
//				String patternToReplace = "";				
//				patternToReplace = findPatternSequenceAttributeValues(patternObj,additionalInfo,shopOrderReleaseHookDTO,patternSequences);				
//				if (!StringUtils.isBlank(patternToReplace)) {
//					replacedSFC = StringUtils.replace(replacedSFC, pat,
//							patternToReplace);
//				}
//				
//
//			}		
//			
//			SerializeSfcDetail serializeSfcDetail=new SerializeSfcDetail();
//			serializeSfcDetail.setSfc(replacedSFC);
//			List<SerializeSfcDetail> newSfcList=new ArrayList<SerializeSfcDetail>();
//			newSfcList.add(serializeSfcDetail);
//			SerializeSfcRequest serializeSfcRequest=new SerializeSfcRequest("SFCBO:"+CommonMethods.getSite()+","+basicData.getSfc());
//			serializeSfcRequest.setNewSfcList(newSfcList);
//			serializeSfcRequest.setQuantityToSerialize(new BigDecimal(1));
//			serializeServiceInterface.serializeSfc(serializeSfcRequest);
//
//		}
		generateSequence();

	}

	public String  findPatternSequenceAttributeValues(com.atos.hook.Pattern pattern,ShopOrderReleaseHookDTO.AdditionalInfoList additionalInfoList,ShopOrderReleaseHookDTO shopOrderReleaseHookDTO,List<PatternSequence> patternSequences)throws BusinessException {
		//sorting list to arrange attributes in proper sequence
		Collections.sort(patternSequences,new PatternSeqComparator());
		String patternType=pattern.getPatternType();
		Map<String,String[]> attributes = null;
		ShopOrderFullConfiguration shopOrderFullConfiguration=this.shoporderService.readShopOrder(new ObjectReference(shopOrderReleaseHookDTO.getShopOrderBO().getValue()));
		String stringToReplace="";
		if(AttributeValueConstants.MATERIAL.equals(patternType)){
			attributes=new HashMap<String,String[]>();
			ItemFullConfiguration fullConfiguration=itemConfigurationServiceInterface.readItem(new ObjectReference(additionalInfoList.getItemBO().getValue()));
			
			String customData="";
			for(CustomValue value:fullConfiguration.getCustomData()){
				customData=customData+value.getValue();
			}		
			attributes.put(AttributeValueConstants.VERSION, new String[]{fullConfiguration.getRevision(),null});
			attributes.put(AttributeValueConstants.TYPE, new String[]{fullConfiguration.getItemType().value(),fullConfiguration.getItemType().name()});
			attributes.put(AttributeValueConstants.ORDER_TYPE, new String[]{shopOrderFullConfiguration.getShopOrderType().value(),shopOrderFullConfiguration.getShopOrderType().name()});
			attributes.put(AttributeValueConstants.LOT_SIZE,  new String[]{fullConfiguration.getLotSize().toString(),fullConfiguration.getLotSize().toString()});
			attributes.put(AttributeValueConstants.DRAWING_NAME, new String[]{fullConfiguration.getDrawingName(),fullConfiguration.getDrawingName()});
			attributes.put(AttributeValueConstants.PANEL, new String[]{fullConfiguration.getPanel()?"P":"",""});
			attributes.put(AttributeValueConstants.TSM_VAL, new String[]{fullConfiguration.isTimeSensitive()?"TSM":"",""});
			attributes.put(AttributeValueConstants.COLLECTOR, new String[]{fullConfiguration.getCollector()?"COLLECTOR":"",""});
			attributes.put(AttributeValueConstants.CERTIFICATION, new String[]{"",""});
			attributes.put(AttributeValueConstants.CUSTOM_DATA, new String[]{customData,customData});
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
				String data="";
				if(SALE_TYPE.equals(value.getAttribute())){
					data=SalesType.valueOf(value.getValue())==null?"":SalesType.valueOf(value.getValue()).getCode();
				}else if(MODEL.equals(value.getAttribute())){
					if( StringUtils.isNumeric(value.getValue())){						
						data=Model.getModelNo(Integer.valueOf(value.getValue()));
					}else{
						data=value.getValue();
					}
				}else if(WHEEL_BASE_CODE.equals(value.getAttribute())){					
					data=WheelBaseCode.valueOf(StringUtils.deleteWhitespace(value.getValue()))==null?"":WheelBaseCode.valueOf(StringUtils.deleteWhitespace(value.getValue())).getWheelBaseCode();
				}else{
					data=data+value.getValue();
				}
				if(!"".equals(customData)){
					customData=customData+SEPERATOR;
				}
				customData=customData+data;
			}
			attributes.put(AttributeValueConstants.ORDER_TYPE, new String[]{shopOrderFullConfiguration.getShopOrderType().value(),shopOrderFullConfiguration.getShopOrderType().name()});
			attributes.put(AttributeValueConstants.LCC, new String[]{basicConfiguration.getLaborChargeCode(),null});
			attributes.put(AttributeValueConstants.PRIORITY, new String[]{shopOrderFullConfiguration.getPriority()==null?"":shopOrderFullConfiguration.getPriority().toString(),null});
			attributes.put(AttributeValueConstants.BUILT_QTY, new String[]{shopOrderFullConfiguration.getQuantityToBuild()==null?"":shopOrderFullConfiguration.getQuantityToBuild().toString(),null});
			attributes.put(AttributeValueConstants.CUSTOM_DATA, new String[]{customData,customData});
			
			
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
				customData=customData+value.getValue();
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
				customData=customData+value.getValue();
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
					customData=customData+value.getValue();
			
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
							"MESYS", site, null);
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
			if(!"".equals(stringToReplace)){
				stringToReplace=stringToReplace+SEPERATOR;
			}
			if(attributesValue!=null && attributesValue.length>=2){
				String[] patternValues=patternSequence.getPatternValue().split(",");
				boolean patternValueFound=false;
				for(String patrn:patternValues){					
					if(patrn.split(":").length>0){
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
			}else{
				stringToReplace=stringToReplace+(patternSequence.getPatternValue()==null?"":patternSequence.getPatternValue());
			}
					
		}
		stringToReplace=stringToReplace+SEPERATOR+StringUtils.substringAfterLast(shopOrderFullConfiguration.getShopOrder(), "_");
		return stringToReplace;			
		
	}
	static class PatternSeqComparator implements Comparator<PatternSequence> {
        public int compare(PatternSequence o1, PatternSequence o2) {
            int result = o1.getSequenceNo() - o2.getSequenceNo();
            return result;
           
        }
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
	private void generateSequence()throws BusinessException{
		NumberingServiceInterface numberingServiceInterface =null;
		ServiceReference numberigServiceRef = new ServiceReference(
				"com.sap.me.numbering", "NumberingService");
		numberingServiceInterface = RunAsServiceLocator.getService(
				numberigServiceRef, NumberingServiceInterface.class,
				"MESYS", site, null);
		
		GenerateNextNumberRequest generateNextNumberRequest=new GenerateNextNumberRequest(NextNumberTypeEnum.INVENTORYRECEIPT, new BigDecimal(1));
		generateNextNumberRequest.setItemGroupRef("ItemGroupBO:1780,TEST");
		GenerateNextNumberResponse generateNextNumberResponse=numberingServiceInterface.generateNextNumber(generateNextNumberRequest);
		generateNextNumberResponse.getCurrentSequence();
		
	}

}
