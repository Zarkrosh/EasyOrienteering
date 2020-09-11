package com.hergomsoft.easyorienteering.data.api.requests;

import com.google.gson.annotations.Expose;

public class RegistroRequest {

    @Expose
    private String codigo;
    @Expose
    private String secreto;

    public RegistroRequest(String codigo, String secreto) {
        this.codigo = codigo;
        this.secreto = secreto;
    }
}
