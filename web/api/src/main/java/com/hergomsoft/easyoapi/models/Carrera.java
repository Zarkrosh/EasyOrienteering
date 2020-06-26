package com.hergomsoft.easyoapi.models;

import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import org.hibernate.annotations.Type;

@Entity
@Table(name = "carreras")
public class Carrera {
    public static final int MLEN_NOMBRE = 50;
    
    public enum TIPO {EVENTO, CIRCUITO};
    public enum MODALIDAD {LINEA, SCORE};
   
    @Id
    @Column(name = "ID", columnDefinition = "serial")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "NOMBRE")
    private String nombre;
    
    @Enumerated(EnumType.STRING)
    @Type(type = "com.hergomsoft.easyoapi.models.EnumTypesPostgres")
    @Column(name = "TIPO")
    private TIPO tipo;
    
    @Enumerated(EnumType.STRING)
    @Type(type = "com.hergomsoft.easyoapi.models.EnumTypesPostgres")
    @Column(name = "MODALIDAD")
    private MODALIDAD modalidad;
    
    @ManyToOne(optional = false)
    private Usuario organizador;
    
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name="CARRERA_ID", referencedColumnName="ID")
    private List<Recorrido> recorridos;
    
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY, mappedBy = "carrera")
    private List<Control> controles;
    
    // PENDIENTES
    //private List<Resultado> resultados;

    public Carrera() {}

    public Carrera(String nombre, TIPO tipo, MODALIDAD modalidad, Usuario organizador, List<Recorrido> recorridos, List<Control> controles) {
        this.nombre = nombre;
        this.tipo = tipo;
        this.modalidad = modalidad;
        this.organizador = organizador;
        this.recorridos = recorridos;
        this.controles = controles;
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

    public TIPO getTipo() {
        return tipo;
    }

    public void setTipo(TIPO tipo) {
        this.tipo = tipo;
    }

    public MODALIDAD getModalidad() {
        return modalidad;
    }

    public void setModalidad(MODALIDAD modalidad) {
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

    public List<Control> getControles() {
        return controles;
    }

    public void setControles(List<Control> controles) {
        this.controles = controles;
    }
    
}

