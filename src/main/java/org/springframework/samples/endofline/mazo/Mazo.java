package org.springframework.samples.endofline.mazo;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import org.springframework.samples.endofline.carta.Carta;
import org.springframework.samples.endofline.model.BaseEntity;
import org.springframework.samples.endofline.partida.Partida;
import org.springframework.samples.endofline.usuario.Usuario;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Mazo extends BaseEntity{
	
	@ManyToOne(optional = false)
	private Usuario j;
	
	@ManyToOne(optional = false)
	private Partida partida;

	//true si es mi turno, false si no lo es
	@NotNull
	private Boolean turno;
	
	//true si se usa algun efecto al final de ronda se cambiara a false de nuevo
	@NotNull
	private Boolean usaEfecto;
	
	@NotNull
	private Integer ronda;
	
	String mensaje;
	
	Boolean usadoCambioMano;
	
	@ManyToMany
	private List<Carta> cartas;	
	
	
	@Override
	public String toString() {
		return "Mazo [j=" + j + ", partida=" + partida + ", turno=" + turno + ", cartas=" + cartas + "]";
	}


 
}