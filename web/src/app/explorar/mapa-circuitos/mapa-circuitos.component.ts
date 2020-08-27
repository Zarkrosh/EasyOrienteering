import { Component, OnInit } from '@angular/core';
import { ClienteApiService } from 'src/app/shared/cliente-api.service';
import { Carrera } from 'src/app/shared/app.model';
import { AlertService } from 'src/app/alert';
import { DatePipe } from '@angular/common';
import * as L from 'leaflet';

@Component({
  selector: 'app-mapa-circuitos',
  templateUrl: './mapa-circuitos.component.html',
  styleUrls: ['./mapa-circuitos.component.scss']
})
export class MapaCircuitosComponent implements OnInit {

  readonly LATITUD_DEFECTO = 40.325479;
  readonly LONGITUD_DEFECTO = -3.757923;
  readonly ZOOM_DEFECTO = 6;
  readonly MAX_SIZE = 2147483647;

  mapa: L.Map;
  iconoMarcador: L.Icon;
  capaMarcadores: L.LayerGroup;

  // Alertas
  alertOptions = {
    autoClose: true,
    keepAfterRouteChange: true
  };

  constructor(
    protected alertService: AlertService,
    private clienteApi: ClienteApiService,
    private datepipe: DatePipe) { }

  ngOnInit() {
    console.log(this.mapa);
    let latCentro = this.LATITUD_DEFECTO;
    let lonCentro = this.LONGITUD_DEFECTO;
    let zoomCentro = this.ZOOM_DEFECTO;

    this.mapa = L.map('mapa-circuitos', {
      center: [ latCentro,  lonCentro],
      zoom: zoomCentro
    });

    // Tiles
    const tiles = L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      maxZoom: 19,
      attribution: '&copy; <a href="http://www.openstreetmap.org/copyright">OpenStreetMap</a>'
    });
    tiles.addTo(this.mapa);

    // Capa de marcadores
    this.capaMarcadores = new L.LayerGroup();
    this.capaMarcadores.addTo(this.mapa);
     // Icono personalizado
    this.iconoMarcador = L.icon({
      iconUrl: 'assets/img/marcador.png',
        iconSize:     [40, 48],
        iconAnchor:   [20, 47],
        popupAnchor:  [0, 0]
    });

    // Carga los circuitos
    this.clienteApi.buscaCarreras("", Carrera.TIPO_CIRCUITO, "", 0, this.MAX_SIZE).subscribe(
      resp => {
        if(resp.status === 200) {
          this.cargaMarcadores(resp.body);
        } else {
          this.alertService.error("Respuesta inesperada", this.alertOptions);
          console.log(resp);
        }
      }, err => {
        let mensaje = "Se produjo un error";
        if(typeof err.error === 'string') mensaje += ": " + err.error;
        this.alertService.error(mensaje, this.alertOptions);
        console.log(err);
      }
    );
  }


  /**
   * Crea los marcadores de las carreras en el mapa.
   * @param circuitos Lista de circuitos
   */
  cargaMarcadores(circuitos: Carrera[]): void {
    this.capaMarcadores.clearLayers();
    
    for (let circuito of circuitos) {
      let marcador = L.marker([circuito.latitud, circuito.longitud], {icon: this.iconoMarcador});
      //marcador.on("click", (evt) => muestraInfoCarrera);
      // Popup
      let popup =  `<div class="info-popup">
                      <h4>${circuito.nombre}</h4>
                      <table>
                        <tr>
                          <td>
                            <span>${this.datepipe.transform(circuito.fecha, 'dd-MM-yyyy')}</span>
                          </td>
                          <td>
                            <span class="info-popup-modalidad">${circuito.modalidad}</span>
                          </td>
                        </tr>
                      </table>
                      <span>3 recorridos con 42 participantes</span>
                      <a href="/carreras/${circuito.id}" target="_blank"><button class="btn btn-primary">Más información</button></a>
                    </div>`;
      marcador.bindPopup(popup);
      this.capaMarcadores.addLayer(marcador);
    }
  }

}
