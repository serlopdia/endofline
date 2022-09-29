<%@ page session="false" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="endofline" tagdir="/WEB-INF/tags" %>

<endofline:css pageName="partidasEnCurso">
    
    <table id="ownersTable" class="table table-striped">
    <c:forEach items="${partidas}" var="partida">
        <thead>
        <tr>
            <th style="width: 150px;">Identificador de partida</th>
            <th style="width: 150px;">Fecha</th>
            <th style="width: 150px;">Jugador 1</th>
            <th style="width: 150px;">Jugador 2</th>
            <c:if test="${partida.estado=='FINALIZADA'}">
            <th style="width: 200px;">Ganador</th>
            </c:if>

        </tr>
        </thead>
        <tbody>
        
            <tr>
            
            	<td>
                    <c:out value="${partida.id}"/>
                </td>
                <td>
                    <c:out value="${partida.fecha}"/>
                </td>
                
                <td>
                    <c:out value="${partida.j1.usuario}"/>
                </td>
                
                <td>
                    <c:out value="${partida.j2.usuario}"/>
                </td>
       			
                
                 <c:if test="${partida.estado=='FINALIZADA'}">
               		<td>
                     	<c:out value="${partida.ganador.usuario}"/>
                     </td>
                        
                  </c:if>
              	
                
            </tr>
        </c:forEach>
        </tbody>
    </table>
</endofline:css>
