package com.hergomsoft.easyorienteering.data.repositories;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.hergomsoft.easyorienteering.data.api.responses.ApiResponse;
import com.hergomsoft.easyorienteering.data.model.Carrera;
import com.hergomsoft.easyorienteering.data.model.Recurso;
import com.hergomsoft.easyorienteering.data.model.Usuario;
import com.hergomsoft.easyorienteering.data.persistence.CarreraDAO;
import com.hergomsoft.easyorienteering.data.persistence.EasyODatabase;
import com.hergomsoft.easyorienteering.util.AppExecutors;
import com.hergomsoft.easyorienteering.util.Constants;
import com.hergomsoft.easyorienteering.util.NetworkBoundResource;
import com.hergomsoft.easyorienteering.util.Resource;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CarreraRepository extends ApiRepository {

    CarreraDAO carreraDAO;

    // Singleton
    private static CarreraRepository instance;
    public static CarreraRepository getInstance(Context context) {
        if(instance == null) {
            instance = new CarreraRepository(context);
        }

        return instance;
    }

    private CarreraRepository(Context context) {
        carreraDAO = EasyODatabase.getInstance(context).getCarreraDAO();
    }

    public LiveData<Resource<Carrera>> getCarrera(long id) {
        return new NetworkBoundResource<Carrera, Carrera>(AppExecutors.getInstance(), true) {
            @Override
            protected void saveCallResult(@NonNull Carrera item) {
                item.setTimestamp((int)(System.currentTimeMillis() / 1000));
                carreraDAO.insertCarrera(item);
            }

            @Override
            protected boolean shouldFetch(@Nullable Carrera data) {
                int currentTimeSecs = (int) (System.currentTimeMillis() / 1000);
                if(data == null || data.getTimestamp() == null) return true;
                return currentTimeSecs - data.getTimestamp() >= Constants.REFRESH_CARRERA_TIME;
            }

            @NonNull
            @Override
            protected LiveData<Carrera> loadFromDb() {
                return carreraDAO.getCarrera(id);
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<Carrera>> createCall() {
                return apiClient.getCarrera(id);
            }
        }.getAsLiveData();
    }

    public LiveData<Resource<List<Carrera>>> getCarrerasParticipadasUsuario() {
        return new NetworkBoundResource<List<Carrera>, List<Carrera>>(AppExecutors.getInstance(), false) {
            @NonNull
            @Override
            protected LiveData<ApiResponse<List<Carrera>>> createCall() {
                return apiClient.getCarrerasParticipadasUsuario();
            }
        }.getAsLiveData();
    }

    public LiveData<Resource<List<Carrera>>> getCarrerasOrganizadasUsuario() {
        return new NetworkBoundResource<List<Carrera>, List<Carrera>>(AppExecutors.getInstance(), false) {
            @NonNull
            @Override
            protected LiveData<List<Carrera>> loadFromDb() {
                return new MutableLiveData<>(new ArrayList<>()); // No se usa DB
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<List<Carrera>>> createCall() {
                return apiClient.getCarrerasOrganizadasUsuario();
            }
        }.getAsLiveData();
    }
}
