package com.atos.wpmf.web.samplepodplugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.el.ELContext;
import javax.el.MethodExpression;
import javax.el.ValueExpression;
import javax.faces.component.EditableValueHolder;
import javax.faces.component.UIColumn;
import javax.faces.component.UIComponent;
import javax.faces.component.UIData;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import com.sap.me.frame.domain.BusinessException;
import com.sap.me.wpmf.BrowseSelectionModelInterface;
import com.sap.me.wpmf.InternalTableSelectionEventListener;
import com.sap.me.wpmf.Plugin;
import com.sap.me.wpmf.TableConfigurator;
import com.sap.me.wpmf.TableSelectionEvent;
import com.sap.me.wpmf.browse.CoreBrowsePluginInterface;
import com.sap.me.wpmf.util.ExceptionHandler;
import com.sap.me.wpmf.util.FacesUtility;
import com.sap.ui.faces.component.sap.UIRowSelector;
import com.sap.ui.faces.event.sap.TypedActionEvent;

public class CustomBrowsePlugin extends Plugin implements InternalTableSelectionEventListener, CoreBrowsePluginInterface {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private TableConfigurator configBean;
	
	private EditableValueHolder comp;
	
	private List<Map<String,String>> data;
	
	private  List<Map<String, String>> selections ;
	
	private String header ;
	
	private boolean okPressed = false;
	
	private String userDefinedCallbackMethod;
	
	private Plugin requestingPlugin;
	


