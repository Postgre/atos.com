package com.atos.hook;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import com.atos.ordersequenceEJB.OrderSequenceDataInterface;
import com.atos.ordersequenceEJB.OrderSequenceDataBean;
import com.sap.me.common.ObjectReference;
import com.sap.me.demand.ShopOrderFullConfiguration;
import com.sap.me.demand.ShopOrderReleaseHookDTO;
import com.sap.me.demand.ShopOrderServiceInterface;
import com.sap.me.demand.ShopOrderReleaseHookDTO.AdditionalInfoList;
import com.sap.me.frame.service.CommonMethods;
import com.sap.me.production.SfcStateServiceInterface;
import com.sap.me.security.RunAsServiceLocator;
import com.visiprise.frame.configuration.ServiceReference;
import com.visiprise.frame.service.ext.ActivityInterface;

public class PostOrderReleaseHook implements ActivityInterface<ShopOrderReleaseHookDTO> {
	
	private static final long serialVersionUID = 1L;
	private ShopOrderServiceInterface shoporderService;
	private SfcStateServiceInterface sfcStateServiceInterface;
	private String site = CommonMethods.getSite();
	private String user = CommonMethods.getUserId();

	@Override
	public void execute(ShopOrderReleaseHookDTO shopOrderReleaseHookDTO)
			throws Exception {

		ServiceReference shoporderServiceRef = new ServiceReference(
				"com.sap.me.demand", "ShopOrderService");
		shoporderService = RunAsServiceLocator.getService(shoporderServiceRef,
				ShopOrderServiceInterface.class, user, site, null);
		ShopOrderFullConfiguration fullConfiguration = shoporderService
				.readShopOrder(new ObjectReference(shopOrderReleaseHookDTO
						.getShopOrderBO().getValue()));
		ServiceReference sfcServiceRef = new ServiceReference(
				"com.sap.me.production", "SfcStateService");
		this.sfcStateServiceInterface = RunAsServiceLocator.getService(
				sfcServiceRef, SfcStateServiceInterface.class, user, site,
				null);
		List<AdditionalInfoList> additionalInfoList = shopOrderReleaseHookDTO.getAdditionalInfoList();
		for (AdditionalInfoList infoList : additionalInfoList) 
		{	
			Context ctx = new InitialContext();
			OrderSequenceDataInterface ordersequenceEjb = (OrderSequenceDataInterface) ctx.lookup("ejb:/appName=atos.com/apps~ear,jarName=atos.com~apps~ejb.jar, beanName=OrderSequenceDataBean, interfaceName=com.atos.ordersequenceEJB.OrderSequenceDataInterface");
			ordersequenceEjb.insertData(shopOrderReleaseHookDTO,infoList,fullConfiguration.getPlannedStartDate());

		}
	}

}
