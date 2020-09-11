package com.hergomsoft.easyorienteering.data.api.responses;

import com.google.gson.JsonSyntaxException;

import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketTimeoutException;

import retrofit2.Response;

/**
 * Generic class for handling responses from Retrofit.
 * @param <T>
 */
public class ApiResponse<T> {

    public ApiResponse<T> create(Throwable error){
        String sError ;
        if(error instanceof ConnectException) {
            sError = "No hay conexión con el servidor. Comprueba tu conexión e inténtalo de nuevo";
        } else if(error instanceof SocketTimeoutException) {
            sError = "Tiempo de espera agotado. Inténtalo de nuevo";
        } else if(error instanceof JsonSyntaxException) {
            sError = "Ocurrió un error al leer la respuesta del servidor";
        } else if(!error.getMessage().isEmpty()) {
            sError = error.getMessage();
        } else {
            sError = "Error desconocido de red";
        }

        return new ApiErrorResponse<>(sError);
    }

    public ApiResponse<T> create(Response<T> response){

        if(response.isSuccessful()){
            T body = response.body();

            if(body == null || response.code() == 204) { // 204 is empty response
                return new ApiEmptyResponse<>();
            }
            else{
                return new ApiSuccessResponse<>(body);
            }
        }
        else{
            String errorMsg = "";
            try {
                errorMsg = response.errorBody().string();
            } catch (IOException e) {
                e.printStackTrace();
                errorMsg = response.message();
            }
            return new ApiErrorResponse<>(errorMsg);
        }
    }

    /**
     * Respuesta genérica exitosa de la API.
     * @param <T>
     */
    public class ApiSuccessResponse<T> extends ApiResponse<T> {

        private T body;

        ApiSuccessResponse(T body) {
            this.body = body;
        }

        public T getBody() {
            return body;
        }

    }

    /**
     * Respuesta genérica errónea de la API.
     * Generic Error response from API.
     * @param <T>
     */
    public class ApiErrorResponse<T> extends ApiResponse<T> {

        private String errorMessage;

        ApiErrorResponse(String errorMessage) {
            this.errorMessage = errorMessage;
        }

        public String getErrorMessage() {
            return errorMessage;
        }

    }

    /**
     * Respuesta exitosa vacía de la API (código HTTP 204).
     */
    public class ApiEmptyResponse<T> extends ApiResponse<T> { }

}