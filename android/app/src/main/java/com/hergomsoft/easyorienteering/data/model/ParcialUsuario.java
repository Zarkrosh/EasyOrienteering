package com.hergomsoft.easyorienteering.data.model;

public class ParcialUsuario {

    private Long tiempoParcial;
    private Long tiempoAcumulado;
    private int posicionParcial;
    private int posicionAcumulada;

    public ParcialUsuario(Long tiempoParcial, Long tiempoAcumulado) {
        this.tiempoParcial = tiempoParcial;
        this.tiempoAcumulado = tiempoAcumulado;
    }

    public ParcialUsuario(Long tiempoParcial, Long tiempoAcumulado, int posicionParcial, int posicionAcumulada) {
        this.tiempoParcial = tiempoParcial;
        this.tiempoAcumulado = tiempoAcumulado;
        this.posicionParcial = posicionParcial;
        this.posicionAcumulada = posicionAcumulada;
    }

    public Long getTiempoParcial() {
        return tiempoParcial;
    }

    public void setTiempoParcial(Long tiempoParcial) {
        this.tiempoParcial = tiempoParcial;
    }

    public Long getTiempoAcumulado() {
        return tiempoAcumulado;
    }

    public void setTiempoAcumulado(Long tiempoAcumulado) {
        this.tiempoAcumulado = tiempoAcumulado;
    }

    public int getPosicionParcial() {
        return posicionParcial;
    }

    public void setPosicionParcial(int posicionParcial) {
        this.posicionParcial = posicionParcial;
    }

    public int getPosicionAcumulada() {
        return posicionAcumulada;
    }

    public void setPosicionAcumulada(int posicionAcumulada) {
        this.posicionAcumulada = posicionAcumulada;
    }
}
