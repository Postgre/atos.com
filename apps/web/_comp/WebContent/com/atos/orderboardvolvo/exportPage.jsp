<%@page pageEncoding="UTF-8"%>
<%@ page language="java"%>
<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core"%>
<%@ taglib prefix="fh" uri="http://java.sun.com/jsf/html"%>
<%@ taglib prefix="h" uri="http://java.sap.com/jsf/html"%>
<%@ taglib prefix="sap" uri="http://java.sap.com/jsf/html/extended"%>
<%@ taglib prefix="ls" uri="http://java.sap.com/jsf/html/internal"%>
<f:view>
	<sap:html>

	<sap:head></sap:head>

	<sap:body title="ORDER BOARD" id="body" height="100%">
		<ls:page id="materialCustomDataPage" initBaseLibrary="false"
			title="ORDER BOARD" hasMargin="false" scrollingMode="HIDE"
			verticalSizing="FILL" formId="materialCustomDataForm"
			hasEventQueue="false" browserHistory="DISABLED">
			<ls:script facet="headScripts" type="CUSTOM"
				content="

             function closePopup(var obj) {
             alert('hi');
           		alert(document.getElementById('orderReleaseDataForm12:dwnloadBtn').value);
           		document.getElementById('dwnloadBtn').click();
           		window.opener = self;
    			window.close();
           }
           
           
          " />
			<h:form id="orderReleaseDataForm12">
				<ls:matrixLayout facet="content" id="selectionPanelLayout13"
					width="100%" height="100%">
					<ls:matrixLayoutRow facet="rows" id="row5121">
						<ls:matrixLayoutCell facet="cells" id="cell5121"
							cellBackgroundDesign="TRANSPARENT" cellDesign="PADLESS"
							HAlign="CENTER" width="100%" height="50%">
							<h:outputText value="Do ypu want to download excel?"></h:outputText>
						</ls:matrixLayoutCell>
					</ls:matrixLayoutRow>
					<ls:matrixLayoutRow facet="rows" id="row51212">
						<ls:matrixLayoutCell facet="cells" id="cell51212"
							cellBackgroundDesign="TRANSPARENT" cellDesign="PADLESS"
							HAlign="CENTER" width="100%" height="50%" customStyle="mystyle1 ">
							<fh:commandButton value="Download Excel" onclick="return closePopup();"></fh:commandButton>
							<h:commandButton value="Download Excel" id="dwnloadBtn"
								actionListener="#{orderReleaseDataBeanVolvo.exportOrders}"></h:commandButton>
						</ls:matrixLayoutCell>
					</ls:matrixLayoutRow>
				</ls:matrixLayout>


			</h:form>
		</ls:page>

	</sap:body>
	</sap:html>
</f:view>