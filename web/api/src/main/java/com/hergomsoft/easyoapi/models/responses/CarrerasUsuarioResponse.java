package com.hergomsoft.easyoapi.models.responses;

import com.hergomsoft.easyoapi.models.Carrera;
import java.util.List;

public class CarrerasUsuarioResponse {
    private final List<Carrera> participadas;
    private final List<Carrera> organizadas;

    public CarrerasUsuarioResponse(List<Carrera> participadas, List<Carrera> organizadas) {
        this.participadas = participadas;
        this.organizadas = organizadas;
    }

    public List<Carrera> getParticipadas() {
        return participadas;
    }

    public List<Carrera> getOrganizadas() {
        return organizadas;
    }

}
