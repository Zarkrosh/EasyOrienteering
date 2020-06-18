package com.hergomsoft.easyoapi.models;

import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "carreras")
public class Carrera {
    public static final int MLEN_NOMBRE = 50;
   
    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "NOMBRE")
    private String nombre;
    
    @ManyToOne(optional = false, cascade = CascadeType.ALL)
    private Usuario organizador;
    
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name="CARRERA_ID", referencedColumnName="ID")
    private List<Recorrido> recorridos;
    
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name="CARRERA_ID", referencedColumnName="ID")
    private List<Control> controles;
    
    
    // PENDIENTES
    //private List<Resultado> resultados;

    public Carrera() {}
    
    public Carrera(String nombre, Usuario organizador, List<Recorrido> recorridos, List<Control> controles) {
        this.nombre = nombre;
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

