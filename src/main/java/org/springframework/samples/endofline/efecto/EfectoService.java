package org.springframework.samples.endofline.efecto;

import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.samples.endofline.mazo.Mazo;
import org.springframework.samples.endofline.mazo.MazoService;
import org.springframework.samples.endofline.partida.Partida;
import org.springframework.samples.endofline.posicion.Posicion;
import org.springframework.samples.endofline.posicion.PosicionService;
import org.springframework.samples.endofline.carta.Carta;
import org.springframework.samples.endofline.carta.CartaService;
import org.springframework.samples.endofline.posicion.Rotacion;
import org.springframework.samples.endofline.usuario.Usuario;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EfectoService {

	Integer NUMERO_CARTAS_RESTANTES_ACELERON = 2;
	Integer NUMERO_CARTAS_RESTANTES_FRENAZO = 4;
	Integer NUMERO_CARTAS_RESTANTES_MARCHA_ATRAS = 3;
	Integer NUMERO_CARTAS_RESTANTES_GAS_EXTRA = 4;
	Integer ENERGIA_INICIAL = 3;
	Integer ULTIMA_RONDA_EFECTO_NO_DISPONIBLE = 2;

	@Autowired
	public EfectoRepository efectoRepository;

	@Autowired
	public PosicionService posicionService;

	@Autowired
	public MazoService mazoService;

	@Autowired
	public CartaService cartaService;

	@Transactional
	public List<Efecto> getAllEfectosByPartidaUsuario(Partida p, Usuario j) {
		return efectoRepository.findAllEfectosByPartidaUsuario(p, j);
	}

	public Efecto getLastEfectoByPartida(Partida p) {
		List<Efecto> listEfectos = efectoRepository.findAllEfectosByPartida(p);
		return listEfectos.get(listEfectos.size() - 1);
	}

	public void checkCartaHabil(Mazo mazo) throws EfectoImposibleException {

		List<Posicion> posList = posicionService.getByIdPartidaUsuario(mazo.getPartida(), mazo.getJ()).get();
		Posicion p = posList.get(posList.size() - 2);
		Carta cartaRotada = cartaService.rotarCarta(p.getCarta(), p.getGradoRotado());

		Optional<Posicion> arriba = posicionService.getPosicionPorPartidaFilaColumna(p.getP(), p.getFila() - 1,
				p.getColumna());
		Optional<Posicion> abajo = posicionService.getPosicionPorPartidaFilaColumna(p.getP(), p.getFila() + 1,
				p.getColumna());
		Optional<Posicion> izquierda = posicionService.getPosicionPorPartidaFilaColumna(p.getP(), p.getFila(),
				p.getColumna() - 1);
		Optional<Posicion> derecha = posicionService.getPosicionPorPartidaFilaColumna(p.getP(), p.getFila(),
				p.getColumna() + 1);

		boolean vacio = false;

		if (cartaRotada.getSalida_arriba() != null) {
			if (vacio == false && (cartaRotada.getSalida_arriba() == true && !arriba.isPresent()))
				vacio = true;
		}

		if (cartaRotada.getSalida_abajo() != null) {
			if (vacio == false && (cartaRotada.getSalida_abajo() == true && !abajo.isPresent()))
				vacio = true;
		}

		if (cartaRotada.getSalida_izquierda() != null) {
			if (vacio == false && (cartaRotada.getSalida_izquierda() == true && !izquierda.isPresent()))
				vacio = true;
		}

		if (cartaRotada.getSalida_derecha() != null) {
			if (vacio == false && (cartaRotada.getSalida_derecha() == true && !derecha.isPresent()))
				vacio = true;
		}

		if (!vacio) {
			throw new EfectoImposibleException();
		}
	}

	@Transactional
	public Efecto save(Efecto efecto) {
		return efectoRepository.save(efecto);
	}

	public TipoEfecto parseTipoEfecto(String nombreEfecto) {

		TipoEfecto tipoEfecto = null;

		switch (nombreEfecto) {
		case "aceleron":
			tipoEfecto = TipoEfecto.ACELERON;
			break;
		case "frenazo":
			tipoEfecto = TipoEfecto.FRENAZO;
			break;
		case "marchaAtras":
			tipoEfecto = TipoEfecto.MARCHA_ATRAS;
			break;
		case "gasExtra":
			tipoEfecto = TipoEfecto.GAS_EXTRA;
			break;
		}
		return tipoEfecto;
	}

	@Transactional
	public void activarEfecto(TipoEfecto tipoEfecto, Mazo mazo) {

		mazo.setUsaEfecto(true);

		switch (tipoEfecto) {
		case ACELERON:
			aceleron(mazo);
			break;
		case FRENAZO:
			frenazo(mazo);
			break;
		case MARCHA_ATRAS:
			break;
		case GAS_EXTRA:
			gasExtra(mazo);
			break;
		}
	}

	@Transactional
	public void gasExtra(Mazo mazo) {
		List<Carta> cartasMazo = mazo.getCartas();
		mazo.setCartas(cartaService.generaListaCartas(mazo.getPartida(), mazo.getJ(), 1));
	}

	@Transactional
	public void aceleron(Mazo mazo) {
		if (mazo.getCartas().size() == NUMERO_CARTAS_RESTANTES_ACELERON) {
			mazoService.cambiarTurno(mazo);
			mazoService.repartirCartas(mazo);
		}
	}

	@Transactional
	public void frenazo(Mazo mazo) {
		if (mazo.getCartas().size() == NUMERO_CARTAS_RESTANTES_FRENAZO) {
			mazoService.cambiarTurno(mazo);
			mazoService.repartirCartas(mazo);
		}

	}

	@Transactional
	public void cambiarTurnoAfterEfecto(Mazo mazo) {
		TipoEfecto tipoEfecto = getLastEfectoByPartida(mazo.getPartida()).getNombre();

		switch (tipoEfecto) {
		case ACELERON:
			if (mazo.getCartas().size() == NUMERO_CARTAS_RESTANTES_ACELERON)
				mazoService.cambiarTurno(mazo);
			break; // 2 es el numero de cartas que deben quedar despues del aceleron
		case FRENAZO:
			if (mazo.getCartas().size() == NUMERO_CARTAS_RESTANTES_FRENAZO)
				mazoService.cambiarTurno(mazo);
			break; // 4 es el numero de cartas que deben quedar despues del frenazo
		case MARCHA_ATRAS:
			if (mazo.getCartas().size() == NUMERO_CARTAS_RESTANTES_MARCHA_ATRAS)
				mazoService.cambiarTurno(mazo);
			break; // 3 es el numero de cartas que deben quedar despues de la marcha atras
		case GAS_EXTRA:
			if (mazo.getCartas().size() == NUMERO_CARTAS_RESTANTES_GAS_EXTRA)
				mazoService.cambiarTurno(mazo);
			break; // 4 es el numero de cartas que deben quedar despues del gas extra
		}

	}

	@Transactional(rollbackFor = { RondaParaEfectoException.class, SinEnergiaException.class, EfectoImposibleException.class })
	public void newEfecto(String nombreEfecto, Usuario u, Partida p)
			throws SinEnergiaException, RondaParaEfectoException, EfectoImposibleException {
		Mazo mazo = mazoService.findByPartidaAndUsuario(p, u).get();
		TipoEfecto tipoEfecto = parseTipoEfecto(nombreEfecto);
		Efecto nuevoEfecto = new Efecto();
		nuevoEfecto.setNombre(tipoEfecto);
		nuevoEfecto.setJ(u);
		nuevoEfecto.setP(p);
		nuevoEfecto.setRonda(mazo.getRonda());

		Integer energiaUsada = getAllEfectosByPartidaUsuario(p, u).size();
		if (mazo.getRonda() <= ULTIMA_RONDA_EFECTO_NO_DISPONIBLE) {
			throw new RondaParaEfectoException();
		}
		if (energiaUsada < ENERGIA_INICIAL) {
			if (tipoEfecto == TipoEfecto.MARCHA_ATRAS) {
				checkCartaHabil(mazo);
				save(nuevoEfecto);
				activarEfecto(tipoEfecto, mazo);
			} else {
				save(nuevoEfecto);
				activarEfecto(tipoEfecto, mazo);
			}

		} else {
			throw new SinEnergiaException();
		}
	}

}