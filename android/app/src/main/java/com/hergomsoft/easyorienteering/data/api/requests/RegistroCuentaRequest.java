package com.hergomsoft.easyorienteering.data.api.requests;

public class RegistroCuentaRequest {
    private final String username;
    private final String email;
    private final String club;
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
