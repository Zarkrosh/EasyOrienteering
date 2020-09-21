package com.hergomsoft.easyorienteering.data.persistence;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.hergomsoft.easyorienteering.data.model.Usuario;

@Dao
public interface UsuarioDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertUsuario(Usuario usuario);

    @Query("SELECT * FROM usuarios WHERE id = :id")
    LiveData<Usuario> getUsuario(long id);

}
