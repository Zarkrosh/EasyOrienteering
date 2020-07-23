package com.hergomsoft.easyorienteering.data.api.responses;

import com.hergomsoft.easyorienteering.data.model.Carrera;
import com.hergomsoft.easyorienteering.data.model.Registro;

public class PendienteResponse {
    private Carrera carrera;
    private long idRecorrido;
    private Registro[] registros;

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
