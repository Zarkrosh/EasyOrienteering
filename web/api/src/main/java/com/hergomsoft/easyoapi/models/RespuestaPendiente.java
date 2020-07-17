package com.hergomsoft.easyoapi.models;

import java.io.Serializable;

public class RespuestaPendiente implements Serializable {
    private Carrera carrera;
    private long idRecorrido;
    private Registro[] registros;

    public RespuestaPendiente(Carrera carrera, long idRecorrido, Registro[] registros) {
        this.carrera = carrera;
        this.idRecorrido = idRecorrido;
        this.registros = registros;
    }

    public Carrera getCarrera() {
        return carrera;
    }

    public long getIdRecorrido() {
        return idRecorrido;
    }

    public Registro[] getRegistros() {
        return registros;
    }
    
}
