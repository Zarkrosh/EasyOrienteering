package com.hergomsoft.easyorienteering.data.model;

import com.google.gson.annotations.Expose;
import com.hergomsoft.easyorienteering.data.model.Control;
import com.hergomsoft.easyorienteering.data.model.Recorrido;
import com.hergomsoft.easyorienteering.data.model.Usuario;

public class RegistroControl {

    @Expose
    private long id;
    @Expose
    private Usuario corredor;
    @Expose
    private Control control;
    @Expose
    private Recorrido recorrido;

    public long getId() {
        return id;
    }

    public Usuario getCorredor() {
        return corredor;
    }

    public Control getControl() {
        return control;
    }

    public Recorrido getRecorrido() {
        return recorrido;
    }
}
