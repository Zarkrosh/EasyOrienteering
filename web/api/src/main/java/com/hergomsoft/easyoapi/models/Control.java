package com.hergomsoft.easyoapi.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "controles")
public class Control {
    
    public enum TIPO {SALIDA, CONTROL, META};
    
    @Id
    @Column(name = "CODIGO")
    private String codigo;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "TIPO")
    private TIPO tipo;

    public Control() {}

    public Control(String codigo, TIPO tipo) {
        this.codigo = codigo;
        this.tipo = tipo;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public TIPO getTipo() {
        return tipo;
    }

    public void setTipo(TIPO tipo) {
        this.tipo = tipo;
    }
    
}
