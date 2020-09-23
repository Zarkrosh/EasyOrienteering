import { Component, OnInit, ElementRef, ViewChild } from '@angular/core';
import * as L from 'leaflet';
import { NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';

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

  // Modal
  @ViewChild('modalBorrarUbicacion', {static: true}) modalBorrarUbicacion: ElementRef<NgbModal>;
  activeModal: NgbModalRef;

  constructor(private modalService: NgbModal) {
    this.latitudElegida = this.longitudElegida = this.latitudTemporal = this.longitudTemporal = null;
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
      zoom: zoomCentro,
      scrollWheelZoom: false
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
    this.mapaUbicacion.scrollWheelZoom.enable();
    event.stopPropagation();
    this.latitudTemporal = this.latitudElegida;
    this.longitudTemporal = this.longitudElegida;
    this.marcarUbicacion = false;
    this.editandoUbicacion = true;
    this.onCambioZoomMapa(this.mapaUbicacion);
  }

  guardarUbicacion(): void {
    this.latitudElegida = this.latitudTemporal;
    this.longitudElegida = this.longitudTemporal;
    this.finalizarEdicion();
  }

  manejaClickMapa(evento: L.LeafletMouseEvent) {
    if(this.editandoUbicacion) {
      if(this.mapaUbicacion.getZoom() >= this.MIN_ZOOM_MARCAR) {
        this.setMarcadorUbicacion(evento.latlng.lat, evento.latlng.lng);
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
    this.mapaUbicacion.scrollWheelZoom.enable();
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

  borrarUbicacion(): void {
    this.activeModal = this.modalService.open(this.modalBorrarUbicacion, {centered: true, size: 'lg'});
  }

  confirmaBorrarUbicacion(): void {
    this.activeModal.close();
    this.latitudElegida = this.longitudElegida = this.latitudTemporal = this.longitudTemporal = null;
    this.capaMarcador.clearLayers();
  }

}
