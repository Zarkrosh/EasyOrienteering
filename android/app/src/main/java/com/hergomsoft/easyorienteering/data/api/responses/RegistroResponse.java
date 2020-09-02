package com.hergomsoft.easyorienteering.data.api.responses;

import com.hergomsoft.easyorienteering.data.model.Carrera;

import java.util.Date;

public class RegistroResponse {
    private long corredor;
    private String control;
    private long recorrido;
    private Date fecha;

    public long getCorredor() {
        return corredor;
    }
    public String getControl() { return control; }
    public long getRecorrido() {
        return recorrido;
    }
    public Date getFecha() {
        return fecha;
    }
}
