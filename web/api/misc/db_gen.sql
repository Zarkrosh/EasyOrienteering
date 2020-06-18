DROP SCHEMA public CASCADE;
CREATE SCHEMA public;

CREATE TABLE USUARIOS (
	ID SERIAL PRIMARY KEY,
	NOMBRE TEXT NOT NULL UNIQUE,
	EMAIL TEXT NOT NULL UNIQUE,
	PASSWORD TEXT NOT NULL,
	FECHA_REGISTRO DATE NOT NULL
);

CREATE TABLE CARRERAS (
	ID SERIAL PRIMARY KEY,
	NOMBRE TEXT NOT NULL,
	ORGANIZADOR_ID INTEGER REFERENCES USUARIOS(ID) NOT NULL
);

CREATE TABLE RECORRIDOS (
	NOMBRE TEXT NOT NULL,
	CARRERA_ID INTEGER REFERENCES CARRERAS(ID) NOT NULL,
	PRIMARY KEY (NOMBRE, CARRERA_ID)
);


CREATE TABLE CONTROLES (
	ID SERIAL PRIMARY KEY,
	TIPO SMALLINT NOT NULL,
	CODIGO TEXT NOT NULL,
	CARRERA_ID INTEGER REFERENCES CARRERAS(ID) NOT NULL
);



INSERT INTO USUARIOS(ID, NOMBRE, EMAIL, PASSWORD, FECHA_REGISTRO) VALUES (1, 'Pepito Pérez', 'pepito@test.com', '<redacted>', '2020-06-18');

INSERT INTO CARRERAS(ID, NOMBRE, ORGANIZADOR_ID) VALUES (1, 'Prueba de carrera', 1);

INSERT INTO RECORRIDOS(NOMBRE, CARRERA_ID) VALUES ('OPEN AMARILLO', 1);
INSERT INTO RECORRIDOS(NOMBRE, CARRERA_ID) VALUES ('OPEN NARANJA', 1);
INSERT INTO RECORRIDOS(NOMBRE, CARRERA_ID) VALUES ('OPEN ROJO', 1);

INSERT INTO CONTROLES(ID, TIPO, CODIGO, CARRERA_ID) VALUES (1, 0, 'S1', 1);
INSERT INTO CONTROLES(ID, TIPO, CODIGO, CARRERA_ID) VALUES (2, 1, '31', 1);
INSERT INTO CONTROLES(ID, TIPO, CODIGO, CARRERA_ID) VALUES (3, 1, '32', 1);
INSERT INTO CONTROLES(ID, TIPO, CODIGO, CARRERA_ID) VALUES (4, 1, '33', 1);
INSERT INTO CONTROLES(ID, TIPO, CODIGO, CARRERA_ID) VALUES (5, 2, 'M1', 1);