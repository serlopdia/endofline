package org.springframework.samples.endofline.carta;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CartaController {

	public static final String CARTAS_LISTING="cartas/CartasListing";
	
	@Autowired
	CartaService cartasService;
	

	@GetMapping("/cartas")
	public String listCartas(ModelMap model)
	{
		model.addAttribute("cartas",cartasService.findAll());
		return CARTAS_LISTING;
	}
	
}