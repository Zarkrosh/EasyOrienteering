package com.hergomsoft.easyoapi.models;

public class Control {
    
    public enum Tipo {SALIDA, CONTROL, META};
    
    private String codigo;
    //@Enumerated(EnumType.STRING)
    private Tipo tipo;
    private Integer puntuacion;
    private Coordenadas coords;
    
    public Control() {}

    public Control(String codigo, Tipo tipo, Integer puntuacion, Coordenadas coords) {
        this.codigo = codigo;
        this.tipo = tipo;
        this.puntuacion = puntuacion;
        this.coords = coords;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public Tipo getTipo() {
        return tipo;
    }

    public void setTipo(Tipo tipo) {
        this.tipo = tipo;
    }

    public Integer getPuntuacion() {
        return puntuacion;
    }

    public void setPuntuacion(Integer puntuacion) {
        this.puntuacion = puntuacion;
    }

    public Coordenadas getCoords() {
        return coords;
    }

    public void setCoords(Coordenadas coords) {
        this.coords = coords;
    }
    
    @Override
    public String toString() {
        return "Control: " + codigo + " " + tipo;
    }
    
}
