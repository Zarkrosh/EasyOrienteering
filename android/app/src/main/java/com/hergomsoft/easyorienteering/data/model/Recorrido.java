package com.hergomsoft.easyorienteering.data.model;

import com.google.gson.annotations.Expose;

public class Recorrido {

    @Expose
    private String nombre;

    @Expose
    private String[] trazado;

    public String getNombre() {
        return nombre;
    }

    public String[] getTrazado() {
        return trazado;
    }
}
