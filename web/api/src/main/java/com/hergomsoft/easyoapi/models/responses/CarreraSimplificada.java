package com.hergomsoft.easyoapi.models.responses;

import com.hergomsoft.easyoapi.models.Carrera;
import com.hergomsoft.easyoapi.models.Recorrido;
import com.hergomsoft.easyoapi.models.Usuario;
import java.util.Date;
import java.util.List;

public class CarreraSimplificada {

    private Long id;
    private String nombre;
    private Carrera.Tipo tipo;
    private Carrera.Modalidad modalidad;
    private Usuario organizador;
    private Float latitud;
    private Float longitud;
    private Date fecha;
    private List<Recorrido> recorridos;
    
    public CarreraSimplificada(Carrera carrera) {
        this.id = carrera.getId();
        this.nombre = carrera.getNombre();
        this.tipo = carrera.getTipo();
        this.modalidad = carrera.getModalidad();
        this.organizador = carrera.getOrganizador();
        this.latitud = carrera.getLatitud();
        this.longitud = carrera.getLongitud();
        this.fecha = carrera.getFecha();
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

    public Float getLatitud() {
        return latitud;
    }

    public void setLatitud(Float latitud) {
        this.latitud = latitud;
    }

    public Float getLongitud() {
        return longitud;
    }

    public void setLongitud(Float longitud) {
        this.longitud = longitud;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public List<Recorrido> getRecorridos() {
        return recorridos;
    }

    public void setRecorridos(List<Recorrido> recorridos) {
        this.recorridos = recorridos;
    }
    
}
