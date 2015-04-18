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
        var myWindow;
        function customDataFieldBrowse() {
           var url = '/biopodpluginwar/com/biometdev/ordersequence/exportPage.jsf';
           myWindow = window.open(url, 'Browse', 'menubar=no,height=200,width=300,scrollbars=yes,status=no,location=no,resizable=yes,dependent=yes');  
           setTimeout('myWindow.focus()', 100);    
      }	
	</script>
	</sap:head>
	<sap:body title="KIT DEFINITION MAINTENANCE" id="body" height="60%" browserHistory="disabled" focusId="kittingDefinitionForm">
		<!-- To Localize Title use gapiI18nTransformer['KEY']-->
		
		<h:form id="kittingDefinitionForm">
					<f:attribute name="height" value="100%" />
				<!-- defines the form that will be updated when "delta" submits are made to the server -->
				<f:attribute name="sap-delta-id"
					value="#{sap:toClientId('kittingDefinitionForm')}" />
				<sap:panel id="fieldButtonPanel" width="100%" height="100%"
				isCollapsible="false" contentAreaDesign="transparent" isCollapsed="false">
					<f:attribute name="sap-delta-id"
						value="#{sap:toClientId('fieldButtonPanel')}" />
					<f:facet name="header">
										<!-- Panel title -->
										<h:outputText value="KIT DEFINITION MAINTENANCE" />
									</f:facet>
					<sap:panelGrid width="100%" height="60%" cellHalign="start"
					cellValign="top" columns="1">
						<sap:panelGroup backgroundDesign="transparent" width="100%"
						halign="start" valign="middle" height="3%" padding="5 5 5 5" >
						<table>
						<tr>
						<td>
							<fh:outputLabel value=" "> </fh:outputLabel>
								<fh:outputText id="messageBar" value="#{kitDefinitionScreenControllerBean.message}" rendered="false"/>
								<fh:outputLabel value=" "></fh:outputLabel>
								<fh:outputLabel value=" "></fh:outputLabel>
								<sap:ajaxUpdate/>
						</td>
						</tr>
						</table>								
						</sap:panelGroup>
						<sap:panelGroup backgroundDesign="transparent" 
						halign="center" valign="middle" height="5%" width="100%" >
						<div align="center">
						<table>
						<tr>
							<td align="right">
								<fh:outputLabel value="Site  " style="font-weight: bold;" styleClass="labelStyle">
								</fh:outputLabel>
								<fh:outputLabel value="    " ></fh:outputLabel>
								<fh:outputLabel value="    "></fh:outputLabel>
							</td>
							<td></td><td></td><td></td>
							<td align="left">
								<sap:inputText id="siteInput" width="142px"
									value="#{kitDefinitionScreenControllerBean.site}" disabled="true">
								</sap:inputText>
							
								<fh:outputLabel value="    " ></fh:outputLabel>
								<fh:outputLabel value="    "></fh:outputLabel>
							</td>
						</tr>
						<tr></tr><tr></tr><tr></tr><tr></tr><tr></tr>
						<tr>
							<td align="right">
								<fh:outputLabel value="Kit Number  " style="font-weight: bold;" styleClass="labelStyle">
								</fh:outputLabel>
								<fh:outputLabel value=" "></fh:outputLabel>
								<fh:outputLabel value=" "></fh:outputLabel>
							</td>
							<td></td><td></td><td></td>
							<td align="left">
								<sap:commandInputText id="kitnumberInput"
									value="#{kitDefinitionScreenControllerBean.kitnoAlt}"
									submitOnFieldHelp="true"
									submitOnTabout="false" submitOnChange="false"
									actionListener="#{kitDefinitionScreenControllerBean.showKitNumberBrowse}" disabled="#{kitDefinitionScreenControllerBean.disableKitNUMBER}">
								<f:attribute name="upperCase" value="true" />
								</sap:commandInputText>
								<sap:panelPopup id="kitnumberBrowsePopup"
									rendered="#{kitDefinitionScreenControllerBean.kitNumberBrowseRendered}"
									mode="modeless" height="400px" width="400px">
									<f:facet name="header">
										<h:outputText value="Browse for Kit Number" />
									</f:facet>
									<f:facet name="popupButtonArea">
										<h:panelGroup>
											<h:commandButton value=" OK "
												actionListener="#{kitDefinitionScreenControllerBean.rowSelectedKitNumber}"></h:commandButton>
											<h:commandButton value=" Cancel "
												actionListener="#{kitDefinitionScreenControllerBean.closeKitNumberBrowse}"></h:commandButton>
										</h:panelGroup>
									</f:facet>
									<sap:panelGrid width="100%" height="100%"
										cellpadding="10px 10px 0px 10px">
										<sap:dataTable
											value="#{kitDefinitionScreenControllerBean.kitNoList}"
											var="kitnumberBrowseVar" id="kitnumberBrowseTable"
											width="100%" height="100%" columnReorderingEnabled="true">
											<sap:rowSelector
												value="#{kitDefinitionScreenControllerBean.selectKitNumber}"
												id="kitnumberSelector" selectionMode="single"
												selectionBehaviour="client"
												actionListener="#{kitDefinitionScreenControllerBean.rowSelectedKitNumber}"
												submitOnRowDoubleClick="true" />
											<sap:column id="kitnumberColumn" position="0">
												<f:facet name="header">
													<h:outputText id="kitnumberHeaderText"
														value="Kit Number" />
												</f:facet>
												<h:outputText value="#{kitnumberBrowseVar.kitnoAlt}"
													id="kitnumberText" />
											</sap:column>
										</sap:dataTable>
									</sap:panelGrid>
								</sap:panelPopup>
						
								<fh:outputLabel value="    " ></fh:outputLabel>
								<fh:outputLabel value="    "></fh:outputLabel>	
							</td>
							</tr>
							<tr></tr>
								<tr></tr><tr></tr><tr></tr><tr></tr><tr></tr>
								<tr>
								<td align="right">
								<fh:outputLabel value="Kit Type  " style="font-weight: bold;" styleClass="labelStyle">
								</fh:outputLabel>	
								<fh:outputLabel value=" "></fh:outputLabel>
								<fh:outputLabel value=" "></fh:outputLabel>
								</td>
								<td></td><td></td><td></td>
								<td align="left">
									<sap:selectOneMenu id="kittype" value="#{kitDefinitionScreenControllerBean.kittype}">
										<f:selectItem itemLabel="---Select---" itemValue="#{null}" />
										<f:selectItems  value="#{kitDefinitionScreenControllerBean.kitTypeListSelectItem}"  />
									</sap:selectOneMenu>
						
								<fh:outputLabel value="    " ></fh:outputLabel>
								<fh:outputLabel value="    "></fh:outputLabel>						
							</td>
							</tr>
							<tr></tr>
								<tr></tr><tr></tr><tr></tr><tr></tr><tr></tr>
								<tr>
								<td align="right">
								<fh:outputLabel value="Kit Status  " style="font-weight: bold;" styleClass="labelStyle">
								</fh:outputLabel>	
								<fh:outputLabel value=" "></fh:outputLabel>
								<fh:outputLabel value=" "></fh:outputLabel>
								</td>
								<td></td><td></td><td></td>
								<td align="left">
									<sap:selectOneMenu id="kitstatus" value="#{kitDefinitionScreenControllerBean.status}">
										<f:selectItem itemLabel="---Select---" itemValue="#{null}" />
										<f:selectItems  value="#{kitDefinitionScreenControllerBean.kitStatusListSelectItem}"  />
									</sap:selectOneMenu>
						
								<fh:outputLabel value="    " ></fh:outputLabel>
								<fh:outputLabel value="    "></fh:outputLabel>						
							</td>
							</tr>
							
						</table>
						</div>
						</sap:panelGroup>
	
						<sap:panelGroup backgroundDesign="transparent" 
						halign="center" valign="middle" height="5%" width="100%" >
						<div align="center">
						<table>
						<tr>
						<td>
							<!--action attribute - MethodExpression representing the application action to invoke when this component is activated by the user  -->
							<sap:commandButtonLarge id="readMaterial" value="Retreive"
										width="90px" height="15px" action="#{kitDefinitionScreenControllerBean.readMaterialData}">
							</sap:commandButtonLarge>
							
							<fh:outputLabel value="    " ></fh:outputLabel>
							<fh:outputLabel value="    "></fh:outputLabel>

							</td>
							<td>
								<fh:outputLabel value="    " ></fh:outputLabel>
								<fh:outputLabel value="    "></fh:outputLabel>
							</td>
							<td>
							<!--action attribute - MethodExpression representing the application action to invoke when this component is activated by the user  -->
							<sap:commandButtonLarge id="saveMaterial" value="Save"
										width="90px" height="15px" actionListener="#{kitDefinitionScreenControllerBean.saveMaterialData}">
							</sap:commandButtonLarge>
							
							<fh:outputLabel value="    " ></fh:outputLabel>
							<fh:outputLabel value="    "></fh:outputLabel>

							</td>
							<td>
								<fh:outputLabel value="    " ></fh:outputLabel>
								<fh:outputLabel value="    "></fh:outputLabel>
							</td>
							<td>
							<!--action attribute - MethodExpression representing the application action to invoke when this component is activated by the user  -->
							<sap:commandButtonLarge id="clearFields" value="Clear"
										width="90px" height="15px" action="#{kitDefinitionScreenControllerBean.clear}">
							</sap:commandButtonLarge>
							
							<fh:outputLabel value="    " ></fh:outputLabel>
							<fh:outputLabel value="    "></fh:outputLabel>

							</td>
							
						</tr>
						
						</table>
						</div>
					</sap:panelGroup>
				
					</sap:panelGrid>
					</sap:panel>
				</h:form>
		</sap:body>
	</sap:html>

</f:view>
