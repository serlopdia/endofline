-- Probar la entidad Usuario
INSERT INTO usuario(usuario, nombre, apellidos, contrasenya, email, permiso_admin) VALUES ('adminSergio','Sergio','Lopez Diaz','pruebaAdmin','sergio.ld00@gmail.com','admin');
INSERT INTO usuario(usuario, nombre, apellidos, contrasenya, email, permiso_admin) VALUES ('jugadorBorja','Borja','Pinero Calera','pruebaJugador','borjacalerap@gmail.com','player');
INSERT INTO usuario(usuario, nombre, apellidos, contrasenya, email, permiso_admin) VALUES ('jugador1','Juan','Perez Gomez','pruebaJugador','jugador1@gmail.com','player');
INSERT INTO usuario(usuario, nombre, apellidos, contrasenya, email, permiso_admin) VALUES ('jugadorComodin','Comodin','Perez Gomez','pruebaJugador','jugador1@gmail.com','player');
INSERT INTO usuario(usuario, nombre, apellidos, contrasenya, email, permiso_admin) VALUES ('jugadorComodin2','Comodin','Perez Gomez','pruebaJugador','jugador1@gmail.com','player');
INSERT INTO usuario(usuario, nombre, apellidos, contrasenya, email, permiso_admin) VALUES ('jugadorComodin3','Comodin','Perez Gomez','pruebaJugador','jugador1@gmail.com','player');
INSERT INTO usuario(usuario, nombre, apellidos, contrasenya, email, permiso_admin) VALUES ('jugadorComodin4','Comodin','Perez Gomez','pruebaJugador','jugador1@gmail.com','player');
INSERT INTO usuario(usuario, nombre, apellidos, contrasenya, email, permiso_admin) VALUES ('jugadorComodin5','Comodin','Perez Gomez','pruebaJugador','jugador1@gmail.com','player');
INSERT INTO usuario(usuario, nombre, apellidos, contrasenya, email, permiso_admin) VALUES ('jugadorComodin6','Comodin','Perez Gomez','pruebaJugador','jugador1@gmail.com','player');
INSERT INTO usuario(usuario, nombre, apellidos, contrasenya, email, permiso_admin) VALUES ('jugadorComodin7','Comodin','Perez Gomez','pruebaJugador','jugador1@gmail.com','player');

-- Probar partida
INSERT INTO partida(ganador_usuario, estado, fecha, j1_usuario, j2_usuario) VALUES ('jugador1','FINALIZADA','2021-11-05', 'jugador1', 'jugadorBorja');
INSERT INTO partida(ganador_usuario, estado, fecha, j1_usuario, j2_usuario) VALUES ('jugadorBorja','EN_CURSO','2021-11-05', 'jugadorBorja', 'jugador1');
INSERT INTO partida(ganador_usuario, estado, fecha, j1_usuario, j2_usuario) VALUES (NULL,'PENDIENTE','2021-11-05', 'jugadorComodin', 'jugadorComodin2');

-- Inserción de cartas
INSERT INTO cartas(id, iniciativa, color, salida_arriba, salida_izquierda, salida_derecha, salida_abajo, imagen_azul, imagen_rosa, multiplicidad) VALUES (1, 1, 'AZUL', TRUE, NULL, NULL, FALSE, 'https://i.ibb.co/bQpCqxQ/azul-i1.png', '', 9);
INSERT INTO cartas(id, iniciativa, color, salida_arriba, salida_izquierda, salida_derecha, salida_abajo, imagen_azul, imagen_rosa, multiplicidad) VALUES (2, 2, 'AZUL', NULL, TRUE, NULL, FALSE, 'https://i.ibb.co/QpXRbZQ/azul-i2a.png', '', 4);
INSERT INTO cartas(id, iniciativa, color, salida_arriba, salida_izquierda, salida_derecha, salida_abajo, imagen_azul, imagen_rosa, multiplicidad) VALUES (3, 2, 'AZUL', NULL, NULL, TRUE, FALSE, 'https://i.ibb.co/DgGjswZ/azul-i2b.png', '', 4 );
INSERT INTO cartas(id, iniciativa, color, salida_arriba, salida_izquierda, salida_derecha, salida_abajo, imagen_azul, imagen_rosa, multiplicidad) VALUES (4, 3, 'AZUL', TRUE, TRUE, NULL, FALSE, 'https://i.ibb.co/5rf55BZ/azul-i3a.png', '',2);
INSERT INTO cartas(id, iniciativa, color, salida_arriba, salida_izquierda, salida_derecha, salida_abajo, imagen_azul, imagen_rosa, multiplicidad) VALUES (5, 3, 'AZUL', TRUE, NULL, TRUE, FALSE, 'https://i.ibb.co/xSmNdg0/azul-i3b.png', '', 2);
INSERT INTO cartas(id, iniciativa, color, salida_arriba, salida_izquierda, salida_derecha, salida_abajo, imagen_azul, imagen_rosa, multiplicidad) VALUES (6, 4, 'AZUL', NULL, TRUE, TRUE, FALSE, 'https://i.ibb.co/hV0hmrj/azul-i4.png', '', 2);
INSERT INTO cartas(id, iniciativa, color, salida_arriba, salida_izquierda, salida_derecha, salida_abajo, imagen_azul, imagen_rosa, multiplicidad) VALUES (7, 5, 'AZUL', TRUE, TRUE, TRUE, FALSE, 'https://i.ibb.co/4400wkR/azul-i5.png', '', 1);
INSERT INTO cartas(id, iniciativa, color, salida_arriba, salida_izquierda, salida_derecha, salida_abajo, imagen_azul, imagen_rosa, multiplicidad) VALUES (8, 0, 'AZUL', TRUE, TRUE, TRUE, FALSE, 'https://i.ibb.co/Tk94SNZ/azul-i0.png', '', 1);

