<%@page pageEncoding="UTF-8"%>
<%@ page language="java"%>
<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core"%>
<%@ taglib prefix="fh" uri="http://java.sun.com/jsf/html"%>
<%@ taglib prefix="h" uri="http://java.sap.com/jsf/html"%>
<%@ taglib prefix="sap" uri="http://java.sap.com/jsf/html/extended"%>
<f:view>
	<sap:html>

	<sap:head>

		<style type="text/css">
.mystyle2 {
	border: none;
	background-color: #D9D9FF;
}
.mystyle3 {
	border: none;
	background-color: #E4E4FF;
}


.labelStyle {
	font-size: 12px;
	font-family: "Times New Roman";
}

.header {
	background-color: #9999FF;
}

.mystyle1 {
	color: black;
	background-color: #CCCCFF;
}

.hiddenstyle1 {
	display: none;
}

.alignmentCenter {
	text-align: center;
}
</style>
		<script>
	var myWindow;
	function customDataFieldBrowse() {
		var url = '/trainingwar/com/vendorID/customdata/CustomDataFieldBrowse.jsf';
		myWindow = window
				.open(
						url,
						'Browse',
						'menubar=no,height=200,width=300,scrollbars=yes,status=no,location=no,resizable=yes,dependent=yes');
		setTimeout('myWindow.focus()', 100);
	}
	function customDataFieldClose() {
		windowTemp.close();
	}
