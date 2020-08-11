package com.hergomsoft.easyoapi.models.requests;

public class BusquedaCarrerasRequest {
    private String nombre;
    private String tipo;
    private String modalidad;

    public BusquedaCarrerasRequest() {
    }

    public BusquedaCarrerasRequest(String nombre, String tipo, String modalidad) {
        this.nombre = nombre;
        this.tipo = tipo;
        this.modalidad = modalidad;
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

}
