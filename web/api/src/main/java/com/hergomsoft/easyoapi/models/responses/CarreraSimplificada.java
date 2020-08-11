package com.hergomsoft.easyoapi.models.responses;

import com.hergomsoft.easyoapi.models.Carrera;
import com.hergomsoft.easyoapi.models.Recorrido;
import com.hergomsoft.easyoapi.models.Usuario;
import java.util.List;

public class CarreraSimplificada {

    private Long id;
    private String nombre;
    private Carrera.Tipo tipo;
    private Carrera.Modalidad modalidad;
    private Usuario organizador;
    private List<Recorrido> recorridos;
    
    public CarreraSimplificada(Carrera carrera) {
        this.id = carrera.getId();
        this.nombre = carrera.getNombre();
        this.tipo = carrera.getTipo();
        this.modalidad = carrera.getModalidad();
        this.organizador = carrera.getOrganizador();
        this.recorridos = carrera.getRecorridos();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Carrera.Tipo getTipo() {
        return tipo;
    }

    public void setTipo(Carrera.Tipo tipo) {
        this.tipo = tipo;
    }

    public Carrera.Modalidad getModalidad() {
        return modalidad;
    }

    public void setModalidad(Carrera.Modalidad modalidad) {
        this.modalidad = modalidad;
    }

    public Usuario getOrganizador() {
        return organizador;
    }

    public void setOrganizador(Usuario organizador) {
        this.organizador = organizador;
    }

    public List<Recorrido> getRecorridos() {
        return recorridos;
    }

    public void setRecorridos(List<Recorrido> recorridos) {
        this.recorridos = recorridos;
    }
    
    
}
