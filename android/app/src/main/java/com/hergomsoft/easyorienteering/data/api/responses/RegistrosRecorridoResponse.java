package com.hergomsoft.easyorienteering.data.api.responses;

import com.hergomsoft.easyorienteering.data.model.Carrera;
import com.hergomsoft.easyorienteering.data.model.Recorrido;

import java.util.List;
import java.util.Map;

public class RegistrosRecorridoResponse {
    private Carrera.Modalidad modalidad;
    private long idCarrera;
    private Recorrido recorrido;
    private Map<String, Integer> puntuacionesControles;
    private List<RegistrosUsuario> registrosUsuarios;

    public Carrera.Modalidad getModalidad() { return modalidad; }
    public long getIdCarrera() { return idCarrera; }
    public Recorrido getRecorrido() { return recorrido; }
    public Map<String, Integer> getPuntuacionesControles() { return puntuacionesControles; }
    public List<RegistrosUsuario> getRegistrosUsuarios() { return registrosUsuarios; }
}
