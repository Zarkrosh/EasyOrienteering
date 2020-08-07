package com.hergomsoft.easyorienteering.components;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.hergomsoft.easyorienteering.R;
import com.hergomsoft.easyorienteering.adapters.CarrerasListAdapter;
import com.hergomsoft.easyorienteering.data.model.Carrera;
import com.hergomsoft.easyorienteering.ui.detallescarrera.DetallesCarreraActivity;
import com.hergomsoft.easyorienteering.util.Constants;

import java.util.ArrayList;
import java.util.List;

public class ListaCarrerasComponent extends LinearLayout {

    EditText buscador;
    ListView listaCarreras;
    ProgressBar progressBar;
    TextView error;

    List<Carrera> carreras;
    CarrerasListAdapter adapterCarreras;

    public ListaCarrerasComponent(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        inflate(context, R.layout.lista_carreras, this);

        buscador = findViewById(R.id.lista_carreras_buscador);
        listaCarreras = findViewById(R.id.lista_carreras_lista);
        progressBar = findViewById(R.id.lista_carreras_progress);
        error = findViewById(R.id.lista_carreras_error);

        // Color del spinner circular
        progressBar.getIndeterminateDrawable()
                .setColorFilter(ContextCompat.getColor(context, R.color.colorPrimary), PorterDuff.Mode.SRC_IN );

        // Configura la lista
        carreras = new ArrayList<>();
        adapterCarreras = new CarrerasListAdapter(context, carreras);
        listaCarreras.setAdapter(adapterCarreras);
        AdapterView.OnItemClickListener listenerLista = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Carrera clicked = (Carrera) parent.getItemAtPosition(position);
                Intent intent = new Intent(context, DetallesCarreraActivity.class);
                intent.putExtra(Constants.EXTRA_ID_CARRERA, clicked.getId());
                context.startActivity(intent);
            }
        };
        listaCarreras.setOnItemClickListener(listenerLista);


        // Cambios en el buscador
        buscador.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                //viewModel.actualizaBusqueda(buscador.getText().toString());
            }
        });

        muestraLista();
    }

    public void muestraCargaCarreras() {
        progressBar.setVisibility(View.VISIBLE);
        error.setVisibility(View.GONE);
    }

    public void muestraError(String sError) {
        progressBar.setVisibility(View.GONE);
        listaCarreras.setVisibility(View.INVISIBLE);
        error.setVisibility(View.VISIBLE);
        error.setText(sError);
    }

    public void muestraLista() {
        progressBar.setVisibility(View.GONE);
        error.setVisibility(View.GONE);
        listaCarreras.setVisibility(View.VISIBLE);

        if(carreras.isEmpty()) {
            error.setVisibility(View.VISIBLE);
            error.setText("No hay carreras");
        }
    }

    public void actualizaCarreras(List<Carrera> carreras) {
        adapterCarreras.actualizaCarreras(carreras);
        muestraLista();
    }

}
