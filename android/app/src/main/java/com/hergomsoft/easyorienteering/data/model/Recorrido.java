package com.hergomsoft.easyorienteering.data.model;

import com.google.gson.annotations.Expose;

public class Recorrido {

    private long id;

    @Expose
    private String nombre;

    @Expose
    private String[] trazado;

    public long getId() { return id; }
    public String getNombre() {
        return nombre;
    }
    public String[] getTrazado() {
        return trazado;
    }
}
