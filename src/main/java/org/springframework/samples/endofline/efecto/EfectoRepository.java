package org.springframework.samples.endofline.efecto;

import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.samples.endofline.partida.Partida;
import org.springframework.samples.endofline.usuario.Usuario;

public interface EfectoRepository extends CrudRepository<Efecto, Integer>{
	
	@Query("SELECT efectos FROM Efecto efectos WHERE efectos.p= ?1 AND efectos.j= ?2")
	List<Efecto> findAllEfectosByPartidaUsuario(Partida p, Usuario j) throws DataAccessException;
	
	@Query("SELECT efectos FROM Efecto efectos WHERE efectos.p= ?1")
	List<Efecto> findAllEfectosByPartida(Partida p) throws DataAccessException;
}