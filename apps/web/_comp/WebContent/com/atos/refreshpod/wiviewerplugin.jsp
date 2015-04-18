<%@page pageEncoding="UTF-8"%>
<%@ page language="java"%>
<%@ taglib prefix="h" uri="http://java.sap.com/jsf/html"%>
<%@ taglib prefix="f" uri="http://java.sap.com/jsf/core"%>
<%@ taglib prefix="sap" uri="http://java.sap.com/jsf/html/extended"%>
<%@ taglib prefix="ls" uri="http://java.sap.com/jsf/html/internal"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<f:subview id="workInstructionViewerView">


	<ls:panel facet="content" id="workInstructionViewer" binding="#{workInstructionViewerPlugin.container}"
		title="#{gapiI18nTransformer['WI500.activity.DESC']}" width="100%" height="100%" hasEditableTitle="false" isCollapsible="false"
		collapsed="false" enabled="true" headerDesign="STANDARD" borderDesign="BOX" scrollingMode="NONE" isDragHandle="false"
		contentPadding="NONE" areaDesign="TRANSPARENT">

		<f:attribute name="sap-delta-id" value="#{sap:toClientId('workInstructionViewer')}" />

		<ls:button facet="headerFunctions" type="HELP" tooltip="#{gapiI18nTransformer['toolTip.help.TEXT']}"
			action="#{workInstructionViewerPlugin.showPluginHelp}" pressInfoEnabled="true" pressInfoClientAction="submit"
			pressInfoResponseData="delta" pressInfoParameters="#{sap:deltaUpdateId(sap:toClientId('workInstructionViewer'))}" />

		<ls:gridLayout width="100%" height="100%" facet="content">
			<ls:gridLayoutRow facet="rows">
				<ls:gridLayoutCell VAlign="TOP" height="100%" facet="cells">
					<ls:splitter facet="content" id="splitterMain" width="100" widthSizeMode="RELATIVE" widthSizeModeOfColums="RELATIVE"
						height="100" scrollbars="NONE">
						<ls:splitterCol facet="cols" width="#{workInstructionViewerPlugin.twoPanelMode?50:100}" />
						<ls:splitterCol facet="cols" width="50" rendered="#{workInstructionViewerPlugin.twoPanelMode}" />

						<ls:splitterRow facet="rows" height="100">

							<c:forEach var="workInstr" items="#{workInstructionViewerPlugin.wiView}">
								<ls:splitterCell facet="cells" attachBrowserResize="true">

									<ls:gridLayout height="100%" width="100%" facet="content">
										<ls:gridLayoutRow facet="rows">
											<ls:gridLayoutCell width="100%" facet="cells">
												<ls:contentArea areaDesign="FILL" width="100%" contentPadding="MANUAL" paddingLeft="8" paddingRight="8" facet="content">
													<ls:gridLayout width="100%" height="22px" facet="content">
														<ls:gridLayoutRow facet="rows">
															<ls:gridLayoutCell wrapping="false" facet="cells">
																<ls:label text="#{gapiI18nTransformer['operation.default.LABEL']}" design="EMPHASIZED" wrapping="false" facet="content"/>
																<ls:textView id="wiOperation" text="#{workInstr.wiData.operation}" wrapping="false" facet="content"/>
															</ls:gridLayoutCell>
															<ls:gridLayoutCell paddingLeft="13" wrapping="false" facet="cells">
																<ls:label text="#{gapiI18nTransformer['workInstruction.default.LABEL']}" design="EMPHASIZED" wrapping="false" facet="content"/>
																<ls:textView id="workInsrtuctionID" text="#{workInstr.wiData.wiName}" wrapping="false" facet="content"/>
															</ls:gridLayoutCell>
															<ls:gridLayoutCell paddingLeft="13" wrapping="false" facet="cells">
																<ls:textView text="#{workInstr.sfcCount}" wrapping="false" facet="content"/>
															</ls:gridLayoutCell>
															<ls:gridLayoutCell paddingLeft="4" wrapping="false" facet="cells">
																<ls:textView text="#{gapiI18nTransformer['workInstruction.sfcSelected.LABEL']}" design="EMPHASIZED"
																	wrapping="false" facet="content"/>
															</ls:gridLayoutCell>
															<ls:gridLayoutCell paddingLeft="13" wrapping="false" facet="cells">
																<ls:label text="#{gapiI18nTransformer['sfc.default.LABEL']}" design="EMPHASIZED" wrapping="false" facet="content"/>
																<ls:textView id="wiFirstSfc"
																	text="#{workInstr.firstSfc} #{workInstr.sfcCount}/#{workInstructionViewerPlugin.sfcCount}" wrapping="false" facet="content"/>
															</ls:gridLayoutCell>
															<ls:gridLayoutCell paddingLeft="13" wrapping="false" width="100%" HAlign="LEFT" facet="cells">
																<ls:label text="#{gapiI18nTransformer['description.default.LABEL']}" design="EMPHASIZED" wrapping="false" facet="content"/>
																<ls:textView id="wiDescription" text="#{workInstr.wiData.description}" wrapping="false" facet="content"/>
															</ls:gridLayoutCell>
														</ls:gridLayoutRow>
													</ls:gridLayout>
												</ls:contentArea>
											</ls:gridLayoutCell>
										</ls:gridLayoutRow>
										<ls:gridLayoutRow facet="rows">
											<ls:gridLayoutCell height="100%" width="100%" paddingBottom="2" paddingLeft="2" paddingRight="2" paddingTop="2" facet="cells">
												<sap:panel id="viewPanel" binding="#{workInstr.panel}" height="100%" width="100%" isCollapsible="false"/>
											</ls:gridLayoutCell>
										</ls:gridLayoutRow>
									</ls:gridLayout>

								</ls:splitterCell>
							</c:forEach>
						</ls:splitterRow>
					</ls:splitter>
				</ls:gridLayoutCell>
			</ls:gridLayoutRow>
			
		</ls:gridLayout>
	</ls:panel>

</f:subview>