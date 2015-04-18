<%@page pageEncoding="UTF-8"%>
<%@ page language="java"%>
<%@ taglib prefix="fh" uri="http://java.sun.com/jsf/html"%>
<%@ taglib prefix="h" uri="http://java.sap.com/jsf/html"%>
<%@ taglib prefix="f" uri="http://java.sap.com/jsf/core"%>
<%@ taglib prefix="sap" uri="http://java.sap.com/jsf/html/extended"%>

<f:subview id="notificationMessageTestView">

    <sap:panel id="notificationMessageTestPanel" width="100%" height="100%" isCollapsible="false"
               contentAreaDesign="transparent" isCollapsed="false" binding="#{notificationMessageTestBackingBean.container}">
        <f:facet name="header">
            <h:outputText value="Notification Messages Testing" />
        </f:facet>

        <sap:panelGrid width="100%" height="100%" cellHalign="start" cellValign="top">

            <sap:panelRow cellHalign="start" cellValign="top" >
                <sap:panelGroup backgroundDesign="transparent" width="100%" halign="start" valign="top">
                    <sap:panelGrid binding="#{notificationMessageTestBackingBean.pluginMessageLayout}" width="100%"/>
                </sap:panelGroup>
            </sap:panelRow>
            
            <sap:panelRow cellHalign="start" cellValign="top" >
                <sap:panelGroup backgroundDesign="transparent" width="100%" height="100%" 
                                halign="start" valign="top" padding="0px 10px 15px 10px" >
                    <sap:inputTextarea id="messageText" readonly="true" width="100%" height="100%" value="#{notificationMessageTestPlugin.message}"/>
                </sap:panelGroup>
            </sap:panelRow>
            
            <sap:panelRow cellHalign="center" cellValign="top">

                <sap:panelGroup backgroundDesign="transparent" width="100%" halign="center" valign="top" padding="0 0 0 0">
                    <sap:panelGrid cellHalign="center" cellValign="top">                        
                        <sap:panelGroup backgroundDesign="transparent" padding="20 4" halign="left" valign="top">
                            <sap:commandButton value="Close" action="#{notificationMessageTestPlugin.closePlugin}">
                                <sap:ajaxUpdate />
                            </sap:commandButton>
                        </sap:panelGroup>
                        
                        <sap:panelGroup backgroundDesign="transparent" padding="0" halign="left" valign="top">
                            <sap:commandButtonLarge id="notificationMessagePublishButton" width="0px"
                                height="0px" action="#{notificationMessageTestPlugin.publishMessage}">
                                <sap:ajaxUpdate />
                            </sap:commandButtonLarge>
                            <fh:inputHidden id="notificationMessageCounter" value="#{notificationMessageTestPlugin.messageCount}"/>
                        </sap:panelGroup>
                    </sap:panelGrid>
                
                </sap:panelGroup>

            </sap:panelRow>            
            
        </sap:panelGrid>
    </sap:panel>

</f:subview>