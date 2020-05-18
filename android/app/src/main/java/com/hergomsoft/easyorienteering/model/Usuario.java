package com.hergomsoft.easyorienteering.model;

public class Usuario {

    private String email;
    private String nombre;

    public Usuario(String email, String nombre) {
        this.email = email;
        this.nombre = nombre;
    }

    public String getEmail() {
        return email;
    }
    public String getNombre() {
        return nombre;
    }
}
