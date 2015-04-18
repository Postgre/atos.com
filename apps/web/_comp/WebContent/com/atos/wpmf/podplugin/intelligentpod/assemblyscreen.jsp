<%@page pageEncoding="UTF-8"%>
<%@ page language="java"%>
<%@ taglib prefix="h" uri="http://java.sap.com/jsf/html"%>
<%@ taglib prefix="f" uri="http://java.sap.com/jsf/core"%>
<%@ taglib prefix="sap" uri="http://java.sap.com/jsf/html/extended"%>
<%@ taglib prefix="icon" uri="http://www.sap.com/sapme/ui/icons"%>

<f:subview id="examplePlugin2">
	  <sap:panel id="reportsPanel2" width="100%" height="100%" isCollapsible="false"
               contentAreaDesign="transparent" isCollapsed="false">
        <f:facet name="header">
            <h:outputText value="Assembly Status" />
        </f:facet>
		

        <f:attribute name="sap-delta-id" value="#{sap:toClientId('reportsPanel2')}" />

        <sap:panelGrid width="100%" height="100%" cellHalign="start" cellValign="top">

            <sap:panelRow cellHalign="start" cellValign="top" >
                <sap:panelGroup backgroundDesign="transparent" width="100%" height="100%" halign="start" valign="top">
                    <sap:panelGrid width="100%" height="100%" cellHalign="start" cellValign="top">

                       

                        <sap:panelRow cellHalign="start" cellValign="top">
                            <sap:panelGroup backgroundDesign="transparent" width="100%" height="100%" halign="start" valign="top">
                                <sap:panel id="resultContainer2" isCollapsible="false" contentAreaDesign="transparent" borderDesign="none" width="100%" height="100%" >
                                    <sap:panelGrid id="htmlGrid2" columns="1" width="100%" height="100%" cellHalign="start" cellValign="top">
                                        <h:panelGroup id="reportHtml2" >
                                        		<sap:dataTable													
														value="#{intelligentPodAssemblyPlugin.assemblyDataItems}"
														first="0" var="rows" id="materialCustomDataTable1"
														width="100%" height="100%" columnReorderingEnabled="true"
														rendered="true">
														<sap:column>
															<f:facet name="header">
																<h:outputText value="Assemly" />
															</f:facet>
															<sap:outputLabel id="shopOrderinputText1"
																value="#{rows.assemblyName}" />

														</sap:column>
														<sap:column>
															<f:facet name="header">
																<h:outputText value="Quantity Available" />
															</f:facet>
															<sap:outputLabel id="bomQuantityText"
																value="#{rows.bomQuantity}" />

														</sap:column>
														<sap:column>
															<f:facet name="header">
																<h:outputText value="Assembled Quantity" />
															</f:facet>
															<sap:outputLabel id="assemblyQuantityText"
																value="#{rows.assemblyQuantity}" />

														</sap:column>
														<sap:column>
															<f:facet name="header">
																<h:outputText value="Assembled" />
															</f:facet>
															<h:graphicImage url="/com/atos/icons/#{rows.assemblyStatus}.png" height="10" width="10"  />
																																												
														</sap:column>
														
													</sap:dataTable>       	
                                        </h:panelGroup>
                                   </sap:panelGrid>
                                </sap:panel>
                            </sap:panelGroup>
                        </sap:panelRow>
                    </sap:panelGrid>
                </sap:panelGroup>
            </sap:panelRow>

            <sap:panelRow cellHalign="center" cellValign="top">
                <sap:panelGroup backgroundDesign="transparent" width="100%" padding="4px 0" halign="middle" valign="top">
                    <sap:commandButton id="reportsCloseButton2" value="Close" action="#{intelligentPodAssemblyPlugin.closePlugin}"/>                   
                </sap:panelGroup>
            </sap:panelRow>
        </sap:panelGrid>
    </sap:panel>

</f:subview>
