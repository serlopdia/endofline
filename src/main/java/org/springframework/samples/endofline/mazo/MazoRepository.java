package org.springframework.samples.endofline.mazo;

import java.util.Optional;

import org.springframework.dao.DataAccessException;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.samples.endofline.partida.Partida;
import org.springframework.samples.endofline.usuario.Usuario;

public interface MazoRepository extends CrudRepository <Mazo, Integer>{

	@Query("SELECT mazos FROM Mazo mazos WHERE mazos.partida=?1 and mazos.j=?2")
	Optional<Mazo> findMazoByPartidaAndUsuario(Partida partida, Usuario usuario) throws DataAccessException;
}
