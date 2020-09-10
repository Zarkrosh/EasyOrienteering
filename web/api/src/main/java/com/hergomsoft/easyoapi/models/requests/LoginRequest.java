package com.hergomsoft.easyoapi.models.requests;

import javax.validation.constraints.NotNull;

public class LoginRequest {
    @NotNull
    private final String username;
    @NotNull
    private final String password;

    public LoginRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
