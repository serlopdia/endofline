package org.springframework.samples.endofline.posicion;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.samples.endofline.configuration.SecurityConfiguration;
import org.springframework.samples.endofline.efecto.EfectoService;
import org.springframework.security.config.annotation.web.WebSecurityConfigurer;
import org.springframework.samples.endofline.carta.Carta;
import org.springframework.samples.endofline.carta.CartaService;
import org.springframework.samples.endofline.mazo.Mazo;
import org.springframework.samples.endofline.mazo.MazoService;
import org.springframework.samples.endofline.usuario.Usuario;
import org.springframework.samples.endofline.usuario.UsuarioService;

import org.junit.jupiter.api.BeforeAll;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.security.test.context.support.WithMockUser;

import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

import org.springframework.samples.endofline.util.GetionPermisos;
import org.springframework.samples.endofline.partida.EstadoPartida;
import org.springframework.samples.endofline.partida.Partida;
import org.springframework.samples.endofline.partida.PartidaController;
import org.springframework.samples.endofline.partida.PartidaService;

@WebMvcTest(controllers = PosicionController.class,
excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = WebSecurityConfigurer.class), 
excludeAutoConfiguration = SecurityConfiguration.class)
public class PosicionControllerTest {
	@MockBean
	public PosicionService posicionService;
	
	@MockBean
	public UsuarioService usuarioService;
	
	@MockBean
	public PartidaService partidaService;
	
	@MockBean
	public GetionPermisos permisos;
	
	@MockBean
	public CartaService cartaService;
	
	@MockBean
	public MazoService mazoService;
	
	@MockBean
	public EfectoService efectoService;
	
	@MockBean
	public PartidaController partidaController;
	
	@Autowired
	public MockMvc mockMvc;
	
	
	private static Usuario u;
	private static Usuario u2;
	private static Partida p;
	private static Posicion posicion;
	private static Carta c;
	private static Mazo m;
	
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
		
		c=new Carta();
		c.setId(1);
		c.setImagen_azul("https://i.ibb.co/bQpCqxQ/azul-i1.png");
		c.setImagen_rosa("");
		c.setIniciativa(1);
		c.setMultiplicidad(8);
		c.setSalida_abajo(false);
		c.setSalida_arriba(true);
		c.setSalida_derecha(null);
		c.setSalida_izquierda(null);
		c.setColor("AZUL");

		m=new Mazo();
		m.setCartas(new ArrayList<>());
		m.setJ(u);
		m.setPartida(p);
		m.setRonda(3);
		m.setTurno(true);
		m.setUsaEfecto(false);
		m.setId(5);
		
		

	}
	
	@WithMockUser(value="spring")
	@Test
	public void testNewPosicion() throws Exception {
		given(usuarioService.getByUsuario(u.getUsuario())).willReturn(Optional.of(u));
		given(permisos.getNombreUsuarioActual()).willReturn(u.getUsuario());
		given(cartaService.findById(c.getId())).willReturn(c);
		given(mazoService.eliminarCarta(m, c)).willReturn(m);
		given(partidaService.getById(p.getId())).willReturn(Optional.of(p));
		given(posicionService.insert(posicion)).willReturn(posicion);
		given(mazoService.findByPartidaAndUsuario(p, u)).willReturn(Optional.of(new Mazo()));
		given(posicionService.rotacionByInteger(0)).willReturn(Rotacion.GRADO_0);
		
		posicion=new Posicion();
		posicion.setId(10);
		posicion.setP(p);
		posicion.setCarta(c);		
		posicion.setColumna(6);
		posicion.setFecha(LocalDateTime.now());
		posicion.setFila(6);
		posicion.setGradoRotado(Rotacion.GRADO_0);
		posicion.setJ(u);
		
		
		mockMvc.perform(post("/posicion/new")
				.param("carta", "1")
				.param("partida", "4")
				.param("fila", "6")
				.param("columna", "6")
				.param("rotacion", "0")
				.with(csrf()))
		.andExpect(status().isOk());
	}
	
	
}
