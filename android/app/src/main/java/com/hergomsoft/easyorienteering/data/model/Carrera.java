package com.hergomsoft.easyorienteering.data.model;

import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.Expose;

import java.util.List;
import java.util.Map;

@Entity(tableName = "carreras")
public class Carrera {

    public enum Tipo {EVENTO, CIRCUITO};
    public enum Modalidad {LINEA, SCORE};

    @PrimaryKey
    @Expose
    private Long id;
    @Expose
    private String nombre;
    @Expose
    private Tipo tipo;
    @Expose
    private Modalidad modalidad;

    @Embedded(prefix = "organizador_")
    private Usuario organizador;

    @Expose
    private List<Recorrido> recorridos;
    @Ignore
    @Expose
    private Map<String, Control> controles;

    private Integer timestamp; // Momento en el que se crea en la BD

    public Carrera() {}

    public Long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Tipo getTipo() {
        return tipo;
    }

    public void setTipo(Tipo tipo) {
        this.tipo = tipo;
    }

    public Modalidad getModalidad() {
        return modalidad;
    }

    public void setModalidad(Modalidad modalidad) {
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

    public Map<String, Control> getControles() {
        return controles;
    }

    public void setControles(Map<String, Control> controles) {
        this.controles = controles;
    }

    public Integer getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Integer timestamp) {
        this.timestamp = timestamp;
    }
}
