package com.hergomsoft.easyorienteering.data.model;

import com.google.gson.annotations.Expose;

import java.util.Map;

public class Carrera {

    public enum Tipo {EVENTO, CIRCUITO};
    public enum Modalidad {LINEA, SCORE};

    @Expose
    private long id;
    @Expose
    private String nombre;
    @Expose
    private Tipo tipo; // TODO Enum
    @Expose
    private Modalidad modalidad; // TODO Enum
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

    public Tipo getTipo() {
        return tipo;
    }

    public Modalidad getModalidad() {
        return modalidad;
    }

    public Recorrido[] getRecorridos() {
        return recorridos;
    }

    public Map<String, Control> getControles() {
        return controles;
    }
}
