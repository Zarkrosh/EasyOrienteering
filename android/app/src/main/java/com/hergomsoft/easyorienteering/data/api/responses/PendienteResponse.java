package com.hergomsoft.easyorienteering.data.api.responses;

import com.hergomsoft.easyorienteering.data.model.Carrera;

public class PendienteResponse {
    private Carrera carrera;
    private long idRecorrido;
    private RegistroResponse[] registros;

    public Carrera getCarrera() {
        return carrera;
    }

    public long getIdRecorrido() {
        return idRecorrido;
    }

    public RegistroResponse[] getRegistros() {
        return registros;
    }
}
