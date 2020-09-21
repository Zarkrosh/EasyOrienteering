package com.hergomsoft.easyorienteering.data.api.requests;

public class CambioPassRequest {
    private final String prevPassword;
    private final String nuevaPassword;

    public CambioPassRequest(String prevPassword, String nuevaPassword) {
        this.prevPassword = prevPassword;
        this.nuevaPassword = nuevaPassword;
    }

    public String getPrevPassword() {
        return prevPassword;
    }

    public String getNuevaPassword() {
        return nuevaPassword;
    }

}
