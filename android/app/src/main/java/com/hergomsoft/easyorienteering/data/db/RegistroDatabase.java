package com.hergomsoft.easyorienteering.data.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.hergomsoft.easyorienteering.data.db.dao.RegistroDAO;
import com.hergomsoft.easyorienteering.data.model.Registro;

@Database(entities = {Registro.class}, version = 1)
public abstract class RegistroDatabase extends RoomDatabase {
    private static final String NOMBRE_DB = "registro_database";

    public abstract RegistroDAO getRegistroDAO();

    // Singleton
    private static RegistroDatabase instance;
    public static RegistroDatabase getDatabase(final Context context) {
        if(instance == null) {
            synchronized (RegistroDatabase.class) {
                if(instance == null) {
                    instance = Room.databaseBuilder(context.getApplicationContext(),
                            RegistroDatabase.class, NOMBRE_DB)
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }

        return instance;
    }
}
