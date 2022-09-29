<%@ page session="false" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="petclinic" tagdir="/WEB-INF/tags" %>




<h2>Registro</h2>

<h3><c:out value="${mensaje}"/></h3>
<form:form modelAttribute="usuario" class="form-horizontal" id="add-owner-form" action="/usuarios/register">
    <div class="form-group has-feedback">
        <petclinic:inputField label="Usuario" name="usuario"/>
        <petclinic:inputField label="Nombre" name="nombre"/>
        <petclinic:inputField label="Apellidos" name="apellidos"/>
        <petclinic:inputField label="Email" name="email"/>
        <petclinic:inputField label="Contraseña" name="contrasenya"/>
        <input type="hidden" name="permiso_admin" value="player"/>
        

    </div>
    <div class="form-group">
        <div class="col-sm-offset-2 col-sm-10">
            <button class="btn btn-default" type="submit">Registrar</button>
        </div>
    </div>
</form:form>
