package com.hergomsoft.easyorienteering.ui.resultados;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.hergomsoft.easyorienteering.R;
import com.hergomsoft.easyorienteering.components.DialogoCarga;
import com.hergomsoft.easyorienteering.data.api.responses.RegistroResponse;
import com.hergomsoft.easyorienteering.data.api.responses.RegistrosRecorridoResponse;
import com.hergomsoft.easyorienteering.data.api.responses.RegistrosUsuario;
import com.hergomsoft.easyorienteering.data.model.Carrera;
import com.hergomsoft.easyorienteering.data.model.ParcialUsuario;
import com.hergomsoft.easyorienteering.data.model.Recorrido;
import com.hergomsoft.easyorienteering.data.model.ResultadoUsuario;
import com.hergomsoft.easyorienteering.data.model.Usuario;
import com.hergomsoft.easyorienteering.ui.perfil.PerfilActivity;
import com.hergomsoft.easyorienteering.util.BackableActivity;
import com.hergomsoft.easyorienteering.util.Constants;
import com.hergomsoft.easyorienteering.util.Resource;
import com.hergomsoft.easyorienteering.util.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

public class ResultadosActivity extends BackableActivity {

    ResultadosViewModel viewModel;

    DialogoCarga dialogoCarga;
    TableLayout tablaResultados;
    TextView tvSinResultados;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.resultados_resultados);
        setContentView(R.layout.activity_resultados_carrera);

        viewModel = ViewModelProviders.of(this).get(ResultadosViewModel.class);

        tablaResultados = findViewById(R.id.resultados_tabla);
        tvSinResultados = findViewById(R.id.resultados_sin_resultados);

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
                                Carrera.Modalidad modalidad = response.data.getModalidad();
                                Recorrido recorrido = response.data.getRecorrido();
                                setTitle(getString(R.string.resultados_resultados, recorrido.getNombre()));
                                String[] trazado = recorrido.getTrazado();
                                Map<String, Integer> puntuacionControles = response.data.getPuntuacionesControles();
                                // Genera parciales independientes de los corredores
                                List<ResultadoUsuario> resultados = generaResultados(modalidad, trazado, puntuacionControles, response.data.getRegistrosUsuarios());
                                generaTablaResultados(modalidad, trazado, resultados);
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

    private List<ResultadoUsuario> generaResultados(Carrera.Modalidad modalidad, String[] trazado, Map<String, Integer> puntuacionControles, List<RegistrosUsuario> registrosUsuarios) {
        List<ResultadoUsuario> resultados = new ArrayList<>();

        if(registrosUsuarios.size() > 0) {
            // Sets para calcular los puestos de los tiempos parciales y totales
            List<SortedSet<Long>> setsParciales = new ArrayList<>();
            for(int i = 0; i < trazado.length - 1; i++) setsParciales.add(new TreeSet<>());
            List<SortedSet<Long>> setsAcumulados = new ArrayList<>();
            for(int i = 0; i < trazado.length - 1; i++) setsAcumulados.add(new TreeSet<>());
            // Calcula los parciales de cada usuario
            for(RegistrosUsuario ru : registrosUsuarios) {
                Usuario corredor = ru.getUsuario();
                ResultadoUsuario.Tipo tipo = ResultadoUsuario.Tipo.OK;
                long tAcum = 0;
                List<ParcialUsuario> parciales = new ArrayList<>();
                Map<String, Integer> puntosRegistrados = new HashMap<>();
                if(modalidad == Carrera.Modalidad.LINEA) {
                    // LINEA
                    if(ru.getRegistros().size() < trazado.length) tipo = ResultadoUsuario.Tipo.PENDIENTE;
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
                            // Añade a los set correspondientes
                            setsParciales.get(i-1).add(tParcial);
                            setsAcumulados.get(i-1).add(tAcum);
                        } else {
                            tipo = ResultadoUsuario.Tipo.ABANDONADO;
                        }
                    }
                } else {
                    // SCORE
                    boolean pendiente = true;
                    boolean abandonado = false;
                    String controlMeta = trazado[trazado.length - 1]; // El último control es la meta
                    for(RegistroResponse r : ru.getRegistros()) {
                        String codigoControl = r.getControl();
                        Date fechaRegistro = r.getFecha();
                        if(fechaRegistro != null) {
                            Integer puntuacion = puntuacionControles.get(codigoControl);
                            if(puntuacion != null) {
                                puntosRegistrados.put(codigoControl, puntuacion);
                                if(codigoControl.contentEquals(controlMeta)) pendiente = false;
                            } else {
                                // No debería ocurrir
                            }
                        } else {
                            // Recorrido abandonado
                            abandonado = true;
                        }
                    }

                    if(abandonado) tipo = ResultadoUsuario.Tipo.ABANDONADO;
                    else if(pendiente) tipo = ResultadoUsuario.Tipo.PENDIENTE;
                    tAcum = ru.getRegistros().get(ru.getRegistros().size() - 1).getFecha().getTime() / 1000
                            - ru.getRegistros().get(0).getFecha().getTime() / 1000;
                }

                ResultadoUsuario res = new ResultadoUsuario(corredor.getId(), String.format("%s\n%s", corredor.getNombre(), corredor.getClub()), tAcum, tipo);
                res.setParciales(parciales);
                res.setPuntosRegistrados(puntosRegistrados);
                resultados.add(res);
            }

            if(modalidad == Carrera.Modalidad.LINEA) {
                // Ordena lista por tiempo total y tipo
                Collections.sort(resultados, new Comparator<ResultadoUsuario>() {
                    @Override
                    public int compare(ResultadoUsuario u1, ResultadoUsuario u2) {
                        int c = u1.getTipo().compareTo(u2.getTipo());
                        if(c == 0) {
                            // Mismo tipo
                            switch (u1.getTipo()) {
                                case OK:
                                    // Ordenados por tiempo ascendente
                                    c = (int) (u1.getTiempoTotal() - u2.getTiempoTotal());
                                    break;
                                case PENDIENTE:
                                case ABANDONADO:
                                default:
                                    // Ordenados según número de controles completados: más controles -> antes
                                    c = u1.getParciales().size() - u2.getParciales().size();
                                    break;
                            }
                        }

                        return c;
                    }
                });
                // Convierte los sets a listas ordenadas
                List<List<Long>> parcialesOrdenados = new ArrayList<>();
                for(SortedSet<Long> set : setsParciales) parcialesOrdenados.add(new ArrayList<>(set));
                List<List<Long>> acumuladosOrdenados = new ArrayList<>();
                for(SortedSet<Long> set : setsAcumulados) acumuladosOrdenados.add(new ArrayList<>(set));
                // Actualiza posiciones y diferencias
                long tiempoGanador = resultados.get(0).getTiempoTotal();
                for(ResultadoUsuario ru : resultados) {
                    int i = 0;
                    for(ParcialUsuario p : ru.getParciales()) {
                        p.setPosicionParcial(parcialesOrdenados.get(i).indexOf(p.getTiempoParcial()) + 1);
                        p.setPosicionAcumulada(acumuladosOrdenados.get(i).indexOf(p.getTiempoAcumulado()) + 1);
                        i++;
                    }
                    ru.setPosicion(ru.getParciales().get(ru.getParciales().size() - 1).getPosicionAcumulada());
                    ru.setDiferenciaGanador(ru.getTiempoTotal() - tiempoGanador);
                }
            } else {
                // Ordena lista por puntuación y tiempo
                Collections.sort(resultados, new Comparator<ResultadoUsuario>() {
                    @Override
                    public int compare(ResultadoUsuario u1, ResultadoUsuario u2) {
                        int c = u1.getTipo().compareTo(u2.getTipo());

                        if(c == 0) {
                            // Mismo tipo. Entre el mismo tipo se usa el mismo criterio (puntuación, tiempo)
                            // Ordenados por puntuación descendente
                            c = u2.getPuntuacion() - u1.getPuntuacion();
                            if(c == 0) {
                                // Misma puntuación, se ordena por tiempo
                                c = (int) (u1.getTiempoTotal() - u2.getTiempoTotal());
                            }
                        }

                        return c;
                    }
                });
                // Actualiza posiciones
                // TODO Tener en cuenta empates de puntuación y tiempo
                int posicion = 1;
                for(ResultadoUsuario ru : resultados) {
                    ru.setPosicion(posicion++);
                }
            }
        }

        return resultados;
    }

    private void generaTablaResultados(Carrera.Modalidad modalidad, String[] trazado, List<ResultadoUsuario> resultados) {
        tablaResultados.removeAllViews();

        if(resultados.size() > 0) {
            // Cabecera
            TableRow cabecera;
            if(modalidad == Carrera.Modalidad.LINEA) {
                // LINEA
                cabecera = (TableRow) getLayoutInflater().inflate(R.layout.item_resultados_linea, null);
                TextView tvCabeceraDiferencia = cabecera.findViewById(R.id.item_resultados_diferencia);
                tvCabeceraDiferencia.setText(R.string.resultados_diferencia);
            } else {
                // SCORE
                cabecera = (TableRow) getLayoutInflater().inflate(R.layout.item_resultados_score, null);
                TextView tvCabeceraPuntuacion = cabecera.findViewById(R.id.item_resultados_puntuacion);
                tvCabeceraPuntuacion.setText(R.string.resultados_puntuacion);
            }
            TextView tvCabeceraPosicion = cabecera.findViewById(R.id.item_resultados_posicion);
            TextView tvCabeceraNombreClub = cabecera.findViewById(R.id.item_resultados_nombre_club);
            TextView tvCabeceraTiempoTotal = cabecera.findViewById(R.id.item_resultados_tiempo_total);
            tvCabeceraPosicion.setText(R.string.resultados_posicion);
            tvCabeceraNombreClub.setText(R.string.resultados_nombre_club);
            tvCabeceraTiempoTotal.setText(R.string.resultados_tiempo_total);
            // Trazado de la carrera
            TableRow.LayoutParams lpTvTrazado = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.MATCH_PARENT);
            float tamLetra = (float) getResources().getDimension(R.dimen.tamLetraItemResultados) / getResources().getDisplayMetrics().density;
            for(int i = 0; i < trazado.length - 1; i++) {
                String text;
                if(modalidad == Carrera.Modalidad.LINEA) {
                    // LINEA (tramos)
                    if(i == 0) {
                        // Salida -> Primer control
                        text = String.format("S-1 (%s)", trazado[i+1]);
                    } else if(i == trazado.length - 2) {
                        // Penúltimo control -> Meta
                        text = String.format("%d-M", trazado.length - 1);
                    } else {
                        text = String.format("%d-%d (%s)", i, i+1, trazado[i+1]);
                    }
                } else {
                    // SCORE (solo control)
                    text = trazado[i+1];
                }

                TextView tvTrazado = new TextView(this);
                tvTrazado.setLayoutParams(lpTvTrazado);
                tvTrazado.setGravity(Gravity.CENTER);
                tvTrazado.setPadding(15, 0, 15, 0);
                tvTrazado.setText(text);
                tvTrazado.setTextColor(Color.BLACK);
                tvTrazado.setTextSize(TypedValue.COMPLEX_UNIT_SP, tamLetra);
                cabecera.addView(tvTrazado);
            }
            tablaResultados.addView(cabecera);

            // Resultados de corredores
            int pos = 1;
            for(ResultadoUsuario r : resultados) {
                TableRow row;
                TextView tvDiferencia = null;
                TextView tvPuntuacion = null;
                if(modalidad == Carrera.Modalidad.LINEA) {
                    // LINEA
                    row = (TableRow) getLayoutInflater().inflate(R.layout.item_resultados_linea, null);
                    tvDiferencia = row.findViewById(R.id.item_resultados_diferencia);
                } else {
                    // SCORE
                    row = (TableRow) getLayoutInflater().inflate(R.layout.item_resultados_score, null);
                    tvPuntuacion = row.findViewById(R.id.item_resultados_puntuacion);
                }
                TextView tvPosicion = row.findViewById(R.id.item_resultados_posicion);
                TextView tvNombreClub = row.findViewById(R.id.item_resultados_nombre_club);
                TextView tvTiempoTotal = row.findViewById(R.id.item_resultados_tiempo_total);
                // Asigna valores
                tvNombreClub.setText(r.getNombreClub().trim());
                tvNombreClub.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Al pulsar en un corredor se visualiza su perfil
                        Intent i = new Intent(ResultadosActivity.this, PerfilActivity.class);
                        i.putExtra(Constants.EXTRA_ID_USUARIO, r.getIdUsuario());
                        startActivity(i);
                    }
                });
                String sTiempoTotal;
                String sPosicion = "";
                switch (r.getTipo()) {
                    case OK:
                        sPosicion = String.format("%d", r.getPosicion());
                        sTiempoTotal = Utils.getTiempoResultadoFromSecs(r.getTiempoTotal());
                        if(tvDiferencia != null && r.getDiferenciaGanador() > 0) tvDiferencia.setText(String.format("+%s", Utils.getTiempoResultadoFromSecs(r.getDiferenciaGanador())));
                        if(tvPuntuacion != null) tvPuntuacion.setText(String.format("%d", r.getPuntuacion()));
                        break;
                    case PENDIENTE:
                        sTiempoTotal = getString(R.string.resultados_pendiente);
                        if(tvPuntuacion != null) tvPuntuacion.setText(String.format("%d", r.getPuntuacion()));
                        break;
                    case ABANDONADO:
                    default:
                        sTiempoTotal = getString(R.string.resultados_abandonado);
                }
                tvPosicion.setText(sPosicion);
                tvTiempoTotal.setText(sTiempoTotal);

                if(modalidad == Carrera.Modalidad.LINEA) {
                    // LINEA -> Parciales
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
                        // Estilo especial para los tiempos ganadores
                        if(p.getPosicionParcial() == 1) {
                            configuraTextViewTiempoGanador(tvTiempoParcial);
                            configuraTextViewTiempoGanador(tvPosicionParcial);
                        }
                        if(p.getPosicionAcumulada() == 1) {
                            configuraTextViewTiempoGanador(tvTiempoAcumulado);
                            configuraTextViewTiempoGanador(tvPosicionAcumulada);
                        }
                        row.addView(llParcial);
                    }
                } else {
                    // SCORE -> Indica si el control ha sido registrado o no
                    for(int i = 1; i < trazado.length; i++) {
                        // Se omite el control de salida
                        TextView tvControl = new TextView(this);
                        tvControl.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                        tvControl.setText((r.getPuntosRegistrados().containsKey(trazado[i]) ? "X" : ""));
                        tvControl.setGravity(Gravity.CENTER);
                        tvControl.setTextSize(20);
                        tvControl.setTextColor(getResources().getColor(R.color.colorPrimary));
                        tvControl.setTypeface(tvControl.getTypeface(), Typeface.BOLD);
                        row.addView(tvControl);
                    }
                }

                tablaResultados.addView(row, pos++);
            }
        } else {
            // Muestra el mensaje de no hay resultados
            tvSinResultados.setVisibility(View.VISIBLE);
        }
    }

    private void configuraTextViewTiempoGanador(TextView tv) {
        tv.setTextColor(Color.RED);
        tv.setTypeface(tv.getTypeface(), Typeface.BOLD);
    }

}