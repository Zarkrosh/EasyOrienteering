package com.hergomsoft.easyoapi.models.responses;

import com.hergomsoft.easyoapi.models.Carrera;
import com.hergomsoft.easyoapi.models.Recorrido;
import com.hergomsoft.easyoapi.models.Registro;

public class InicioResponse {
    private Registro registro;
    private Carrera carrera;
    private Recorrido recorrido;

    public InicioResponse(Registro registro, Carrera carrera, Recorrido recorrido) {
        this.registro = registro;
        this.carrera = carrera;
        this.recorrido = recorrido;
    }

    public Registro getRegistro() {
        return registro;
    }

    public void setRegistro(Registro registro) {
        this.registro = registro;
    }

    public Carrera getCarrera() {
        return carrera;
    }

    public void setCarrera(Carrera carrera) {
        this.carrera = carrera;
    }

    public Recorrido getRecorrido() {
        return recorrido;
    }

    public void setRecorrido(Recorrido recorrido) {
        this.recorrido = recorrido;
    }
    
    
}
