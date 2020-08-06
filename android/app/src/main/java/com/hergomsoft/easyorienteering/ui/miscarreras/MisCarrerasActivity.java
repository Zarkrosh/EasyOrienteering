package com.hergomsoft.easyorienteering.ui.miscarreras;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.View;

import com.google.android.material.tabs.TabLayout;
import com.hergomsoft.easyorienteering.R;
import com.hergomsoft.easyorienteering.adapters.MisCarrerasPagerAdapter;
import com.hergomsoft.easyorienteering.components.ListaCarrerasComponent;
import com.hergomsoft.easyorienteering.data.api.responses.CarrerasUsuarioResponse;
import com.hergomsoft.easyorienteering.data.model.Recurso;
import com.hergomsoft.easyorienteering.util.BackableActivity;

import java.util.Vector;

public class MisCarrerasActivity extends BackableActivity {

    private MisCarrerasViewModel viewModel;

    private ViewPager pager;
    private TabLayout tabs;

    ListaCarrerasComponent listaCarrerasParticipadas;
    ListaCarrerasComponent listaCarrerasOrganizadas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mis_carreras);
        setTitle(getString(R.string.home_mis_carreras));

        viewModel = ViewModelProviders.of(this).get(MisCarrerasViewModel.class);

        pager = findViewById(R.id.mis_carreras_pager);
        tabs = findViewById(R.id.mis_carreras_tabs);

        setupListas();
        setupObservadores();
        viewModel.cargaCarreras();
    }

    private void setupListas() {
        // Crea las listas
        listaCarrerasParticipadas = new ListaCarrerasComponent(this);
        listaCarrerasOrganizadas = new ListaCarrerasComponent(this);

        // Configura el ViewPager
        String[] titulosTabs = {getString(R.string.mis_carreras_participadas),
                getString(R.string.mis_carreras_organizadas)};
        Vector<View> pages = new Vector<>();
        pages.add(listaCarrerasParticipadas);
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
                    // TODO Separar
                    listaCarrerasParticipadas.muestraError(recurso.getError());
                    listaCarrerasOrganizadas.muestraError(recurso.getError());
                } else {
                    if(recurso.getRecurso()) {
                        listaCarrerasParticipadas.muestraCargaCarreras();
                        listaCarrerasOrganizadas.muestraCargaCarreras();
                    } else {
                        listaCarrerasParticipadas.muestraLista();
                        listaCarrerasOrganizadas.muestraLista();
                    }
                }
            }
        });

        // Respuesta con las listas de carreras
        viewModel.getCarrerasResponse().observe(this, new Observer<Recurso<CarrerasUsuarioResponse>>() {
            @Override
            public void onChanged(Recurso<CarrerasUsuarioResponse> response) {
                if(response.hayError()) {
                    viewModel.mostrarErrorCarga(response.getError());
                } else {
                    viewModel.cargaCarrerasFinalizada();
                    listaCarrerasParticipadas.actualizaCarreras(response.getRecurso().getParticipadas());
                    listaCarrerasOrganizadas.actualizaCarreras(response.getRecurso().getOrganizadas());
                }
            }
        });

    }


}