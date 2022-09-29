<%@ page session="false" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="endofline" tagdir="/WEB-INF/tags" %>
<!-- %@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %-->  

<endofline:welcome pageName="welcome">
<div class="row">
    <div class="col-4 align-self-start">
        <spring:url value="/usuarios/{usuarioId}" var="showUsuario">
			<spring:param name="usuarioId" value="${usuarioLoggued}"/>
		</spring:url>
		<a href="${showUsuario}" class="glyphicon glyphicon-search">
			Mi Perfil
		</a>
    </div>
    <div class="col-lg-8">
		<p class="text-left">
			<strong><sec:authentication property="name"/></strong>
		</p>
		<p class="text-left">
			<a href="<c:url value="/logout" />"
				class="btn btn-primary btn-block btn-sm">Logout</a>
		</p>
	</div>
	<div class="col-4 align-self-start">
        <spring:url value="/partidas/pendientes/{usuarioId}" var="pendientes">
			<spring:param name="usuarioId" value="${usuarioLoggued}"/>
		</spring:url>
		<a href="${pendientes}" class="glyphicon glyphicon-search">
			Invitaciones
		</a>
    </div>
	<div class="col-4 align-self-start">
        <spring:url value="/partidas/propias/{usuarioId}" var="propias">
			<spring:param name="usuarioId" value="${usuarioLoggued}"/>
		</spring:url>
		<a href="${propias}" class="glyphicon glyphicon-search">
			Partidas Propias
		</a>
    </div>
</div>
    
    <div class="row">
        <h2><center>Bienvenido a End Of Line<center/></h2>
    </div>
    <div class="row">
	    <center>
	        <spring:url value="/partidas/new" var="tablero">
			</spring:url>
			<a href="${tablero}" class="glyphicon glyphicon-search">
				Jugar Partida
			</a>
	    <center/>
    </div>
    <br><br>
    <div class="row">
        <div class="col-md-12">
            <spring:url value="/resources/images/End-Of-line.jpg" htmlEscape="true" var="endOfLineImage"/>
            <center>
           		 <img class="img-responsive" src="${endOfLineImage}" width="150" height="150"/>
            <center/>
            
        </div>
    </div>
</endofline:welcome>
