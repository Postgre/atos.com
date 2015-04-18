<%@page pageEncoding="UTF-8"%>
<%@ page language="java"%>
<%@ taglib prefix="h" uri="http://java.sap.com/jsf/html"%>
<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core"%>
<%@ taglib prefix="sap" uri="http://java.sap.com/jsf/html/extended"%>
<%@ taglib prefix="ls" uri="http://java.sap.com/jsf/html/internal"%>

<!-- Container action for all JavaServer Faces core and custom component actions -->
<f:subview id="EditCellView">

	<ls:panel facet="content" id="EditCellId"
		binding="#{tableCellEditPlugin.container}" title="Edit Cell List"
		width="100%" height="100%" hasEditableTitle="false"
		isCollapsible="false" collapsed="false" enabled="true"
		headerDesign="STANDARD" areaDesign="TRANSPARENT" borderDesign="BOX"
		isDragHandle="false" contentPadding="STANDARD">

		<f:attribute name="sap-delta-id" value="#{sap:toClientId('EditCellId')}" />

		<ls:matrixLayout facet="content" width="100%" height="100%" >
			<ls:matrixLayoutRow facet="rows">
			    <!-- Message Area in POD Plugin -->
				<ls:matrixLayoutCell facet="cells" cellDesign="PADLESS" width="100%"
					VAlign="TOP" HAlign="FORCEDLEFT">
					<ls:matrixLayout facet="content"
						binding="#{tableCellEditPlugin.pluginMessageMatrixLayout}"
						width="100%" />
				</ls:matrixLayoutCell>
			</ls:matrixLayoutRow>
			<ls:matrixLayoutRow facet="rows">
				<ls:matrixLayoutCell facet="cells" cellDesign="PADLESS" width="100%"
					height="98%" VAlign="TOP" HAlign="FORCEDLEFT">
					<sap:dataTable binding="#{tableCellEditPlugin.configBean.table}"
						value="#{tableCellEditPlugin.cellEditList}" var="row" first="0"
						rows="10" id="test_editcell_list_table" width="100%">
					</sap:dataTable>
				</ls:matrixLayoutCell>
			</ls:matrixLayoutRow>
			<ls:matrixLayoutRow facet="rows">
				<ls:matrixLayoutCell facet="cells" cellDesign="PADLESS" width="100%"
					height="2%" VAlign="TOP" HAlign="CENTER">
					<sap:commandButton id="close"
						value="#{gapiI18nTransformer['SFC_STEP_STATUS_PLUGIN.close.text.BUTTON']}"
						width="80px" action="#{tableCellEditPlugin.closePlugin}">
						<sap:ajaxUpdate />
					</sap:commandButton>
				</ls:matrixLayoutCell>
			</ls:matrixLayoutRow>

		</ls:matrixLayout>

	</ls:panel>
</f:subview>
