package com.hergomsoft.easyorienteering.data.api.requests;

import com.google.gson.annotations.Expose;

public class CambioRequest {

    @Expose
    private String cambio;

    public CambioRequest(String cambio) {
        this.cambio = cambio;
    }

    public String getCambio() {
        return cambio;
    }
}
