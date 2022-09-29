package org.springframework.samples.endofline.partida;


import java.time.LocalDateTime;
import java.util.Collection;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.springframework.samples.endofline.usuario.Usuario;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.samples.endofline.efecto.Efecto;
import org.springframework.samples.endofline.model.BaseEntity;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
@Entity
@Table(name="partida")
public class Partida extends BaseEntity{
	
	@ManyToOne(optional=true,fetch = FetchType.LAZY)
	@OnDelete(action = OnDeleteAction.CASCADE)
	@JoinColumn(nullable=true, updatable=true)
	Usuario j1;
	
	@ManyToOne(optional=true, fetch = FetchType.LAZY)
	@OnDelete(action = OnDeleteAction.CASCADE)
	@JoinColumn(nullable=true, updatable=true)
	Usuario j2;
	
	@ManyToOne(optional=true, fetch = FetchType.LAZY)
	@OnDelete(action = OnDeleteAction.CASCADE)
	@JoinColumn(nullable=true, updatable=true)
	Usuario ganador;
	
	@NotNull
	@Enumerated(EnumType.STRING)
	EstadoPartida estado;
	
	@NotNull
	LocalDateTime fecha;
	
	@ManyToMany
	Collection<Efecto> efectos;

	@Override
	public String toString() {
		return "Partida [j1=" + j1 + ", j2=" + j2 + ", ganador=" + ganador +
				", fecha=" + fecha + ", efectos="
				+ efectos + "]";
	}
}
