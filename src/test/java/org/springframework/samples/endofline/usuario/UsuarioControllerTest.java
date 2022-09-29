package org.springframework.samples.endofline.usuario;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.samples.endofline.configuration.SecurityConfiguration;
import org.springframework.security.config.annotation.web.WebSecurityConfigurer;

import org.junit.jupiter.api.BeforeAll;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.security.test.context.support.WithMockUser;
import static org.mockito.BDDMockito.given;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import java.util.Optional;
import org.springframework.samples.endofline.util.GetionPermisos;

@WebMvcTest(controllers = UsuarioController.class,
excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = WebSecurityConfigurer.class), 
excludeAutoConfiguration = SecurityConfiguration.class)
public class UsuarioControllerTest {

	
	@MockBean
	private UsuarioService usuarioService;
	
	@MockBean
	private GetionPermisos permisos;
	
	@Autowired
	private MockMvc mockMvc;
	
	private static Usuario u;
	
	@BeforeAll
	static void iniciar() {
		u=new Usuario();
		u.setApellidos("Gomez");
		u.setContrasenya("12345");
		u.setEmail("prueba@gmail.com");
		u.setNombre("Borja");
		u.setPermiso_admin("player");
		u.setUsuario("jP1");
	}
	
	@WithMockUser(value="spring")
	@Test
	public void testAllUsuarios() throws Exception {
		
//		given(usuarioService.findAll(PageRequest.of(1, 3))).willReturn(new Page<>());
		
		mockMvc.perform(get("/usuarios").param("page", "1"))
		.andExpect(status().isOk());
	}
	
	@WithMockUser(value="spring")
	@Test
	public void testShowEditarUsuarioAdmin() throws Exception {
		given(usuarioService.getByUsuario(u.getUsuario())).willReturn(Optional.of(u));
		given(permisos.getNombreUsuarioActual()).willReturn(u.getUsuario());
		given(permisos.permisosUsuarioActual()).willReturn("admin");
		
		mockMvc.perform(get("/usuarios/edit/{usuario}", u.getUsuario()))
		.andExpect(status().isOk())
		.andExpect(model().attributeExists("usuarioLoggued"))
		.andExpect(model().attributeExists("usuario"))
		.andExpect(view().name("usuarios/usuarioForm"));
	}
	
	@WithMockUser(value="spring")
	@Test
	public void testShowEditarUsuarioPlayer() throws Exception {
		given(usuarioService.getByUsuario(u.getUsuario())).willReturn(Optional.of(u));
		given(permisos.getNombreUsuarioActual()).willReturn(u.getUsuario());
		given(permisos.permisosUsuarioActual()).willReturn("player");
		
		mockMvc.perform(get("/usuarios/edit/{usuario}", u.getUsuario()))
		.andExpect(status().isOk())
		.andExpect(model().attributeExists("usuarioLoggued"))
		.andExpect(model().attributeExists("usuario"))
		.andExpect(view().name("usuarios/usuarioEditComoJugador"));
	}
	
	@WithMockUser(value="spring")
	@Test
	public void testNuevoUsuarioPlayer() throws Exception {
		given(permisos.permisosUsuarioActual()).willReturn("player");
		given(usuarioService.edit(u)).willReturn(u);
		
		mockMvc.perform(post("/usuarios/new")
				.param("usuario", "jP1")
				.param("nombre", "Borja")
				.param("apellidos", "Gomez")
				.param("contrasenya", "12345")
				.param("email", "prueba@gmail.com")
				.param("permiso_admin", "player")
				.with(csrf()))
		.andExpect(status().is3xxRedirection());
	}
	
