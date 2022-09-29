package org.springframework.samples.endofline.configuration;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.session.ConcurrentSessionControlAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * @author japarejo
 */
@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

	@Autowired
	DataSource dataSource;
	
	@Bean
	public SessionAuthenticationStrategy sessionAuthenticationStrategy() {
		ConcurrentSessionControlAuthenticationStrategy sessionAuthenticationStrategy = new ConcurrentSessionControlAuthenticationStrategy(new SessionRegistryImpl());
		sessionAuthenticationStrategy.setMaximumSessions(3);
		sessionAuthenticationStrategy.setExceptionIfMaximumExceeded(true);
		return sessionAuthenticationStrategy;
	}
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests()
				.antMatchers("/resources/**","/webjars/**","/h2-console/**").permitAll()
				.antMatchers(HttpMethod.GET, "/","/oups").permitAll()
				.antMatchers("/usuarios/register").permitAll()
				.antMatchers("/usuarios").hasAnyAuthority("admin")
				
				.antMatchers("/usuarios/**").authenticated()
				
				.antMatchers("/register").permitAll()
				.antMatchers("/session/reparto").permitAll()
				.antMatchers("/welcomeUsuario").hasAnyAuthority("player")
				.antMatchers("/tablero/**").hasAnyAuthority("player")
				.antMatchers("/partidas/jugadas").hasAnyAuthority("admin")
				.antMatchers("/partidas/encurso").hasAnyAuthority("admin")
				.antMatchers("/partidas/**").authenticated()
				.antMatchers("/posicion/**").hasAnyAuthority("player")
				.antMatchers("/efecto/**").hasAnyAuthority("player")
				.antMatchers("/cartas/**").hasAnyAuthority("admin")
				.antMatchers("/admin/**").hasAnyAuthority("admin")
				.antMatchers("/owners/**").hasAnyAuthority("owner","admin")				
				.antMatchers("/vets/**").authenticated()
				.antMatchers("/loginHandler").authenticated()
				.anyRequest().denyAll()
				.and()
				 	.formLogin()
				 	.defaultSuccessUrl("/loginHandler")
				 	/*.loginPage("/login")*/
				 	.failureUrl("/login-error")
				.and()
					.logout()
						.logoutSuccessUrl("/"); 
                // Configuración para que funcione la consola de administración 
                // de la BD H2 (deshabilitar las cabeceras de protección contra
                // ataques de tipo csrf y habilitar los framesets si su contenido
                // se sirve desde esta misma página.
                http.csrf().ignoringAntMatchers("/h2-console/**");
                http.headers().frameOptions().sameOrigin();
                
                http.sessionManagement()
                .sessionAuthenticationStrategy(sessionAuthenticationStrategy());
	}

	@Override
	public void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.jdbcAuthentication()
	      .dataSource(dataSource)
	      .usersByUsernameQuery(
	       "select usuario, contrasenya, true "
	        + "from usuario "
	        + "where usuario = ?"
	        )
	      .authoritiesByUsernameQuery(
	       "select usuario, permiso_admin "
	        + "from usuario "
	        + "where usuario = ?")	      	      
	      .passwordEncoder(passwordEncoder());
		
	}
	
	@Bean
	public PasswordEncoder passwordEncoder() {	    
		PasswordEncoder encoder =  NoOpPasswordEncoder.getInstance();
	    return encoder;
	}
	
}
