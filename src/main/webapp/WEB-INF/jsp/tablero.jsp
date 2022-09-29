<%@ page session="false" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="endofline" tagdir="/WEB-INF/tags" %>

<endofline:css pageName="tablero">
<c:if test="${mazo.partida.estado!='PENDIENTE' && (j1==usuarioLoggued || j2==usuarioLoggued)}">
<style type="text/css">
    table{
    	float: left;
        table-layout: auto;
        width: 250px;
    }
    
    td {
        border: 1px solid black;
        width: 120px;
        height: 120px;
        word-wrap: break-word;
    }
</style>

    <h1>Partida EndOfLine</h1>
	<c:if test="${mazo.partida.estado=='FINALIZADA'}">
		<h2>Partida Finalizada</h2>
		<a class="btn btn-danger" href="/welcomeUsuario">Salir</a>
		<h3>Ganador: <c:out value="${mazo.partida.ganador.usuario}"/></h3>

	</c:if>
    <h2><c:out value="${now}"/></h2>
    <h2><c:out value="${j1}"/> vs <c:out value="${j2}"/></h2>
    <table id="posicionesTable" border="1" class="table table-striped">
        <c:forEach items="${posiciones}" var="posicion">
            <c:if test="${posicion.columna == 1}"><tr id="filaTablero" rowspan="6"></c:if>
                <td id="celdaTablero" style="text-align:center;" colspan="6">
                    <c:if test="${posicion.columna==6 && posicion.fila==7}"><img style="width: 120px; height: 120px;" alt="INICIO AZUL" src="https://i.ibb.co/6Y97Jmq/inicio-azul.png"></img></c:if>
                    <c:if test="${posicion.columna==8 && posicion.fila==7}"><img style="width: 120px; height: 120px;" alt="INICIO ROSA" src="https://i.ibb.co/VBDJZPg/inicio-rosa.png"></img></c:if>
                    <c:if test="${!((posicion.columna==6 || posicion.columna==8) && posicion.fila==7)}">
					<c:if test="${posicion.carta==null}">
						(<c:out value="${posicion.fila}"/>,<c:out value="${posicion.columna}"/>)
					</c:if>
					<c:if test="${posicion.carta!=null}">
						<c:if test="${posicion.j.usuario == j1}">
						
							<c:if test="${posicion.gradoRotado eq 'GRADO_0'}">
								<img style="width: 120px; height: 120px;" src="${posicion.carta.imagen_azul}"></img><br>
							</c:if>
							<c:if test="${posicion.gradoRotado eq 'GRADO_90'}">
								<img style="transform:rotate(270deg); width: 120px; height: 120px;" src="${posicion.carta.imagen_azul}"></img><br>
							</c:if>
							<c:if test="${posicion.gradoRotado eq 'GRADO_180'}">
								<img style="transform:rotate(180deg); width: 120px; height: 120px;" src="${posicion.carta.imagen_azul}"></img><br>
							</c:if>
							<c:if test="${posicion.gradoRotado eq 'GRADO_270'}">
								<img style="transform:rotate(90deg); width: 120px; height: 120px;" src="${posicion.carta.imagen_azul}"></img><br>
							</c:if>
						</c:if>
						<c:if test="${posicion.j.usuario == j2}">
							<c:if test="${posicion.gradoRotado eq 'GRADO_0'}">
								<img style="width: 120px; height: 120px;" src="${posicion.carta.imagen_rosa}"></img><br>
							</c:if>
							<c:if test="${posicion.gradoRotado eq 'GRADO_90'}">
								<img style="transform:rotate(270deg); width: 120px; height: 120px;" src="${posicion.carta.imagen_rosa}"></img><br>
							</c:if>
							<c:if test="${posicion.gradoRotado eq 'GRADO_180'}">
								<img style="transform:rotate(180deg); width: 120px; height: 120px;" src="${posicion.carta.imagen_rosa}"></img><br>
							</c:if>
							<c:if test="${posicion.gradoRotado eq 'GRADO_270'}">
								<img style="transform:rotate(90deg); width: 120px; height: 120px;" src="${posicion.carta.imagen_rosa}"></img><br>
							</c:if>
						</c:if>
						
					</c:if>
					<c:if test="${posicion.carta==null}"></c:if>
                    </c:if>                    
                </td>
            <c:if test="${posicion.columna == 13}"></tr></c:if>
        </c:forEach>
    </table>



