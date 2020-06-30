package com.hergomsoft.easyoapi.services;

import com.hergomsoft.easyoapi.models.Control;
import com.hergomsoft.easyoapi.models.Recorrido;
import com.hergomsoft.easyoapi.models.Registro;
import com.hergomsoft.easyoapi.models.Usuario;
import java.util.List;

public interface IRegistroService {

    List<Registro> findAll();
    Registro registraPasoControl(Registro registro); 

}
