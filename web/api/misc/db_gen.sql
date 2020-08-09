DROP SCHEMA public CASCADE;
CREATE SCHEMA public;

/* Tipos */
CREATE TYPE TIPO_CARRERA AS ENUM('EVENTO', 'CIRCUITO');
CREATE TYPE TIPO_CONTROL AS ENUM('SALIDA', 'CONTROL', 'META');
CREATE TYPE MODALIDAD AS ENUM('LINEA', 'SCORE');

/* Cast para inserción de controles */
CREATE CAST (CHARACTER VARYING AS TIPO_CONTROL) WITH INOUT AS IMPLICIT;

/* Tablas */
CREATE TABLE IF NOT EXISTS USUARIOS (
	ID SERIAL,
	NOMBRE TEXT NOT NULL UNIQUE,
	EMAIL TEXT NOT NULL UNIQUE,
	CLUB TEXT,
	PASSWORD TEXT NOT NULL,
	FECHA_REGISTRO DATE NOT NULL,
	PRIMARY KEY (ID)
);

CREATE TABLE IF NOT EXISTS CARRERAS (
	ID SERIAL,
	SECRET TEXT NOT NULL,
	NOMBRE TEXT NOT NULL,
	TIPO TIPO_CARRERA NOT NULL,
	MODALIDAD MODALIDAD NOT NULL,
	ORGANIZADOR_ID INTEGER REFERENCES USUARIOS(ID),
	PRIMARY KEY (ID)
);

CREATE TABLE IF NOT EXISTS RECORRIDOS (
	ID SERIAL,
	NOMBRE TEXT NOT NULL,
	CARRERA_ID INTEGER REFERENCES CARRERAS(ID) ON DELETE CASCADE,
	PRIMARY KEY (ID)
);

CREATE TABLE IF NOT EXISTS CONTROLES (
	ID SERIAL,
	CODIGO TEXT NOT NULL,
	CARRERA_ID INTEGER NOT NULL REFERENCES CARRERAS(ID) ON DELETE CASCADE,
	TIPO TIPO_CONTROL NOT NULL,
	PUNTUACION INTEGER,
	PRIMARY KEY (ID),
	UNIQUE (CODIGO, CARRERA_ID)
);

CREATE TABLE IF NOT EXISTS CONTROLES_RECORRIDO (
	RECORRIDO_ID INTEGER REFERENCES RECORRIDOS(ID) ON DELETE CASCADE,
	ORDEN SMALLINT,
	CONTROL_CODIGO TEXT,
	PRIMARY KEY(RECORRIDO_ID, ORDEN)
);

CREATE TABLE IF NOT EXISTS REGISTROS (
	ID SERIAL,
	CORREDOR_ID INTEGER NOT NULL REFERENCES USUARIOS(ID) ON DELETE CASCADE,
	CONTROL_ID INTEGER NOT NULL REFERENCES CONTROLES(ID) ON DELETE CASCADE,
	RECORRIDO_ID INTEGER NOT NULL REFERENCES RECORRIDOS(ID) ON DELETE CASCADE,
	FECHA TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	PRIMARY KEY (ID),
	UNIQUE (CORREDOR_ID, CONTROL_ID, RECORRIDO_ID, FECHA)
);

/**** DATOS DE PRUEBA ****/
ALTER SEQUENCE carreras_id_seq restart with 3;   -- Debido a las inserciones de prueba
ALTER SEQUENCE controles_id_seq restart with 15; -- Debido a las inserciones de prueba
INSERT INTO USUARIOS(ID, NOMBRE, EMAIL, CLUB, PASSWORD, FECHA_REGISTRO) VALUES (1, 'Pepito Pérez', 'pepito@test.com', 'ORCA', '<redacted>', '2020-06-18');
INSERT INTO USUARIOS(ID, NOMBRE, EMAIL, CLUB, PASSWORD, FECHA_REGISTRO) VALUES (2, 'Juanito Juárez', 'juanito@test.com', '', '<redacted>', '2020-06-20');
INSERT INTO USUARIOS(ID, NOMBRE, EMAIL, CLUB, PASSWORD, FECHA_REGISTRO) VALUES (3, 'Fernandito Fernández', 'fernandito@test.com', 'TJALVE', '<redacted>', '2020-07-06');

INSERT INTO CARRERAS(ID, SECRET, NOMBRE, TIPO, MODALIDAD, ORGANIZADOR_ID) VALUES (1, 'not-a-real-secret', 'Prueba de carrera', 'EVENTO', 'LINEA', 1);
INSERT INTO CARRERAS(ID, SECRET, NOMBRE, TIPO, MODALIDAD, ORGANIZADOR_ID) VALUES (2, 'not-a-real-secret', 'Otra carrera', 'EVENTO', 'SCORE', 3);

