<%@ page session="false" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="endofline" tagdir="/WEB-INF/tags" %>

<endofline:css pageName="welcome">
    <div class="row">
        <h2><center>Bienvenido a End Of Line<center/></h2>
	    <div class="row">
	        <div class="col-md-12">
	            <spring:url value="/resources/images/endofline_logo_caja.jpg" htmlEscape="true" var="endOfLineImage"/>
	            <center>
	           		 <img class="img-responsive" src="${endOfLineImage}" width="300" height="300"/>
	            <center/>
	            
	        </div>
	    </div>
        <p><h2>Group ${group}</h2></p>
        <p><ul>
            <c:forEach items="${persons}" var="person">
                <li>${person.firstName} ${person.lastName}</li>
            </c:forEach>
        </ul></p>
    </div>
    <h2>Para poder jugar a End of Line necesitas estar registrado</h2>
    <div class="row">
        <div class="col-md-12">
            <p class="">
                <a href="/login">Inicia Sesion</a>
            </p>
            
        </div>
    </div>
    <div class="row">
        <div class="col-md-12">
            <p class="">
                <a href="/usuarios/register">Registrate</a>
            </p>
            
        </div>

    </div>

    </div>

</endofline:css>






