package com.atos.ws.production.postscanactivities;

import com.sap.me.common.InvalidDateValueException;
import com.sap.me.common.InvalidNumberValueException;
import com.sap.me.common.InvalidValueException;
import com.sap.me.extension.Services;
import com.sap.me.frame.domain.BusinessException;
import com.sap.me.frame.domain.LimitValidationException;
import com.sap.me.nonconformance.AssembledComponentsFoundException;
import com.sap.me.nonconformance.CreateNCRequest;
import com.sap.me.nonconformance.DuplicateComponentException;
import com.sap.me.nonconformance.InvalidComponentSFCException;
import com.sap.me.nonconformance.InvalidInputException;
import com.sap.me.nonconformance.InvalidNCCodeException;
import com.sap.me.nonconformance.InvalidSFCComponentException;
import com.sap.me.nonconformance.InvalidSFCRefDesException;
import com.sap.me.nonconformance.MissingValueException;
import com.sap.me.nonconformance.NCCodeLimitException;
import com.sap.me.nonconformance.NCCodeNotAvailableException;
import com.sap.me.nonconformance.NCComponentLimitException;
import com.sap.me.nonconformance.NCProductionServiceInterface;
import com.sap.me.nonconformance.SecondaryRequiredException;
import com.sap.me.nonconformance.UnassembledComponentException;
import com.sap.me.nonconformance.ValueNotFoundException;
import com.sap.me.production.SfcBasicData;

public class LogNC {

	public boolean logNC(SfcBasicData sfcBasisData){
		
			boolean isNcLog=false;
			NCProductionServiceInterface  ncProductionServiceInterface = (NCProductionServiceInterface) Services.getService("com.sap.me.nonconformance", "NCProductionService");
			try {
				ncProductionServiceInterface.createNC(new CreateNCRequest(sfcBasisData.getSfcRef()));
				isNcLog=true;
			} catch (LimitValidationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (DuplicateComponentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvalidComponentSFCException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvalidDateValueException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvalidInputException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvalidNCCodeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvalidNumberValueException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvalidSFCComponentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvalidSFCRefDesException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvalidValueException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (MissingValueException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NCCodeLimitException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NCCodeNotAvailableException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NCComponentLimitException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SecondaryRequiredException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnassembledComponentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ValueNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (AssembledComponentsFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (BusinessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return isNcLog;

		
	}
}
