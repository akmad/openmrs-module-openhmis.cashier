<%@ include file="/WEB-INF/template/include.jsp"%>
<%@ include file="/WEB-INF/template/header.jsp"%>
<%@ include file="template/localHeader.jsp"%>
<openmrs:htmlInclude file="/moduleResources/openhmis/cashier/js/screen/departments.js" />

<h2>
	<spring:message code="openhmis.cashier.admin.departments" />
</h2>

<%@ include file="template/genericAddEdit.jsp"%>