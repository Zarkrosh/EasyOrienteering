package com.hergomsoft.easyorienteering.data.api.responses;

import com.hergomsoft.easyorienteering.data.model.Usuario;

import java.util.List;

public class RegistrosUsuario {
    private Usuario usuario;
    private List<RegistroResponse> registros;

    public Usuario getUsuario() { return usuario; }
    public List<RegistroResponse> getRegistros() { return registros; }
}