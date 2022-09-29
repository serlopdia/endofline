package org.springframework.samples.endofline.mazo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.samples.endofline.carta.Carta;
import org.springframework.samples.endofline.carta.CartaService;
import org.springframework.samples.endofline.efecto.Efecto;
import org.springframework.samples.endofline.efecto.EfectoService;
import org.springframework.samples.endofline.efecto.TipoEfecto;
import org.springframework.samples.endofline.partida.Partida;
import org.springframework.samples.endofline.usuario.Usuario;
import org.springframework.samples.endofline.posicion.PosicionRepository;
import org.springframework.samples.endofline.posicion.PosicionService;
import org.springframework.samples.endofline.posicion.Posicion;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MazoService {

	@Autowired
	public MazoRepository mazoRepository;

	@Autowired
	public PosicionService posicionService;

	@Autowired
	public CartaService cartaService;

	@Autowired
	public EfectoService efectoService;

	private Integer NUM_CARTAS_INICIALES = 5;
	private Integer CARTAS_RESTANTES_PRIMERA_RONDA = 4;
	private Integer CARTAS_NUEVAS_PRIMERA_RONDA = 1;
	private Integer CARTAS_RESTANTES_POR_RONDA = 3;
	private Integer PRIMERA_RONDA_EFECTO_DISPONIBLE = 2;
	private Integer CARTAS_RESTANTES_ACELERON=2;
	private Integer CARTAS_RESTANTES_FRENAZO_O_GAS_EXTRA=4;

	@Transactional
	public Mazo findById(Integer mazoId) {
		return mazoRepository.findById(mazoId).get();
	}

	@Transactional
	public void delete(Mazo mazo) {
		mazoRepository.delete(mazo);
	}

	@Transactional
	public Mazo add(Mazo mazo) {
		return mazoRepository.save(mazo);
	}

	@Transactional
	public Optional<Mazo> findByPartidaAndUsuario(Partida partida, Usuario usuario) {
		return mazoRepository.findMazoByPartidaAndUsuario(partida, usuario);
	}

	@Transactional
	public void crearMazoInicial(Usuario jugador, Partida p, Boolean turno) {
		Mazo mazoActual = new Mazo();
		mazoActual.setJ(jugador);
		mazoActual.setPartida(p);
		mazoActual.setTurno(turno);
		mazoActual.setUsaEfecto(false); // Nuevo
		mazoActual.setCartas(cartaService.generaListaCartas(p, jugador, NUM_CARTAS_INICIALES));
		mazoActual.setRonda(1);
		mazoActual.setUsadoCambioMano(false);
		Mazo mazoD = add(mazoActual);
	}

	@Transactional
	public void turnoInicio(Mazo m1, Mazo m2) {
		if (m1.getTurno() == m2.getTurno()) { // Escogeremos el turno inicial de manera aleatoria
			int numRandom = (int) (Math.random() * 2 + 1);
			switch (numRandom) {
			case 1:
				m1.setTurno(true);
				break;
			case 2:
				m2.setTurno(true);
				break;
			}
		}
	}

	@Transactional
	public void turnoNuevaRonda(Mazo m1, Mazo m2) {

		Partida partida = m1.getPartida();
		Usuario u1 = m1.getPartida().getJ1();
		Usuario u2 = m2.getPartida().getJ2();

		List<Posicion> listPosJ1 = posicionService.getByIdPartidaUsuario(partida, u1).get();
		List<Posicion> listPosJ2 = posicionService.getByIdPartidaUsuario(partida, u2).get();
		Collections.reverse(listPosJ1);
		Collections.reverse(listPosJ2);

		Integer tamanyoMenor;
		// En principio cada jugador debe haber puesto el mismo numero de cartas
		// pero en caso de que se haga uso de algunos efectos esto puede cambiar
		// por lo que como maximo se tendra en cuenta la iniciativa de las "n"
		// ultimas cartas de cada lista de posiciones,siendo "n" el tamaño de
		// la lista más pequeña

		if (listPosJ1.size() < listPosJ2.size()) {
			tamanyoMenor = listPosJ1.size();
		} else {
			tamanyoMenor = listPosJ2.size();
		}

		for (int i = 0; i < tamanyoMenor; i++) {
			Integer inicJ1 = listPosJ1.get(i).getCarta().getIniciativa();
			Integer inicJ2 = listPosJ2.get(i).getCarta().getIniciativa();

			if (inicJ1 < inicJ2) {
				m1.setTurno(true);
				m2.setTurno(false);
				break;
			} else if (inicJ2 < inicJ1) {
				m1.setTurno(false);
				m2.setTurno(true);
				break;
			}
		}
	}

	@Transactional
	public Mazo eliminarCarta(Mazo mazo, Carta carta) {
		List<Carta> cartas = mazo.getCartas();
		List<Carta> cartasFin = new ArrayList<>();
		Boolean quitada = false;
		for (Carta cartaLista : cartas) {
			if (!cartaLista.equals(carta)) {
				cartasFin.add(cartaLista);
			} else if (cartaLista.equals(carta) && quitada == true) {
				cartasFin.add(cartaLista);
			} else if (cartaLista.equals(carta) && quitada == false) {
				quitada = true;
			}
		}
		mazo.setCartas(cartasFin);
		if (mazo.getUsaEfecto()) {
			efectoService.cambiarTurnoAfterEfecto(mazo);
		} else {

			if (mazo.getRonda() == 1) {
				cambiarTurno(mazo);
			} else if (mazo.getRonda() > 1 && (mazo.getRonda() % 2) != 0
					&& mazo.getCartas().size() == CARTAS_RESTANTES_POR_RONDA) {
				cambiarTurno(mazo);

			} else if (mazo.getRonda() % 2 == 0 && mazo.getCartas().size() == CARTAS_RESTANTES_POR_RONDA) {
				cambiarTurno(mazo);
			}
		}

		return mazo;
	}

	@Transactional
	public Mazo cambiarTurno(Mazo mazo) {
		Usuario j1 = mazo.getPartida().getJ1();
		Usuario j2 = mazo.getPartida().getJ2();
		Mazo m1 = mazoRepository.findMazoByPartidaAndUsuario(mazo.getPartida(), j1).get();
		Mazo m2 = mazoRepository.findMazoByPartidaAndUsuario(mazo.getPartida(), j2).get();
		m1.setTurno(!m1.getTurno());
		m2.setTurno(!m2.getTurno());

		if (mazo.getJ().equals(j1)) {
			return m1;
		} else {
			return m2;
		}
	}

	@Transactional
	public void repartirCartas(Mazo mazo) {
		Usuario j1 = mazo.getPartida().getJ1();
		Usuario j2 = mazo.getPartida().getJ2();
		Mazo m1 = mazoRepository.findMazoByPartidaAndUsuario(mazo.getPartida(), j1).get();
		Mazo m2 = mazoRepository.findMazoByPartidaAndUsuario(mazo.getPartida(), j2).get();
		Integer c1 = m1.getCartas().size();
		Integer c2 = m2.getCartas().size();
		TipoEfecto e1 = null;
		TipoEfecto e2 = null;
		Integer cartasRestantes1 = CARTAS_RESTANTES_POR_RONDA;
		Integer cartasRestantes2 = CARTAS_RESTANTES_POR_RONDA;
		if (m1.getUsaEfecto()) {
			List<Efecto> efectosJ1 = efectoService.getAllEfectosByPartidaUsuario(m1.getPartida(), m1.getJ());
			e1 = efectosJ1.get(efectosJ1.size() - 1).getNombre();
		}
		if (m2.getUsaEfecto()) {
			List<Efecto> efectosJ2 = efectoService.getAllEfectosByPartidaUsuario(m2.getPartida(), m2.getJ());
			e2 = efectosJ2.get(efectosJ2.size() - 1).getNombre();
		}
		if (m1.getRonda() >= PRIMERA_RONDA_EFECTO_DISPONIBLE && m2.getRonda() >= PRIMERA_RONDA_EFECTO_DISPONIBLE) {
			if (e1 != null) {
				switch (e1) {
				case ACELERON:
					cartasRestantes1 = CARTAS_RESTANTES_ACELERON;
					break;
				case FRENAZO:
					cartasRestantes1 = CARTAS_RESTANTES_FRENAZO_O_GAS_EXTRA;
					break;
				case GAS_EXTRA:
					cartasRestantes1 = CARTAS_RESTANTES_FRENAZO_O_GAS_EXTRA;
					break;
				}
			}
			if (e2 != null) {
				switch (e2) {
				case ACELERON:
					cartasRestantes2 = CARTAS_RESTANTES_ACELERON;
					break;
				case FRENAZO:
					cartasRestantes2 = CARTAS_RESTANTES_FRENAZO_O_GAS_EXTRA;
					break;
				case GAS_EXTRA:
					cartasRestantes2 = CARTAS_RESTANTES_FRENAZO_O_GAS_EXTRA;
					break;
				}
			}
		}
		if (c1 == cartasRestantes1 && c2 == cartasRestantes2) {
			m1.setCartas(cartaService.generaListaCartas(m1.getPartida(), m1.getJ(), (NUM_CARTAS_INICIALES - cartasRestantes1)));
			m2.setCartas(cartaService.generaListaCartas(m2.getPartida(), m2.getJ(), (NUM_CARTAS_INICIALES - cartasRestantes2)));
			m1.setRonda(m1.getRonda() + 1);
			m2.setRonda(m2.getRonda() + 1);
			m1.setUsaEfecto(false);
			m2.setUsaEfecto(false);
			turnoNuevaRonda(m1, m2);
			add(m1);
			add(m2);
		}
	}

	@Transactional
	public void repartirCartasPrimeraRonda(Mazo mazo) {
		Usuario j1 = mazo.getPartida().getJ1();
		Usuario j2 = mazo.getPartida().getJ2();

		Mazo m1 = mazoRepository.findMazoByPartidaAndUsuario(mazo.getPartida(), j1).get();
		Mazo m2 = mazoRepository.findMazoByPartidaAndUsuario(mazo.getPartida(), j2).get();

		Integer c1 = m1.getCartas().size();
		Integer c2 = m2.getCartas().size();

		if (m1.getRonda() == 1 && m2.getRonda() == 1 && c1 == CARTAS_RESTANTES_PRIMERA_RONDA
				&& c2 == CARTAS_RESTANTES_PRIMERA_RONDA) {
			m1.setCartas(cartaService.generaListaCartas(m1.getPartida(), m1.getJ(), CARTAS_NUEVAS_PRIMERA_RONDA));
			m2.setCartas(cartaService.generaListaCartas(m2.getPartida(), m2.getJ(), CARTAS_NUEVAS_PRIMERA_RONDA));
			m1.setRonda(m1.getRonda() + 1);
			m2.setRonda(m2.getRonda() + 1);
			turnoNuevaRonda(m1, m2);
			add(m1);
			add(m2);
		}
	}

	@Transactional
	public void finalizarPartida(Partida p) {
		Mazo m1 = findByPartidaAndUsuario(p, p.getJ1()).get();
		m1.setTurno(false);
		Mazo m2 = findByPartidaAndUsuario(p, p.getJ2()).get();
		m2.setTurno(false);
		add(m1);
		add(m2);
	}
}