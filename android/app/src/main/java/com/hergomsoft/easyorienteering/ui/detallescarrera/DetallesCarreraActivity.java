package com.hergomsoft.easyorienteering.ui.detallescarrera;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

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
import com.hergomsoft.easyorienteering.util.DialogoCarga;

import java.util.ArrayList;
import java.util.Arrays;

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

        viewModel = new ViewModelProvider(this).get(DetallesCarreraViewModel.class);

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


        setupObservadores();
        viewModel.cargaDatosCarrera(getIntent(), "", getString(R.string.detalles_carrera_cargando));
    }

    private void setupObservadores() {
        // Estado de diálogo de carga/error
        viewModel.getEstadoDialogo().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                String titulo = viewModel.getTituloDialogo();
                String mensaje = viewModel.getMensajeDialogo();
                switch(integer) {
                    case DetallesCarreraViewModel.ESTADO_ERROR:
                        dialogoCarga.muestraMensajeError(titulo, mensaje);
                        break;
                    case DetallesCarreraViewModel.ESTADO_CARGANDO:
                        dialogoCarga.muestraMensajeCarga(titulo, mensaje);
                        break;
                    case DetallesCarreraViewModel.ESTADO_OCULTO:
                    default:
                        dialogoCarga.dismiss();
                        break;
                }
            }
        });

        // Respuesta a la petición de detalles de una carrera
        viewModel.getCarreraResponse().observe(this, new Observer<Recurso<Carrera>>() {
            @Override
            public void onChanged(Recurso<Carrera> carreraRecurso) {
                if(carreraRecurso.hayError()) {
                    viewModel.muestraErrorCarga(getString(R.string.error), carreraRecurso.getError());
                } else {
                    Carrera carrera = carreraRecurso.getRecurso();
                    if(carrera == null) {
                        viewModel.muestraErrorCarga(getString(R.string.error), "No se ha recibido ninguna carrera");
                    } else {
                        viewModel.ocultaDialogo();
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