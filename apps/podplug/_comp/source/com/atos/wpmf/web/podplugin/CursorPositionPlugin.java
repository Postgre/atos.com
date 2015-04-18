
package com.atos.wpmf.web.podplugin;

import java.util.ArrayList;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.context.FacesContextFactory;
import com.sap.me.wpmf.CancelProcessingException;
import com.sap.me.wpmf.util.FacesUtility;

public class CursorPositionPlugin extends com.sap.me.production.podclient.BasePodPlugin {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public void execute() throws Exception {
		  UIComponent comp = null;
			UIComponent panel = getContainer();
			panel = findComponent(FacesUtility.getFacesContext().getViewRoot(),"podSelection");
			List<String> excludedIdList = new ArrayList<String>();
		//	excludedIdList.add("INPUT_ID_Label");
			excludedIdList.add("CustomLabel1");
			comp = findComponent(panel, "OperationConfrm", excludedIdList);
			setComponentFocus(comp.getClientId(FacesContext.getCurrentInstance()));
			
			
		
	}
	public void processPluginLoaded() {
		super.processPluginLoaded();
		try {
			// FacesUtility.addScriptCommand("alert(document.getElementById('templateForm:reservedArea:podSelectView:OperationConfrm').value);");
			// FacesUtility.addScriptCommand("alert(document.getElementById('templateForm:reservedArea:examplePlugin:reportFrame'));");
			//executePluginButtonActivityWithFeedback("Z_CURSOR_POS");
			//FacesUtility.addScriptCommand("setTimeout(function(){document.getElementById('templateForm:reservedArea:podSelectView:OperationConfrm').focus()},5000); ");
			UIComponent comp = null;
			UIComponent panel = getContainer();
			panel = findComponent(FacesUtility.getFacesContext().getViewRoot(),"podSelection");
			List<String> excludedIdList = new ArrayList<String>();
		//	excludedIdList.add("INPUT_ID_Label");
			excludedIdList.add("CustomLabel1");
			comp = findComponent(panel, "OperationConfrm", excludedIdList);
			setComponentFocus(comp.getClientId(FacesContext.getCurrentInstance()));
			
		} catch (CancelProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (AbortProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}


//public class CursorPositionPlugin extends com.sap.me.production.podclient.BasePodPlugin {
//
//	/**
//	 * 
//	 */
//	private static final long serialVersionUID = 1L;
//
//	public void beforeLoad() throws Exception {
//		savePluginInSession();
//
//	}
//
//	private CursorPositionPlugin findPluginInSession() {
//		CursorPositionPlugin sessionPlugin = (CursorPositionPlugin) FacesUtility.getSessionMapValue("customBrowsePlugin");
//		return sessionPlugin;
//	}
//
//	private void savePluginInSession() {
//		CursorPositionPlugin sessionPlugin = findPluginInSession();
//		if (this != sessionPlugin) {
//			FacesUtility.setSessionMapValue("customBrowsePlugin", this);
//		}
//	}
//
//	@Override
//	public String getInitialFocusId() {
//		UIComponent comp = null;
//		UIComponent panel = getContainer();
//		panel = findComponent(FacesUtility.getFacesContext().getViewRoot(),"podSelection");
//		List<String> excludedIdList = new ArrayList<String>();
//		excludedIdList.add("INPUT_ID_Label");
//		comp = findComponent(panel, "INPUT_ID", excludedIdList);
//		return comp.getClientId(FacesContext.getCurrentInstance());
//	}
//
//	public CursorPositionPlugin() {
//		super();
//		UIComponent comp = null;
//		UIComponent panel = getContainer();
//		panel = findComponent(FacesUtility.getFacesContext().getViewRoot(),"podSelection");
//		List<String> excludedIdList = new ArrayList<String>();
//	//	excludedIdList.add("INPUT_ID_Label");
//		excludedIdList.add("CustomLabel1");
//		comp = findComponent(panel, "OperationConfrm", excludedIdList);
//		setComponentFocus(comp.getClientId(FacesContext.getCurrentInstance()));
//		
//
//	}
//	public void execute() throws Exception {
//		  UIComponent comp = null;
//			UIComponent panel = getContainer();
//			panel = findComponent(FacesUtility.getFacesContext().getViewRoot(),"podSelection");
//			List<String> excludedIdList = new ArrayList<String>();
//		//	excludedIdList.add("INPUT_ID_Label");
//			excludedIdList.add("CustomLabel1");
//			comp = findComponent(panel, "OperationConfrm", excludedIdList);
//			setComponentFocus(comp.getClientId(FacesContext.getCurrentInstance()));
//			
//			
//		
//	}
//
//}
