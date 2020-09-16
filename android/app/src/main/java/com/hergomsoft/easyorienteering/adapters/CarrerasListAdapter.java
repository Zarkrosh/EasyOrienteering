package com.hergomsoft.easyorienteering.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hergomsoft.easyorienteering.R;
import com.hergomsoft.easyorienteering.data.model.Carrera;

import java.util.ArrayList;
import java.util.List;

public class CarrerasListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int TIPO_CARRERA = 0;
    private final int TIPO_CARGANDO = 1;

    private List<Carrera> carreras;
    private OnItemListener carreraListener;
    private boolean cargando = false;

    public static class CarreraViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView nombre, tipo, modalidad;
        OnItemListener carreraListener;

        public CarreraViewHolder(@NonNull View itemView, OnItemListener carreraListener) {
            super(itemView);
            this.carreraListener = carreraListener;

            nombre = itemView.findViewById(R.id.item_carreras_nombre);
            tipo = itemView.findViewById(R.id.item_carreras_tipo);
            modalidad = itemView.findViewById(R.id.item_carreras_modalidad);
            itemView.setOnClickListener(this);
        }

        public void bind(Carrera carrera) {
            if(carrera.getNombre() != null) nombre.setText(carrera.getNombre());
            if(carrera.getTipo() != null) tipo.setText(carrera.getTipo().name());
            if(carrera.getModalidad() != null) modalidad.setText(carrera.getModalidad().name());
        }

        @Override
        public void onClick(View v) {
            carreraListener.onItemClick(getAdapterPosition());
        }
    }

    public static class CargandoViewHolder extends RecyclerView.ViewHolder {
        ProgressBar progressBar;
        public CargandoViewHolder(@NonNull View itemView) {
            super(itemView);
            progressBar = itemView.findViewById(R.id.item_cargando_progress);
            //progressBar.getIndeterminateDrawable()
            //        .setColorFilter(ContextCompat.getColor(context, R.color.colorPrimary), PorterDuff.Mode.SRC_IN );
        }
    }

    public CarrerasListAdapter(OnItemListener carreraListener) {
        this.carreras = new ArrayList<>();
        this.carreraListener = carreraListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder;
        View view;
        switch (viewType) {
            case TIPO_CARRERA:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_carreras, parent, false);
                holder = new CarreraViewHolder(view, carreraListener);
                break;
            case TIPO_CARGANDO:
            default:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cargando, parent, false);
                holder = new CargandoViewHolder(view);
                break;
        }

        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof CarreraViewHolder && position < carreras.size()) {
            ((CarreraViewHolder) holder).bind(carreras.get(position));
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(position == carreras.size() - 1 && cargando) {
            return TIPO_CARGANDO;
        } else {
            return TIPO_CARRERA;
        }
    }

    @Override
    public int getItemCount() { return carreras.size() + ((cargando) ? 1 : 0); }

    public Carrera getCarreraSeleccionada(int position) {
        if(carreras != null && carreras.size() > 0 && carreras.size() > position) {
            return carreras.get(position);
        }
        return null;
    }

    public void setCargando(boolean cargando) {
        this.cargando = cargando;
        if(cargando) {
            carreras.add(new Carrera());
            notifyDataSetChanged();
        } else {
            if(carreras.get(carreras.size() - 1).getId() == null) {
                carreras.remove(carreras.size() - 1);
                notifyDataSetChanged();
            }
        }
    }

    /**
     * Reemplaza la lista de carreras por una nueva y actualiza los datos.
     * @param carreras Lista de carreras
     */
    public void actualizaCarreras(List<Carrera> carreras) {
        this.carreras.clear();
        this.carreras.addAll(carreras);
        notifyDataSetChanged();
    }

    public void borraCarreras() {
        carreras.clear();
        notifyDataSetChanged();
    }
}