<div class="col-sm-10">
	<h3><c:out value="${mazo.mensaje}"/></h3>
</div>
<div class="col-sm-10">
	<h3><b>Energia <c:out value="${j1}"/>:</b> <c:out value="${e1}"/>  ##  <b>Energia <c:out value="${j2}"/>:</b> <c:out value="${e2}"/></h3>
 </div>
 <div class="col-sm-10">
	<h3><b>Ronda: </b><c:out value="${mazo.ronda}"/></h3>
 </div>
    <table border="1" class="table table-striped">
        
            <c:out value="${error}"></c:out>
            <c:forEach items="${mazo.cartas}" var="carta">
			
				<form:form modelAttribute="prePosicion" class="form-horizontal" id="add-owner-form" action="/posicion/new">
            	<tr>
	                <td style="text-align:center;" colspan="6">
	                    <c:if test="${usuarioLoggued == j1}">
	                                     
	                                <img src="${carta.imagen_azul}"></img><br>
	                   
	                    </c:if>
	                    <c:if test="${usuarioLoggued == j2}">
	                                        
	                                <img src="${carta.imagen_rosa}"></img><br>
	                    </c:if>
                    </td>
                    <c:if test="${mazo.turno==true}">
	                    <input type="hidden" name="carta" value="${carta.id}"/>
	                    <input type="hidden" name="partida" value="${mazo.partida.id}"/>
	                	<td style="vertical-align:middle; text-align:center; position: static;" colspan="6">
	                    	<input name="fila" placeholder="Fila"/><br>
	                    	<input name="columna" placeholder="Columna"/><br>
	                    	<input name="rotacion" placeholder="Rotacion (0, 90, 180, 270)"/><br>
	                    	<p>(Rotacion antihoraria)</p>
	                    	<button type="submit">Usar</button>
                    	</td>
                	</c:if>
               	</tr>
            </form:form>    	
			</c:forEach>
           
    </table>
    
    <table>
    	<c:if test="${mazo.turno==true && mazo.ronda >=3 && ultimoEfectoRonda != mazo.ronda}">
			<form:form modelAttribute="efecto" class="form-horizontal" id="add-owner-form" action="/efecto/new">
				<input type="hidden" name="partida" value="${mazo.partida.id}"/>

				<tr>

				<tr>

					<td>
						<button name="boton" value="aceleron" type="submit">Aceleron</button>
					</td>
					<td>
						<button name="boton" value="frenazo" type="submit">Frenazo</button>
					</td>
					<td>
						<button name="boton" value="marchaAtras" type="submit">Marcha atras</button>
					</td>
					<td>
						<button name="boton" value="gasExtra" type="submit">Gas extra</button>
					</td>
				</tr>
			</form:form>  
			</c:if>
    </table>

	<c:if test="${mazo.partida.estado!='FINALIZADA'}">
		<form:form class="form-horizontal" id="add-owner-form" action="/partidas/fin">
			<input type="hidden" name="partida" value="${mazo.partida.id}"/>
			<button type="submit">Abandonar Partida</button>
		</form:form>
	</c:if>

	<c:if test="${mazo.ronda==1 && mazo.turno==true && !mazo.usadoCambioMano}">
		<form:form modelAttribute="cambioMano" class="form-horizontal" id="add-owner-form" action="/partidas/nuevamano">
			<input type="hidden" name="partida" value="${mazo.partida.id}"/>
			<button type="submit">Cambiar mano inicial</button>
		</form:form>
    </c:if>

</c:if>
<c:if test="${mazo.partida.estado=='PENDIENTE'}">
	<h1>Partida no aceptada</h1>
</c:if>
</endofline:css>