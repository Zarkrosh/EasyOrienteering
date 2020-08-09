package com.hergomsoft.easyoapi.models.responses;

import com.hergomsoft.easyoapi.models.Carrera;
import com.hergomsoft.easyoapi.models.Control;
import com.hergomsoft.easyoapi.models.Recorrido;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RegistrosRecorridoResponse {
    
    private final Carrera.Modalidad modalidad;
    private final Recorrido recorrido;
    private final Map<String, Integer> puntuacionesControles;
    private final List<RegistrosUsuario> registrosUsuarios;

    public RegistrosRecorridoResponse(Recorrido recorrido, List<RegistrosUsuario> registrosUsuarios) {
        this.recorrido = recorrido;
        this.registrosUsuarios = registrosUsuarios;
        this.modalidad = recorrido.getCarrera().getModalidad();
        this.puntuacionesControles = new HashMap<>();
        for(Control c : recorrido.getCarrera().getControles().values()) {
            puntuacionesControles.put(c.getCodigo(), c.getPuntuacion());
        }
    }

    public Carrera.Modalidad getModalidad() {
        return modalidad;
    }

    public Recorrido getRecorrido() {
        return recorrido;
    }

    public Map<String, Integer> getPuntuacionesControles() {
        return puntuacionesControles;
    }

    public List<RegistrosUsuario> getRegistrosUsuarios() {
        return registrosUsuarios;
    }
    
}
