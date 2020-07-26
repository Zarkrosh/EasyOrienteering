package com.hergomsoft.easyorienteering.data.api.responses;

import com.hergomsoft.easyorienteering.data.model.Carrera;
import com.hergomsoft.easyorienteering.data.model.Recorrido;

public class InicioResponse {
    private RegistroResponse registro;
    private Carrera carrera;
    private Recorrido recorrido;

    public RegistroResponse getRegistro() {
        return registro;
    }

    public Carrera getCarrera() {
        return carrera;
    }

    public Recorrido getRecorrido() {
        return recorrido;
    }
}
