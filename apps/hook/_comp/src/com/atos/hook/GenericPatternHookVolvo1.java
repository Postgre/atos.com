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
public class GenericPatternHookVolvo1 implements
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
	private static final String VOLVOPAT2 = "VOLVOPAT2";
	private String user=CommonMethods.getUserId();
	
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
				if(!VOLVOPAT2.equals(patternTemp)){
					continue;
				}				
				String patternToReplace = "";				
				patternToReplace = findPatternSequenceAttributeValues(patternTemp,additionalInfo,shopOrderReleaseHookDTO);				
				if (!StringUtils.isBlank(patternToReplace)) {
					replacedSFC = StringUtils.replace(replacedSFC, pat,
							patternToReplace);
				}
				

			}		
			if(!replacedSFC.equals(basicData.getSfc())){
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

	public String  findPatternSequenceAttributeValues(String pattern,ShopOrderReleaseHookDTO.AdditionalInfoList additionalInfoList,ShopOrderReleaseHookDTO shopOrderReleaseHookDTO)throws BusinessException {
		ShopOrderFullConfiguration shopOrderFullConfiguration=this.shoporderService.readShopOrder(new ObjectReference(shopOrderReleaseHookDTO.getShopOrderBO().getValue()));
		String stringToReplace="MC2"+SEPERATOR;
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
				}
				if(!"".equals(data)){
					data=data+SEPERATOR;
				}
				customData=customData+data;
		}
		
		stringToReplace=stringToReplace+customData;		
		stringToReplace=stringToReplace+StringUtils.substringAfterLast(shopOrderFullConfiguration.getShopOrder(), "_");
		return stringToReplace;			
		
	}
	

}
