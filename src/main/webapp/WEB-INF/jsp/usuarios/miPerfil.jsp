<%@ page session="false" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="endofline" tagdir="/WEB-INF/tags" %>

<endofline:css pageName="miPerfil">
    <h2>Mi perfil</h2>


    <table class="table table-striped">
        <tr>
            <th><b>Nombre de usuario:</b></th>
            <td><c:out value="${usuario.usuario}"/></td>
        </tr>
        <tr>
            <th><b>Nombre:</b></th>
            <td><c:out value="${usuario.nombre}"/></td>
        </tr>
        <tr>
            <th><b>Apellidos:</b></th>
            <td><c:out value="${usuario.apellidos}"/></td>
        </tr>  
        <tr>
            <th><b>Email:</b></th>
            <td><c:out value="${usuario.email}"/></td>
        </tr>
        
        
    </table>
    
    <spring:url value="/usuarios/edit/{usuario}" var="usuarioUrl">
        <spring:param name="usuario" value="${usuario.usuario}"/>
    </spring:url>
    <a class="btn btn-success" href="${fn:escapeXml(usuarioUrl)}" role="button">Editar</a>
    


</endofline:css>