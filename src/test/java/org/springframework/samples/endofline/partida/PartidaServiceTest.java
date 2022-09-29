package org.springframework.samples.endofline.partida;



import static org.junit.jupiter.api.Assertions.assertEquals;
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
import org.springframework.samples.endofline.usuario.Usuario;
import org.springframework.samples.endofline.usuario.UsuarioService;
import org.springframework.stereotype.Service;

@DataJpaTest(includeFilters = @ComponentScan.Filter(Service.class))
@TestInstance(Lifecycle.PER_CLASS)
public class PartidaServiceTest {

	@Autowired
	public PartidaService partidaService;
	
	@Autowired
	public UsuarioService usuarioService;
	
	@BeforeEach
	void createUsuarios() {
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
		Usuario u3=new Usuario();
		u3.setApellidos("Gomez");
		u3.setContrasenya("12345");
		u3.setEmail("prueba@gmail.com");
		u3.setNombre("Borja");
		u3.setPermiso_admin("admin");
		u3.setUsuario("jA3");
		usuarioService.insert(u3);
	}
	
	@AfterEach
	void deleteUsuarios() {
		Optional<Usuario> u1=usuarioService.getByUsuario("jP1");
		Optional<Usuario> u2=usuarioService.getByUsuario("jP2");
		Optional<Usuario> u3=usuarioService.getByUsuario("jA3");
		usuarioService.delete(u1.get());
		usuarioService.delete(u2.get());
		usuarioService.delete(u3.get());
	}
	
	@Test
	public void testGetAllEnCurso() throws PartidaEnCursoConGanadorException, PartidaAdminException, PartidaConUnMismoException {
		Partida p=new Partida();
		p.setEstado(EstadoPartida.EN_CURSO);
		p.setGanador(null);
		LocalDateTime fechaHora= LocalDateTime.now();
		p.setFecha(fechaHora);
		Optional<Usuario> u3=usuarioService.getByUsuario("jP1");
		p.setJ1(usuarioService.getByUsuario("jP1").get());
		p.setJ2(usuarioService.getByUsuario("jP2").get());
		Integer partidasEnBBDD=(int)partidaService.getAllEnCurso().size();
		partidaService.insert(p);
		Integer partidasNuevasEnBBDD=partidaService.getAllEnCurso().size();
		assertTrue(partidasEnBBDD+1==partidasNuevasEnBBDD);
		partidaService.delete(p);
	}
	
	
	
	@Test
	public void testGetAllJugadas() throws PartidaEnCursoConGanadorException, PartidaAdminException, PartidaConUnMismoException {
		Partida p=new Partida();
		p.setEstado(EstadoPartida.FINALIZADA);
		Usuario ganador= usuarioService.getByUsuario("jP1").get();
		p.setGanador(ganador);
		LocalDateTime fechaHora= LocalDateTime.now();
		p.setFecha(fechaHora);
		p.setJ1(usuarioService.getByUsuario("jP1").get());
		p.setJ2(usuarioService.getByUsuario("jP2").get());
		Integer partidasEnBBDD=(int)((List<Partida>) partidaService.getAllJugadas()).size();
		partidaService.insert(p);
		Integer partidasNuevasEnBBDD=(int)((List<Partida>) partidaService.getAllJugadas()).size();
		assertEquals(partidasEnBBDD+1, partidasNuevasEnBBDD);
		partidaService.delete(p);
	}
	
	@Test
	public void testValidatorPartidasEnCursoConGanador() {
		Partida p=new Partida();
		p.setEstado(EstadoPartida.EN_CURSO);
		Usuario ganador= usuarioService.getByUsuario("jP1").get();
		p.setGanador(ganador);
		LocalDateTime fechaHora= LocalDateTime.now();
		p.setFecha(fechaHora);
		p.setJ1(usuarioService.getByUsuario("jP1").get());
		p.setJ2(usuarioService.getByUsuario("jP2").get());
		assertThrows(PartidaEnCursoConGanadorException.class, ()->partidaService.insert(p));
		partidaService.delete(p);
	}
	
	@Test
	public void testPartidaSinAdmin() throws PartidaEnCursoConGanadorException, PartidaAdminException, PartidaConUnMismoException {
		Partida p=new Partida();
		p.setEstado(EstadoPartida.FINALIZADA);
		Usuario ganador= usuarioService.getByUsuario("jP2").get();
		p.setGanador(ganador);
		LocalDateTime fechaHora= LocalDateTime.now();
		p.setFecha(fechaHora);
		p.setJ1(usuarioService.getByUsuario("jA3").get());
		p.setJ2(usuarioService.getByUsuario("jP2").get());
		assertThrows(PartidaAdminException.class, ()->partidaService.insert(p));
		partidaService.delete(p);
	}
	
	@Test
	public void testPartidasPendientes() throws PartidaEnCursoConGanadorException, PartidaAdminException, PartidaConUnMismoException {
		Partida p=new Partida();
		p.setEstado(EstadoPartida.PENDIENTE);
		Optional<Usuario> j1=usuarioService.getByUsuario("jP1");
		Optional<Usuario> j2=usuarioService.getByUsuario("jP2");
		p.setJ1(j1.get());
		p.setJ2(j2.get());
		p.setFecha(LocalDateTime.now());
		partidaService.insert(p);
		Iterable<Partida> partidasJ1=partidaService.getPendientes(j2.get());
		Boolean esta=false;
		while(esta==false && partidasJ1.iterator().hasNext()) {
			if(partidasJ1.iterator().next().equals(p)) {
				System.out.println("partida: "+partidasJ1.iterator().next());
				esta=true;
			}
		}
		assertTrue(esta);
		partidaService.delete(p);
	}
	
	@Test
	public void testFinalizarPartida() throws PartidaEnCursoConGanadorException, PartidaAdminException, PartidaConUnMismoException {
		Partida p=new Partida();
		p.setEstado(EstadoPartida.EN_CURSO);
		Optional<Usuario> j1=usuarioService.getByUsuario("jP1");
		Optional<Usuario> j2=usuarioService.getByUsuario("jP2");
		p.setJ1(j1.get());
		p.setJ2(j2.get());
		p.setFecha(LocalDateTime.now());
		Partida p1=partidaService.insert(p);
		partidaService.finalizarPartida(p, j1.get());
		Partida p2=partidaService.getById(p1.getId()).get();
		assertTrue(p2.getGanador().equals(j2.get()));
		assertTrue(p2.getEstado().equals(EstadoPartida.FINALIZADA));
		partidaService.delete(p);
	}
	
	@Test
	public void testPartidasDeUnJugador() throws PartidaEnCursoConGanadorException, PartidaAdminException, PartidaConUnMismoException {
		Optional<Usuario> j1=usuarioService.getByUsuario("jP1");
		List<Partida> partidasAntes=partidaService.findByPartidaByUsuario(j1.get());
		Optional<Usuario> j2=usuarioService.getByUsuario("jP2");
		Partida p=new Partida();
		p.setEstado(EstadoPartida.EN_CURSO);
		p.setJ1(j1.get());
		p.setJ2(j2.get());
		p.setFecha(LocalDateTime.now());
		Partida p1=partidaService.insert(p);
		List<Partida> partidasDespues=partidaService.findByPartidaByUsuario(j1.get());
		assertTrue(partidasAntes.size()+1==partidasDespues.size());
		partidaService.delete(p);
	}
}
