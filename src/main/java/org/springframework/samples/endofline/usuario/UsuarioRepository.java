package org.springframework.samples.endofline.usuario;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;


public interface UsuarioRepository extends PagingAndSortingRepository<Usuario, String>{
	
	public Page<Usuario> findAll(Pageable pageable);

}