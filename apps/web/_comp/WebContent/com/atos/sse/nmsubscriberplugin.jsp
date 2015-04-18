<%@page pageEncoding="UTF-8"%>
<%@ page language="java"%>
<%@ taglib prefix="h" uri="http://java.sap.com/jsf/html"%>
<%@ taglib prefix="f" uri="http://java.sap.com/jsf/core"%>
<%@ taglib prefix="sap" uri="http://java.sap.com/jsf/html/extended"%>

<f:subview id="nmSubscriberView">


    <sap:panel id="nmSubscriberPanel" width="100%" height="100%" isCollapsible="false"
               contentAreaDesign="transparent" isCollapsed="false" binding="#{nmSubscriberBackingBean.container}">
        <f:facet name="header">
            <h:outputText value="Notification Message Subscriber" />
        </f:facet>

        <sap:panelGrid width="100%" height="100%" cellHalign="start" cellValign="top">

            <sap:panelRow cellHalign="start" cellValign="top" >
                <sap:panelGroup backgroundDesign="transparent" width="100%" halign="start" valign="top">
                    <sap:panelGrid binding="#{nmSubscriberBackingBean.pluginMessageLayout}" width="100%"/>
                </sap:panelGroup>
            </sap:panelRow>
            
            <sap:panelRow cellHalign="start" cellValign="top" >
                <sap:panelGroup backgroundDesign="transparent" width="100%" height="100%" 
                                halign="start" valign="top" padding="0px 10px 15px 10px" >
                    <sap:inputTextarea id="messageText" readonly="true" width="100%" height="100%" value="#{nmSubscriberPlugin.message}"/>
                </sap:panelGroup>
            </sap:panelRow>
            
            <sap:panelRow cellHalign="center" cellValign="top">

                <sap:panelGroup id="nmPublisherToolbar" backgroundDesign="transparent" width="100%" halign="center" valign="top" padding="0 0 0 0">
                    <sap:panelGrid cellHalign="center" cellValign="top">                        
                        <sap:panelGroup backgroundDesign="transparent" padding="20 4" halign="left" valign="top">
                            <sap:commandButton value="Close" action="#{nmSubscriberPlugin.closePlugin}">
                                <sap:ajaxUpdate />
                            </sap:commandButton>
                        </sap:panelGroup>
                        
                    </sap:panelGrid>
                
                </sap:panelGroup>

            </sap:panelRow>            
            
        </sap:panelGrid>
    </sap:panel>

</f:subview>