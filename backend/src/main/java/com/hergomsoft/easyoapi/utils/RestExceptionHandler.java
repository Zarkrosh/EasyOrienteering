package com.hergomsoft.easyoapi.utils;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {
    
   @ExceptionHandler(ResponseStatusException.class)
   protected ResponseEntity<Object> handleEntityNotFound(ResponseStatusException ex, WebRequest request) {
        String razon = ex.getReason();
        return handleExceptionInternal(ex, razon, new HttpHeaders(), ex.getStatus(), request);
   }
   
}
