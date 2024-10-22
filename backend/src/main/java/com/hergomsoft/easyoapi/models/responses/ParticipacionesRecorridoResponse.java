package com.hergomsoft.easyoapi.models.responses;

import com.hergomsoft.easyoapi.models.Carrera;
import com.hergomsoft.easyoapi.models.Control;
import com.hergomsoft.easyoapi.models.Participacion;
import com.hergomsoft.easyoapi.models.Recorrido;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParticipacionesRecorridoResponse {
    
    private final Carrera.Modalidad modalidad;
    private final long idCarrera;
    private final Recorrido recorrido;
    private final Map<String, Integer> puntuacionesControles;
    private final List<Participacion> participaciones;

    public ParticipacionesRecorridoResponse(Recorrido recorrido, List<Participacion> participaciones) {
        this.recorrido = recorrido;
        this.participaciones = participaciones;
        this.idCarrera = recorrido.getCarrera().getId();
        this.modalidad = recorrido.getCarrera().getModalidad();
        this.puntuacionesControles = new HashMap<>();
        for(Control c : recorrido.getCarrera().getControles().values()) {
            puntuacionesControles.put(c.getCodigo(), c.getPuntuacion());
        }
    }

    public Carrera.Modalidad getModalidad() {
        return modalidad;
    }

    public long getIdCarrera() {
        return idCarrera;
    }

    public Recorrido getRecorrido() {
        return recorrido;
    }

    public Map<String, Integer> getPuntuacionesControles() {
        return puntuacionesControles;
    }

    public List<Participacion> getParticipaciones() {
        return participaciones;
    }
    
}
