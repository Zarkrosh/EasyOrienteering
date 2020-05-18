package com.hergomsoft.easyorienteering.ui.conexion.registro;

import androidx.annotation.Nullable;

/**
 * Estado de la petición de registro.
 */
class RegisterState {
    public static final int ESTADO_OCULTO = 0;
    public static final int ESTADO_REGISTRANDO = 1;
    public static final int ESTADO_EXITO_PRE = 2; // Para mostrar el registro exitoso
    public static final int ESTADO_EXITO_FIN = 3; // Para redirigir tras registro
    public static final int ESTADO_ERROR = 4;

    private int estado;
    @Nullable
    private Integer mensaje;

    RegisterState() {
        this.estado = ESTADO_REGISTRANDO;
    }

    int getEstado() { return estado; }

    @Nullable
    Integer getMensaje() {
        return mensaje;
    }

    public void setEstado(int nEstado) {
        this.estado = nEstado;
    }

    public void setMensaje(int nMensaje) {
        this.mensaje = nMensaje;
    }
}
