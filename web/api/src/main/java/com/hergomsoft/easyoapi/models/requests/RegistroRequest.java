package com.hergomsoft.easyoapi.models.requests;

import javax.validation.constraints.NotNull;

public class RegistroRequest {
    
    // Códigos de error asociados a una petición de registro
    public static final String ERROR_NO_EXISTE_CONTROL  = "REG-0"; // El código de control no se corresponde con ningún control de la carrera
    public static final String ERROR_ESCANEA_SALIDA     = "REG-1"; // Debe escanear una salida primero
    public static final String ERROR_YA_CORRIDO         = "REG-2"; // El usuario ya ha corrido este recorrido
    public static final String ERROR_CONTROL_EQUIVOCADO = "REG-3"; // El control no es el siguiente en su recorrido
    public static final String ERROR_YA_REGISTRADO      = "REG-4"; // El usuario ya registrado el control (SCORE)
    public static final String ERROR_SECRETO            = "REG-5"; // El secreto proporcionado no es correcto
    public static final String ERROR_OTRO_RECORRIDO     = "REG-6"; // El usuario no está corriendo este recorrido
    public static final String ERROR_ES_ORGANIZADOR     = "REG-7"; // El usuario es el organizador de la carrera
    
    @NotNull
    private String codigo; // Código del control
    @NotNull
    private String secreto; // Secreto del control

    public RegistroRequest(String codigo, String secreto) {
        this.codigo = codigo;
        this.secreto = secreto;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getSecreto() {
        return secreto;
    }

    public void setSecreto(String secreto) {
        this.secreto = secreto;
    }
}
