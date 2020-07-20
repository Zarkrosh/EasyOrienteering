package com.hergomsoft.easyorienteering.data.model;

import com.google.gson.annotations.Expose;

import java.util.Map;

public class Carrera {

    @Expose
    private long id;

    @Expose
    private String nombre;

    @Expose
    private String tipo; // TODO Enum

    @Expose
    private String modalidad; // TODO Enum

    @Expose
    private Recorrido[] recorridos;

    @Expose
    private Map<String, Control> controles;

    public long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getTipo() {
        return tipo;
    }

    public String getModalidad() {
        return modalidad;
    }

    public Recorrido[] getRecorridos() {
        return recorridos;
    }

    public Map<String, Control> getControles() {
        return controles;
    }
}
