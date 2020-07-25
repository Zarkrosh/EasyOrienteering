package com.hergomsoft.easyorienteering.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.Expose;

@Entity
public class Control {
    @PrimaryKey
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
