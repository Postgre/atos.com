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
				font-family: "Times New Roman";
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
           var url = '/biopodpluginwar/com/atos/orderboardvolvo/exportPage.jsf';
           myWindow = window.open(url, 'Browse', 'menubar=no,height=200,width=300,scrollbars=yes,status=no,location=no,resizable=yes,dependent=yes');  
           setTimeout('myWindow.focus()', 100);
           
      }	
	   
	   </script>
	
	</sap:head>

	<sap:body title="Bulk Order Release" id="body" height="100%" browserHistory="disabled" focusId="orderReleaseDataForm">
		<!-- To Localize Title use gapiI18nTransformer['KEY']-->
		<h:form id="orderReleaseDataForm">
				<f:attribute name="height" value="100%" />
				<!-- defines the form that will be updated when "delta" submits are made to the server -->
				<f:attribute name="sap-delta-id"
					value="#{sap:toClientId('orderReleaseDataForm')}" />
				<sap:panel id="fieldButtonPanel" width="100%" height="100%"
				isCollapsible="false" contentAreaDesign="transparent" isCollapsed="false">
					<f:attribute name="sap-delta-id"
						value="#{sap:toClientId('fieldButtonPanel')}" />
					<f:facet name="header">
										<!-- Panel title -->
										<h:outputText value="Bulk Order Release" />
									</f:facet>
					<sap:panelGrid width="100%" height="100%" cellHalign="start"
					cellValign="top" columns="1">
						<sap:panelGroup backgroundDesign="transparent" width="100%"
						halign="start" valign="middle" height="3%" padding="5 5 5 5" >
								<fh:outputText id="messageBar" value="#{orderReleaseDataBeanVolvo.message}" rendered="false"/>
						</sap:panelGroup>
						<sap:panelGroup backgroundDesign="transparent" width="100%"
						halign="start" valign="middle" height="12%" padding="5 5 5 5" >
						<table>
						<tr>
						<td>
						<!-- Label * site : -->
								<fh:outputLabel value="Site " styleClass="labelStyle"></fh:outputLabel>
								<fh:outputLabel value=" "></fh:outputLabel>
								<sap:inputText id="siteInput"
									value="#{orderReleaseDataBeanVolvo.site}" disabled="true"></sap:inputText>
						
						
						</td>
						<td>
								<fh:outputLabel value=" "></fh:outputLabel>
								<fh:outputLabel value=" "></fh:outputLabel>
						</td>
						<td>
								<!-- Label * production line -->
								<fh:outputLabel value="Production Line " styleClass="labelStyle"></fh:outputLabel>
								<fh:outputLabel value=" "></fh:outputLabel>
								<sap:commandInputText id="productionLineInput"
									value="#{orderReleaseDataBeanVolvo.productionLine}"
									submitOnFieldHelp="false"
									actionListener="#{orderReleaseDataBeanVolvo.activateOnProductionLineSelect}"
									submitOnTabout="true" submitOnChange="false"
									disabled="#{orderReleaseDataBeanVolvo.disableProductionLine}">
								</sap:commandInputText>
								<fh:outputLabel value=" "></fh:outputLabel>
								<h:commandButton style="height:30px;width:30px;"
									image="/com/atos/icons/select.png" id="commandButton1"
									actionListener="#{orderReleaseDataBeanVolvo.showProductionLineBrowse}"
									disabled="#{orderReleaseDataBeanVolvo.disableProductionLine}"
									rendered="#{!orderReleaseDataBeanVolvo.disableProductionLine}">
								</h:commandButton>
								<sap:panelPopup id="rcTypeBrowsePopup"
									rendered="#{orderReleaseDataBeanVolvo.productionLineBrowseRendered}"
									mode="modeless" height="400px" width="400px">
									<f:facet name="header">
										<h:outputText value="Browse for Production Line" />
									</f:facet>
									<f:facet name="popupButtonArea">
										<h:panelGroup>
											<h:commandButton value="OK"
												actionListener="#{orderReleaseDataBeanVolvo.rowSelectedProductionLine}"></h:commandButton>
											<h:commandButton value="Cancel"
												actionListener="#{orderReleaseDataBeanVolvo.closeProductionLineBrowse}"></h:commandButton>
										</h:panelGroup>
									</f:facet>
									<sap:panelGrid width="100%" height="100%"
										cellpadding="10px 10px 0px 10px">
										<sap:dataTable
											value="#{orderReleaseDataBeanVolvo.productionLineList}"
											var="productionLineBrowseVar" id="productionLineBrowseTable"
											width="100%" height="100%" columnReorderingEnabled="true">
											<sap:rowSelector
												value="#{orderReleaseDataBeanVolvo.selectProductionLine}"
												id="productionLineRowSelector" selectionMode="single"
												selectionBehaviour="client"
												actionListener="#{orderReleaseDataBeanVolvo.rowSelectedProductionLine}"
												submitOnRowDoubleClick="true" />
											<sap:column id="rcTypeColumn" position="0">
												<f:facet name="header">
													<h:outputText id="productionLineHeaderText"
														value="Production Line" />
												</f:facet>
												<h:outputText value="#{productionLineBrowseVar.material}"
													id="rcTypeText" />
											</sap:column>
										</sap:dataTable>
									</sap:panelGrid>
								</sap:panelPopup>

						</td>
						<td>
								<fh:outputLabel value=" "></fh:outputLabel>
								<fh:outputLabel value=" "></fh:outputLabel>						
						</td>
						<td>
								<!-- Label * material : -->
								<fh:outputLabel value="Material " styleClass="labelStyle"></fh:outputLabel>
								<fh:outputLabel value=" "></fh:outputLabel>
								<sap:commandInputText id="materialInput"
									value="#{orderReleaseDataBeanVolvo.material}"
									submitOnFieldHelp="false"
									actionListener="#{orderReleaseDataBeanVolvo.activateOnProductionLineSelect}"
									submitOnTabout="true" submitOnChange="false"
									disabled="#{orderReleaseDataBeanVolvo.disableMaterial}">
								</sap:commandInputText>
								<fh:outputLabel value=" "></fh:outputLabel>
								<h:commandButton style="height:30px;width:30px;" 
									image="/com/atos/icons/select.png" id="commandButton2"
									actionListener="#{orderReleaseDataBeanVolvo.showMaterialBrowse}"
									disabled="#{orderReleaseDataBeanVolvo.disableMaterial}"
									rendered="#{!orderReleaseDataBeanVolvo.disableMaterial}">
								</h:commandButton>
								<sap:panelPopup id="materialBrowsePopup"
									rendered="#{orderReleaseDataBeanVolvo.materialBrowseRendered}"
									mode="modeless" height="400px" width="400px">
									<f:facet name="header">
										<h:outputText value="Browse for Material" />
									</f:facet>
									<f:facet name="popupButtonArea">
										<h:panelGroup>
											<h:commandButton value="OK"
												actionListener="#{orderReleaseDataBeanVolvo.rowSelected}"></h:commandButton>
											<h:commandButton value="Cancel"
												actionListener="#{orderReleaseDataBeanVolvo.closeMaterialBrowse}"></h:commandButton>
										</h:panelGroup>
									</f:facet>
									<sap:panelGrid width="100%" height="100%"
										cellpadding="10px 10px 0px 10px">
										<sap:dataTable value="#{orderReleaseDataBeanVolvo.materialList}"
											var="materialBrowseVar" id="materialBrowseTable" width="100%"
											height="100%" columnReorderingEnabled="true">
											<sap:rowSelector value="#{orderReleaseDataBeanVolvo.selected}"
												id="rowSelector" selectionMode="single"
												selectionBehaviour="client"
												actionListener="#{orderReleaseDataBeanVolvo.rowSelected}"
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
							<fh:outputLabel value=" "></fh:outputLabel>
							<fh:outputLabel value=" "></fh:outputLabel>
						
						</td>
						</tr>
						
						</table>															
				</sap:panelGroup>
				<sap:panelGroup backgroundDesign="transparent" width="100%"
				halign="start" valign="top" height="12%" padding="5 5 5 5">
								<table>
								<tr>
									<td>
										<!-- Label * priority: -->
								<fh:outputLabel value="Priority " styleClass="labelStyle"></fh:outputLabel>
								<fh:outputLabel value=" "></fh:outputLabel>
								<sap:commandInputText id="priorityInput"
									value="#{orderReleaseDataBeanVolvo.priority}"
									submitOnFieldHelp="false" submitOnTabout="false"
									submitOnChange="false"
									disabled="#{orderReleaseDataBeanVolvo.disableBrowsePopup}">
								</sap:commandInputText>
								<fh:outputLabel value=" "></fh:outputLabel>
								<h:commandButton style="height:30px;width:30px;" 
									image="/com/atos/icons/select.png" id="commandButton3"
									actionListener="#{orderReleaseDataBeanVolvo.showPriorityBrowse}"
									disabled="#{orderReleaseDataBeanVolvo.disableBrowsePopup}">
								</h:commandButton>
								<sap:panelPopup id="priorityBrowsePopup"
									rendered="#{orderReleaseDataBeanVolvo.priorityBrowseRendered}"
									mode="modeless" height="400px" width="400px">
									<f:facet name="header">
										<h:outputText value="Browse for Priority" />
									</f:facet>
									<f:facet name="popupButtonArea">
										<h:panelGroup>
											<h:commandButton value="OK"
												actionListener="#{orderReleaseDataBeanVolvo.rowSelectedPriority}"></h:commandButton>
											<h:commandButton value="Cancel"
												actionListener="#{orderReleaseDataBeanVolvo.closePriorityBrowse}"></h:commandButton>
										</h:panelGroup>
									</f:facet>
									<sap:panelGrid width="100%" height="100%"
										cellpadding="10px 10px 0px 10px">
										<sap:dataTable value="#{orderReleaseDataBeanVolvo.priorityList}"
											var="priorityBrowseVar" id="priorityBrowseTable" width="100%"
											height="100%" columnReorderingEnabled="true">
											<sap:rowSelector
												value="#{orderReleaseDataBeanVolvo.selectPriority}"
												id="priorityRowSelector" selectionMode="single"
												selectionBehaviour="client"
												actionListener="#{orderReleaseDataBeanVolvo.rowSelectedPriority}"
												submitOnRowDoubleClick="true" />
											<sap:column id="priorityColumn" position="0">
												<f:facet name="header">
													<h:outputText id="priorityHeaderText" value="Priority" />
												</f:facet>
												<h:outputText value="#{priorityBrowseVar.material}"
													id="priorityText" />
											</sap:column>
										</sap:dataTable>
									</sap:panelGrid>
								</sap:panelPopup>
							</td>
							<td>							
								<fh:outputLabel value=" "></fh:outputLabel>
								<fh:outputLabel value=" "></fh:outputLabel>						
							</td>
							<td>							
								<!-- Label * Schedule Start Date:-->
								<fh:outputLabel value="Planned Start Date " styleClass="labelStyle"></fh:outputLabel>
								<fh:outputLabel value=" "></fh:outputLabel>
								<sap:inputDate id="schedulestartdate"
									value="#{orderReleaseDataBeanVolvo.scheduleStartDate}"
									disabled="#{orderReleaseDataBeanVolvo.disableBrowsePopup}"></sap:inputDate>
						
							</td>
							<td>
								<fh:outputLabel value=" "></fh:outputLabel>
								<fh:outputLabel value=" "></fh:outputLabel>							
							</td>	
							<td>
								<!-- Label * Schedule End Date:-->
								<fh:outputLabel value="Planned End Date " styleClass="labelStyle"></fh:outputLabel>
								<fh:outputLabel value=" "></fh:outputLabel>
								<sap:inputDate id="scheduleenddate"
									value="#{orderReleaseDataBeanVolvo.scheduleEndDate}"
									disabled="#{orderReleaseDataBeanVolvo.disableBrowsePopup}">
								</sap:inputDate>								
							</td>							
						</tr>							
						</table>										
						</sap:panelGroup>
						<sap:panelGroup backgroundDesign="transparent" width="100%"
						halign="center" valign="top" height="10%" padding="5 5 5 5">
								<table>
								<tr>
									<td>
										<!--action attribute - MethodExpression representing the application action to invoke when this component is activated by the user  -->
										<sap:commandButtonLarge id="retrieve" value="Retrieve"
											width="85px" height="20px"
											action="#{orderReleaseDataBeanVolvo.readMaterialCustomData}">
											<sap:ajaxUpdate />
										</sap:commandButtonLarge>
										<fh:outputLabel value=" "></fh:outputLabel>
									
									
									</td>
									<td style="display:none">
									
										<!--action attribute - MethodExpression representing the application action to invoke when this component is activated by the user  -->
										<sap:commandButtonLarge id="SaveSequence" value="Save"
											width="85px" height="20px"
											actionListener="#{orderReleaseDataBeanVolvo.saveOrders}" rendered="#{orderReleaseDataBeanVolvo.sequencing}">
										</sap:commandButtonLarge>
										<fh:outputLabel value=" "></fh:outputLabel>
									
									
									
									</td>
									<td style="display:none">
									
											<sap:commandButtonLarge id="ExportSequence" value="Export"
											width="85px" height="20px"
											actionListener="#{orderReleaseDataBeanVolvo.executeExportScript}" rendered="#{orderReleaseDataBeanVolvo.sequencing}">
											</sap:commandButtonLarge>
											<fh:outputLabel value=" "></fh:outputLabel>
									
									
									
									</td>
									<td>
											<!--action attribute - MethodExpression representing the application action to invoke when this component is activated by the user  -->
											<sap:commandButtonLarge id="Release" value="Release"
												width="85px" height="20px"
												actionListener="#{orderReleaseDataBeanVolvo.showOrderReleasePopup}" rendered="#{orderReleaseDataBeanVolvo.releasable}"> 
											</sap:commandButtonLarge>
											<sap:panelPopup id="orderReleasePopup"
												rendered="#{orderReleaseDataBeanVolvo.orderReleasePopupRendered}"
												mode="modeless" height="200px" width="1000px">
												<f:facet name="header">
													<h:outputText value="Released Shop Orders" />
												</f:facet>
												<f:facet name="popupButtonArea">
													<h:panelGroup>
														<h:commandButton value="Close"
															actionListener="#{orderReleaseDataBeanVolvo.closeOrderReleasePopup}"></h:commandButton>
													</h:panelGroup>
												</f:facet>
												<sap:panelGrid width="100%" height="100%"
													cellpadding="10px 10px 0px 10px">
													<sap:dataTable
														value="#{orderReleaseDataBeanVolvo.orderReleasedList}"
														var="orderReleaseVar" id="orderReleaseTable" width="100%"
														height="100%" columnReorderingEnabled="true">
														<sap:column id="orderReleaseShopOrder" position="0">
															<f:facet name="header">
																<h:outputText id="orderReleaseHeaderText1"
																	value="Shop Order" />
															</f:facet>
															<h:outputText value="#{orderReleaseVar.shopOrder}"
																id="orderReleaseText1" />
			
														</sap:column>
														<sap:column id="orderReleaseQuantity" position="0">
															<f:facet name="header">
																<h:outputText id="orderReleaseHeaderText2" value="Quantity" />
															</f:facet>
															<h:outputText value="#{orderReleaseVar.quantity}"
																id="orderReleaseText2" />
														</sap:column>
														<sap:column id="orderReleaseFirstSFC" position="0">
															<f:facet name="header">
																<h:outputText id="orderReleaseHeaderText3"
																	value="First SFC" />
															</f:facet>
															<h:outputText value="#{orderReleaseVar.firstSFC}"
																id="orderReleaseText3" />
														</sap:column>
														<sap:column id="orderReleaseLastSFC" position="0">
															<f:facet name="header">
																<h:outputText id="orderReleaseHeaderText4" value="Last SFC" />
															</f:facet>
															<h:outputText value="#{orderReleaseVar.lastSFC}"
																id="orderReleaseText4" />
														</sap:column>
														<sap:column id="orderReleaseMaterial" position="0">
															<f:facet name="header">
																<h:outputText id="orderReleaseHeaderText5" value="Material" />
															</f:facet>
															<h:outputText value="#{orderReleaseVar.material}"
																id="orderReleaseText5" />
														</sap:column>
														<sap:column id="orderReleaseRouting" position="0">
															<f:facet name="header">
																<h:outputText id="orderReleaseHeaderText6" value="Routing" />
															</f:facet>
															<h:outputText value="#{orderReleaseVar.routing}"
																id="orderReleaseText6" />
														</sap:column>
														<sap:column id="orderReleaseOperation" position="0">
															<f:facet name="header">
																<h:outputText id="orderReleaseHeaderText7"
																	value="Operation" />
															</f:facet>
															<h:outputText value="#{orderReleaseVar.operation}"
																id="orderReleaseText7" />
														</sap:column>
														<sap:column id="orderReleaseProcessLoss" position="0">
															<f:facet name="header">
																<h:outputText id="orderReleaseHeaderText8"
																	value="Process Lot" />
															</f:facet>
															<h:outputText value="#{orderReleaseVar.processLoss}"
																id="orderReleaseText8" />
														</sap:column>
													</sap:dataTable>
												</sap:panelGrid>
											</sap:panelPopup>
									
									
									
									</td>
									<td>
										<fh:outputLabel value=" "></fh:outputLabel>
										<sap:commandButtonLarge id="CLEAR" value="Clear" width="90px"
											imagePosition="start" height="20px"
											actionListener="#{orderReleaseDataBeanVolvo.clear}">
											<sap:ajaxUpdate />
										</sap:commandButtonLarge>						
									
									
									</td>
								</tr>
								
								
								
								</table>							

								<sap:commandButtonLarge id="windowCloseButton" value=""
									width="0px" height="0px"
									action="#{orderReleaseDataBeanVolvo.processWindowClosed}">
									<sap:ajaxUpdate />
								</sap:commandButtonLarge>
							</sap:panelGroup>
							<sap:panel id="displayPanel"  width="100%" height="100%"
								isCollapsible="false" contentAreaDesign="transparent"
								isCollapsed="false">
								
									<f:facet name="header">
										<!-- Panel title -->
										<h:outputText value="Releasable Orders " />
									</f:facet>
									<!-- The ajaxUpdate component enables server side ajax updates of specified UI components -->
									<sap:ajaxUpdate render="#{sap:toClientId('displayPanel')}"/>
									<sap:dataTable
										value="#{orderReleaseDataBeanVolvo.materialCustomDataList}"
										first="0" var="rows" id="materialCustomDataTable" width="100%"
										height="100%" columnReorderingEnabled="true" rendered="true" browsingMode="pager" rows="10">
										<sap:rowSelector
												value="#{rows.select}"
												selectionMode="multiTouch"
												id="selector123"
												selectionBehaviour="client"
												submitOnRowDoubleClick="true" />
									    <sap:column>
											<f:facet name="header">
												<h:outputText value="Order No" style="float:right;" />
											</f:facet>
											<sap:outputLabel id="outputText1" value="#{rows.orderno}"></sap:outputLabel>
										</sap:column>
										<sap:column>
											<f:facet name="header">
												<h:outputText value="Material" />
											</f:facet>
											<sap:outputLabel id="outputText2" value="#{rows.material}"></sap:outputLabel>
										</sap:column>
										<sap:column>
											<f:facet name="header">
												<h:outputText value="Work Center" />
											</f:facet>
											<sap:outputLabel id="outputText3" value="#{rows.workcenter}"></sap:outputLabel>
										</sap:column>
										<sap:column width="170px">
											<f:facet name="header">
												<h:outputText value="Priority" />
											</f:facet>
											<sap:commandInputText id="inputTextSequencing1"
												value="#{rows.priority}"
												submitOnFieldHelp="false" submitOnTabout="false"
												submitOnChange="true"
												disabled="#{rows.disable  or (orderReleaseDataBeanVolvo.releasable)}" 
												width="100px">
											</sap:commandInputText>
											<sap:outputLabel value=" "></sap:outputLabel>
										</sap:column>
										<sap:column width="170px">
											<f:facet name="header">
												<h:outputText value="Quantity To Release" />
											</f:facet>
											<sap:inputText id="inputText1"
												value="#{rows.quantityToBeReleased}"
												disabled="#{rows.disable}" required="true" width="100px"></sap:inputText>
											<sap:outputLabel value=" "></sap:outputLabel>
											<h:graphicImage height="10px" width="10px"
												value="/com/atos/icons/exclamation.png"
												rendered="#{rows.error}" alt="#{rows.errorMessage}"></h:graphicImage>
										</sap:column>
										<sap:column>
											<f:facet name="header">
												<h:outputText value="Quantity To Be Built" />
											</f:facet>
											<sap:outputLabel id="outputText5"
												value="#{rows.qtytobebuild}"></sap:outputLabel>
										</sap:column>
										<sap:column>
											<f:facet name="header">
												<h:outputText value="Labor Charge Code" />
											</f:facet>
											<sap:outputLabel id="outputText6"
												value="#{rows.laborchargecode}"></sap:outputLabel>
										</sap:column>
										<sap:column>
											<f:facet name="header">
												<h:outputText value="Planned Start Date" />
											</f:facet>
											<sap:outputLabel id="outputText7"
												value="#{rows.scheduledstartdate}"></sap:outputLabel>
										</sap:column>
										<sap:column>
											<f:facet name="header">
												<h:outputText value="Planned End Date" />
											</f:facet>
											<sap:outputLabel id="outputText8"
												value="#{rows.scheduleenddate}"></sap:outputLabel>
										</sap:column>
									</sap:dataTable>						
							
							</sap:panel>
						</sap:panelGrid>
				</sap:panel>
				</h:form>
		</sap:body>
	</sap:html>

</f:view>