</script>
	</sap:head>

	<sap:body title="Shop Order Creation" id="body" height="100%"
		focusId="orderCreationDataForm" browserHistory="disabled">
		<!-- To Localize Title use gapiI18nTransformer['KEY']-->
		<h:form id="orderCreationDataForm">
			<sap:panel id="fieldButtonPanelVolvo" title="Shop Order Creation"
				width="100%" height="100%" isCollapsible="false"
				headerDesign="STANDARD" borderDesign="NONE" contentAreaDesign="transparent" isCollapsed="false">
				<f:facet name="header123">
					<fh:outputLabel value="Shop Order Creation" ></fh:outputLabel>
				</f:facet>
				<!-- The ajaxUpdate component enables server side ajax updates of specified UI components -->
                <sap:ajaxUpdate render="#{sap:toClientId('fieldButtonPanelVolvo')}"/>
                <sap:panelGrid width="100%" height="100%" columns="1" id="selectionPanelLayout">
				<sap:panelGroup  width="100%"
						halign="start" valign="middle" height="2em">
						<div class="mystyle2 ">
							<fh:outputLabel value=" " ></fh:outputLabel>
							<h:graphicImage value="/com/atos/icons/save.png"
								height="15px" width="15px" />
							<h:commandLink value="Save" id="commandLinkSave"
								actionListener="#{orderCreationDataBeanVolvo.showConfirmationPopup}" />
							<fh:outputLabel value=" " ></fh:outputLabel>
							<h:graphicImage value="/com/atos/icons/clear.png"
								height="15px" width="15px" />
							<h:commandLink value="Clear" id="commandLinkclear"
								action="#{orderCreationDataBeanVolvo.crearData}" />
							<sap:panelPopup id="confirmationPopup"
								rendered="#{orderCreationDataBeanVolvo.renderConfirmationPopup}"
								mode="modeless"  height="150px" width="600px">
								<f:facet name="header">
									<h:outputText value="Confirmation" />
								</f:facet>
								<f:facet name="popupButtonArea">
									<h:panelGroup>
										<h:commandButton value="Close"
											actionListener="#{orderCreationDataBeanVolvo.closeConfirmationPopup}"></h:commandButton>
									</h:panelGroup>
								</f:facet>
								<sap:panelGrid width="100%" height="100%"
									cellpadding="10px 10px 0px 10px" columns="1"
									cellHalign="center">
									<h:panelGroup>
										<h:outputLabel
											value="Do you want to create orders for components of Material #{orderCreationDataBeanVolvo.material} ? "></h:outputLabel>
										
									</h:panelGroup>
									<h:panelGroup>
										<sap:commandButton id="confirmationButtonYes" value="Yes"
											width="70px"
											actionListener="#{orderCreationDataBeanVolvo.confirmYes}"></sap:commandButton>
										<fh:outputLabel value=" " ></fh:outputLabel>
										<sap:commandButton id="confirmationButtonNo" value="No"
											width="70px"
											actionListener="#{orderCreationDataBeanVolvo.confirmNo}"></sap:commandButton>
									</h:panelGroup>
								</sap:panelGrid>
							</sap:panelPopup>
						</div>
					</sap:panelGroup>	
					<sap:panelGroup backgroundDesign="transparent" width="100%"
						halign="start" valign="middle" height="3%">					
							<!-- Message area -->
						<fh:outputText id="messageBar" value="#{orderCreationDataBeanVolvo.message}" rendered="false"/>						
					</sap:panelGroup>

					<sap:panelGroup width="100%" backgroundDesign="transparent"
						halign="center" valign="middle" height="3%">
						<div>
							<table>
								<tr>
									<td align="right">
									<fh:outputLabel value="Site " styleClass="labelStyle"></fh:outputLabel>
									</td>
									<td><sap:inputText id="siteInfo1"
										value="#{orderCreationDataBeanVolvo.site}" disabled="true">
									</sap:inputText></td>
								</tr>
								<tr>
									<td align="right">
									<fh:outputLabel value="Parent Shop Order" styleClass="labelStyle"></fh:outputLabel>
									</td>
									<td>
															<sap:commandInputText
															id="shopOrderInput"
															value="#{orderCreationDataBeanVolvo.shopOrderDownloaded}"
															submitOnFieldHelp="false" submitOnTabout="true"
															submitOnChange="false">
															<f:attribute name="upperCase" value="true" />
															</sap:commandInputText>															
															<fh:outputLabel value=" " ></fh:outputLabel> <h:commandButton
															style="height:30px;width:30px;"
															image="/com/atos/icons/select.png"
															id="shopOrderCommandButton2"
															actionListener="#{orderCreationDataBeanVolvo.showShopOrderBrowsePopup}">
														    </h:commandButton>	
														    <sap:panelPopup id="shopOrderBrowsePopup"
																rendered="#{orderCreationDataBeanVolvo.renderShopOrderBrowse}"
																mode="modeless" height="300px" width="400px">
																<f:facet name="header">
																	<h:outputText value="Browse for shop Order" />
																</f:facet>
																<f:facet name="popupButtonArea">
																	<h:panelGroup>
																		<h:commandButton value="OK"
																			actionListener="#{orderCreationDataBeanVolvo.rowSelectedForShopOrder}"></h:commandButton>
																		<h:commandButton value="Cancel"
																			actionListener="#{orderCreationDataBeanVolvo.closeShopOrderBrowse}"></h:commandButton>
																	</h:panelGroup>
																</f:facet>
																<sap:panelGrid width="100%" height="100%"
																	cellpadding="10px 10px 0px 10px" columns="1">
																	<sap:dataTable
																		value="#{orderCreationDataBeanVolvo.shopOrderList}"
																		var="shopOrderBrowseVar" id="shopOrderBrowseTable"
																		width="100%" height="100%"
																		columnReorderingEnabled="true">
																		<sap:rowSelector value="#{shopOrderBrowseVar.selected}"
																			id="shopOrderrowSelector" selectionMode="single"
																			selectionBehaviour="client"
																			actionListener="#{orderCreationDataBeanVolvo.rowSelectedForShopOrder}"
																			submitOnRowDoubleClick="true" />
																		<sap:column id="shopOrderColumn" position="0">
																			<f:facet name="header">
																				<h:outputText id="shopOrderHeaderText"
																					value="Shop Order" />
																			</f:facet>
																			<h:outputText value="#{shopOrderBrowseVar.shopOrder}"
																				id="shopOrderText" />
																		</sap:column>
																	</sap:dataTable>
																</sap:panelGrid>
															</sap:panelPopup>								
									</td>
								</tr>
								<tr>
									<td align="right">
									<fh:outputLabel value="Material " styleClass="labelStyle"></fh:outputLabel>
									</td>
									<td><sap:inputText id="info2"
										value="#{orderCreationDataBeanVolvo.material}" disabled="true">
									</sap:inputText></td>
								</tr>
								<tr >
									<td align="right">
									<fh:outputLabel value="Built Quantity " styleClass="labelStyle"></fh:outputLabel>
									</td>
									<td><sap:inputText 
										value="#{orderCreationDataBeanVolvo.builtQuantityDownloaded}" disabled="true">
									</sap:inputText></td>
								</tr>

							</table>
							<fh:outputLabel value=" " ></fh:outputLabel>
						</div>
					</sap:panelGroup>
					<sap:panelGroup width="100%" backgroundDesign="transparent"
						halign="center" valign="middle" height="100%">
							<sap:panelTabbed id="tabbedPanel" width="100%"
								isCollapsible="false" height="100%" borderDesign="none"
								isCollapsed="false"
								selectedPanelId="#{orderCreationDataBeanVolvo.selectedPanelId}">
								<sap:panel id="orderCreation" title="Shop Order" height="100%"
									defaultButton="orderCreationDataForm:hiidenbtn2">
									<div class="mystyle3">
									<f:facet name="header">
										<h:outputText value="Shop Order"></h:outputText>
									</f:facet>
									 <sap:panelGrid width="100%" height="100%" columns="1" id="selectionPanelLayout2">
										<sap:panelGroup width="100%" backgroundDesign="transparent"
											halign="center" valign="middle" height="97%">
											
												<table>
													<tr>
														<td height="50px" align="right">
														<fh:outputLabel value="Shop Order " styleClass="labelStyle"></fh:outputLabel>
														</td>
														<td height="50px"><sap:inputText id="shopOrderInput2"
															value="#{orderCreationDataBeanVolvo.shopOrder}"
															disabled="#{!orderCreationDataBeanVolvo.shopOrderEnable}"
															required="false">
															<f:attribute name="upperCase" value="true" />
														</sap:inputText></td>
													</tr>
													<tr>
													</tr>
													<tr>
														<td height="50px" align="right">
														<fh:outputLabel value="Material " styleClass="labelStyle"></fh:outputLabel>
														</td>
														<td height="50px"><sap:commandInputText
															id="materialInput"
															value="#{orderCreationDataBeanVolvo.material}"
															submitOnFieldHelp="false" submitOnTabout="true"
															submitOnChange="false"
															disabled="#{!orderCreationDataBeanVolvo.materialEnable}"
															required="false">
														</sap:commandInputText> <fh:outputLabel value=" " ></fh:outputLabel> <h:commandButton
															style="height:30px;width:30px;"
															image="/com/atos/icons/select.png"
															id="materialCommandButton2"
															actionListener="#{orderCreationDataBeanVolvo.showMaterialBrowse}"
															disabled="#{!orderCreationDataBeanVolvo.materialEnable}">
														</h:commandButton> <sap:panelPopup id="materialBrowsePopup"
															rendered="#{orderCreationDataBeanVolvo.materialBrowseRendered}"
															mode="modeless" height="300px" width="400px">
															<f:facet name="header">
																<h:outputText value="Browse for Material" />
															</f:facet>
															<f:facet name="popupButtonArea">
																<h:panelGroup>
																	<h:commandButton value="OK"
																		actionListener="#{orderCreationDataBeanVolvo.rowSelected}"></h:commandButton>
																	<h:commandButton value="Cancel"
																		actionListener="#{orderCreationDataBeanVolvo.closeMaterialBrowse}"></h:commandButton>
																</h:panelGroup>
															</f:facet>
															<sap:panelGrid width="100%" height="100%"
																cellpadding="10px 10px 0px 10px" columns="1">
																<sap:dataTable
																	value="#{orderCreationDataBeanVolvo.materialList}"
																	var="materialBrowseVar" id="materialBrowseTable"
																	width="100%" height="100%"
																	columnReorderingEnabled="true">
																	<sap:rowSelector value="#{materialBrowseVar.selected}"
																		id="rowSelector" selectionMode="single"
																		selectionBehaviour="client"
																		actionListener="#{orderCreationDataBeanVolvo.rowSelected}"
																		submitOnRowDoubleClick="true" />
																	<sap:column id="materialColumn" position="0">
																		<f:facet name="header">
																			<h:outputText id="materialHeaderText"
																				value="Material" />
																		</f:facet>
																		<h:outputText value="#{materialBrowseVar.material}"
																			id="materialText" />
																	</sap:column>
																</sap:dataTable>
															</sap:panelGrid>
														</sap:panelPopup></td>
													</tr>
													<tr>
													</tr>
													<tr>
														<td height="50px" align="right">
														<fh:outputLabel value="Build Qty " styleClass="labelStyle"></fh:outputLabel>
														</td>
														<td height="50px"><sap:inputText id="siteInput2"
															value="#{orderCreationDataBeanVolvo.builtQuantity}"
															required="false" /></td>
													</tr>
													<tr></tr>
													<tr></tr>
													<tr>
														<td height="50px" align="right">
														<fh:outputLabel value="Serial Number Start " styleClass="labelStyle"></fh:outputLabel>
														</td>
														<td height="50px"><sap:inputText id="siteInput3"
															value="#{orderCreationDataBeanVolvo.serialStart}" /></td>
														<td height="50px"><fh:outputLabel value=" "></fh:outputLabel></td>
														<td height="50px" align="right">
														<fh:outputLabel value="Serial Number End " styleClass="labelStyle"></fh:outputLabel>
														</td>
														<td height="50px"><sap:inputText id="siteInput4"
															value="#{orderCreationDataBeanVolvo.serialEnd}" /></td>
													</tr>
													<tr></tr>
													<tr></tr>
													<tr>
														<td height="50px" align="right">
														<fh:outputLabel value="Planned Start Date " styleClass="labelStyle"></fh:outputLabel>
														</td>
														<td height="50px"><sap:inputDate
															id="schedulestartdate1"
															value="#{orderCreationDataBeanVolvo.plannedStartDate}"></sap:inputDate></td>
														<td height="50px">
														<fh:outputLabel value=" "></fh:outputLabel>
														</td>
														<td height="50px" align="right">
														<fh:outputLabel value="Planned End Date " styleClass="labelStyle"></fh:outputLabel>
														</td><td height="50px">
														<sap:inputDate
															id="scheduleenddate1"
															value="#{orderCreationDataBeanVolvo.plannedEndDate}"></sap:inputDate></td>
													</tr>
													<tr></tr>
													<tr></tr>
													<tr>
														<td height="50px" align="right">
														<fh:outputLabel value="Scheduled Start Date " styleClass="labelStyle"></fh:outputLabel>
														</td>
														<td height="50px"><sap:inputDate
															id="schedulestartdate2"
															value="#{orderCreationDataBeanVolvo.scheduleStartDate}">
														</sap:inputDate></td>
														<td height="50px">
														<fh:outputLabel value=" "></fh:outputLabel>
														</td>
														<td height="50px" align="right">
														<fh:outputLabel value="Schedule End Date " styleClass="labelStyle"></fh:outputLabel>
														</td>
														<td height="50px"><sap:inputDate
															id="scheduleenddate2"
															value="#{orderCreationDataBeanVolvo.scheduleEndDate}"></sap:inputDate></td>
													</tr>

												</table>
												<div style="display: none"><h:commandButton
													id="hiidenbtn2"
													actionListener="#{orderCreationDataBeanVolvo.orderCreationBtnClick}"
													style="display:none;"></h:commandButton> <h:commandButton
													id="hiidenbtn3"
													actionListener="#{orderCreationDataBeanVolvo.showModalPopupPanel}"
													style="display:none;"></h:commandButton></div>
											
										</sap:panelGroup>
									</sap:panelGrid>
									</div>
								</sap:panel>
								<sap:panel title="BOM" id="bom">
									<div class="mystyle3">
									<f:facet name="header">
										<h:outputText value="BOM"></h:outputText>
									</f:facet>
									<sap:panelGrid width="100%" height="100%" columns="1" id="selectionPanelLayout3">
										<sap:panelGroup width="100%" backgroundDesign="transparent"
											halign="center" valign="middle" height="97%">
											
													<sap:dataTable													
														value="#{orderCreationDataBeanVolvo.bomCustomDataItems}"
														first="0" var="rows" id="materialCustomDataTable"
														width="100%" height="100%" columnReorderingEnabled="true"
														rendered="true">
														<sap:column width="370px">
															<f:facet name="header">
																<h:outputText value="Shop Order" />
															</f:facet>
															<sap:outputLabel id="shopOrderinputText1"
																value="#{rows.shopOrder}" />

														</sap:column>
														<sap:column>
															<f:facet name="header">
																<h:outputText value="Material" />
															</f:facet>
															<sap:outputLabel id="materialOutputText"
																value="#{rows.material}"></sap:outputLabel>
														</sap:column>
														<sap:column width="70px">
															<f:facet name="header">
																<h:outputText value="Lead Time" />
															</f:facet>
															<sap:inputText id="inputText1" value="#{rows.leadTime}"
																width="70px"></sap:inputText>
														</sap:column>
														<sap:column>
															<f:facet name="header">
																<h:outputText value="Serial No Start" />
															</f:facet>
															<sap:inputText id="serialStartInputText1"
																value="#{rows.serialStart}" width="100px"></sap:inputText>
														</sap:column>
														<sap:column>
															<f:facet name="header">
																<h:outputText value="Serial No End" />
															</f:facet>
															<sap:inputText id="serialEndInputText1"
																value="#{rows.serialEnd}" width="100px"></sap:inputText>
														</sap:column>
														<sap:column>
															<f:facet name="header">
																<h:outputText value="Quantity" />
															</f:facet>
															<sap:inputText id="quantityInputText1"
																value="#{rows.quantity}" width="100px"></sap:inputText>
														</sap:column>
														<sap:column>
															<f:facet name="header">
																<h:outputText value="Create Order" />
															</f:facet>
															<h:selectBooleanCheckbox id="selectBooleanCheckbox1"
																value="#{rows.createOrder}" />
														</sap:column>
														<sap:column>
															<f:facet name="header">
																<h:outputText value="" />
															</f:facet>
															<h:graphicImage height="10px" width="10px"
																value="/com/atos/icons/failure.png"
																rendered="#{rows.error}" alt="#{rows.errorMessage}"></h:graphicImage>
															<h:graphicImage height="10px" width="10px"
																value="/com/atos/icons/success.png"
																rendered="#{rows.success}"></h:graphicImage>
														</sap:column>
													</sap:dataTable>
												
												
											
										</sap:panelGroup>

									</sap:panelGrid>

									</div>
								</sap:panel>
							</sap:panelTabbed>
						
					</sap:panelGroup>				
				</sap:panelGrid>
			</sap:panel>

		</h:form>
	</sap:body>
	</sap:html>

</f:view>
