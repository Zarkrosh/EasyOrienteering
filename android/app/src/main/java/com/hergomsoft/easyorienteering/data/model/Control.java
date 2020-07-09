package com.hergomsoft.easyorienteering.data.model;

import com.google.gson.annotations.Expose;

public class Control {
    @Expose
    private String codigo;

    @Expose
    private String tipo;

    public String getCodigo() {
        return codigo;
    }

    public String getTipo() {
        return tipo;
    }
}
