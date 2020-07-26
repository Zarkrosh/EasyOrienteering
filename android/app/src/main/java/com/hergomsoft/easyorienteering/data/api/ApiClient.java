package com.hergomsoft.easyorienteering.data.api;

import com.hergomsoft.easyorienteering.data.api.requests.RegistroRequest;
import com.hergomsoft.easyorienteering.data.api.responses.AbandonoResponse;
import com.hergomsoft.easyorienteering.data.api.responses.CarrerasUsuarioResponse;
import com.hergomsoft.easyorienteering.data.api.responses.InicioResponse;
import com.hergomsoft.easyorienteering.data.api.responses.PendienteResponse;
import com.hergomsoft.easyorienteering.data.api.responses.RegistroResponse;
import com.hergomsoft.easyorienteering.data.model.Carrera;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiClient {
    String BASE_URL = "http://192.168.1.69:4200/api/";

    @GET("carreras/{id}")
    Call<Carrera> getCarrera(@Path("id") long idCarrera);

    @GET("carreras")
    Call<CarrerasUsuarioResponse> getCarrerasUsuario();


    @POST("registros/{id}")
    Call<RegistroResponse> registraControl(@Path("id") long idCarrera, @Body RegistroRequest registro);

    @GET("registros/pendiente")
    Call<PendienteResponse> getPendiente();

    @POST("registros/{idCarrera}/iniciar/{idRecorrido}")
    Call<InicioResponse> iniciaRecorrido(@Path("idCarrera") long idCarrera,
                 @Path("idRecorrido") long idRecorrido, @Body RegistroRequest registro);

    @POST("registros/abandonar/{idRecorrido}")
    Call<AbandonoResponse> abandonaRecorrido(@Path("idRecorrido") long idRecorrido);

}
