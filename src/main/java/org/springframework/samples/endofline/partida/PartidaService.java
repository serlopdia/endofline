package org.springframework.samples.endofline.partida;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.samples.endofline.usuario.Usuario;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PartidaService {

	@Autowired
	private PartidaRepository partidaRepository;
	
	@Transactional
	public List<Partida> getAllEnCurso(){
		return partidaRepository.findPartidasEnCurso();
	}

	@Transactional
	public Iterable<Partida> getAllJugadas() {
		return partidaRepository.findPartidasJugadas();
	}
	
	@Transactional
	public Iterable<Partida> getAll() {
		return partidaRepository.findAll();
	}
	
	@Transactional
	public Optional<Partida> getById(Integer id){
		return partidaRepository.findById(id);
	}
	
	@Transactional
	public Iterable<Partida> getPendientes(Usuario usuario){
		return partidaRepository.findPartidasPendientes(usuario);
	}
	
	@Transactional(rollbackFor= {PartidaEnCursoConGanadorException.class, PartidaAdminException.class, PartidaConUnMismoException.class})
	public Partida insert(Partida partida) throws PartidaEnCursoConGanadorException, PartidaAdminException, PartidaConUnMismoException{
		if(partida.getEstado()==EstadoPartida.EN_CURSO && partida.getGanador()!=null) {
			throw new PartidaEnCursoConGanadorException();
		}
		if(partida.getJ1().getPermiso_admin()=="admin" ||  partida.getJ2().getPermiso_admin()=="admin") {
			throw new PartidaAdminException();
		}
		if(partida.getJ1()==partida.getJ2() || partida.getJ1().equals(partida.getJ2())) {
			throw new PartidaConUnMismoException();
		}
		return partidaRepository.save(partida);
	}
	
	@Transactional
	public Partida aceptar(Integer id) {
		Optional<Partida> p = getById(id);
		Partida partidaEntity = p.get();
		partidaEntity.setEstado(EstadoPartida.EN_CURSO);
		return partidaRepository.save(partidaEntity);
	}
	
	@Transactional
	public Partida rechazar(Integer id) {
		Optional<Partida> p = getById(id);
		Partida partidaEntity = p.get();
		partidaEntity.setEstado(EstadoPartida.RECHAZADA);
		return partidaRepository.save(partidaEntity);
	}
	
	@Transactional
	public void finalizarPartida(Partida p, Usuario perdedor) {
		  Usuario ganador=null;
		  if(p.getJ1().equals(perdedor)) {
			  ganador=p.getJ2();
		  }else if(p.getJ2().equals(perdedor)) {
			  ganador=p.getJ1();
		  }
		  
		  p.setGanador(ganador);
		  p.setEstado(EstadoPartida.FINALIZADA);
		  
	}
	
	@Transactional
	public List<Partida> findByPartidaByUsuario(Usuario usuario){
		return partidaRepository.findPartidasByUsuario(usuario);
	}
	
	@Transactional
	public void delete(Partida p) {
		partidaRepository.delete(p);
	}
	
}
