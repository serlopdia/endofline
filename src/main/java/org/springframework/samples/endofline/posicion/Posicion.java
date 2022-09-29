package org.springframework.samples.endofline.posicion;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
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
@Table(name = "posicion")
public class Posicion extends BaseEntity {
	
	@NotNull(message = "Please enter fila")
	@Column(name = "fila")
	Integer fila;
	
	@NotNull(message = "Please enter columna")
	@Column(name = "columna")
	Integer columna;
	
	@NotNull(message = "Please enter fecha")
	LocalDateTime fecha;
	
	@ManyToOne(optional = false)
	private Usuario j;
	
	@ManyToOne(optional = false)
	private Partida p;
	
	@ManyToOne(optional = false)
	private Carta carta;
	
	@NotNull(message = "Please enter grado")
	@Enumerated(EnumType.STRING)
	private Rotacion gradoRotado;

	@Override
	public String toString() {
		return "Posicion [fila=" + fila + ", columna=" + columna + ", fecha=" + fecha + ", j=" + j + ", p=" + p
				+ ", carta=" + carta + ", gradoRotado=" + gradoRotado + "]";
	}

}
