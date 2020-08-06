package com.hergomsoft.easyorienteering.data.api.responses;

import com.hergomsoft.easyorienteering.data.model.Carrera;

import java.util.List;

public class CarrerasUsuarioResponse {

    private List<Carrera> participadas;
    private List<Carrera> organizadas;

    public List<Carrera> getParticipadas() { return participadas; }
    public List<Carrera> getOrganizadas() {
        return organizadas;
    }
}