	@WithMockUser(value="spring")
	@Test
	public void testNuevoUsuarioPlayerBad() throws Exception {
		given(permisos.permisosUsuarioActual()).willReturn("player");
		given(usuarioService.edit(u)).willReturn(u);
		
		mockMvc.perform(post("/usuarios/new")
				.param("usuario", "jP1")
				.param("nombre", "")
				.param("apellidos", "Gomez")
				.param("contrasenya", "12345")
				.param("email", "prueba@gmail.com")
				.param("permiso_admin", "player")
				.with(csrf()))
		.andExpect(status().isOk())
		.andExpect(view().name("usuarios/usuarioEditComoJugador"))
		.andExpect(model().attributeHasErrors("usuario"));
	}
	
	@WithMockUser(value="spring")
	@Test
	public void testRegister() throws Exception {
		given(usuarioService.getByUsuario(u.getUsuario())).willReturn(Optional.empty());
		given(usuarioService.edit(u)).willReturn(u);
		
		mockMvc.perform(post("/usuarios/register")
				.param("usuario", "jP1")
				.param("nombre", "Borja")
				.param("apellidos", "Gomez")
				.param("contrasenya", "12345")
				.param("email", "prueba@gmail.com")
				.param("permiso_admin", "player")
				.with(csrf()))
		.andExpect(status().is3xxRedirection())
		.andExpect(view().name("redirect:/"));
	}
	
	@WithMockUser(value="spring")
	@Test
	public void testRegisterBad() throws Exception {
		given(usuarioService.getByUsuario(u.getUsuario())).willReturn(Optional.of(u));
		given(usuarioService.edit(u)).willReturn(u);
		
		mockMvc.perform(post("/usuarios/register")
				.param("usuario", "jP1")
				.param("nombre", "Borja")
				.param("apellidos", "Gomez")
				.param("contrasenya", "12345")
				.param("email", "prueba@gmail.com")
				.param("permiso_admin", "player")
				.with(csrf()))
		.andExpect(status().isOk())
		.andExpect(view().name("usuarios/register"))
		.andExpect(model().attributeExists("mensaje"));
	}
	
	@WithMockUser(value="spring")
	@Test
	public void testDeleteUsuario() throws Exception {
		given(usuarioService.getByUsuario(u.getUsuario())).willReturn(Optional.of(u));
		
		mockMvc.perform(get("/usuarios/delete/{usuario}", u.getUsuario()))
		.andExpect(status().is3xxRedirection());
	}
	
	
	@WithMockUser(value="spring")
	@Test
	public void testShowUsuarioPlayer() throws Exception {
		u.setPermiso_admin("admin");
		given(usuarioService.getByUsuario(u.getUsuario())).willReturn(Optional.of(u));
		given(permisos.getNombreUsuarioActual()).willReturn(u.getUsuario());
		given(permisos.permisosUsuarioActual()).willReturn("player");
		mockMvc.perform(get("/usuarios/{usuario}", u.getUsuario()))
		.andExpect(status().isOk())
		.andExpect(model().attributeExists("usuarioLoggued"))
		.andExpect(model().attributeExists("usuario"))
		.andExpect(view().name("usuarios/miPerfil"));
	}
	
	@WithMockUser(value="spring")
	@Test
	public void testShowUsuarioAdmin() throws Exception {
		given(usuarioService.getByUsuario(u.getUsuario())).willReturn(Optional.of(u));
		given(permisos.getNombreUsuarioActual()).willReturn(u.getUsuario());
		given(permisos.permisosUsuarioActual()).willReturn("admin");
		mockMvc.perform(get("/usuarios/{usuario}", u.getUsuario()))
		.andExpect(status().isOk())
		.andExpect(model().attributeExists("usuarioLoggued"))
		.andExpect(model().attributeExists("usuario"))
		.andExpect(view().name("usuarios/miPerfil"));
	}

	@WithMockUser(value="spring")
	@Test
	public void testWelcomeUsuario() throws Exception {
		given(permisos.getNombreUsuarioActual()).willReturn(u.getUsuario());
		mockMvc.perform(get("/welcomeUsuario"))
		.andExpect(status().isOk())
		.andExpect(model().attributeExists("usuarioLoggued"))
		.andExpect(view().name("usuarios/welcomeUsuario"));
	}
}
