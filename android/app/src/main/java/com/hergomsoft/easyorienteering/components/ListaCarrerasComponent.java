package com.hergomsoft.easyorienteering.components;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hergomsoft.easyorienteering.R;
import com.hergomsoft.easyorienteering.adapters.CarrerasListAdapter;
import com.hergomsoft.easyorienteering.adapters.OnItemListener;
import com.hergomsoft.easyorienteering.data.model.Carrera;
import com.hergomsoft.easyorienteering.ui.detallescarrera.DetallesCarreraActivity;
import com.hergomsoft.easyorienteering.util.Constants;

import java.util.List;

public class ListaCarrerasComponent extends LinearLayout implements OnItemListener {

    Spinner selectorTipo;
    Spinner selectorModalidad;
    SearchView buscador;
    ProgressBar progressBar;
    TextView error;
    RecyclerView recyclerCarreras;
    CarrerasListAdapter adapterCarreras;
    RecyclerView.LayoutManager layoutManager;

    Context context;

    public ListaCarrerasComponent(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public ListaCarrerasComponent(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    private void init() {
        inflate(context, R.layout.lista_carreras, this);

        selectorTipo = findViewById(R.id.lista_carreras_selector_tipo);
        selectorModalidad = findViewById(R.id.lista_carreras_selector_modalidad);
        buscador = findViewById(R.id.lista_carreras_buscador);
        recyclerCarreras = findViewById(R.id.lista_carreras_lista);
        progressBar = findViewById(R.id.lista_carreras_progress);
        error = findViewById(R.id.lista_carreras_error);

        // Color del spinner circular
        progressBar.getIndeterminateDrawable()
                .setColorFilter(ContextCompat.getColor(context, R.color.colorPrimary), PorterDuff.Mode.SRC_IN );

        // Configura la lista
        recyclerCarreras.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(context);
        recyclerCarreras.setLayoutManager(layoutManager);
        adapterCarreras = new CarrerasListAdapter(this);
        recyclerCarreras.setAdapter(adapterCarreras);
    }

    public void muestraCargaCarrerasGeneral() {
        progressBar.setVisibility(View.VISIBLE);
        error.setVisibility(View.GONE);
    }

    public void muestraCargaCarrerasFinal() {
        adapterCarreras.setCargando(true);
    }

    public void muestraError(String sError) {
        progressBar.setVisibility(View.GONE);
        recyclerCarreras.setVisibility(View.INVISIBLE);
        error.setVisibility(View.VISIBLE);
        error.setText(sError);
    }

    public void muestraLista() {
        progressBar.setVisibility(View.GONE);
        error.setVisibility(View.GONE);
        recyclerCarreras.setVisibility(View.VISIBLE);

        if(adapterCarreras.getItemCount() == 0) {
            error.setVisibility(View.VISIBLE);
            error.setText("No hay carreras");
        }
    }

    public void actualizaCarreras(List<Carrera> carreras) {
        adapterCarreras.actualizaCarreras(carreras);
        muestraLista();
    }

    public void scrollSuaveArriba() {
        recyclerCarreras.smoothScrollToPosition(0);
    }
    public void quitaFocusBuscador() {
        buscador.clearFocus();
    }

    public void addOnScrollListener(RecyclerView.OnScrollListener scrollListener) { recyclerCarreras.addOnScrollListener(scrollListener); }
    public void setOnQueryTextListener(SearchView.OnQueryTextListener listener) { buscador.setOnQueryTextListener(listener); }
    public void setSelectoresListener(AdapterView.OnItemSelectedListener listener) {
        selectorTipo.setOnItemSelectedListener(listener);
        selectorModalidad.setOnItemSelectedListener(listener);
    }

    public String getFiltroTipo() {
        String selected = (String) selectorTipo.getSelectedItem();
        if(selected.contentEquals(context.getResources().getStringArray(R.array.entries_tipo)[0])) {
            // La primera opción es no utilizar filtro
            return "";
        } else {
            return selected;
        }
    }
    public String getFiltroModalidad() {
        String selected = (String) selectorModalidad.getSelectedItem();
        if(selected.contentEquals(context.getResources().getStringArray(R.array.entries_modalidad)[0])) {
            // La primera opción es no utilizar filtro
            return "";
        } else {
            return selected;
        }
    }
    public String getFiltroNombre() { return buscador.getQuery().toString(); }

    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(context, DetallesCarreraActivity.class);
        intent.putExtra(Constants.EXTRA_ID_CARRERA, adapterCarreras.getCarreraSeleccionada(position).getId());
        context.startActivity(intent);
    }

}
