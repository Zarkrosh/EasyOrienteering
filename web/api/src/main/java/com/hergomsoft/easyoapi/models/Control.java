package com.hergomsoft.easyoapi.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "controles")
public class Control {
    // Tipo de control
    public static final int SALIDA = 0;
    public static final int NORMAL = 1;
    public static final int META = 2;
    
    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "TIPO")
    private int tipo;
    @Column(name = "CODIGO")
    private String codigo;

    public Control() {}

    public Control(int tipo, String codigo) {
        this.tipo = tipo;
        this.codigo = codigo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getTipo() {
        return tipo;
    }

    public void setTipo(int tipo) {
        this.tipo = tipo;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }
    
    
}
