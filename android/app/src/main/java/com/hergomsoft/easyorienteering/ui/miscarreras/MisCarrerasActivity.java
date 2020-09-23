package com.hergomsoft.easyorienteering.ui.miscarreras;

import android.os.Bundle;
import android.view.View;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.hergomsoft.easyorienteering.R;
import com.hergomsoft.easyorienteering.adapters.MisCarrerasPagerAdapter;
import com.hergomsoft.easyorienteering.components.ListaCarrerasComponent;
import com.hergomsoft.easyorienteering.data.model.Carrera;
import com.hergomsoft.easyorienteering.util.BackableActivity;
import com.hergomsoft.easyorienteering.util.Resource;

import java.util.List;
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
    }

    private void setupListas() {
        // Crea las listas
        listaCarrerasParticipadas = new ListaCarrerasComponent(this);
        listaCarrerasParticipadas.muestraFiltros(false);
        listaCarrerasOrganizadas = new ListaCarrerasComponent(this);
        listaCarrerasOrganizadas.muestraFiltros(false);

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
        viewModel.getCarrerasParticipadasUsuario().observe(this, new Observer<Resource<List<Carrera>>>() {
            @Override
            public void onChanged(Resource<List<Carrera>> listResource) {
                manejaCargaCarreras(listResource, listaCarrerasParticipadas);
            }
        });

        viewModel.getCarrerasOrganizadasUsuario().observe(this, new Observer<Resource<List<Carrera>>>() {
            @Override
            public void onChanged(Resource<List<Carrera>> listResource) {
                manejaCargaCarreras(listResource, listaCarrerasOrganizadas);
            }
        });
    }

    private void manejaCargaCarreras(Resource<List<Carrera>> carrerasResource, ListaCarrerasComponent component) {
        if(carrerasResource != null) {
            switch (carrerasResource.status) {
                case LOADING:
                    component.muestraCargaCarrerasGeneral();
                    break;
                case SUCCESS:
                    if (carrerasResource.data != null) {
                        component.actualizaCarreras(carrerasResource.data);
                    } else {
                        component.muestraError("Error inesperado");
                    }
                    break;
                case ERROR:
                    component.muestraError(carrerasResource.message);
                    break;
            }
        }
    }

}