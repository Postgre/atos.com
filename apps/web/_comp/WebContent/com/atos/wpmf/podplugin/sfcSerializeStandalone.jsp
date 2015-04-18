<%@page pageEncoding="UTF-8"%>
<%@ page language="java"%>
<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core"%>
<%@ taglib prefix="fh" uri="http://java.sun.com/jsf/html"%>
<%@ taglib prefix="h" uri="http://java.sap.com/jsf/html"%>
<%@ taglib prefix="sap" uri="http://java.sap.com/jsf/html/extended"%>
<f:view>
	<sap:html>

	<sap:head>
		<style>
			.labelStyle {
				font-size: 12px;
				font-family: "Arial";
			}
			
			.headerStyle
			{
				font-size: 12px;
			}	
			.mystyle2 
			{
			border: none;
			background-color: #D0E8E7;
			font-size: 18px;
			font-family: "Arial Rounded MT Bold";
			}
		</style>
	   <script>
	   UCF_DomUtil.attachEvent(window, 'load', loadLightSpeed);
       UCF_DomUtil.attachEvent(window, 'unload', windowUnload);

       function loadLightSpeed() {
           window.oLS = new UCF_LS();
          }

       function unloadLightSpeed() {
           if (window.oLS) {
               window.oLS.destroy();
           }
       }
        function windowUnload() {
           if (window.parent == undefined) {
            //   window.alert('windowLoaded: window.parent is undefined');
               return;
           }
           var btnControl = document.getElementById('materialCustomDataForm:windowCloseButton');
           if (btnControl) {
               btnControl.click();
           } else {
             //  window.alert('windowLoaded: hidden button not found');
           }

           unloadLightSpeed();
       }
   
	</script>
	</sap:head>
	<sap:body title="Assign to Chasis" id="body" height="60%" browserHistory="disabled" focusId="serializeStandAlone">
		<!-- To Localize Title use gapiI18nTransformer['KEY']-->
		
		<h:form id="serializeStandAlone">
					<f:attribute name="height" value="100%" />
				<!-- defines the form that will be updated when "delta" submits are made to the server -->
				<f:attribute name="sap-delta-id"
					value="#{sap:toClientId('serializeStandAlone')}" />
				<sap:panel id="fieldButtonPanel" width="100%" height="100%"
				isCollapsible="false" contentAreaDesign="transparent" isCollapsed="false">
					<f:attribute name="sap-delta-id"
						value="#{sap:toClientId('fieldButtonPanel')}" />
					<f:facet name="header">
										<!-- Panel title -->
										<h:outputText value="SFC Serialization to EIN" />
									</f:facet>
					<sap:panelGrid width="100%" height="60%" cellHalign="start"
					cellValign="top" columns="1">
					<sap:panelGroup id="labelCell100" valign="middle" halign="center"  width="100%" >
                              <table style="width:100%">

		                       <tr>
	                       	  <td width="30%"><sap:outputLabel  value= " "></sap:outputLabel></td>
                               <td width="5%">
                               <fh:outputLabel id="CustomLabel121"  value= " SFC : " style="font-size:10pt;font-weight:bold;font-family:Arial Rounded MT Bold">
                               </fh:outputLabel>
                               </td>
		                       <td width="20%"><sap:inputText   id="actualSfc" value="#{serialisation.actualSfc}">
		                       <f:attribute name="upperCase" value="true" />
		                       </sap:inputText></td>
		                      <td width="30%"><sap:outputLabel  value= " "></sap:outputLabel></td>
		                      </tr>   
		                      <tr></tr> <tr></tr> <tr></tr> <tr></tr> <tr></tr> <tr></tr> <tr></tr> 
		                       <tr>
	                       	  <td width="30%"><sap:outputLabel   value= " "></sap:outputLabel></td>
                               <td width="5%">
                               <fh:outputLabel id="CustomLabel12"  value= " EIN : " style="font-size:10pt;font-weight:bold;font-family:Arial Rounded MT Bold">
                               </fh:outputLabel>
                               </td>
		                       <td width="20%"><sap:inputText   id="serializeSFCvalue" value="#{serialisation.serializedSFCvalue}">
		                       <f:attribute name="upperCase" value="true" />
		                       </sap:inputText></td>
		                      </tr>
		                      <tr></tr> <tr></tr> <tr></tr><tr></tr> <tr></tr> <tr></tr><tr></tr> <tr></tr> <tr></tr>
		                      <tr>
		                      <td>
								<sap:commandButtonLarge id="serialize" value="Serialize"
										width="90px" height="15px" action="#{serialisation.serializeSFC}">
								</sap:commandButtonLarge>
								<fh:outputLabel value="    " ></fh:outputLabel>
								</td>
								<td>
								<sap:commandButtonLarge id="returntoPOD" value="Return to POD"
										width="90px" height="15px" action="#{serialisation.closePlugin}">
								</sap:commandButtonLarge>
								</td>
								</tr>           
	                         </table>  
                        </sap:panelGroup>  
					</sap:panelGrid>
					</sap:panel>
				</h:form>
		</sap:body>
	</sap:html>

</f:view>
