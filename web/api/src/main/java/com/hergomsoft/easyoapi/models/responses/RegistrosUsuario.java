package com.hergomsoft.easyoapi.models.responses;

import com.hergomsoft.easyoapi.models.Registro;
import com.hergomsoft.easyoapi.models.Usuario;
import java.util.List;

public class RegistrosUsuario {
    private Usuario usuario;
    private List<Registro> registros;

    public RegistrosUsuario(Usuario usuario, List<Registro> registros) {
        this.usuario = usuario;
        this.registros = registros;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public List<Registro> getRegistros() {
        return registros;
    }
    
}
