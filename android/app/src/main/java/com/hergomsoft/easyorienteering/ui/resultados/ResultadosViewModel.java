package com.hergomsoft.easyorienteering.ui.resultados;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.hergomsoft.easyorienteering.data.api.responses.ParticipacionesRecorridoResponse;
import com.hergomsoft.easyorienteering.data.repositories.RegistroRepository;
import com.hergomsoft.easyorienteering.util.AndroidViewModelConCarga;
import com.hergomsoft.easyorienteering.util.Resource;

public class ResultadosViewModel extends AndroidViewModelConCarga {

    RegistroRepository registroRepository;

    public ResultadosViewModel(Application app) {
        super(app);
        registroRepository = RegistroRepository.getInstance(app);
    }


    public LiveData<Resource<ParticipacionesRecorridoResponse>> getResultados(long idRecorrido) {
        return registroRepository.getRegistrosRecorrido(idRecorrido);
    }
}
