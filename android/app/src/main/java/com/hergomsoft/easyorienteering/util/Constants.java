package com.hergomsoft.easyorienteering.util;

public class Constants {
    // TEST TODO Borrar
    public static final long ID_USUARIO_PRUEBA = 3;

    // Extras de intents
    public static final String EXTRA_EMAIL_NOMBRE = "EXTRA_EMAIL_NOMBRE";
    public static final String EXTRA_ID_CARRERA = "EXTRA_ID_CARRERA";

    public static final int MIN_PASSWORD_LENGTH = 8;

    public static final int REFRESH_USUARIO_TIME = 60 * 5; // 5 minutos (segundos)

    // Regex
    public static final String REGEX_USERNAME = "^[a-zA-Z0-9_ ]+$";
     // https://emailregex.com/ (RFC 5322 Official Standard)
    public static final String REGEX_EMAIL = "^(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])$";
    public static final String REGEX_SCAN_TRIANGULO = "^S\\d+-\\d+-\\d+-[0-9a-zA-Z]+$"; // CODIGO-IDCARRERA-IDRECORRIDO-SECRETO
    public static final String REGEX_SCAN_CONTROL = "^\\d+-[0-9a-zA-Z]+$";
    public static final String REGEX_SCAN_META = "^M\\d+-[0-9a-zA-Z]+$";
}
