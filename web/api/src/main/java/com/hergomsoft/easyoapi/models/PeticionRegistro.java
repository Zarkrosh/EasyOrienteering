package com.hergomsoft.easyoapi.models;

import javax.validation.constraints.NotNull;

public class PeticionRegistro {
    @NotNull
    private String codigo; // Código del control
    @NotNull
    private String secreto; // Secreto del control

    public PeticionRegistro(String codigoControl, String secretControl) {
        this.codigo = codigoControl;
        this.secreto = secretControl;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getSecreto() {
        return secreto;
    }

    public void setSecreto(String secreto) {
        this.secreto = secreto;
    }
    
}
