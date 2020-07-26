package com.hergomsoft.easyorienteering.ui.miscarreras;

import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.hergomsoft.easyorienteering.R;
import com.hergomsoft.easyorienteering.adapters.CarrerasListAdapter;
import com.hergomsoft.easyorienteering.adapters.MisCarrerasPagerAdapter;
import com.hergomsoft.easyorienteering.data.api.responses.CarrerasUsuarioResponse;
import com.hergomsoft.easyorienteering.data.model.Carrera;
import com.hergomsoft.easyorienteering.data.model.Recurso;
import com.hergomsoft.easyorienteering.ui.detallescarrera.DetallesCarreraActivity;
import com.hergomsoft.easyorienteering.util.BackableActivity;
import com.hergomsoft.easyorienteering.util.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class MisCarreras extends BackableActivity {

    private MisCarrerasViewModel viewModel;

    private ViewPager pager;
    private TabLayout tabs;
    private ProgressBar progressBar;
    private TextView error;

    List<Carrera> carrerasCorridas;
    List<Carrera> carrerasOrganizadas;
    CarrerasListAdapter adapterCorridas;
    CarrerasListAdapter adapterOrganizadas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mis_carreras);
        setTitle(getString(R.string.home_mis_carreras));

        viewModel = new ViewModelProvider(this).get(MisCarrerasViewModel.class);

        pager = findViewById(R.id.mis_carreras_pager);
        tabs = findViewById(R.id.mis_carreras_tabs);
        progressBar = findViewById(R.id.mis_carreras_progress);
        error = findViewById(R.id.mis_carreras_error);

        carrerasCorridas = new ArrayList<>();
        carrerasOrganizadas = new ArrayList<>();

        // Color del spinner circular
        progressBar.getIndeterminateDrawable()
                .setColorFilter(ContextCompat.getColor(this, R.color.colorPrimary), PorterDuff.Mode.SRC_IN );

        setupListas();
        setupObservadores();
        viewModel.cargaCarreras();
    }

    private void setupListas() {
        // Configura los ListViews
        ListView listaCarrerasCorridas = new ListView(this);
        ListView listaCarrerasOrganizadas = new ListView(this);
        adapterCorridas = new CarrerasListAdapter(this, carrerasCorridas);
        adapterOrganizadas = new CarrerasListAdapter(this, carrerasOrganizadas);
        listaCarrerasCorridas.setAdapter(adapterCorridas);
        listaCarrerasOrganizadas.setAdapter(adapterOrganizadas);
        AdapterView.OnItemClickListener listenerLista = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Carrera clicked = (Carrera) parent.getItemAtPosition(position);
                Intent intent = new Intent(MisCarreras.this, DetallesCarreraActivity.class);
                intent.putExtra(Constants.EXTRA_ID_CARRERA, clicked.getId());
                startActivity(intent);
            }
        };
        listaCarrerasCorridas.setOnItemClickListener(listenerLista);
        listaCarrerasOrganizadas.setOnItemClickListener(listenerLista);

        // Configura el ViewPager
        String[] titulosTabs = {getString(R.string.mis_carreras_corridas),
                getString(R.string.mis_carreras_organizadas)};
        Vector<View> pages = new Vector<>();
        pages.add(listaCarrerasCorridas);
        pages.add(listaCarrerasOrganizadas);
        pager.setAdapter(new MisCarrerasPagerAdapter(this, pages, titulosTabs));
        tabs.setupWithViewPager(pager);
    }

    private void setupObservadores() {
        // Estados de carga o error de las listas
        viewModel.getEstadoCargaCarreras().observe(this, new Observer<Recurso<Boolean>>() {
            @Override
            public void onChanged(Recurso<Boolean> recurso) {
                if(recurso.hayError()) {
                    error.setText(recurso.getError());
                    error.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                } else {
                    error.setVisibility(View.GONE);
                    if(recurso.getRecurso()) {
                        progressBar.setVisibility(View.VISIBLE);
                    } else {
                        progressBar.setVisibility(View.GONE);
                    }
                }
            }
        });

        // Respuesta con las listas de carreras
        viewModel.getCarrerasResponse().observe(this, new Observer<Recurso<CarrerasUsuarioResponse>>() {
            @Override
            public void onChanged(Recurso<CarrerasUsuarioResponse> response) {
                // TEST
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {


                        if(response.hayError()) {
                            viewModel.mostrarErrorCarga(response.getError());
                        } else {
                            viewModel.cargaCarrerasFinalizada();
                            adapterCorridas.actualizaCarreras(response.getRecurso().getCorridas());
                            adapterOrganizadas.actualizaCarreras(response.getRecurso().getOrganizadas());
                        }


                    }
                }, 2000);
            }
        });
    }


}