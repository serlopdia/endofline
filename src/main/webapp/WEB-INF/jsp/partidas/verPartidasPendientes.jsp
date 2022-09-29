<%@ page session="false" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="endofline" tagdir="/WEB-INF/tags" %>

<endofline:css pageName="invitaciones">
    
    
    <table id="ownersTable" class="table table-striped">
        <thead>
        <tr>
            <th style="width: 150px;">Fecha</th>
            <th style="width: 150px;">Jugador 1</th>
            <th style="width: 150px;">Aceptar</th>
            <th style="width: 150px;">Rechazar</th>
            
            
        </tr>
        </thead>
        <tbody>
        <c:forEach items="${partidas}" var="partida">
            <tr>
                <td>
                    <c:out value="${partida.fecha}"/>
                </td>
                
                <td>
                    <c:out value="${partida.j1.usuario}"/>
                </td>
                <td>
                    <spring:url value="/partidas/aceptar/{id}" var="aceptar">
			            <spring:param name="id" value="${partida.id}"/>
		            </spring:url>
		            <a href="${aceptar}" class="glyphicon glyphicon-search">
			            Aceptar Partida
		            </a>
                </td>
                <td>
                    <spring:url value="/partidas/rechazar/{id}" var="rechazar">
			            <spring:param name="id" value="${partida.id}"/>
		            </spring:url>
		            <a href="${rechazar}" class="glyphicon glyphicon-search">
			            Rechazar Partida
		            </a>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>

</endofline:css>