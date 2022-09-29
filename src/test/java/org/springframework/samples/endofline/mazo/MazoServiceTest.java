package org.springframework.samples.endofline.mazo;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
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
import org.springframework.samples.endofline.carta.Carta;
import org.springframework.samples.endofline.carta.CartaService;
import org.springframework.samples.endofline.partida.EstadoPartida;
import org.springframework.samples.endofline.partida.Partida;
import org.springframework.samples.endofline.partida.PartidaAdminException;
import org.springframework.samples.endofline.partida.PartidaConUnMismoException;
import org.springframework.samples.endofline.partida.PartidaEnCursoConGanadorException;
import org.springframework.samples.endofline.partida.PartidaService;
import org.springframework.samples.endofline.posicion.Posicion;
import org.springframework.samples.endofline.posicion.PosicionInvalidaLimiteDeTablero;
import org.springframework.samples.endofline.posicion.PosicionNoContinuaFlujo;
import org.springframework.samples.endofline.posicion.PosicionService;
import org.springframework.samples.endofline.posicion.PosicionYaUsada;
import org.springframework.samples.endofline.posicion.Rotacion;
import org.springframework.samples.endofline.usuario.Usuario;
import org.springframework.samples.endofline.usuario.UsuarioService;
import org.springframework.stereotype.Service;

@DataJpaTest(includeFilters = @ComponentScan.Filter(Service.class))
@TestInstance(Lifecycle.PER_CLASS)
public class MazoServiceTest {
	
	@Autowired
	MazoService mazoService;
	
	@Autowired
	PartidaService partidaService;
	
	@Autowired
	UsuarioService usuarioService;
	
	@Autowired
	CartaService cartaService;
	
	@Autowired
	PosicionService posicionService;
	
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
	public void testFindMazoByPartidaAndUsuario() throws PartidaEnCursoConGanadorException, PartidaAdminException, PartidaConUnMismoException {
		Partida p=new Partida();
		p.setEstado(EstadoPartida.EN_CURSO);
		p.setGanador(null);
		LocalDateTime fechaHora= LocalDateTime.now();
		p.setFecha(fechaHora);
		p.setJ1(usuarioService.getByUsuario("jP1").get());
		p.setJ2(usuarioService.getByUsuario("jP2").get());
		Partida p1=partidaService.insert(p);
		Usuario j1 = usuarioService.getByUsuario("jP1").get();
		
		List<Carta> cartas=cartaService.generaListaCartas(p1, j1, 5);
		Mazo m=new Mazo();
		m.setCartas(cartas);
		m.setJ(j1);
		m.setPartida(p1);
		m.setRonda(3);
		m.setTurno(true);
		m.setUsaEfecto(false);
		m=mazoService.add(m);
		Mazo m2 = mazoService.findByPartidaAndUsuario(p, j1).get();
		assertTrue(m.equals(m2));
		partidaService.delete(p);
		mazoService.delete(m);
	}
	
	@Test
	public void testCrearMazoInicial() throws PartidaEnCursoConGanadorException, PartidaAdminException, PartidaConUnMismoException {
		Partida p=new Partida();
		p.setEstado(EstadoPartida.EN_CURSO);
		p.setGanador(null);
		LocalDateTime fechaHora= LocalDateTime.now();
		p.setFecha(fechaHora);
		p.setJ1(usuarioService.getByUsuario("jP1").get());
		p.setJ2(usuarioService.getByUsuario("jP2").get());
		Partida p1=partidaService.insert(p);
		Usuario j1 = usuarioService.getByUsuario("jP1").get();
		mazoService.crearMazoInicial(j1, p1, false);
		Mazo m = mazoService.findByPartidaAndUsuario(p, j1).get();
		assertTrue(m.getCartas().size()==5);
		assertTrue(m.getTurno()==false);
		assertTrue(m.getJ().equals(j1));
		partidaService.delete(p);
	}
	
