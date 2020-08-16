import { Component, OnInit, ElementRef } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { ClienteApiService } from '../shared/cliente-api.service';
import { AlertService } from '../alert';
import { Carrera, Control, AppSettings, Usuario } from '../shared/app.model';
import * as QRCode from 'easyqrcodejs';
import * as L from 'leaflet';

@Component({
  selector: 'app-vista-carrera',
  templateUrl: './vista-carrera.component.html',
  styleUrls: ['./vista-carrera.component.scss']
})
export class VistaCarreraComponent implements OnInit {

  // Alertas
  optionsAlerts = {
    autoClose: true,
    keepAfterRouteChange: false
  };

  carrera: Carrera;
  controles: Control[];
  secretos: Map<string, string>;
  errorCarga: boolean;

  // Ubicación
  readonly LATITUD_DEFECTO = 40.463667;
  readonly LONGITUD_DEFECTO = -3.74922;
  readonly ZOOM_DEFECTO = 5;
  readonly MIN_ZOOM_MARCAR = 16;
  mapaUbicacion: L.Map;
  capaMarcador: L.LayerGroup;
  iconoMarcador: L.Icon;
  editandoUbicacion: boolean;
  mostrarEditUbicacion: boolean;
  mostrarAnadirUbicacion: boolean;
  mensajeUbicacion: string;
  marcarUbicacion: boolean;
  guardandoUbicacion: boolean;
  latitudElegida: number;
  longitudElegida: number;

  constructor(private route: ActivatedRoute,
    private router: Router,
    private clienteApi: ClienteApiService,
    protected alertService: AlertService) { }

  ngOnInit() {
    this.carrera = null;
    this.controles = [];
    this.secretos = new Map<string, string>();
    this.errorCarga = false;
    this.editandoUbicacion = false;
    this.mostrarEditUbicacion = false;
    this.mostrarAnadirUbicacion = false;
    this.guardandoUbicacion = false;
    this.mensajeUbicacion = null;

    this.cargaDatosCarrera();
  }

  guardarUbicacion() {
    this.guardandoUbicacion = true;
    if(this.carrera.id != null && this.latitudElegida != null && this.longitudElegida != null) {
      this.clienteApi.cambiaUbicacionCarrera(this.carrera.id, this.latitudElegida, this.longitudElegida).subscribe(
        resp => {
          if(resp.status == 200) {
            this.alertService.success("Ubicación cambiada", this.optionsAlerts);
          } else {
            this.alertService.warn("Código de respuesta inesperado: " + resp.status, this.optionsAlerts);
          }
          this.editandoUbicacion = this.marcarUbicacion = this.guardandoUbicacion = false;
          this.mostrarEditUbicacion = true; // TODO Solo para el organizador
        }, err => {
          this.alertService.error("No se pudo cambiar la ubicación", this.optionsAlerts);
          console.log(err); // DEBUG
          this.guardandoUbicacion = false;
      });
    }
  }

