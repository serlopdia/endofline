package org.springframework.samples.endofline.efecto;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.springframework.samples.endofline.model.BaseEntity;
import org.springframework.samples.endofline.partida.Partida;
import org.springframework.samples.endofline.usuario.Usuario;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name="efecto")
public class Efecto extends BaseEntity{
	
	@NotNull
	TipoEfecto nombre;
	
	
	@ManyToOne(optional=false)
	Usuario j;
	
	@ManyToOne(optional = false)
	Partida p;
	
	@NotNull
	private Integer ronda;
	
	@Override
	public String toString() {
		return "Efecto [Nombre=" + nombre + ", Usuario=" + j.getUsuario() + ", Partida=" + p.getId() + "]";
	}
	
}