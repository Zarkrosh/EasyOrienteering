package com.hergomsoft.easyorienteering.data.api;

import com.hergomsoft.easyorienteering.data.api.requests.RegistroRequest;
import com.hergomsoft.easyorienteering.data.model.Carrera;
import com.hergomsoft.easyorienteering.data.model.pagers.RegistroControl;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiClient {
    public static final String BASE_URL = "http://192.168.1.69:4200/api/";

    @GET("carreras/{id}")
    Call<Carrera> getCarrera(@Path("id") long idCarrera);

    @POST("registros/{id}")
    Call<RegistroControl> registraControl(@Path("id") long idCarrera, @Body RegistroRequest registro);

}
