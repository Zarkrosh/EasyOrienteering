package com.hergomsoft.easyorienteering.data.model;

import com.google.gson.annotations.Expose;

public class Control {

    public enum Tipo {SALIDA, CONTROL, META};

    @Expose
    private String codigo;
    @Expose
    private Tipo tipo;
    @Expose
    private Integer puntuacion;
    @Expose
    private Coordenadas coords;

    public String getCodigo() {
        return codigo;
    }

    public Tipo getTipo() {
        return tipo;
    }
}
