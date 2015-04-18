package com.atos.ordersequenceEJB;
import java.sql.Date;
import java.util.List;

import javax.ejb.Local;

import com.sap.me.demand.ItemRouterException;
import com.sap.me.demand.OperationStatusException;
import com.sap.me.demand.RepetitiveOrderDueException;
import com.sap.me.demand.RouterStatusException;
import com.sap.me.demand.SfcCountException;
import com.sap.me.demand.ShopOrderBomNewException;
import com.sap.me.demand.ShopOrderBomStatusException;
import com.sap.me.demand.ShopOrderInputException;
import com.sap.me.demand.ShopOrderItemException;
import com.sap.me.demand.ShopOrderItemNewException;
import com.sap.me.demand.ShopOrderItemStatusException;
import com.sap.me.demand.ShopOrderItemTypeException;
import com.sap.me.demand.ShopOrderNotFoundException;
import com.sap.me.demand.ShopOrderQuantityException;
import com.sap.me.demand.ShopOrderRecursionException;
import com.sap.me.demand.ShopOrderReleaseHookDTO;
import com.sap.me.demand.ShopOrderRmaException;
import com.sap.me.demand.ShopOrderStatusException;
import com.sap.me.demand.ShopOrderUpdateException;
import com.sap.me.demand.UsedSfcException;
import com.sap.me.demand.WorkCenterPermitException;
import com.sap.me.demand.WorkCenterRequiredException;
import com.sap.me.demand.ShopOrderReleaseHookDTO.AdditionalInfoList;
import com.sap.me.frame.domain.BusinessException;
import com.sap.me.production.StartHookDTO;
import com.visiprise.globalization.util.DateTimeInterface;

@Local
public interface OrderSequenceDataInterface
 {
	public boolean readDataPreStartHook(StartHookDTO dto) throws Exception;
	
	public void updateDataPostStartHook(StartHookDTO dto) throws Exception;
	
	public List<OrderSequenceData> readCustomTableData(String workcenter, String material, java.util.Date date);
	
	public void updateDataExceptPriority( OrderSequenceData orderSequenceData, java.util.Date sqlDate,String boolString);
	
	public boolean updateData(OrderSequenceData orderSequenceData) throws BusinessException;
	
	public boolean insertAfterUpdateData(OrderSequenceData orderSequenceData,Date date,String boolString) throws BusinessException;
	
	public void insertData(ShopOrderReleaseHookDTO shopOrderReleaseHookDTO,AdditionalInfoList infoList, DateTimeInterface plannedStartDate) throws ShopOrderNotFoundException, ShopOrderItemException, ShopOrderInputException, ShopOrderStatusException, ShopOrderQuantityException, RepetitiveOrderDueException, ShopOrderRmaException, ShopOrderUpdateException, ShopOrderRecursionException, ShopOrderItemStatusException, ShopOrderItemNewException, ShopOrderItemTypeException, ShopOrderBomStatusException, ShopOrderBomNewException, WorkCenterRequiredException, WorkCenterPermitException, ItemRouterException, RouterStatusException, OperationStatusException, SfcCountException, UsedSfcException, BusinessException;
	
	public String readSpecialInstruction(String sfc)throws BusinessException;

	public String getSfcFromTable(int givenPriority,String workcenter,java.util.Date plannedStartDate);

	public String getParentOrder(ShopOrderReleaseHookDTO shopOrderReleaseHookDTO);
	

	public String findExportImportBySFC(String sfc);


}
