package com.hergomsoft.easyorienteering.data.persistence;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.hergomsoft.easyorienteering.data.model.Carrera;

@Dao
public interface CarreraDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertCarrera(Carrera carrera);

    @Query("SELECT * FROM carreras WHERE id = :id")
    LiveData<Carrera> getCarrera(long id);

    @Query("DELETE FROM carreras")
    void clearAll();

}
