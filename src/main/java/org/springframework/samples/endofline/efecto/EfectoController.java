package org.springframework.samples.endofline.efecto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
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
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class EfectoController {
	
	@Autowired(required=true)
	public GetionPermisos permisos;
	
	@Autowired
	public EfectoService efectoService;
	
	@Autowired
	public UsuarioService usuarioService;
	
	@Autowired
	public PartidaService partidaService;
	
	@Autowired
	public MazoService mazoService;
	

	
	@PostMapping(value="/efecto/new",
			consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE, produces = {MediaType.APPLICATION_ATOM_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
	public String newEfecto(@RequestBody MultiValueMap<String, String> efectoForm, BindingResult result,  ModelMap model) {
		
		String nombreEfecto = efectoForm.getFirst("boton");
		Usuario usuario=usuarioService.getByUsuario(permisos.getNombreUsuarioActual()).get();		
		
		Integer partidaId= Integer.parseInt(efectoForm.getFirst("partida"));
		Partida partida = partidaService.getById(partidaId).get();
		
		Mazo m=mazoService.findByPartidaAndUsuario(partida, usuario).get();
		try {
			efectoService.newEfecto(nombreEfecto, usuario, partida);
			m.setMensaje("");
			mazoService.add(m);
		} catch (SinEnergiaException e) {
			m.setMensaje("ERROR: No tienes energia suficiente para este efecto");
			mazoService.add(m);
		} catch (RondaParaEfectoException e) {
			m.setMensaje("ERROR: Los efectos se pueden usar a partir de la ronda 3");
			mazoService.add(m);
		} catch (EfectoImposibleException e) {
			m.setMensaje("ERROR: La carta anterior no tiene ninguna salida hábil");
			mazoService.add(m);
		}
		return "redirect:/partidas/"+partidaId;

	}	
	
}