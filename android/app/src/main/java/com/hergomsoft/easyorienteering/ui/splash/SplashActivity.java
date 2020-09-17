package com.hergomsoft.easyorienteering.ui.splash;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.security.ProviderInstaller;
import com.hergomsoft.easyorienteering.R;
import com.hergomsoft.easyorienteering.data.repositories.UsuarioRepository;
import com.hergomsoft.easyorienteering.ui.conexion.ConexionActivity;
import com.hergomsoft.easyorienteering.ui.home.HomeActivity;
import com.hergomsoft.easyorienteering.ui.resumen.ResumenActivity;
import com.hergomsoft.easyorienteering.util.Constants;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_splash);

        // En dispositivos con versión < 5.0 hay un problema existente con SSL. Esto lo soluciona
        // TODO Sigue crasheando
        try {
            ProviderInstaller.installIfNeeded(this);
        } catch (GooglePlayServicesRepairableException e) {
            // Thrown when Google Play Services is not installed, up-to-date, or enabled
            // Show dialog to allow users to install, update, or otherwise enable Google Play services.
            GooglePlayServicesUtil.getErrorDialog(e.getConnectionStatusCode(), this, 0);
        } catch (GooglePlayServicesNotAvailableException e) {
            Log.e("SecurityException", "Google Play Services not available.");
        } catch (Exception e) {
            Log.d("Unknown Exception", e.getMessage());
        }

        // Comprueba primer inicio de la aplicación
        SharedPreferences prefs = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);
        Intent intent;
        //boolean primerInicio = prefs.getBoolean(Constants.PREFS_PRIMER_INICIO, true);
        boolean primerInicio = false; // Bug. Comprobar setter del primer inicio al omitir
        if(primerInicio) {
            intent = new Intent(SplashActivity.this, ResumenActivity.class);
            intent.putExtra(Constants.EXTRA_VOLUNTARIO, false);
        } else {
            // Comprueba si hay una cuenta conectada
            // TODO
            boolean conectado = UsuarioRepository.getInstance(this).isLoggedIn();
            if(conectado) {
                // Si hay una cuenta conectada, muestra la pantalla principal
                intent = new Intent(SplashActivity.this, HomeActivity.class);
                //intent = new Intent(SplashActivity.this, ExplorarActivity.class); // TEST
            } else {
                // Si no hay cuenta conectada, muestra la pantalla de conexión
                intent = new Intent(this, ConexionActivity.class);
            }
        }

        startActivity(intent);
    }
}
