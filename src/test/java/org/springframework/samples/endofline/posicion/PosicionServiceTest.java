package org.springframework.samples.endofline.posicion;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.samples.endofline.carta.Carta;
import org.springframework.samples.endofline.carta.CartaService;
import org.springframework.samples.endofline.mazo.Mazo;
import org.springframework.samples.endofline.mazo.MazoService;
import org.springframework.samples.endofline.partida.EstadoPartida;
import org.springframework.samples.endofline.partida.Partida;
import org.springframework.samples.endofline.partida.PartidaAdminException;
import org.springframework.samples.endofline.partida.PartidaConUnMismoException;
import org.springframework.samples.endofline.partida.PartidaEnCursoConGanadorException;
import org.springframework.samples.endofline.partida.PartidaService;
import org.springframework.samples.endofline.usuario.Usuario;
import org.springframework.samples.endofline.usuario.UsuarioService;
import org.springframework.stereotype.Service;

@DataJpaTest(includeFilters = @ComponentScan.Filter(Service.class))
public class PosicionServiceTest {
	
	@Autowired
	public PartidaService partidaService;
	
	@Autowired
	public PosicionService posicionService;
	
	@Autowired
	public CartaService cartaService;
	
	@Autowired
	public UsuarioService usuarioService;
	
	@Autowired
	public MazoService mazoService;
	
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
	void testGetByIdPartida() {
	
		
		Partida p = partidaService.getById(2).get();
		List<Posicion> pos = posicionService.getByIdPartida(p).get();
		assertTrue(pos.size()==4);
	}
	
	
	@Test
	void testGetByIdPartidaUsuario() {
		
		Partida p = partidaService.getById(2).get();
		Usuario u = usuarioService.getByUsuario("jugador1").get();
		List<Posicion> pos = posicionService.getByIdPartidaUsuario(p, u).get();
		assertTrue(pos.size()==2);
		
	}
	

	@Test
	void testPosicionesPorPartidaYCarta() {
		
		Partida p = partidaService.getById(2).get();
		Carta c = cartaService.findById(1);
		List<Posicion> pos = posicionService.getPosicionesPorPartidaYCarta(p, c).get();
		assertTrue(pos.size()==2);
		
	}
	
	@Test
	void testPosicionesPorPartidaFilaColumnaYJugador() {
		
		Partida p = partidaService.getById(2).get();
		Usuario u = usuarioService.getByUsuario("jugador1").get();
		Posicion pos = posicionService.getPosicionesPorPartidaFilaColumnaYJugador(p, 1, 1, u).get();
		assertTrue(pos.getGradoRotado().toString()=="GRADO_0");
		
	}
	
	@Test
	void testPosicionesPorPartidaOrdenadasPorFila() {
		
		Partida p = partidaService.getById(2).get();
		List<Posicion> pos = posicionService.getPosicionesPorPartidaOrdenadasPorFila(p).get();
		assertTrue(pos.get(0).getFila()==1);
	}
	
	@Test
	void testPosicionesPorPartidaOrdenadasPorColumna() {
		
		Partida p = partidaService.getById(2).get();
		List<Posicion> pos = posicionService.getPosicionesPorPartidaOrdenadasPorColumna(p).get();
		assertTrue(pos.get(pos.size()-1).getColumna()==4);
	}
	
