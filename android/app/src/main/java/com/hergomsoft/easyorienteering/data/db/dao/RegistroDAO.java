package com.hergomsoft.easyorienteering.data.db.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.hergomsoft.easyorienteering.data.model.Registro;

import java.util.List;

@Dao
public interface RegistroDAO {

    @Query("DELETE FROM registro")
    void deleteAll();

    @Query("SELECT * FROM registro")
    List<Registro> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void add(Registro[] registros);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void add(Registro registro);

    @Delete
    void delete(Registro registro);

    @Update
    void update(Registro registro);

}
