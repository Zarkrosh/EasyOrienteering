package com.hergomsoft.easyorienteering.ui.carrera;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.hergomsoft.easyorienteering.R;
import com.hergomsoft.easyorienteering.data.api.requests.RegistroRequest;
import com.hergomsoft.easyorienteering.util.Constants;

public class CarreraActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carrera);

        /*
        // Obtiene el ID del recorrido
        long idRecorrido = getIntent().getLongExtra(Constants.EXTRA_ID_RECORRIDO, -1);
        if(idRecorrido == -1) {
            // El recorrido ya está iniciado
        } else {
            // Está iniciando un nuevo recorrido

        }
        */
    }
}