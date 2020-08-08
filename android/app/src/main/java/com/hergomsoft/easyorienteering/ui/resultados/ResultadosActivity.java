package com.hergomsoft.easyorienteering.ui.resultados;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.hergomsoft.easyorienteering.R;
import com.hergomsoft.easyorienteering.components.DialogoCarga;
import com.hergomsoft.easyorienteering.data.api.responses.RegistrosRecorridoResponse;
import com.hergomsoft.easyorienteering.data.api.responses.RegistrosUsuario;
import com.hergomsoft.easyorienteering.data.model.ParcialUsuario;
import com.hergomsoft.easyorienteering.data.model.Recorrido;
import com.hergomsoft.easyorienteering.data.model.ResultadoUsuario;
import com.hergomsoft.easyorienteering.data.model.Usuario;
import com.hergomsoft.easyorienteering.util.BackableActivity;
import com.hergomsoft.easyorienteering.util.Constants;
import com.hergomsoft.easyorienteering.util.Resource;
import com.hergomsoft.easyorienteering.util.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class ResultadosActivity extends BackableActivity {

    ResultadosViewModel viewModel;

    DialogoCarga dialogoCarga;
    TableLayout tablaResultados;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.resultados_resultados);
        setContentView(R.layout.activity_resultados_carrera);

        viewModel = ViewModelProviders.of(this).get(ResultadosViewModel.class);

        tablaResultados = findViewById(R.id.resultados_tabla);

        setupDialogoCarga();
        setupObservadores();
    }

    private void setupDialogoCarga() {
        dialogoCarga = new DialogoCarga(this);
        dialogoCarga.setObservadorEstado(this, viewModel.getEstadoDialogo());
        dialogoCarga.setObservadorTitulo(this, viewModel.getTituloDialogo());
        dialogoCarga.setObservadorMensaje(this, viewModel.getMensajeDialogo());
    }

    private void setupObservadores() {
        long idRecorrido = 0;
        if(getIntent().hasExtra(Constants.EXTRA_ID_RECORRIDO)) {
            idRecorrido = getIntent().getLongExtra(Constants.EXTRA_ID_RECORRIDO, -1);
            if(idRecorrido == -1) {
                Toast.makeText(this, "Error al cargar resultados (ID erróneo)", Toast.LENGTH_SHORT).show();
                finish();
            }
        } else {
            Toast.makeText(this, "Error al cargar resultados (No hay ID)", Toast.LENGTH_SHORT).show();
            finish();
        }

        viewModel.getResultados(idRecorrido).observe(this, new Observer<Resource<RegistrosRecorridoResponse>>() {
            @Override
            public void onChanged(Resource<RegistrosRecorridoResponse> response) {
                if(response != null) {
                    switch(response.status) {
                        case LOADING:
                            viewModel.actualizaDialogoCarga(DialogoCarga.ESTADO_CARGANDO,
                                    "", getString(R.string.resultados_cargando));
                            break;
                        case SUCCESS:
                            if(response.data != null) {
                                RegistrosRecorridoResponse registrosUsuarios = response.data;
                                Recorrido recorrido = response.data.getRecorrido();
                                setTitle(getString(R.string.resultados_resultados, recorrido.getNombre()));
                                String[] trazado = recorrido.getTrazado();
                                // Genera parciales independientes de los corredores
                                List<ResultadoUsuario> resultados = generaResultados(trazado, response.data.getRegistrosUsuarios());
                                generaTablaResultados(trazado, resultados);
                                viewModel.ocultaDialogoCarga();
                            } else {
                                viewModel.actualizaDialogoCarga(DialogoCarga.ESTADO_ERROR,
                                        getString(R.string.error_inesperado), response.message);
                            }
                            break;
                        case ERROR:
                            viewModel.actualizaDialogoCarga(DialogoCarga.ESTADO_ERROR,
                                    getString(R.string.error), response.message);
                            break;
                    }
                }
            }
        });
    }

    private List<ResultadoUsuario> generaResultados(String[] trazado, List<RegistrosUsuario> registrosUsuarios) {
        List<ResultadoUsuario> resultados = new ArrayList<>();

        // Calcula los parciales de cada usuario
        for(RegistrosUsuario ru : registrosUsuarios) {
            Usuario corredor = ru.getUsuario();
            ResultadoUsuario.Tipo tipo = ResultadoUsuario.Tipo.OK;
            if(ru.getRegistros().size() < trazado.length) tipo = ResultadoUsuario.Tipo.PENDIENTE;
            long tAcum = 0;
            List<ParcialUsuario> parciales = new ArrayList<>();
            for(int i = 1; i < ru.getRegistros().size(); i++) {
                Date dPri = ru.getRegistros().get(i-1).getFecha();
                Date dSec = ru.getRegistros().get(i).getFecha();
                Long tParcial = null;
                if(dPri != null && dSec != null) {
                    long tPri = dPri.getTime() / 1000; // Segundos
                    long tSec = dSec.getTime() / 1000; // Segundos
                    tParcial = tSec - tPri;
                    tAcum += tParcial;
                    parciales.add(new ParcialUsuario(tParcial, tAcum));
                } else {
                    tipo = ResultadoUsuario.Tipo.ABANDONADO;
                }
            }

            ResultadoUsuario res = new ResultadoUsuario(String.format("%s\n%s", corredor.getNombre(), corredor.getClub()), tAcum, tipo);
            res.setParciales(parciales);
            resultados.add(res);
        }

        // Ordena lista por tiempo total y tipo
        Collections.sort(resultados, new Comparator<ResultadoUsuario>() {
            @Override
            public int compare(ResultadoUsuario u1, ResultadoUsuario u2) {
                if(u1.getTipo().equals(ResultadoUsuario.Tipo.ABANDONADO)) return Integer.MAX_VALUE; // Abandonados al final
                else if(u1.getTipo().equals(ResultadoUsuario.Tipo.PENDIENTE)) {
                    if(u2.getTipo().equals(ResultadoUsuario.Tipo.ABANDONADO)) {
                        return -1; // Los pendientes van antes que los abandonados
                    } else {
                        return 1;  // Los pendientes van después que los acabados
                    }
                } else {
                    return (int) (u1.getTiempoTotal() - u2.getTiempoTotal());
                }
            }
        });
        // Actualiza posiciones y diferencias
        int posicion = 1;
        long tiempoGanador = resultados.get(0).getTiempoTotal();
        for(ResultadoUsuario ru : resultados) {
            ru.setPosicion(posicion++);
            ru.setDiferenciaGanador(ru.getTiempoTotal() - tiempoGanador);
        }

        return resultados;
    }

    private void generaTablaResultados(String[] trazado, List<ResultadoUsuario> resultados) {
        tablaResultados.removeAllViews();
        // Cabecera
        final TableRow cabecera = (TableRow) getLayoutInflater().inflate(R.layout.item_resultados, null);
        TextView tvCabeceraPosicion = cabecera.findViewById(R.id.item_resultados_posicion);
        TextView tvCabeceraNombreClub = cabecera.findViewById(R.id.item_resultados_nombre_club);
        TextView tvCabeceraTiempoTotal = cabecera.findViewById(R.id.item_resultados_tiempo_total);
        TextView tvCabeceraDiferencia = cabecera.findViewById(R.id.item_resultados_diferencia);
        tvCabeceraPosicion.setText(R.string.resultados_posicion);
        tvCabeceraNombreClub.setText(R.string.resultados_nombre_club);
        tvCabeceraTiempoTotal.setText(R.string.resultados_tiempo_total);
        tvCabeceraDiferencia.setText(R.string.resultados_diferencia);
         // Trazado de la carrera
        TableRow.LayoutParams lpTvTrazado = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.MATCH_PARENT);
        lpTvTrazado.setMarginStart(Utils.getPixFromDP(getResources().getDimension(R.dimen.marginStartItemResultados), getResources()));
        float tamLetra = (float) getResources().getDimension(R.dimen.tamLetraItemResultados) / getResources().getDisplayMetrics().density;
        for(int i = 0; i < trazado.length - 1; i++) {
            String text;
            if(i == 0) {
                // Salida -> Primer control
                text = String.format("S-1 (%s)", trazado[i+1]);
            } else if(i == trazado.length - 2) {
                // Penúltimo control -> Meta
                text = String.format("%d-M", trazado.length - 1);
            } else {
                text = String.format("%d-%d (%s)", i, i+1, trazado[i+1]);
            }
            TextView tvTrazado = new TextView(this);
            tvTrazado.setLayoutParams(lpTvTrazado);
            tvTrazado.setGravity(Gravity.CENTER);
            tvTrazado.setText(text);
            tvTrazado.setTextColor(Color.BLACK);
            tvTrazado.setTextSize(TypedValue.COMPLEX_UNIT_SP, tamLetra);
            cabecera.addView(tvTrazado);
        }
        tablaResultados.addView(cabecera);

        // Resultados de corredores
        int pos = 1;
        for(ResultadoUsuario r : resultados) {
            final TableRow row = (TableRow) getLayoutInflater().inflate(R.layout.item_resultados, null);
            // Crea los elementos
            TextView tvPosicion = row.findViewById(R.id.item_resultados_posicion);
            TextView tvNombreClub = row.findViewById(R.id.item_resultados_nombre_club);
            TextView tvTiempoTotal = row.findViewById(R.id.item_resultados_tiempo_total);
            TextView tvDiferencia = row.findViewById(R.id.item_resultados_diferencia);
            // Asigna valores
            tvNombreClub.setText(r.getNombreClub());
            String sTiempoTotal;
            String sPosicion = "";
            String sDiferencia = "";
            switch (r.getTipo()) {
                case OK:
                    sPosicion = String.format("%d", r.getPosicion());
                    sTiempoTotal = Utils.getTiempoResultadoFromSecs(r.getTiempoTotal());
                    if(r.getDiferenciaGanador() > 0) sDiferencia = String.format("+%s", Utils.getTiempoResultadoFromSecs(r.getDiferenciaGanador()));
                    break;
                case PENDIENTE:
                    sTiempoTotal = getString(R.string.resultados_pendiente);
                    break;
                case ABANDONADO:
                default:
                    sTiempoTotal = getString(R.string.resultados_abandonado);
            }
            tvPosicion.setText(sPosicion);
            tvTiempoTotal.setText(sTiempoTotal);
            tvDiferencia.setText(sDiferencia);
            // Parciales
            for(ParcialUsuario p : r.getParciales()) {
                final LinearLayout llParcial = (LinearLayout) getLayoutInflater().inflate(R.layout.item_parcial, null);
                TextView tvTiempoParcial = llParcial.findViewById(R.id.item_parcial_tiempo_parcial);
                TextView tvTiempoAcumulado = llParcial.findViewById(R.id.item_parcial_tiempo_acumulado);
                TextView tvPosicionParcial = llParcial.findViewById(R.id.item_parcial_posicion_parcial);
                TextView tvPosicionAcumulada = llParcial.findViewById(R.id.item_parcial_posicion_acumulado);
                tvTiempoParcial.setText(Utils.getTiempoResultadoFromSecs(p.getTiempoParcial()));
                tvTiempoAcumulado.setText(Utils.getTiempoResultadoFromSecs(p.getTiempoAcumulado()));
                tvPosicionParcial.setText(String.format("(%d)", p.getPosicionParcial()));
                tvPosicionAcumulada.setText(String.format("(%d)", p.getPosicionAcumulada()));
                row.addView(llParcial);
            }

            tablaResultados.addView(row, pos++);
        }
    }

}