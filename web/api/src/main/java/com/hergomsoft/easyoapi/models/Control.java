package com.hergomsoft.easyoapi.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
import javax.persistence.Table;
import org.hibernate.annotations.Type;

@Entity
@Table(name = "controles")
public class Control implements IdEntity {
    
    public enum TIPO {SALIDA, CONTROL, META};
    
    @JsonIgnore
    @Id
    @Column(name = "ID", columnDefinition = "serial")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "CODIGO")
    private String codigo;
    
    @Type(type = "com.hergomsoft.easyoapi.models.EnumTypesPostgres")
    @Column(name = "TIPO")
    @Enumerated(EnumType.STRING)
    private TIPO tipo;
    
    @JsonIgnore
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name="CARRERA_ID")
    private Carrera carrera;

    public Control() {}

    public Control(String codigo, TIPO tipo, Carrera carrera) {
        this.codigo = codigo;
        this.tipo = tipo;
        this.carrera = carrera;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Carrera getCarrera() {
        return carrera;
    }

    public void setCarrera(Carrera carrera) {
        this.carrera = carrera;
    }
    
    @Override
    public String toString() {
        return "Control: " + codigo + " " + tipo + " (" + id + ")";
    }
    
}
