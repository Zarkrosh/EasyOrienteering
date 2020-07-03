package com.hergomsoft.easyoapi.models;

import javax.validation.constraints.NotNull;

public class PeticionRegistro {
    @NotNull
    private String codigo; // Código del control
    @NotNull
    private String secreto; // Secreto del control
    
    private Long idRecorrido;

    public PeticionRegistro(String codigo, String secreto, Long idRecorrido) {
        this.codigo = codigo;
        this.secreto = secreto;
        this.idRecorrido = idRecorrido;
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

    public Long getIdRecorrido() {
        return idRecorrido;
    }

    public void setIdRecorrido(Long idRecorrido) {
        this.idRecorrido = idRecorrido;
    }
}
