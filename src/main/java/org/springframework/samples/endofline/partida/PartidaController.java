package org.springframework.samples.endofline.partida;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.samples.endofline.carta.CambioDeTurnoNoDisponible;
import org.springframework.samples.endofline.carta.CartaService;
import org.springframework.samples.endofline.efecto.Efecto;
import org.springframework.samples.endofline.mazo.Mazo;
import org.springframework.samples.endofline.mazo.MazoService;
import org.springframework.samples.endofline.efecto.EfectoService;
import org.springframework.samples.endofline.posicion.Posicion;
import org.springframework.samples.endofline.posicion.PosicionService;
import org.springframework.samples.endofline.usuario.*;
import org.springframework.samples.endofline.util.GetionPermisos;

@Controller
public class PartidaController {

	@Autowired
	public PartidaService partidaService;

	@Autowired(required = true)
	public GetionPermisos permisos;

	@Autowired
	public UsuarioService usuarioService;

	@Autowired
	public PosicionService posicionService;

	@Autowired
	public CartaService cartaService;

	@Autowired
	public MazoService mazoService;

	@Autowired
	public EfectoService efectoService;

	@GetMapping("/partidas/encurso")
	public String showAllEnCurso(ModelMap mMap) {
		String vista = "partidas/partidasListas";
		List<Partida> partidas = partidaService.getAllEnCurso();
		mMap.addAttribute("partidas", partidas);
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String userLogged = auth.getName();
		mMap.addAttribute("usuarioLoggued", userLogged);
		return vista;
	}

	@GetMapping("/partidas/jugadas")
	public String showAllJugadas(ModelMap mMap) {
		String vista = "partidas/partidasListas";
		Iterable<Partida> partidas = partidaService.getAllJugadas();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String userLogged = auth.getName();
		mMap.addAttribute("usuarioLoggued", userLogged);
		mMap.addAttribute("partidas", partidas);
		return vista;
	}

	@GetMapping("/partidas/new")
	public String crearPartida() {
		return "partidas/crearPartida";
	}

	@PostMapping(value = "/partidas/new", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE, produces = {
			MediaType.APPLICATION_ATOM_XML_VALUE, MediaType.APPLICATION_JSON_VALUE })
	public String save(@RequestBody MultiValueMap<String, String> j2, ModelMap modelMap)
			throws PartidaEnCursoConGanadorException, PartidaAdminException, PartidaConUnMismoException {
		Partida p = new Partida();
		Optional<Usuario> u2 = usuarioService.getByUsuario(j2.getFirst("usuario"));
		if (u2 == null || u2.isEmpty()) {
			modelMap.addAttribute("mensaje", "Jugador no encontrado. Intentelo de nuevo");
			return "partidas/crearPartida";
		} else {
			p.setJ2(u2.get());
			p.setFecha(LocalDateTime.now());
			p.setGanador(null);
			p.setEstado(EstadoPartida.PENDIENTE);
			Collection<Efecto> listaEfectos = new ArrayList<Efecto>();
			p.setEfectos(listaEfectos);
			Usuario j1 = usuarioService.getByUsuario(permisos.getNombreUsuarioActual()).get();
			p.setJ1(j1);
			Partida pInsertado = partidaService.insert(p);
			Integer id = pInsertado.getId();
			modelMap.addAttribute("id", id);
			return "partidas/cargando";
		}
	}

	@GetMapping("/partidas/pendientes/{usuarioId}")
	public String partidasPendientes(@PathVariable("usuarioId") String usuarioId, ModelMap modelMap,
			HttpServletResponse response) {
		response.addHeader("Refresh", "2");
		Usuario j2 = usuarioService.getByUsuario(usuarioId).get();
		Iterable<Partida> partidas = partidaService.getPendientes(j2);
		modelMap.addAttribute("partidas", partidas);
		return "partidas/verPartidasPendientes";
	}

	@GetMapping(value = "/partidas/aceptar/{id}")
	public String aceptarPartida(@PathVariable("id") Integer id, ModelMap modelMap) {
		Optional<Partida> partidaEntity = partidaService.getById(id);
		Usuario j1 = partidaEntity.get().getJ1();
		Usuario j2 = usuarioService.getByUsuario(permisos.getNombreUsuarioActual()).get();
		if (partidaEntity.isPresent()) {
			partidaService.aceptar(id);
			mazoService.crearMazoInicial(j1, partidaEntity.get(), false);
			mazoService.crearMazoInicial(j2, partidaEntity.get(), false);

			Mazo m1 = mazoService.findByPartidaAndUsuario(partidaEntity.get(), j1).get();
			Mazo m2 = mazoService.findByPartidaAndUsuario(partidaEntity.get(), j2).get();

			mazoService.turnoInicio(m1, m2);

		}
		return "redirect:/partidas/{id}";
	}

