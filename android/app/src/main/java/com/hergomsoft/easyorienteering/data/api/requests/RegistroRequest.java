package com.hergomsoft.easyorienteering.data.api.requests;

import com.google.gson.annotations.Expose;

public class RegistroRequest {

    @Expose
    private String codigo;

    @Expose
    private String secreto;

    @Expose
    private long idCorredor;

    @Expose
    private long idRecorrido;

    public RegistroRequest(String codigo, String secreto, long idCorredor, long idRecorrido) {
        this.codigo = codigo;
        this.secreto = secreto;
        this.idCorredor = idCorredor;
        this.idRecorrido = idRecorrido;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getSecreto() {
        return secreto;
    }

    public long getIdCorredor() {
        return idCorredor;
    }

    public long getIdRecorrido() {
        return idRecorrido;
    }
}
