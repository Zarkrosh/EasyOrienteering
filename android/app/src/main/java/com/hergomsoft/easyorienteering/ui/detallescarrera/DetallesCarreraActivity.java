package com.hergomsoft.easyorienteering.ui.detallescarrera;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.hergomsoft.easyorienteering.R;
import com.hergomsoft.easyorienteering.adapters.RecorridosDetallesAdapter;
import com.hergomsoft.easyorienteering.data.model.Carrera;
import com.hergomsoft.easyorienteering.data.model.Recurso;
import com.hergomsoft.easyorienteering.util.BackableActivity;
import com.hergomsoft.easyorienteering.components.DialogoCarga;

import java.util.ArrayList;

public class DetallesCarreraActivity extends BackableActivity {

    DetallesCarreraViewModel viewModel;

    TextView nombreCarrera;
    TextView tipoCarrera;
    TextView modalidadCarrera;
    TextView organizadorCarrera;

    ListView listaRecorridos;
    RecorridosDetallesAdapter adapterRecorridos;

    DialogoCarga dialogoCarga;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalles_carrera);
        setTitle(R.string.detalles_carrera);

        viewModel = ViewModelProviders.of(this).get(DetallesCarreraViewModel.class);

        nombreCarrera = findViewById(R.id.detalles_nombre_carrera);
        tipoCarrera = findViewById(R.id.detalles_tipo_carrera);
        modalidadCarrera = findViewById(R.id.detalles_modalidad_carrera);
        organizadorCarrera = findViewById(R.id.detalles_organizador_carrera);
        listaRecorridos = findViewById(R.id.detalles_resultados);

        dialogoCarga = new DialogoCarga(this);

        adapterRecorridos = new RecorridosDetallesAdapter(this, new ArrayList<>());
        listaRecorridos.setAdapter(adapterRecorridos);
        listaRecorridos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO
                Toast.makeText(DetallesCarreraActivity.this, "TODO Ver resultados recorrido", Toast.LENGTH_SHORT).show();
            }
        });

        setupDialogoCarga();
        setupObservadores();
        viewModel.cargaDatosCarrera(getIntent(), "", getString(R.string.detalles_carrera_cargando));
    }

    private void setupDialogoCarga() {
        dialogoCarga = new DialogoCarga(this);
        dialogoCarga.setObservadorEstado(this, viewModel.getEstadoDialogo());
        dialogoCarga.setObservadorTitulo(this, viewModel.getTituloDialogo());
        dialogoCarga.setObservadorMensaje(this, viewModel.getMensajeDialogo());
    }

    private void setupObservadores() {
        // Respuesta a la petición de detalles de una carrera
        viewModel.getCarreraResponse().observe(this, new Observer<Recurso<Carrera>>() {
            @Override
            public void onChanged(Recurso<Carrera> carreraRecurso) {
                if(carreraRecurso.hayError()) {
                    viewModel.actualizaDialogoCarga(DialogoCarga.ESTADO_ERROR, getString(R.string.error), carreraRecurso.getError());
                } else {
                    Carrera carrera = carreraRecurso.getRecurso();
                    if(carrera == null) {
                        viewModel.actualizaDialogoCarga(DialogoCarga.ESTADO_ERROR, getString(R.string.error), "No se ha recibido ninguna carrera");
                    } else {
                        viewModel.ocultaDialogoCarga();
                        viewModel.setCarrera(carrera);

                        // Datos de la carrera
                        nombreCarrera.setText(carrera.getNombre());
                        tipoCarrera.setText(carrera.getTipo().toString());
                        modalidadCarrera.setText(carrera.getModalidad().toString());
                        adapterRecorridos.actualizaRecorridos(carrera.getRecorridos());
                    }
                }
            }
        });
    }
}