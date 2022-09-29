package org.springframework.samples.endofline.efecto;

import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.samples.endofline.carta.Carta;
import org.springframework.samples.endofline.carta.CartaService;
import org.springframework.samples.endofline.configuration.SecurityConfiguration;
import org.springframework.samples.endofline.mazo.Mazo;
import org.springframework.samples.endofline.mazo.MazoService;
import org.springframework.samples.endofline.partida.EstadoPartida;
import org.springframework.samples.endofline.partida.Partida;
import org.springframework.samples.endofline.partida.PartidaAdminException;
import org.springframework.samples.endofline.partida.PartidaConUnMismoException;
import org.springframework.samples.endofline.partida.PartidaEnCursoConGanadorException;
import org.springframework.samples.endofline.partida.PartidaService;
import org.springframework.samples.endofline.posicion.PosicionService;
import org.springframework.samples.endofline.usuario.Usuario;
import org.springframework.samples.endofline.usuario.UsuarioService;
import org.springframework.samples.endofline.util.GetionPermisos;
import org.springframework.security.config.annotation.web.WebSecurityConfigurer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.mockito.BDDMockito.given;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@WebMvcTest(controllers = EfectoController.class,
	excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = WebSecurityConfigurer.class), 
	excludeAutoConfiguration = SecurityConfiguration.class)
public class EfectoControllerTest {
	
	@MockBean
	private PartidaService partidaService;
	
	@MockBean
	private GetionPermisos permisos;
	
	@MockBean
	private UsuarioService usuarioService;
	
	@MockBean
	private PosicionService posicionService;
	
	@MockBean
	private CartaService cartaService;
	
	@MockBean
	private MazoService mazoService;
	
	@MockBean
	private EfectoService efectoService;
	
	@Autowired
	private MockMvc mockMvc;
	
	
	private Usuario u;
	private Usuario u2;
	private Partida p;
	private Mazo m;
	private List<Carta> cartas;
	
	@BeforeEach
	void createUsuariosYPartida() throws PartidaEnCursoConGanadorException, PartidaAdminException, PartidaConUnMismoException{
		u=new Usuario();
		u.setApellidos("Gomez");
		u.setContrasenya("12345");
		u.setEmail("prueba@gmail.com");
		u.setNombre("Borja");
		u.setPermiso_admin("player");
		u.setUsuario("jP1");
		
		u2=new Usuario();
		u2.setApellidos("Gomez");
		u2.setContrasenya("12345");
		u2.setEmail("prueba@gmail.com");
		u2.setNombre("Borja");
		u2.setPermiso_admin("player");
		u2.setUsuario("jP2");

		
	}
	

	@WithMockUser(value = "spring",authorities = "player", username="jP1")
	@Test
	public void testNewEfecto() throws Exception {
		p=new Partida();
		p.setEstado(EstadoPartida.EN_CURSO);
		p.setGanador(null);
		LocalDateTime fechaHora= LocalDateTime.now();
		p.setFecha(fechaHora);
		p.setJ1(u);
		p.setJ2(u2);
		p.setId(4);

		given(permisos.getNombreUsuarioActual()).willReturn(u.getUsuario());
		given(usuarioService.getByUsuario(u.getUsuario())).willReturn(Optional.of(u));
		given(partidaService.getById(p.getId())).willReturn(Optional.of(p));

		mockMvc.perform(post("/efecto/new").param("boton", "aceleron").param("partida", p.getId().toString()).with(csrf()))
		.andExpect(status().isOk());

	}

}
