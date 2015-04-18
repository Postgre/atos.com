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
	<sap:body title="SAP Manufacturing Execution: Assign to Chassis" id="body" height="60%" browserHistory="disabled" focusId="assignaggPlugin">
		<!-- To Localize Title use gapiI18nTransformer['KEY']-->
		
		<h:form id="assignaggPlugin">
					<f:attribute name="height" value="100%" />
				<!-- defines the form that will be updated when "delta" submits are made to the server -->
				<f:attribute name="sap-delta-id"
					value="#{sap:toClientId('assignaggPlugin')}" />
				<sap:panel id="fieldButtonPanel" width="100%" height="100%"
				isCollapsible="false" contentAreaDesign="transparent" isCollapsed="false">
					<f:attribute name="sap-delta-id"
						value="#{sap:toClientId('fieldButtonPanel')}" />
					<f:facet name="header">
										<!-- Panel title -->
										<h:outputText value="Assign to Chassis" />
									</f:facet>
					<sap:panelGrid width="100%" height="60%" cellHalign="start"
					cellValign="top" columns="1">
					<sap:panelGroup backgroundDesign="transparent" width="100%"
						halign="center" valign="middle" height="6%" padding="5 5 5 5" >
						<table>
						<tr>
						<td>	
								<fh:outputLabel value=" "></fh:outputLabel>
								<fh:outputText id="messageBar"  value="#{chassisassignbean.message}" rendered="false">	
								<fh:outputLabel value=" " ></fh:outputLabel>
								<fh:outputLabel value=" " ></fh:outputLabel>
									<h:graphicImage value="/com/atos/icons/ok.png" height="14px" width="14px"/>
									<fh:outputLabel value=" "></fh:outputLabel>
								</fh:outputText>
								<fh:outputLabel value=" "></fh:outputLabel>
								<fh:outputLabel value=" "></fh:outputLabel>
								
								<fh:outputLabel value=" "></fh:outputLabel>
								<fh:outputText id="messageBar2"  value="#{chassisassignbean.message}" rendered="false">	
								<fh:outputLabel value=" " ></fh:outputLabel>
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
						halign="center" valign="middle" height="3%" padding="5 5 5 5" >
						<table>
						<tr>
						       <td align="right">
						       <fh:outputLabel value="SFC  " style="font-size:10pt;font-weight:bold;font-family:Arial Rounded MT Bold">
						       </fh:outputLabel> 
						       <fh:outputLabel value=" "></fh:outputLabel>
								<fh:outputLabel value=" "></fh:outputLabel>
								<fh:outputLabel value="  *  " style="color: red;font-weight: bold;font-size:12pt;"></fh:outputLabel>
						       </td> 
						    <td>
						    <sap:inputText   id="SFCvalue" value="#{chassisassignbean.sfcValue}" width="140px" >
						    <f:attribute name="upperCase" value="true" />
						    </sap:inputText> 
						    </td>
						    </tr>
						    <tr></tr><tr></tr><tr></tr><tr></tr><tr></tr><tr></tr><tr></tr>
						    <tr>
							<td align="right">
								<fh:outputLabel value="Chassis No  " style="font-size:10pt;font-weight:bold;font-family:Arial Rounded MT Bold" >
								</fh:outputLabel>
								<fh:outputLabel value=" "></fh:outputLabel>
								<fh:outputLabel value=" "></fh:outputLabel>
								<fh:outputLabel value="  *  " style="color: red;font-weight: bold;font-size:12pt;"></fh:outputLabel>
							</td>
							<td>
								<sap:commandInputText id="sfcInput" 
									value="#{chassisassignbean.chassisno}"
									submitOnFieldHelp="true"
									submitOnTabout="false" submitOnChange="false"
									actionListener="#{chassisassignbean.showSfcBrowse}" disabled="#{chassisassignbean.disableSfc}">
								<f:attribute name="upperCase" value="true" />
								</sap:commandInputText>
								<sap:panelPopup id="sfcBrowsePopup"
									rendered="#{chassisassignbean.sfcBrowseRendered}"
									mode="modeless" height="400px" width="400px">
									<f:facet name="header">
										<h:outputText value="Browse for Chassis No" />
									</f:facet>
									<f:facet name="popupButtonArea">
										<h:panelGroup>
											<h:commandButton value=" OK "
												actionListener="#{chassisassignbean.rowSelectedSfc}"></h:commandButton>
											<h:commandButton value=" Cancel "
												actionListener="#{chassisassignbean.closeSfcBrowse}"></h:commandButton>
										</h:panelGroup>
									</f:facet>
									<sap:panelGrid width="100%" height="100%"
										cellpadding="10px 10px 0px 10px">
										<sap:dataTable
											value="#{chassisassignbean.sfcList}"
											var="sfcBrowseVar" id="sfcBrowseTable"
											width="100%" height="100%" columnReorderingEnabled="true">
											<sap:rowSelector
												value="#{chassisassignbean.selectSfc}"
												id="sfcSelector" selectionMode="single"
												selectionBehaviour="client"
												actionListener="#{chassisassignbean.rowSelectedSfc}"
												submitOnRowDoubleClick="true" />
											<sap:column id="sfcColumn" position="0">
												<f:facet name="header">
													<h:outputText id="sfcHeaderText"
														value="Chassis No" />
												</f:facet>
												<h:outputText value="#{sfcBrowseVar.material}"
													id="kitnumberText" />
											</sap:column>
										</sap:dataTable>
									</sap:panelGrid>
								</sap:panelPopup>
								<fh:outputLabel value="    " ></fh:outputLabel>
								<fh:outputLabel value="    "></fh:outputLabel>	
							</td>
							</tr>
						</table>
						</sap:panelGroup>	
						<sap:panelGroup backgroundDesign="transparent" width="100%"
						halign="center" valign="middle" height="3%" padding="5 5 5 5" >
						<table>
						
						<tr>
							<td>
							<!--action attribute - MethodExpression representing the application action to invoke when this component is activated by the user  -->
							<sap:commandButtonLarge id="serialize" value="Assign"
										width="90px" height="15px" action="#{chassisassignbean.assign}">
							</sap:commandButtonLarge>
							
							
							</td>
							<td><sap:outputLabel   value= "        "></sap:outputLabel></td>
							<td>
							<!--action attribute - MethodExpression representing the application action to invoke when this component is activated by the user  -->
							<sap:commandButtonLarge id="returntoPOD" value="Clear "
										width="90px" height="15px" action="#{chassisassignbean.clear}">
							</sap:commandButtonLarge>
							
							<fh:outputLabel value="    " ></fh:outputLabel>
							<fh:outputLabel value="    "></fh:outputLabel>

							</td>
							
							
						</tr>
						</table>
						</sap:panelGroup>
								
								
					</sap:panelGrid>
					</sap:panel>
						    	<sap:panelPopup id="confirmationPopup"
											rendered="#{chassisassignbean.renderConfirmationPopup}"
											mode="modeless"  height="150px" width="600px">
								<f:facet name="header">
									<h:outputText value=" Confirmation Window - Assign to Chassis " />
								</f:facet>
								<f:facet name="popupButtonArea">
									<h:panelGroup>
										<h:commandButton value="Close"
											actionListener="#{chassisassignbean.closeConfirmationPopup}"></h:commandButton>
									</h:panelGroup>
								</f:facet>
								<sap:panelGrid width="100%" height="100%"
									cellpadding="10px 10px 0px 10px" columns="1"
									cellHalign="center">
									<h:panelGroup>
										<h:outputLabel
											value=" Inventory already exist for given SFC #{chassisassignbean.sfcValue}. Do you want to Update it ? "></h:outputLabel>
									</h:panelGroup>
									<h:panelGroup>
										<sap:commandButton id="confirmationButtonYes" value=" Yes "
											width="70px"
											actionListener="#{chassisassignbean.confirmYes}">
										</sap:commandButton>
										<fh:outputLabel value=" " ></fh:outputLabel>
										<sap:commandButton id="confirmationButtonNo" value=" No "
											width="70px"
											actionListener="#{chassisassignbean.confirmNo}">
										</sap:commandButton>
									</h:panelGroup>
								</sap:panelGrid>
						</sap:panelPopup>
				</h:form>
		</sap:body>
	</sap:html>

</f:view>
