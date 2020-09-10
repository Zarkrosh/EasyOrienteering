package com.hergomsoft.easyoapi.models.responses;

import java.util.List;

public class JwtResponse {
    private final String token;
    private final Long id;
    private final String username;
    private final List<String> roles;

    public JwtResponse(String token, Long id, String username, List<String> roles) {
        this.token = token;
        this.id = id;
        this.username = username;
        this.roles = roles;
    }

    public String getToken() {
        return token;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public List<String> getRoles() {
        return roles;
    }
    
}