	@Test
	void testPosicionYaUsada() throws PartidaEnCursoConGanadorException, PartidaAdminException, PartidaConUnMismoException, PosicionYaUsada, PosicionNoContinuaFlujo, PosicionInvalidaLimiteDeTablero {
		Partida p=new Partida();
		p.setEstado(EstadoPartida.EN_CURSO);
		p.setGanador(null);
		LocalDateTime fechaHora= LocalDateTime.now();
		p.setFecha(fechaHora);
		p.setJ1(usuarioService.getByUsuario("jP1").get());
		p.setJ2(usuarioService.getByUsuario("jP2").get());
		Partida p1=partidaService.insert(p);
		Usuario j1 = usuarioService.getByUsuario("jP1").get();
		
		Carta carta=cartaService.findById(1);
		
		Posicion posicion1=new Posicion();
		posicion1.setFila(6);
		posicion1.setColumna(6);
		posicion1.setJ(j1);
		posicion1.setP(p1);
		posicion1.setFecha(fechaHora);
		posicion1.setCarta(carta);
		posicion1.setGradoRotado(Rotacion.GRADO_0);
		
		List<Carta> cartas=cartaService.generaListaCartas(p1, j1, 4);
		Mazo m=new Mazo();
		m.setCartas(cartas);
		m.setJ(j1);
		m.setPartida(p1);
		m.setRonda(3);
		m.setTurno(true);
		m.setUsaEfecto(false);
		m=mazoService.add(m);
		
		Posicion posicion2=new Posicion();
		posicion2.setFila(6);
		posicion2.setColumna(6);
		posicion2.setJ(j1);
		posicion2.setP(p1);
		posicion2.setFecha(fechaHora);
		posicion2.setCarta(carta);
		posicion2.setGradoRotado(Rotacion.GRADO_0);
		
		posicionService.insert(posicion1);
		
		assertThrows(PosicionYaUsada.class, ()->posicionService.insert(posicion2));
	}
	
	
	@Test
	public void testContinuaFlujo() throws PosicionYaUsada, PosicionNoContinuaFlujo, PosicionInvalidaLimiteDeTablero, PartidaEnCursoConGanadorException, PartidaAdminException, PartidaConUnMismoException {
		Partida p=new Partida();
		p.setEstado(EstadoPartida.EN_CURSO);
		p.setGanador(null);
		LocalDateTime fechaHora= LocalDateTime.now();
		p.setFecha(fechaHora);
		p.setJ1(usuarioService.getByUsuario("jP1").get());
		p.setJ2(usuarioService.getByUsuario("jP2").get());
		Partida p1=partidaService.insert(p);
		Usuario j1 = usuarioService.getByUsuario("jP1").get();
		
		Carta carta=cartaService.findById(1);
		
		Posicion posicion1=new Posicion();
		posicion1.setFila(6);
		posicion1.setColumna(6);
		posicion1.setJ(j1);
		posicion1.setP(p1);
		posicion1.setFecha(fechaHora);
		posicion1.setCarta(carta);
		posicion1.setGradoRotado(Rotacion.GRADO_0);
		
		List<Carta> cartas=cartaService.generaListaCartas(p1, j1, 4);
		Mazo m=new Mazo();
		m.setCartas(cartas);
		m.setJ(j1);
		m.setPartida(p1);
		m.setRonda(3);
		m.setTurno(true);
		m.setUsaEfecto(false);
		m=mazoService.add(m);
		
		Posicion posicion2=new Posicion();
		posicion2.setFila(5);
		posicion2.setColumna(6);
		posicion2.setJ(j1);
		posicion2.setP(p1);
		posicion2.setFecha(fechaHora);
		posicion2.setCarta(carta);
		posicion2.setGradoRotado(Rotacion.GRADO_180);
		
		posicionService.insert(posicion1);
		
		assertThrows(PosicionNoContinuaFlujo.class, ()->posicionService.insert(posicion2));
	}
	
