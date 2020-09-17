package com.hergomsoft.easyoapi.models.requests;

import javax.validation.constraints.NotNull;

public class CambioPassRequest {
    @NotNull
    private final String prevPassword;
    @NotNull
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
