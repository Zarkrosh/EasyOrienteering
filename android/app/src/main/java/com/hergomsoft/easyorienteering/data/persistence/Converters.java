package com.hergomsoft.easyorienteering.data.persistence;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hergomsoft.easyorienteering.data.model.Carrera;
import com.hergomsoft.easyorienteering.data.model.Recorrido;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.List;

public class Converters {
    @TypeConverter
    public static Date toDate(Long value) {
        return value == null ? null : new Date(value);
    }
    @TypeConverter
    public static Long fromDate(Date date) {
        return date == null ? null : date.getTime();
    }

    @TypeConverter
    public static String[] toStringArray(String value) {
        Type type = new TypeToken<List<String>>(){}.getType();
        return new Gson().fromJson(value, type);
    }
    @TypeConverter
    public static String fromStringArray(String[] array) {
        return new Gson().toJson(array);
    }

    // TIPO DE CARRERA
    @TypeConverter
    public static String fromTipo(Carrera.Tipo tipo) { return tipo.name(); }

    @TypeConverter
    public static Carrera.Tipo toTipo(String valor) {
        if(valor.contentEquals(Carrera.Tipo.CIRCUITO.name())) {
            return Carrera.Tipo.CIRCUITO;
        } else if(valor.contentEquals(Carrera.Tipo.EVENTO.name())) {
            return Carrera.Tipo.EVENTO;
        }

        return null;
    }

    // MODALIDAD DE CARRERA
    @TypeConverter
    public static String fromModalidad(Carrera.Modalidad modalidad) { return modalidad.name(); }

    @TypeConverter
    public static Carrera.Modalidad toModalidad(String valor) {
        if(valor.contentEquals(Carrera.Modalidad.TRAZADO.name())) {
            return Carrera.Modalidad.TRAZADO;
        } else if(valor.contentEquals(Carrera.Modalidad.SCORE.name())) {
            return Carrera.Modalidad.SCORE;
        }

        return null;
    }

    // LISTA DE RECORRIDOS
    @TypeConverter
    public static List<Recorrido> toLista(String value) {
        Type type = new TypeToken<List<Recorrido>>(){}.getType();
        return new Gson().fromJson(value, type);
    }
    @TypeConverter
    public static String fromLista(List<Recorrido> recorridos) {
        Type type = new TypeToken<List<Recorrido>>() {}.getType();
        return new Gson().toJson(recorridos, type);
    }
}