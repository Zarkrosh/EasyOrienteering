package com.hergomsoft.easyorienteering.data.api.responses;

import java.util.List;

public class LoginResponse {
    private final String token;
    private final Long id;
    private final List<String> roles;

    public LoginResponse(String token, Long id, List<String> roles) {
        this.token = token;
        this.id = id;
        this.roles = roles;
    }

    public String getToken() {
        return token;
    }

    public Long getId() {
        return id;
    }

    public List<String> getRoles() {
        return roles;
    }

}