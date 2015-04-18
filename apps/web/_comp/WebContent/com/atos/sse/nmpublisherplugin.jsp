<%@page pageEncoding="UTF-8"%>
<%@ page language="java"%>
<%@ taglib prefix="h" uri="http://java.sap.com/jsf/html"%>
<%@ taglib prefix="f" uri="http://java.sap.com/jsf/core"%>
<%@ taglib prefix="sap" uri="http://java.sap.com/jsf/html/extended"%>

<f:subview id="nmPublisherView">


    <sap:panel id="nmPublisherPanel" width="100%" height="100%" isCollapsible="false"
               contentAreaDesign="transparent" isCollapsed="false" binding="#{nmPublisherBackingBean.container}">
        <f:facet name="header">
            <h:outputText value="Notification Message Publisher" />
        </f:facet>

        <sap:panelGrid width="100%" height="100%" cellHalign="start" cellValign="top">

            <sap:panelRow cellHalign="start" cellValign="top" >
                <sap:panelGroup backgroundDesign="transparent" width="100%" halign="start" valign="top">
                    <sap:panelGrid binding="#{nmPublisherBackingBean.pluginMessageLayout}" width="100%"/>
                </sap:panelGroup>
            </sap:panelRow>
            
            <sap:panelRow cellHalign="start" cellValign="top" >
                <sap:panelGroup id="messageArea" backgroundDesign="transparent" width="100%" height="100%" 
                                halign="start" valign="top" padding="0px 10px 15px 10px" >
                    <sap:inputTextarea id="messageText" width="100%" height="100%" value="#{nmPublisherPlugin.message}"/>
                </sap:panelGroup>
            </sap:panelRow>
            
            <sap:panelRow cellHalign="center" cellValign="top">

                <sap:panelGroup id="nmPublisherToolbar" backgroundDesign="transparent" width="100%" halign="center" valign="top" padding="0 0 0 0">
                    <sap:panelGrid cellHalign="center" cellValign="top">

                        <sap:panelGroup backgroundDesign="transparent" padding="20 4" halign="right" valign="top">
                            <sap:commandButton value="Send Message" action="#{nmPublisherPlugin.processSend}">
                                <sap:ajaxUpdate render="#{sap:toClientId('nmPublisherPanel')}"/>
                            </sap:commandButton>
                        </sap:panelGroup>
                        
                        <sap:panelGroup backgroundDesign="transparent" padding="20 4" halign="left" valign="top">
                            <sap:commandButton value="Close" action="#{nmPublisherPlugin.closePlugin}">
                                <sap:ajaxUpdate />
                            </sap:commandButton>
                        </sap:panelGroup>
                        
                    </sap:panelGrid>
                
                </sap:panelGroup>

            </sap:panelRow>

        </sap:panelGrid>
    </sap:panel>

</f:subview>
