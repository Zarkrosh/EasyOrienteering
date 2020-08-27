import { Component, OnInit, ElementRef, ViewChildren, QueryList } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { ClienteApiService } from '../shared/cliente-api.service';
import { AlertService } from '../alert';
import { Carrera, Usuario } from '../shared/app.model';
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
  errorCarga: boolean;

  // Ubicación
  @ViewChildren('mapaUbicacion') queryMapa: QueryList<ElementRef>;
  mapaUbicacion: L.Map;
  capaMarcador: L.LayerGroup;
  iconoMarcador: L.Icon;
  readonly NIVEL_ZOOM = 14;

  constructor(private route: ActivatedRoute,
    private router: Router,
    private clienteApi: ClienteApiService,
    protected alertService: AlertService) { }

  ngOnInit() {
    this.carrera = null;
    this.errorCarga = false;

    this.cargaDatosCarrera();
  }

  ngAfterViewInit() {
    this.queryMapa.changes.subscribe((items: Array<any>) => {
      if(!this.mapaUbicacion) this.iniciaMapa();
    });
  }

  cargaDatosCarrera(): void {
    this.route.params.subscribe(routeParams => {
      let idCarrera = +routeParams['id'];
      // Obtiene datos generales de la carrera
      this.clienteApi.getCarrera(idCarrera).subscribe(
        resp => {
          if(resp.status == 200) {
            this.carrera = resp.body;
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

  iniciaMapa(): void {
    if(this.carrera.latitud == null || this.carrera.longitud == null) return;

    this.mapaUbicacion = L.map('mapa-ubicacion', {
      center: [ this.carrera.latitud,  this.carrera.longitud],
      zoom: this.NIVEL_ZOOM
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

    this.setMarcadorUbicacion(this.carrera.latitud, this.carrera.longitud);
  }

  setMarcadorUbicacion(latitud: number, longitud: number) {
    // Borra posible marcador anterior y añade el nuevo
    this.capaMarcador.clearLayers();
    let marcador = L.marker([this.carrera.latitud, this.carrera.longitud], {icon: this.iconoMarcador});
    marcador.on("click", (evt) => this.animacionVueloMarcador());
    this.capaMarcador.addLayer(marcador);
  }

  animacionVueloMarcador() {
    this.mapaUbicacion.flyTo([this.carrera.latitud, this.carrera.longitud], this.NIVEL_ZOOM, {
        animate: true,
        duration: 1
    });
  }

  editarCarrera(): void {
    this.router.navigate(['/editar', this.carrera.id]);
  }

  generarQR() {
    this.router.navigate(["qr"], {relativeTo: this.route});
  }

  refresh(): void {
    this.errorCarga = false;
    this.cargaDatosCarrera();
  }

}
