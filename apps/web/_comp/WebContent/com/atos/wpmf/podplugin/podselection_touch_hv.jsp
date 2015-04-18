<%@page pageEncoding="UTF-8"%>
<%@ page language="java"%>
<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core"%>
<%@ taglib prefix="fh" uri="http://java.sun.com/jsf/html"%>
<%@ taglib prefix="h" uri="http://java.sap.com/jsf/html"%>
<%@ taglib prefix="sap" uri="http://java.sap.com/jsf/html/extended"%>

<f:subview id="podSelectView">

    <sap:panel id="podSelection" binding="#{podSelectionHVPlugin.backingBean.container}"
               contentAreaDesign="transparent" isCollapsible="false" width="100%">

        <f:attribute name="sap-delta-id" value="#{sap:toClientId('podSelection')}" />

        <sap:panelGrid width="100%" cellValign="middle">
            <sap:panelRow>
                <sap:panelGroup halign="#{podSelectionHVPlugin.touchDevice ? 'left' : 'left'}" width="80%" valign="middle">

                    <sap:panelGrid height="100%" cellValign="middle" width="80%">
                        <sap:panelGroup id="labelCell1" 
                            halign="#{podSelectionHVPlugin.touchDevice ? 'start' : 'start'}" padding="3px 0 3px 12px"
                            binding="#{podSelectionHVPlugin.backingBean.outputLabelCell1}"/>
                        <sap:panelGroup id="fieldCell1" width="#{podSelectionHVPlugin.touchDevice ? 0 : 15}%" 
                            halign="#{podSelectionHVPlugin.touchDevice ? 'start' : 'left'}" padding="3px 0 3px 0"
                            binding="#{podSelectionHVPlugin.backingBean.commandInputFieldCell1}" />
                        
                        <sap:panelGroup id="labelCell2" width="#{podSelectionHVPlugin.touchDevice ? 0 : 10}%" 
                            halign="#{podSelectionHVPlugin.touchDevice ? 'start' : 'right'}" padding="3px 0 3px 6px"
                            binding="#{podSelectionHVPlugin.backingBean.outputLabelCell2}" />
                        <sap:panelGroup id="fieldCell2" width="#{podSelectionHVPlugin.touchDevice ? 0 : 15}%" 
                            halign="#{podSelectionHVPlugin.touchDevice ? 'start' : 'left'}" padding="3px 0 3px 0"
                            binding="#{podSelectionHVPlugin.backingBean.commandInputFieldCell2}" />
                        
                        <sap:panelGroup id="labelCell3" width="#{podSelectionHVPlugin.touchDevice ? 0 : 10}%" 
                            halign="#{podSelectionHVPlugin.touchDevice ? 'start' : 'right'}" padding="3px 0 3px 6px"
                            binding="#{podSelectionHVPlugin.backingBean.outputLabelCell3}" />
                        <sap:panelGroup id="fieldCell3" width="100%" 
                            halign="#{podSelectionHVPlugin.touchDevice ? 'start' : 'left'}" padding="3px 0 3px 0"
                            binding="#{podSelectionHVPlugin.backingBean.commandInputFieldCell3}" />
                        
                        <sap:panelGroup id="labelCell4" width="#{podSelectionHVPlugin.touchDevice ? 0 : 10}%" 
                            halign="#{podSelectionHVPlugin.touchDevice ? 'start' : 'right'}" padding="3px 0 3px 6px"
                            binding="#{podSelectionHVPlugin.backingBean.outputLabelCell4}" />
                        <sap:panelGroup id="fieldCell4" width="#{podSelectionHVPlugin.touchDevice ? 0 : 15}%" 
                            halign="#{podSelectionHVPlugin.touchDevice ? 'start' : 'left'}" padding="3px 0 3px 0"
                            binding="#{podSelectionHVPlugin.backingBean.commandInputFieldCell4}" />
                    </sap:panelGrid>
                </sap:panelGroup>
                <sap:panelGroup padding="3px 3px 3px 7px" halign="right" valign="middle" width="20%">
                    <table>
                        <tr>
                            <td nowrap="nowrap">
                                    <fh:outputLabel id="sessionInfo1LabelView" styleClass="urTxtStd" value="FERT:" />&nbsp;
                                    <fh:outputLabel id="sessionInfo1TextView" styleClass="urTxtStd" value="#{podSelectionHVPlugin.sessionInfo1Text}" />&nbsp;&nbsp;
                                    <fh:outputLabel id="sessionInfo2LabelView" styleClass="urTxtStd" value="STATUS(VIN):" />&nbsp;
                                    <fh:outputLabel id="sessionInfo2TextView" styleClass="urTxtStd" value="#{podSelectionHVPlugin.sessionInfo2Text}" />                            
                            </td>
                        </tr>
                    </table>
                </sap:panelGroup>
                <sap:panelGroup height="0px">
                    <fh:inputHidden id="OPERATION_CHANGED" binding="#{podSelectionHVPlugin.backingBean.operationChangedField}" />
                    <fh:inputHidden id="RESOURCE_CHANGED" binding="#{podSelectionHVPlugin.backingBean.resourceChangedField}" />
                </sap:panelGroup>
            </sap:panelRow>
        </sap:panelGrid>
    </sap:panel>


</f:subview>
