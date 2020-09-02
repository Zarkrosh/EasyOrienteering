package com.hergomsoft.easyoapi.utils;

/**
 * Excepción personalizada para representar un mensaje.
 */
public class MessageException extends Exception {
    
    public MessageException(String errorMessage) {
        super(errorMessage);
    }
    
}
