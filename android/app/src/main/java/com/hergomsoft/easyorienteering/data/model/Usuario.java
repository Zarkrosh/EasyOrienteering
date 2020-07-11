package com.hergomsoft.easyorienteering.data.model;

import com.google.gson.annotations.Expose;

import java.util.Date;

public class Usuario {

    @Expose
    private String nombre;
    @Expose
    private String email;
    @Expose
    private Date fechaRegistro;

    public String getNombre() {
        return nombre;
    }

    public String getEmail() {
        return email;
    }

    public Date getFechaRegistro() {
        return fechaRegistro;
    }

    public Usuario(String nombre, String email, Date fechaRegistro) {
        this.nombre = nombre;
        this.email = email;
        this.fechaRegistro = fechaRegistro;
    }
}
