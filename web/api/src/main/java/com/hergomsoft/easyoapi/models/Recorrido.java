package com.hergomsoft.easyoapi.models;

import java.util.List;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
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
    @JoinTable(name = "CONTROLES_RECORRIDO", 
        joinColumns = @JoinColumn(name = "RECORRIDO_ID")
    )
    @OrderColumn(name = "ORDEN")
    @Column(name = "CONTROL_CODIGO")
    private List<String> trazado; // Salida - Controles intermedios* - Meta

    public Recorrido() {}

    public Recorrido(String nombre, List<String> trazado) {
        this.nombre = nombre;
        this.trazado = trazado;
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

    public List<String> getTrazado() {
        return trazado;
    }

    public void setTrazado(List<String> trazado) {
        this.trazado = trazado;
    }

}
