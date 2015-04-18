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
		 var myWindow;
		 var timeout;
		function customDataFieldBrowse() {
			clearTimeout( timeout );
	         var url = '/MEEXTDC~apps~web~atos.com/com/atos/orderboardvolvo/SubAssemblyOrders.jsf';
	         myWindow = window.open(url, 'Browse', 'menubar=no,height=1000,width=1000,scrollbars=yes,status=no,location=no,resizable=yes,dependent=yes');			 
	         timeout=setTimeout('myWindow.focus()', 1000);
	    }
		
		</script>	
	</sap:head>

	<sap:body title="Order Dashboard" id="body" height="100%" browserHistory="disabled" focusId="orderReleaseForm">
		<!-- To Localize Title use gapiI18nTransformer['KEY']-->
		<h:form id="orderReleaseForm">
				<f:attribute name="height" value="100%" />
				<!-- defines the form that will be updated when "delta" submits are made to the server -->
				<f:attribute name="sap-delta-id"
					value="#{sap:toClientId('orderReleaseForm')}" />
				<sap:panel id="panel1" width="100%" height="100%"
				isCollapsible="false" contentAreaDesign="transparent" isCollapsed="false">
					<f:attribute name="sap-delta-id"
						value="#{sap:toClientId('panel1')}" />
					<f:facet name="header">
										<!-- Panel title -->
										<h:outputText value="Order Dashboard" />
									</f:facet>
					<sap:panelGrid width="100%" height="100%" cellHalign="start"
					cellValign="top" columns="1">
						<sap:panelGroup backgroundDesign="transparent" width="100%"
						halign="start" valign="middle" height="3%" padding="5 5 5 5" >
								<fh:outputText id="messageBar" value="#{orderReleaseBeanVolvo.message}" rendered="false"/>
						</sap:panelGroup>
						<sap:panelGroup backgroundDesign="transparent" width="100%"
						halign="start" valign="middle" height="10%" padding="5 5 5 5" >
						<table>
						<tr>
						<td>
						<!-- Label * site : -->
								<fh:outputLabel value="Site " styleClass="labelStyle"></fh:outputLabel>
								<fh:outputLabel value=" "></fh:outputLabel>
								<sap:inputText id="siteInput"
									value="#{orderReleaseBeanVolvo.site}" disabled="true"></sap:inputText>
						
						
						</td>
						<td>
								<fh:outputLabel value=" "></fh:outputLabel>
								<fh:outputLabel value=" "></fh:outputLabel>						
						</td>
						<td>
								<!-- Label * material : -->
								<fh:outputLabel value="Material " styleClass="labelStyle"></fh:outputLabel>
								<fh:outputLabel value=" "></fh:outputLabel>
								<fh:outputLabel value="*" style="color: red;font-weight: bold;"></fh:outputLabel>
								<fh:outputLabel value=" "></fh:outputLabel>
								<sap:commandInputText id="materialInput"
									value="#{orderReleaseBeanVolvo.material}"
									submitOnFieldHelp="false"
									submitOnTabout="true" submitOnChange="false"
									disabled="#{orderReleaseBeanVolvo.disableMaterial}">
								</sap:commandInputText>
								<fh:outputLabel value=" "></fh:outputLabel>
								<h:commandButton style="height:30px;width:30px;" 
									image="/com/atos/icons/select.png" id="commandButton2"
									actionListener="#{orderReleaseBeanVolvo.showMaterialBrowse}"
									disabled="#{orderReleaseBeanVolvo.disableMaterial}"
									rendered="#{!orderReleaseBeanVolvo.disableMaterial}">
								</h:commandButton>
								<sap:panelPopup id="materialBrowsePopup"
									rendered="#{orderReleaseBeanVolvo.materialBrowseRendered}"
									mode="modeless" height="400px" width="400px">
									<f:facet name="header">
										<h:outputText value="Browse for Material" />
									</f:facet>
									<f:facet name="popupButtonArea">
										<h:panelGroup>
											<h:commandButton value="OK"
												actionListener="#{orderReleaseBeanVolvo.rowSelected}"></h:commandButton>
											<h:commandButton value="Cancel"
												actionListener="#{orderReleaseBeanVolvo.closeMaterialBrowse}"></h:commandButton>
										</h:panelGroup>
									</f:facet>
									<sap:panelGrid width="100%" height="100%"
										cellpadding="10px 10px 0px 10px">
										<sap:dataTable value="#{orderReleaseBeanVolvo.materialList}"
											var="materialBrowseVar" id="materialBrowseTable" width="100%"
											height="100%" columnReorderingEnabled="true">
											<sap:rowSelector value="#{orderReleaseBeanVolvo.selected}"
												id="rowSelector" selectionMode="single"
												selectionBehaviour="client"
												actionListener="#{orderReleaseBeanVolvo.rowSelected}"
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
						<td>							
								<!-- Label * Schedule Start Date:-->
								<fh:outputLabel value="Planned Start Date " styleClass="labelStyle"></fh:outputLabel>
								<fh:outputLabel value=" "></fh:outputLabel>
								<fh:outputLabel value="*" style="color: red;font-weight: bold;"></fh:outputLabel>
								<fh:outputLabel value=" "></fh:outputLabel>
								<sap:inputDate id="schedulestartdate"
									value="#{orderReleaseBeanVolvo.scheduleStartDate}"></sap:inputDate>
						
						</td>
						<td>
								<fh:outputLabel value=" "></fh:outputLabel>
								<fh:outputLabel value=" "></fh:outputLabel>							
						</td>	
						<td>
								<!-- Label * Schedule End Date:-->
								<fh:outputLabel value="Planned End Date " styleClass="labelStyle"></fh:outputLabel>
								<fh:outputLabel value=" "></fh:outputLabel>
								<fh:outputLabel value="*" style="color: red;font-weight: bold;"></fh:outputLabel>
								<fh:outputLabel value=" "></fh:outputLabel>
								<sap:inputDate id="scheduleenddate"
									value="#{orderReleaseBeanVolvo.scheduleEndDate}">
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
											action="#{orderReleaseBeanVolvo.readMaterialCustomData}">
											<sap:ajaxUpdate />
										</sap:commandButtonLarge>
										<fh:outputLabel value=" "></fh:outputLabel>
									
									
									</td>
									<td>
											<!--action attribute - MethodExpression representing the application action to invoke when this component is activated by the user  -->
											<sap:commandButtonLarge id="Release" value="Release"
												width="85px" height="20px"
												actionListener="#{orderReleaseBeanVolvo.showOrderReleasePopup}"> 
											</sap:commandButtonLarge>							
									
									</td>
									<td>
										<fh:outputLabel value=" "></fh:outputLabel>
										<sap:commandButtonLarge id="CLEAR" value="Clear" width="90px"
											imagePosition="start" height="20px"
											actionListener="#{orderReleaseBeanVolvo.clear}">
											<sap:ajaxUpdate />
										</sap:commandButtonLarge>								
									
									</td>
								
								
								</tr>
								
								
								
								</table>							

								<sap:commandButtonLarge id="windowCloseButton" value=""
									width="0px" height="0px"
									action="#{orderReleaseBeanVolvo.processWindowClosed}">
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
										value="#{orderReleaseBeanVolvo.materialCustomDataList}"
										first="0" var="rows" id="materialCustomDataTable" width="100%"
										height="100%" columnReorderingEnabled="true" rendered="true" >
										<sap:column >
											<f:facet name="header">
												<h:outputText value="Select" />
											</f:facet>
											<h:selectBooleanCheckbox id="selectBooleanCheckbox1"
												value="#{rows.select}" disabled="#{rows.disable}" >
												<sap:ajaxUpdate></sap:ajaxUpdate>
												</h:selectBooleanCheckbox>
										</sap:column>										
										<sap:column>
											<f:facet name="header">
												<h:outputText value="Order" style="float:right;" />
											</f:facet>
											<sap:outputLabel id="outputText1" value="#{rows.orderno}"></sap:outputLabel>
										</sap:column>
										<sap:column width="170px">
											<f:facet name="header">
												<h:outputText value="Priority" />
											</f:facet>
											<sap:outputLabel value="#{rows.priority}"></sap:outputLabel>
										</sap:column>
										<sap:column>
											<f:facet name="header">
												<h:outputText value="Sfc Start"></h:outputText>
											</f:facet>
											<sap:outputLabel id="outputText7"
												value="#{rows.chasisStart}"></sap:outputLabel>
										</sap:column>
										<sap:column>
											<f:facet name="header">
												<h:outputText value="Sfc End" />
											</f:facet>
											<sap:outputLabel id="outputText8"
												value="#{rows.chasisEnd}"></sap:outputLabel>
										</sap:column>
										<sap:column width="170px">
											<f:facet name="header">
												<h:outputText value="Quantity Available" />
											</f:facet>
											<sap:inputText id="inputText1"
												value="#{rows.quantityToBeReleased}" width="100px" disabled="#{rows.disable}"></sap:inputText>											
										</sap:column>
										<sap:column>
											<f:facet name="header">
												<h:outputText value="Quantity To Be Built" />
											</f:facet>
											<sap:outputLabel id="outputText5"
												value="#{rows.qtytobebuild}"></sap:outputLabel>
										</sap:column>
										<sap:column>
										<div style="text-align: center; vertical-align: middle">									
											<f:facet name="header">
												<h:outputText value="Subassembly Status" />
											</f:facet>
											<h:commandButton  image="#{rows.buttonStyle}"  style="width: 15px; height: 15px;" alt="#{rows.subassStatus}" actionListener="#{orderReleaseBeanVolvo.openSubaasmblyWindow}">
													<f:param name="order_id" value="#{rows.orderno}"></f:param>											
											</h:commandButton>
										</div>
										</sap:column>
										</sap:dataTable>						
							
							</sap:panel>
						</sap:panelGrid>
				</sap:panel>
				<sap:panelPopup id="orderReleasePopup"
												rendered="#{orderReleaseBeanVolvo.orderReleasePopupRendered}"
												mode="modal" height="200px" width="1000px">
												<f:facet name="header">
													<h:outputText value="Released Shop Orders" />
												</f:facet>
												<f:facet name="popupButtonArea">
													<h:panelGroup>
														<h:commandButton value="Close"
															actionListener="#{orderReleaseBeanVolvo.closeOrderReleasePopup}"></h:commandButton>
													</h:panelGroup>
												</f:facet>
												<sap:panelGrid width="100%" height="100%"
													cellpadding="10px 10px 0px 10px">
													<sap:dataTable
														value="#{orderReleaseBeanVolvo.orderReleasedList}"
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
				</h:form>
		</sap:body>
	</sap:html>

</f:view>
