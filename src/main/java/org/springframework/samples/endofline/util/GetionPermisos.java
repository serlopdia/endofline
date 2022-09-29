package org.springframework.samples.endofline.util;

import java.util.Collection;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GetionPermisos {

	@Transactional
	public String permisosUsuarioActual() {
		Authentication auth=SecurityContextHolder.getContext().getAuthentication();
		Collection<? extends GrantedAuthority> userLogged=auth.getAuthorities();
		Object[] permiso = userLogged.toArray();
		if(permiso[0].toString().equals("admin")) {
			return "admin";
		}else if(permiso[0].toString().equals("player")){
			return "player";
		}else {
			return "error";
		}
	}
	
	@Transactional
	public String getNombreUsuarioActual() {
		Authentication auth=SecurityContextHolder.getContext().getAuthentication();
		String userLogged=auth.getName();
		return userLogged;
	}
}
