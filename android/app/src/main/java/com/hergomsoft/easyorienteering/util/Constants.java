package com.hergomsoft.easyorienteering.util;

import java.util.HashMap;
import java.util.Map;

public class Constants {
    // TEST TODO Borrar
    public static final long ID_USUARIO_PRUEBA = 3;

    // SharedPreferences
    public static final String PREFS_NAME = "PrefsEasyO";
    public static final String PREFS_PRIMER_INICIO = "priminicio";

    // Extras de intents
    public static final String EXTRA_EMAIL_NOMBRE = "EMAIL_NOMBRE";
    public static final String EXTRA_ID_CARRERA = "ID_CARRERA";
    public static final String EXTRA_ID_USUARIO = "ID_USUARIO";
    public static final String EXTRA_ID_RECORRIDO = "ID_RECORRIDO";
    public static final String EXTRA_VOLUNTARIO = "VOLUNTARIO";

    public static final int MIN_PASSWORD_LENGTH = 8;

    public static final int REFRESH_USUARIO_TIME = 60 * 5; // 5 minutos (segundos)
    public static final int REFRESH_CARRERA_TIME = 60 * 5; // 5 minutos (segundos)

    // Regex
    public static final String REGEX_USERNAME = "^[a-zA-Z0-9_ ]+$";
     // https://emailregex.com/ (RFC 5322 Official Standard)
    public static final String REGEX_EMAIL = "^(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])$";
    // QR
    public static final String SEPARADOR_QR_REGEX = "\\|";
    public static final String REGEX_SCAN_SALIDA = "^[a-zA-Z]+" + SEPARADOR_QR_REGEX + "\\d+" + SEPARADOR_QR_REGEX + "[0-9a-zA-Z]+$"; // CODIGO|IDRECORRIDO|SECRETO
    public static final String REGEX_SCAN_CONTROL = "^[0-9a-zA-Z]+" + SEPARADOR_QR_REGEX + "[0-9a-zA-Z]+$"; // CODIGO|SECRETO


    // Sistema
    public static final String CODIGO_SALIDA = "SALIDA";
    public static final String CODIGO_META = "META";

    // Códigos de error de registro
    public static final Map<String, String> erroresRegistro = new HashMap<>();




    static {
        erroresRegistro.put("REG-0", "Este control no pertenece a la carrera");
        erroresRegistro.put("REG-1", "Tienes que escanear una salida");
        erroresRegistro.put("REG-2", "Ya has corrido este recorrido");
        erroresRegistro.put("REG-3", "Este no es tu siguiente control");
        erroresRegistro.put("REG-4", "Ya has registrado este control");
        erroresRegistro.put("REG-5", "Control incorrecto");
        erroresRegistro.put("REG-6", "Este no es el recorrido que estás corriendo");
        erroresRegistro.put("REG-7", "No puedes correr una carrera que has organizado");
    }
}