	@Test
	public void testTurnoInicial() throws PartidaEnCursoConGanadorException, PartidaAdminException, PartidaConUnMismoException {
		Usuario j2=usuarioService.getByUsuario("jP2").get();
		Usuario j1 = usuarioService.getByUsuario("jP1").get();
		Partida p=new Partida();
		p.setEstado(EstadoPartida.EN_CURSO);
		p.setGanador(null);
		LocalDateTime fechaHora= LocalDateTime.now();
		p.setFecha(fechaHora);
		p.setJ1(j1);
		p.setJ2(j2);
		Partida p1=partidaService.insert(p);
		
		List<Carta> cartas=cartaService.generaListaCartas(p1, j1, 5);
		Mazo m=new Mazo();
		m.setCartas(cartas);
		m.setJ(j1);
		m.setPartida(p1);
		m.setRonda(3);
		m.setTurno(false);
		m.setUsaEfecto(false);
		m=mazoService.add(m);
		
		List<Carta> cartas2=cartaService.generaListaCartas(p1, j2, 5);
		Mazo m2=new Mazo();
		m2.setCartas(cartas2);
		m2.setJ(j2);
		m2.setPartida(p1);
		m2.setRonda(3);
		m2.setTurno(false);
		m2.setUsaEfecto(false);
		m2=mazoService.add(m2);
		
		mazoService.turnoInicio(m, m2);
		
		assertTrue(m.getTurno()!=m2.getTurno());
		mazoService.delete(m);
		mazoService.delete(m2);
		
	}
	
	
	@Test
	public void testEliminarCarta() throws PartidaEnCursoConGanadorException, PartidaAdminException, PartidaConUnMismoException {
		Usuario j2=usuarioService.getByUsuario("jP2").get();
		Usuario j1 = usuarioService.getByUsuario("jP1").get();
		Partida p=new Partida();
		p.setEstado(EstadoPartida.EN_CURSO);
		p.setGanador(null);
		LocalDateTime fechaHora= LocalDateTime.now();
		p.setFecha(fechaHora);
		p.setJ1(j1);
		p.setJ2(j2);
		Partida p1=partidaService.insert(p);
		
		List<Carta> cartas=cartaService.generaListaCartas(p1, j1, 5);
		Mazo m=new Mazo();
		m.setCartas(cartas);
		m.setJ(j1);
		m.setPartida(p1);
		m.setRonda(3);
		m.setTurno(true);
		m.setUsaEfecto(false);
		m=mazoService.add(m);
		
		List<Carta> cartas2=cartaService.generaListaCartas(p1, j2, 5);
		Mazo m2=new Mazo();
		m2.setCartas(cartas2);
		m2.setJ(j2);
		m2.setPartida(p1);
		m2.setRonda(3);
		m2.setTurno(true);
		m2.setUsaEfecto(false);
		m2=mazoService.add(m2);
		
		mazoService.eliminarCarta(m, m.getCartas().get(0));
		Mazo m3 = mazoService.findByPartidaAndUsuario(p, j1).get();
		assertTrue(m3.getCartas().size()==4);
		partidaService.delete(p);
		mazoService.delete(m);
		mazoService.delete(m2);
	}
	
	@Test
	public void testCambiarTurno() throws PartidaEnCursoConGanadorException, PartidaAdminException, PartidaConUnMismoException {
		Usuario j2=usuarioService.getByUsuario("jP2").get();
		Usuario j1 = usuarioService.getByUsuario("jP1").get();
		Partida p=new Partida();
		p.setEstado(EstadoPartida.EN_CURSO);
		p.setGanador(null);
		LocalDateTime fechaHora= LocalDateTime.now();
		p.setFecha(fechaHora);
		p.setJ1(j1);
		p.setJ2(j2);
		Partida p1=partidaService.insert(p);
		
		List<Carta> cartas=cartaService.generaListaCartas(p1, j1, 5);
		Mazo m=new Mazo();
		m.setCartas(cartas);
		m.setJ(j1);
		m.setPartida(p1);
		m.setRonda(3);
		m.setTurno(true);
		m.setUsaEfecto(false);
		m=mazoService.add(m);
		
		List<Carta> cartas2=cartaService.generaListaCartas(p1, j2, 5);
		Mazo m2=new Mazo();
		m2.setCartas(cartas2);
		m2.setJ(j2);
		m2.setPartida(p1);
		m2.setRonda(3);
		m2.setTurno(false);
		m2.setUsaEfecto(false);
		m2=mazoService.add(m2);
		
		m=mazoService.cambiarTurno(m);
		assertTrue(m.getTurno()==false);
		assertTrue(m2.getTurno()==true);
		partidaService.delete(p);
		mazoService.delete(m);
		mazoService.delete(m2);
	}
	
	@Test
	public void testRepartirCartas() throws PartidaEnCursoConGanadorException, PartidaAdminException, PartidaConUnMismoException, PosicionYaUsada, PosicionNoContinuaFlujo, PosicionInvalidaLimiteDeTablero {
		Usuario j2=usuarioService.getByUsuario("jP2").get();
		Usuario j1 = usuarioService.getByUsuario("jP1").get();
		Partida p=new Partida();
		p.setEstado(EstadoPartida.EN_CURSO);
		p.setGanador(null);
		LocalDateTime fechaHora= LocalDateTime.now();
		p.setFecha(fechaHora);
		p.setJ1(j1);
		p.setJ2(j2);
		Partida p1=partidaService.insert(p);
		
		List<Carta> cartas=cartaService.generaListaCartas(p1, j1, 3);
		Mazo m=new Mazo();
		m.setCartas(cartas);
		m.setJ(j1);
		m.setPartida(p1);
		m.setRonda(3);
		m.setTurno(true);
		m.setUsaEfecto(false);
		m=mazoService.add(m);
		
		List<Carta> cartas2=cartaService.generaListaCartas(p1, j2, 3);
		Mazo m2=new Mazo();
		m2.setCartas(cartas2);
		m2.setJ(j2);
		m2.setPartida(p1);
		m2.setRonda(3);
		m2.setTurno(false);
		m2.setUsaEfecto(false);
		m2=mazoService.add(m2);
		
		Carta carta=cartaService.findById(1);
		Carta carta2=cartaService.findById(9);
		
		Posicion posicion2=new Posicion();
		posicion2.setFila(6);
		posicion2.setColumna(6);
		posicion2.setJ(j1);
		posicion2.setP(p1);
		posicion2.setFecha(fechaHora);
		posicion2.setCarta(carta);
		posicion2.setGradoRotado(Rotacion.GRADO_0);
		
		Posicion posicion3=new Posicion();
		posicion3.setFila(6);
		posicion3.setColumna(8);
		posicion3.setJ(j2);
		posicion3.setP(p1);
		posicion3.setFecha(fechaHora);
		posicion3.setCarta(carta2);
		posicion3.setGradoRotado(Rotacion.GRADO_0);
		posicionService.insert(posicion2);
		posicionService.insert(posicion3);
		mazoService.repartirCartas(m);
		assertTrue(m.getCartas().size()==5);
		assertTrue(m2.getCartas().size()==5);

		partidaService.delete(p);
		mazoService.delete(m);
		mazoService.delete(m2);
	}
	
