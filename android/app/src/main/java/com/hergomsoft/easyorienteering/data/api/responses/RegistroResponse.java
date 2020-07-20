package com.hergomsoft.easyorienteering.data.api.responses;

import java.util.Date;

public class RegistroResponse {
    private long corredor;  // ID del corredor
    private String control; // Código del control
    private long recorrido; // ID del recorrido
    private Date fecha;     // Fecha del registro

    public long getCorredor() {
        return corredor;
    }

    public String getControl() {
        return control;
    }

    public long getRecorrido() {
        return recorrido;
    }

    public Date getFecha() {
        return fecha;
    }
}
