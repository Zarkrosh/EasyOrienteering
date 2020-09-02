package com.hergomsoft.easyorienteering.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.hergomsoft.easyorienteering.R;
import com.hergomsoft.easyorienteering.data.model.Carrera;
import com.hergomsoft.easyorienteering.data.model.Recorrido;

import java.util.List;
import java.util.Random;

public class RecorridosDetallesAdapter extends ArrayAdapter<Recorrido> {
    private Context context;
    private List<Recorrido> recorridos;

    public RecorridosDetallesAdapter(Context context, List<Recorrido> recorridos) {
        super(context, 0, recorridos);
        this.recorridos = recorridos;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.item_recorridos_detalles, parent, false);
        }

        Recorrido recorrido = getItem(position);
        if(recorrido != null) {
            TextView nombre = convertView.findViewById(R.id.item_recorridos_nombre_recorrido);
            TextView acabados = convertView.findViewById(R.id.item_recorridos_numero_acabados);
            TextView controles = convertView.findViewById(R.id.item_recorridos_numero_controles);

            nombre.setText(recorrido.getNombre());
            acabados.setText(String.valueOf(new Random().nextInt(100))); // TEST
            controles.setText(String.valueOf(recorrido.getTrazado().length * 2));
        }

        return convertView;
    }

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
