package com.hergomsoft.easyorienteering.data.api.responses;

import com.hergomsoft.easyorienteering.data.model.Carrera;
import com.hergomsoft.easyorienteering.data.model.Participacion;

public class PendienteResponse {
    private Carrera carrera;
    private long idRecorrido;
    private Participacion participacion;

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
