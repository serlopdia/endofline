package org.springframework.samples.endofline.posicion;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.samples.endofline.carta.Carta;
import org.springframework.samples.endofline.carta.CartaService;
import org.springframework.samples.endofline.efecto.EfectoService;
import org.springframework.samples.endofline.efecto.TipoEfecto;
import org.springframework.samples.endofline.mazo.Mazo;
import org.springframework.samples.endofline.mazo.MazoService;
import org.springframework.samples.endofline.partida.Partida;
import org.springframework.samples.endofline.usuario.Usuario;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PosicionService {

	private Integer NUMERO_FILAS_COLUMNAS_TABLERO = 13;
	private Integer FILA_CARTA_INICIO=7;
	private Integer COLUMNA_CARTA_INICIO_AZUL=6;
	private Integer COLUMNA_CARTA_INICIO_ROJO=8;
	private Integer NUMERO_POSICIONES_BORDE_CONTRARIO=6;
	private Integer FILA_PRIMERA_CARTA=6;
	private Integer COLUMNA_PRIMERA_CARTA_AZUL=6;
	private Integer COLUMNA_PRIMERA_CARTA_ROJO=8;
	private Integer TAMANYO_MAXIMO=7;
	private Integer LIMITE_HASTA_TAMANYO_MAXIMO=6;

	@Autowired
	private PosicionRepository posicionRepository;

	@Autowired
	private CartaService cartaService;

	@Autowired
	public EfectoService efectoService;

	@Autowired
	private MazoService mazoService;

	@Transactional
	public Optional<List<Posicion>> getByIdPartida(Partida partida) {
		return posicionRepository.findPosicionesByIdPartida(partida);
	}// Todas las posiciones en una determinada partida

	@Transactional
	public Optional<List<Posicion>> getByIdPartidaUsuario(Partida p, Usuario j) {
		return posicionRepository.findPosicionByPartidaUsuario(p, j);
	}// Devuelve las posiciones de las cartas de un determinado jugador en una
		// determinada partida

	@Transactional
	public void setRotacionByIdCarta(Rotacion gradoRotado, Integer idCarta, String idPartida) {
		posicionRepository.setRotacionByIdCartaPartida(gradoRotado, idCarta, idPartida);
	} // Establece el grado de rotacion de una carta en
		// una determinada posicion dentro de cierta partida

	@Transactional
	public void delete(Posicion p) {
		posicionRepository.delete(p);
	}

	// Inserta una posicion, comprobando que hay hueco y que se continua el flujo
	@Transactional(rollbackFor = { PosicionYaUsada.class, PosicionNoContinuaFlujo.class,
			PosicionInvalidaLimiteDeTablero.class })
	public Posicion insert(Posicion posicion)
			throws PosicionYaUsada, PosicionNoContinuaFlujo, PosicionInvalidaLimiteDeTablero {
		Optional<Posicion> posicionAntigua = getPosicionesPorPartidaFilaColumnaYJugador(posicion.getP(),
				posicion.getFila(), posicion.getColumna(), posicion.getP().getJ1());
		Optional<Posicion> posicionAntigua2 = getPosicionesPorPartidaFilaColumnaYJugador(posicion.getP(),
				posicion.getFila(), posicion.getColumna(), posicion.getP().getJ2());
		if (posicionAntigua.isPresent() || posicionAntigua2.isPresent()
				|| (posicion.getFila() == FILA_CARTA_INICIO && posicion.getColumna() == COLUMNA_CARTA_INICIO_AZUL)
				|| (posicion.getFila() == FILA_CARTA_INICIO && posicion.getColumna() == COLUMNA_CARTA_INICIO_ROJO)) {
			throw new PosicionYaUsada();
		}

		if (!continuaFlujo(posicion)) {
			throw new PosicionNoContinuaFlujo();

		} else if (!continuaFlujo(posicion)) {
			throw new PosicionNoContinuaFlujo();
		}
		if (!dentroDeTablero(posicion)) {
			throw new PosicionInvalidaLimiteDeTablero();
		}
		
		Mazo mazo = mazoService.findByPartidaAndUsuario(posicion.getP(), posicion.getJ()).get();
		if (mazo.getUsaEfecto() && efectoService.getLastEfectoByPartida(mazo.getPartida()).getNombre() == TipoEfecto.MARCHA_ATRAS) mazo.setUsaEfecto(false);
		
		return posicionRepository.save(posicion);
	}

	// Devuelve la posicion de una partida, una fila y columna
	@Transactional
	public Optional<Posicion> getPosicionesPorPartidaFilaColumnaYJugador(Partida p, Integer fila, Integer columna,
			Usuario j) {
		return posicionRepository.getPosicionesPorPartidaFilaColumnaYJugador(p, fila, columna, j);
	}

	// Devuelve una lista de posiciones preparadas para la vista
	public List<Posicion> getPosicionAsListToTablero(Partida partida) {
		List<Posicion> posiciones = new ArrayList<>();
		for (int r = 1; r <= NUMERO_FILAS_COLUMNAS_TABLERO; r++) {
			for (int c = 1; c <= NUMERO_FILAS_COLUMNAS_TABLERO; c++) {
				Posicion posicionNoReal = new Posicion();
				posicionNoReal.setCarta(null);
				posicionNoReal.setFila(r);
				posicionNoReal.setColumna(c);
				posiciones.add(posicionNoReal);
			}
		}

		Optional<List<Posicion>> posicionesReales = getByIdPartida(partida);
		if (posicionesReales.isPresent()) {
			for (Posicion p : posicionesReales.get()) {
				Integer fila = p.getFila() - 1;
				Integer columna = p.getColumna() - 1;
				Integer indice = fila * NUMERO_FILAS_COLUMNAS_TABLERO + columna;
				posiciones.remove(indice);
				posiciones.set(indice, p);
			}
		}
		return posiciones;
	}

	// Pasa de un integer a una rotacion
	@Transactional
	public Rotacion rotacionByInteger(Integer rotacion) {
		Rotacion rotacionTipo = null;
		switch (rotacion) {
		case 0:
			rotacionTipo = Rotacion.GRADO_0;
			break;
		case 90:
			rotacionTipo = Rotacion.GRADO_90;
			break;
		case 180:
			rotacionTipo = Rotacion.GRADO_180;
			break;
		case 270:
			rotacionTipo = Rotacion.GRADO_270;
			break;
		}
		return rotacionTipo;
	}

	// Comprueba dada una posicion, que puede continuar el flujo de sus cartas
	@Transactional
	public Boolean continuaFlujo(Posicion p) {
		Carta cartaRotada = cartaService.rotarCarta(p.getCarta(), p.getGradoRotado());
		Optional<Posicion> arriba = getPosicionesPorPartidaFilaColumnaYJugador(p.getP(), p.getFila() - 1,
				p.getColumna(), p.getJ());
		Optional<Posicion> abajo = getPosicionesPorPartidaFilaColumnaYJugador(p.getP(), p.getFila() + 1, p.getColumna(),
				p.getJ());
		Optional<Posicion> izquierda = getPosicionesPorPartidaFilaColumnaYJugador(p.getP(), p.getFila(),
				p.getColumna() - 1, p.getJ());
		Optional<Posicion> derecha = getPosicionesPorPartidaFilaColumnaYJugador(p.getP(), p.getFila(),
				p.getColumna() + 1, p.getJ());

		Boolean limiteH = enElLimiteH(p);
		Boolean limiteV = enElLimiteV(p);

		if (limiteV != null && limiteV == true) {
			abajo = getPosicionesPorPartidaFilaColumnaYJugador(p.getP(), p.getFila() - NUMERO_POSICIONES_BORDE_CONTRARIO, p.getColumna(), p.getJ());
		} else if (limiteV != null && limiteV == false) {
			arriba = getPosicionesPorPartidaFilaColumnaYJugador(p.getP(), p.getFila() + NUMERO_POSICIONES_BORDE_CONTRARIO, p.getColumna(), p.getJ());
		}
		if (limiteH != null && limiteH == false) {
			izquierda = getPosicionesPorPartidaFilaColumnaYJugador(p.getP(), p.getFila(), p.getColumna() + NUMERO_POSICIONES_BORDE_CONTRARIO, p.getJ());
		} else if (limiteH != null && limiteH == true && limiteH.equals(true)) {
			derecha = getPosicionesPorPartidaFilaColumnaYJugador(p.getP(), p.getFila(), p.getColumna() - NUMERO_POSICIONES_BORDE_CONTRARIO, p.getJ());
		}

		Boolean valida = false;
		Partida partida = p.getP();
		Usuario j = p.getJ();
		Optional<List<Posicion>> cartasJugador = getByIdPartidaUsuario(partida, j);
		Posicion ultimaCarta = null;
		if (cartasJugador.isPresent()) {
			Collections.reverse(cartasJugador.get());
			ultimaCarta = cartasJugador.get().get(0);
		}
		
		Mazo mazo = mazoService.findByPartidaAndUsuario(p.getP(), p.getJ()).get();
		
		if (mazo.getUsaEfecto() && efectoService.getLastEfectoByPartida(mazo.getPartida()).getNombre() == TipoEfecto.MARCHA_ATRAS) {
				ultimaCarta = cartasJugador.get().get(1);
		}
		
		if (valida == false && p.getJ().equals(p.getP().getJ1()) && p.getFila() == FILA_PRIMERA_CARTA && p.getColumna() == COLUMNA_PRIMERA_CARTA_AZUL
				&& cartaRotada.getSalida_abajo() == false) {
			valida = true;
		} else if (valida == false && p.getJ().equals(p.getP().getJ2()) && p.getFila() == FILA_PRIMERA_CARTA && p.getColumna() == COLUMNA_PRIMERA_CARTA_ROJO
				&& cartaRotada.getSalida_abajo() == false) {
			valida = true;
		}
		if (valida == false && arriba.isPresent()
				&& (cartaRotada.getSalida_arriba() != null && cartaRotada.getSalida_arriba() == false)) {
			Carta arribaCarta = cartaService.rotarCarta(arriba.get().getCarta(), arriba.get().getGradoRotado());
			if ((arribaCarta.getSalida_abajo() != null && arribaCarta.getSalida_abajo() == true)
					&& (ultimaCarta == null || (arriba.get().getFila().equals(ultimaCarta.getFila())
							&& arriba.get().getColumna().equals(ultimaCarta.getColumna())))) {
				valida = true;
			}
		}
		if (valida == false && abajo.isPresent()
				&& (cartaRotada.getSalida_abajo() != null && cartaRotada.getSalida_abajo() == false)) {
			Carta abajoCarta = cartaService.rotarCarta(abajo.get().getCarta(), abajo.get().getGradoRotado());
			if ((abajoCarta.getSalida_arriba() != null && abajoCarta.getSalida_arriba().equals(true))
					&& (ultimaCarta == null || (abajo.get().getFila().equals(ultimaCarta.getFila())
							&& abajo.get().getColumna().equals(ultimaCarta.getColumna())))) {
				valida = true;
			}
		}
		if (valida == false && izquierda.isPresent()
				&& (cartaRotada.getSalida_izquierda() != null && cartaRotada.getSalida_izquierda() == false)) {
			Carta izquierdaCarta = cartaService.rotarCarta(izquierda.get().getCarta(),
					izquierda.get().getGradoRotado());
			if ((izquierdaCarta.getSalida_derecha() != null && izquierdaCarta.getSalida_derecha().equals(true))
					&& (ultimaCarta == null || (izquierda.get().getFila().equals(ultimaCarta.getFila())
							&& izquierda.get().getColumna().equals(ultimaCarta.getColumna())))) {
				valida = true;
			}
		}
		if (valida == false && derecha.isPresent()
				&& (cartaRotada.getSalida_derecha() != null && cartaRotada.getSalida_derecha() == false)) {
			Carta derechaCarta = cartaService.rotarCarta(derecha.get().getCarta(), derecha.get().getGradoRotado());
			if ((derechaCarta.getSalida_izquierda() != null && derechaCarta.getSalida_izquierda().equals(true))
					&& (ultimaCarta == null || (derecha.get().getFila().equals(ultimaCarta.getFila())
							&& derecha.get().getColumna().equals(ultimaCarta.getColumna())))) {
				valida = true;
			}
		}
		return valida;

	}

	@Transactional
	public Boolean dentroDeTablero(Posicion posicion) {
		Partida partida = posicion.getP();
		Integer fila = posicion.getFila();
		Integer columna = posicion.getColumna();
		Optional<List<Posicion>> porFilas = getPosicionesPorPartidaOrdenadasPorFila(partida);
		Optional<List<Posicion>> porColumnas = getPosicionesPorPartidaOrdenadasPorColumna(partida);
		Boolean valida = true;
		if (porFilas.isPresent() && porColumnas.isPresent()) {
			Integer minFilas = porFilas.get().get(0).getFila();
			Integer minColumnas = porColumnas.get().get(0).getColumna();
			Integer maxFilas = porFilas.get().get(porFilas.get().size() - 1).getFila();
			Integer maxColumnas = porColumnas.get().get(porFilas.get().size() - 1).getColumna();
			Integer dimAltura = maxFilas - minFilas + 1;
			Integer dimAnchura = maxColumnas - minColumnas + 1;

			if (dimAltura < TAMANYO_MAXIMO) {
				valida = true;
			} else if (dimAltura >= TAMANYO_MAXIMO) {
				valida = fila <= maxFilas && fila >= minFilas;
			}
			if (dimAnchura < TAMANYO_MAXIMO) {
				valida = valida && true;
			} else if (dimAnchura >= TAMANYO_MAXIMO) {
				valida = valida && columna <= maxColumnas && columna >= minColumnas;
			}
		}
		return valida;
	}

	// Metodo para saber si una posicion dada se encuentra junto a algun limite
	// vertical del tablero
	@Transactional
	public Boolean enElLimiteV(Posicion posicion) {
		Partida partida = posicion.getP();
		Integer fila = posicion.getFila();
		Optional<List<Posicion>> porFilas = getPosicionesPorPartidaOrdenadasPorFila(partida);
		Boolean limiteV = null;
		if (porFilas.isPresent()) {
			Integer minFilas = porFilas.get().get(0).getFila();
			Integer maxFilas = porFilas.get().get(porFilas.get().size() - 1).getFila();
			Integer dimAltura = maxFilas - minFilas + 1;
			if (maxFilas < TAMANYO_MAXIMO) {
				dimAltura++;
				maxFilas++;
			}
			if (dimAltura == LIMITE_HASTA_TAMANYO_MAXIMO && fila == maxFilas + 1) {
				limiteV = true;
			} else if (dimAltura == LIMITE_HASTA_TAMANYO_MAXIMO && fila == minFilas - 1) {
				limiteV = false;
			} else if (dimAltura == TAMANYO_MAXIMO && fila == minFilas) {
				limiteV = false;
			} else if (dimAltura == TAMANYO_MAXIMO && fila == maxFilas) {
				limiteV = true;
			}
		}
		return limiteV;
	}

	// Metodo para saber si una posicion dada se encuentra junto a algun limite
	// horizontal del tablero
	@Transactional
	public Boolean enElLimiteH(Posicion posicion) {
		Partida partida = posicion.getP();
		Integer columna = posicion.getColumna();
		Optional<List<Posicion>> porColumnas = getPosicionesPorPartidaOrdenadasPorColumna(partida);
		Boolean limiteH = null;
		List<Boolean> listaLimites = Arrays.asList(null, null);
		if (porColumnas.isPresent()) {
			Integer minColumnas = porColumnas.get().get(0).getColumna();
			Integer maxColumnas = porColumnas.get().get(porColumnas.get().size() - 1).getColumna();
			Integer dimAnchura = maxColumnas - minColumnas + 1;

			if (dimAnchura == LIMITE_HASTA_TAMANYO_MAXIMO && columna == maxColumnas + 1) {
				limiteH = true;
			} else if (dimAnchura == LIMITE_HASTA_TAMANYO_MAXIMO && columna == minColumnas - 1) {
				limiteH = false;
			} else if (dimAnchura == TAMANYO_MAXIMO && columna == minColumnas) {
				limiteH = false;
			} else if (dimAnchura == TAMANYO_MAXIMO && columna == maxColumnas) {
				limiteH = true;
			}
		}
		return limiteH;
	}

	@Transactional
	public Optional<List<Posicion>> getPosicionesPorPartidaYCarta(Partida p, Carta c) {
		return posicionRepository.getPosicionesPorPartidaYCarta(p, c);
	}

	@Transactional
	public Optional<List<Posicion>> getPosicionesPorPartidaOrdenadasPorFila(Partida p) {
		return posicionRepository.getPosicionesPorPartidaOrdenadasPorFila(p);
	}

	@Transactional
	public Optional<List<Posicion>> getPosicionesPorPartidaOrdenadasPorColumna(Partida p) {
		return posicionRepository.getPosicionesPorPartidaOrdenadasPorColumna(p);
	}

	@Transactional
	public Optional<Posicion> getPosicionPorPartidaFilaColumna(Partida p, Integer fila, Integer columna) {
		return posicionRepository.findPosicionPorPartidaFilaColumna(p, fila, columna);
	}
}
