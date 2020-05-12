package com.hergomsoft.easyorienteering.ui.inicial;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.hergomsoft.easyorienteering.R;
import com.hergomsoft.easyorienteering.ui.conexion.ConexionActivity;

public class InicialActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicial);

        // Comprueba primer inicio de la aplicación
        // TODO

        // Comprueba si hay una cuenta conectada
        boolean conectado = false;
        // TODO
        if(conectado) {
            // Si hay una cuenta conectada, muestra la pantalla principal
            // TODO
            Toast.makeText(this, "[TODO] Conectado. Pantalla principal", Toast.LENGTH_SHORT).show();
        } else {
            // Si no hay cuenta conectada, muestra la pantalla de conexión
            startActivity(new Intent(this, ConexionActivity.class));
        }

    }
}
