<%@page pageEncoding="UTF-8"%>
<%@ page language="java"%>
<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core"%>
<%@ taglib prefix="fh" uri="http://java.sun.com/jsf/html"%>
<%@ taglib prefix="h" uri="http://java.sap.com/jsf/html"%>
<%@ taglib prefix="sap" uri="http://java.sap.com/jsf/html/extended"%>
<%@ taglib prefix="ls" uri="http://java.sap.com/jsf/html/internal"%>

<f:view>
	<sap:html>

	<sap:head>

		<style type="text/css">
.mystyle2 {
	border: none;
	background-color: #D9D9FF;
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
	</sap:head>

	<sap:body title="Shop Order Creation" id="body" height="100%">
		<!-- To Localize Title use gapiI18nTransformer['KEY']-->
		<ls:page id="materialCustomDataPage" initBaseLibrary="false"
			title="Shop Order Creation" hasMargin="false" scrollingMode="HIDE"
			verticalSizing="FILL" formId="materialCustomDataForm"
			hasEventQueue="false" browserHistory="DISABLED">
			<ls:script facet="headScripts" type="CUSTOM"
				content="

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
                var url = '/trainingwar/com/vendorID/customdata/CustomDataFieldBrowse.jsf';
                myWindow = window.open(url, 'Browse', 'menubar=no,height=200,width=300,scrollbars=yes,status=no,location=no,resizable=yes,dependent=yes');  
                setTimeout('myWindow.focus()', 100);
           }
           function customDataFieldClose() {
                windowTemp.close();
           }
           
          " />
			<h:form id="orderCreationDataForm">
				<f:attribute name="height" value="100%" />
				<!-- defines the form that will be updated when "delta" submits are made to the server -->
				<f:attribute name="sap-delta-id"
					value="#{sap:toClientId('orderCreationDataForm')}" />
				<ls:panel facet="content" id="fieldButtonPanelVolvo"
					title="Shop Order Creation" width="100%" height="100%"
					hasEditableTitle="false" isCollapsible="false" collapsed="false"
					enabled="true" headerDesign="STANDARD" areaDesign="TRANSPARENT"
					borderDesign="NONE" scrollingMode="NONE" isDragHandle="false"
					contentPadding="NONE">
					<f:attribute name="sap-delta-id"
						value="#{sap:toClientId('fieldButtonPanelVolvo')}" />
					<ls:matrixLayout facet="content" id="selectionPanelLayout"
						width="100%" height="100%">
						<ls:matrixLayoutRow facet="rows" id="row512">
							<ls:matrixLayoutCell facet="cells" id="cell512"
								cellBackgroundDesign="TRANSPARENT" cellDesign="PADLESS"
								HAlign="LEFT" width="100%" height="3%" customStyle="mystyle1 ">
								<ls:label text=" " visibility="BLANK" />
								<h:graphicImage value="/com/atos/icons/save.png"
									height="15px" width="15px" />
								<h:commandLink value="Save" id="commandLinkSave"
									actionListener="#{orderCreationDataBeanVolvo.showConfirmationPopup}" />
								<ls:label text=" " visibility="BLANK" />
								<h:graphicImage value="/com/atos/icons/clear.png"
									height="15px" width="15px" />
								<h:commandLink value="Clear" id="commandLinkclear"
									action="#{orderCreationDataBeanVolvo.crearData}" />
								<sap:panelPopup id="confirmationPopup"
									rendered="#{orderCreationDataBeanVolvo.renderConfirmationPopup}"
									mode="modal" height="150px" width="600px">
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
											</outputLabel>
										</h:panelGroup>
										<h:panelGroup>
											<sap:commandButton id="confirmationButtonYes" value="Yes"
												width="70px"
												actionListener="#{orderCreationDataBeanVolvo.confirmYes}"></sap:commandButton>
											<ls:label text=" " visibility="BLANK" />
											<sap:commandButton id="confirmationButtonNo" value="No"
												width="70px"
												actionListener="#{orderCreationDataBeanVolvo.confirmNo}"></sap:commandButton>
										</h:panelGroup>
									</sap:panelGrid>
								</sap:panelPopup>
							</ls:matrixLayoutCell>
						</ls:matrixLayoutRow>
						<ls:matrixLayoutRow facet="rows" id="row1">
							<ls:matrixLayoutCell facet="cells" id="cell1"
								cellBackgroundDesign="TRANSPARENT" cellDesign="PADLESS"
								HAlign="LEFT" width="100%" height="3%" customStyle="mystyle1 ">
								<!-- 'messageBar' tag is used for displaying a message for the users  -->
								<ls:messageBar id="messageBar"
									text="#{orderCreationDataBeanVolvo.message}" rendered="false" />
								</ls:matrixLayoutCell>
						</ls:matrixLayoutRow>

						<ls:matrixLayoutRow facet="rows" id="inforow">
							<ls:matrixLayoutCell facet="cells" id="infocell"
								cellDesign="PADLESS" HAlign="CENTER" width="100%" height="5%"
								customStyle="mystyle2 ">
								<table>
									<tr>
										<td align="right"><ls:label facet="content"
											design="EMPHASIZED" text="Site " width="60px" /></td>
										<td><sap:inputText id="siteInfo1"
											value="#{orderCreationDataBeanVolvo.site}" disabled="true">
										</sap:inputText></td>
									</tr>
									<tr>
										<td align="right"><ls:label facet="content"
											design="EMPHASIZED" text="Material " width="60px" /></td>
										<td><sap:inputText id="info2"
											value="#{orderCreationDataBeanVolvo.material}"
											disabled="true">
										</sap:inputText></td>
									</tr>

								</table>


								<ls:label text=" " visibility="BLANK" />
							</ls:matrixLayoutCell>
						</ls:matrixLayoutRow>
						<ls:matrixLayoutRow facet="rows" id="tabrow">
							<ls:matrixLayoutCell facet="cells" id="tabcell"
								cellBackgroundDesign="TRANSPARENT" cellDesign="PADLESS"
								HAlign="CENTER" width="100%" height="100%">
								<sap:panelTabbed id="tabbedPanel" width="100%"
									isCollapsible="false" height="100%" borderDesign="none"
									isCollapsed="false"
									selectedPanelId="#{orderCreationDataBeanVolvo.selectedPanelId}">
									<sap:panel id="orderCreation" title="Shop Order" height="100%"
										defaultButton="orderCreationDataForm:hiidenbtn2">
										<f:facet name="header">
											<h:outputText value="Shop Order"></h:outputText>
										</f:facet>
										<ls:matrixLayout facet="content" id="selectionPanelLayout2"
											width="100%" height="100%">
											<ls:matrixLayoutRow facet="rows" id="row3">
												<ls:matrixLayoutCell facet="cells" id="cell3"
													cellBackgroundDesign="TRANSPARENT" cellDesign="PADLESS"
													HAlign="CENTER" width="100%" height="97%"
													customStyle="mystyle2 ">
													<table>
														<tr>
															<td height="50px" align="right"><ls:label
																facet="content" design="EMPHASIZED" text="Shop Order "
																width="60px" /></td>
															<td height="50px"><sap:inputText
																id="shopOrderInput2"
																value="#{orderCreationDataBeanVolvo.shopOrder}"
																disabled="#{!orderCreationDataBeanVolvo.shopOrderEnable}" required="false">
																<f:attribute name="upperCase" value="true" />
															</sap:inputText></td>
														</tr>
														<tr>
														</tr>
														<tr>
															<td height="50px" align="right"><ls:label
																facet="content" design="EMPHASIZED" text="Material "
																width="60px" /></td>
															<td height="50px"><sap:commandInputText
																id="materialInput"
																value="#{orderCreationDataBeanVolvo.material}"
																submitOnFieldHelp="false" submitOnTabout="true"
																submitOnChange="false"
																disabled="#{!orderCreationDataBeanVolvo.materialEnable}" required="false">
															</sap:commandInputText> <ls:label text=" " visibility="BLANK" /> <h:commandButton
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
															<td height="50px" align="right"><ls:label
																facet="content" design="EMPHASIZED" text="Build Qty "
																width="60px" /></td>
															<td height="50px"><sap:inputText id="siteInput2"
																value="#{orderCreationDataBeanVolvo.builtQuantity}" required="false" /></td>
														</tr>
														<tr></tr>
														<tr></tr>
														<tr>
															<td height="50px" align="right"><ls:label
																facet="content" design="EMPHASIZED"
																text=" Serial Number Start " width="60px" /></td>
															<td height="50px"><sap:inputText id="siteInput3"
																value="#{orderCreationDataBeanVolvo.serialStart}" /></td>
															<td height="50px"><ls:label text="   "
																visibility="BLANK" /></td>
															<td height="50px" align="right"><ls:label
																facet="content" design="EMPHASIZED"
																text="Serial Number End " width="60px" /></td>
															<td height="50px"><sap:inputText id="siteInput4"
																value="#{orderCreationDataBeanVolvo.serialEnd}" /></td>
														</tr>
														<tr></tr>
														<tr></tr>
														<tr>
															<td height="50px" align="right"><ls:label
																facet="content" design="EMPHASIZED"
																text="Planned Start Date" width="40px" /></td>
															<td height="50px"><sap:inputDate
																id="schedulestartdate1"
																value="#{orderCreationDataBeanVolvo.plannedStartDate}"></sap:inputDate></td>
															<td height="50px"><ls:label text="   "
																visibility="BLANK" /></td>
															<td height="50px" align="right"><ls:label
																facet="content" design="EMPHASIZED"
																text="Planned End Date" width="40px" /></td>
															<td height="50px"><sap:inputDate
																id="scheduleenddate1"
																value="#{orderCreationDataBeanVolvo.plannedEndDate}"></sap:inputDate></td>
														</tr>
														<tr></tr>
														<tr></tr>
														<tr>
															<td height="50px" align="right"><ls:label
																facet="content" design="EMPHASIZED"
																text="Scheduled Start Date" width="40px" /></td>
															<td height="50px"><sap:inputDate
																id="schedulestartdate2"
																value="#{orderCreationDataBeanVolvo.scheduleStartDate}">
															</sap:inputDate></td>
															<td height="50px"><ls:label text="   "
																visibility="BLANK" /></td>
															<td height="50px" align="right"><ls:label
																facet="content" design="EMPHASIZED"
																text="Schedule End Date" width="40px" /></td>
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
												</ls:matrixLayoutCell>
											</ls:matrixLayoutRow>
										</ls:matrixLayout>
									</sap:panel>
									<sap:panel title="BOM" id="bom"
										defaultButton="orderCreationDataForm:hiidenbtn1">
										<f:facet name="header">
											<h:outputText value="BOM"></h:outputText>
										</f:facet>
										<ls:matrixLayout facet="content" id="selectionPanelLayout3"
											width="100%" height="100%">
											<ls:matrixLayoutRow facet="rows" id="row313">
												<ls:matrixLayoutCell facet="cells" id="cell313"
													cellBackgroundDesign="TRANSPARENT" cellDesign="PADLESS"
													HAlign="CENTER" width="100%" height="97%">
													<ls:scrollContainer id="tableScroller" facet="content"
														width="100%" height="97%" scrollingMode="AUTO"
														scrollInfoEnabled="true" visibility="VISIBLE"
														isLayout="true" scrollTop="0" scrollLeft="0">
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
													</ls:scrollContainer>
													<div style="display: none;"><h:commandButton
														id="hiidenbtn1"
														actionListener="#{orderCreationDataBeanVolvo.bomBtnClick}"
														styleClass="hiddenstyle1"></h:commandButton></div>
												</ls:matrixLayoutCell>
											</ls:matrixLayoutRow>

										</ls:matrixLayout>


									</sap:panel>
								</sap:panelTabbed>
							</ls:matrixLayoutCell>
						</ls:matrixLayoutRow>
					</ls:matrixLayout>
				</ls:panel>

			</h:form>
		</ls:page>
	</sap:body>
	</sap:html>

</f:view>
