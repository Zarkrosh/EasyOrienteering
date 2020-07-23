package com.hergomsoft.easyoapi.models.responses;

public class AbandonoResponse {
    private boolean abandonado;
    private String error;

    public AbandonoResponse() {
        abandonado = false;
        error = "";
    }

    public AbandonoResponse(boolean abandonado, String error) {
        this.abandonado = abandonado;
        this.error = error;
    }

    public boolean isAbandonado() {
        return abandonado;
    }

    public void setAbandonado(boolean abandonado) {
        this.abandonado = abandonado;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
    
}
