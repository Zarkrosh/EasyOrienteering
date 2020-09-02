package com.hergomsoft.easyoapi.models.requests;

public class UbicacionRequest {

    private final Float latitud;
    private final Float longitud;

    public UbicacionRequest(Float latitud, Float longitud) {
        this.latitud = latitud;
        this.longitud = longitud;
    }

    public Float getLatitud() {
        return latitud;
    }

    public Float getLongitud() {
        return longitud;
    }
}
