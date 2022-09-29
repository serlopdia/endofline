package org.springframework.samples.endofline.carta;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.samples.endofline.mazo.Mazo;
import org.springframework.samples.endofline.mazo.MazoService;
import org.springframework.samples.endofline.partida.Partida;
import org.springframework.samples.endofline.posicion.Posicion;
import org.springframework.samples.endofline.posicion.PosicionService;
import org.springframework.samples.endofline.posicion.Rotacion;
import org.springframework.samples.endofline.usuario.Usuario;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CartaService {

	@Autowired
	CartaRepository cartaRepo;
	
	@Autowired
	MazoService mazoService;
	
	@Autowired
	PosicionService posicionService;

	Integer NUMERO_CARTAS_MAZO = 5;
	
	Integer CARTA_MAS_ALTA_AZUL = 8;
	
	Integer CARTA_MAS_BAJA_AZUL = 1;
	
	Integer CARTA_MAS_ALTA_ROSA = 16;
	
	Integer CARTA_MAS_BAJA_ROSA = 9;

	public Collection<Carta> findAll(){
		return cartaRepo.findAll();
	}
	
	public Carta findById(int id) {
		return cartaRepo.findById(id);
	}
	
	//Genera una carta aleatoria teniendo en cuenta el color de cada jugador
	@Transactional
    public Carta cartaAleatoria(Partida p, Usuario usuario) {
		
		if (p.getJ1().getUsuario()==usuario.getUsuario()) {
			// Genera número aleatorio entre 1 y 8 ambos inclusive (Devuelve un tipo Random)
			int id = (int)(Math.random()*(CARTA_MAS_ALTA_AZUL-CARTA_MAS_BAJA_AZUL)) + CARTA_MAS_BAJA_AZUL;
	        return cartaRepo.findById(id);			
		} else {
			// Genera número aleatorio entre 9 y 16 ambos inclusive (Devuelve un tipo Random)
			int id = (int)(Math.random()*(CARTA_MAS_ALTA_ROSA-CARTA_MAS_BAJA_ROSA)) + CARTA_MAS_BAJA_ROSA;	
	        return cartaRepo.findById(id);
		}
    }
	
	//Genera una lista de cartas dado un numero de cartasy las introduce en la lista del mazo
	@Transactional
    public List<Carta> generaListaCartas(Partida p, Usuario jugador, Integer numeroCartas) {
		
		Optional<Mazo> mazo = mazoService.findByPartidaAndUsuario(p, jugador);
		List<Carta> cartas=null;
		if(mazo.isEmpty()) {
			cartas=new ArrayList<>();
		}else {
			cartas=mazo.get().getCartas();
		}
		while(numeroCartas!=0) {
			
			Carta cartaGenerada=cartaAleatoria(p, jugador);
			
			if(obtenerCartaSegunMultiplicidad(p, cartaGenerada, jugador)==true) {
				
				cartas.add(cartaGenerada);
				numeroCartas-=1;
			}
		}
        return cartas;
    }
	
	//Dice si ya hay suficientes cartas de un id en una partida
	@Transactional
	public Boolean obtenerCartaSegunMultiplicidad(Partida p, Carta carta, Usuario usuario) {
		 Optional<List<Posicion>> cartas=posicionService.getPosicionesPorPartidaYCarta(p, carta);
		 Integer numCartas=0;
		if(cartas.isPresent()) {
			numCartas=cartas.get().size();
		}
		
		Optional<Mazo> mazo=mazoService.findByPartidaAndUsuario(p, usuario);
		if(mazo.isPresent()) {
			for(Carta c:mazo.get().getCartas()) {
				if(c.getId().equals(carta.getId())) {
					numCartas++;
				}
			}
		}
		
		if(numCartas>=carta.getMultiplicidad()) {
			return false;
		}else {
			
			return true;
		}
	}
	
	
	@Transactional
	public Carta rotarCarta(Carta carta, Rotacion rotacion) {
		
		Boolean abajo=carta.getSalida_abajo();
		Boolean arriba=carta.getSalida_arriba();
		Boolean izquierda=carta.getSalida_izquierda();
		Boolean derecha=carta.getSalida_derecha();
		
		Carta cartaNueva=new Carta();
		cartaNueva.setColor(carta.getColor());
		cartaNueva.setId(carta.getId());
		cartaNueva.setImagen_azul(carta.getImagen_azul());
		cartaNueva.setImagen_rosa(carta.getImagen_rosa());
		cartaNueva.setIniciativa(carta.getIniciativa());
		cartaNueva.setMultiplicidad(carta.getMultiplicidad());
		switch (rotacion) {
		case GRADO_0:
			cartaNueva.setSalida_abajo(abajo);
			cartaNueva.setSalida_arriba(arriba);
			cartaNueva.setSalida_derecha(derecha);
			cartaNueva.setSalida_izquierda(izquierda);
			break;
		case GRADO_90:
			cartaNueva.setSalida_abajo(izquierda);
			cartaNueva.setSalida_arriba(derecha);
			cartaNueva.setSalida_derecha(abajo);
			cartaNueva.setSalida_izquierda(arriba);
			break;
		case GRADO_180:
			cartaNueva.setSalida_abajo(arriba);
			cartaNueva.setSalida_arriba(abajo);
			cartaNueva.setSalida_derecha(izquierda);
			cartaNueva.setSalida_izquierda(derecha);
			break;
		case GRADO_270:
			cartaNueva.setSalida_abajo(derecha);
			cartaNueva.setSalida_arriba(izquierda);
			cartaNueva.setSalida_derecha(arriba);
			cartaNueva.setSalida_izquierda(abajo);
			break;
		}
		
		return cartaNueva;
	}
	
	@Transactional(readOnly = true)
    public Collection<Carta> findCartaByIniciativa(Integer iniciativa) throws DataAccessException {
        return cartaRepo.findByIniciativa(iniciativa);
    }
	
	@Transactional(rollbackFor=CambioDeTurnoNoDisponible.class)
	public void cambioManoInicial(Mazo m) throws CambioDeTurnoNoDisponible{
		if(m.getTurno()==true && m.getRonda()==1) {
			List<Carta> vacio = new ArrayList<>();
		m.setCartas(vacio);
		generaListaCartas(m.getPartida(), m.getJ(), NUMERO_CARTAS_MAZO);
		m.setUsadoCambioMano(true);
		}else {
			throw new CambioDeTurnoNoDisponible();
		}
	}
	
}