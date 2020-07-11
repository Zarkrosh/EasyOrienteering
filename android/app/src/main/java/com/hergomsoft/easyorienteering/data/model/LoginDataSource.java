package com.hergomsoft.easyorienteering.data.model;

import java.io.IOException;
import java.util.Date;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class LoginDataSource {

    public Result<Usuario> login(String username, String password) {
        try {
            // TODO: handle loggedInUser authentication
            if(username.contentEquals("admin") && password.contentEquals("12341234")) {
                Usuario usuario = new Usuario("Jane Doe", "jane@test.com", new Date());
                return new Result.Success<>(usuario);
            } else {
                return new Result.Error(new IllegalAccessException("No autorizado"));
            }
        } catch (Exception e) {
            return new Result.Error(new IOException("Error logging in", e));
        }
    }

    public Result<Void> register(String email, String username, String password) {
        // TODO: envía petición al servidor de registro
        return new Result.Success<Void>(null);
    }

    public void logout() {
        // TODO: revoke authentication
    }
}
