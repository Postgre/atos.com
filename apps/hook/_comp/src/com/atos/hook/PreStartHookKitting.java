package com.atos.hook;
import javax.naming.Context;
import javax.naming.InitialContext;

import com.atos.kittingEJB.KittingHookInterface;
import com.sap.me.production.StartHookDTO;
import com.visiprise.frame.service.ext.ActivityInterface;

public class PreStartHookKitting implements ActivityInterface<StartHookDTO> 
{
	
	private static final long serialVersionUID = 1L;
	@Override
	public void execute(StartHookDTO dto) throws Exception 
	{

		Context ctx = new InitialContext();
		KittingHookInterface kittingHookInterface = (KittingHookInterface) ctx.lookup("ejb:/appName=atos.com/apps~ear,jarName=atos.com~apps~ejb.jar, beanName=KittingHookControllerBean, interfaceName=com.atos.kittingEJB.KittingHookInterface");
		kittingHookInterface.preStartHookKitting(dto);

		
		
	}
	
}
