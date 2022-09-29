package org.springframework.samples.endofline.usuario;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.samples.endofline.util.*;

@Controller
public class UsuarioController {
	
	private static final String VIEWS_USUARIO_CREATE_OR_UPDATE_FORM = "usuarios/usuarioForm";

	@Autowired
	public UsuarioService usuarioService;
	
	
	@Autowired(required=true)
	public GetionPermisos permisos;
	

	@GetMapping("/loginHandler")
	public String loginHandler(ModelMap modelMap) {
		
		if(permisos.permisosUsuarioActual()=="admin") {
			return "redirect:/usuarios";
		}else if(permisos.permisosUsuarioActual()=="player"){
			return "redirect:/welcomeUsuario";
		}else {
			return "redirect:/login";
		}
	}
	

	@GetMapping("/usuarios")
	public String showAll(@RequestParam Map<String, Object> params, ModelMap modelMap) {
		String vista="usuarios/usuariosLista";
		int page = params.get("page") != null ? (Integer.valueOf(params.get("page").toString())-1) : 0;
		PageRequest pageRequest = PageRequest.of(page, 3);
		Page<Usuario> pageUsuario = usuarioService.findAll(pageRequest);
		
		int totalPage = pageUsuario.getTotalPages();
		if (totalPage>0) {
			List<Integer> pages = IntStream.rangeClosed(1, totalPage).boxed().collect(Collectors.toList());
			modelMap.addAttribute("pages", pages);
		}
		
		String userLogged=permisos.getNombreUsuarioActual();
		modelMap.addAttribute("usuarioLoggued", userLogged);
		modelMap.addAttribute("usuarios", pageUsuario.getContent());
		modelMap.addAttribute("current", page+1);
		modelMap.addAttribute("next", page+2);
		modelMap.addAttribute("prev", page);
		modelMap.addAttribute("last", totalPage);
		return vista;
	}
	
	@GetMapping(value="/usuarios/edit/{usuario}")
	public String showEditUsuario(@PathVariable("usuario") String usuario, ModelMap modelMap) {
		Optional<Usuario> user=usuarioService.getByUsuario(usuario);
		String userLogged=permisos.getNombreUsuarioActual();
		modelMap.addAttribute("usuarioLoggued", userLogged);
		modelMap.addAttribute("usuario", user.get());
		if(permisos.permisosUsuarioActual()=="admin") {
			return VIEWS_USUARIO_CREATE_OR_UPDATE_FORM;
		}else if(permisos.permisosUsuarioActual()=="player" && userLogged.equals(user.get().getUsuario())) {
			return "usuarios/usuarioEditComoJugador";
		}else {
			return "redirect:/welcomeUsuario";
		}
		
	}

	@GetMapping(value="/usuarios/new")
	public String newUsuario(ModelMap modelMap) {
		modelMap.addAttribute("usuario", new Usuario());
		if(permisos.permisosUsuarioActual()=="admin") {
			return VIEWS_USUARIO_CREATE_OR_UPDATE_FORM;
		}else if(permisos.permisosUsuarioActual()=="player") {
			return "usuarios/usuarioEditComoJugador";
		}else{
			return "usuarios/register";
		}
		
	}
	
	@GetMapping(value="/usuarios/register")
	public String newUsuarioRegister(ModelMap modelMap) {
		modelMap.addAttribute("usuario", new Usuario());
		return "usuarios/register";
	}
	
	@PostMapping(value="/usuarios/register")
	public String register (@Valid Usuario usuario, BindingResult result, ModelMap model) {	
		if (usuario.getPermiso_admin()==null) {
			usuario.setPermiso_admin("player");
		}
		if(result.hasErrors()) {
				return "usuarios/register";
		}else {
			Optional<Usuario> u=usuarioService.getByUsuario(usuario.getUsuario());
			if(u.isPresent() ) {
				model.addAttribute("mensaje", "Nombre de usuario ya escogido, intentelo de nuevo");
				return "usuarios/register";
			}else {
				usuarioService.edit(usuario);
				return "redirect:/";
			}			
		}	
	}
	
	
	@PostMapping(value="/usuarios/new")
	public String editUsuario (@Valid Usuario usuario, BindingResult result, ModelMap model) {
		if (usuario.getPermiso_admin()==null) {
			usuario.setPermiso_admin("player");
		}
		if(result.hasErrors()) {
			if(permisos.permisosUsuarioActual()=="admin") {
				return VIEWS_USUARIO_CREATE_OR_UPDATE_FORM;
			}else if (permisos.permisosUsuarioActual()=="player"){
				return "usuarios/usuarioEditComoJugador";
			}else {
				return "redirect:/";
			}
		}else {
			usuarioService.edit(usuario);
			if(permisos.permisosUsuarioActual()=="admin") {
				return "redirect:/usuarios";
			}else if(permisos.permisosUsuarioActual()=="player") {
				return "redirect:/usuarios/"+usuario.getUsuario();
			}else {
				return "redirect:/welcomeUsuario";
			}
			
		}	
	}
	
	@GetMapping(value="/usuarios/delete/{usuario}")
	public String deleteUsusario(@PathVariable("usuario") String usuario, ModelMap modelMap) {
		Optional<Usuario> usuarioEntity=usuarioService.getByUsuario(usuario);
		if(usuarioEntity.isPresent() && permisos.permisosUsuarioActual()=="admin") {
			usuarioService.delete(usuarioEntity.get());	
		}
		return "redirect:/usuarios";
	}
	
	@GetMapping(value="/usuarios/{usuario}")
	public String showUsuario(@PathVariable("usuario") String usuario, ModelMap modelMap) {
		Optional<Usuario> user=usuarioService.getByUsuario(usuario);
		String userLogged=permisos.getNombreUsuarioActual();
		modelMap.addAttribute("usuarioLoggued", userLogged);
		modelMap.addAttribute("usuario", user.get());
		
		if(permisos.permisosUsuarioActual()=="admin") {
			return "usuarios/miPerfil";
		}else if(permisos.permisosUsuarioActual()=="player" && userLogged.equals(user.get().getUsuario())) {
			return "usuarios/miPerfil";
		}else {
			return "redirect:/";
		}
	}
	
	@GetMapping(value="/welcomeUsuario")
	public String welcomeUsuario(ModelMap model) {
		String vista="usuarios/welcomeUsuario";
		String userLogged=permisos.getNombreUsuarioActual();
		model.addAttribute("usuarioLoggued", userLogged);
		return vista;
	}

}