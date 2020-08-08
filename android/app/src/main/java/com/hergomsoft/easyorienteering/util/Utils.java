package com.hergomsoft.easyorienteering.util;

import android.app.Activity;
import android.content.res.Resources;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public final class Utils {

    // Comprueba nombre de usuario válido
    public static boolean nombreUsuarioValido(String username) {
        return username != null && username.matches(Constants.REGEX_USERNAME);
    }

    // Comprueba email válido
    public static boolean emailValido(String email) {
        return email != null && email.matches(Constants.REGEX_EMAIL);
    }

    // Comprueba requisitos de seguridad de la contraseña
    public static boolean isPasswordValid(String password) {
        return password != null && password.trim().length() >= Constants.MIN_PASSWORD_LENGTH;
    }

    // Comprueba si el escaneo de un control se corresponde con el de un triángulo
    public static boolean esEscaneoTriangulo(String escaneado) {
        return escaneado != null && escaneado.trim().matches(Constants.REGEX_SCAN_TRIANGULO);
    }

    // Comprueba si el escaneo de un control se corresponde con el de un control
    public static boolean esEscaneoControl(String escaneado) {
        return escaneado != null && escaneado.trim().matches(Constants.REGEX_SCAN_CONTROL);
    }

    // Comprueba si el escaneo de un control se corresponde con el de un meta
    public static boolean esEscaneoMeta(String escaneado) {
        return escaneado != null && escaneado.trim().matches(Constants.REGEX_SCAN_META);
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

    public static String getTiempoResultadoFromSecs(Long tiempoSecs) {
        String res = "";
        if(tiempoSecs != null) {
            // Primero genera mm.ss
            res = String.format("%02d.%02d",
                    TimeUnit.SECONDS.toMinutes(tiempoSecs) -
                            TimeUnit.HOURS.toMinutes(TimeUnit.SECONDS.toHours(tiempoSecs)),
                    TimeUnit.SECONDS.toSeconds(tiempoSecs) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.SECONDS.toMinutes(tiempoSecs)));

            // Se añaden también las horas si es necesario
            if(TimeUnit.SECONDS.toHours(tiempoSecs) > 0) {
                res = String.format("%d:%s", TimeUnit.SECONDS.toHours(tiempoSecs), res);
            }
        }

        return res;
    }

    public static int randomInt(int min, int max) {
        return new Random().nextInt((max - min) + 1) + min;
    }

    /**
     * Convierte una medida en DP a píxeles.
     * @param dp DP
     * @param r Recursos
     * @return Medida en píxeles
     */
    public static int getPixFromDP(float dp, Resources r) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                r.getDisplayMetrics()
        );
    }

    /**
     * Hides the software keyboard in the specified activity.
     * Credits: Accepted answer in https://stackoverflow.com/questions/1109022/close-hide-android-soft-keyboard
     * @param activity Activity in which the keyboard is displayed
     */
    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        // Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        // If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
