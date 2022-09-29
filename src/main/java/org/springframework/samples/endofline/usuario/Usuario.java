package org.springframework.samples.endofline.usuario;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

import org.springframework.core.style.ToStringCreator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "usuario")
public class Usuario{
	
		@LastModifiedDate private LocalDateTime lastModifiedDate;
		@CreatedDate private LocalDateTime createdDate;
	
		@NotEmpty
		@Id
		String usuario;
		

		@NotEmpty
		String nombre;
		
		@NotEmpty
		String apellidos;
		
		@NotEmpty
		String contrasenya;
		
		@Email
		@NotEmpty
		String email;
		
		@NotEmpty
		String permiso_admin;
		
		public boolean isNew() {
			return this.getUsuario() == null;
		}
		
		@Override
		public String toString() {
			return new ToStringCreator(this)

					.append("usuario", this.getUsuario())
					.append("nombre", this.getNombre())
					.append("apellidos", this.getApellidos())
					.append("contrasenya", this.getContrasenya())
					.append("email", this.getEmail())
					.append("permiso_admin", this.getPermiso_admin()).toString();
		}
		
	
}
