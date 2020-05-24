package com.hergomsoft.easyorienteering.model;

import androidx.annotation.Nullable;

/**
 * Estado de la petición de login o registro.
 */
public class ConexionState {
    public static final int ESTADO_OCULTO = 0;
    public static final int ESTADO_CARGANDO = 1;
    public static final int ESTADO_EXITO_PRE = 2; // Para mostrar el registro exitoso
    public static final int ESTADO_EXITO_FIN = 3; // Para redirigir tras registro
    public static final int ESTADO_ERROR = 4;

    private int estado;
    @Nullable
    private Integer mensaje;

    public ConexionState() {
        this.estado = ESTADO_CARGANDO;
    }

    public int getEstado() { return estado; }

    @Nullable
    public Integer getMensaje() {
        return mensaje;
    }

    public void setEstado(int nEstado) {
        this.estado = nEstado;
    }

    public void setMensaje(int nMensaje) {
        this.mensaje = nMensaje;
    }
}
