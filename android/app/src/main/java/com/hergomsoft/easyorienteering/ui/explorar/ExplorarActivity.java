package com.hergomsoft.easyorienteering.ui.explorar;

import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.ViewSwitcher;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import com.hergomsoft.easyorienteering.BuildConfig;
import com.hergomsoft.easyorienteering.R;
import com.hergomsoft.easyorienteering.components.ListaCarrerasComponent;
import com.hergomsoft.easyorienteering.data.model.Carrera;
import com.hergomsoft.easyorienteering.util.BackableActivity;
import com.hergomsoft.easyorienteering.util.Resource;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.MapView;

import java.util.List;

public class ExplorarActivity extends BackableActivity {

    ExplorarViewModel viewModel;

    // Cambios entre vistas
    ViewSwitcher viewSwitcher;
    ImageButton btnCambioVista;
    Animation slide_in_left, slide_in_right, slide_out_left, slide_out_right;

    // Vista de búsqueda
    ListaCarrerasComponent listaCarreras;

    // Vista de mapa
    MapView mapView;
    IMapController mapController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.home_explorar);
        setContentView(R.layout.activity_explorar);

        viewModel = ViewModelProviders.of(this).get(ExplorarViewModel.class);

        // TODO Gestionar permisos

        viewSwitcher = findViewById(R.id.explorar_switcher);
        btnCambioVista = findViewById(R.id.explorar_btn_cambio_vista);
        listaCarreras = findViewById(R.id.explorar_lista_carreras);
        mapView = findViewById(R.id.explorar_mapa);

        // Animaciones
        slide_in_left = AnimationUtils.loadAnimation(this, R.anim.slide_in_left);
        slide_in_right = AnimationUtils.loadAnimation(this, R.anim.slide_in_right);
        slide_out_left = AnimationUtils.loadAnimation(this, R.anim.slide_out_left);
        slide_out_right = AnimationUtils.loadAnimation(this, R.anim.slide_out_right);

        setupListeners();
        setupMapa();
        setupObservadores();
        viewModel.buscaCarreras("", "", "", 0);
    }

    private void setupListeners() {
        // Búsqueda de más elementos al llegar al final del scroll en la lista de resultados
        RecyclerView.OnScrollListener scrollListener = new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if(!recyclerView.canScrollVertically(1)) viewModel.buscaSiguientePagina();
            }
        };
        listaCarreras.addOnScrollListener(scrollListener);

        // Cambios en los filtros
        listaCarreras.setSelectoresListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                buscaCarreras();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Cambio en el buscador
        listaCarreras.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String nombre) {
                buscaCarreras();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        // Cambio de vistas al pulsar el botón
        btnCambioVista.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(viewSwitcher.getDisplayedChild() == 0) mostrarVistaMapa();
                else mostrarVistaBusqueda();
            }
        });
    }

    private void setupObservadores() {
        // Cambios en la lista de carreras
        viewModel.getCarreras().observe(this, new Observer<Resource<List<Carrera>>>() {
            @Override
            public void onChanged(Resource<List<Carrera>> listResource) {
                if(listResource != null) {
                    switch (listResource.status) {
                        case LOADING:
                            if(viewModel.getNumeroPagina() > 0) listaCarreras.muestraCargaCarrerasFinal();
                            else listaCarreras.muestraCargaCarrerasGeneral();
                            break;
                        case SUCCESS:
                            listaCarreras.actualizaCarreras(listResource.data);
                            break;
                        case ERROR:
                            listaCarreras.muestraError(listResource.message);
                            break;
                    }
                }
            }
        });
    }

    private void buscaCarreras() {
        String nombre = listaCarreras.getFiltroNombre();
        String tipo = listaCarreras.getFiltroTipo();
        String modalidad = listaCarreras.getFiltroModalidad();
        viewModel.buscaCarreras(nombre, tipo, modalidad, 0);
        listaCarreras.scrollSuaveArriba();
        listaCarreras.quitaFocusBuscador();
    }

    private void mostrarVistaBusqueda() {
        viewSwitcher.setInAnimation(slide_in_left);
        viewSwitcher.setOutAnimation(slide_out_right);
        viewSwitcher.setDisplayedChild(0);
        btnCambioVista.setImageResource(R.drawable.img_gps);
    }

    private void mostrarVistaMapa() {
        viewSwitcher.setInAnimation(slide_in_right);
        viewSwitcher.setOutAnimation(slide_out_left);
        viewSwitcher.setDisplayedChild(1);
        btnCambioVista.setImageResource(R.drawable.img_search);
        viewModel.cargaMarcadoresCircuitos();
    }

    /**
     * Configura los elementos del mapa.
     */
    private void setupMapa() {
        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        Configuration.getInstance().setUserAgentValue(BuildConfig.APPLICATION_ID);
        mapView.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE);
        mapView.setMultiTouchControls(true);
        mapController = mapView.getController();
        mapView.getZoomController().setVisibility(CustomZoomButtonsController.Visibility.SHOW_AND_FADEOUT);
        // TODO Cargar configuración anterior (centro, zoom...)
        mapController.setZoom(6.8);
        mapController.setCenter(new GeoPoint(40.0, -3.0));
    }






    @Override
    public void onResume() {
        super.onResume();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
        mapView.onResume(); //needed for compass, my location overlays, v6.0.0 and up
    }

    @Override
    public void onPause() {
        super.onPause();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().save(this, prefs);
        mapView.onPause();  //needed for compass, my location overlays, v6.0.0 and up
    }

    @Override
    public void onBackPressed() {
        // Si pulsa atrás en la vista de mapa, vuelve a la vista de búsqueda
        if(viewSwitcher.getDisplayedChild() == 0)
            super.onBackPressed();
        else
            mostrarVistaBusqueda();
    }
}