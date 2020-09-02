import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { Control, Recorrido } from './app.model';

@Injectable({
  providedIn: 'root'
})
export class DataService {
  
  private _mapaBase = new BehaviorSubject<string>("");
  mapaBase = this._mapaBase.asObservable();

  private _mapasTrazados = new BehaviorSubject<Map<string, string>>(new Map());
  mapasTrazados = this._mapasTrazados.asObservable();


  constructor() { }

  setMapaBase(value: string): void {
    this._mapaBase.next(value);
  }

  setMapasTrazados(value: Map<string, string>): void {
    this._mapasTrazados.next(value);
  }

}
