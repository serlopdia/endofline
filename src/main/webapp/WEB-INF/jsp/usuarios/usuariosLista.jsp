<%@ page session="false" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="petclinic" tagdir="/WEB-INF/tags" %>

<petclinic:layout pageName="usuariosLista">
    <h2>Usuarios</h2>
    
    <a href="/usuarios/new">Nuevo</a>
    <table id="ownersTable" class="table table-striped">
        <thead>
        <tr>
            <th>Usuario</th>
            <th style="width: 150px;">Nombre</th>
            <th style="width: 200px;">Apellidos</th>
            <th style="width: 120px">Email</th>
            
        </tr>
        </thead>
        <tbody>
        <c:forEach items="${usuarios}" var="usuario">
            
                <td>
                    <c:out value="${usuario.usuario}"/>
                </td>
                <td>
                    <c:out value="${usuario.nombre}"/>
                </td>
                <td>
                    <c:out value="${usuario.apellidos}"/>
                </td>
                <td>
                    <c:out value="${usuario.email} "/>
                </td>
                <td>
                    <spring:url value="/usuarios/edit/{usuario}" var="usuarioUrl">
                        <spring:param name="usuario" value="${usuario.usuario}"></spring:param>
                    </spring:url>
                    <a href="${fn:escapeXml(usuarioUrl)}">Editar</a>
                </td>
                <td>
                    <spring:url value="/usuarios/delete/{usuario}" var="usuarioUrl">
                        <spring:param name="usuario" value="${usuario.usuario}"></spring:param>
                    </spring:url>
                    <a href="${fn:escapeXml(usuarioUrl)}">Eliminar</a>
                </td>
                
      

                
            </tr>
        </c:forEach>
        </tbody>
    </table>
    
    <div class="row">
    	<div class="col-md-2"></div>
    	<div class="col-md-8">
    		<nav aria-label="Pagination">
				  <ul class="pagination justify-content-center">
				
				    <li class="page-item">
					    <c:forEach items="${pages}" var="page">
					   
					    	<a class="page-link" href="/usuarios/?page=${page}"><c:out value="${page}"/></a>
					    
					    </c:forEach>
				    	
				    </li>
				   
				   
				  </ul>
			</nav>
    	</div>
    	<div class="col-md-2"></div>
    </div>
  
</petclinic:layout>
