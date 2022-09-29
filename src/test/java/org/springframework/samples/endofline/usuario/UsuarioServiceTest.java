package org.springframework.samples.endofline.usuario;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Service;

@DataJpaTest(includeFilters = @ComponentScan.Filter(Service.class))
public class UsuarioServiceTest {

		@Autowired
		private UsuarioService usuarioService;
		
		@Test
		public void testEditUsuario() {
			Usuario usuario=usuarioService.getByUsuario("adminSergio").get();
			usuario.setNombre("Rafael");
			usuarioService.edit(usuario);
			Usuario usuarioNew=usuarioService.getByUsuario("adminSergio").get();
			assertEquals("Rafael", usuarioNew.getNombre());
		}
		
		@Test
		public void testNewUsuario() {
			Usuario usuario=new Usuario();
			usuario.setUsuario("prueba");
			usuario.setNombre("prueba");
			usuario.setApellidos("prueba");
			usuario.setEmail("prueba@gmail.com");
			usuario.setContrasenya("12345");
			usuario.setPermiso_admin("player");
			usuarioService.edit(usuario);
			Usuario usuarioNew=usuarioService.getByUsuario(usuario.getUsuario()).get();
			assertEquals(usuario.getUsuario(), usuarioNew.getUsuario());
			assertEquals(usuario.getNombre(), usuarioNew.getNombre());
		}
		
		@Test
		public void testDeleteUsuario() {
			Usuario usuario=new Usuario();
			usuario.setUsuario("prueba");
			usuario.setNombre("prueba");
			usuario.setApellidos("prueba");
			usuario.setEmail("prueba@gmail.com");
			usuario.setContrasenya("12345");
			usuario.setPermiso_admin("admin");
			usuarioService.edit(usuario);
			Usuario usuarioNew=usuarioService.getByUsuario(usuario.getUsuario()).get();
			assertEquals(usuario.getUsuario(), usuarioNew.getUsuario());
			assertEquals(usuario.getNombre(), usuarioNew.getNombre());
			usuarioService.delete(usuarioNew);
			Optional<Usuario> usuarioDeleted=usuarioService.getByUsuario(usuario.getUsuario());
			assert(usuarioDeleted.isEmpty());
		}
		
		
}
