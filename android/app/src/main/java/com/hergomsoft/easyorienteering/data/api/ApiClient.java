package com.hergomsoft.easyorienteering.data.api;

import androidx.lifecycle.LiveData;

import com.hergomsoft.easyorienteering.data.api.requests.CambioPassRequest;
import com.hergomsoft.easyorienteering.data.api.requests.CambioRequest;
import com.hergomsoft.easyorienteering.data.api.requests.LoginRequest;
import com.hergomsoft.easyorienteering.data.api.requests.RegistroCuentaRequest;
import com.hergomsoft.easyorienteering.data.api.requests.RegistroRequest;
import com.hergomsoft.easyorienteering.data.api.responses.AbandonoResponse;
import com.hergomsoft.easyorienteering.data.api.responses.ApiResponse;
import com.hergomsoft.easyorienteering.data.api.responses.InicioResponse;
import com.hergomsoft.easyorienteering.data.api.responses.LoginResponse;
import com.hergomsoft.easyorienteering.data.api.responses.MessageResponse;
import com.hergomsoft.easyorienteering.data.api.responses.PendienteResponse;
import com.hergomsoft.easyorienteering.data.api.responses.ParticipacionesRecorridoResponse;
import com.hergomsoft.easyorienteering.data.model.Carrera;
import com.hergomsoft.easyorienteering.data.model.Registro;
import com.hergomsoft.easyorienteering.data.model.Usuario;
import com.hergomsoft.easyorienteering.util.Constants;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiClient {
    String BASE_URL = "https://192.168.1.69:4200/api/";
    String MAPA_URL = "recorridos/mapa/"; // + idRecorrido

    // CARRERAS
    @GET("carreras/{id}")
    LiveData<ApiResponse<Carrera>> getCarrera(@Path("id") long idCarrera);
    @GET("carreras/participadas")
    LiveData<ApiResponse<List<Carrera>>> getCarrerasParticipadasUsuario();
    @GET("carreras/organizadas")
    LiveData<ApiResponse<List<Carrera>>> getCarrerasOrganizadasUsuario();
    @GET("carreras/buscar")
    LiveData<ApiResponse<List<Carrera>>> buscaCarreras(@Query("nombre") String nombre, @Query("tipo") String tipo, @Query("modalidad") String modalidad, @Query("page") int numeroPagina);

    // RECORRIDOS
    @GET("recorridos/{idRecorrido}")
    LiveData<ApiResponse<ParticipacionesRecorridoResponse>> getRegistrosRecorrido(@Path("idRecorrido") long idRecorrido);
    @POST("recorridos/{id}")
    Call<Registro> registraControl(@Path("id") long idCarrera, @Body RegistroRequest registro);
    @GET("recorridos/pendiente")
    Call<PendienteResponse> getPendiente();
    @POST("recorridos/iniciar/{idRecorrido}")
    Call<InicioResponse> iniciaRecorrido(@Path("idRecorrido") long idRecorrido, @Body RegistroRequest registro);
    @POST("recorridos/abandonar/{idRecorrido}")
    Call<AbandonoResponse> abandonaRecorrido(@Path("idRecorrido") long idRecorrido);

    // AUTENTICACIÓN
    @POST("auth/login")
    Call<LoginResponse> loginUsuario(@Body LoginRequest request);
    @POST("auth/register")
    Call<MessageResponse> registerUsuario(@Body RegistroCuentaRequest request);
    @POST("auth/logout")
    Call<MessageResponse> logoutUsuario();
    @POST("auth/change")
    Call<MessageResponse> cambiaPassword(@Body CambioPassRequest registro);

    // USUARIOS
    @GET("usuarios/{id}")
    LiveData<ApiResponse<Usuario>> getUsuario(@Path("id") long id);
    @POST("usuarios/cambionombre")
    Call<Usuario> cambiaNombreUsuario(@Body CambioRequest registro);
    @POST("usuarios/cambioclub")
    Call<Usuario> cambiaClub(@Body CambioRequest registro);
    @DELETE("usuarios")
    Call<MessageResponse> borrarCuenta();

}
