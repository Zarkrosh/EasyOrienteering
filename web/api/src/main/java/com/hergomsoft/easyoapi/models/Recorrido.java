package com.hergomsoft.easyoapi.models;

import java.util.List;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OrderColumn;
import javax.persistence.Table;

@Entity
@Table(name = "recorridos")
public class Recorrido {
    public static final int MLEN_NOMBRE = 15;
    
    @Id
    @Column(name = "ID", columnDefinition = "serial")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "NOMBRE", length = MLEN_NOMBRE, nullable = false)
    private String nombre;
    
    @ElementCollection
    @CollectionTable(name = "CONTROLES_RECORRIDO", 
        joinColumns = @JoinColumn(name = "RECORRIDO_ID"))
    @OrderColumn(name = "ORDEN")
    @Column(name = "CODIGO_CONTROL")
    private List<String> controles; // Salida - Controles intermedios* - Meta

    public Recorrido() {}

    public Recorrido(String nombre) {
        this.nombre = nombre;
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

    public List<String> getControles() {
        return controles;
    }

    public void setControles(List<String> controles) {
        this.controles = controles;
    }

}
