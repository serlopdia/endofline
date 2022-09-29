package org.springframework.samples.endofline.web;

import java.util.ArrayList;
import java.util.List;

import org.springframework.samples.endofline.model.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WelcomeController {
	
	
	  @GetMapping({"/","/welcome"})
	  public String welcome(ModelMap model) {

		  List<Person> persons=new ArrayList<Person>();
		  Person p1=new Person();
		  p1.setFirstName("Raúl ");
		  p1.setLastName("Gallardo Roco");
		  Person p2=new Person();
		  p2.setFirstName("Francisco ");
		  p2.setLastName("García De La Vega García");
		  Person p3=new Person();
		  p3.setFirstName("Sergio ");
		  p3.setLastName("López Díaz");
		  Person p4=new Person();
		  p4.setFirstName("Borja ");
		  p4.setLastName("Piñero Calera");
		  Person p5=new Person();
		  p5.setFirstName("Alejandro ");
		  p5.setLastName("Sánchez Mayorga");
		  persons.add(p1);
		  persons.add(p2);
		  persons.add(p3);
		  persons.add(p4);
		  persons.add(p5);
		  model.put("persons", persons);
		  model.put("title", "dp1-2021-2022");
		  model.put("group", "G-04");
	    return "welcome";
	  }
}