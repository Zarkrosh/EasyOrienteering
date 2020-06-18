package com.hergomsoft.easyoapi.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "recorridos")
public class Recorrido {
    public static final int MLEN_NOMBRE = 15;
    
    @Id
    @Column(name = "NOMBRE", length = MLEN_NOMBRE, nullable = false)
    private String nombre;
    
    // PENDIENTES
    //private List<Control> controles; // Salida - Controles intermedios* - Meta

    public Recorrido() {}

    public Recorrido(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    
}
