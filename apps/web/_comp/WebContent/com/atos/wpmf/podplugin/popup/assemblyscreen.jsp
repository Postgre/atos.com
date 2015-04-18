<%@page pageEncoding="UTF-8"%>
<%@ page language="java"%>
<%@ taglib prefix="h" uri="http://java.sap.com/jsf/html"%>
<%@ taglib prefix="f" uri="http://java.sap.com/jsf/core"%>
<%@ taglib prefix="sap" uri="http://java.sap.com/jsf/html/extended"%>
<%@ taglib prefix="ls" uri="http://java.sap.com/jsf/html/internal"%>
<%@ taglib prefix="icon" uri="http://www.sap.com/sapme/ui/icons"%>

<f:subview id="examplePlugin2">
	  <sap:panel id="reportsPanel2" width="100%" height="100%" isCollapsible="false"
               contentAreaDesign="transparent" isCollapsed="false">
        <f:facet name="header">
            <h:outputText value="Assembly Status" />
        </f:facet>
		

        <f:attribute name="sap-delta-id" value="#{sap:toClientId('reportsPanel2')}" />
       

        <sap:panelGrid width="100%" height="100%" cellHalign="start" cellValign="top">
        	<sap:panelRow>
		        <sap:panelGroup halign="start" valign="top" height="10%">
		          <sap:panelGrid binding="#{assemblyPlugin.pluginMessageLayout}" />
		        </sap:panelGroup>
      		</sap:panelRow>
			<sap:panelRow cellpadding="10 10 10 10">
                        <sap:panelGroup id="labelCell100" valign="middle" halign="middle"  width="100%" >
                        	<sap:outputLabel id="CustomLabel12"  value= "Scanned Component: "></sap:outputLabel>                        	
                        	<sap:outputLabel id="blank123456"  value= " "></sap:outputLabel>
                        	<sap:inputText   id="OperationConfrm1" value="#{assemblyPlugin.operationConfirmation}"></sap:inputText>
                            <sap:outputLabel id="blank12345"  value= " "></sap:outputLabel>
                            <sap:commandButton id="Search1" title="Search" action="#{assemblyPlugin.postScanOperation}"></sap:commandButton>
                        </sap:panelGroup>                      
             </sap:panelRow>
             <sap:panelRow cellHalign="center" cellValign="top">
                <sap:panelGroup backgroundDesign="transparent" width="100%" padding="4px 0" halign="middle" valign="top">
                    <sap:commandButton id="reportsCloseButton2" value="Submit" action="#{assemblyPlugin.closePlugin}"/>                   
                </sap:panelGroup>
            </sap:panelRow>
        </sap:panelGrid>
    </sap:panel>
</f:subview>
