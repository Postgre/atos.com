package com.atos.hook;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import com.atos.hook.customDataEnums.Driver;
import com.atos.hook.customDataEnums.Version;
import com.atos.ordersequenceEJB.OrderSequenceDataBean;
import com.sap.me.common.AttributeValue;
import com.sap.me.common.BoHandleReference;
import com.sap.me.common.CustomValue;
import com.sap.me.common.ObjectReference;
import com.sap.me.demand.ShopOrderFullConfiguration;
import com.sap.me.demand.ShopOrderServiceInterface;
import com.sap.me.frame.SystemBase;
import com.sap.me.frame.domain.BusinessException;
import com.sap.me.frame.domain.InvalidInputException;
import com.sap.me.frame.service.CommonMethods;
import com.sap.me.labor.LaborChargeCodeBasicConfiguration;
import com.sap.me.labor.LaborChargeCodeConfigurationServiceInterface;
import com.sap.me.plant.WorkCenterConfigurationServiceInterface;
import com.sap.me.plant.WorkCenterKeyData;
import com.sap.me.productdefinition.BOMComponentConfiguration;
import com.sap.me.productdefinition.BOMConfigurationServiceInterface;
import com.sap.me.productdefinition.BOMFullConfiguration;
import com.sap.me.productdefinition.InvalidRouterOperationException;
import com.sap.me.productdefinition.ItemBOHandle;
import com.sap.me.productdefinition.ItemConfigurationServiceInterface;
import com.sap.me.productdefinition.ItemFullConfiguration;
import com.sap.me.productdefinition.OperationConfigurationServiceInterface;
import com.sap.me.productdefinition.ReadBOMRequest;
import com.sap.me.productdefinition.RouterConfigurationServiceInterface;
import com.sap.me.productdefinition.RouterFullConfiguration;
import com.sap.me.productdefinition.RouterStep;
import com.sap.me.productdefinition.SingleQuantityRestrictionException;
import com.sap.me.productdefinition.WholeQuantityRestrictionException;
import com.sap.me.production.InvalidQuantityForNewSfcWithLocationException;
import com.sap.me.production.InvalidQuantityValueException;
import com.sap.me.production.InvalidSfcException;
import com.sap.me.production.InvalidSfcOwnerShipException;
import com.sap.me.production.InvalidSfcRoutingException;
import com.sap.me.production.MissingSfcLocationToSerializeException;
import com.sap.me.production.NonUniqueSfcException;
import com.sap.me.production.NotEnoughQuantityException;
import com.sap.me.production.SerializeSfcDetail;
import com.sap.me.production.SerializeSfcRequest;
import com.sap.me.production.SfcBasicData;
import com.sap.me.production.SfcDisabledException;
import com.sap.me.production.SfcLocationOnHoldException;
import com.sap.me.production.SfcLocationScrappedException;
import com.sap.me.production.SfcLocationSerializedException;
import com.sap.me.production.SfcStateServiceInterface;
import com.sap.me.production.SplitSerializeServiceInterface;
import com.sap.me.production.StartHookDTO;
import com.sap.me.security.RunAsServiceLocator;
import com.sap.me.wpmf.MessageType;
import com.sap.me.wpmf.util.MessageHandler;
import com.visiprise.frame.configuration.ServiceReference;
import com.visiprise.frame.service.ext.ActivityInterface;

/**
 * @author Oksana Zubchenko
 * 
 */
