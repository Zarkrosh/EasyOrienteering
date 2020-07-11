package com.hergomsoft.easyorienteering.ui.scan_inicial;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.hergomsoft.easyorienteering.data.model.pagers.RegistroControl;
import com.hergomsoft.easyorienteering.data.repositories.RegistroControlRepository;
import com.hergomsoft.easyorienteering.util.Utils;

import static android.Manifest.permission.CAMERA;

public class ScanInicialViewModel extends ViewModel {

    // Permisos que utiliza la actividad
    private String[] permisosNecesarios = new String[]{ CAMERA };

    // Datos de triángulo escaneado
    private String codigo;
    private Long idCarrera;
    private Long idRecorrido;
    private String secreto;

    private RegistroControlRepository registroRepository;

    private LiveData<RegistroControl> peticionConfirmacion;

    public ScanInicialViewModel(RegistroControlRepository registroRepository) {
        this.registroRepository = registroRepository;
        peticionConfirmacion = this.registroRepository.getRegistroResponse();
    }

    public LiveData<RegistroControl> getResultadoConfirmacion() {
        return peticionConfirmacion;
    }

    /**
     * Realiza una petición de confirmación de recorrido al servidor.
     */
    public void confirmaInicioRecorrido() {
        registroRepository.registraControl(codigo, idCarrera, idRecorrido, secreto);
    }

    /**
     * Actualiza los datos de inicio de recorrido.
     * @param escaneado Datos del triángulo escaneado
     * @return True si se han actualizado correctamente, false si ha ocurrido algún error
     */
    public boolean actualizaDatosEscaneado(String escaneado) {
        codigo = Utils.getCodigoControlEscaneado(escaneado);
        idCarrera = Utils.getIdentificadorCarreraEscaneado(escaneado);
        idRecorrido = Utils.getIdentificadorRecorridoEscaneado(escaneado);
        secreto = Utils.getSecretoControlEscaneado(escaneado);

        return codigo != null && idCarrera != null && idRecorrido != null && secreto != null;
    }

    /**
     * Devuelve la lista de permisos necesarios para la actividad.
     * @return Permisos
     */
    public String[] getPermisosNecesarios() {
        return permisosNecesarios;
    }

}
