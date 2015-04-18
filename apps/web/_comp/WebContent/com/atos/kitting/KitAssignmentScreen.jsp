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
	<sap:body title="SAP Manufacturing Execution: Kit Assignment Maintenance" id="body" height="60%" browserHistory="disabled" focusId="kittingAssignmentForm">
		<!-- To Localize Title use gapiI18nTransformer['KEY']-->
		
		<h:form id="kittingAssignmentForm">
					<f:attribute name="height" value="100%" />
				<!-- defines the form that will be updated when "delta" submits are made to the server -->
				<f:attribute name="sap-delta-id"
					value="#{sap:toClientId('kittingAssignmentForm')}" />
				<sap:panel id="fieldButtonPanel" width="100%" height="100%"
				isCollapsible="false" contentAreaDesign="transparent" isCollapsed="false">
					<f:attribute name="sap-delta-id"
						value="#{sap:toClientId('fieldButtonPanel')}" />
					<f:facet name="header">
										<!-- Panel title -->
										<h:outputText value="KIT ASSIGNMENT MAINTENANCE" styleClass="labelStyle2" />
									</f:facet>
					<sap:panelGrid width="100%" height="100%" cellHalign="start"
					cellValign="top" columns="1">
						<sap:panelGroup backgroundDesign="transparent" width="100%"
						halign="start" valign="middle" height="6%" padding="5 5 5 5" >
						<table>
    					<tr>
						<td>
								<fh:outputLabel value=" "></fh:outputLabel>
								<fh:outputText id="messageBar"  value="#{kitAssignmentScreenControllerBean.message}" rendered="false">	
								<fh:outputLabel value=" " ></fh:outputLabel>
									<h:graphicImage value="/com/atos/icons/ok.png" height="14px" width="14px"/>
									<fh:outputLabel value=" "></fh:outputLabel>
								</fh:outputText>
								<fh:outputLabel value=" "></fh:outputLabel>
								<fh:outputLabel value=" "></fh:outputLabel>
								
								<fh:outputLabel value=" "></fh:outputLabel>
								<fh:outputText id="messageBar2"  value="#{kitAssignmentScreenControllerBean.message}" rendered="false">	
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
						halign="center" valign="middle" height="15%" padding="5 5 5 5" >
						<table>
						<tr>
						<td>
						<!-- Label * site : -->
								<fh:outputLabel value="Site " style="font-weight: bold;" styleClass="labelStyle"></fh:outputLabel>
								<fh:outputLabel value="    " ></fh:outputLabel>
								<fh:outputLabel value="    "></fh:outputLabel>
								<sap:inputText id="siteInput"
									value="#{kitAssignmentScreenControllerBean.site}" disabled="true">
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
								<fh:outputLabel value="  *  " style="color: red;font-weight: bold;font-size:12pt;"></fh:outputLabel>
						</td>
						<td>
								<fh:outputLabel value=" "></fh:outputLabel>
								<fh:outputLabel value=" "></fh:outputLabel>
								<sap:commandInputText id="productionLineInput"
									value="#{kitAssignmentScreenControllerBean.productionLine}"
									submitOnFieldHelp="true"
									submitOnTabout="false" submitOnChange="false"
									actionListener="#{kitAssignmentScreenControllerBean.showProductionLineBrowse}" disabled="#{kitAssignmentScreenControllerBean.disableProductionLine}">
								<f:attribute name="upperCase" value="true" />
								</sap:commandInputText>
								<sap:panelPopup id="rcTypeBrowsePopup"
									rendered="#{kitAssignmentScreenControllerBean.productionLineBrowseRendered}"
									mode="modeless" height="400px" width="400px">
									<f:facet name="header">
										<h:outputText value="Browse for Work Center" />
									</f:facet>
									<f:facet name="popupButtonArea">
										<h:panelGroup>
											<h:commandButton value="OK"
												actionListener="#{kitAssignmentScreenControllerBean.rowSelectedProductionLine}"></h:commandButton>
											<h:commandButton value="Cancel"
												actionListener="#{kitAssignmentScreenControllerBean.closeProductionLineBrowse}"></h:commandButton>
										</h:panelGroup>
									</f:facet>
									<sap:panelGrid width="100%" height="100%"
										cellpadding="10px 10px 0px 10px">
										<sap:dataTable
											value="#{kitAssignmentScreenControllerBean.productionLineList}"
											var="productionLineBrowseVar" id="productionLineBrowseTable"
											width="100%" height="100%" columnReorderingEnabled="true">
											<sap:rowSelector
												value="#{kitAssignmentScreenControllerBean.selectProductionLine}"
												id="productionLineRowSelector" selectionMode="single"
												selectionBehaviour="client"
												actionListener="#{kitAssignmentScreenControllerBean.rowSelectedProductionLine}"
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
								<!-- Label * Start Date:-->
								<fh:outputLabel value="Planned Start Date " style="font-weight: bold;" styleClass="labelStyle"></fh:outputLabel>
								<fh:outputLabel value=" "></fh:outputLabel>
								<fh:outputLabel value=" "></fh:outputLabel>
								<fh:outputLabel value="  *  " style="color: red;font-weight: bold;font-size:12pt;"></fh:outputLabel>
						</td>
						<td>
								<fh:outputLabel value=" "></fh:outputLabel>
								<fh:outputLabel value=" "></fh:outputLabel>
								<sap:inputDate id="schedulestartdate"
									value="#{kitAssignmentScreenControllerBean.scheduleStartDate}">
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
						halign="center" valign="left" height="5%" padding="5 5 5 5" >
						<table>
						<tr>
						<td>
							<!--action attribute - MethodExpression representing the application action to invoke when this component is activated by the user  -->
							<sap:commandButtonLarge id="readMaterial" value=" Retrieve "
										width="90px" height="15px" action="#{kitAssignmentScreenControllerBean.readMaterialData}">
							</sap:commandButtonLarge>
							
							<fh:outputLabel value="    " ></fh:outputLabel>
							<fh:outputLabel value="    "></fh:outputLabel>

							</td>
							
							<td>
							<!--action attribute - MethodExpression representing the application action to invoke when this component is activated by the user  -->
							<sap:commandButtonLarge id="clearFields" value=" Clear "
										width="90px" height="15px" action="#{kitAssignmentScreenControllerBean.clear}">
							</sap:commandButtonLarge>
							
							<fh:outputLabel value="    " ></fh:outputLabel>
							<fh:outputLabel value="    "></fh:outputLabel>

							</td>
							<td>
								<fh:outputLabel value="    " ></fh:outputLabel>
								<fh:outputLabel value="    "></fh:outputLabel>
							</td>
						</tr>
						<tr></tr><tr></tr><tr></tr><tr></tr><tr></tr><tr></tr><tr></tr>
						</table>
					</sap:panelGroup>
							<sap:panel id="displayPanel"  width="100%" height="100%"
								isCollapsible="false" contentAreaDesign="transparent"
								isCollapsed="false" >
									<f:facet name="header">
										<!-- Panel title -->
										<h:outputText value=" KITTING ASSIGNMENT BOARD " styleClass="labelStyle2"/>
									</f:facet>
									<!-- The ajaxUpdate component enables server side ajax updates of specified UI components -->
									<sap:ajaxUpdate render="#{sap:toClientId('displayPanel')}"/>
									<sap:dataTable
										value="#{kitAssignmentScreenControllerBean.materialCustomDataList}"
										first="0" var="rows" id="materialCustomDataTable" width="100%"
										height="100%" columnReorderingEnabled="true" rendered="true" browsingMode="pager" rows="5">
											
									   <sap:rowSelector
												value="#{rows.select}"
												selectionMode="single"
												id="selector123"
												selectionBehaviour="client"/>
										<sap:column>
											<f:facet name="header">
												<h:outputText value="Chassis No "/>
											</f:facet>
											<sap:outputLabel id="outputText1" value="#{rows.sfcfromPPC}"></sap:outputLabel>
										</sap:column>
										<sap:column>
											<f:facet name="header">
												<h:outputText value="Work Center "/>
											</f:facet>
											<sap:outputLabel id="outputTextWC" value="#{rows.workcenter}"></sap:outputLabel>
										</sap:column>
										<sap:column>
											<f:facet name="header">
												<h:outputText value="Priority "/>
											</f:facet>
											<sap:outputLabel id="outputText2" value="#{rows.priorityFromPPC}"></sap:outputLabel>
										</sap:column>
										<sap:column>
											<f:facet name="header">
												<h:outputText value="Planned Start Date "/>
											</f:facet>
											<sap:outputLabel id="outputText3" value="#{rows.plannedStartDateFromPPC}"></sap:outputLabel>
										</sap:column>
										<sap:column>
											<f:facet name="header">
												<h:outputText value="Kit Number"/>
											</f:facet>
											
											<sap:selectOneMenu id="kitnumber" value="#{rows.kitno}">
												<f:selectItem itemLabel="--- Select Kit Number ---" itemValue="#{null}" />
												<f:selectItems  value="#{kitAssignmentScreenControllerBean.kitNumberListSelectItem}"  />
											</sap:selectOneMenu>
											
											
											<sap:outputLabel value=" "></sap:outputLabel>
										</sap:column>
										<sap:column>
											<f:facet name="header">
												<h:outputText value=" Assign Kit "/>
											</f:facet>
											<h:commandButton value=" Assign "
												actionListener="#{kitAssignmentScreenControllerBean.assignKit}"></h:commandButton>
										</sap:column>		
										
									</sap:dataTable>						
							</sap:panel>
						</sap:panelGrid>
						</sap:panel>
						<sap:panelPopup id="confirmationPopup"
											rendered="#{kitAssignmentScreenControllerBean.renderConfirmationPopup}"
											mode="modeless"  height="150px" width="600px">
								<f:facet name="header">
									<h:outputText value=" Confirmation Window - KIT ASSIGNMENT MAINTENANCE " />
								</f:facet>
								<f:facet name="popupButtonArea">
									<h:panelGroup>
										<h:commandButton value="Close"
											actionListener="#{kitAssignmentScreenControllerBean.closeConfirmationPopup}"></h:commandButton>
									</h:panelGroup>
								</f:facet>
								<sap:panelGrid width="100%" height="100%"
									cellpadding="10px 10px 0px 10px" columns="1"
									cellHalign="center">
									<h:panelGroup>
										<h:outputLabel
											value=" SFC #{kitAssignmentScreenControllerBean.sfcValue} is already assigned with Kit. Do you want to Update it ? "></h:outputLabel>
									</h:panelGroup>
									<h:panelGroup>
										<sap:commandButton id="confirmationButtonYes" value=" Yes "
											width="70px"
											actionListener="#{kitAssignmentScreenControllerBean.confirmYes}">
										</sap:commandButton>
										<fh:outputLabel value=" " ></fh:outputLabel>
										<sap:commandButton id="confirmationButtonNo" value=" No "
											width="70px"
											actionListener="#{kitAssignmentScreenControllerBean.confirmNo}">
										</sap:commandButton>
									</h:panelGroup>
								</sap:panelGrid>
						</sap:panelPopup>
				</h:form>
		</sap:body>
	</sap:html>

</f:view>