public class VinNumberGenerationHook implements
		ActivityInterface<StartHookDTO> {

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
	private static final String WHEEL_BASE = "WHEEL_BASE";
	//PATTERN CREATED USING PATTERN GENERATION
	private static final String WINPATTERN = "#WINOPATTERN#";
	//STRUCTURE OF VIN : Table V2
	private static final String[] wheelBaseArr={"A","B","C","D","E","F","G","H","J","K","L","M","N","P","R","S"};
	private Map<String,Integer> alphabetNoAss = new HashMap<String, Integer>(){{
	       put("A",1); put("B",2);put("C",3);put("D",4);put("E",5);put("F",6);put("G",7);put("H",8);put("J",1);put("K",2);put("L",3);put("M",4);put("N",5);put("P",7);put("R",9);put("S",2);put("T",3);put("U",4);put("V",5);put("W",6);put("X",7);put("Y",8);put("Z",9);}};
    private Integer[] alphabetWeight = 	{8,7,6,5,4,3,2,10,0,9,8,7,6,5,4,3,2};       
	private static final Map<String, String> wheelbase = new HashMap<String, String>();
	//STRUCTURE OF VIN : Table V2
	private static final String[] monthMapping = {"A","B","C","D","E","F","G","H","J","K","L","M"};
	//STRUCTURE OF VIN : Table V3
	private static final String[] yearMapping = {"E","F","G","H","J","K","L","M","N","P","R","T","U","V","W","X","Y","1","2","3","4","5","6","7","8","9","A","B","C","D"};
	private static final int startYear =2014;
	private String checkDigit=null; 
	private Connection getConnection() {
		Connection con = null;
		

		con = dbBase.getDBConnection();

		return con;
	}

	@Override
	public void execute(StartHookDTO startHookDTO)throws Exception{
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
		try {
			//initializing wheelbase : Table V1
			int counter = 2000;
			for(int i=0;i<wheelBaseArr.length;i++){			
				String key = ""+counter+"-";
				counter = counter+300;
				key=key+counter;
				wheelbase.put(key, wheelBaseArr[i]);
			}
			//(#[^#]*#)
			Pattern pattern = Pattern.compile("(" + HASH + "[^" + HASH + "]*"
					+ HASH + ")");
			 BoHandleReference sfcBO=startHookDTO.getSfcBO();
			 SfcBasicData basicData = sfcStateServiceInterface
						.findSfcDataByRef(new ObjectReference(sfcBO.getValue()));
			 if((!StringUtils.isNumeric(basicData.getSfc()))){
				return; 
			 }
			 //continue for sfc of state NEW
			    String replacedSFC =WINPATTERN+StringUtils.leftPad(basicData.getSfc(), 6, "0");
				Matcher matcher = pattern.matcher(replacedSFC);
				//checking whether pattern is enclosed with ##
				int countHash = StringUtils.countMatches(replacedSFC, HASH);
				if (countHash % 2 != 0) {
					throw new Exception("Invalid pattern " + HASH
							+ " is missing in some pattern");
				}
				Set<String> patterns = new HashSet<String>();
				//find all patterns and adding into array
				while (matcher.find()) {
					patterns.add(matcher.group(1));
				}
				//forming string and replacing pattern with string
				for (String pat : patterns) {
					String patternTemp = StringUtils.substringBetween(pat, HASH,
							HASH);
					com.atos.hook.Pattern patternObj=findPattern(patternTemp);
					if(patternObj==null)
						continue;
					List<PatternSequence> patternSequences = findPatternSequence("PatternBO:"
							+ this.site + "," + patternTemp);
					String patternToReplace = "";				
					patternToReplace = findPatternSequenceAttributeValues(patternObj,patternSequences,basicData);	
					//Concatenating manufacturing year and month
					patternToReplace=patternToReplace+getcurrentYear()+geCurrentMonth();
					if (!StringUtils.isBlank(patternToReplace)) {
						replacedSFC = StringUtils.replace(replacedSFC, pat,
								patternToReplace);
					}
					//calculate vin if type EXPORT
					if(!"0".equals(this.checkDigit)){
						String checkDigit = calculateCheckDigit(replacedSFC);
						replacedSFC=replacedSFC.substring(0,8)+checkDigit+replacedSFC.substring(9);
					}
					if(replacedSFC.length()!=17){
						ItemBOHandle boHandle = new ItemBOHandle(basicData.getItemRef());
						throw new VinGenerationCustomException("Vin number generated is invalid , Check Custom Data Values for Material "+boHandle.getItem()+" are defined properly");
					}

				}		
				if(patterns.size()!=0){
					SerializeSfcDetail serializeSfcDetail=new SerializeSfcDetail();
					serializeSfcDetail.setSfc(replacedSFC);
					List<SerializeSfcDetail> newSfcList=new ArrayList<SerializeSfcDetail>();
					newSfcList.add(serializeSfcDetail);
					SerializeSfcRequest serializeSfcRequest=new SerializeSfcRequest(basicData.getSfcRef());
					serializeSfcRequest.setNewSfcList(newSfcList);
					serializeSfcRequest.setOperationRef(startHookDTO.getOperationBO().getValue());
					serializeSfcRequest.setResourceRef(startHookDTO.getResourceBO().getValue());
					serializeSfcRequest.setQuantityToSerialize(new BigDecimal(1));
					serializeServiceInterface.serializeSfc(serializeSfcRequest);
				}
		} catch (SfcLocationOnHoldException e) {
			setMessage(" Sfc Location On Hold  ",MessageType.ERROR);
			e.printStackTrace();
		} catch (SfcLocationSerializedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			setMessage(" Sfc Location Serialized ",MessageType.ERROR);
		} catch (SfcLocationScrappedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			setMessage(" Sfc Location Scrapped ",MessageType.ERROR);
		} catch (InvalidSfcOwnerShipException e) {
			// TODO Auto-generated catch block
			setMessage(" Invalid Sfc OwnerShip ",MessageType.ERROR);
			e.printStackTrace();
		} catch (NonUniqueSfcException e) {
			// TODO Auto-generated catch block
			setMessage(" Non Unique Sfc Exception ",MessageType.ERROR);
			e.printStackTrace();
		} catch (InvalidSfcException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			setMessage(" Invalid Sfc   ",MessageType.ERROR);
		} catch (InvalidQuantityValueException e) {
			// TODO Auto-generated catch block
			setMessage(" Invalid Quantity Value   ",MessageType.ERROR);
			e.printStackTrace();
		} catch (SfcDisabledException e) {
			// TODO Auto-generated catch block
			setMessage(" Sfc Disabled Exception  ",MessageType.ERROR);
			e.printStackTrace();
		} catch (InvalidSfcRoutingException e) {
			// TODO Auto-generated catch block
			setMessage(" Invalid Sfc Routing  ",MessageType.ERROR);
			e.printStackTrace();
		} catch (InvalidRouterOperationException e) {
			// TODO Auto-generated catch block
			setMessage(" Invalid Router Operation ",MessageType.ERROR);
			e.printStackTrace();
		} catch (NotEnoughQuantityException e) {
			// TODO Auto-generated catch block
			setMessage("Not Enough Quantity  while serializing VIN number",MessageType.ERROR);
			e.printStackTrace();
		} catch (SingleQuantityRestrictionException e) {
			// TODO Auto-generated catch block
			setMessage("Single Quantity Restriction  while serializing VIN number",MessageType.ERROR);
			e.printStackTrace();
		} catch (WholeQuantityRestrictionException e) {
			// TODO Auto-generated catch block
			setMessage("Whole Quantity Restriction while serializing VIN number",MessageType.ERROR);
			e.printStackTrace();
		} catch (InvalidQuantityForNewSfcWithLocationException e) {
			// TODO Auto-generated catch block
			setMessage("Invalid Quantity For New Sfc With Location Exception while serializing VIN number",MessageType.ERROR);
			e.printStackTrace();
		} catch (InvalidInputException e) {
			// TODO Auto-generated catch block
			setMessage("Invalid Input ",MessageType.ERROR);
			e.printStackTrace();
		} catch (MissingSfcLocationToSerializeException e) {
			// TODO Auto-generated catch block
			setMessage("Missing Sfc Location To Serialize",MessageType.ERROR);
			e.printStackTrace();
		} catch (BusinessException e) {
			// TODO Auto-generated catch block
			setMessage("Business Exception while generating vin",MessageType.ERROR);
			e.printStackTrace();
		} catch (VinGenerationCustomException e) {
			// TODO Auto-generated catch block
			setMessage(e.getMessage(),MessageType.ERROR);
			e.printStackTrace();
		} catch (Exception e) {
			setMessage(e.getMessage(),MessageType.ERROR);
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
			
		

	}
	private void setMessage(String message,MessageType messageType)throws VinGenerationCustomException{
		MessageHandler.clear();
		HashMap<String, Object> map = new HashMap<String, Object>();
		// Set success message on the POD that operation was created 
		// message.success.operation.hook  = Create Operation Hook was successfully executed and created Operation %OPERATION%
		MessageHandler.handle("Cannot generate Vin Number , "+message, map, messageType);
		throw  new VinGenerationCustomException("Cannot generate Vin Number , "+message);
	}
	public String  findPatternSequenceAttributeValues(com.atos.hook.Pattern pattern,List<PatternSequence> patternSequences,SfcBasicData sfcBasicData)throws BusinessException,VinGenerationCustomException {
		String patternType=pattern.getPatternType();
		Map<String,String[]> attributes = null;
		ShopOrderFullConfiguration shopOrderFullConfiguration=this.shoporderService.readShopOrder(new ObjectReference(sfcBasicData.getShopOrderRef()));
		String stringToReplace="";
		if(AttributeValueConstants.MATERIAL.equals(patternType)){
			attributes=new HashMap<String,String[]>();
			ItemFullConfiguration fullConfiguration=itemConfigurationServiceInterface.readItem(new ObjectReference(sfcBasicData.getItemRef()));
			
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
							user, site, null);
			
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
							user, site, null);
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
							user, site, null);
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
							user, site, null);
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
									//finding enum values for VERSION, DRIVER, MODEL_CODE,WHEEL_BASE
									String attrValueTemp = getValueForCustomData(str.split("=")[0],str.split("=")[1]);
									//if check digit is export calculate value for check digit
									stringToReplace=stringToReplace+(attrValueTemp!=null?attrValueTemp:str.split("=")[1]);
								}else
									stringToReplace=stringToReplace+"";
								
							}
						}
						patternValueFound=true;
												
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
				//logic for getting check digit from sequence table
				if(patternSequence.getPatternAttribute().equals(CHECK_DIGIT)){
					OrderSequenceDataBean bean=new OrderSequenceDataBean();
					String val = bean.findExportImportBySFC(sfcBasicData.getSfc());
					if(StringUtils.isNotBlank(val)){
						String checkDigiVal="Yes".equals(val)?"1":"0";
						this.checkDigit=checkDigiVal;
						stringToReplace=stringToReplace+(checkDigiVal);
					}else{
						this.checkDigit="0";
						stringToReplace=stringToReplace+("0");
					}				
				}else{
					// attribute is custom attribute
					stringToReplace=stringToReplace+patternSequence.getPatternValue();
				}
			}
			
		}
		return stringToReplace;			
		
	}
	private String getValueForCustomData(String attributeName,String attributeValue)throws VinGenerationCustomException{
		String data=null;
		try {
			if(VERSION.equals(attributeName)){
				if(StringUtils.isBlank(attributeValue)){
					throw new VinGenerationCustomException("Exception while generating sequence : "+VERSION+" can not be blank.");
				}
				data=Version.valueOf(attributeValue)==null?"":Version.valueOf(attributeValue).getVersionVal();
			}else if(DRIVER.equals(attributeName)){		
				if(StringUtils.isBlank(attributeValue)){
					throw new VinGenerationCustomException("Exception while generating sequence : "+DRIVER+" can not be blank.");
				}
				data=Driver.valueOf(attributeValue)==null?"":Driver.valueOf(attributeValue).getdriverCode();
			}else if(WHEEL_BASE.equals(attributeName)){
				if(StringUtils.isBlank(attributeValue)){
					throw new VinGenerationCustomException("Exception while generating sequence : "+WHEEL_BASE+" can not be blank.");
				}
				if(!StringUtils.isNumeric(attributeValue)){
					return null;
				}
				int wheelbaseValue=Integer.parseInt(attributeValue);
				for(String key:wheelbase.keySet()){
					String[] range = key.split("-");
					int val1=Integer.parseInt(range[0]);
					int val2=Integer.parseInt(range[1]);
					if(val1<=wheelbaseValue && wheelbaseValue<val2 ){
						data = wheelbase.get(key);
						break;
					}
					
				}
			}
		} catch (IllegalArgumentException e) {
			return null;
		}
		return data;
	}
	//method to get current month code from Table V2
	private String geCurrentMonth(){
		Calendar calendar = Calendar.getInstance();
		int currentMonth=calendar.get(Calendar.MONTH);
		if(currentMonth<monthMapping.length){
			return monthMapping[currentMonth];
		}
		
		return "";
	}
	//method to get current year code from Table V3
	private String getcurrentYear(){
		int currMonth=Calendar.getInstance().get(Calendar.MONTH);	
		int year = Calendar.getInstance().get(Calendar.YEAR);
		//e.g. The numeral “3” in the table V3 represents 2003 i.e. the vehicles produced from 1st August 2002 ` 31st July 2003 will carry numeral ‘3’ at the place of model year in VIN
		if(Calendar.AUGUST<=currMonth && currMonth <=Calendar.DECEMBER && (!"0".equals(this.checkDigit))){
			year = year+1;
		}
		if((year-startYear)<0){
			return "";
		}
		int index = (year-startYear)%yearMapping.length;
		return yearMapping[index];
		
	}
	public String calculateCheckDigit(String vinNo) throws VinGenerationCustomException{
		if(vinNo.length()!=17){
			throw new VinGenerationCustomException("Vin number generated is invalid , Check Custom Data Values");
		}
		int sum=0;
		for(int i=0;i<vinNo.length();i++){
			if((i+1)==9){
				//don't calculate for 9 th place as its check digit place
				continue;
			}
			//find assigned value for i th char
			String valueTemp = ""+vinNo.charAt(i);
			int assignedValue = 0;
			if(StringUtils.isNumeric(valueTemp)){
				assignedValue = Integer.parseInt(valueTemp);
			}else{
				if(valueTemp==null){
					throw new VinGenerationCustomException("Assigned value for '"+valueTemp+"' is not present");
				}
				assignedValue=alphabetNoAss.get(valueTemp);
			}			
			//get the assigned weight
			int assignedWeight = alphabetWeight[i];
			int product = assignedValue * assignedWeight;
			sum =sum+product;
			//find the remainder
			
			
		}
		int remainder = sum % 11;
		if(0<=remainder && remainder<=9){
			return ""+remainder;
		}else{
			return " ";
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

}
