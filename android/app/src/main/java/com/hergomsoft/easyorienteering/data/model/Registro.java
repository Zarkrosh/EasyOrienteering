package com.hergomsoft.easyorienteering.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.util.Date;


public class Registro {
    private String control;
    private Date fecha;

    public Registro(String control, Recorrido recorrido, Date fecha) {
        this.control = control;
        this.fecha = fecha;
    }

    public String getControl() {
        return control;
    }

    public void setControl(String control) {
        this.control = control;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }
}
