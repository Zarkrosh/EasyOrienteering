package com.hergomsoft.easyorienteering.data.persistence;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.hergomsoft.easyorienteering.data.model.Carrera;

import java.util.List;

@Dao
public interface CarreraDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertCarreras(List<Carrera> carrera);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertCarrera(Carrera carrera);

    @Query("SELECT * FROM carreras WHERE id = :id")
    LiveData<Carrera> getCarrera(long id);

    @Query("SELECT * FROM carreras WHERE UPPER(nombre) LIKE '%' || :nombre || '%'  AND tipo LIKE '%' || :tipo || '%' AND modalidad LIKE '%' || :modalidad || '%' ORDER BY id DESC LIMIT ((:numeroPagina + 1) * 20)")
    LiveData<List<Carrera>> buscaCarreras(String nombre, String tipo, String modalidad, int numeroPagina);

    @Query("DELETE FROM carreras")
    void clearAll();
}
