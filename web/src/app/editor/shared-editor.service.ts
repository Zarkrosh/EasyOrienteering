import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { Recorrido, Control, Coordenadas, Pair } from '../shared/app.model';

@Injectable({
  providedIn: 'root'
})
export class SharedEditorService {
  private _mapaBase = new BehaviorSubject<File>(null);
  mapaBase = this._mapaBase.asObservable();

  private _recorridoActual = new BehaviorSubject<Recorrido>(null);
  recorridoActual = this._recorridoActual.asObservable();

  private _controles = new BehaviorSubject<Map<string, Control>>(new Map());
  controles = this._controles.asObservable();

  private _nuevoControl = new BehaviorSubject<Control>(null);
  nuevoControl = this._nuevoControl.asObservable();
  
  private _controlBorrado = new BehaviorSubject<Control>(null);
  controlBorrado = this._controlBorrado.asObservable();
  // Confirmación de control borrado
  private _controlBorradoConf = new BehaviorSubject<number>(null);
  controlBorradoConf = this._controlBorradoConf.asObservable();

  constructor() { }

  /**
   * Cambia el archivo de mapa base.
   * @param mapa Archivo de mapa
   */
  cambiarMapaBase(mapa: any) {
    this._mapaBase.next(mapa);
  }

  actualizaControles(controles: Map<string, Control>) {
    this._controles.next(controles);
  }

  /**
   * Selecciona uno de los recorridos. Puede ser null para indicar
   * que se muestren todos los controles.
   * @param recorrido Nuevo recorrido o null
   */
  actualizaRecorrido(recorrido: Recorrido) {
    this._recorridoActual.next(recorrido);
  }

  /**
   * Notifica la adición de un control.
   * @param nControl Nuevo control
   */
  anadirControl(nControl) {
    this._nuevoControl.next(nControl);
  }
  
  /**
   * Notifica el borrado de un control del recorrido.
   * @param control Control a borrar
   */
  borrarControl(control: Control) {
    this._controlBorrado.next(control);
  }

  /**
   * Confirma el borrado de un control del tipo especificado.
   * @param tipo Tipo de control
   */
  confirmaBorrado(tipo) {
    this._controlBorradoConf.next(tipo);
  }
}
