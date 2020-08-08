package com.hergomsoft.easyorienteering.data.model;

import com.hergomsoft.easyorienteering.util.Utils;

import java.util.ArrayList;
import java.util.List;

public class ResultadoUsuario {
    public enum Tipo { OK, PENDIENTE, ABANDONADO };

    private int posicion;
    private String nombreClub;
    private long tiempoTotal;
    private long diferenciaGanador;
    private Tipo tipo;
    private List<ParcialUsuario> parciales;

    public ResultadoUsuario(String nombreClub, long tiempoTotal, Tipo tipo) {
        this.nombreClub = nombreClub;
        this.tiempoTotal = tiempoTotal;
        this.tipo = tipo;
    }

    // TEST
    public ResultadoUsuario(int posicion, String nombreClub, long tiempoTotal, long diferenciaGanador, int numeroControles) {
        this.posicion = posicion;
        this.nombreClub = nombreClub;
        this.tiempoTotal = tiempoTotal;
        this.diferenciaGanador = diferenciaGanador;
        this.parciales = new ArrayList<>();
        for(int i = 0; i < numeroControles; i++) {
            long tiempoParcial = Utils.randomInt(0, 10) * 60 + Utils.randomInt(0,59);
            long tiempoAcumulado = Utils.randomInt(0, 10) * 60 + Utils.randomInt(0,59);
            int posParcial = Utils.randomInt(1, 10);
            int posAcumulada = Utils.randomInt(1, 10);
            this.parciales.add(new ParcialUsuario(tiempoParcial, tiempoAcumulado, posParcial, posAcumulada));
        }
    }

    public int getPosicion() {
        return posicion;
    }

    public void setPosicion(int posicion) {
        this.posicion = posicion;
    }

    public String getNombreClub() {
        return nombreClub;
    }

    public void setNombreClub(String nombreClub) {
        this.nombreClub = nombreClub;
    }

    public long getTiempoTotal() {
        return tiempoTotal;
    }

    public void setTiempoTotal(long tiempoTotal) {
        this.tiempoTotal = tiempoTotal;
    }

    public long getDiferenciaGanador() {
        return diferenciaGanador;
    }

    public void setDiferenciaGanador(long diferenciaGanador) {
        this.diferenciaGanador = diferenciaGanador;
    }

    public Tipo getTipo() {
        return tipo;
    }

    public void setTipo(Tipo tipo) {
        this.tipo = tipo;
    }

    public List<ParcialUsuario> getParciales() {
        return parciales;
    }

    public void setParciales(List<ParcialUsuario> parciales) {
        this.parciales = parciales;
    }
}
