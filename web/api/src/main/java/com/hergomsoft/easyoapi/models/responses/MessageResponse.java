package com.hergomsoft.easyoapi.models.responses;

public class MessageResponse {
    private final String message;
    private final boolean error;

    public MessageResponse(String message, boolean error) {
        this.message = message;
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public boolean isError() {
        return error;
    }
    
}
