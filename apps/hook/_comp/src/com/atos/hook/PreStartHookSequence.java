package com.atos.hook;
import javax.naming.Context;
import javax.naming.InitialContext;

import com.atos.ordersequenceEJB.OrderSequenceDataInterface;
import com.sap.me.production.StartHookDTO;
import com.sap.me.wpmf.MessageType;
import com.sap.me.wpmf.util.MessageHandler;
import com.visiprise.frame.service.ext.ActivityInterface;
public class PreStartHookSequence implements ActivityInterface<StartHookDTO> {
	
	private static final long serialVersionUID = 1L;
	
	@Override
	public void execute(StartHookDTO dto) throws Exception
	{
		try
		{
		Context ctx = new InitialContext();
		OrderSequenceDataInterface ordersequenceEjb = (OrderSequenceDataInterface) ctx.lookup("ejb:/appName=atos.com/apps~ear,jarName=atos.com~apps~ejb.jar, beanName=OrderSequenceDataBean, interfaceName=com.atos.ordersequenceEJB.OrderSequenceDataInterface");
		ordersequenceEjb.readDataPreStartHook(dto);
		}
		catch(Exception e)
		{
			
			 e.printStackTrace();
		     MessageHandler.clear();
             MessageHandler.handle(e.getMessage() , null, MessageType.ERROR);
             throw e;
		}
	}
}
	

