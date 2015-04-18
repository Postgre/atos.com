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
.mystyle1 {
	color: black;
	background-color: #CCCCFF;
}
</style>
	</sap:head>

	<sap:body title="Extended Pattern Generation" id="body" height="100%">
		<!-- To Localize Title use gapiI18nTransformer['KEY']-->		
			<h:form id="materialCustomDataForm">
				<f:attribute name="height" value="100%" />
				<!-- defines the form that will be updated when "delta" submits are made to the server -->
				<f:attribute name="sap-delta-id"
					value="#{sap:toClientId('materialCustomDataForm')}" />
				<sap:panel id="fieldButtonPanel"
					title="Generic Pattern"
					width="100%" height="100%" 
					isCollapsible="false" 
					headerDesign="STANDARD" 
					borderDesign="NONE"  contentAreaDesign="transparent" isCollapsed="false">
					<f:facet name="header123">
					<fh:outputLabel value="Pattern Generation" ></fh:outputLabel>
				</f:facet>
				<f:facet name="header">
										<!-- Panel title -->
										<h:outputText value="Extended Pattern Generation" />
									</f:facet>
				<!-- The ajaxUpdate component enables server side ajax updates of specified UI components -->
                <sap:ajaxUpdate render="#{sap:toClientId('fieldButtonPanel')}"/>
                <sap:panelGrid width="100%" height="30%" columns="1" id="selectionPanelLayout">
							<sap:panelGroup id="cell22"	width="100%" backgroundDesign="transparent" halign="start" valign="middlare" height="3%" >
							    <div class="mystyle1">
								<h:commandLink value="Retrieve" id="commandLink1"
									action="#{genericPatternCustomDataBean.readMaterialCustomData}" />
								<fh:outputLabel value=" "></fh:outputLabel>
								<h:commandLink value="Save" id="commandLink2"
									action="#{genericPatternCustomDataBean.savePattern}" />
								<fh:outputLabel value=" "></fh:outputLabel>
								<h:commandLink value="Clear" id="commandLink3"
									action="#{genericPatternCustomDataBean.clear}" />
								<fh:outputLabel value=" "></fh:outputLabel>
								<h:commandLink value="Delete" id="commandLink4"
									action="#{genericPatternCustomDataBean.deletePattern}" />
								</div>
							</sap:panelGroup>
						<sap:panelGroup backgroundDesign="transparent" width="100%"
						halign="start" valign="middle" height="3%" padding="5 5 5 5" >
						<table>
						<tr>
						<td>
								<fh:outputText id="messageBar" value="#{genericPatternCustomDataBean.message}" rendered="false"/>
								<fh:outputLabel value=" "></fh:outputLabel>
								<fh:outputLabel value=" "></fh:outputLabel>
								<sap:ajaxUpdate/>
						</td>
						</tr>
						</table>								
						</sap:panelGroup>
						<sap:panelGroup id="rowId1"	width="100%" backgroundDesign="transparent" halign="center" valign="middle" height="12%" >
								<div align="center">
								<sap:outputLabel value="Pattern Name" width="120px" align="right"></sap:outputLabel>
								<sap:commandInputText
									id="materialInput"
									value="#{genericPatternCustomDataBean.patternName}"
									submitOnFieldHelp="false" submitOnTabout="false"
									submitOnChange="false"
									disabled="#{genericPatternCustomDataBean.disablePatternType}">
									<f:attribute name="upperCase" value="true" />
								</sap:commandInputText> <fh:outputLabel value=" "></fh:outputLabel> <h:commandButton
									style="height:30px;width:30px;"
									image="/com/atos/icons/select.png"
									id="patterCommandButton1"
									actionListener="#{genericPatternCustomDataBean.showMaterialBrowse}"
									disabled="#{genericPatternCustomDataBean.disablePatternType}">
								</h:commandButton> <sap:panelPopup id="materialBrowsePopup"
									rendered="#{genericPatternCustomDataBean.materialBrowseRendered}"
									mode="modeless" height="400px" width="400px">
									<f:facet name="header">
										<h:outputText value="Browse for Pattern" />
									</f:facet>
									<f:facet name="popupButtonArea">
										<h:panelGroup>
											<h:commandButton value="OK"
												actionListener="#{genericPatternCustomDataBean.rowSelected}"></h:commandButton>
											<h:commandButton value="Cancel"
												actionListener="#{genericPatternCustomDataBean.closeMaterialBrowse}"></h:commandButton>
										</h:panelGroup>
									</f:facet>
									<sap:panelGrid width="100%" height="100%"
										cellpadding="10px 10px 0px 10px">
										<sap:dataTable
											value="#{genericPatternCustomDataBean.patternList}"
											var="materialBrowseVar" id="materialBrowseTable" width="100%"
											height="100%" columnReorderingEnabled="true">
											<sap:rowSelector
												value="#{genericPatternCustomDataBean.selected}"
												id="rowSelector" selectionMode="single"
												selectionBehaviour="client"
												actionListener="#{genericPatternCustomDataBean.rowSelected}"
												submitOnRowDoubleClick="true" />
											<sap:column id="materialColumn" position="0">
												<f:facet name="header">
													<h:outputText id="materialHeaderText"
														value="Pattern Name" />
												</f:facet>
												<h:outputText value="#{materialBrowseVar.value}"
													id="materialText" />
											</sap:column>
										</sap:dataTable>
									</sap:panelGrid>
								</sap:panelPopup></div>
							</sap:panelGroup>
						<sap:panelGroup id="rowId2" width="100%" backgroundDesign="transparent" halign="center" valign="middle" height="12%" >
								<table>
									<tr>
										<td align="right">
										<sap:outputLabel value="Pattern Type" width="120px" align="right"></sap:outputLabel>
										</td>
										<td align="left"><sap:commandInputText id="patternType"
											value="#{genericPatternCustomDataBean.patternType}"
											actionListener="#{genericPatternCustomDataBean.activateOnPatternType}"
											submitOnFieldHelp="false"
											disabled="#{genericPatternCustomDataBean.disablePatternType}">
										</sap:commandInputText> <fh:outputLabel value=" "></fh:outputLabel> <h:commandButton
											style="height:30px;width:30px;"
											image="/com/atos/icons/select.png"
											id="patterCommandButton2"
											actionListener="#{genericPatternCustomDataBean.showPatternTypeBrowse}"
											disabled="#{genericPatternCustomDataBean.disablePatternType}">
										</h:commandButton> <sap:panelPopup id="patternTypePopup"
											rendered="#{genericPatternCustomDataBean.renderPatternTypePopup}"
											mode="modeless" height="400px" width="400px">
											<f:facet name="header">
												<h:outputText value="Browse for Pattern Type" />
											</f:facet>
											<f:facet name="popupButtonArea">
												<h:panelGroup>
													<h:commandButton value="OK"
														actionListener="#{genericPatternCustomDataBean.rowSelectedPatternTypePopup}"></h:commandButton>
													<h:commandButton value="Cancel"
														actionListener="#{genericPatternCustomDataBean.closePatternTypeBrowse}"></h:commandButton>
												</h:panelGroup>
											</f:facet>
											<sap:panelGrid width="100%" height="100%"
												cellpadding="10px 10px 0px 10px">
												<sap:dataTable
													value="#{genericPatternCustomDataBean.patternTypeList}"
													var="patternTypeBrowseVar" id="patternTypeBrowseTable"
													width="100%" height="100%" columnReorderingEnabled="true">
													<sap:rowSelector value="#{patternTypeBrowseVar.selected}"
														id="patternTypeRowSelector" selectionMode="single"
														selectionBehaviour="client"
														actionListener="#{genericPatternCustomDataBean.rowSelectedPatternTypePopup}"
														submitOnRowDoubleClick="true" />
													<sap:column id="patternTypeColumn" position="0">
														<f:facet name="header">
															<h:outputText id="patternTypeHeaderText"
																value="Pattern Type" />
														</f:facet>
														<h:outputText value="#{patternTypeBrowseVar.value}"
															id="patternTypeText" />
													</sap:column>
												</sap:dataTable>
											</sap:panelGrid>
										</sap:panelPopup></td>
									</tr>
								</table>
							</sap:panelGroup>
						<sap:panelGroup id="rowId3" width="100%" backgroundDesign="transparent" halign="center" valign="middle" height="12%" >
								<div align="center">								
								<sap:outputLabel value="Pattern Type Master" width="120px" align="right"></sap:outputLabel>
								 <sap:commandInputText
									id="patternTypeMaster"
									value="#{genericPatternCustomDataBean.patternTypeMaster}"
									submitOnFieldHelp="false"
									disabled="#{genericPatternCustomDataBean.disablePatternTypeMaster || genericPatternCustomDataBean.disablePatternType}">
								</sap:commandInputText> 
								<fh:outputLabel value=" "></fh:outputLabel> 
								<h:commandButton
									style="height:30px;width:30px;"
									image="/com/atos/icons/select.png"
									id="patterCommandButton3"
									disabled="#{genericPatternCustomDataBean.disablePatternTypeValue || genericPatternCustomDataBean.disablePatternType}"
									actionListener="#{genericPatternCustomDataBean.showPatternTypeMasterBrowse}">
								</h:commandButton> <sap:panelPopup id="patternTypeMasterBrowsePopup"
									rendered="#{genericPatternCustomDataBean.renderPatternTypeMasterPopup}"
									mode="modeless" height="400px" width="400px">
									<f:facet name="header">
										<h:outputText value="Browse for Pattern Type Master" />
									</f:facet>
									<f:facet name="popupButtonArea">
										<h:panelGroup>
											<h:commandButton value="OK"
												actionListener="#{genericPatternCustomDataBean.rowSelectedPatternTypeMasterPopup}"></h:commandButton>
											<h:commandButton value="Cancel"
												actionListener="#{genericPatternCustomDataBean.closePatternTypeMasterBrowse}"></h:commandButton>
										</h:panelGroup>
									</f:facet>
									<sap:panelGrid width="100%" height="100%"
										cellpadding="10px 10px 0px 10px">
										<sap:dataTable
											value="#{genericPatternCustomDataBean.patternTypeMasterList}"
											var="patternTypeMasterBrowseVar"
											id="patternTypeMasterBrowseTable" width="100%" height="100%"
											columnReorderingEnabled="true">
											<sap:rowSelector
												value="#{genericPatternCustomDataBean.patternTypeMasterSelected}"
												id="patternTypeMasterRowSelector" selectionMode="single"
												selectionBehaviour="client"
												actionListener="#{genericPatternCustomDataBean.rowSelectedPatternTypeMasterPopup}"
												submitOnRowDoubleClick="true" />
											<sap:column id="patternTypeMasterColumn" position="0">
												<f:facet name="header">
													<h:outputText id="patternTypeMasterHeaderText"
														value="Pattern Type Master" />
												</f:facet>
												<h:outputText value="#{patternTypeMasterBrowseVar.value}"
													id="patternTypeMasterText" />
											</sap:column>
										</sap:dataTable>
									</sap:panelGrid>
								</sap:panelPopup></div>
							</sap:panelGroup>
						<sap:panelGroup id="rowId4" width="100%" backgroundDesign="transparent" halign="center" valign="middle" height="12%" >
								<div align="center">
								<sap:outputLabel value="Pattern Type Value" width="120px" align="right"></sap:outputLabel>
								<sap:commandInputText id="patternTypeValue"
									value="#{genericPatternCustomDataBean.patternTypeValue}"
									submitOnFieldHelp="false"
									disabled="#{genericPatternCustomDataBean.disablePatternTypeValue || genericPatternCustomDataBean.disablePatternType}">
								</sap:commandInputText> <fh:outputLabel value=" "></fh:outputLabel> <h:commandButton
									style="height:30px;width:30px;"
									image="/com/atos/icons/select.png"
									id="patterCommandButton4"
									actionListener="#{genericPatternCustomDataBean.showPatternTypeValueBrowse}"
									disabled="#{genericPatternCustomDataBean.disablePatternTypeValue || genericPatternCustomDataBean.disablePatternType}">
								</h:commandButton> <sap:panelPopup id="patternTypeValueBrowsePopup"
									rendered="#{genericPatternCustomDataBean.renderPatternTypeValuePopup}"
									mode="modeless" height="400px" width="400px">
									<f:facet name="header">
										<h:outputText value="Browse for Pattern Type Master" />
									</f:facet>
									<f:facet name="popupButtonArea">
										<h:panelGroup>
											<h:commandButton value="OK"
												actionListener="#{genericPatternCustomDataBean.rowSelectedPatternTypeValuePopup}"></h:commandButton>
											<h:commandButton value="Cancel"
												actionListener="#{genericPatternCustomDataBean.closePatternTypeValueBrowse}"></h:commandButton>
										</h:panelGroup>
									</f:facet>
									<sap:panelGrid width="100%" height="100%"
										cellpadding="10px 10px 0px 10px">
										<sap:dataTable
											value="#{genericPatternCustomDataBean.patternTypeValueList}"
											var="patternTypeValueBrowseVar"
											id="patternTypeValueBrowseTable" width="100%" height="100%"
											columnReorderingEnabled="true">
											<sap:rowSelector
												value="#{genericPatternCustomDataBean.patternTypeValueSelected}"
												id="patternTypeValueRowSelector" selectionMode="single"
												selectionBehaviour="client"
												actionListener="#{genericPatternCustomDataBean.rowSelectedPatternTypeValuePopup}"
												submitOnRowDoubleClick="true" />
											<sap:column id="patternTypeValueColumn" position="0">
												<f:facet name="header">
													<h:outputText id="patternTypeValueHeaderText"
														value="Pattern Type Master" />
												</f:facet>
												<h:outputText value="#{patternTypeValueBrowseVar.value}"
													id="patternTypeValueText" />
											</sap:column>
										</sap:dataTable>
									</sap:panelGrid>
								</sap:panelPopup></div>
							</sap:panelGroup>						
					</sap:panelGrid>	
					<sap:panelGrid width="100%" height="70%" columns="1" id="displayPanelLayout">		
					<sap:panelGroup id="rowId5" width="100%" backgroundDesign="transparent" halign="center" valign="middle" height="100%" >
							<sap:panel  id="displayPanel" width="100%"
								height="100%" isCollapsible="false"
								 headerDesign="TRANSPARENT"
								 borderDesign="NONE" contentAreaDesign="transparent">
								 <div class="mystyle1">
								<f:attribute name="sap-delta-id"
									value="#{sap:toClientId('displayPanel')}" />
								<h:commandLink id="InsertNew" value="Insert New"
									action="#{genericPatternCustomDataBean.insertNewRow}">
									<sap:ajaxUpdate />
								</h:commandLink>
								<sap:outputLabel value="  " ></sap:outputLabel>
								<h:commandLink id="insertBefore" value="Insert Before"
									action="#{genericPatternCustomDataBean.insertNewRowBefore}">
									<sap:ajaxUpdate />
								</h:commandLink>
								<sap:outputLabel value="  " ></sap:outputLabel>
								<h:commandLink id="insertAfter" value="Insert After"
									action="#{genericPatternCustomDataBean.insertNewRowAfter}">
									<sap:ajaxUpdate />
								</h:commandLink>
								<sap:outputLabel value="  " ></sap:outputLabel>
								<h:commandLink id="DeleteSelected" value="Remove Selected"
									action="#{genericPatternCustomDataBean.deletSelectedRow}">
									<sap:ajaxUpdate />
								</h:commandLink>
								<sap:outputLabel value="  " ></sap:outputLabel>
								<h:commandLink id="removeAll" value="Remove All"
									action="#{genericPatternCustomDataBean.deleteAll}">
									<sap:ajaxUpdate />
								</h:commandLink>															
									<sap:dataTable
										value="#{genericPatternCustomDataBean.patternCustomDataList}"
										first="0" var="rows" id="materialCustomDataTable" width="100%"
										height="100%" columnReorderingEnabled="true" rendered="true">
										<sap:rowSelector value="#{rows.select}" id="selector123"
											selectionMode="multiTouch" selectionBehaviour="client"
											actionListener="#{genericPatternCustomDataBean.rowSelectedPatternCustomItemDataTable}"
											submitOnRowDoubleClick="true" disabled="#{!rows.editable}" />
										<sap:column id="column21" position="0">
											<f:facet name="header">
												<h:outputText id="HeaderText21" value="Sequence No" />
											</f:facet>
											<sap:commandInputText id="inputText21"
												value="#{rows.sequenceNo}" submitOnFieldHelp="false"
												disabled="#{!rows.editable}" />
										</sap:column>
										<sap:column id="column22" position="0">
											<f:facet name="header">
												<h:outputText id="HeaderText22" value="Pattern Attribute" />
											</f:facet>
											<sap:commandInputText id="inputText22"
												value="#{rows.patternAttribute}" submitOnFieldHelp="true"
												actionListener="#{genericPatternCustomDataBean.showPatternAttributeBrowse}"
												disabled="#{(!rows.editable)}" />
										</sap:column>
										<sap:column id="column23" position="0">
											<f:facet name="header">
												<h:outputText id="HeaderText23" value="Attribute Value" />
											</f:facet>
											<sap:commandInputText id="inputText23"
												value="#{rows.attributeValue}" submitOnFieldHelp="false"
												disabled="#{!rows.editable}" />
										</sap:column>
										<sap:column id="column24" position="0">
											<f:facet name="header">
												<h:outputText id="HeaderText24" value="Current Version" />
											</f:facet>
											<h:selectBooleanCheckbox value="#{rows.currentVesion}"
												id="selectBox24" disabled="#{!rows.editable}"></h:selectBooleanCheckbox>
											<sap:outputLabel value="  " ></sap:outputLabel>
											<h:graphicImage height="10px" width="10px"
												value="/com/atos/icons/exclamation.png"
												rendered="#{rows.error}" alt="#{rows.errorMessage}"></h:graphicImage>
										</sap:column>
									</sap:dataTable>
									</div>								
							</sap:panel>
						</sap:panelGroup>					
				</sap:panelGrid>
				</sap:panel>
				<sap:panelPopup id="BrowsePopup10"
									rendered="#{genericPatternCustomDataBean.renderAttributeValuePopup}"
									height="400px" width="400px">
									<f:facet name="header">
										<h:outputText value="Browse for Attribute" />
									</f:facet>
									<f:facet name="popupButtonArea">
										<h:panelGroup>
											<h:commandButton value="OK"
												actionListener="#{genericPatternCustomDataBean.rowSelectedAttributePanelPopup}"></h:commandButton>
											<h:commandButton value="Cancel"
												actionListener="#{genericPatternCustomDataBean.closeAttributeBrowse}"></h:commandButton>
										</h:panelGroup>
									</f:facet>
									<sap:panelGrid width="100%" height="100%"
										cellpadding="10px 10px 0px 10px">
										<sap:dataTable
											value="#{genericPatternCustomDataBean.patternAttributeList}"
											var="patternAttributeBrowseVar" id="BrowseTable10"
											width="100%" height="100%" columnReorderingEnabled="true">
											<sap:rowSelector
												value="#{genericPatternCustomDataBean.attributeSelected}"
												id="rowSelector10" selectionMode="single"
												selectionBehaviour="client"
												actionListener="#{genericPatternCustomDataBean.rowSelectedAttributePanelPopup}"
												submitOnRowDoubleClick="true" />
											<sap:column id="Column10" position="0">
												<f:facet name="header">
													<h:outputText id="HeaderText10"
														value="#{genericPatternCustomDataBean.columnHeading}" />
												</f:facet>
												<h:outputText value="#{patternAttributeBrowseVar.value}"
													id="Text10" />
											</sap:column>
										</sap:dataTable>
									</sap:panelGrid>
								</sap:panelPopup>
			</h:form>
		</sap:body>
	</sap:html>

</f:view>
