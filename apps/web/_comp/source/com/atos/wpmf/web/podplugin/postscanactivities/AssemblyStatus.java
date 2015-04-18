package com.atos.wpmf.web.podplugin.postscanactivities;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.naming.Context;
import javax.naming.InitialContext;

import com.atos.ordersequenceEJB.OrderSequenceDataInterface;
import com.sap.me.common.CustomValue;
import com.sap.me.common.ObjectReference;
import com.sap.me.container.InvalidSFCException;
import com.sap.me.demand.SFCBOHandle;
import com.sap.me.extension.Services;
import com.sap.me.frame.domain.BusinessException;
import com.sap.me.frame.service.CommonMethods;
import com.sap.me.plant.ResourceKeyData;
import com.sap.me.productdefinition.BOMBOHandle;
import com.sap.me.productdefinition.BOMBasicConfiguration;
import com.sap.me.productdefinition.BOMComponentRequest;
import com.sap.me.productdefinition.BOMConfigurationServiceInterface;
import com.sap.me.productdefinition.BomComponentProductionConfiguration;
import com.sap.me.productdefinition.FindBomsByKeyFieldsRequest;
import com.sap.me.productdefinition.InvalidOperationException;
import com.sap.me.productdefinition.ItemConfigurationServiceInterface;
import com.sap.me.productdefinition.ItemFullConfiguration;
import com.sap.me.productdefinition.OperationBOHandle;
import com.sap.me.productdefinition.OperationConfigurationServiceInterface;
import com.sap.me.productdefinition.OperationKeyData;
import com.sap.me.production.AssembledComponent;
import com.sap.me.production.AssembledComponentsResponse;
import com.sap.me.production.FindSfcsAtOperationRequest;
import com.sap.me.production.GroupAssembledAsBuiltComponentsRequest;
import com.sap.me.production.RetrieveComponentsServiceInterface;
import com.sap.me.production.SfcBasicData;
import com.sap.me.production.SfcGroupStatusEnum;
import com.sap.me.production.SfcIdentifier;
import com.sap.me.production.SfcStateServiceInterface;
import com.sap.me.production.SfcStep;
import com.sap.me.production.podclient.PodSelectionModelInterface;
import com.sap.me.report.BOMHelper;
import com.sap.me.status.StatusBasicConfiguration;
import com.sap.me.status.StatusServiceInterface;
import com.sap.me.wpmf.CancelProcessingException;
import com.sap.me.wpmf.MessageType;
import com.sap.me.wpmf.util.FacesUtility;
import com.sap.me.wpmf.util.MessageHandler;

public class AssemblyStatus extends com.sap.me.production.podclient.BasePodPlugin {
	
	private List<AssemblyDataItem> assemblyDataItems = null;
	private boolean renderAssembly=false;
	private String operationRef;
	private String resourceRef;
	private String curOperitemRef;
	private String currentModel;
	private String prevOperRef;
	private String prevOperItemRef;
	private String tempPrevOperRef;
	private String specialInstruction;
	private String nextModel;
	private String sfcRef;
	private OperationConfigurationServiceInterface operationConfigurationService = (OperationConfigurationServiceInterface)Services.getService("com.sap.me.productdefinition", "OperationConfigurationService");
	private ItemConfigurationServiceInterface itemConfigurationServiceInterface=(ItemConfigurationServiceInterface) Services.getService("com.sap.me.productdefinition", "ItemConfigurationService");
	private BOMConfigurationServiceInterface bomConfigurationServiceInterface = (BOMConfigurationServiceInterface) Services.getService("com.sap.me.productdefinition", "BOMConfigurationService");
	private SfcStateServiceInterface sfcStateServiceInterface = (SfcStateServiceInterface) Services.getService("com.sap.me.production", "SfcStateService");
	private RetrieveComponentsServiceInterface componentsServiceInterface = (RetrieveComponentsServiceInterface) Services.getService("com.sap.me.production", "RetrieveComponentsService");
	private String assemblyMessage="";
	private SfcStateServiceInterface sfcStateService =(SfcStateServiceInterface) Services.getService("com.sap.me.production", "SfcStateService");
	private StatusServiceInterface statusService = (StatusServiceInterface) Services.getService("com.sap.me.status", "StatusService");
	private String operationConfirmation;
	
