package com.hergomsoft.easyorienteering.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.hergomsoft.easyorienteering.R;
import com.hergomsoft.easyorienteering.data.model.Carrera;

import java.util.List;

public class CarrerasListAdapter extends ArrayAdapter<Carrera> {
    private Context context;
    private List<Carrera> carreras;

    public CarrerasListAdapter(Context context, List<Carrera> carreras) {
        super(context, 0, carreras);
        this.carreras = carreras;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.item_carreras, parent, false);
        }

        Carrera carrera = getItem(position);
        if(carrera != null) {
            TextView nombre = convertView.findViewById(R.id.item_carreras_nombre);
            TextView tipo = convertView.findViewById(R.id.item_carreras_tipo);
            TextView modalidad = convertView.findViewById(R.id.item_carreras_modalidad);

            nombre.setText(carrera.getNombre());
            tipo.setText(carrera.getTipo().toString());
            modalidad.setText(carrera.getModalidad().toString());
        }

        return convertView;
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
}
