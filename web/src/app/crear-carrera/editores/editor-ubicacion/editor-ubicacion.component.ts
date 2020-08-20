import { Component, OnInit } from '@angular/core';
import * as L from 'leaflet';

@Component({
  selector: 'app-editor-ubicacion',
  templateUrl: './editor-ubicacion.component.html',
  styleUrls: ['./editor-ubicacion.component.scss']
})
export class EditorUbicacionComponent implements OnInit {

  readonly LATITUD_DEFECTO = 40.463667;
  readonly LONGITUD_DEFECTO = -3.74922;
  readonly ZOOM_DEFECTO = 5;
  readonly MIN_ZOOM_MARCAR = 16;

  mapaUbicacion: L.Map;
  capaMarcador: L.LayerGroup;
  iconoMarcador: L.Icon;
  editandoUbicacion: boolean;
  mensajeUbicacion: string;
  marcarUbicacion: boolean;

  latitudTemporal: number;
  longitudTemporal: number;
  latitudElegida: number;
  longitudElegida: number;

  constructor() {
    this.editandoUbicacion = this.marcarUbicacion = false;
    this.mensajeUbicacion = "";
  }

  ngOnInit() {
    this.iniciaMapa();
  }

  iniciaMapa(): void {
    let latCentro = this.LATITUD_DEFECTO;
    let lonCentro = this.LONGITUD_DEFECTO;
    let zoomCentro = this.ZOOM_DEFECTO;

    this.mapaUbicacion = L.map('mapa-ubicacion', {
      center: [ latCentro,  lonCentro],
      zoom: zoomCentro
    });

    // Tiles
    const tiles = L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      maxZoom: 19,
      attribution: '&copy; <a href="http://www.openstreetmap.org/copyright">OpenStreetMap</a>'
    });
    tiles.addTo(this.mapaUbicacion);

    // Capa del marcador
    this.capaMarcador = new L.LayerGroup();
    this.capaMarcador.addTo(this.mapaUbicacion);
    // Icono personalizado
    this.iconoMarcador = L.icon({
        iconUrl: 'assets/img/marcador.png',
        iconSize:     [48, 56],
        iconAnchor:   [24, 55], // point of the icon which will correspond to marker's location
    });

    // Eventos
    this.mapaUbicacion.on("zoomend", (evt) => this.onCambioZoomMapa(evt.target));
    this.mapaUbicacion.on("click", (evt: L.LeafletMouseEvent) => this.manejaClickMapa(evt));
  }

  editarUbicacion(event): void {
    event.stopPropagation();
    this.latitudTemporal = this.latitudElegida;
    this.longitudTemporal = this.longitudElegida;
    this.marcarUbicacion = false;
    this.editandoUbicacion = true;
    this.onCambioZoomMapa(this.mapaUbicacion);
  }

  guardarUbicacion(): void {
    /* TODO Si está editando
    if(this.carrera.id != null && this.latitudElegida != null && this.longitudElegida != null) {
      this.clienteApi.cambiaUbicacionCarrera(this.carrera.id, this.latitudElegida, this.longitudElegida).subscribe(
        resp => {
          if(resp.status == 200) {
            this.alertService.success("Ubicación cambiada", this.optionsAlerts);
          } else {
            this.alertService.warn("Código de respuesta inesperado: " + resp.status, this.optionsAlerts);
          }
          this.editandoUbicacion = this.marcarUbicacion = this.guardandoUbicacion = false;
        }, err => {
          this.alertService.error("No se pudo cambiar la ubicación", this.optionsAlerts);
          console.log(err); // DEBUG
          this.guardandoUbicacion = false;
      });
    }*/

    this.latitudElegida = this.latitudTemporal;
    this.longitudElegida = this.longitudTemporal;
    this.finalizarEdicion();
  }

  manejaClickMapa(evento: L.LeafletMouseEvent) {
    if(this.editandoUbicacion) {
      if(this.mapaUbicacion.getZoom() >= this.MIN_ZOOM_MARCAR) {
        this.setMarcadorUbicacion(evento.latlng.lat, evento.latlng.lng);
      } else {
        // Agita el mensaje de ubicación
        // TODO
      }
    }
  }

  onCambioZoomMapa(map: L.Map): void {
    if(this.editandoUbicacion) {
      if(map.getZoom() >= this.MIN_ZOOM_MARCAR) {
        if(this.latitudTemporal == null && this.longitudTemporal == null) {
          this.mensajeUbicacion = "Haz click en el mapa";
        } else {
          this.mensajeUbicacion = null;
        }
        this.marcarUbicacion = true;
      } else {
        this.mensajeUbicacion = "Aumenta el zoom para elegir ubicación";
        this.marcarUbicacion = false;
      }
    }
  }

  setMarcadorUbicacion(latitud: number, longitud: number) {
    this.latitudTemporal = latitud;
    this.longitudTemporal = longitud;
    // Borra posible marcador anterior y añade el nuevo
    this.capaMarcador.clearLayers();
    let marcador = L.marker([this.latitudTemporal, this.longitudTemporal], {icon: this.iconoMarcador});
    marcador.on("click", (evt) => this.animacionVueloMarcador());
    this.capaMarcador.addLayer(marcador);
  }

  animacionVueloMarcador() {
    this.mapaUbicacion.flyTo([this.latitudTemporal, this.longitudTemporal], this.MIN_ZOOM_MARCAR, {
        animate: true,
        duration: 1
    });
  }

  cancelarEdicion() {
    if(this.latitudElegida) {
      // Recoloca en las coordenadas anteriores
      this.setMarcadorUbicacion(this.latitudElegida, this.longitudElegida);
    } else {
      // Borra marcador
      this.capaMarcador.clearLayers();
    }

    this.finalizarEdicion();
  }

  finalizarEdicion() {
    this.editandoUbicacion = this.marcarUbicacion = false;
  }

}
