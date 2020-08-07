package com.hergomsoft.easyorienteering.util;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.hergomsoft.easyorienteering.data.api.responses.ApiResponse;

// CacheObject: Type for the Resource data. (database cache)
// RequestObject: Type for the API response. (network request)
public abstract class NetworkBoundResource<CacheObject, RequestObject> {

    private static final String TAG = "NetworkBoundResource";

    private AppExecutors appExecutors;
    private boolean usesDB;
    private MediatorLiveData<Resource<CacheObject>> results = new MediatorLiveData<>();

    public NetworkBoundResource(AppExecutors appExecutors, boolean usesDB) {
        this.appExecutors = appExecutors;
        this.usesDB = usesDB;
        init();
    }

    private void init(){
        // Notifica estado "Cargando"
        results.setValue((Resource<CacheObject>) Resource.loading(null));

        // Obtiene la fuente de LiveData de la DB
        final LiveData<CacheObject> dbSource = loadFromDb();
        results.addSource(dbSource, new Observer<CacheObject>() {
            @Override
            public void onChanged(@Nullable CacheObject cacheObject) {
                results.removeSource(dbSource);

                if(shouldFetch(cacheObject)){
                    // get data from the network
                    fetchFromNetwork(dbSource);
                }
                else{
                    results.addSource(dbSource, new Observer<CacheObject>() {
                        @Override
                        public void onChanged(@Nullable CacheObject cacheObject) {
                            setValue(Resource.success(cacheObject));
                        }
                    });
                }
            }
        });
    }

    /**
     * 1) observe local db
     * 2) if <condition/> query the network
     * 3) stop observing the local db
     * 4) insert new data into local db
     * 5) begin observing local db again to see the refreshed data from network
     * @param dbSource
     */
    private void fetchFromNetwork(final LiveData<CacheObject> dbSource){
        // update LiveData for loading status
        results.addSource(dbSource, new Observer<CacheObject>() {
            @Override
            public void onChanged(@Nullable CacheObject cacheObject) {
                setValue(Resource.loading(cacheObject));
            }
        });

        final LiveData<ApiResponse<RequestObject>> apiResponse = createCall();
        results.addSource(apiResponse, new Observer<ApiResponse<RequestObject>>() {
            @Override
            public void onChanged(@Nullable final ApiResponse<RequestObject> requestObjectApiResponse) {
                results.removeSource(dbSource);
                results.removeSource(apiResponse);

                /* 3 situaciones:
                       1) ApiSuccessResponse
                       2) ApiErrorResponse
                       3) ApiEmptyResponse
                 */
                if(requestObjectApiResponse instanceof ApiResponse.ApiSuccessResponse){
                    appExecutors.diskIO().execute(new Runnable() {
                        @Override
                        public void run() {
                            // Guarda la respuesta en la BD
                            saveCallResult((RequestObject) processResponse((ApiResponse.ApiSuccessResponse) requestObjectApiResponse));
                            appExecutors.mainThread().execute(new Runnable() {
                                @Override
                                public void run() {
                                    if(usesDB) {
                                        // Obtiene el valor desde la BD
                                        results.addSource(loadFromDb(), new Observer<CacheObject>() {
                                            @Override
                                            public void onChanged(@Nullable CacheObject cacheObject) {
                                                setValue(Resource.success(cacheObject));
                                            }
                                        });
                                    } else {
                                        // Devuelve el resultado directamente
                                        setValue(Resource.success(processResponse((ApiResponse.ApiSuccessResponse) requestObjectApiResponse)));
                                    }
                                }
                            });
                        }
                    });
                }
                else if(requestObjectApiResponse instanceof ApiResponse.ApiEmptyResponse){
                    appExecutors.mainThread().execute(new Runnable() {
                        @Override
                        public void run() {
                            // Como es una respuesta vacía, no se guarda ningún valor
                            results.addSource(loadFromDb(), new Observer<CacheObject>() {
                                @Override
                                public void onChanged(@Nullable CacheObject cacheObject) {
                                    setValue(Resource.success(cacheObject));
                                }
                            });
                        }
                    });
                }
                else if(requestObjectApiResponse instanceof ApiResponse.ApiErrorResponse){
                    results.addSource(dbSource, new Observer<CacheObject>() {
                        @Override
                        public void onChanged(@Nullable CacheObject cacheObject) {
                            setValue(
                                    Resource.error(
                                            ((ApiResponse.ApiErrorResponse) requestObjectApiResponse).getErrorMessage(),
                                            cacheObject
                                    )
                            );
                        }
                    });
                }
            }
        });
    }

    private CacheObject processResponse(ApiResponse.ApiSuccessResponse response){
        return (CacheObject) response.getBody();
    }

    private void setValue(Resource<CacheObject> newValue){
        if(results.getValue() != newValue){
            results.setValue(newValue);
        }
    }

    // Called to save the result of the API response into the database.
    @WorkerThread
    protected void saveCallResult(@NonNull RequestObject item) {
        // Por defecto no se usa DB
        // Si se desea usar, se overridea
    }

    // Called with the data in the database to decide whether to fetch
    // potentially updated data from the network.
    @MainThread
    protected boolean shouldFetch(@Nullable CacheObject data) {
        // Por defecto no se usa DB
        return true;
    }

    // Called to get the cached data from the database.
    @NonNull @MainThread
    protected LiveData<CacheObject> loadFromDb() {
        // Por defecto no se usa DB
        return new MutableLiveData<>(null);
    }

    // Called to create the API call.
    @NonNull @MainThread
    protected abstract LiveData<ApiResponse<RequestObject>> createCall();

    // Returns a LiveData object that represents the resource that's implemented
    // in the base class.
    public final LiveData<Resource<CacheObject>> getAsLiveData(){
        return results;
    };
}