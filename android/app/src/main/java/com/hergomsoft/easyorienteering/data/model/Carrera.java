package com.hergomsoft.easyorienteering.data.model;

import com.google.gson.annotations.Expose;

import java.util.List;
import java.util.Map;

public class Carrera {

    public enum Tipo {EVENTO, CIRCUITO};
    public enum Modalidad {LINEA, SCORE};

    @Expose
    private long id;
    @Expose
    private String nombre;
    @Expose
    private Tipo tipo;
    @Expose
    private Modalidad modalidad;
    @Expose
    private List<Recorrido> recorridos;
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

    public List<Recorrido> getRecorridos() {
        return recorridos;
    }

    public Map<String, Control> getControles() {
        return controles;
    }
}
