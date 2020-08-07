package com.hergomsoft.easyorienteering.data.api.responses;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;

import retrofit2.Response;

/**
 * Generic class for handling responses from Retrofit
 * @param <T>
 */
public class ApiResponse<T> {

    public ApiResponse<T> create(Throwable error){
        String sError ;
        if(error instanceof SocketTimeoutException) {
            sError = "No hay conexión con el servidor. Comprueba tu conexión e inténtalo de nuevo";
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
     * Generic success response from api
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
     * Generic Error response from API
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
     * Separate class for HTTP 204 responses so that we can make ApiSuccessResponse's body non-null.
     */
    public class ApiEmptyResponse<T> extends ApiResponse<T> { }

}