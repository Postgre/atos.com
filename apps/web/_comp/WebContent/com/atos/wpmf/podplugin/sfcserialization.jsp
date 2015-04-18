<%@page pageEncoding="UTF-8"%>
<%@ page language="java"%>
<%@ taglib prefix="h" uri="http://java.sap.com/jsf/html"%>
<%@ taglib prefix="f" uri="http://java.sap.com/jsf/core"%>
<%@ taglib prefix="sap" uri="http://java.sap.com/jsf/html/extended"%>
<%@ taglib prefix="fh" uri="http://java.sun.com/jsf/html"%>
<%@ taglib prefix="icon" uri="http://www.sap.com/sapme/ui/icons"%>

<f:subview id="serializeplugin">
	  <sap:panel id="serializePanel" width="100%" height="100%" isCollapsible="false"
               contentAreaDesign="transparent" isCollapsed="false">
        <f:facet name="header">
            <h:outputText value="SFC Serialization to EIN" />
        </f:facet>
		

        <f:attribute name="sap-delta-id" value="#{sap:toClientId('serializePanel')}" />
         <sap:panelGrid width="100%" height="100%" cellHalign="start" cellValign="top">
        	<sap:panelRow>
		        <sap:panelGroup halign="start" valign="top" height="10%">
		          <sap:panelGrid binding="#{serialisation.pluginMessageLayout}" />
		        </sap:panelGroup>
      		</sap:panelRow>
			<sap:panelRow cellpadding="10 10 10 10">
			 
                        <sap:panelGroup id="labelCell100" valign="middle" halign="center"  width="100%" >
                        <table>
						<tr>
						       <td>
						       <fh:outputLabel value="SFC  " style="font-size:10pt;font-weight:bold;font-family:Arial Rounded MT Bold">
						       </fh:outputLabel> 
						       </td> 
						    <td>
						    <sap:inputText   id="actualSfc" value="#{serialisation.actualSfc}" disabled="true" width="200px">
						    </sap:inputText> 
						    </td>
						    </tr>
						    <tr></tr><tr></tr><tr></tr><tr></tr><tr></tr><tr></tr><tr></tr>
						    <tr>
							<td>
								<fh:outputLabel value="EIN  " style="font-size:10pt;font-weight:bold;font-family:Arial Rounded MT Bold" >
								</fh:outputLabel>
							</td>
							<td>
								<sap:inputText   id="serializeSFCvalue" value="#{serialisation.serializedSFCvalue}" width="200px">
								<f:attribute name="upperCase" value="true" />
								</sap:inputText>
								<fh:outputLabel value="    " ></fh:outputLabel>
								<fh:outputLabel value="    "></fh:outputLabel>	
							</td>
							</tr>
							 <tr></tr><tr></tr><tr></tr><tr></tr><tr></tr><tr></tr><tr></tr>
						    <tr align="center" valign="middle">
							<td> 
							<sap:commandButtonLarge id="serialize" value="Serialize"
										width="90px" height="15px" action="#{serialisation.serializeSFC}">
								</sap:commandButtonLarge>
								<fh:outputLabel value="    " ></fh:outputLabel>
								<fh:outputLabel value="    " ></fh:outputLabel>
							</td> 
							<td>
								<sap:commandButtonLarge id="returntoPOD" value="Return to POD"
										width="150px" height="15px" action="#{serialisation.closePlugin}">
								</sap:commandButtonLarge>
							<fh:outputLabel value="    " ></fh:outputLabel>
							<fh:outputLabel value="    "></fh:outputLabel>
							</td>
						</tr>
						</table>
						</sap:panelGroup>	
						
                                       
             </sap:panelRow>
        </sap:panelGrid>
    </sap:panel>
</f:subview>
