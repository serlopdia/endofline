package org.springframework.samples.endofline.partida;

import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.samples.endofline.configuration.SecurityConfiguration;
import org.springframework.samples.endofline.efecto.Efecto;
import org.springframework.samples.endofline.efecto.EfectoService;
import org.springframework.security.config.annotation.web.WebSecurityConfigurer;
import org.springframework.samples.endofline.carta.CartaService;
import org.springframework.samples.endofline.mazo.Mazo;
import org.springframework.samples.endofline.mazo.MazoService;
import org.springframework.samples.endofline.posicion.PosicionService;
import org.springframework.samples.endofline.posicion.Posicion;
import org.springframework.samples.endofline.usuario.Usuario;
import org.springframework.samples.endofline.usuario.UsuarioService;

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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

import org.springframework.samples.endofline.util.GetionPermisos;



@WebMvcTest(controllers = PartidaController.class,
excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = WebSecurityConfigurer.class), 
excludeAutoConfiguration = SecurityConfiguration.class)
public class PartidaControllerTest {
	
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
	MockMvc mockMvc;
	
	
	private static Usuario u;
	private static Usuario u2;
	private static Partida p;
	private static Mazo m, m2;
	
	@BeforeAll
	static void iniciar() {
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
		
		p=new Partida();
		p.setEstado(EstadoPartida.PENDIENTE);
		p.setGanador(null);
		LocalDateTime fechaHora= LocalDateTime.now();
		p.setFecha(fechaHora);
		p.setJ1(u);
		p.setJ2(u2);
		p.setId(4);
		p.setEfectos(new ArrayList<>());
		
		m=new Mazo();
		m.setCartas(new ArrayList<>());
		m.setJ(u);
		m.setPartida(p);
		m.setRonda(3);
		m.setTurno(true);
		m.setUsaEfecto(false);
		m.setId(5);
		
		m2=new Mazo();
		m2.setCartas(new ArrayList<>());
		m2.setJ(u2);
		m2.setPartida(p);
		m2.setRonda(3);
		m2.setTurno(true);
		m2.setUsaEfecto(false);
		m2.setId(6);

	}
	
	@WithMockUser(value = "spring")
	@Test
	public void testPartidasEnCurso() throws Exception {
		mockMvc.perform(get("/partidas/encurso"))
		.andExpect(status().isOk())
		.andExpect(model().attributeExists("partidas"))
		.andExpect(model().attributeExists("usuarioLoggued"))
		.andExpect(view().name("partidas/partidasListas"));
	}
	
	@WithMockUser(value = "spring")
	@Test
	public void testPartidasJugadas() throws Exception {
		mockMvc.perform(get("/partidas/jugadas"))
		.andExpect(status().isOk())
		.andExpect(model().attributeExists("partidas"))
		.andExpect(model().attributeExists("usuarioLoggued"))
		.andExpect(view().name("partidas/partidasListas"));
	}
	
	
	@WithMockUser(value = "spring")
	@Test
	public void testPartidasNew() throws Exception {
		given(usuarioService.getByUsuario(u.getUsuario())).willReturn(Optional.of(u));
		given(usuarioService.getByUsuario(u2.getUsuario())).willReturn(Optional.of(u2));
		given(permisos.getNombreUsuarioActual()).willReturn(u.getUsuario());
		given(partidaService.insert(p)).willReturn(p);
		
		mockMvc.perform(post("/partidas/new")
				.param("usuario", u2.getUsuario()).with(csrf()))
		.andExpect(status().isOk());
		
	}
	
	@WithMockUser(value = "spring")
	@Test
	public void testPartidasPendientes() throws Exception {
		mockMvc.perform(get("/partidas/pendientes/{usuarioId}", u2.getUsuario()))
		.andExpect(status().isOk());
		
	}
	
	@WithMockUser(value = "spring")
	@Test
	public void testAceptarPartidas() throws Exception {
		mockMvc.perform(get("/partidas/aceptar/{partidaId}", p.getId()))
		.andExpect(status().isOk());
	}
	
	@WithMockUser(value = "spring")
	@Test
	public void testRechazarPartidas() throws Exception {
		given(partidaService.getById(p.getId())).willReturn(Optional.of(p));
		mockMvc.perform(get("/partidas/rechazar/{partidaId}", p.getId()))
		.andExpect(status().is3xxRedirection());
	}
	

	@WithMockUser(value = "spring")
	@Test
	public void testComprobarEstadoPartidas() throws Exception {
		given(partidaService.getById(p.getId())).willReturn(Optional.of(p));
		mockMvc.perform(get("/partidas/comprobar/{partidaId}", p.getId()))
		.andExpect(status().isOk())
		.andExpect(view().name("partidas/cargando"));
	}
	
	@WithMockUser(value = "spring")
	@Test
	public void testPartidas() throws Exception {
		
		given(permisos.getNombreUsuarioActual()).willReturn(u.getUsuario());
		given(usuarioService.getByUsuario(u.getUsuario())).willReturn(Optional.of(u));
		given(usuarioService.getByUsuario(u2.getUsuario())).willReturn(Optional.of(u2));
		given(partidaService.getById(p.getId())).willReturn(Optional.of(p));
		given(mazoService.findByPartidaAndUsuario(p, u)).willReturn(Optional.of(m));
		given(mazoService.findByPartidaAndUsuario(p, u2)).willReturn(Optional.of(m2));
		given(posicionService.getPosicionAsListToTablero(p)).willReturn(new ArrayList<Posicion>());
		given(efectoService.getAllEfectosByPartidaUsuario(p, u)).willReturn(new ArrayList<Efecto>());
		given(efectoService.getAllEfectosByPartidaUsuario(p, u2)).willReturn(new ArrayList<Efecto>());
		p.setEstado(EstadoPartida.EN_CURSO);
		
		mockMvc.perform(get("/partidas/{partidaId}", p.getId()))
		.andExpect(status().isOk())
		.andExpect(model().attributeExists("j1"))
		.andExpect(model().attributeExists("j2"))
		.andExpect(model().attributeExists("numCartas"))
		.andExpect(model().attributeExists("mazo"))
		.andExpect(model().attributeExists("ultimoEfectoRonda"))
		.andExpect(model().attributeExists("e1"))
		.andExpect(model().attributeExists("e2"))
		.andExpect(model().attributeExists("posiciones"))
		.andExpect(model().attributeExists("usuarioLoggued"))
		.andExpect(model().attributeExists("now"))
		.andExpect(view().name("tablero"));
	}
	
	@WithMockUser(value = "spring")
	@Test
	public void testPartidasPropias() throws Exception {
		given(permisos.getNombreUsuarioActual()).willReturn(u.getUsuario());
		given(usuarioService.getByUsuario(u.getUsuario())).willReturn(Optional.of(u));
		given(partidaService.findByPartidaByUsuario(u)).willReturn(new ArrayList<>());
		
		mockMvc.perform(get("/partidas/propias/{usuario}", u.getUsuario()))
		.andExpect(status().isOk())
		.andExpect(model().attributeExists("partidas"))
		.andExpect(view().name("partidas/partidasJugador"));
		
	}
	

}
