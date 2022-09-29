<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="petclinic" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="sec"
	uri="http://www.springframework.org/security/tags"%>
<!--  >%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%-->
<%@ attribute name="name" required="true" rtexprvalue="true"
	description="Name of the active menu: home, owners, vets or error"%>

<nav class="navbar navbar-default" role="navigation">
	<div class="container">
		<div class="navbar-header">
			<a href="/usuarios"><img src="/resources/images/endofline_logo_caja.jpg" 
				alt="logo" width="100" height="100" /></a>
			<button type="button" class="navbar-toggle" data-toggle="collapse"
				data-target="#main-navbar">
				<span class="sr-only"><os-p>Toggle navigation</os-p></span> <span
					class="icon-bar"></span> <span class="icon-bar"></span> <span
					class="icon-bar"></span>
			</button>
		</div>
		<div class="navbar-collapse collapse" id="main-navbar">
			<ul class="nav navbar-nav">

				
				<spring:url value="/usuarios/{usuarioId}" var="showUsuario">
					<spring:param name="usuarioId" value="${usuarioLoggued}"/>
				</spring:url>
				<petclinic:menuItem active="${name eq 'home'}" url="${showUsuario}"
					title="Ver mi perfil">
					<span class="glyphicon glyphicon-user" aria-hidden="true"></span>
					<span>Mi perfil</span>
				</petclinic:menuItem>
				
					
				<petclinic:menuItem active="${name eq 'home'}" url="/usuarios"
					title="Ver usuarios del sistema">
					<span class="glyphicon glyphicon-home" aria-hidden="true"></span>
					<span>Administrador Usuarios</span>
				</petclinic:menuItem>

				<petclinic:menuItem active="${name eq 'vets'}" url="/partidas/encurso"
					title="Ver las partidas en curso">
					<span class="glyphicon glyphicon-th-list" aria-hidden="true"></span>
					<span>Partidas en Curso</span>
				</petclinic:menuItem>
				
				<petclinic:menuItem active="${name eq 'vets'}" url="/partidas/jugadas"
					title="Ver las partidas ya jugadas">
					<span class="glyphicon glyphicon-th-list" aria-hidden="true"></span>
					<span>Partidas Jugadas</span>
				</petclinic:menuItem>
				
				<petclinic:menuItem active="${name eq 'cartas'}" url="/cartas"
					title="Ver las cartas">
					<span class="glyphicon glyphicon-th-list" aria-hidden="true"></span>
					<span>Cartas</span>
				</petclinic:menuItem>

				<petclinic:menuItem active="${name eq 'error'}" url="/oups"
					title="trigger a RuntimeException to see how it is handled">
					<span class="glyphicon glyphicon-warning-sign" aria-hidden="true"></span>
					<span>Error</span>
				</petclinic:menuItem>
				
				

			</ul>




			<ul class="nav navbar-nav navbar-right">
				<sec:authorize access="!isAuthenticated()">
					<li><a href="<c:url value="/login" />">Login</a></li>
					<li><a href="<c:url value="/usuarios/new" />">Register</a></li>
				</sec:authorize>
				<div class="col-lg-9">
		<p class="text-left">
			<strong><sec:authentication property="name"/></strong>
		</p>
		<p class="text-left">
			<a href="<c:url value="/logout" />"
				class="btn btn-primary btn-block btn-sm">Logout</a>
		</p>
	</div>
				
							<li class="divider"></li>
<!-- 							
                            <li> 
								<div class="navbar-login navbar-login-session">
									<div class="row">
										<div class="col-lg-12">
											<p>
												<a href="#" class="btn btn-primary btn-block">My Profile</a>
												<a href="#" class="btn btn-danger btn-block">Change
													Password</a>
											</p>
										</div>
									</div>
								</div>
							</li>
-->
						</ul>
			
		</div>



	</div>
</nav>