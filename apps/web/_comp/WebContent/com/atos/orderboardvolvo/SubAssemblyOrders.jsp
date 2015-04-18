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
				font-weight: bold;
			}
			.textStyle {
				font-size: 12px;
				font-family: "Times New Roman";
			}		
		</style>
			
	</sap:head>

	<sap:body title="Sub Assembly Order Status" id="body" height="100%" browserHistory="disabled" focusId="orderReleaseForm">
		<!-- To Localize Title use gapiI18nTransformer['KEY']-->
		<h:form id="subAssemblyForm">
				<f:attribute name="height" value="100%" />
				<!-- defines the form that will be updated when "delta" submits are made to the server -->
				<f:attribute name="sap-delta-id"
					value="#{sap:toClientId('orderReleaseForm')}" />
				<sap:panel id="subAssPanel1" width="100%" height="100%"
				isCollapsible="false" contentAreaDesign="transparent" isCollapsed="false">
					<f:attribute name="sap-delta-id"
						value="#{sap:toClientId('subAssPanel1')}" />
					<f:facet name="header">
										<!-- Panel title -->
										<h:outputText value="Sub Assembly Order Status" />
									</f:facet>
					<sap:panelGrid width="100%" height="100%" cellHalign="start"
					cellValign="top" columns="1">
						<sap:panelGroup backgroundDesign="transparent" width="100%"
						halign="start" valign="middle" height="10%" padding="5 5 5 5" >
						<table>
						<tr>
						<td>
						<!-- Label * site : -->
								<fh:outputLabel value="Site " styleClass="labelStyle"></fh:outputLabel>
								<fh:outputLabel value=" "></fh:outputLabel>
								<fh:outputLabel id="materialInput1"
									value="#{subAssemblyOrderBeanVolvo.site}" styleClass="textStyle"/>
								
						
						
						</td>
						<td width="75px">
								<fh:outputLabel value=" "></fh:outputLabel>
								<fh:outputLabel value=" "></fh:outputLabel>						
						</td>
						<td>
								<!-- Label * material : -->
								<fh:outputLabel value="Material" styleClass="labelStyle"></fh:outputLabel>
								<fh:outputLabel value=" "></fh:outputLabel>
								<fh:outputLabel id="materialInput2"
									value="#{subAssemblyOrderBeanVolvo.material}" styleClass="textStyle"/>
								
																		
						</td>
						<td width="75px">
							<fh:outputLabel value=" "></fh:outputLabel>
							<fh:outputLabel value=" "></fh:outputLabel>
						
						</td>
						<td>							
								<!-- Label * Schedule Start Date:-->
								<fh:outputLabel value="Planned Start Date " styleClass="labelStyle"></fh:outputLabel>
								<fh:outputLabel value=" "></fh:outputLabel>
								<fh:outputLabel id="materialInput3"
									value="#{subAssemblyOrderBeanVolvo.scheduleStartDate}" styleClass="textStyle"/>
								
												
						</td>
						<td width="75px">
								<fh:outputLabel value=" "></fh:outputLabel>
								<fh:outputLabel value=" "></fh:outputLabel>							
						</td>	
						<td>
								<!-- Label * Schedule End Date:-->
								<fh:outputLabel value="Planned End Date " styleClass="labelStyle"></fh:outputLabel>
								<fh:outputLabel value=" "></fh:outputLabel>
								<fh:outputLabel id="materialInput4"
									value="#{subAssemblyOrderBeanVolvo.scheduleEndDate}" styleClass="textStyle"/>
								
																
						</td>
						<td width="75px">
								<fh:outputLabel value=" "></fh:outputLabel>
								<fh:outputLabel value=" "></fh:outputLabel>							
						</td>	
						<td>
								<!-- Label * Parent Shop Order:-->
								<fh:outputLabel value="Parent Shop Order " styleClass="labelStyle"></fh:outputLabel>
								<fh:outputLabel value=" "></fh:outputLabel>
								<fh:outputLabel value="#{subAssemblyOrderBeanVolvo.parentShopOrder}" styleClass="textStyle"></fh:outputLabel>
								<fh:outputLabel value=" "></fh:outputLabel>
																
						</td>	
						</tr>
						
						</table>															
				</sap:panelGroup>
				<sap:panel id="subAssdisplayPanel"  width="100%" height="100%"
								isCollapsible="false" contentAreaDesign="transparent"
								isCollapsed="false">
								
									<f:facet name="header">
										<!-- Panel title -->
										<h:outputText value="Sub Assembly Releasable Orders " />
									</f:facet>
									<!-- The ajaxUpdate component enables server side ajax updates of specified UI components -->
									<sap:ajaxUpdate render="#{sap:toClientId('subAssdisplayPanel')}"/>
									<sap:dataTable
										value="#{subAssemblyOrderBeanVolvo.subAsemblyCustomDataItemList}"
										first="0" var="rows" id="subAssCustomDataTable" width="100%"
										height="100%" columnReorderingEnabled="true" rendered="true" >
										<sap:column>
											<f:facet name="header">
												<h:outputText value="Material" style="float:right;" />
											</f:facet>
											<sap:outputLabel id="subOutputText1" value="#{rows.material}"></sap:outputLabel>
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
											<sap:outputLabel id="outputText9"
												value="#{rows.qtyAvailable}"></sap:outputLabel>										
										</sap:column>
										<sap:column>
											<f:facet name="header">
												<h:outputText value="Quantity To Be Built" />
											</f:facet>
											<sap:outputLabel id="outputText10"
												value="#{rows.qtytobebuild}"></sap:outputLabel>
										</sap:column>
										<sap:column>
										<div style="text-align: center; vertical-align: middle">									
											<f:facet name="header">
												<h:outputText value="Subassembly Status" />
											</f:facet>
											<fh:commandButton   style="#{rows.buttonStyle}" alt="#{rows.subassStatus}">
											</fh:commandButton>
										</div>
										</sap:column>
										</sap:dataTable>						
							
							</sap:panel>				
				</sap:panelGrid>
				</sap:panel>
				</h:form>
		</sap:body>
	</sap:html>

</f:view>
