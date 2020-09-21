package com.hergomsoft.easyorienteering.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hergomsoft.easyorienteering.R;
import com.hergomsoft.easyorienteering.data.model.Carrera;
import com.hergomsoft.easyorienteering.data.model.Recorrido;

import java.util.ArrayList;
import java.util.List;

public class RecorridosListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Carrera carrera;
    private List<Recorrido> recorridos;
    private OnItemListener recorridoListener;


    public static class RecorridoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView nombre, participantes, controles;
        OnItemListener recorridoListener;

        public RecorridoViewHolder(@NonNull View itemView, OnItemListener recorridoListener) {
            super(itemView);
            this.recorridoListener = recorridoListener;

            nombre = itemView.findViewById(R.id.item_recorridos_nombre_recorrido);
            participantes = itemView.findViewById(R.id.item_recorridos_numero_participantes);
            controles = itemView.findViewById(R.id.item_recorridos_numero_controles);
            itemView.setOnClickListener(this);
        }

        public void bind(Recorrido recorrido, Carrera carrera) {
            nombre.setText(recorrido.getNombre());
            participantes.setText(String.valueOf(recorrido.getParticipaciones()));
            String nControles = String.valueOf(recorrido.getTrazado().length);
            if(carrera.getModalidad() == Carrera.Modalidad.SCORE) {
                if(carrera.getControles() != null) nControles = String.valueOf(carrera.getControles().size());
                else nControles = ""; // Fallo al persistir controles
            }
            controles.setText(nControles);
        }

        @Override
        public void onClick(View v) {
            recorridoListener.onItemClick(getAdapterPosition());
        }
    }


    public RecorridosListAdapter(OnItemListener carreraListener) {
        this.recorridos = new ArrayList<>();
        this.recorridoListener = carreraListener;
    }

    public Recorrido getRecorridoSeleccionado(int position) {
        if(recorridos != null && recorridos.size() > 0 && recorridos.size() > position) {
            return recorridos.get(position);
        }
        return null;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recorridos, parent, false);
        return new RecorridosListAdapter.RecorridoViewHolder(view, recorridoListener);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((RecorridosListAdapter.RecorridoViewHolder) holder).bind(recorridos.get(position), carrera);
    }

    @Override
    public int getItemCount() { return recorridos.size(); }


    /**
     * Reemplaza la lista de recorridos por una nueva y actualiza los datos.
     * @param nRecorridos Nueva lista de recorridos
     */
    public void actualizaRecorridos(List<Recorrido> nRecorridos, Carrera carrera) {
        this.carrera = carrera;
        this.recorridos.clear();
        this.recorridos.addAll(nRecorridos);
        notifyDataSetChanged();
    }
}
