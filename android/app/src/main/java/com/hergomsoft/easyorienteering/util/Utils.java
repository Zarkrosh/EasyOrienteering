package com.hergomsoft.easyorienteering.util;

import android.util.Log;

public final class Utils {

    public static final int MIN_PASSWORD_LENGTH = 8;

    /*************** EXPRESIONES REGULARES ***************/
    public static final String REGEX_USERNAME = "^[a-zA-Z0-9_ ]+$";
    // https://emailregex.com/ (RFC 5322 Official Standard)
    public static final String REGEX_EMAIL = "^(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])$";
    public static final String REGEX_SCAN_TRIANGULO = "^S\\d+-\\d+-\\d+-[0-9a-zA-Z]+$"; // CODIGO-IDCARRERA-IDRECORRIDO-SECRETO
    public static final String REGEX_SCAN_CONTROL = "^\\d+-[0-9a-zA-Z]+$";
    public static final String REGEX_SCAN_META = "^M\\d+-[0-9a-zA-Z]+$";

    // Comprueba nombre de usuario válido
    public static boolean nombreUsuarioValido(String username) {
        return username != null && username.matches(REGEX_USERNAME);
    }

    // Comprueba email válido
    public static boolean emailValido(String email) {
        return email != null && email.matches(REGEX_EMAIL);
    }

    // Comprueba requisitos de seguridad de la contraseña
    public static boolean isPasswordValid(String password) {
        return password != null && password.trim().length() >= MIN_PASSWORD_LENGTH;
    }

    // Comprueba si el escaneo de un control se corresponde con el de un triángulo
    public static boolean esEscaneoTriangulo(String escaneado) {
        return escaneado != null && escaneado.trim().matches(REGEX_SCAN_TRIANGULO);
    }

    // Comprueba si el escaneo de un control se corresponde con el de un control
    public static boolean esEscaneoControl(String escaneado) {
        return escaneado != null && escaneado.trim().matches(REGEX_SCAN_CONTROL);
    }

    // Comprueba si el escaneo de un control se corresponde con el de un meta
    public static boolean esEscaneoMeta(String escaneado) {
        return escaneado != null && escaneado.trim().matches(REGEX_SCAN_META);
    }

    /**
     * Devuelve el código del control escaneado.
     * @param escaneado Texto escaneado del control
     * @return Código del control
     */
    public static String getCodigoControlEscaneado(String escaneado) {
        // En todos los tipos es el primer elemento separado por "-".
        return escaneado.split("-")[0];
    }

    /**
     * Devuelve el identificador del triángulo de salida de un recorrido, o null si hay algún error.
     * @param escaneado Texto escaneado del triángulo
     * @return ID del recorrido o null
     */
    public static Long getIdentificadorCarreraEscaneado(String escaneado) {
        // Según REGEX_SCAN_TRIANGULO el ID de la carrera es el segundo elemento delimitado por "-"
        Long res = null;
        try {
            res = Long.parseLong(escaneado.split("-")[1]);
        } catch(Exception e) {
            Log.d("EASYO", "Error al procesar el identificador de la carrera: " + escaneado);
        }

        return res;
    }

    /**
     * Devuelve el identificador del triángulo de salida de un recorrido, o null si hay algún error.
     * @param escaneado Texto escaneado del triángulo
     * @return ID del recorrido o null
     */
    public static Long getIdentificadorRecorridoEscaneado(String escaneado) {
        // Según REGEX_SCAN_TRIANGULO el ID del recorrido es el tercer elemento delimitado por "-"
        Long res = null;
        try {
            res = Long.parseLong(escaneado.split("-")[2]);
        } catch(Exception e) {
            Log.d("EASYO", "Error al procesar el identificador del recorrido: " + escaneado);
        }

        return res;
    }

    /**
     * Devuelve el secreto del control escaneado.
     * @param escaneado Texto escaneado del control
     * @return Secreto del control
     */
    public static String getSecretoControlEscaneado(String escaneado) {
        // En todos los tipos es el último elemento separado por "-".
        String[] campos = escaneado.split("-");
        return campos[campos.length - 1];
    }
}
