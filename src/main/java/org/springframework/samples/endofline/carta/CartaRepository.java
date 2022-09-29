package org.springframework.samples.endofline.carta;

import java.util.Collection;

import org.springframework.dao.DataAccessException;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface CartaRepository extends CrudRepository<Carta, Integer>{
	Collection<Carta> findAll();
	Carta findById(int id) throws DataAccessException;
	@Query("SELECT DISTINCT carta FROM Carta carta WHERE carta.iniciativa=?1")
    public Collection<Carta> findByIniciativa(@Param("iniciativa") Integer iniciativa);	
}