	public String getSpecialInstruction() {
		return specialInstruction;
	}
	public void setSpecialInstruction(String specialInstruction) {
		this.specialInstruction = specialInstruction;
	}
	public String getCurrentModel() {
		return currentModel;
	}
	public void setCurrentModel(String currentModel) {
		this.currentModel = currentModel;
	}
	public String getNextModel() {
		return nextModel;
	}
	public void setNextModel(String nextModel) {
		this.nextModel = nextModel;
	}
	public String getResourceRef() {
		return resourceRef;
	}
	public void setResourceRef(String resourceRef) {
		this.resourceRef = resourceRef;
	}
	public String getOperationConfirmation() {
		return operationConfirmation;
	}
	public void setOperationConfirmation(String operationConfirmation) {
		this.operationConfirmation = operationConfirmation;
	}
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
			MessageHandler.clear(this);
			getPodFieldData();
			setOperationConfirmation("");
			if(this.sfcRef!=null && this.operationRef !=null){
				assemblyDataItems=getAssemblyStatus(this.sfcRef,this.operationRef);
				setCurrentAndNextModel(this.sfcRef, this.operationRef);
				setSpecialInstructions(this.sfcRef);
				this.renderAssembly=true;
			}
			
			
		}catch (BusinessException e) {
			// TODO Auto-generated catch block
			// TODO Auto-generated catch block
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
    public void setSpecialInstructions(String sfcRef) throws Exception
	{
		String sfc =sfcStateServiceInterface.findSfcDataByRef(new ObjectReference(sfcRef)).getSfc();
		Context ctx = new InitialContext();
		OrderSequenceDataInterface ordersequenceEjb = (OrderSequenceDataInterface) ctx.lookup("ejb:/appName=atos.com/apps~ear,jarName=atos.com~apps~ejb.jar, beanName=OrderSequenceDataBean, interfaceName=com.atos.ordersequenceEJB.OrderSequenceDataInterface");
		if (ordersequenceEjb.readSpecialInstruction(sfc) != null)
			this.specialInstruction=ordersequenceEjb.readSpecialInstruction(sfc);
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
		String bomRef = itemFullConfiguration.getBomRef();
		BOMBOHandle bomboHandle = new BOMBOHandle(bomRef);
		String bomName = bomboHandle.getBOM();
		FindBomsByKeyFieldsRequest bomsByKeyFieldsRequest=new FindBomsByKeyFieldsRequest();
		bomsByKeyFieldsRequest.setBom(bomName);
		Collection<BOMBasicConfiguration>  bomBasicConfigurations = bomConfigurationServiceInterface.findBomsByKeyFields(bomsByKeyFieldsRequest);
		for(BOMBasicConfiguration comp:bomBasicConfigurations){
			if(comp.isCurrentRevision()){
				bomRef = comp.getRef();
				break;
			}
		}
		Collection<BomComponentProductionConfiguration> bomComponentProductionConfigurations= bomConfigurationServiceInterface.findAllBOMComponents(new BOMComponentRequest(bomRef, new BigDecimal(1)));
		
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
			}else if(asBuiltCompCount<bomComponentConfiguration.getQuantity().intValue()&&asBuiltCompCount>0){
				assembled=false;
				assemblyStatus="PARTIALLYCOMPLTED";
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
			this.operationConfirmation = "";
	        UIComponent comp = null;
			UIComponent panel = getContainer();
			panel = findComponent(FacesUtility.getFacesContext().getViewRoot(),"reportsPanel2");
			List<String> excludedIdList = new ArrayList<String>();
		//	excludedIdList.add("INPUT_ID_Label");
			excludedIdList.add("CustomLabel1");
			comp = findComponent(panel, "OperationConfrm1", excludedIdList);			
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
	public void postScanOperation(ActionEvent event){
		MessageHandler.clear(this);
		MessageHandler.clear();
		if(this.operationConfirmation.equals("")){
			MessageHandler.clear();
    		MessageHandler.handle("Barcode code can not be null" , null, MessageType.ERROR,this);
			clearOprSearch();
			 return;
		}
		PodSelectionModelInterface selectionModel = getPodSelectionModel();
		if (selectionModel == null) {
			
			return;
		}
		List<OperationKeyData> operationList;
		try {
			operationList = selectionModel.getRouterStepsOrOperation();
			//fetch the operation selected in POD
			if (operationList != null) {
				
				OperationBOHandle operBO = new OperationBOHandle(CommonMethods.getSite(), operationList.get(0).getOperation(), "#");
				operationRef=operBO.getValue();
			}
			// If operation is null, simply exit.
			if (operationRef == null) {
				MessageHandler.clear();
				MessageHandler.clear(this);
	    		MessageHandler.handle("Operation can not be null" , null, MessageType.ERROR,this);
				clearOprSearch();
				 return;
			}
			//fetch the resource selected in POD
			ResourceKeyData resourceKeydata = selectionModel.getResolvedResource();
			if (resourceKeydata != null) {
				resourceRef = resourceKeydata.getRef();
			}
			if (resourceRef == null) {
				MessageHandler.clear();
				MessageHandler.clear(this);				
	    		MessageHandler.handle("Resource can not be null" , null, MessageType.ERROR,this);
				clearOprSearch();
				return;
			}
			if(selectionModel.getSfcs()!=null){
				this.sfcRef=selectionModel.getSfcs().get(0).getInputId();
				SFCBOHandle sfcboHandle = new SFCBOHandle(CommonMethods.getSite(),this.sfcRef);
				this.sfcRef=sfcboHandle.getValue();
			}
			if (this.sfcRef == null) {
				 clearOprSearch();
				MessageHandler.clear();
				MessageHandler.clear(this);
	    		MessageHandler.handle("SFC can not be null" , null, MessageType.ERROR,this);
				return;
			}
			
		
		} catch (InvalidOperationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (BusinessException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		//get operation and barcode		
		try {
			//check if its SFC / not
			SFCBOHandle sfcref = new SFCBOHandle(CommonMethods.getSite(),operationConfirmation);
			SfcBasicData sfcBasicData = null;
			try {
				sfcBasicData=sfcStateService.findSfcDataByRef(new ObjectReference(sfcref.getValue()));
			} catch (InvalidSFCException invalidSFCException) {
				// TODO: handle exception
				invalidSFCException.printStackTrace();
			}catch(BusinessException businessException){
				businessException.printStackTrace();
			}			
			StatusBasicConfiguration basicConfiguration=null;
			if(sfcBasicData !=null){
				basicConfiguration=statusService.findStatusByRef(new ObjectReference(sfcBasicData.getStatusRef()));
			}
			if(sfcBasicData ==null || basicConfiguration.getStatus().equals("405")){
				new PVTCheck().postScanOperations(this.operationRef,this.resourceRef,this.operationConfirmation,this.sfcRef);
				this.assemblyDataItems = getAssemblyStatus(this.sfcRef, this.operationRef);
			}else{
				throw new PODCustomException("Scanned input is invalid.");			
			}
			//finding assembly status
			boolean isAllCompAssembled=true;
			for(AssemblyDataItem assemblyDataItem:this.assemblyDataItems){
				if(assemblyDataItem.getAssemblyStatus().equals("NO")|| assemblyDataItem.getAssemblyStatus().equals("PARTIALLYCOMPLTED")){
					isAllCompAssembled = false;
					break;
				}
			}
			if(isAllCompAssembled){
				MessageHandler.clear();
				MessageHandler.clear(this);
	    		MessageHandler.handle("All Components are Assembled Successfully" , null, MessageType.INFO);
				
			}else{
				MessageHandler.clear();
				MessageHandler.clear(this);
	    		MessageHandler.handle("Assembled Successfully" , null, MessageType.INFO,this);
			}
			
			
			
		}catch (BusinessException e) {
			// TODO Auto-generated catch block
			MessageHandler.clear();
			e.printStackTrace();
			MessageHandler.clear(this);
    		MessageHandler.handle(e.getMessage() , null, MessageType.ERROR,this);
		}catch(PODCustomException customException){
			// TODO Auto-generated catch block
			customException.printStackTrace();
			MessageHandler.clear();
			MessageHandler.clear(this);
    		MessageHandler.handle(customException.getMessage() , null, MessageType.ERROR,this);
		}
		
       clearOprSearch();
       return;
		
		
	}
	public void setCurrentAndNextModel(String sfcRef,String operationRef) throws Exception {
		ObjectReference sfcObjRef = new ObjectReference();
		sfcObjRef.setRef(sfcRef);
		Collection<SfcStep> sfcStepCollection = sfcStateServiceInterface.findCurrentRouterSfcStepsBySfcRef(sfcObjRef);
		List<SfcStep> sfcStepList = new ArrayList<SfcStep>(sfcStepCollection);
		Collections.sort(sfcStepList,new Comparator<SfcStep>()    {
			public int compare(SfcStep a, SfcStep b) {
				return a.getStepSequence().compareTo(b.getStepSequence());
			}
		});
		for (SfcStep sfcStep:sfcStepList) {
				String tempOperRef =sfcStep.getOperationRef();
				ObjectReference tempOperObjRef = new ObjectReference();
				tempOperObjRef.setRef(tempOperRef);
				ObjectReference resolveObjOperRef = new ObjectReference();
				resolveObjOperRef=operationConfigurationService.resolveCurrentRevision(tempOperObjRef);
				String resolveObjOper =resolveObjOperRef.getRef();
				if (resolveObjOper.equals(operationRef)) {
					prevOperRef = tempPrevOperRef;
					break;
				}
				tempPrevOperRef = tempOperRef;
		}
		if (prevOperRef != null) {
			FindSfcsAtOperationRequest sfcsAtOperationRequest=new FindSfcsAtOperationRequest(prevOperRef);	
			List<SfcGroupStatusEnum> includedStatusCollection = new ArrayList<SfcGroupStatusEnum>();
			includedStatusCollection.add(SfcGroupStatusEnum.ACTIVE);
			sfcsAtOperationRequest.setIncludedStatusCollection(includedStatusCollection);
			Collection<SfcBasicData> prevOperSfcData=sfcStateServiceInterface.findSfcsAtOperation(sfcsAtOperationRequest);	
			List<SfcBasicData> prevOpersfcStepList = new ArrayList<SfcBasicData>(prevOperSfcData);
			if (prevOpersfcStepList.size() > 0) {
				prevOperItemRef =prevOpersfcStepList.get(0).getItemRef();
			
			ObjectReference objRef = new ObjectReference();
			objRef.setRef(prevOperItemRef);

			ItemFullConfiguration fullConfiguration = itemConfigurationServiceInterface
					.readItem(objRef);
			if (fullConfiguration != null) {
				List<CustomValue> itemCustomValues = fullConfiguration
						.getCustomData();
				for (CustomValue customValue : itemCustomValues) {
					if ("MODEL".equals(customValue.getName())) {
						setNextModel(customValue.getValue().toString());
						break;
					}
						
				

				}
			}
			}
			
		}
		SfcBasicData curOperSfcData =sfcStateServiceInterface.findSfcDataByRef(sfcObjRef);
		curOperitemRef=curOperSfcData.getItemRef();
		ObjectReference curobjRef = new ObjectReference();
		curobjRef.setRef(curOperitemRef);
		ItemFullConfiguration curfullConfiguration = itemConfigurationServiceInterface
		.readItem(curobjRef);
		if (curfullConfiguration != null) {
			List<CustomValue> itemCustomValues = curfullConfiguration
					.getCustomData();
			for (CustomValue customValue : itemCustomValues) {
				if ("MODEL".equals(customValue.getName())) {
					setCurrentModel(customValue.getValue().toString());
					break;
				}
					
			

			}
		}

	}
	public String getCurOperitemRef() {
		return curOperitemRef;
	}
	public void setCurOperitemRef(String curOperitemRef) {
		this.curOperitemRef = curOperitemRef;
	}
	public String getPrevOperRef() {
		return prevOperRef;
	}
	public void setPrevOperRef(String prevOperRef) {
		this.prevOperRef = prevOperRef;
	}
	public String getPrevOperItemRef() {
		return prevOperItemRef;
	}
	public void setPrevOperItemRef(String prevOperItemRef) {
		this.prevOperItemRef = prevOperItemRef;
	}
	public String getTempPrevOperRef() {
		return tempPrevOperRef;
	}
	public void setTempPrevOperRef(String tempPrevOperRef) {
		this.tempPrevOperRef = tempPrevOperRef;
	}
	public void clearOprSearch(){

		this.operationConfirmation = "";
        UIComponent comp = null;
		UIComponent panel = getContainer();
		panel = findComponent(FacesUtility.getFacesContext().getViewRoot(),"reportsPanel2");
		List<String> excludedIdList = new ArrayList<String>();
	//	excludedIdList.add("INPUT_ID_Label");
		excludedIdList.add("CustomLabel1");
		comp = findComponent(panel, "OperationConfrm", excludedIdList);
		setComponentFocus(comp.getClientId(FacesContext.getCurrentInstance()));
	
	}
}




