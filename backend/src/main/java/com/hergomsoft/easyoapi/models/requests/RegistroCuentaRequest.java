package com.hergomsoft.easyoapi.models.requests;

import javax.validation.constraints.NotNull;

public class RegistroCuentaRequest {
    @NotNull
    private final String username;
    @NotNull
    private final String email;
    @NotNull
    private final String club;
    @NotNull
    private final String password;

    public RegistroCuentaRequest(String username, String email, String club, String password) {
        this.username = username;
        this.email = email;
        this.club = club;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getClub() {
        return club;
    }

    public String getPassword() {
        return password;
    }
    
}
