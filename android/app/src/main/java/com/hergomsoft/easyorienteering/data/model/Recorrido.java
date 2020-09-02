package com.hergomsoft.easyorienteering.data.model;

import androidx.room.PrimaryKey;

import com.google.gson.annotations.Expose;


public class Recorrido {
    @PrimaryKey
    private long id;

    @Expose
    private String nombre;

    @Expose
    private String[] trazado;

    @Expose
    private Boolean mapa;

    public Recorrido(long id, String nombre, String[] trazado, Boolean mapa) {
        this.id = id;
        this.nombre = nombre;
        this.trazado = trazado;
        this.mapa = mapa;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String[] getTrazado() { return trazado; }
    public void setTrazado(String[] trazado) { this.trazado = trazado; }

    public Boolean tieneMapa() { return (mapa != null && mapa); }
    public void setMapa(Boolean mapa) { this.mapa = mapa; }
}
