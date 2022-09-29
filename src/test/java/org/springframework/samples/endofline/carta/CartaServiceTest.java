package org.springframework.samples.endofline.carta;

import static org.assertj.core.api.Assertions.assertThat;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.samples.endofline.usuario.Usuario;
import org.springframework.samples.endofline.usuario.UsuarioService;
import org.springframework.samples.endofline.partida.EstadoPartida;
import org.springframework.samples.endofline.partida.Partida;
import org.springframework.samples.endofline.partida.PartidaAdminException;
import org.springframework.samples.endofline.partida.PartidaConUnMismoException;
import org.springframework.samples.endofline.partida.PartidaEnCursoConGanadorException;
import org.springframework.samples.endofline.partida.PartidaService;
import org.springframework.samples.endofline.posicion.Rotacion;
import org.springframework.stereotype.Service;



@DataJpaTest(includeFilters = @ComponentScan.Filter(Service.class))
@TestInstance(Lifecycle.PER_CLASS)
public class CartaServiceTest {

	@Autowired
	private CartaService cartaService;
	
	@Autowired
	private UsuarioService usuarioService;
	
	@Autowired
	private PartidaService partidaService;
	
	@BeforeEach
	void createUsuariosYPartida() throws PartidaEnCursoConGanadorException, PartidaAdminException, PartidaConUnMismoException{
		Usuario u=new Usuario();
		u.setApellidos("Gomez");
		u.setContrasenya("12345");
		u.setEmail("prueba@gmail.com");
		u.setNombre("Borja");
		u.setPermiso_admin("player");
		u.setUsuario("jP1");
		usuarioService.insert(u);
		Usuario u2=new Usuario();
		u2.setApellidos("Gomez");
		u2.setContrasenya("12345");
		u2.setEmail("prueba@gmail.com");
		u2.setNombre("Borja");
		u2.setPermiso_admin("player");
		u2.setUsuario("jP2");
		usuarioService.insert(u2);
	}
	
	@AfterEach
	void deleteUsuarios() {
		Optional<Usuario> u1=usuarioService.getByUsuario("jP1");
		Optional<Usuario> u2=usuarioService.getByUsuario("jP2");
		usuarioService.delete(u1.get());
		usuarioService.delete(u2.get());
	}
	
	@Test
	void shouldFindCartaWithCorrectId() {
		Carta carta2 = this.cartaService.findById(2);
		assertThat(carta2.getIniciativa()).isEqualTo(2);
		assertThat(carta2.getSalida_arriba()).isEqualTo(null);
		assertThat(carta2.getSalida_abajo()).isEqualTo(false);
		assertThat(carta2.getSalida_izquierda()).isEqualTo(true);
		assertThat(carta2.getSalida_derecha()).isEqualTo(null);
	}
	
	@Test
	void shouldFindAllCarta() {
		int total_Cartas = this.cartaService.findAll().size();
		assertThat(total_Cartas==9);
	}
	
	@Test
	public void testCartaCorrectaPorColor() throws PartidaEnCursoConGanadorException, PartidaAdminException, PartidaConUnMismoException {
		Partida p=new Partida();
		p.setEstado(EstadoPartida.EN_CURSO);
		p.setGanador(null);
		LocalDateTime fechaHora= LocalDateTime.now();
		p.setFecha(fechaHora);
		p.setJ1(usuarioService.getByUsuario("jP1").get());
		p.setJ2(usuarioService.getByUsuario("jP2").get());
		Partida p1=partidaService.insert(p);
		Usuario u=usuarioService.getByUsuario("jP1").get();
		Carta c=cartaService.cartaAleatoria(p1, u);
		assertTrue(c.getImagen_azul()!=null);
		assertTrue(c.getImagen_rosa()=="");
		assertTrue(c.getColor().equals("AZUL"));
		partidaService.delete(p);
	}
	
	@Test
	public void testGeneraListaDeCartas() throws PartidaEnCursoConGanadorException, PartidaAdminException, PartidaConUnMismoException {
		Partida p=new Partida();
		p.setEstado(EstadoPartida.EN_CURSO);
		p.setGanador(null);
		LocalDateTime fechaHora= LocalDateTime.now();
		p.setFecha(fechaHora);
		p.setJ1(usuarioService.getByUsuario("jP1").get());
		p.setJ2(usuarioService.getByUsuario("jP2").get());
		Partida p1=partidaService.insert(p);
		Usuario u=usuarioService.getByUsuario("jP1").get();
		List<Carta> cartas=cartaService.generaListaCartas(p1, u, 5);
		Boolean azules=true;
		for(Carta c:cartas) {
			if(!c.getColor().equals("AZUL")) {
				azules=false;
				break;
			}
		}
		assertTrue(cartas.size()==5);
		assertTrue(azules);
		partidaService.delete(p);
	}
	
	@Test
	public void rotacionCarta() {
		Carta c=cartaService.findById(1);
		Carta cRotada=cartaService.rotarCarta(c, Rotacion.GRADO_180);
		assertTrue(cRotada.getSalida_abajo()==true);
		assertTrue(cRotada.getSalida_arriba()==false);
		assertTrue(cRotada.getSalida_derecha()==null);
		assertTrue(cRotada.getSalida_izquierda()==null);
	}
	
	@Test
    void shouldFindCartaByIniciativa() {
        Collection<Carta> cartas = this.cartaService.findCartaByIniciativa(1);
        assertTrue(cartas.size()==2);
        
        Collection<Carta> cartas2 = this.cartaService.findCartaByIniciativa(3);
        assertTrue(cartas2.size()==4);
    }
	
	@Test
	public void testCartaSegunMultiplicidad() throws PartidaEnCursoConGanadorException, PartidaAdminException, PartidaConUnMismoException {
		Partida p=new Partida();
		p.setEstado(EstadoPartida.EN_CURSO);
		p.setGanador(null);
		LocalDateTime fechaHora= LocalDateTime.now();
		p.setFecha(fechaHora);
		p.setJ1(usuarioService.getByUsuario("jP1").get());
		p.setJ2(usuarioService.getByUsuario("jP2").get());
		Partida p1=partidaService.insert(p);
		Usuario u=usuarioService.getByUsuario("jP1").get();
		List<Carta> cartas=cartaService.generaListaCartas(p1, u, 5);
		Carta carta = cartas.get(0);
		Boolean res = cartaService.obtenerCartaSegunMultiplicidad(p1, carta, u);
		
		assertTrue(res);
		partidaService.delete(p);
	}
	
	
	
	
}