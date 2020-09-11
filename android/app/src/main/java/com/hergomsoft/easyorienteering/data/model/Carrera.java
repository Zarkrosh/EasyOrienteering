package com.hergomsoft.easyorienteering.data.model;

import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.Expose;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Entity(tableName = "carreras")
public class Carrera {

    public enum Tipo {EVENTO, CIRCUITO};
    public enum Modalidad {TRAZADO, SCORE};

    @PrimaryKey
    @Expose
    private Long id;
    @Expose
    private String nombre;
    @Expose
    private Tipo tipo;
    @Expose
    private Modalidad modalidad;
    @Expose
    private boolean privada;
    @Expose
    private Float latitud;
    @Expose
    private Float longitud;
    @Expose
    private String notas;
    @Expose
    private Date fecha;

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

    public String getNotas() { return notas; }
    public void setNotas(String notas) { this.notas = notas; }

    public boolean isPrivada() { return privada; }
    public void setPrivada(boolean privada) { this.privada = privada; }

    public Float getLatitud() { return latitud; }
    public void setLatitud(Float latitud) { this.latitud = latitud; }

    public Float getLongitud() { return longitud; }
    public void setLongitud(Float longitud) { this.longitud = longitud; }

    public Date getFecha() { return fecha; }
    public void setFecha(Date fecha) { this.fecha = fecha; }

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
