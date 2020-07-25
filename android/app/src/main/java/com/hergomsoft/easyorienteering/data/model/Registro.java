package com.hergomsoft.easyorienteering.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.hergomsoft.easyorienteering.util.DateConverter;

import java.util.Date;

//@Entity
//@TypeConverters(DateConverter.class)
public class Registro {
    //@PrimaryKey
    private int id;
    private Control control;
    private Recorrido recorrido;
    private Date fecha;

    public Registro(Control control, Recorrido recorrido, Date fecha) {
        this.control = control;
        this.recorrido = recorrido;
        this.fecha = fecha;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Control getControl() {
        return control;
    }

    public void setControl(Control control) {
        this.control = control;
    }

    public Recorrido getRecorrido() {
        return recorrido;
    }

    public void setRecorrido(Recorrido recorrido) {
        this.recorrido = recorrido;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }
}
