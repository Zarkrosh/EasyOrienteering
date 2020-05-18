package com.hergomsoft.easyorienteering.data;

import com.hergomsoft.easyorienteering.model.LoginDataSource;
import com.hergomsoft.easyorienteering.model.Result;

public class RegisterRepository {

    private static volatile RegisterRepository instance;

    private LoginDataSource dataSource;

    // private constructor : singleton access
    private RegisterRepository(LoginDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public static RegisterRepository getInstance(LoginDataSource dataSource) {
        if (instance == null) {
            instance = new RegisterRepository(dataSource);
        }
        return instance;
    }

    public Result<Void> register(String email, String username, String password) {
        // TODO: Registro
        Result<Void> result = dataSource.register(email, username, password);
        /*
        if (result instanceof Result.Success) {
            setLoggedInUser(((Result.Success<Usuario>) result).getData());
        }*/
        return result;
    }
}
