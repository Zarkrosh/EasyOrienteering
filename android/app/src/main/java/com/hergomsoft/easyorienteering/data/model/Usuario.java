package com.hergomsoft.easyorienteering.data.model;

import com.google.gson.annotations.Expose;

import java.util.Date;

public class Usuario {

    @Expose
    private String nombre;
    @Expose
    private String email;
    @Expose
    private String club;
    @Expose
    private Date fechaRegistro;

    public Usuario(String nombre, String email, String club, Date fechaRegistro) {
        this.nombre = nombre;
        this.email = email;
        this.club = club;
        this.fechaRegistro = fechaRegistro;
    }

    public String getNombre() {
        return nombre;
    }
    public String getEmail() {
        return email;
    }
    public String getClub() { return club; }
    public Date getFechaRegistro() {
        return fechaRegistro;
    }

}
