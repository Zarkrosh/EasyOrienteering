package com.hergomsoft.easyoapi.models.requests;

import javax.validation.constraints.NotNull;

public class CambioRequest {
    
    @NotNull
    private String cambio;

    public CambioRequest() {
    }

    public CambioRequest(String cambio) {
        this.cambio = cambio;
    }

    public String getCambio() {
        return cambio;
    }

    public void setCambio(String cambio) {
        this.cambio = cambio;
    }

}
