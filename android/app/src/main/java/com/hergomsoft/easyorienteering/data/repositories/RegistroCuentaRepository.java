package com.hergomsoft.easyorienteering.data.repositories;

import com.hergomsoft.easyorienteering.data.model.Result;

public class RegistroCuentaRepository {
    /*
    private static volatile RegistroCuentaRepository instance;

    private LoginDataSource dataSource;

    // private constructor : singleton access
    private RegistroCuentaRepository(LoginDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public static RegistroCuentaRepository getInstance(LoginDataSource dataSource) {
        if (instance == null) {
            instance = new RegistroCuentaRepository(dataSource);
        }
        return instance;
    }

    public Result<Void> register(String email, String username, String password) {
        // TODO: Registro
        Result<Void> result = dataSource.register(email, username, password);

        if (result instanceof Result.Success) {
            setLoggedInUser(((Result.Success<Usuario>) result).getData());
        }
        return result;
    }
     */
}
