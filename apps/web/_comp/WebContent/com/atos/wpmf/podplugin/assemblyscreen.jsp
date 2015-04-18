<%@page pageEncoding="UTF-8"%>
<%@ page language="java"%>
<%@ taglib prefix="h" uri="http://java.sap.com/jsf/html"%>
<%@ taglib prefix="f" uri="http://java.sap.com/jsf/core"%>
<%@ taglib prefix="sap" uri="http://java.sap.com/jsf/html/extended"%>
<%@ taglib prefix="fh" uri="http://java.sun.com/jsf/html"%>
<%@ taglib prefix="icon" uri="http://www.sap.com/sapme/ui/icons"%>

<f:subview id="examplePlugin2">
	  <sap:panel id="reportsPanel2" width="100%" height="100%" isCollapsible="false"
               contentAreaDesign="transparent" isCollapsed="false">
        <f:facet name="header">
            <h:outputText value="Assembly Status" />
        </f:facet>
		

        <f:attribute name="sap-delta-id" value="#{sap:toClientId('reportsPanel2')}" />
       

        <sap:panelGrid width="100%" height="100%" cellHalign="start" cellValign="top">
        <div style="max-height: 150px;overflow-y: scroll;">
        <sap:panelRow>
		        <sap:panelGroup halign="start" valign="top" height="10%">
		          <sap:panelGrid binding="#{assemblyPlugin.pluginMessageLayout}" />
		        </sap:panelGroup>
      		</sap:panelRow>
      		<sap:panelRow cellpadding="2 10 2 10">
      		<sap:panelGroup  valign="middle" halign="left"  width="100%" height="3%" >
		      		<table style="width:100%">
			      		<tr>
				      		<td align="left">      			
							         <fh:outputLabel   style="color:#000000;font-weight: bold;" value= "Model: "></fh:outputLabel>                        	
					                 <fh:outputLabel   style="color:#000000" value= "#{assemblyPlugin.currentModel}"></fh:outputLabel> 
				      		</td>
				      		<td align="right">
						          	  <fh:outputLabel  style="color:#000000;font-weight: bold;" value= "Next Model: "></fh:outputLabel>                          	
					                 <fh:outputLabel   style="color:#000000;" value= "#{assemblyPlugin.nextModel}"></fh:outputLabel> 
				      		</td>
			      		
			      		</tr>     		
		      		</table>
		       </sap:panelGroup>	         
      		</sap:panelRow>
			<sap:panelRow cellpadding="10 10 10 10">
                        <sap:panelGroup id="labelCell100" valign="middle" halign="left" height="20%" width="100%" >
	                         <table style="width:100%">
	                         <tr>
		                         <td width="5%"><sap:outputLabel id="CustomLabel12"  value= "Component to Scan: "></sap:outputLabel>   </td>
		                         <td width="2%"><sap:outputLabel id="blank123456"  value= " "></sap:outputLabel></td>
		                         <td><sap:commandInputText id="OperationConfrm1" submitOnChange="false" submitOnTabout="true" 
                                                       value="#{assemblyPlugin.operationConfirmation}"
                
                                                      actionListener="#{assemblyPlugin.postScanOperation}">

                                               

                                                <sap:ajaxUpdate render="#{sap:toClientId('reportsPanel2')}" />

                                 </sap:commandInputText></td>
		                         <td width="3%"><div style="display:none"><sap:commandButton id="Search1" title="Search" actionListener="#{assemblyPlugin.postScanOperation}"></sap:commandButton></div></td>
		                         <td width="2%"><sap:outputLabel   value= " "></sap:outputLabel></td>
		                         <td width="80%"><sap:inputTextarea height="70px" width="100%" value="#{assemblyPlugin.specialInstruction}" readonly="true"></sap:inputTextarea></td>
	                         </tr>                         
	                         </table>
                        </sap:panelGroup>   
                </sap:panelRow>
            <sap:panelRow cellHalign="start" cellValign="top" >
                <sap:panelGroup backgroundDesign="transparent" width="100%" height="100%" halign="start" valign="top">
                    <sap:panelGrid width="100%" height="100%" cellHalign="start" cellValign="top">       
                        <sap:panelRow cellHalign="start" cellValign="top">
                            <sap:panelGroup backgroundDesign="transparent" width="100%" height="100%" halign="start" valign="top">
                                <sap:panel id="resultContainer2" isCollapsible="false" contentAreaDesign="transparent" borderDesign="none" width="100%" height="100%" >
                                    <sap:panelGrid id="htmlGrid2" columns="1" width="100%" height="100%" cellHalign="start" cellValign="top">
                                        <h:panelGroup id="reportHtml2" >
                                        		<sap:dataTable													
														value="#{assemblyPlugin.assemblyDataItems}"
														first="0" var="rows" id="materialCustomDataTable1"
														width="100%" height="500px" columnReorderingEnabled="true"
														rendered="true">
														<sap:column>
															<f:facet name="header">
																<h:outputText value="Component" />
															</f:facet>
															<sap:outputLabel id="shopOrderinputText1"
																value="#{rows.assemblyName}" />

														</sap:column>
														<sap:column>
															<f:facet name="header">
																<h:outputText value="Quantity" />
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
																<h:outputText value="Assembly Status" />
															</f:facet>
															<h:graphicImage url="/com/atos/icons/#{rows.assemblyStatus}.png" height="21" width="21"  />
																																												
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
			</div>
            
        </sap:panelGrid>
    </sap:panel>
	<script>
	document.getElementById('templateForm:tcArea:threepanelh:areaB:examplePlugin2:OperationConfrm1').focus();	
	</script>
</f:subview>