	@Test
	public void testRepartirCartasPrimeraRonda() throws PartidaEnCursoConGanadorException, PartidaAdminException, PartidaConUnMismoException, PosicionYaUsada, PosicionNoContinuaFlujo, PosicionInvalidaLimiteDeTablero {
		Usuario j2=usuarioService.getByUsuario("jP2").get();
		Usuario j1 = usuarioService.getByUsuario("jP1").get();
		Partida p=new Partida();
		p.setEstado(EstadoPartida.EN_CURSO);
		p.setGanador(null);
		LocalDateTime fechaHora= LocalDateTime.now();
		p.setFecha(fechaHora);
		p.setJ1(j1);
		p.setJ2(j2);
		Partida p1=partidaService.insert(p);
		
		List<Carta> cartas=cartaService.generaListaCartas(p1, j1, 4);
		Mazo m=new Mazo();
		m.setCartas(cartas);
		m.setJ(j1);
		m.setPartida(p1);
		m.setRonda(1);
		m.setTurno(true);
		m.setUsaEfecto(false);
		m=mazoService.add(m);
		
		List<Carta> cartas2=cartaService.generaListaCartas(p1, j2, 4);
		Mazo m2=new Mazo();
		m2.setCartas(cartas2);
		m2.setJ(j2);
		m2.setPartida(p1);
		m2.setRonda(1);
		m2.setTurno(false);
		m2.setUsaEfecto(false);
		m2=mazoService.add(m2);
	
		Carta carta=cartaService.findById(1);
		Carta carta2=cartaService.findById(9);
		
		Posicion posicion2=new Posicion();
		posicion2.setFila(6);
		posicion2.setColumna(6);
		posicion2.setJ(j1);
		posicion2.setP(p1);
		posicion2.setFecha(fechaHora);
		posicion2.setCarta(carta);
		posicion2.setGradoRotado(Rotacion.GRADO_0);
		
		Posicion posicion3=new Posicion();
		posicion3.setFila(6);
		posicion3.setColumna(8);
		posicion3.setJ(j2);
		posicion3.setP(p1);
		posicion3.setFecha(fechaHora);
		posicion3.setCarta(carta2);
		posicion3.setGradoRotado(Rotacion.GRADO_0);
		posicionService.insert(posicion2);
		posicionService.insert(posicion3);
		
		mazoService.repartirCartasPrimeraRonda(m);
	
		
		assertTrue(m.getCartas().size()==5);
		assertTrue(m2.getCartas().size()==5);
		
		partidaService.delete(p);
		mazoService.delete(m);
		mazoService.delete(m2);

}
	
	@Test
	public void testFinalizarPartida() throws PartidaEnCursoConGanadorException, PartidaAdminException, PartidaConUnMismoException {
		Usuario j2=usuarioService.getByUsuario("jP2").get();
		Usuario j1 = usuarioService.getByUsuario("jP1").get();
		Partida p=new Partida();
		p.setEstado(EstadoPartida.EN_CURSO);
		p.setGanador(null);
		LocalDateTime fechaHora= LocalDateTime.now();
		p.setFecha(fechaHora);
		p.setJ1(j1);
		p.setJ2(j2);
		Partida p1=partidaService.insert(p);
		
		List<Carta> cartas=cartaService.generaListaCartas(p1, j1, 5);
		Mazo m=new Mazo();
		m.setCartas(cartas);
		m.setJ(j1);
		m.setPartida(p1);
		m.setRonda(1);
		m.setTurno(true);
		m.setUsaEfecto(false);
		m=mazoService.add(m);
		
		List<Carta> cartas2=cartaService.generaListaCartas(p1, j2, 5);
		Mazo m2=new Mazo();
		m2.setCartas(cartas2);
		m2.setJ(j2);
		m2.setPartida(p1);
		m2.setRonda(1);
		m2.setTurno(false);
		m2.setUsaEfecto(false);
		m2=mazoService.add(m2);
	
		mazoService.finalizarPartida(p1);
		assertTrue(!m.getTurno());

		partidaService.delete(p);
		mazoService.delete(m);
		mazoService.delete(m2);

}
	}
