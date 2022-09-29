package org.springframework.samples.endofline.carta;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.springframework.samples.endofline.model.BaseEntity;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "cartas")
public class Carta extends BaseEntity {
	@Override
	public String toString() {
		return "Carta [iniciativa=" + iniciativa + ", salida_arriba=" + salida_arriba + ", salida_izquierda="
				+ salida_izquierda + ", salida_derecha=" + salida_derecha + ", salida_abajo=" + salida_abajo + "]";
	}

	@NotNull
	@Column(name = "iniciativa", columnDefinition = "INT")
	private Integer iniciativa;
	@Column(name = "color", columnDefinition = "CHAR")
	private String color;
	
	//Lados de las cartas. Indican True=Salida, False=Entrada, Null=No entrada/salida.
	@Column(name = "salida_arriba", columnDefinition = "BOOLEAN")
	private Boolean salida_arriba;
	@Column(name = "salida_izquierda", columnDefinition = "BOOLEAN")
	private Boolean salida_izquierda;
	@Column(name = "salida_derecha", columnDefinition = "BOOLEAN")
	private Boolean salida_derecha;
	@Column(name = "salida_abajo", columnDefinition = "BOOLEAN")
	private Boolean salida_abajo;
	
	//Imágenes
	@Column(name = "imagen_azul", columnDefinition = "VARCHAR")
	private String imagen_azul;
	@Column(name = "imagen_rosa", columnDefinition = "VARCHAR")
	private String imagen_rosa;
	
	//Multiplicidad
	@NotNull
	private Integer multiplicidad;
	
}
