package com.hergomsoft.easyorienteering.data.api.responses;

import com.hergomsoft.easyorienteering.data.model.Recorrido;

import java.util.List;

public class RegistrosRecorridoResponse {

    private Recorrido recorrido;
    private List<RegistrosUsuario> registrosUsuarios;

    public Recorrido getRecorrido() { return recorrido; }
    public List<RegistrosUsuario> getRegistrosUsuarios() { return registrosUsuarios; }
}
