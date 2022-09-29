<%@ page session="false" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="petclinic" tagdir="/WEB-INF/tags" %>

    <h2>Editar Usuario</h2>
    <form:form modelAttribute="usuario" class="form-horizontal" id="add-owner-form" action="/usuarios/new">
        <div class="form-group has-feedback">
        	
            <input type="hidden" name="usuario" value="${usuario.usuario}"/>
            <petclinic:inputField label="Nombre" name="nombre"/>
            <petclinic:inputField label="Apellidos" name="apellidos"/>
            <petclinic:inputField label="Email" name="email"/>
            <petclinic:inputField label="Contraseña" name="contrasenya"/>

        </div>
        <div class="form-group">
            <div class="col-sm-offset-2 col-sm-10">
                <button class="btn btn-default" type="submit">Editar</button>
            </div>
            <div class="col-sm-offset-2 col-sm-10">
                <a href="/welcomeUsuario">Volver al Menu</a>
            </div>
        </div>
    </form:form>