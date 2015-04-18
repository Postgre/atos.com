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
			.mystyle2 
			{
			border: none;
			background-color: #D0E8E7;
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
	<sap:body title="SAP Manufacturing Execution: Order Sequence Board" id="body" height="100%" browserHistory="disabled" focusId="orderSequenceDataForm">
		<!-- To Localize Title use gapiI18nTransformer['KEY']-->
		
		<h:form id="orderSequenceDataForm">
					<f:attribute name="height" value="100%" />
				<!-- defines the form that will be updated when "delta" submits are made to the server -->
				<f:attribute name="sap-delta-id"
					value="#{sap:toClientId('orderSequenceDataForm')}" />
				<sap:panel id="fieldButtonPanel" width="100%" height="100%"
				isCollapsible="false" contentAreaDesign="transparent" isCollapsed="false">
					<f:attribute name="sap-delta-id"
						value="#{sap:toClientId('fieldButtonPanel')}" />
					<f:facet name="header">
										<!-- Panel title -->
										<h:outputText value="ORDER BOARD" />
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
								action="#{orderSequenceDataBean.readMaterialData}">
								<sap:ajaxUpdate />
							</h:commandLink>
							<fh:outputLabel value=" " ></fh:outputLabel>
							<h:graphicImage value="/com/atos/icons/remove.png"
								height="15px" width="15px" />
							<fh:outputLabel value=" "></fh:outputLabel>
							<h:commandLink value=" Clear" style="font-weight: bold;" styleClass="labelStyle" id="commandLinkclear"
								action="#{orderSequenceDataBean.clear}" />
								</div>
						</sap:panelGroup>
						<sap:panelGroup backgroundDesign="transparent" width="100%"
						halign="start" valign="middle" height="6%" padding="5 5 5 5" >
						<table>
						<tr>
						<td>	
								<fh:outputLabel value=" "></fh:outputLabel>
								<fh:outputText id="messageBar"  value="#{orderSequenceDataBean.message}" rendered="false">	
								<fh:outputLabel value=" " ></fh:outputLabel>
									<h:graphicImage value="/com/atos/icons/ok.png" height="14px" width="14px"/>
									<fh:outputLabel value=" "></fh:outputLabel>
								</fh:outputText>
								<fh:outputLabel value=" "></fh:outputLabel>
								<fh:outputLabel value=" "></fh:outputLabel>
								
								<fh:outputLabel value=" "></fh:outputLabel>
								<fh:outputText id="messageBar2"  value="#{orderSequenceDataBean.message}" rendered="false">	
								<fh:outputLabel value=" " ></fh:outputLabel>
									<h:graphicImage value="/com/atos/icons/exclamation.png" height="12px" width="12px"/>
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
						halign="start" valign="center" height="12%" padding="5 5 5 5" >
						<table>
						<tr>
						<td>
						<!-- Label * site : -->
								<fh:outputLabel value="Site " style="font-weight: bold;" styleClass="labelStyle"></fh:outputLabel>
								<fh:outputLabel value="    " ></fh:outputLabel>
								<fh:outputLabel value="    "></fh:outputLabel>
								<sap:inputText id="siteInput"
									value="#{orderSequenceDataBean.site}" disabled="true">
								</sap:inputText>
						</td>
						<td>
								<fh:outputLabel value="    " ></fh:outputLabel>
								<fh:outputLabel value="    "></fh:outputLabel>
						</td>
						<td>
								<!-- Label * Work Center -->
								<fh:outputLabel value="Work Center" style="font-weight: bold;" styleClass="labelStyle"></fh:outputLabel>
								<fh:outputLabel value=" "></fh:outputLabel>
								<fh:outputLabel value=" "></fh:outputLabel>
								<sap:commandInputText id="productionLineInput"
									value="#{orderSequenceDataBean.productionLine}"
									submitOnFieldHelp="true"
									submitOnTabout="false" submitOnChange="false"
									actionListener="#{orderSequenceDataBean.showProductionLineBrowse}" disabled="#{orderSequenceDataBean.disableProductionLine}">
								<f:attribute name="upperCase" value="true" />
								</sap:commandInputText>
								<sap:panelPopup id="rcTypeBrowsePopup"
									rendered="#{orderSequenceDataBean.productionLineBrowseRendered}"
									mode="modeless" height="400px" width="400px">
									<f:facet name="header">
										<h:outputText value="Browse for Work Center" />
									</f:facet>
									<f:facet name="popupButtonArea">
										<h:panelGroup>
											<h:commandButton value="OK"
												actionListener="#{orderSequenceDataBean.rowSelectedProductionLine}"></h:commandButton>
											<h:commandButton value="Cancel"
												actionListener="#{orderSequenceDataBean.closeProductionLineBrowse}"></h:commandButton>
										</h:panelGroup>
									</f:facet>
									<sap:panelGrid width="100%" height="100%"
										cellpadding="10px 10px 0px 10px">
										<sap:dataTable
											value="#{orderSequenceDataBean.productionLineList}"
											var="productionLineBrowseVar" id="productionLineBrowseTable"
											width="100%" height="100%" columnReorderingEnabled="true">
											<sap:rowSelector
												value="#{orderSequenceDataBean.selectProductionLine}"
												id="productionLineRowSelector" selectionMode="single"
												selectionBehaviour="client"
												actionListener="#{orderSequenceDataBean.rowSelectedProductionLine}"
												submitOnRowDoubleClick="true" />
											<sap:column id="rcTypeColumn" position="0">
												<f:facet name="header">
													<h:outputText id="productionLineHeaderText"
														value="Work Center" />
												</f:facet>
												<h:outputText value="#{productionLineBrowseVar.material}"
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
						<td>
								<!-- Label * material : -->
								<fh:outputLabel value="Material " style="font-weight: bold;" styleClass="labelStyle"></fh:outputLabel>
								<fh:outputLabel value=" "></fh:outputLabel>
								<fh:outputLabel value=" "></fh:outputLabel>
								<sap:commandInputText id="materialInput"
									value="#{orderSequenceDataBean.material}"
									submitOnFieldHelp="true"
									submitOnTabout="false" submitOnChange="false"
									actionListener="#{orderSequenceDataBean.showMaterialBrowse}">
								<f:attribute name="upperCase" value="true" />
								</sap:commandInputText>
								<sap:panelPopup id="materialBrowsePopup"
									rendered="#{orderSequenceDataBean.materialBrowseRendered}"
									mode="modeless" height="400px" width="400px">
									<f:facet name="header">
										<h:outputText value="Browse for Material" />
									</f:facet>
									<f:facet name="popupButtonArea">
										<h:panelGroup>
											<h:commandButton value="OK"
												actionListener="#{orderSequenceDataBean.rowSelectedMaterial}"></h:commandButton>
											<h:commandButton value="Cancel"
												actionListener="#{orderSequenceDataBean.closeMaterialBrowse}"></h:commandButton>
										</h:panelGroup>
									</f:facet>
									<sap:panelGrid width="100%" height="100%"
										cellpadding="10px 10px 0px 10px">
										<sap:dataTable value="#{orderSequenceDataBean.materialList}"
											var="materialBrowseVar" id="materialBrowseTable" width="100%"
											height="100%" columnReorderingEnabled="true">
											<sap:rowSelector value="#{orderSequenceDataBean.selectMaterial}"
												id="rowSelectorMaterial" selectionMode="single"
												selectionBehaviour="client"
												actionListener="#{orderSequenceDataBean.rowSelectedMaterial}"
												submitOnRowDoubleClick="true" />
											<sap:column id="materialColumn" position="0">
												<f:facet name="header">
													<h:outputText id="materialHeaderText" value="Material" />
												</f:facet>
												<h:outputText value="#{materialBrowseVar.material}"
													id="materialText" />
											</sap:column>
										</sap:dataTable>
									</sap:panelGrid>
								</sap:panelPopup>					
						</td>
						<td>
								<fh:outputLabel value="    " ></fh:outputLabel>
								<fh:outputLabel value="    "></fh:outputLabel>		
						</td>
						<td>							
								<!-- Label * Start Date:-->
								<fh:outputLabel value="Start Date " style="font-weight: bold;" styleClass="labelStyle"></fh:outputLabel>
								<fh:outputLabel value=" "></fh:outputLabel>
								<fh:outputLabel value=" "></fh:outputLabel>
								<sap:inputDate id="schedulestartdate"
									value="#{orderSequenceDataBean.scheduleStartDate}">
								</sap:inputDate>
							</td>
							<td>
								<fh:outputLabel value="    " ></fh:outputLabel>
								<fh:outputLabel value="    "></fh:outputLabel>						
							</td>	
						</tr>
						</table>													
					</sap:panelGroup>
					<sap:panelGroup backgroundDesign="transparent" width="100%"
						halign="start" valign="left" height="10%" padding="5 5 5 5" >
						<table>
						<tr>
						<td>
						<!-- Label * Schedule Start Date:-->
								<fh:outputLabel value="Planned Start Date " style="font-weight:bold;" styleClass="labelStyle"></fh:outputLabel>
								<fh:outputLabel value="    " ></fh:outputLabel>
								<fh:outputLabel value="    "></fh:outputLabel>
								<fh:outputLabel value="* " style="color: red;font-weight: bold;"></fh:outputLabel>
								<sap:inputDate id="plannedDate"
									value="#{orderSequenceDataBean.scheduleStartDate2}">
								</sap:inputDate>
						</td>
						<td>
								<fh:outputLabel value="    " ></fh:outputLabel>
								<fh:outputLabel value="    "></fh:outputLabel>
						</td>
							<td>
							<!--action attribute - MethodExpression representing the application action to invoke when this component is activated by the user  -->
							<sap:commandButtonLarge id="SaveSequence" value="Save"
										width="90px" height="15px" actionListener="#{orderSequenceDataBean.updateOrderSequence}">
								<fh:outputLabel value=" " ></fh:outputLabel>
							<h:graphicImage value="/com/atos/icons/save.png"
								height="2%" width="2%" />
							<fh:outputLabel value=" "></fh:outputLabel>		
							</sap:commandButtonLarge>
							
							<fh:outputLabel value="    " ></fh:outputLabel>
							<fh:outputLabel value="    "></fh:outputLabel>

							</td>
							<td>
								<fh:outputLabel value="    " ></fh:outputLabel>
								<fh:outputLabel value="    "></fh:outputLabel>
							</td>
						</tr>
						</table>
					</sap:panelGroup>
					
					
					<sap:panel id="displayPanel"  width="100%" height="100%"
								isCollapsible="false" contentAreaDesign="transparent"
								isCollapsed="false">
									<f:facet name="header">
										<!-- Panel title -->
										<h:outputText value=" SEQUENCE ORDERS " />
									</f:facet>
									<!-- The ajaxUpdate component enables server side ajax updates of specified UI components -->
									<sap:ajaxUpdate render="#{sap:toClientId('displayPanel')}"/>
									<sap:dataTable
										value="#{orderSequenceDataBean.materialCustomDataList}"
										first="0" var="rows" id="sfc_sequencing" width="100%"
										height="100%" columnReorderingEnabled="true" rendered="true" browsingMode="pager" rows="5">
											
									   <sap:rowSelector
												value="#{rows.select}"
												selectionMode="multiTouch"
												id="selector123"
												selectionBehaviour="client"/>
										<sap:column>
											<f:facet name="header">
												<h:outputText value="Material"/>
											</f:facet>
											<sap:outputLabel id="outputText1" value="#{rows.itembo}"></sap:outputLabel>
										</sap:column>
										<sap:column>
											<f:facet name="header">
												<h:outputText value="Chassis No"/>
											</f:facet>
											<sap:outputLabel id="outputText2" value="#{rows.sfcbo}"></sap:outputLabel>
										</sap:column>
										<sap:column>
											<f:facet name="header">
												<h:outputText value="Order"/>
											</f:facet>
											<sap:outputLabel id="outputText3" value="#{rows.orderbo}"></sap:outputLabel>
										</sap:column>
										<sap:column>
											<f:facet name="header">
												<h:outputText value="Workcenter"/>
											</f:facet>
											<sap:outputLabel id="outputText5" value="#{rows.workcenterbo}"></sap:outputLabel>
										</sap:column>
								
										<sap:column>
											<f:facet name="header">
												<h:outputText value="Priority"/>
											</f:facet>
											<sap:commandInputText id="priority1"
												value="#{rows.priority1}"
												submitOnFieldHelp="false" submitOnTabout="false"
												submitOnChange="false"
												disabled="#{rows.disable  or (orderSequenceDataBean.releasable)}" 
												width="100px" maxlength="5">
												
											</sap:commandInputText>
											<sap:outputLabel value=" "></sap:outputLabel>
										</sap:column>
									
										<sap:column width="200px">
											<f:facet name="header">
												<h:outputText value="Special Instruction"/>
											</f:facet>
												<sap:commandInputText id="outputText6"
												value="#{rows.special_int}"
												submitOnFieldHelp="false" submitOnTabout="false"
												submitOnChange="false"
												width="150px">
											</sap:commandInputText>
										</sap:column>
										<sap:column width="100px">
											<f:facet name="header">
												<h:outputText value="Export" style="font-weight: bold;" styleClass="headerStyle" />
											</f:facet>
											<h:selectBooleanCheckbox id="selectBooleanCheckbox2"
												value="#{rows.export_import2}" disabled="#{rows.disable}" />
										</sap:column>

										<sap:column width="150px">
											<f:facet name="header">
												<h:outputText value="Planned Start Date" />
											</f:facet>
									
										<sap:outputLabel value="#{rows.startDate}"></sap:outputLabel>
										
										</sap:column>
									
									</sap:dataTable>						
							
							</sap:panel>
						</sap:panelGrid>
						</sap:panel>
				</h:form>
		</sap:body>
	</sap:html>

</f:view>
