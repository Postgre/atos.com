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
	<sap:body title="Allocation to Chasis" id="body" height="60%" browserHistory="disabled" focusId="einForm">
		<!-- To Localize Title use gapiI18nTransformer['KEY']-->
		
		<h:form id="einForm">
					<f:attribute name="height" value="100%" />
				<!-- defines the form that will be updated when "delta" submits are made to the server -->
				<f:attribute name="sap-delta-id"
					value="#{sap:toClientId('einForm')}" />
				<sap:panel id="fieldButtonPanel" width="100%" height="100%"
				isCollapsible="false" contentAreaDesign="transparent" isCollapsed="false">
					<f:attribute name="sap-delta-id"
						value="#{sap:toClientId('fieldButtonPanel')}" />
					<f:facet name="header">
										<!-- Panel title -->
										<h:outputText value="Allocation to Chasis" />
									</f:facet>
					<sap:panelGrid width="100%" height="60%" cellHalign="start"
					cellValign="top" columns="1">
					<sap:panelGroup backgroundDesign="transparent" width="100%"
						halign="start" valign="middle" height="3%" padding="5 5 5 5" >
						<table>
						<tr>
						<td>
							<fh:outputLabel value=" "> </fh:outputLabel>
								<fh:outputText id="messageBar" value="#{einSerializeBean.message}" rendered="false"/>
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
						
							<td>
								<fh:outputLabel value="Chasis No  " style="font-weight: bold;" styleClass="labelStyle">
								</fh:outputLabel>
							</td>
							<td>
								<sap:commandInputText id="sfcInput" 
									value="#{einSerializeBean.sfcField}"
									submitOnFieldHelp="true"
									submitOnTabout="false" submitOnChange="false"
									actionListener="#{einSerializeBean.showSfcBrowse}" disabled="#{einSerializeBean.disableSfc}">
								<f:attribute name="upperCase" value="true" />
								</sap:commandInputText>
								<sap:panelPopup id="sfcBrowsePopup"
									rendered="#{einSerializeBean.sfcBrowseRendered}"
									mode="modeless" height="400px" width="400px">
									<f:facet name="header">
										<h:outputText value="Browse for Chasis No" />
									</f:facet>
									<f:facet name="popupButtonArea">
										<h:panelGroup>
											<h:commandButton value=" OK "
												actionListener="#{einSerializeBean.rowSelectedSfc}"></h:commandButton>
											<h:commandButton value=" Cancel "
												actionListener="#{einSerializeBean.closeSfcBrowse}"></h:commandButton>
										</h:panelGroup>
									</f:facet>
									<sap:panelGrid width="100%" height="100%"
										cellpadding="10px 10px 0px 10px">
										<sap:dataTable
											value="#{einSerializeBean.sfcList}"
											var="sfcBrowseVar" id="sfcBrowseTable"
											width="100%" height="100%" columnReorderingEnabled="true">
											<sap:rowSelector
												value="#{einSerializeBean.selectSfc}"
												id="sfcSelector" selectionMode="single"
												selectionBehaviour="client"
												actionListener="#{einSerializeBean.rowSelectedSfc}"
												submitOnRowDoubleClick="true" />
											<sap:column id="sfcColumn" position="0">
												<f:facet name="header">
													<h:outputText id="sfcHeaderText"
														value="Chasis No" />
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
										width="90px" height="15px" action="#{einSerializeBean.assign}">
							</sap:commandButtonLarge>
							
							<fh:outputLabel value="    " ></fh:outputLabel>
							</td>
							<td>
							<!--action attribute - MethodExpression representing the application action to invoke when this component is activated by the user  -->
							<sap:commandButtonLarge id="returntoPOD" value="Return to POD"
										width="90px" height="15px" actionListener="#{einSerializeBean.returntoPOD}">
							</sap:commandButtonLarge>
							
							<fh:outputLabel value="    " ></fh:outputLabel>
							<fh:outputLabel value="    "></fh:outputLabel>

							</td>
							
						</tr>
						</table>
						</sap:panelGroup>
								
					</sap:panelGrid>
					</sap:panel>
				</h:form>
		</sap:body>
	</sap:html>

</f:view>
