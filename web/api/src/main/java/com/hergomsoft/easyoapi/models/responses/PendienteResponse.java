package com.hergomsoft.easyoapi.models.responses;

import com.hergomsoft.easyoapi.models.Carrera;
import com.hergomsoft.easyoapi.models.Registro;
import java.io.Serializable;

public class PendienteResponse implements Serializable {
    private Carrera carrera;
    private long idRecorrido;
    private Registro[] registros;

    public PendienteResponse(Carrera carrera, long idRecorrido, Registro[] registros) {
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