	@GetMapping(value = "/partidas/rechazar/{id}")
	public String rechazarPartida(@PathVariable("id") Integer id, ModelMap modelMap) {
		Optional<Partida> partidaEntity = partidaService.getById(id);
		if (partidaEntity.isPresent()) {
			partidaService.rechazar(id);
		}
		return "redirect:/welcomeUsuario";
	}

	@GetMapping(value = "/partidas/comprobar/{id}")
	public String redirigirSegunEstado(@PathVariable("id") Integer id, ModelMap modelMap) {

		Partida partidaEntity = partidaService.getById(id).get();
		if (partidaEntity.getEstado() == EstadoPartida.EN_CURSO) {
			return "redirect:/partidas/{id}";
		} else {
			modelMap.addAttribute("id", id);
			return "partidas/cargando";
		}
	}

	// Cuando se llama a la partida, aparte de devolverse la vista del tablero, se
	// devuelve la lista de las posiciones, dada la partida.
	@GetMapping("/partidas/{id}")
	public String welcome(@PathVariable("id") Integer id, ModelMap model, HttpServletResponse response) {
		String userLogged = permisos.getNombreUsuarioActual();
		Usuario usuarioActual = usuarioService.getByUsuario(userLogged).get();
		Partida p = partidaService.getById(id).get();
		Mazo mazo = mazoService.findByPartidaAndUsuario(p, usuarioActual).get();

		if (!mazo.getTurno())
			response.addHeader("Refresh", "5");
		model.addAttribute("now", new Date());

		// Coger al usuario logueado
		model.addAttribute("usuarioLoggued", userLogged);

		// Cargar la lista de posiciones
		List<Posicion> posiciones = posicionService.getPosicionAsListToTablero(p);
		model.addAttribute("posiciones", posiciones);

		// Cargar energia restante
		List<Efecto> e1 = efectoService.getAllEfectosByPartidaUsuario(p, p.getJ1());
		List<Efecto> e2 = efectoService.getAllEfectosByPartidaUsuario(p, p.getJ2());
		Integer energiaRestante1 = e1 != null ? 3 - e1.size() : 3;
		Integer energiaRestante2 = e2 != null ? 3 - e2.size() : 3;
		model.addAttribute("e1", energiaRestante1);
		model.addAttribute("e2", energiaRestante2);

		// Cargar ronda de ultimo efecto usado
		List<Efecto> listEfectosJugador = usuarioActual.equals(p.getJ1()) ? e1 : e2;
		Integer ronda = listEfectosJugador.size() > 0 ? listEfectosJugador.get(listEfectosJugador.size() - 1).getRonda()
				: 0;
		model.addAttribute("ultimoEfectoRonda", ronda);

		// Coger el mazo actual
		model.addAttribute("mazo", mazo);

		// Pone a los dos jugadores
		model.addAttribute("j1", p.getJ1().getUsuario());
		model.addAttribute("j2", p.getJ2().getUsuario());

		Integer numCartas = mazo.getCartas().size();
		model.addAttribute("numCartas", numCartas);

		return "tablero";
	}

	@PostMapping(value = "/partidas/fin", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	public String finalizarPartida(@RequestBody MultiValueMap<String, String> finalizar, ModelMap modelMap) {
		Integer partidaId = Integer.parseInt(finalizar.getFirst("partida").toString());
		Partida p = partidaService.getById(partidaId).get();
		Usuario perdedor = usuarioService.getByUsuario(permisos.getNombreUsuarioActual()).get();
		partidaService.finalizarPartida(p, perdedor);
		mazoService.finalizarPartida(p);
		return "redirect:/welcomeUsuario";
	}

	@GetMapping("/partidas/propias/{usuario}")
	public String partidasJugadasJugador(@PathVariable("usuario") String usuario, ModelMap model) {
		Usuario u = usuarioService.getByUsuario(permisos.getNombreUsuarioActual()).get();
		List<Partida> partidas = partidaService.findByPartidaByUsuario(u);
		model.addAttribute("partidas", partidas);
		return "partidas/partidasJugador";
	}

	@PostMapping(value = "/partidas/nuevamano", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	public String nuevaMano(@RequestBody MultiValueMap<String, String> request, ModelMap model) {
		Partida p = partidaService.getById(Integer.parseInt(request.getFirst("partida").toString())).get();
		Usuario j = usuarioService.getByUsuario(permisos.getNombreUsuarioActual()).get();
		Mazo m = mazoService.findByPartidaAndUsuario(p, j).get();
		try {
			cartaService.cambioManoInicial(m);
		} catch (CambioDeTurnoNoDisponible e) {
			m.setMensaje(
					"ERROR: El cambio de mano inicial no esta disponible. Puede que no sea tu turno o no estes en la ronda 1");
		}
		return "redirect:/partidas/" + m.getPartida().getId().toString();

	}

}