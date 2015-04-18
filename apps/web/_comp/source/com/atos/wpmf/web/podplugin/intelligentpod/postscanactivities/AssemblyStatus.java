package com.atos.wpmf.web.podplugin.intelligentpod.postscanactivities;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;

import com.sap.me.common.ObjectReference;
import com.sap.me.demand.SFCBOHandle;
import com.sap.me.extension.Services;
import com.sap.me.frame.domain.BusinessException;
import com.sap.me.frame.service.CommonMethods;
import com.sap.me.productdefinition.BOMComponentRequest;
import com.sap.me.productdefinition.BOMConfigurationServiceInterface;
import com.sap.me.productdefinition.BomComponentProductionConfiguration;
import com.sap.me.productdefinition.InvalidOperationException;
import com.sap.me.productdefinition.ItemConfigurationServiceInterface;
import com.sap.me.productdefinition.ItemFullConfiguration;
import com.sap.me.productdefinition.OperationBOHandle;
import com.sap.me.productdefinition.OperationKeyData;
import com.sap.me.production.AssembledComponent;
import com.sap.me.production.AssembledComponentsResponse;
import com.sap.me.production.GroupAssembledAsBuiltComponentsRequest;
import com.sap.me.production.RetrieveComponentsServiceInterface;
import com.sap.me.production.SfcBasicData;
import com.sap.me.production.SfcIdentifier;
import com.sap.me.production.SfcStateServiceInterface;
import com.sap.me.production.podclient.PodSelectionModelInterface;
import com.sap.me.wpmf.CancelProcessingException;
import com.sap.me.wpmf.MessageType;
import com.sap.me.wpmf.util.FacesUtility;
import com.sap.me.wpmf.util.MessageHandler;

public class AssemblyStatus extends com.sap.me.production.podclient.BasePodPlugin {
	private List<AssemblyDataItem> assemblyDataItems = null;
	private boolean renderAssembly=false;
	private String operationRef;
	private String sfcRef;
	private ItemConfigurationServiceInterface itemConfigurationServiceInterface=(ItemConfigurationServiceInterface) Services.getService("com.sap.me.productdefinition", "ItemConfigurationService");
	private BOMConfigurationServiceInterface bomConfigurationServiceInterface = (BOMConfigurationServiceInterface) Services.getService("com.sap.me.productdefinition", "BOMConfigurationService");
	private SfcStateServiceInterface sfcStateServiceInterface = (SfcStateServiceInterface) Services.getService("com.sap.me.production", "SfcStateService");
	private RetrieveComponentsServiceInterface componentsServiceInterface = (RetrieveComponentsServiceInterface) Services.getService("com.sap.me.production", "RetrieveComponentsService");

