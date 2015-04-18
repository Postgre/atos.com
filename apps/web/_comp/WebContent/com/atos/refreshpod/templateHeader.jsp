<%@page pageEncoding="UTF-8"%>
<%@ page language="java"%>
<%@ taglib prefix="fh" uri="http://java.sun.com/jsf/html"%>
<%@ taglib prefix="h" uri="http://java.sap.com/jsf/html"%>
<%@ taglib prefix="f" uri="http://java.sap.com/jsf/core"%>
<%@ taglib prefix="sap" uri="http://java.sap.com/jsf/html/extended"%>
<%@ taglib prefix="icon" uri="http://www.sap.com/sapme/ui/icons"%>

<f:subview id="headerView">

    <sap:pageHeader id="headerContainer" >
        <f:facet name="header">
            <h:outputText id="pageHeaderTitle" value="#{frameworkManagerBean.podTitle}" />
        </f:facet>

        <f:facet name="headerRightAlignedArea">

            <sap:panelGrid cellBackgroundDesign="transparent" width="100%" cellHalign="start" cellValign="middle" columns="4">

                <sap:panelGroup backgroundDesign="transparent" padding="0 20 0 0">
                    <h:commandButton id="home" image="#{icon:podHeaderUrl('home')}"
                                       alt="#{gapiI18nTransformer['Home.button.TEXT']}"
                                       title="#{gapiI18nTransformer['Home.button.TEXT']}"
                                       actionListener="#{frameworkManagerBean.reloadAction}">
                        <sap:ajaxUpdate />
                    </h:commandButton>
                </sap:panelGroup>

                <sap:panelGroup backgroundDesign="transparent" padding="0 20 0 0" rendered="#{frameworkManagerBean.logoutRendered}">

                    <h:commandButton id="mainLogout" rendered="#{frameworkManagerBean.logoutRendered}"
                                       image="#{icon:podHeaderUrl('logout')}"
                                       alt="#{gapiI18nTransformer['podSfcSelect.logout.TOOLTIP']}"
                                       title="#{gapiI18nTransformer['podSfcSelect.logout.TOOLTIP']}">
                        <sap:ajaxUpdate onevent="return window.doLogout()"/>
                    </h:commandButton>
                </sap:panelGroup>

                <sap:panelGroup backgroundDesign="transparent" padding="0 20 0 0">
                    <h:commandButton id="mainHelp" image="#{icon:podHeaderUrl('help')}"
                                       alt="#{gapiI18nTransformer['toolTip.help.TEXT']}"
                                       title="#{gapiI18nTransformer['toolTip.help.TEXT']}"
                                       actionListener="#{frameworkManagerBean.mainHelpAction}">
                        <sap:ajaxUpdate />
                    </h:commandButton>
                </sap:panelGroup>

                <sap:panelGroup backgroundDesign="transparent" padding="0 20 0 0" rendered="#{rtedMessagePlugin.displayIcon}">
                    <h:commandButton id="rtedLink" rendered="#{rtedMessagePlugin.displayIcon}"
                                       image="#{rtedMessagePlugin.rtemImageUrl}"
                                       alt="#{rtedMessagePlugin.toolTip}"
                                       title="#{rtedMessagePlugin.toolTip}"
                                       actionListener="#{rtedMessagePlugin.openRtemAction}">
                        <sap:ajaxUpdate />
                    </h:commandButton>
                </sap:panelGroup>

            </sap:panelGrid>
        </f:facet>

        <sap:ajaxUpdate render="#{sap:toClientId('headerContainer')}" />

        <sap:panelGrid cellBackgroundDesign="transparent" width="100%" height="0px" cellHalign="start" cellValign="top" columns="1">

            <sap:panelGroup backgroundDesign="transparent" width="100%" height="0px">


                <sap:poll id="timeTrigger" rendered="#{rtedMessagePlugin.enableRTEDTimeTrigger}"
                                    interval="#{rtedMessagePlugin.pollingPeriod}"
                                    actionListener="#{rtedMessagePlugin.pollRTEMState}" >
                    <sap:ajaxUpdate render="#{sap:toClientId('headerContainer')}" 
                                    onevent="window.startPolling();"/>
                </sap:poll>
            </sap:panelGroup>

        </sap:panelGrid>

    </sap:pageHeader>
</f:subview>