	@Test
	public void testPosicionEnLimiteDelTablero() throws PartidaEnCursoConGanadorException, PartidaAdminException, PartidaConUnMismoException, PosicionYaUsada, PosicionNoContinuaFlujo, PosicionInvalidaLimiteDeTablero {
		Partida p=new Partida();
		Usuario j2=usuarioService.getByUsuario("jP2").get();
		Usuario j1 = usuarioService.getByUsuario("jP1").get();
		p.setEstado(EstadoPartida.EN_CURSO);
		p.setGanador(null);
		LocalDateTime fechaHora= LocalDateTime.now();
		p.setFecha(fechaHora);
		p.setJ1(j1);
		p.setJ2(j2);
		Partida p1=partidaService.insert(p);
		
		
		Carta carta=cartaService.findById(1);
		Carta carta2=cartaService.findById(2);
		Posicion posicion1=new Posicion();
		posicion1.setFila(6);
		posicion1.setColumna(6);
		posicion1.setJ(j1);
		posicion1.setP(p1);
		posicion1.setFecha(fechaHora);
		posicion1.setCarta(carta2);
		posicion1.setGradoRotado(Rotacion.GRADO_0);
		
		List<Carta> cartas=cartaService.generaListaCartas(p1, j1, 4);
		Mazo m=new Mazo();
		m.setCartas(cartas);
		m.setJ(j1);
		m.setPartida(p1);
		m.setRonda(3);
		m.setTurno(true);
		m.setUsaEfecto(false);
		m=mazoService.add(m);
		
		List<Carta> cartas2=cartaService.generaListaCartas(p1, j2, 4);
		Mazo m2=new Mazo();
		m2.setCartas(cartas2);
		m2.setJ(j2);
		m2.setPartida(p1);
		m2.setRonda(3);
		m2.setTurno(true);
		m2.setUsaEfecto(false);
		m2=mazoService.add(m2);
		
		Posicion posicion2=new Posicion();
		posicion2.setFila(6);
		posicion2.setColumna(5);
		posicion2.setJ(j1);
		posicion2.setP(p1);
		posicion2.setFecha(fechaHora);
		posicion2.setCarta(carta);
		posicion2.setGradoRotado(Rotacion.GRADO_90);
		
		Posicion posicion3=new Posicion();
		posicion3.setFila(6);
		posicion3.setColumna(4);
		posicion3.setJ(j1);
		posicion3.setP(p1);
		posicion3.setFecha(fechaHora);
		posicion3.setCarta(carta);
		posicion3.setGradoRotado(Rotacion.GRADO_90);
		
		Posicion posicion4=new Posicion();
		posicion4.setFila(6);
		posicion4.setColumna(3);
		posicion4.setJ(j1);
		posicion4.setP(p1);
		posicion4.setFecha(fechaHora);
		posicion4.setCarta(carta);
		posicion4.setGradoRotado(Rotacion.GRADO_90);
		
		Carta carta3=cartaService.findById(9);
		Carta carta4=cartaService.findById(11);
		Posicion posicion5=new Posicion();
		posicion5.setFila(6);
		posicion5.setColumna(8);
		posicion5.setJ(j2);
		posicion5.setP(p1);
		posicion5.setFecha(fechaHora);
		posicion5.setCarta(carta4);
		posicion5.setGradoRotado(Rotacion.GRADO_0);
		
		Posicion posicion6=new Posicion();
		posicion6.setFila(6);
		posicion6.setColumna(9);
		posicion6.setJ(j2);
		posicion6.setP(p1);
		posicion6.setFecha(fechaHora);
		posicion6.setCarta(carta3);
		posicion6.setGradoRotado(Rotacion.GRADO_270);
		
		Posicion posicion7=new Posicion();
		posicion7.setFila(6);
		posicion7.setColumna(10);
		posicion7.setJ(j2);
		posicion7.setP(p1);
		posicion7.setFecha(fechaHora);
		posicion7.setCarta(carta3);
		posicion7.setGradoRotado(Rotacion.GRADO_270);
		posicionService.insert(posicion1);
		posicionService.insert(posicion2);
		posicionService.insert(posicion3);
		posicionService.insert(posicion4);
		posicionService.insert(posicion5);
		posicionService.insert(posicion6);
		assertThrows(PosicionInvalidaLimiteDeTablero.class, ()->posicionService.insert(posicion7));
		
	}
	
	@Test
	public void testRotacionByInteger() {
		Rotacion r=posicionService.rotacionByInteger(0);
		assertTrue(r.equals(Rotacion.GRADO_0));
	}
	
}
