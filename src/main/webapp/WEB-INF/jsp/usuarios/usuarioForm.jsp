<%@ page session="false" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="petclinic" tagdir="/WEB-INF/tags" %>



<petclinic:layout pageName="usuarioForm">


	<h2>
        <c:if test="${usuario['new']}">Nuevo</c:if> Usuario
    </h2>
    
    <form:form modelAttribute="usuario" class="form-horizontal" id="add-owner-form" action="/usuarios/new">
        <div class="form-group has-feedback">
            <c:choose>
                    <c:when test="${usuario['new']}">
                        <petclinic:inputField label="Usuario" name="usuario"/>
                    </c:when>
                    <c:otherwise>
                        <input type="hidden" name="usuario" value="${usuario.usuario}"/>
                    </c:otherwise>
                </c:choose>
            <c:if test="${usuario['new']}"></c:if>
            
            <petclinic:inputField label="Nombre" name="nombre"/>
            <petclinic:inputField label="Apellidos" name="apellidos"/>
            <petclinic:inputField label="Email" name="email"/>
            <petclinic:inputField label="Contraseña" name="contrasenya"/>
            <petclinic:inputField label="Permiso Admin" name="permiso_admin"/>
           
            

        </div>
        <div class="form-group">
            <div class="col-sm-offset-2 col-sm-10">
            <c:choose>
                    <c:when test="${usuario['new']}">
                        <button class="btn btn-default" type="submit">Añadir</button>
                    </c:when>
                    <c:otherwise>
                        <button class="btn btn-default" type="submit">Editar</button>
                    </c:otherwise>
                </c:choose>
                
            </div>
        </div>
    </form:form>
    

</petclinic:layout>