  onCambioZoomMapa(map: L.Map): void {
    if(this.editandoUbicacion) {
      if(map.getZoom() >= this.MIN_ZOOM_MARCAR) {
        if(this.latitudElegida == null && this.longitudElegida == null) {
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

  iniciaMapa(): void {
    let latCentro = this.LATITUD_DEFECTO;
    let lonCentro = this.LONGITUD_DEFECTO;
    let zoomCentro = this.ZOOM_DEFECTO;
    if(this.carrera.latitud != null && this.carrera.longitud != null) {
      latCentro = this.carrera.latitud;
      lonCentro = this.carrera.longitud;
      zoomCentro = 16;
      this.mostrarEditUbicacion = true; // TODO Solo mostrar para el organizador de la carrera
    }

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

    // Muestra marcador si existen coordenadas
    if(this.carrera.latitud != null && this.carrera.longitud != null) {
      this.setMarcadorUbicacion(this.carrera.latitud, this.carrera.longitud);
    }
  }

  cargaDatosCarrera(): void {
    this.route.params.subscribe(routeParams => {
      let idCarrera = +routeParams['id'];
      // Obtiene datos generales de la carrera
      this.clienteApi.getCarrera(idCarrera).subscribe(
        resp => {
          if(resp.status == 200) {
            // TODO
            console.log(resp.body);
            this.carrera = resp.body;
            this.controles = [];
            for(let control of Object.entries(this.carrera.controles)) {
              if(control[1].tipo != Control.TIPO_SALIDA) this.controles.push(control[1]);
            }

            // Triángulos de recorridos
            for(let recorrido of this.carrera.recorridos) {
              if(recorrido.trazado.length > 0) {
                this.controles.push(new Control(recorrido.nombre, null, null));
              }
            }

            // DEBUG: muestra los controles QR
            this.clienteApi.getSecretosControlesCarrera(idCarrera).subscribe(
              resp => {
                if(resp.status == 200) {
                  // Renderiza la lista
                  this.secretos = new Map(Object.entries(resp.body));

                  for(let control of this.controles) {
                    if(control.tipo !== Control.TIPO_SALIDA && control.tipo) {
                      let options = {
                        text: control.codigo + AppSettings.SEPARADOR_QR + this.secretos.get(control.codigo),
                        width: 200,
                        height: 200,
                        quietZone: 20,
                        quietZoneColor: 'transparent',
                      }
                      
                      new QRCode(document.getElementById("QR-" + control.codigo), options);
                    }
                  }

                  for(let recorrido of this.carrera.recorridos) {
                    if(recorrido.trazado.length > 0) {
                      let codigoTriangulo = recorrido.trazado[0];
                      let secretoTriangulo = this.secretos.get(codigoTriangulo);
                      if(secretoTriangulo) {
                        let options = {
                          text: codigoTriangulo + AppSettings.SEPARADOR_QR + this.carrera.id + AppSettings.SEPARADOR_QR + recorrido.id + AppSettings.SEPARADOR_QR + secretoTriangulo,
                          width: 200,
                          height: 200,
                          quietZone: 20,
                          quietZoneColor: 'transparent',
                        }
                        
                        new QRCode(document.getElementById("QR-" + recorrido.nombre), options);
                      }
                    }
                  }

                  this.iniciaMapa();
                  if(this.carrera.latitud == null || this.carrera.longitud == null) {
                    this.mostrarAnadirUbicacion = true; // TODO Solo para el organizador
                  }
                }
              }, err => {
                this.alertService.error("Error al obtener los secretos: " + JSON.stringify(err), this.optionsAlerts);
                this.errorCarga = true;
              }
            );
          } else {
            this.alertService.error("Error al obtener la carrera", this.optionsAlerts);
            this.errorCarga = true;
          }
        }, err => {
          if(err.status == 404) {
            this.alertService.error("No existe ninguna carrera con ese ID", this.optionsAlerts);
          } else if(err.status == 504) {
            this.alertService.error("No hay conexión con el servidor. Espera un momento y vuelve a intentarlo.", this.optionsAlerts);
          } else {
            this.alertService.error("Error al obtener la carrera: " + JSON.stringify(err), this.optionsAlerts);
          }
          this.errorCarga = true;
        }
      );
    })
  }
  

  // TODO Convertir a pure pipe para mejorar rendimiento
  getOrganizador(): string {
    let org: Usuario = this.carrera.organizador;
    let res: string = org.nombre;
    if(org.club.length > 0) res += " (" + org.club + ")"; 
    return res;
  }

  editarCarrera(): void {
    this.router.navigate(['/carreras', this.carrera.id]); // TODO
  }


  editarUbicacion(): void {
    this.latitudElegida = (this.carrera.latitud != null) ? this.carrera.latitud : null;
    this.longitudElegida = (this.carrera.longitud != null) ? this.carrera.longitud : null;
    this.mostrarEditUbicacion = this.mostrarAnadirUbicacion = this.guardandoUbicacion = this.marcarUbicacion = false;
    this.editandoUbicacion = true;
    this.onCambioZoomMapa(this.mapaUbicacion);
  }

  manejaClickMapa(evento: L.LeafletMouseEvent) {
    if(this.editandoUbicacion) {
      this.setMarcadorUbicacion(evento.latlng.lat, evento.latlng.lng);
    }
  }

  setMarcadorUbicacion(latitud: number, longitud: number) {
    if(this.mapaUbicacion.getZoom() >= this.MIN_ZOOM_MARCAR) {
      this.latitudElegida = latitud;
      this.longitudElegida = longitud;
      // Borra posible marcador anterior y añade el nuevo
      this.capaMarcador.clearLayers();
      let marcador = L.marker([this.latitudElegida, this.longitudElegida], {icon: this.iconoMarcador});
      marcador.on("click", (evt) => this.animacionVueloMarcador());
      this.capaMarcador.addLayer(marcador);
    } else {
      // Agita el mensaje de ubicación
      // TODO
    }
  }

  animacionVueloMarcador() {
    this.mapaUbicacion.flyTo([this.latitudElegida, this.longitudElegida], this.MIN_ZOOM_MARCAR, {
        animate: true,
        duration: 1
    });
  }

  refresh(): void {
    this.errorCarga = false;
    this.cargaDatosCarrera();
  }

}