	public boolean isRenderAssembly() {
		return renderAssembly;
	}
	public void setRenderAssembly(boolean renderAssembly) {
		this.renderAssembly = renderAssembly;
	}
	public List<AssemblyDataItem> getAssemblyDataItems() {
		return assemblyDataItems;
	}
	public void setAssemblyDataItems(List<AssemblyDataItem> assemblyDataItems) {
		this.assemblyDataItems = assemblyDataItems;
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void beforeLoad() throws Exception {
		try {
			getPodFieldData();
			if(sfcRef!=null && operationRef!=null){
				assemblyDataItems=getAssemblyStatus(this.sfcRef,this.operationRef);
				this.renderAssembly=true;
			}
			
		}catch (BusinessException e) {

			e.printStackTrace();
			MessageHandler.clear();
    		MessageHandler.handle("Business Exception while fetching status"+e.getMessage() , null, MessageType.INFO);
		}
	    
		

	}

	public void closePlugin() {
		//Closes the current plugin, clears messages in the global area 
		MessageHandler.clear(this);
		closeCurrentPlugin();
		FacesUtility.setSessionMapValue("assemblyPlugin", null);
		this.assemblyDataItems=null;
		
	}

	
	@Override
	public String getInitialFocusId() {
		UIComponent comp = null;
		UIComponent panel = getContainer();
		panel = findComponent(FacesUtility.getFacesContext().getViewRoot(),"podSelection");
		List<String> excludedIdList = new ArrayList<String>();
		excludedIdList.add("INPUT_ID_Label");
		comp = findComponent(panel, "INPUT_ID", excludedIdList);
		return comp.getClientId(FacesContext.getCurrentInstance());
	}

	public AssemblyStatus() {
		super();
		

	}
	public List<AssemblyDataItem> getAssemblyStatus(String sfcRef,String operationRef) throws BusinessException{
		List<AssemblyDataItem> assemblyDataItems = new ArrayList<AssemblyDataItem>();
		//check if its SFC / not		
		OperationBOHandle currentOperation = new OperationBOHandle(operationRef);
		SfcBasicData basicData = sfcStateServiceInterface.findSfcDataByRef(new ObjectReference(sfcRef));
		ItemFullConfiguration itemFullConfiguration=itemConfigurationServiceInterface.readItem(new ObjectReference(basicData.getItemRef()));
		if(itemFullConfiguration.getBomRef()==null){
			return assemblyDataItems;
		}
		Collection<BomComponentProductionConfiguration> bomComponentProductionConfigurations= bomConfigurationServiceInterface.findAllBOMComponents(new BOMComponentRequest(itemFullConfiguration.getBomRef(), new BigDecimal(1)));
		
		//finding assembled components
		
		GroupAssembledAsBuiltComponentsRequest asBuiltComponentsRequest = new GroupAssembledAsBuiltComponentsRequest();		
		List<SfcIdentifier> identifiers = new ArrayList<SfcIdentifier>();
		identifiers.add(new SfcIdentifier(sfcRef));		
		asBuiltComponentsRequest.setOperationRef(operationRef);
		asBuiltComponentsRequest.setSfcList(identifiers);
		
		//finding as built components
		AssembledComponentsResponse assembledComponentsResponse=componentsServiceInterface.findAssembledAsBuiltComponents(asBuiltComponentsRequest);
		List<AssembledComponent> assembledComponentList =assembledComponentsResponse.getAssembledComponentsList();
		for(BomComponentProductionConfiguration bomComponentConfiguration:bomComponentProductionConfigurations){
			OperationBOHandle bomOperation = new OperationBOHandle(bomComponentConfiguration.getOperationRef());
			if(!currentOperation.getOperation().equals(bomOperation.getOperation())){
				continue;
			}
			AssemblyDataItem dataItem = new AssemblyDataItem();	
			//find the whether assembly components are assembled
			String assemblyStatus="NO";
			boolean assembled = false;
			//counting assembled components from BOM
			int asBuiltCompCount=0;
			for(AssembledComponent assembledComponent:assembledComponentList){
				if(bomComponentConfiguration.getComponentRef()!=null && assembledComponent.getComponentRef()!=null && bomComponentConfiguration.getOperationRef()!=null){
					if(bomComponentConfiguration.getComponentRef().equals(assembledComponent.getComponentRef())){						
							asBuiltCompCount++;											
					}
				}
			}
			if(asBuiltCompCount==bomComponentConfiguration.getQuantity().intValue()){
				assembled=true;
				assemblyStatus="YES";
			}
			dataItem.setAssemblyQuantity(asBuiltCompCount);
			dataItem.setAssembled(assembled);
			dataItem.setAssemblyStatus(assemblyStatus);
			ItemFullConfiguration fullConfiguration= itemConfigurationServiceInterface.readItem(new ObjectReference(bomComponentConfiguration.getComponentRef()));
			dataItem.setBomQuantity(bomComponentConfiguration.getQuantity()==null?0:new Integer(""+bomComponentConfiguration.getQuantity()));
			dataItem.setAssemblyName(fullConfiguration.getItem());			
			dataItem.setAssemblyType(fullConfiguration.getItemType().value());
			assemblyDataItems.add(dataItem);
		}
		return assemblyDataItems;
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

	private void getPodFieldData(){
		PodSelectionModelInterface selectionModel = getPodSelectionModel();
		if (selectionModel == null) {
			return;
		}
		List<OperationKeyData> operationList;
		try {
			operationList = selectionModel.getRouterStepsOrOperation();
			//String operationRef = null;
			if (operationList != null) {
				
				OperationBOHandle operBO = new OperationBOHandle(CommonMethods.getSite(), operationList.get(0).getOperation(), "#");
				operationRef=operationList.get(0).getRef();
			}
			// If operation is null, simply exit.
			if (operationRef == null) {
				return;
			}
			if(selectionModel.getSfcs()!=null){
				this.sfcRef=selectionModel.getSfcs().get(0).getInputId();
				SFCBOHandle sfcboHandle = new SFCBOHandle(CommonMethods.getSite(),this.sfcRef);
				this.sfcRef=sfcboHandle.getValue();
			}
			
		
		} catch (InvalidOperationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (BusinessException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
}




