<%@ page session="false" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="petclinic" tagdir="/WEB-INF/tags" %>

<petclinic:layout pageName="Cartas">
	<h2>Cartas</h2>
	    <table id="cartasTable" class="table table-striped">
        <thead>
        <tr>
            <th style="width: 15%;">Iniciativa</th>
            <th style="width: 15%;">Arriba</th>
            <th style="width: 15%;">Izquierda</th>
            <th style="width: 15%;">Derecha</th>
            <th style="width: 15%;">Abajo</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach items="${cartas}" var="carta">
            <tr>
                <td>                    
                    <c:out value="${carta.iniciativa}"/>
                </td>
                <td>
                    <c:out value="${carta.salida_arriba}"/>
                </td>
                <td>
                    <c:out value="${carta.salida_izquierda}"/>
                </td>
                <td>
                    <c:out value="${carta.salida_derecha}"/>
                </td>
                <td>
                    <c:out value="${carta.salida_abajo}"/>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
</petclinic:layout>