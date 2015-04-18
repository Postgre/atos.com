<%@page pageEncoding="UTF-8"%>
<%@ page language="java"%>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html"%>
<%@ taglib prefix="sh" uri="http://java.sap.com/jsf/html"%>
<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core"%>
<%@ taglib prefix="sap" uri="http://java.sap.com/jsf/html/extended"%>
<%@ taglib prefix="i" uri="http://www.sap.com/sapme/ui/icons"%>

<f:subview id="podSelectView">
	
    <sap:panel id="podSelection" binding="#{podSelectionHVPlugin.backingBean.container}" isCollapsible="false" width="100%" >

        <f:attribute name="sap-delta-id" value="#{sap:toClientId('podSelection')}" />

        <f:facet name="header">
            <h:outputText id="headerTitle" value="#{gapiI18nTransformer['podselection.title.TEXT']}" />
        </f:facet>
    <f:facet name="toolbar">
        <sap:toolBar>
            <f:facet name="rightAlignedItems">
                <sap:panelGroup>
                    <sap:commandButton id="helpIcon" image="#{i:url('Icon','Help')}"
                                     title="#{gapiI18nTransformer['toolTip.help.TEXT']}"
                                     action="#{podSelectionHVPlugin.showPluginHelp}">
                        <sap:ajaxUpdate />
                    </sap:commandButton>
                </sap:panelGroup>
            </f:facet>
        </sap:toolBar>
    </f:facet>

        <sap:panelGrid width="100%" cellHalign="center" cellValign="middle" cellpadding="9">
            <sap:panelRow>
                <sap:panelGroup width="80%" valign="middle">
                    <sap:panelGrid width="100%" cellHalign="center" cellValign="middle">
                        <sap:panelRow cellpadding="0 0 5 0">
                            <sap:panelGroup id="celllabelWithAsterisk1" width="25%" halign="right" valign="middle">
                                <sap:panelGrid cellHalign="right" cellValign="middle">
                                    <sap:panelGroup id="labelCell1" valign="middle" halign="right" binding="#{podSelectionHVPlugin.backingBean.outputLabelCell1}"/>
                                </sap:panelGrid>
                            </sap:panelGroup>
                            <sap:panelGroup id="fieldCell1" valign="middle" halign="left" binding="#{podSelectionHVPlugin.backingBean.commandInputFieldCell1}" width="25%" />
                            <sap:panelGroup id="celllabelWithAsterisk2" width="25%" halign="right" valign="middle">
                                <sap:panelGrid cellHalign="right" cellValign="middle">
                                    <sap:panelGroup id="labelCell2" valign="middle" halign="right" binding="#{podSelectionHVPlugin.backingBean.outputLabelCell2}"/>
                                </sap:panelGrid>
                            </sap:panelGroup>
                            <sap:panelGroup id="fieldCell2" valign="middle" halign="left" binding="#{podSelectionHVPlugin.backingBean.commandInputFieldCell2}" width="25%" />
                        </sap:panelRow>
                        <sap:panelRow>
                            <sap:panelGroup id="labelCell3" valign="middle" halign="right" binding="#{podSelectionHVPlugin.backingBean.outputLabelCell3}" width="25%" />
                            <sap:panelGroup id="fieldCell3" valign="middle" halign="left" binding="#{podSelectionHVPlugin.backingBean.commandInputFieldCell3}" width="25%" />
                            <sap:panelGroup id="labelCell4" valign="middle" halign="right" binding="#{podSelectionHVPlugin.backingBean.outputLabelCell4}" width="25%" />
                            <sap:panelGroup id="fieldCell4" valign="middle" halign="left" binding="#{podSelectionHVPlugin.backingBean.commandInputFieldCell4}" width="25%" />
                        </sap:panelRow>
                        <sap:panelRow cellpadding="0 0 5 0">
                        <sap:panelGroup id="labelCell10" valign="middle" halign="right"  width="25%" >
                        	<sap:outputLabel id="CustomLabel1"  value= "Scanned Component: "></sap:outputLabel>
                        </sap:panelGroup>                      
                            <sap:panelGroup id="customAdded1" halign="left" valign="middle" width="25%">
								<sap:panelGrid >									
                                	<sap:inputText   id="OperationConfrm" value="#{podHeaderExtensionForIntelligentPod.operationConfirmation}"  ></sap:inputText>
                                	 <sap:outputLabel id="blank1234"  value= " "></sap:outputLabel>
                                   	<sap:commandButton id="Search" title="Search" action="#{podHeaderExtensionForIntelligentPod.postScanOperation}"></sap:commandButton>
                           		</sap:panelGrid>
                            </sap:panelGroup>
                              <sap:panelGroup id="fieldCell30" valign="middle" halign="left" width="25%" />
                            <sap:panelGroup id="fieldCell40" valign="middle" halign="left"  width="25%" />
                       </sap:panelRow>
                        
                    </sap:panelGrid>
                </sap:panelGroup>
                <sap:panelGroup width="20%" valign="middle">
                    <sap:panelGrid  cellHalign="right" width="100%" cellValign="middle">
                        <sap:panelRow>
                            <sap:panelGroup padding="3 20 3 0" halign="right" valign="middle" width="100%" >
                                <table border="1">
                                    <tr>
                                        <td><table id="localT1" border="0">
                                                <tr>
                                                    <td align="right" nowrap="nowrap"><h:outputLabel id="sessionInfo1LabelView" styleClass="urTxtStd" value="#{podSelectionHVPlugin.sessionInfo1Label}" /></td>
                                                    <td align="left"><h:outputLabel id="sessionInfo1TextView" styleClass="urTxtStd" value="#{podSelectionHVPlugin.sessionInfo1Text}" /></td>
                                                </tr>
                                                <tr>
                                                    <td align="right" nowrap="nowrap"><h:outputLabel id="sessionInfo2LabelView" styleClass="urTxtStd" value="#{podSelectionHVPlugin.sessionInfo2Label}" /></td>
                                                    <td align="left"><h:outputLabel id="sessionInfo2TextView" styleClass="urTxtStd" value="#{podSelectionHVPlugin.sessionInfo2Text}" /></td>
                                                </tr>
                                            </table></td>
                                    </tr>
                                </table>
                            </sap:panelGroup>
                            <sap:panelGroup height="0px" width="0px">
                                <h:inputHidden id="OPERATION_CHANGED" binding="#{podSelectionHVPlugin.backingBean.operationChangedField}" />
                                <h:inputHidden id="RESOURCE_CHANGED" binding="#{podSelectionHVPlugin.backingBean.resourceChangedField}" />
                            </sap:panelGroup>
                        </sap:panelRow>
                    </sap:panelGrid>
                </sap:panelGroup>
            </sap:panelRow>
        </sap:panelGrid>
    </sap:panel>
</f:subview>