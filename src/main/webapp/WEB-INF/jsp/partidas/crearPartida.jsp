<%@ page session="false" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="endofline" tagdir="/WEB-INF/tags" %>

<endofline:css pageName="cargando">

<h2>Invitar al Jugador 2</h2>


<form:form class="form-horizontal" id="add-owner-form" action="/partidas/new">
    <div class="form-group has-feedback">
        <h3>Introduce el nombre del usuario con el que quieres jugar</h3>
        <input label="Usuario" name="usuario"/>

    </div>
    <div class="form-group">
        <div class="col-sm-offset-2 col-sm-10">
            <button class="btn btn-default" type="submit">Invitar</button>
        </div>
    </div>
</form:form>

<h3><c:out value="${mensaje}"/></h3>

</endofline:css>