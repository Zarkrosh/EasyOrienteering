package com.hergomsoft.easyorienteering.data.api;

import androidx.lifecycle.LiveData;

import com.hergomsoft.easyorienteering.data.api.requests.CambioRequest;
import com.hergomsoft.easyorienteering.data.api.requests.RegistroRequest;
import com.hergomsoft.easyorienteering.data.api.responses.AbandonoResponse;
import com.hergomsoft.easyorienteering.data.api.responses.ApiResponse;
import com.hergomsoft.easyorienteering.data.api.responses.InicioResponse;
import com.hergomsoft.easyorienteering.data.api.responses.PendienteResponse;
import com.hergomsoft.easyorienteering.data.api.responses.RegistroResponse;
import com.hergomsoft.easyorienteering.data.api.responses.RegistrosRecorridoResponse;
import com.hergomsoft.easyorienteering.data.model.Carrera;
import com.hergomsoft.easyorienteering.data.model.Usuario;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiClient {
    String BASE_URL = "http://192.168.1.69:4200/api/";

    // CARRERAS
    @GET("carreras/{id}")
    LiveData<ApiResponse<Carrera>> getCarrera(@Path("id") long idCarrera);
    @GET("carreras/participadas")
    LiveData<ApiResponse<List<Carrera>>> getCarrerasParticipadasUsuario();
    @GET("carreras/organizadas")
    LiveData<ApiResponse<List<Carrera>>> getCarrerasOrganizadasUsuario();

    // REGISTROS
    @GET("registros/{idRecorrido}")
    LiveData<ApiResponse<RegistrosRecorridoResponse>> getRegistrosRecorrido(@Path("idRecorrido") long idRecorrido);
    @POST("registros/{id}")
    Call<RegistroResponse> registraControl(@Path("id") long idCarrera, @Body RegistroRequest registro);
    @GET("registros/pendiente")
    Call<PendienteResponse> getPendiente();
    @POST("registros/{idCarrera}/iniciar/{idRecorrido}")
    Call<InicioResponse> iniciaRecorrido(@Path("idCarrera") long idCarrera,
                 @Path("idRecorrido") long idRecorrido, @Body RegistroRequest registro);
    @POST("registros/abandonar/{idRecorrido}")
    Call<AbandonoResponse> abandonaRecorrido(@Path("idRecorrido") long idRecorrido);

    // USUARIOS
    @GET("usuarios/{id}")
    LiveData<ApiResponse<Usuario>> getUsuario(@Path("id") long id);
    @POST("usuarios/cambionombre")
    Call<Usuario> cambiaNombreUsuario(@Body CambioRequest registro);
    @POST("usuarios/cambioclub")
    Call<Usuario> cambiaClub(@Body CambioRequest registro);


}
