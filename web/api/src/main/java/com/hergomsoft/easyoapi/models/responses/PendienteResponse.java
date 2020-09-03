package com.hergomsoft.easyoapi.models.responses;

import com.hergomsoft.easyoapi.models.Carrera;
import com.hergomsoft.easyoapi.models.Participacion;
import java.io.Serializable;

public class PendienteResponse implements Serializable {
    private final Carrera carrera;
    private final long idRecorrido;
    private final Participacion participacion;

    public PendienteResponse(Carrera carrera, long idRecorrido, Participacion participacion) {
        this.carrera = carrera;
        this.idRecorrido = idRecorrido;
        this.participacion = participacion;
    }

    public Carrera getCarrera() {
        return carrera;
    }

    public long getIdRecorrido() {
        return idRecorrido;
    }

    public Participacion getParticipacion() {
        return participacion;
    }
    
}
