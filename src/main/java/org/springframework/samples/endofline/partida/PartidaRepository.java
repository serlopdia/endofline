package org.springframework.samples.endofline.partida;

import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.samples.endofline.usuario.Usuario;

public interface PartidaRepository extends CrudRepository<Partida, Integer>{

	@Query("SELECT partidas FROM Partida partidas WHERE partidas.estado='EN_CURSO' ORDER BY partidas.fecha")
	List<Partida> findPartidasEnCurso() throws DataAccessException;
	
	@Query("SELECT partidas FROM Partida partidas WHERE partidas.estado='FINALIZADA' ORDER BY partidas.fecha")
	Iterable<Partida> findPartidasJugadas() throws DataAccessException;

	@Query("SELECT partidas FROM Partida partidas WHERE partidas.estado='PENDIENTE' and partidas.j2=?1 ORDER BY partidas.fecha")
	Iterable<Partida> findPartidasPendientes(Usuario usuario) throws DataAccessException;
	
	@Query("SELECT partidas FROM Partida partidas WHERE partidas.j2=?1 or partidas.j1=?1 ORDER BY partidas.fecha")
	List<Partida> findPartidasByUsuario(Usuario usuario) throws DataAccessException;
	
}
