package com.hergomsoft.easyorienteering.data.api.responses;

import com.hergomsoft.easyorienteering.data.model.Carrera;
import com.hergomsoft.easyorienteering.data.model.Recorrido;
import com.hergomsoft.easyorienteering.data.model.Registro;

public class InicioResponse {
    private Registro registro;
    private Carrera carrera;
    private Recorrido recorrido;

    public Registro getRegistro() {
        return registro;
    }
    public Carrera getCarrera() {
        return carrera;
    }
    public Recorrido getRecorrido() {
        return recorrido;
    }
}
