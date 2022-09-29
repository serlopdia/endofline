package org.springframework.samples.endofline.posicion;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.samples.endofline.carta.Carta;
import org.springframework.samples.endofline.carta.CartaService;
import org.springframework.samples.endofline.mazo.Mazo;
import org.springframework.samples.endofline.mazo.MazoService;
import org.springframework.samples.endofline.partida.Partida;
import org.springframework.samples.endofline.partida.PartidaService;
import org.springframework.samples.endofline.usuario.Usuario;
import org.springframework.samples.endofline.usuario.UsuarioService;
import org.springframework.samples.endofline.util.GetionPermisos;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@Controller
public class PosicionController {
	
	@Autowired
	public PosicionService posicionService;
	
	@Autowired
	public UsuarioService usuarioService;
	
	@Autowired
	public PartidaService partidaService;
	
	@Autowired
	public GetionPermisos permisos;
	
	@Autowired
	public CartaService cartaService;
	
	@Autowired
	public MazoService mazoService;

	@PostMapping(value="/posicion/new", 
			consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE, produces = {MediaType.APPLICATION_ATOM_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
	public String newPosicion(@RequestBody MultiValueMap<String, String> posicionForm, ModelMap modelMap){
		Integer cartaId=Integer.parseInt(posicionForm.getFirst("carta").toString());
		Integer partidaId=Integer.parseInt(posicionForm.getFirst("partida").toString());
		Integer fila=Integer.parseInt(posicionForm.getFirst("fila").toString());
		Integer columna=Integer.parseInt(posicionForm.getFirst("columna").toString());
		Integer rotacion=Integer.parseInt(posicionForm.getFirst("rotacion").toString());
		Carta carta=cartaService.findById(cartaId);
		Partida partida = partidaService.getById(partidaId).get();
		Usuario usuario = usuarioService.getByUsuario(permisos.getNombreUsuarioActual()).get();

		//Insertamos la nueva posicion
		Posicion posicionNueva = new Posicion();
		posicionNueva.setColumna(columna);
		posicionNueva.setFila(fila);
		posicionNueva.setFecha(LocalDateTime.now());
		posicionNueva.setJ(usuario);
		posicionNueva.setP(partida);
		posicionNueva.setCarta(carta);
		posicionNueva.setGradoRotado(posicionService.rotacionByInteger(rotacion));
		Mazo mazo = mazoService.findByPartidaAndUsuario(partida, usuario).get();
		
		if(posicionNueva.getGradoRotado()==null || posicionNueva.getGradoRotado().equals(null)){
			mazo.setMensaje("ERROR: El grado de rotacion de la carta no es uno de los permitidos");
			return "redirect:/partidas/"+partidaId;
		}else {
			try {
			Posicion posicionInsertada=posicionService.insert(posicionNueva);
			mazo.setMensaje("");
			mazo=mazoService.eliminarCarta(mazo, carta);
			mazoService.repartirCartasPrimeraRonda(mazo);
			mazoService.repartirCartas(mazo);
		} catch (PosicionYaUsada e) {
			mazo.setMensaje("ERROR: La posicion ya tiene una carta");
			mazoService.add(mazo);
		} catch (PosicionNoContinuaFlujo e) {
			mazo.setMensaje("ERROR: La posicion no continua el flujo de tus cartas");
			mazoService.add(mazo);
		} catch (PosicionInvalidaLimiteDeTablero e) {
			mazo.setMensaje("ERROR: La posicion no está dentro de los limites del tablero");
			mazoService.add(mazo);
		}
		return "redirect:/partidas/"+partidaId;
		}
		
	}
	
}
