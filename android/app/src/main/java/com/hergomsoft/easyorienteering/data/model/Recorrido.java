package com.hergomsoft.easyorienteering.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.Expose;

@Entity
public class Recorrido {
    @PrimaryKey
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
