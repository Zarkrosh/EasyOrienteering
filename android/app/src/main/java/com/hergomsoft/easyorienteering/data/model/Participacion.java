package com.hergomsoft.easyorienteering.data.model;

import java.util.Date;
import java.util.List;

public class Participacion {

    private Long id;
    private Date fechaInicio;
    private List<Registro> registros;
    private Boolean abandonado;
    private Boolean pendiente;
    private Usuario corredor;
    private Recorrido recorrido;

    //public Participacion() {}


    public Long getId() {
        return id;
    }

    public Date getFechaInicio() {
        return fechaInicio;
    }

    public List<Registro> getRegistros() {
        return registros;
    }

    public Boolean getAbandonado() {
        return abandonado;
    }

    public Boolean getPendiente() {
        return pendiente;
    }

    public Usuario getCorredor() {
        return corredor;
    }

    public Recorrido getRecorrido() {
        return recorrido;
    }
}
