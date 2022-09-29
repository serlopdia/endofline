package org.springframework.samples.endofline.usuario;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UsuarioService {

	@Autowired
	private UsuarioRepository usuarioRepository;
	
	@Transactional
	public int usuarioCount(){
		return (int) usuarioRepository.count();
	}
	
	@Transactional
	public Iterable<Usuario> getAll(){
		return usuarioRepository.findAll();
	}
	
	
	@Transactional
	public Usuario insert(Usuario usuario) {
		return usuarioRepository.save(usuario);
	}
	
	@Transactional
	public Optional<Usuario> getByUsuario(String usuario) {
		return usuarioRepository.findById(usuario);
	}
	
	@Transactional
	public Usuario edit(Usuario usuario) {
		return usuarioRepository.save(usuario);
	}
	
	@Transactional
	public void delete(Usuario usuario) {
		usuarioRepository.delete(usuario);
	}
	
	@Transactional
	public Page<Usuario> findAll(Pageable pageable){
		return usuarioRepository.findAll(pageable);
	}	

}
