package org.springframework.samples.endofline.efecto;

import static org.junit.jupiter.api.Assertions.assertThrows;
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
import org.springframework.samples.endofline.mazo.Mazo;
import org.springframework.samples.endofline.mazo.MazoService;
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
public class EfectoServiceTest {

	@Autowired
	EfectoService efectoService;
	
	@Autowired
	PartidaService partidaService;
	
	@Autowired
	UsuarioService usuarioService;
	
	@Autowired
	MazoService mazoService;
	
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
		
		Partida p=new Partida();
		p.setEstado(EstadoPartida.EN_CURSO);
		p.setGanador(null);
		LocalDateTime fechaHora= LocalDateTime.now();
		p.setFecha(fechaHora);
		p.setJ1(usuarioService.getByUsuario("jP1").get());
		p.setJ2(usuarioService.getByUsuario("jP2").get());
		p.setId(50);
		partidaService.insert(p);
	}
	
	@AfterEach
	void deleteUsuarios() {
		Optional<Usuario> u1=usuarioService.getByUsuario("jP1");
		Optional<Usuario> u2=usuarioService.getByUsuario("jP2");
		usuarioService.delete(u1.get());
		usuarioService.delete(u2.get());
	}
	
	@Test
	public void testTodosLosEfectosDeUnaPartidaYUsuario() throws PartidaEnCursoConGanadorException, PartidaAdminException, PartidaConUnMismoException {
		Partida p=new Partida();
		p.setEstado(EstadoPartida.EN_CURSO);
		p.setGanador(null);
		LocalDateTime fechaHora= LocalDateTime.now();
		p.setFecha(fechaHora);
		Usuario j1=usuarioService.getByUsuario("jP1").get();
		p.setJ1(j1);
		p.setJ2(usuarioService.getByUsuario("jP2").get());
		Partida p1=partidaService.insert(p);
		
		Efecto e1=new Efecto();
		e1.setJ(j1);
		e1.setP(p1);	
		e1.setNombre(TipoEfecto.ACELERON);
		e1.setRonda(3);
		e1=efectoService.save(e1);
		
		Efecto e2=new Efecto();
		e2.setJ(j1);
		e2.setP(p1);	
		e2.setNombre(TipoEfecto.FRENAZO);
		e2.setRonda(4);
		e2=efectoService.save(e2);
		
		List<Efecto> efectos=efectoService.getAllEfectosByPartidaUsuario(p1, j1);
		assertTrue(efectos.size()==2);
		assertTrue(efectos.get(0).equals(e1));
		assertTrue(efectos.get(1).equals(e2));
		partidaService.delete(p);
	}
	
	@Test
	public void testUltimoEfectoDeUnUsuarioEnPartida() throws PartidaEnCursoConGanadorException, PartidaAdminException, PartidaConUnMismoException {
		Partida p=new Partida();
		p.setEstado(EstadoPartida.EN_CURSO);
		p.setGanador(null);
		LocalDateTime fechaHora= LocalDateTime.now();
		p.setFecha(fechaHora);
		Usuario j1=usuarioService.getByUsuario("jP1").get();
		p.setJ1(j1);
		p.setJ2(usuarioService.getByUsuario("jP2").get());
		Partida p1=partidaService.insert(p);
		
		Efecto e1=new Efecto();
		e1.setJ(j1);
		e1.setP(p1);	
		e1.setNombre(TipoEfecto.ACELERON);
		e1.setRonda(3);
		e1=efectoService.save(e1);
		
		Efecto e2=new Efecto();
		e2.setJ(j1);
		e2.setP(p1);	
		e2.setNombre(TipoEfecto.FRENAZO);
		e2.setRonda(4);
		e2=efectoService.save(e2);
		Efecto ultimoEfecto=efectoService.getLastEfectoByPartida(p1);
		assertTrue(ultimoEfecto.equals(e2));
		partidaService.delete(p);
	}
	
	@Test
	public void testParseTipoEfecto() {
		TipoEfecto e=efectoService.parseTipoEfecto("aceleron");
		assertTrue(e.equals(TipoEfecto.ACELERON));
	}
	
	@Test
	public void testGasExtra() throws PartidaEnCursoConGanadorException, PartidaAdminException, PartidaConUnMismoException, SinEnergiaException, RondaParaEfectoException, EfectoImposibleException {
		Partida p=new Partida();
		p.setEstado(EstadoPartida.EN_CURSO);
		p.setGanador(null);
		LocalDateTime fechaHora= LocalDateTime.now();
		p.setFecha(fechaHora);
		Usuario j1=usuarioService.getByUsuario("jP1").get();
		p.setJ1(j1);
		p.setJ2(usuarioService.getByUsuario("jP2").get());
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
		
		efectoService.newEfecto("gasExtra", j1, p1);
		assertTrue(m.getCartas().size()==6);
		assertTrue(m.getUsaEfecto()==true);
		
		Efecto e=efectoService.getAllEfectosByPartidaUsuario(p1, j1).get(0);
		assertTrue(e.getNombre().equals(TipoEfecto.GAS_EXTRA));
		partidaService.delete(p);
	}
	
	@Test
	public void testAceleron() throws PartidaEnCursoConGanadorException, PartidaAdminException, PartidaConUnMismoException, SinEnergiaException, RondaParaEfectoException, EfectoImposibleException {
		Usuario j2=usuarioService.getByUsuario("jP2").get();
		Partida p=new Partida();
		p.setEstado(EstadoPartida.EN_CURSO);
		p.setGanador(null);
		LocalDateTime fechaHora= LocalDateTime.now();
		p.setFecha(fechaHora);
		Usuario j1=usuarioService.getByUsuario("jP1").get();
		p.setJ1(j1);
		p.setJ2(usuarioService.getByUsuario("jP2").get());
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
		
		efectoService.newEfecto("aceleron", j1, p1);
		
		mazoService.eliminarCarta(m, m.getCartas().get(0));
		Mazo m3 = mazoService.findByPartidaAndUsuario(p, j1).get();
		mazoService.eliminarCarta(m3, m3.getCartas().get(0));
		Mazo m4 = mazoService.findByPartidaAndUsuario(p, j1).get();
		mazoService.eliminarCarta(m4, m4.getCartas().get(0));
		Mazo m5 = mazoService.findByPartidaAndUsuario(p, j1).get();
		
	

		assertTrue(m5.getTurno()==false);
		assertTrue(m.getUsaEfecto()==true);
		
		
		Efecto e=efectoService.getAllEfectosByPartidaUsuario(p1, j1).get(0);
		assertTrue(e.getNombre().equals(TipoEfecto.ACELERON));
		partidaService.delete(p);
	}
	
	@Test
	public void testFrenazo() throws PartidaEnCursoConGanadorException, PartidaAdminException, PartidaConUnMismoException, SinEnergiaException, RondaParaEfectoException, EfectoImposibleException {
		Usuario j2=usuarioService.getByUsuario("jP2").get();
		Partida p=new Partida();
		p.setEstado(EstadoPartida.EN_CURSO);
		p.setGanador(null);
		LocalDateTime fechaHora= LocalDateTime.now();
		p.setFecha(fechaHora);
		Usuario j1=usuarioService.getByUsuario("jP1").get();
		p.setJ1(j1);
		p.setJ2(usuarioService.getByUsuario("jP2").get());
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
		

		mazoService.eliminarCarta(m, m.getCartas().get(0));
		Mazo m3 = mazoService.findByPartidaAndUsuario(p, j1).get();
		
		efectoService.newEfecto("frenazo", j1, p1);
		
		assertTrue(m3.getTurno()==false);
		assertTrue(m.getCartas().size()==4);
		assertTrue(m.getUsaEfecto()==true);

		
		
	
		Efecto e=efectoService.getAllEfectosByPartidaUsuario(p1, j1).get(0);
		assertTrue(e.getNombre().equals(TipoEfecto.FRENAZO));
		partidaService.delete(p);
	}
	
	@Test
	public void testSinEnergiaParaEfecto() throws PartidaEnCursoConGanadorException, PartidaAdminException, PartidaConUnMismoException {
		Partida p=new Partida();
		p.setEstado(EstadoPartida.EN_CURSO);
		p.setGanador(null);
		LocalDateTime fechaHora= LocalDateTime.now();
		p.setFecha(fechaHora);
		Usuario j1=usuarioService.getByUsuario("jP1").get();
		p.setJ1(j1);
		p.setJ2(usuarioService.getByUsuario("jP2").get());
		Partida p1=partidaService.insert(p);
		List<Carta> cartas=cartaService.generaListaCartas(p1, j1, 5);
		
		Efecto e1=new Efecto();
		e1.setJ(j1);
		e1.setP(p1);	
		e1.setNombre(TipoEfecto.ACELERON);
		e1.setRonda(3);
		e1=efectoService.save(e1);
		
		Efecto e2=new Efecto();
		e2.setJ(j1);
		e2.setP(p1);	
		e2.setNombre(TipoEfecto.FRENAZO);
		e2.setRonda(4);
		e2=efectoService.save(e2);
		
		Efecto e3=new Efecto();
		e3.setJ(j1);
		e3.setP(p1);	
		e3.setNombre(TipoEfecto.ACELERON);
		e3.setRonda(5);
		e3=efectoService.save(e3);
		
		Mazo m=new Mazo();
		m.setCartas(cartas);
		m.setJ(j1);
		m.setPartida(p1);
		m.setRonda(6);
		m.setTurno(true);
		m.setUsaEfecto(false);
		m=mazoService.add(m);
		assertThrows(SinEnergiaException.class, ()->efectoService.newEfecto("gasExtra", j1, p1));
		partidaService.delete(p);
		
	}
	
	@Test
	public void testRondaParaEfectoException() throws PartidaEnCursoConGanadorException, PartidaAdminException, PartidaConUnMismoException {
		Partida p=new Partida();
		p.setEstado(EstadoPartida.EN_CURSO);
		p.setGanador(null);
		LocalDateTime fechaHora= LocalDateTime.now();
		p.setFecha(fechaHora);
		Usuario j1=usuarioService.getByUsuario("jP1").get();
		p.setJ1(j1);
		p.setJ2(usuarioService.getByUsuario("jP2").get());
		Partida p1=partidaService.insert(p);
		List<Carta> cartas=cartaService.generaListaCartas(p1, j1, 5);
		
		Mazo m=new Mazo();
		m.setCartas(cartas);
		m.setJ(j1);
		m.setPartida(p1);
		m.setRonda(2);
		m.setTurno(true);
		m.setUsaEfecto(false);
		m=mazoService.add(m);
		
		assertThrows(RondaParaEfectoException.class, ()->efectoService.newEfecto("gasExtra", j1, p1));
		partidaService.delete(p);
		
	}
	
	@Test
	public void testCambiarTurnoAfterEfecto() throws PartidaEnCursoConGanadorException, PartidaAdminException, PartidaConUnMismoException {
		Partida p=new Partida();
		p.setEstado(EstadoPartida.EN_CURSO);
		p.setGanador(null);
		LocalDateTime fechaHora= LocalDateTime.now();
		p.setFecha(fechaHora);
		Usuario j1=usuarioService.getByUsuario("jP1").get();
		p.setJ1(j1);
		Usuario j2=usuarioService.getByUsuario("jP2").get();
		p.setJ2(j2);
		Partida p1=partidaService.insert(p);
		List<Carta> cartas=cartaService.generaListaCartas(p1, j1, 2);
		
		Mazo m=new Mazo();
		m.setCartas(cartas);
		m.setJ(j1);
		m.setPartida(p1);
		m.setRonda(3);
		m.setTurno(true);
		m.setUsaEfecto(false);
		m=mazoService.add(m);
		
		Mazo m2=new Mazo();
		m2.setCartas(cartas);
		m2.setJ(j2);
		m2.setPartida(p1);
		m2.setRonda(3);
		m2.setTurno(false);
		m2.setUsaEfecto(false);
		m2=mazoService.add(m2);
		
		Efecto e1=new Efecto();
		e1.setJ(j1);
		e1.setP(p1);	
		e1.setNombre(TipoEfecto.ACELERON);
		e1.setRonda(3);
		e1=efectoService.save(e1);
		
		efectoService.cambiarTurnoAfterEfecto(m);
		
		Mazo mDespues=mazoService.findById(m.getId());
		Mazo m2Despues=mazoService.findById(m2.getId());
		assertTrue(mDespues.getTurno()==false);
		assertTrue(m2Despues.getTurno()==true);
		partidaService.delete(p);
	}
	
	
}
