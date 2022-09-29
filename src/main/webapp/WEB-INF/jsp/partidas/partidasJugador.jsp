<%@ page session="false" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="endofline" tagdir="/WEB-INF/tags" %>

<endofline:css pageName="cargando">
    
    
    <table id="partidasPropias" class="table table-striped">
        <thead>
        <tr>
            <th style="width: 150px;">Fecha</th>
            <th style="width: 150px;">Creada por</th>
            <th style="width: 150px;">Invitado</th>
            <th style="width: 150px;">Ganador</th>
            <th style="width: 150px;">Estado</th>
            <th style="width: 150px;">Ir a Partida</th>
            
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
                    <c:out value="${partida.j2.usuario}"/>
                </td>
                <td>
                    <c:if test="${partida.estado=='FINALIZADA'}">
                        <c:out value="${partida.ganador.usuario}"/>
                        
                    </c:if>
                    <c:if test="${partida.estado!='FINALIZADA'}">
                        -
                    </c:if>
                    
                </td>
                <td>
                    <c:out value="${partida.estado}"/>
                </td>
                <td>
                    <spring:url value="/partidas/{id}" var="ir">
			            <spring:param name="id" value="${partida.id}"/>
		            </spring:url>
		            <a href="${ir}" class="glyphicon glyphicon-search">
			            Ir
		            </a>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>

</endofline:css>