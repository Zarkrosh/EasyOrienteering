package com.hergomsoft.easyorienteering.adapters;

import android.content.Context;
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
import java.util.Random;

public class RecorridosListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Recorrido> recorridos;
    private OnItemListener recorridoListener;


    public static class RecorridoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView nombre, acabados, controles;
        OnItemListener recorridoListener;

        public RecorridoViewHolder(@NonNull View itemView, OnItemListener recorridoListener) {
            super(itemView);
            this.recorridoListener = recorridoListener;

            nombre = itemView.findViewById(R.id.item_recorridos_nombre_recorrido);
            acabados = itemView.findViewById(R.id.item_recorridos_numero_acabados);
            controles = itemView.findViewById(R.id.item_recorridos_numero_controles);
            itemView.setOnClickListener(this);
        }

        public void bind(Recorrido recorrido) {
            nombre.setText(recorrido.getNombre());
            acabados.setText(String.valueOf(new Random().nextInt(100)));
            controles.setText(String.valueOf(recorrido.getTrazado().length));
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recorridos_detalles, parent, false);
        return new CarrerasListAdapter.CarreraViewHolder(view, recorridoListener);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((RecorridosListAdapter.RecorridoViewHolder) holder).bind(recorridos.get(position));
    }

    /*
    @Override
    public int getItemViewType(int position) {
        if(position == carreras.size() - 1 && cargando) {
            return TIPO_CARGANDO;
        } else {
            return TIPO_CARRERA;
        }
    }
     */

    @Override
    public int getItemCount() { return recorridos.size(); }


    /**
     * Reemplaza la lista de recorridos por una nueva y actualiza los datos.
     * @param recorridos Lista de recorridos
     */
    public void actualizaRecorridos(List<Recorrido> recorridos) {
        this.recorridos.clear();
        this.recorridos.addAll(recorridos);
        notifyDataSetChanged();
    }
}
