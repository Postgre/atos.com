<%@page pageEncoding="UTF-8"%>
<%@ page language="java"%>
<%@ taglib prefix="h" uri="http://java.sap.com/jsf/html"%>
<%@ taglib prefix="f" uri="http://java.sap.com/jsf/core"%>
<%@ taglib prefix="sap" uri="http://java.sap.com/jsf/html/extended"%>
<%@ taglib prefix="ls" uri="http://java.sap.com/jsf/html/internal"%>
<%@ taglib prefix="icon" uri="http://www.sap.com/sapme/ui/icons"%>

<f:subview id="examplePlugin">
	  <sap:panel id="reportsPanel" width="100%" height="100%" isCollapsible="false"
               contentAreaDesign="transparent" isCollapsed="false">
        <f:facet name="header">
            <h:outputText value="Work Instructions" />
        </f:facet>
		<f:facet name="toolbar">
            <sap:toolBar>
              <f:facet name="rightAlignedItems">
                    <h:panelGroup>
                       
                    </h:panelGroup>
              </f:facet>
            </sap:toolBar>
        </f:facet>

        <f:attribute name="sap-delta-id" value="#{sap:toClientId('reportsPanel')}" />

        <sap:panelGrid width="100%" height="100%" cellHalign="start" cellValign="top">

            <sap:panelRow cellHalign="start" cellValign="top" >
                <sap:panelGroup backgroundDesign="transparent" width="100%" height="100%" halign="start" valign="top">
                    <sap:panelGrid width="100%" height="100%" cellHalign="start" cellValign="top">

                       

                        <sap:panelRow cellHalign="start" cellValign="top">
                            <sap:panelGroup backgroundDesign="transparent" width="100%" height="100%" halign="start" valign="top">
                                <sap:panel id="resultContainer" isCollapsible="false" contentAreaDesign="transparent" borderDesign="none" width="100%" height="100%" >
                                    <sap:panelGrid id="htmlGrid" columns="1" width="100%" height="100%" cellHalign="start" cellValign="top">
                                        <h:panelGroup id="reportHtml" >
                                       
                                        							<ls:iframe id="reportFrame" url="#{laboronPlugin.reportUrl}" width="100%" height="100%" scrolling="AUTO" >
                                                                		 
                                                                	</ls:iframe>    
                                                                	
                                                                	
                                                                	
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
                    <sap:commandButton id="reportsCloseButton" value="close" actionListener="#{laboronPlugin.closePlugin}"/>                   
                </sap:panelGroup>
            </sap:panelRow>
        </sap:panelGrid>
    </sap:panel>

</f:subview>
