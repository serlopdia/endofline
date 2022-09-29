package org.springframework.samples.endofline.posicion;

import java.util.List;
import java.util.Optional;

import org.springframework.dao.DataAccessException;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.samples.endofline.carta.Carta;
import org.springframework.samples.endofline.partida.Partida;
import org.springframework.samples.endofline.usuario.Usuario;

public interface PosicionRepository extends CrudRepository <Posicion, Integer>{
	
	
	@Query("SELECT posiciones FROM Posicion posiciones WHERE posiciones.p=?1 ORDER BY posiciones.fila, posiciones.columna")
	Optional<List<Posicion>> findPosicionesByIdPartida(Partida partida) throws DataAccessException;
	
	@Query("SELECT posiciones FROM Posicion posiciones WHERE posiciones.p= ?1 AND posiciones.carta= ?2")
	Posicion findPosicionByPartidaCarta(Partida p, Carta c) throws DataAccessException;
	
	@Query("SELECT posiciones FROM Posicion posiciones WHERE posiciones.p= ?1 AND posiciones.j= ?2")
	Optional<List<Posicion>> findPosicionByPartidaUsuario(Partida p, Usuario j) throws DataAccessException;
	
	@Query("UPDATE Posicion posiciones set posiciones.gradoRotado= ?1 WHERE posiciones.carta= ?2 AND posiciones.p= ?3")
	void setRotacionByIdCartaPartida(Rotacion gradoRotado, Integer idCarta, String idPartida) throws DataAccessException;
	
	@Query("SELECT posiciones FROM Posicion posiciones WHERE posiciones.p=?1 AND posiciones.carta=?2")
	Optional<List<Posicion>> getPosicionesPorPartidaYCarta(Partida p, Carta c) throws DataAccessException;
	
	@Query("SELECT posiciones FROM Posicion posiciones WHERE posiciones.p=?1 AND posiciones.fila=?2 AND posiciones.columna=?3 AND posiciones.j=?4")
	Optional<Posicion> getPosicionesPorPartidaFilaColumnaYJugador(Partida p, Integer fila, Integer columna, Usuario j) throws DataAccessException;
	
	@Query("SELECT posiciones FROM Posicion posiciones WHERE posiciones.p=?1 ORDER BY posiciones.fila")
	Optional<List<Posicion>> getPosicionesPorPartidaOrdenadasPorFila(Partida p) throws DataAccessException;
	
	@Query("SELECT posiciones FROM Posicion posiciones WHERE posiciones.p=?1 ORDER BY posiciones.columna")
	Optional<List<Posicion>> getPosicionesPorPartidaOrdenadasPorColumna(Partida p) throws DataAccessException;
	
	@Query("SELECT posicion FROM Posicion posicion WHERE posicion.p= ?1 AND posicion.fila= ?2 AND posicion.columna= ?3")
	Optional<Posicion> findPosicionPorPartidaFilaColumna(Partida p, Integer fila, Integer columna) throws DataAccessException;
	
}
