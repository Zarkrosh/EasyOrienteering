package com.hergomsoft.easyorienteering.ui.splash;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.hergomsoft.easyorienteering.R;
import com.hergomsoft.easyorienteering.ui.conexion.ConexionActivity;
import com.hergomsoft.easyorienteering.ui.home.HomeActivity;
import com.hergomsoft.easyorienteering.ui.resultados.ResultadosActivity;
import com.hergomsoft.easyorienteering.ui.resumen.ResumenActivity;
import com.hergomsoft.easyorienteering.util.Constants;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_inicial);

        // Comprueba primer inicio de la aplicación
        // TODO

        // Comprueba si hay una cuenta conectada
        Intent intent;
        boolean conectado = false;
        // TODO
        if(conectado) {
            // Si hay una cuenta conectada, muestra la pantalla principal
            intent = null;
            // TODO
            Toast.makeText(this, "[TODO] Conectado. Pantalla principal", Toast.LENGTH_SHORT).show();
        } else {
            // Si no hay cuenta conectada, muestra la pantalla de conexión
            intent = new Intent(this, ConexionActivity.class);
        }

        // Si la aplicación ha cargado demasiado rápido, espera hasta cumplir el tiempo mínimo de visión del logotipo
        // TODO
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Muestra la pantalla de conexión de cuenta
                //startActivity(new Intent(SplashActivity.this, ConexionActivity.class));
                /*
                // Muestra el resumen de la aplicación
                Intent i = new Intent(SplashActivity.this, ResumenActivity.class);
                i.putExtra(ResumenActivity.ARG_VOLUNTARIO, false);
                startActivity(i);
                */

                startActivity(new Intent(SplashActivity.this, HomeActivity.class));
            }
        }, Constants.SPLASH_MIN_TIEMPO_LOGOTIPO);


    }
}
