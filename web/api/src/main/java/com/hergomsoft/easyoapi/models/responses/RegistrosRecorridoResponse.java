package com.hergomsoft.easyoapi.models.responses;

import com.hergomsoft.easyoapi.models.Recorrido;
import java.util.List;

public class RegistrosRecorridoResponse {
    
    private final Recorrido recorrido;
    private final List<RegistrosUsuario> registrosUsuarios;

    public RegistrosRecorridoResponse(Recorrido recorrido, List<RegistrosUsuario> registrosUsuarios) {
        this.recorrido = recorrido;
        this.registrosUsuarios = registrosUsuarios;
    }

    public Recorrido getRecorrido() {
        return recorrido;
    }

    public List<RegistrosUsuario> getRegistrosUsuarios() {
        return registrosUsuarios;
    }
    
}
