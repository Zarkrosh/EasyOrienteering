package com.hergomsoft.easyoapi.models.responses;

import com.hergomsoft.easyoapi.models.Carrera;
import java.util.List;

public class CarrerasUsuarioResponse {
    private List<Carrera> corridas;
    private List<Carrera> organizadas;

    public CarrerasUsuarioResponse(List<Carrera> corridas, List<Carrera> organizadas) {
        this.corridas = corridas;
        this.organizadas = organizadas;
    }

    public List<Carrera> getCorridas() {
        return corridas;
    }

    public List<Carrera> getOrganizadas() {
        return organizadas;
    }

}
