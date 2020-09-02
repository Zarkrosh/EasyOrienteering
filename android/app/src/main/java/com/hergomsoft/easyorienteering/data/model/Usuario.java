package com.hergomsoft.easyorienteering.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverter;
import androidx.room.TypeConverters;

import com.google.gson.annotations.Expose;
import com.j256.ormlite.stmt.query.In;

import java.util.Date;

@Entity(tableName = "usuarios")
public class Usuario {
    @PrimaryKey
    @Expose
    private long id;
    @Expose
    private String nombre;
    @Expose
    private String club;
    @Expose
    private Date fechaRegistro;

    private Integer timestamp; // Momento en el que se crea en la BD

    public Usuario(long id, String nombre, String club, Date fechaRegistro) {
        this.id = id;
        this.nombre = nombre;
        this.club = club;
        this.fechaRegistro = fechaRegistro;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getClub() {
        return club;
    }

    public void setClub(String club) {
        this.club = club;
    }

    public Date getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(Date fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public Integer getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Integer timestamp) {
        this.timestamp = timestamp;
    }
}
