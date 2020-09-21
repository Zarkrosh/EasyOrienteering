package com.hergomsoft.easyorienteering.ui.resumen;

import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.material.tabs.TabLayout;
import com.hergomsoft.easyorienteering.R;
import com.hergomsoft.easyorienteering.adapters.ResumenPagerAdapter;
import com.hergomsoft.easyorienteering.util.BackableActivity;
import com.hergomsoft.easyorienteering.ui.conexion.ConexionActivity;
import com.hergomsoft.easyorienteering.util.Constants;

public class ResumenActivity extends BackableActivity {
    private boolean voluntario;

    private Button btnOmitir;
    private Button btnSiguiente;
    private ViewPager pager;
    private TabLayout tabs;
    private Button btnFin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.resumenTitulo);
        setContentView(R.layout.activity_resumen);

        btnOmitir = findViewById(R.id.resumenOmitir);
        //btnSiguiente = findViewById(R.id.resumenSiguiente);
        pager = findViewById(R.id.resumenPager);
        tabs = findViewById(R.id.resumenPuntos);

        // Enlaza ViewPager y TabLayout
        pager.setAdapter(new ResumenPagerAdapter(this));
        tabs.setupWithViewPager(pager);

        // Distingue entre primera visualización o voluntaria
        voluntario = getIntent().getBooleanExtra(Constants.EXTRA_VOLUNTARIO, false);
        if(voluntario) {
            // Ya la ha visto -> se permite volver atrás (pero no omitir)
            btnOmitir.setVisibility(View.GONE);
        } else {
            // Primera vez -> no se permite volver atrás (pero sí omitir)
            getSupportActionBar().hide();
            btnOmitir.setVisibility(View.VISIBLE);
        }


        View.OnClickListener listenerFinaliza = this::finalizaResumen;
        btnOmitir.setOnClickListener(listenerFinaliza);
        //btnSiguiente.setOnClickListener(listenerFinaliza);
    }

    public void finalizaResumen(View v) {
        if(voluntario) {
            // Vuelve atrás
            finish();
        } else {
            // Para que no vuelva a mostrar de nuevo el resumen
            SharedPreferences.Editor editor = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE).edit();
            editor.putBoolean(Constants.PREFS_PRIMER_INICIO, false);
            editor.apply();
            // Lleva a la pantalla de conexión de cuenta
            mostrarPantallaConexion();
        }
    }

    /**
     * Muestra la pantalla de conexión de cuenta.
     */
    public void mostrarPantallaConexion() {
        Intent i = new Intent(ResumenActivity.this, ConexionActivity.class);
        finish();
        startActivity(i);
    }
}