INSERT INTO cartas(id, iniciativa, color, salida_arriba, salida_izquierda, salida_derecha, salida_abajo, imagen_azul, imagen_rosa, multiplicidad) VALUES (9, 1, 'ROSA', TRUE, NULL, NULL, FALSE, '', 'https://i.ibb.co/34R7kbn/rosa-i1.png' , 9);
INSERT INTO cartas(id, iniciativa, color, salida_arriba, salida_izquierda, salida_derecha, salida_abajo, imagen_azul, imagen_rosa, multiplicidad) VALUES (10, 2, 'ROSA', NULL, TRUE, NULL, FALSE, '', 'https://i.ibb.co/DD9YdP1/rosa-i2a.png', 4);
INSERT INTO cartas(id, iniciativa, color, salida_arriba, salida_izquierda, salida_derecha, salida_abajo, imagen_azul, imagen_rosa, multiplicidad) VALUES (11, 2, 'ROSA', NULL, NULL, TRUE, FALSE, '', 'https://i.ibb.co/Wvs18kh/rosa-i2b.png', 4);
INSERT INTO cartas(id, iniciativa, color, salida_arriba, salida_izquierda, salida_derecha, salida_abajo, imagen_azul, imagen_rosa, multiplicidad) VALUES (12, 3, 'ROSA', TRUE, TRUE, NULL, FALSE, '', 'https://i.ibb.co/rQ62P15/rosa-i3a.png', 2);
INSERT INTO cartas(id, iniciativa, color, salida_arriba, salida_izquierda, salida_derecha, salida_abajo, imagen_azul, imagen_rosa, multiplicidad) VALUES (13, 3, 'ROSA', TRUE, NULL, TRUE, FALSE, '', 'https://i.ibb.co/BTvKNY3/rosa-i3b.png', 2);
INSERT INTO cartas(id, iniciativa, color, salida_arriba, salida_izquierda, salida_derecha, salida_abajo, imagen_azul, imagen_rosa, multiplicidad) VALUES (14, 4, 'ROSA', NULL, TRUE, TRUE, FALSE, '', 'https://i.ibb.co/TkvJrmm/rosa-i4.png', 2);
INSERT INTO cartas(id, iniciativa, color, salida_arriba, salida_izquierda, salida_derecha, salida_abajo, imagen_azul, imagen_rosa, multiplicidad) VALUES (15, 5, 'ROSA', TRUE, TRUE, TRUE, FALSE, '', 'https://i.ibb.co/PcsYx6R/rosa-i5.png',1);
INSERT INTO cartas(id, iniciativa, color, salida_arriba, salida_izquierda, salida_derecha, salida_abajo, imagen_azul, imagen_rosa, multiplicidad) VALUES (16, 0, 'ROSA', TRUE, TRUE, TRUE, FALSE, '', 'https://i.ibb.co/wCWfc9Y/rosa-i0.png', 1);

-- Insercion de mazo
INSERT INTO mazo(turno, usa_efecto, j_usuario, partida_id, ronda) VALUES ( true, false, 'jugadorBorja', 2, 1);
INSERT INTO mazo(turno, usa_efecto, j_usuario, partida_id, ronda) VALUES ( false, false, 'jugador1', 2, 1);


-- Inserción de posiciones
INSERT INTO posicion(fila, columna, grado_rotado, carta_id, j_usuario, p_id, fecha) VALUES(1, 1, 'GRADO_0', 1, 'jugador1', 1, '2021-11-05');
INSERT INTO posicion(fila, columna, grado_rotado, carta_id, j_usuario, p_id, fecha) VALUES(1, 2, 'GRADO_0', 1, 'jugador1', 1, '2021-11-05');
INSERT INTO posicion(fila, columna, grado_rotado, carta_id, j_usuario, p_id, fecha) VALUES(1, 3, 'GRADO_0', 2, 'jugadorBorja', 1, '2021-11-05');
INSERT INTO posicion(fila, columna, grado_rotado, carta_id, j_usuario, p_id, fecha) VALUES(1, 4, 'GRADO_0', 2, 'jugadorBorja', 1, '2021-11-05');

INSERT INTO posicion(fila, columna, grado_rotado, carta_id, j_usuario, p_id, fecha) VALUES(1, 1, 'GRADO_0', 1, 'jugador1', 2, '2021-11-05');
INSERT INTO posicion(fila, columna, grado_rotado, carta_id, j_usuario, p_id, fecha) VALUES(1, 2, 'GRADO_0', 1, 'jugador1', 2, '2021-11-05');
INSERT INTO posicion(fila, columna, grado_rotado, carta_id, j_usuario, p_id, fecha) VALUES(1, 3, 'GRADO_0', 2, 'jugadorBorja', 2, '2021-11-05');
INSERT INTO posicion(fila, columna, grado_rotado, carta_id, j_usuario, p_id, fecha) VALUES(1, 4, 'GRADO_0', 2, 'jugadorBorja', 2, '2021-11-05');