	/**
	 * Default constructor
	 */
	public CustomBrowsePlugin() {
		super();
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.sap.me.wpmf.Plugin#getUrl()
	 */
    public String getUrl() {
        return "/com/atos/wpmf/samplepodplugin/browsedialog.jsp";
    }
    
    /*
     * (non-Javadoc)
     * @see com.sap.me.wpmf.Plugin#getInitialFocusId()
     */
    public String getInitialFocusId() {
        UIComponent panel = getContainer();
        if (panel == null) {
            return null;
        }
        UIComponent comp = panel.findComponent("browse_table");
        if (comp == null) {
            return null;
        }
        if (comp instanceof UIData) {
            return getTableFocusId((UIData)comp);
        }
        return comp.getClientId(FacesContext.getCurrentInstance());
    }
    
    /**
     * Get the localized header or title for the browse window.
     * @return
     */
	public String getHeader() {
		return header;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.sap.me.wpmf.browse.BrowsePluginInterface2#setHeader(java.lang.String)
	 */
	public void setHeader ( String hdr) {
		this.header = hdr;
	}

	
	/**
	 * Get the Table configurator associated with this browse plugin.
	 * @return  {@link TableConfigurator} the configBean
	 */
	public TableConfigurator getConfigBean() {
		return configBean;
	}




	/**
	 * Set the table configurator for this plugin.  This is an injector method.
	 * @param  configBean  {@link TableConfigurator} the configBean to set
	 */
	public void setConfigBean(TableConfigurator configBean) {
		this.configBean = configBean;
		this.configBean.addInternalTableSelectionEventListener(this);
		configBean.setDoubleClick(true);
		StringBuilder builder = new StringBuilder();
		builder.append("#{");
		String beanName = ( getBeanName() == null )  ? "defaultCoreBrowsePlugin" :  getBeanName();
		builder.append(beanName);
		builder.append(".");
		builder.append("processSelectAction");
		builder.append("}");
        configBean.setSelectionActionExpression(builder.toString());
	}


	/* (non-Javadoc)
	 * @see com.sap.me.wpmf.InternalTableSelectionEventListener#processTableSelectionEvent(com.sap.me.wpmf.TableSelectionEvent)
	 */
	public void processTableSelectionEvent(TableSelectionEvent event) {
		TableConfigurator conf =  (TableConfigurator) event.getSource();
		if ( ! configBean.equals(conf)) {
			return;
		}
	}

	/* (non-Javadoc)
	 * @see com.sap.me.wpmf.browse.BrowsePluginInterface2#setRequestingUIComponent(javax.faces.component.EditableValueHolder)
	 */
	public void setRequestingUIComponent(EditableValueHolder comp) {
		this.comp = comp;
		
	}


	/* (non-Javadoc)
	 * @see com.sap.me.wpmf.browse.BrowsePluginInterface2#getRequestingUIComponent()
	 */
	public EditableValueHolder getRequestingUIComponent() {
		return comp;
	}

	


	/*
	 * (non-Javadoc)
	 * @see com.sap.me.wpmf.browse.CoreBrowsePluginInterface#getRequestingPlugin()
	 */
	public Plugin getRequestingPlugin() {
		return requestingPlugin;
	}

	/*
	 * (non-Javadoc)
	 * @see com.sap.me.wpmf.browse.CoreBrowsePluginInterface#setRequestingPlugin(com.sap.me.wpmf.Plugin)
	 */
	public void setRequestingPlugin(Plugin requestingPlugin) {
		this.requestingPlugin = requestingPlugin;
	}

	/*
	 * (non-Javadoc)
	 * @see com.sap.me.wpmf.browse.BrowsePluginInterface2#getData()
	 */
	public List<Map<String, String>> getData() {
		return data;
	}
	
	
	/*
	 * (non-Javadoc)
	 * @see com.sap.me.wpmf.browse.BrowsePluginInterface2#setData(java.util.List)
	 */
	public void setData(List<Map<String, String>> data) {
		this.data = data;
	}
	
	
	
   /*
    * (non-Javadoc)
    * @see com.sap.me.wpmf.browse.BrowsePluginInterface2#getSelections()
    */
	public List<Map<String,String>> getSelections() {
		if ( selections == null ) {
			selections = new ArrayList<Map<String,String>>();
		}
		return selections;
	}

	
	

	/* (non-Javadoc)
	 * @see com.sap.me.wpmf.browse.BrowsePluginInterface2#setUserDefinedCallBackMethod(java.lang.String)
	 */
	public void setUserDefinedCallBackMethod(String methodExprStr) {
		this.userDefinedCallbackMethod = methodExprStr;
		
	}


	/* (non-Javadoc)
	 * @see com.sap.me.wpmf.browse.BrowsePluginInterface2#getUserDefinedCallBackMethod()
	 */
	public String getUserDefinedCallBackMethod() {
		return userDefinedCallbackMethod;
	}


	/**
     * handles the OK button action
     */
    @SuppressWarnings("unchecked")
	public void okAction() {
        if ( selections != null) selections.clear();
        getFrameworkManager().getBrowseSelectionModel().removeSelections(BrowseSelectionModelInterface.BROWSE_LIST);
        selections = (List<Map<String, String>>) configBean.getSelectedItems();
        if (selections == null || selections.size() <= 0) {
            if (configBean.isMultiSelectType()) {
                ExceptionHandler.handle(new BusinessException(12212), this);
            } else {
                ExceptionHandler.handle(new BusinessException(12950), this);
            }
            okPressed = false;
            return;
        } else {
        	getFrameworkManager().getBrowseSelectionModel().putSelections(BrowseSelectionModelInterface.BROWSE_LIST, selections);
        }
        okPressed = true;
        updateComponenField();
        closeDialog();
    }


    /**
     * handles the Cancel button action
     */
    public void cancelAction() {
    	okPressed = false;
        closeDialog();
    }
	/* (non-Javadoc)
	 * @see com.sap.me.wpmf.Plugin#processDialogClosed()
	 */
	@Override
	public void processDialogClosed() {
		super.processDialogClosed();
		//updateComponenField();
		getSelections().clear();
    	configBean.clearSelectedRows();
    	if ( data != null ) data.clear();
		// check to see if we a have a callback method to invoke
		if ( this.userDefinedCallbackMethod != null && okPressed ) {
			try {
				Class<?>[] args = new Class[0];
				MethodExpression methodExpr = FacesUtility.createMethodExpression("#{" + userDefinedCallbackMethod + "}", null, args);
				ELContext ctx = FacesUtility.getFacesContext().getELContext();
				Object[] params = new Object[0];
				methodExpr.invoke(ctx, params);
			} catch (Exception e) {
				ExceptionHandler.handle(e, requestingPlugin);
				FacesUtility.log("Failed to invoke the methodExpression: " + userDefinedCallbackMethod ,e);
			} 
		}
		//set the focus back on the browse component
		UIComponent parent =  ((UIComponent) comp).getParent();
        // here we need to check if the comp component is  on  a table  and if so, set the focus differntly.
        if ( ! ( parent instanceof UIColumn ) ) {   
        	setComponentFocus((UIComponent) comp);
        } else {
        	String clientId = ((UIComponent) comp).getClientId(FacesContext.getCurrentInstance());
            String id = ((UIComponent) comp).getId();
            Pattern pat = Pattern.compile(id);
            Matcher mat = pat.matcher(clientId);
            int curRowIdx = this.configBean.getTable().getRowIndex();
            clientId=mat.replaceAll(curRowIdx + ":" + id);
            setComponentFocus(clientId);
        }
        FacesUtility.addControlUpdate(comp);
	}
	
    /**
     * The action handler for the table selection event.  This should be primarily used by for double click action.
     * This is called after the TableSelectionEvent has been processed.
     * @param event  An ActionEvent
     */
    @SuppressWarnings("rawtypes")
	public void processSelectAction(ActionEvent event) {
        TypedActionEvent tevent = null;
        if ( event instanceof TypedActionEvent) {
            tevent = ( TypedActionEvent) event;
            Enum eventType = tevent.getEventType();
            if (eventType instanceof UIRowSelector.EventType) {
                UIRowSelector.EventType rowSelectorEventType = (UIRowSelector.EventType) eventType;
                if (rowSelectorEventType == UIRowSelector.EventType.ROW_DOUBLE_CLICK){
                    okAction();
                }
            }
        }
    }
	
    /**
     * Updates the UI component with the primary selected value(s)
     */
	private void updateComponenField() {
		if ( ! this.okPressed) return;
		if ( selections == null || selections.isEmpty()) {
			return;
		}
        StringBuilder builder = new StringBuilder();
        int i =0;
        for ( Object row: selections) {
        	if ( row == null) continue;
        	Map<String, String > curRow = (Map<String, String >) row;
        	String val = curRow.get("PRIMARY");
        	if ( i > 0 ) {
        		builder.append(",");
        	} 
        	builder.append(val);
        	i++;
        }

        ValueExpression expression = ((UIComponent) comp).getValueExpression("value");
        ELContext ctx = FacesUtility.getFacesContext().getELContext();
        expression.setValue(ctx, builder.toString());
        FacesUtility.addControlUpdate(comp);
        setLastBrowseFieldValue(builder);
	}
	
	 protected void setLastBrowseFieldValue(Object value) {
	    FacesUtility.setSessionMapValue("LastBrowseFieldValue", value);
	 }

	@Override
	public String getBrowsingMode() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
	
	 
