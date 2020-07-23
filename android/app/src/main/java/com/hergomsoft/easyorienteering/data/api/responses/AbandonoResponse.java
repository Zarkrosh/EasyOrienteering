package com.hergomsoft.easyorienteering.data.api.responses;

import com.hergomsoft.easyorienteering.data.model.Carrera;
import com.hergomsoft.easyorienteering.data.model.Registro;

public class AbandonoResponse {
    private boolean abandonado;
    private String error;

    public boolean isAbandonado() {
        return abandonado;
    }

    public String getError() {
        return error;
    }
}
