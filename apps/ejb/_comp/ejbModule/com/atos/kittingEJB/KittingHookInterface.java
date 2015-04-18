package com.atos.kittingEJB;

import javax.ejb.Local;

import com.sap.me.production.PostCompleteHookDTO;
import com.sap.me.production.StartHookDTO;

@Local
public interface KittingHookInterface 

{
	public void preStartHookKitting(StartHookDTO dto) throws Exception ;
	
	public void postCompleteHookKitting(PostCompleteHookDTO dto)throws Exception ;
}
