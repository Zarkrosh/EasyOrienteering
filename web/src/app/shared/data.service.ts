import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class DataService {

  private _mapasTrazados = new BehaviorSubject<Map<string, string>>(new Map());
  mapasTrazados = this._mapasTrazados.asObservable();
  /*getValueMapasTrazados(): Map<string, string> {
    return this._mapasTrazados.getValue();
  }*/

  constructor() { }

  setMapasTrazados(value: Map<string, string>): void {
    this._mapasTrazados.next(value);
  }
}