/* Recorridos Carrera 1 (LINEA) */
INSERT INTO RECORRIDOS(NOMBRE, CARRERA_ID) VALUES ('OPEN AMARILLO', 1);
INSERT INTO RECORRIDOS(NOMBRE, CARRERA_ID) VALUES ('OPEN NARANJA', 1);
INSERT INTO RECORRIDOS(NOMBRE, CARRERA_ID) VALUES ('OPEN ROJO', 1);
/* Recorridos Carrera 2 (SCORE) */
INSERT INTO RECORRIDOS(NOMBRE, CARRERA_ID) VALUES ('ASDF', 2);


/* Controles Carrera 1 (LINEA) */
INSERT INTO CONTROLES(ID, CODIGO, CARRERA_ID, TIPO, PUNTUACION) VALUES (1, 'S1', 1, 'SALIDA', 0);
INSERT INTO CONTROLES(ID, CODIGO, CARRERA_ID, TIPO, PUNTUACION) VALUES (2, '31', 1, 'CONTROL', 0);
INSERT INTO CONTROLES(ID, CODIGO, CARRERA_ID, TIPO, PUNTUACION) VALUES (3, '32', 1, 'CONTROL', 0);
INSERT INTO CONTROLES(ID, CODIGO, CARRERA_ID, TIPO, PUNTUACION) VALUES (4, '33', 1, 'CONTROL', 0);
INSERT INTO CONTROLES(ID, CODIGO, CARRERA_ID, TIPO, PUNTUACION) VALUES (5, 'M1', 1, 'META', 0);
/* Controles Carrera 2 (SCORE) */
INSERT INTO CONTROLES(ID, CODIGO, CARRERA_ID, TIPO, PUNTUACION) VALUES (6, 'S1', 2, 'SALIDA', 0);
INSERT INTO CONTROLES(ID, CODIGO, CARRERA_ID, TIPO, PUNTUACION) VALUES (7, '31', 2, 'CONTROL', 3);
INSERT INTO CONTROLES(ID, CODIGO, CARRERA_ID, TIPO, PUNTUACION) VALUES (8, '32', 2, 'CONTROL', 3);
INSERT INTO CONTROLES(ID, CODIGO, CARRERA_ID, TIPO, PUNTUACION) VALUES (9, '33', 2, 'CONTROL', 3);
INSERT INTO CONTROLES(ID, CODIGO, CARRERA_ID, TIPO, PUNTUACION) VALUES (10, '34', 2, 'CONTROL', 3);
INSERT INTO CONTROLES(ID, CODIGO, CARRERA_ID, TIPO, PUNTUACION) VALUES (11, '35', 2, 'CONTROL', 3);
INSERT INTO CONTROLES(ID, CODIGO, CARRERA_ID, TIPO, PUNTUACION) VALUES (12, '36', 2, 'CONTROL', 3);
INSERT INTO CONTROLES(ID, CODIGO, CARRERA_ID, TIPO, PUNTUACION) VALUES (13, '37', 2, 'CONTROL', 3);
INSERT INTO CONTROLES(ID, CODIGO, CARRERA_ID, TIPO, PUNTUACION) VALUES (14, 'M1', 2, 'META', 0);


/* Trazado Open amarillo */
INSERT INTO CONTROLES_RECORRIDO(RECORRIDO_ID, ORDEN, CONTROL_CODIGO) VALUES (1, 0, 'S1');
INSERT INTO CONTROLES_RECORRIDO(RECORRIDO_ID, ORDEN, CONTROL_CODIGO) VALUES (1, 1, '31');
INSERT INTO CONTROLES_RECORRIDO(RECORRIDO_ID, ORDEN, CONTROL_CODIGO) VALUES (1, 2, '33');
INSERT INTO CONTROLES_RECORRIDO(RECORRIDO_ID, ORDEN, CONTROL_CODIGO) VALUES (1, 3, 'M1');
/* Trazado Open naranja */
INSERT INTO CONTROLES_RECORRIDO(RECORRIDO_ID, ORDEN, CONTROL_CODIGO) VALUES (2, 0, 'S1');
INSERT INTO CONTROLES_RECORRIDO(RECORRIDO_ID, ORDEN, CONTROL_CODIGO) VALUES (2, 1, '32');
INSERT INTO CONTROLES_RECORRIDO(RECORRIDO_ID, ORDEN, CONTROL_CODIGO) VALUES (2, 2, '31');
INSERT INTO CONTROLES_RECORRIDO(RECORRIDO_ID, ORDEN, CONTROL_CODIGO) VALUES (2, 3, '33');
INSERT INTO CONTROLES_RECORRIDO(RECORRIDO_ID, ORDEN, CONTROL_CODIGO) VALUES (2, 4, 'M1');
/* Trazado Open rojo (vacío) */
/* Trazado carrera 2 (SCORE) */
INSERT INTO CONTROLES_RECORRIDO(RECORRIDO_ID, ORDEN, CONTROL_CODIGO) VALUES (4, 0, 'S1');
INSERT INTO CONTROLES_RECORRIDO(RECORRIDO_ID, ORDEN, CONTROL_CODIGO) VALUES (4, 1, '31');
INSERT INTO CONTROLES_RECORRIDO(RECORRIDO_ID, ORDEN, CONTROL_CODIGO) VALUES (4, 2, '32');
INSERT INTO CONTROLES_RECORRIDO(RECORRIDO_ID, ORDEN, CONTROL_CODIGO) VALUES (4, 3, '33');
INSERT INTO CONTROLES_RECORRIDO(RECORRIDO_ID, ORDEN, CONTROL_CODIGO) VALUES (4, 4, '34');
INSERT INTO CONTROLES_RECORRIDO(RECORRIDO_ID, ORDEN, CONTROL_CODIGO) VALUES (4, 5, '35');
INSERT INTO CONTROLES_RECORRIDO(RECORRIDO_ID, ORDEN, CONTROL_CODIGO) VALUES (4, 6, '36');
INSERT INTO CONTROLES_RECORRIDO(RECORRIDO_ID, ORDEN, CONTROL_CODIGO) VALUES (4, 7, '37');
INSERT INTO CONTROLES_RECORRIDO(RECORRIDO_ID, ORDEN, CONTROL_CODIGO) VALUES (4, 8, 'M1');


