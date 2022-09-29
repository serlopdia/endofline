<%@ page session="false" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="endofline" tagdir="/WEB-INF/tags" %>

<endofline:css pageName="cargando">
<h2>Espera a que tu contrincante acepte la partida</h2>

<spring:url value="/partidas/comprobar/{id}" var="aceptar">
	<spring:param name="id" value="${id}"/>
</spring:url>
<a href="${fn:escapeXml(aceptar)}" class="glyphicon glyphicon-search">
	Jugar
</a>
</endofline:css>
