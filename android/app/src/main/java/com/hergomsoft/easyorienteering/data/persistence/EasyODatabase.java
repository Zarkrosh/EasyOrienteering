package com.hergomsoft.easyorienteering.data.persistence;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.hergomsoft.easyorienteering.data.model.Usuario;

@Database(entities = {Usuario.class}, version = 1)
@TypeConverters({Converters.class})
public abstract class EasyODatabase extends RoomDatabase {

    public static final String DATABASE_NAME = "easyo_db";

    // Singleton
    private static EasyODatabase instance;
    public static EasyODatabase getInstance(Context context) {
        if(instance == null) {
            instance = Room.databaseBuilder(
                        context.getApplicationContext(),
                        EasyODatabase.class,
                        DATABASE_NAME
            ).build();
        }

        return instance;
    }

    public abstract UsuarioDAO getUsuarioDAO();

}
