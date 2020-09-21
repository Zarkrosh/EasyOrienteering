package com.hergomsoft.easyorienteering.data.model;

import java.util.Date;
import java.util.List;

public class Participacion {

    private Long id;
    private Date fechaInicio;
    private List<Registro> registros;
    private boolean abandonado;
    private boolean pendiente;
    private Usuario corredor;
    private Recorrido recorrido;

    public Long getId() {
        return id;
    }
    public Date getFechaInicio() {
        return fechaInicio;
    }
    public List<Registro> getRegistros() {
        return registros;
    }
    public Boolean isAbandonado() {
        return abandonado;
    }
    public Boolean isPendiente() {
        return pendiente;
    }
    public Usuario getCorredor() { return corredor; }
    public Recorrido getRecorrido() { return recorrido; }
}
