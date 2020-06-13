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

  private _controles = new BehaviorSubject<Record<string, Control>>({});
  controles = this._controles.asObservable();

  private _nuevoControl = new BehaviorSubject<Control>(null);
  nuevoControl = this._nuevoControl.asObservable();

  constructor() { }

  /**
   * Cambia el archivo de mapa base.
   * @param mapa Archivo de mapa
   */
  cambiarMapaBase(mapa: any) {
    this._mapaBase.next(mapa);
  }

  actualizaControles(controles: Record<string, Control>) {
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

  anadirControl(nControl) {
    this._nuevoControl.next(nControl);
  }
}
