DROP SCHEMA public CASCADE;
CREATE SCHEMA public;

/* Tipos */
CREATE TYPE TIPO_CARRERA AS ENUM('EVENTO', 'CIRCUITO');
CREATE TYPE TIPO_CONTROL AS ENUM('SALIDA', 'CONTROL', 'META');
CREATE TYPE MODALIDAD AS ENUM('TRAZADO', 'SCORE');

/* Cast para inserción de controles */
CREATE CAST (CHARACTER VARYING AS TIPO_CONTROL) WITH INOUT AS IMPLICIT;
/* Cast para JSON */
--CREATE CAST (CHARACTER VARYING AS JSON) WITH INOUT AS IMPLICIT;

/* Tablas */
CREATE TABLE IF NOT EXISTS USUARIOS (
	ID SERIAL,
	NOMBRE VARCHAR(30) NOT NULL UNIQUE,
	EMAIL VARCHAR(100) NOT NULL UNIQUE,
	CLUB VARCHAR(30),
	PASSWORD VARCHAR(64) NOT NULL,
	FECHA_REGISTRO DATE NOT NULL,
	ROLES JSON NOT NULL,
	PRIMARY KEY (ID)
);

CREATE TABLE IF NOT EXISTS CARRERAS (
	ID SERIAL,
	SECRET TEXT NOT NULL,
	NOMBRE VARCHAR(100) NOT NULL,
	TIPO TIPO_CARRERA NOT NULL,
	MODALIDAD MODALIDAD NOT NULL,
	ORGANIZADOR_ID INTEGER REFERENCES USUARIOS(ID),
	PRIVADA BOOLEAN NOT NULL DEFAULT FALSE,
	LATITUD REAL,
	LONGITUD REAL,
	NOTAS VARCHAR(1000),
	FECHA DATE DEFAULT CURRENT_DATE,
	CONTROLES JSON NOT NULL,
	PRIMARY KEY (ID)
);

CREATE TABLE IF NOT EXISTS RECORRIDOS (
	ID SERIAL,
	NOMBRE TEXT NOT NULL,
	CARRERA_ID INTEGER REFERENCES CARRERAS(ID) ON DELETE CASCADE,
	TRAZADO JSON NOT NULL,
	MAPA BYTEA,
	PRIMARY KEY (ID)
);

CREATE TABLE IF NOT EXISTS PARTICIPACIONES (
	ID SERIAL,
	CORREDOR_ID INTEGER NOT NULL REFERENCES USUARIOS(ID) ON DELETE CASCADE,
	RECORRIDO_ID INTEGER NOT NULL REFERENCES RECORRIDOS(ID) ON DELETE CASCADE,
	FECHA_INICIO TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
	REGISTROS JSON NOT NULL,
	ABANDONADO BOOLEAN NOT NULL DEFAULT FALSE,
	PENDIENTE BOOLEAN NOT NULL DEFAULT TRUE,
	UNIQUE(CORREDOR_ID, RECORRIDO_ID),
	PRIMARY KEY(ID)
);