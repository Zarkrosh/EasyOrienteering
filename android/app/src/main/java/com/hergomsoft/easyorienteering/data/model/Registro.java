package com.hergomsoft.easyorienteering.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.hergomsoft.easyorienteering.util.DateConverter;

import java.util.Date;

@Entity
@TypeConverters(DateConverter.class)
public class Registro {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String control;
    private int recorrido;
    private Date fecha;

    public Registro(int recorrido, String control, Date fecha) {
        this.recorrido = recorrido;
        this.control = control;
        this.fecha = fecha;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRecorrido() {
        return recorrido;
    }

    public void setRecorrido(int recorrido) {
        this.recorrido = recorrido;
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
