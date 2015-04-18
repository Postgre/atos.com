<%@page pageEncoding="UTF-8"%>
<%@ page language="java"%>
<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core"%>
<%@ taglib prefix="fh" uri="http://java.sun.com/jsf/html"%>
<%@ taglib prefix="h" uri="http://java.sap.com/jsf/html"%>
<%@ taglib prefix="sap" uri="http://java.sap.com/jsf/html/extended"%>
<f:view>
	<sap:html>

	<sap:head>
		<style>
			.labelStyle {
				font-size: 12px;
				font-family: "Arial";
			}
			
			.headerStyle
			{
				font-size: 12px;
			}
			.labelStyle2
			{
			border: none;
			background-color: #D0E8E7;
			font-size: 20px;
			font-family: "Arial Rounded MT Bold";
			}
				
			.mystyle2 
			{
			border: none;
			background-color: #E7E8F5;
			font-size: 18px;
			font-family: "Arial Rounded MT Bold";
			}
			.commandLinkstyle1 
			{
			border: none;
			background-color: #E7E8F5;
			font-size: 18px;
			font-family: "Arial Rounded MT Bold";
			}
		</style>
	   <script>
	   UCF_DomUtil.attachEvent(window, 'load', loadLightSpeed);
       UCF_DomUtil.attachEvent(window, 'unload', windowUnload);

       function loadLightSpeed() {
           window.oLS = new UCF_LS();
          }

       function unloadLightSpeed() {
           if (window.oLS) {
               window.oLS.destroy();
           }
       }
        function windowUnload() {
           if (window.parent == undefined) {
            //   window.alert('windowLoaded: window.parent is undefined');
               return;
           }
           var btnControl = document.getElementById('materialCustomDataForm:windowCloseButton');
           if (btnControl) {
               btnControl.click();
           } else {
             //  window.alert('windowLoaded: hidden button not found');
           }

           unloadLightSpeed();
       }
	</script>
	</sap:head>
	<sap:body title="SAP Manufacturing Execution: Assembly Data Screen" id="body" height="100%" browserHistory="disabled" focusId="assemblyDataScreenForm">
		<!-- To Localize Title use gapiI18nTransformer['KEY']-->
		
		<h:form id="assemblyDataScreenForm">
					<f:attribute name="height" value="100%" />
				<!-- defines the form that will be updated when "delta" submits are made to the server -->
				<f:attribute name="sap-delta-id"
					value="#{sap:toClientId('assemblyDataScreenForm')}" />
				<sap:panel id="fieldButtonPanel" width="100%" height="100%"
				isCollapsible="false" contentAreaDesign="transparent" isCollapsed="false">
					<f:attribute name="sap-delta-id"
						value="#{sap:toClientId('fieldButtonPanel')}" />
					<f:facet name="header">
										<!-- Panel title -->
										<h:outputText value="Assembly Data Order Board" styleClass="labelStyle2" />
									</f:facet>
					<sap:panelGrid width="100%" height="100%" cellHalign="start"
					cellValign="top" columns="1">
					
					<sap:panelGroup  width="100%" halign="start" valign="middle" height="4%">
						<div class="mystyle2 ">
							<fh:outputLabel value=" " ></fh:outputLabel>
							<h:graphicImage value="/com/atos/icons/fetch.png"
								height="15px" width="15px" />
							<fh:outputLabel value=" "></fh:outputLabel>
							<h:commandLink value=" Retrieve "style="font-weight: bold;" styleClass="labelStyle" id="commandLinkRetrieve"
								action="#{assemblyDataScreenController.readMaterialData}">
								<sap:ajaxUpdate />
							</h:commandLink>
							<fh:outputLabel value=" " ></fh:outputLabel>
							<h:graphicImage value="/com/atos/icons/save.png"
								height="15px" width="15px" />
							<fh:outputLabel value=" "></fh:outputLabel>
							<h:commandLink value=" Save "style="font-weight: bold;" styleClass="labelStyle" id="commandLinkSave"
								action="#{assemblyDataScreenController.saveMaterialData}">
								<sap:ajaxUpdate />
							</h:commandLink>
							<fh:outputLabel value=" " ></fh:outputLabel>
							<h:graphicImage value="/com/atos/icons/remove.png"
								height="15px" width="15px" />
							<fh:outputLabel value=" "></fh:outputLabel>
							<h:commandLink value=" Clear" style="font-weight: bold;" styleClass="labelStyle" id="commandLinkclear"
								action="#{assemblyDataScreenController.clear}" />
								</div>
						</sap:panelGroup>
						<sap:panelGroup backgroundDesign="transparent" width="100%"
						halign="start" valign="middle" height="6%" padding="5 5 5 5" >
						<table>
						<tr>
						<td>	
								<fh:outputLabel value=" "></fh:outputLabel>
								<fh:outputText id="messageBar"  value="#{assemblyDataScreenController.message}" rendered="false">	
								<fh:outputLabel value=" " ></fh:outputLabel>
									<h:graphicImage value="/com/atos/icons/ok.png" height="14px" width="14px"/>
									<fh:outputLabel value=" "></fh:outputLabel>
								</fh:outputText>
								<fh:outputLabel value=" "></fh:outputLabel>
								<fh:outputLabel value=" "></fh:outputLabel>
								
								<fh:outputLabel value=" "></fh:outputLabel>
								<fh:outputText id="messageBar2"  value="#{assemblyDataScreenController.message}" rendered="false">	
								<fh:outputLabel value=" " ></fh:outputLabel>
									<h:graphicImage value="/com/atos/icons/exclamation.png" height="14px" width="14px"/>
									<fh:outputLabel value=" "></fh:outputLabel>
								</fh:outputText>
								<fh:outputLabel value=" "></fh:outputLabel>
								<fh:outputLabel value=" "></fh:outputLabel>
								
								<sap:ajaxUpdate/>
						</td>
						</tr>
						</table>
						</sap:panelGroup>
						
						<sap:panelGroup backgroundDesign="transparent" width="100%"
						halign="center" valign="left" height="5%" padding="5 5 5 5" >
						<table>
						<tr>
						<td>
						<!-- Label * site : -->
								<fh:outputLabel value="SEARCH BY   " style="font-weight: bold;" styleClass="labelStyle"></fh:outputLabel>
								<fh:outputLabel value=" "></fh:outputLabel>
								<fh:outputLabel value=" "></fh:outputLabel>
								<fh:outputLabel value="  *  " style="color: red;font-weight: bold;"></fh:outputLabel>
								
						</td>
						<td>
								<sap:commandOneRadio value="#{assemblyDataScreenController.searchBy}" valueChangeListener="#{assemblyDataScreenController.radioButtonValueChange}" immediate="true">
   									<f:selectItems  value="#{assemblyDataScreenController.radioList}"/>
								</sap:commandOneRadio>						
						</td>					
						</tr>
						<tr></tr><tr></tr><tr></tr><tr></tr><tr></tr><tr></tr><tr></tr><tr></tr>
						
						<tr>
						
						<td>
								<!-- Label * Work Center -->
								<fh:outputLabel value=" SFC / WORKCENTER " style="font-weight: bold;" styleClass="labelStyle"></fh:outputLabel>
								<fh:outputLabel value=" "></fh:outputLabel>
								<fh:outputLabel value=" "></fh:outputLabel>
								<fh:outputLabel value="  *  " style="color: red;font-weight: bold;"></fh:outputLabel>
						</td>
						<td>
								<fh:outputLabel value=" "></fh:outputLabel>
								<fh:outputLabel value=" "></fh:outputLabel>
								<sap:commandInputText id="validationInput"
									value="#{assemblyDataScreenController.validationField}"
									submitOnFieldHelp="true"
									submitOnTabout="false" submitOnChange="false"
									actionListener="#{assemblyDataScreenController.showValidationBrowse}">
								<f:attribute name="upperCase" value="true" />
								</sap:commandInputText>
								
								
								<sap:panelPopup id="sfcBrowsePopup"
									rendered="#{assemblyDataScreenController.sfcBrowseRendered}"
									mode="modeless" height="400px" width="400px">
									<f:facet name="header">
										<h:outputText value="Browse for SFC" />
									</f:facet>
									<f:facet name="popupButtonArea">
										<h:panelGroup>
											<h:commandButton value="OK"
												actionListener="#{assemblyDataScreenController.rowSelectedSfc}"></h:commandButton>
											<h:commandButton value="Cancel"
												actionListener="#{assemblyDataScreenController.closeSfcBrowse}"></h:commandButton>
										</h:panelGroup>
									</f:facet>
									<sap:panelGrid width="100%" height="100%"
										cellpadding="10px 10px 0px 10px">
										<sap:dataTable
											value="#{assemblyDataScreenController.sfcList}"
											var="validationFieldBrowse" id="sfcBrowseTable"
											width="100%" height="100%" columnReorderingEnabled="true">
											<sap:rowSelector
												value="#{assemblyDataScreenController.selectValidationField}"
												id="sfcRowSelector" selectionMode="single"
												selectionBehaviour="client"
												actionListener="#{assemblyDataScreenController.rowSelectedSfc}"
												submitOnRowDoubleClick="true" />
											<sap:column id="sfcColumn" position="0">
												<f:facet name="header">
													<h:outputText id="sfcHeaderText"
														value="SFC" />
												</f:facet>
												<h:outputText value="#{validationFieldBrowse.material}"
													id="sfcText" />
											</sap:column>
										</sap:dataTable>
									</sap:panelGrid>
								</sap:panelPopup>
								
								
								<sap:panelPopup id="rcTypeBrowsePopup"
									rendered="#{assemblyDataScreenController.productionLineBrowseRendered}"
									mode="modeless" height="400px" width="400px">
									<f:facet name="header">
										<h:outputText value="Browse for Work Center" />
									</f:facet>
									<f:facet name="popupButtonArea">
										<h:panelGroup>
											<h:commandButton value="OK"
												actionListener="#{assemblyDataScreenController.rowSelectedProductionLine}"></h:commandButton>
											<h:commandButton value="Cancel"
												actionListener="#{assemblyDataScreenController.closeProductionLineBrowse}"></h:commandButton>
										</h:panelGroup>
									</f:facet>
									<sap:panelGrid width="100%" height="100%"
										cellpadding="10px 10px 0px 10px">
										<sap:dataTable
											value="#{assemblyDataScreenController.productionLineList}"
											var="validationFieldBrowse" id="productionLineBrowseTable"
											width="100%" height="100%" columnReorderingEnabled="true">
											<sap:rowSelector
												value="#{assemblyDataScreenController.selectValidationField}"
												id="productionLineRowSelector" selectionMode="single"
												selectionBehaviour="client"
												actionListener="#{assemblyDataScreenController.rowSelectedProductionLine}"
												submitOnRowDoubleClick="true" />
											<sap:column id="rcTypeColumn" position="0">
												<f:facet name="header">
													<h:outputText id="productionLineHeaderText"
														value="Work Center" />
												</f:facet>
												<h:outputText value="#{validationFieldBrowse.material}"
													id="rcTypeText" />
											</sap:column>
										</sap:dataTable>
									</sap:panelGrid>
								</sap:panelPopup>
						</td>
						<td>
								<fh:outputLabel value="    " ></fh:outputLabel>
								<fh:outputLabel value="    "></fh:outputLabel>						
						</td>
						</tr>
						<tr></tr><tr></tr><tr></tr><tr></tr><tr></tr><tr></tr><tr></tr><tr></tr>
						</table>
						</sap:panelGroup>
						
							<sap:panel id="displayPanel"  width="100%" height="100%"
								isCollapsible="false" contentAreaDesign="transparent"
								isCollapsed="false" >
								<f:facet name="header">
										<!-- Panel title -->
										<h:outputText value="Assembly Data Maintenance Board" styleClass="labelStyle2" />
									</f:facet>
									<sap:ajaxUpdate render="#{sap:toClientId('displayPanel')}"/>
								<div class="commandLinkstyle1">
								<table>
								<tr>
								<td>
									<fh:outputLabel value="    "></fh:outputLabel>
									<h:commandLink id="InsertNew" value="Insert New"
									action="#{assemblyDataScreenController.insertNewRow}">
									<sap:ajaxUpdate />
								</h:commandLink>
								</td>
								<td>
									<fh:outputLabel value="    "></fh:outputLabel>
									<fh:outputLabel value="    "></fh:outputLabel>
								</td>
								<td>
								<fh:outputLabel value="    "></fh:outputLabel>
								<h:commandLink id="DeleteSelected" value="Remove Selected" 
									action="#{assemblyDataScreenController.deletSelectedRow}">
									<sap:ajaxUpdate />
								</h:commandLink>
								</td>
								</tr>
								</table>
								</div>
								<table>
								<tr>
								<td>
								<fh:outputLabel value="    " ></fh:outputLabel>
								<fh:outputLabel value="    "></fh:outputLabel>						
								</td>
								</tr>
								<tr>
								<td>
								<fh:outputLabel value="    " ></fh:outputLabel>
								<fh:outputLabel value="    "></fh:outputLabel>						
								</td>
								</tr>
								</table>

									<sap:dataTable
										value="#{assemblyDataScreenController.materialCustomDataList}"
										first="0" var="rows" id="materialCustomDataTable" width="100%"
										height="100%" columnReorderingEnabled="true" rendered="true" browsingMode="pager" rows="5">
											
									   <sap:rowSelector
												value="#{rows.select}"
												selectionMode="multiTouch"
												id="selector123"
												selectionBehaviour="client"/>
												
											<sap:column width="325px">
											<f:facet name="header">
												<h:outputText value="Host Operation"/>
											</f:facet>
												<sap:selectOneMenu id="host_operation" value ="#{rows.host_operation}" >
												<f:selectItem itemLabel="-Select Host Operation-" itemValue="#{null}" />
												<f:selectItems  value="#{assemblyDataScreenController.operationsSelectItem}"  />
											</sap:selectOneMenu>
											<sap:outputLabel value=" "></sap:outputLabel>
										</sap:column>
										
										<sap:column width="325px">
											<f:facet name="header">
												<h:outputText value="Slave Operation"/>
											</f:facet>
												<sap:selectOneMenu id="slave_operation" value ="#{rows.slave_operation}">
												<f:selectItem itemLabel="-Select Slave Operation-" itemValue="#{null}" />
												<f:selectItems  value="#{assemblyDataScreenController.operationsSelectItem}"  />
											</sap:selectOneMenu>
											<sap:outputLabel value=" "></sap:outputLabel>
										</sap:column>
										
									<sap:column width="325px">
											<f:facet name="header">
												<h:outputText value="Validation Element"/>
											</f:facet>
											
											<sap:selectOneMenu id="validate_element" value ="#{rows.validate_element}">
												<f:selectItem itemLabel="-Select Validate Element-" itemValue="#{null}" />
												<f:selectItems  value="#{assemblyDataScreenController.validateElementSelectItem}"  />
											</sap:selectOneMenu>
											<sap:outputLabel value=" "></sap:outputLabel>
										</sap:column>
										
										<sap:column width="325px">
											<f:facet name="header">
												<h:outputText value="Material"/>
											</f:facet>
											<sap:outputLabel id="material" value="#{rows.materialItem}"></sap:outputLabel>
										</sap:column>
									
							</sap:dataTable>						
							</sap:panel>
						</sap:panelGrid>
						</sap:panel>
							
				</h:form>
		</sap:body>
	</sap:html>

</f:view>