/* Pepito (1): Open amarillo acabado */
INSERT INTO REGISTROS(CORREDOR_ID, CONTROL_ID, RECORRIDO_ID, FECHA) VALUES (1, 1, 1, '2020-06-30 09:00:00');
INSERT INTO REGISTROS(CORREDOR_ID, CONTROL_ID, RECORRIDO_ID, FECHA) VALUES (1, 2, 1, '2020-06-30 09:01:15');
INSERT INTO REGISTROS(CORREDOR_ID, CONTROL_ID, RECORRIDO_ID, FECHA) VALUES (1, 4, 1, '2020-06-30 09:04:48');
INSERT INTO REGISTROS(CORREDOR_ID, CONTROL_ID, RECORRIDO_ID, FECHA) VALUES (1, 5, 1, '2020-06-30 09:08:32');
/* Pepito (1): Open naranja sin acabar */
INSERT INTO REGISTROS(CORREDOR_ID, CONTROL_ID, RECORRIDO_ID, FECHA) VALUES (1, 1, 2, '2020-06-30 09:15:03');
INSERT INTO REGISTROS(CORREDOR_ID, CONTROL_ID, RECORRIDO_ID, FECHA) VALUES (1, 3, 2, '2020-06-30 09:15:40');
INSERT INTO REGISTROS(CORREDOR_ID, CONTROL_ID, RECORRIDO_ID, FECHA) VALUES (1, 2, 2, '2020-06-30 09:18:48');

/* Juanito (2): Open naranja acabado */
INSERT INTO REGISTROS(CORREDOR_ID, CONTROL_ID, RECORRIDO_ID, FECHA) VALUES (2, 1, 2, '2020-06-30 09:05:18');
INSERT INTO REGISTROS(CORREDOR_ID, CONTROL_ID, RECORRIDO_ID, FECHA) VALUES (2, 3, 2, '2020-06-30 09:05:43');
INSERT INTO REGISTROS(CORREDOR_ID, CONTROL_ID, RECORRIDO_ID, FECHA) VALUES (2, 2, 2, '2020-06-30 09:10:52');
INSERT INTO REGISTROS(CORREDOR_ID, CONTROL_ID, RECORRIDO_ID, FECHA) VALUES (2, 4, 2, '2020-06-30 09:11:35');
INSERT INTO REGISTROS(CORREDOR_ID, CONTROL_ID, RECORRIDO_ID, FECHA) VALUES (2, 5, 2, '2020-06-30 09:13:14');
/* Juanito (2): carrera score acabada con algunos controles */
INSERT INTO REGISTROS(CORREDOR_ID, CONTROL_ID, RECORRIDO_ID, FECHA) VALUES (2, 6, 4, '2020-06-30 09:25:36');
INSERT INTO REGISTROS(CORREDOR_ID, CONTROL_ID, RECORRIDO_ID, FECHA) VALUES (2, 7, 4, '2020-06-30 09:26:51');
INSERT INTO REGISTROS(CORREDOR_ID, CONTROL_ID, RECORRIDO_ID, FECHA) VALUES (2, 9, 4, '2020-06-30 09:28:18');
INSERT INTO REGISTROS(CORREDOR_ID, CONTROL_ID, RECORRIDO_ID, FECHA) VALUES (2, 11, 4, '2020-06-30 09:29:05');
INSERT INTO REGISTROS(CORREDOR_ID, CONTROL_ID, RECORRIDO_ID, FECHA) VALUES (2, 13, 4, '2020-06-30 09:31:49');
INSERT INTO REGISTROS(CORREDOR_ID, CONTROL_ID, RECORRIDO_ID, FECHA) VALUES (2, 14, 4, '2020-06-30 09:32:23');
