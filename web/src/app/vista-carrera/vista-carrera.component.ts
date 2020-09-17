import { Component, OnInit, ElementRef, ViewChildren, QueryList } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { ClienteApiService } from '../_services/cliente-api.service';
import { AlertService } from '../alert';
import { Carrera, Usuario } from '../_shared/app.model';
import { Utils } from '../_shared/utils';
import * as L from 'leaflet';
import { TokenStorageService } from '../_services/token-storage.service';

@Component({
  selector: 'app-vista-carrera',
  templateUrl: './vista-carrera.component.html',
  styleUrls: ['./vista-carrera.component.scss']
})
export class VistaCarreraComponent implements OnInit {

  readonly CARRERA_TIPO_EVENTO = Carrera.TIPO_EVENTO;
  readonly CARRERA_TIPO_CIRCUITO = Carrera.TIPO_CIRCUITO;

  // Alertas
  alertOptions = {
    autoClose: true,
    keepAfterRouteChange: false
  };

  carrera: Carrera;
  esOrganizador: boolean;
  organizador: string;

  errorCarga: boolean;
  errorPrivada: boolean;
  descargandoMapas: boolean;
  hayMapas: boolean;

  // Ubicación
  @ViewChildren('mapaUbicacion') queryMapa: QueryList<ElementRef>;
  mapaUbicacion: L.Map;
  capaMarcador: L.LayerGroup;
  iconoMarcador: L.Icon;
  readonly NIVEL_ZOOM = 14;

  constructor(private route: ActivatedRoute,
    private router: Router,
    private clienteApi: ClienteApiService,
    protected alertService: AlertService,
    private tokenService: TokenStorageService) { }

  ngOnInit() {
    this.carrera = this.organizador = null;
    this.errorCarga = this.errorPrivada = this.esOrganizador = this.hayMapas = false;

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
            this.organizador = this.getOrganizador();
            if(this.tokenService.isLoggedIn() && this.tokenService.getUser().id === this.carrera.organizador.id) {
              this.esOrganizador = true;
              for(let recorrido of this.carrera.recorridos) {
                if(recorrido.mapa) this.hayMapas = true;
              }
            }
          } else {
            this.alertService.error("Error al obtener la carrera", this.alertOptions);
            this.errorCarga = true;
          }
        }, err => {
          if(err.status == 403) {
            this.errorPrivada = true;
          } else {
            this.alertService.error(Utils.getMensajeError(err, "Error al obtener la carrera"), this.alertOptions);
          }

          this.errorCarga = true;
        }
      );
    })
  }

  getOrganizador(): string {
    let res: string ;
    let org: Usuario = this.carrera.organizador;
    if(org) {
      res = org.nombre;
      if(org.club.length > 0) res += " (" + org.club + ")"; 
    } else {
      res = "Cuenta borrada";
    }
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

  descargarMapas(): void {
    this.descargandoMapas = true;
    this.clienteApi.getMapasCarrera(this.carrera.id).subscribe(
      resp => {
        if(resp.status == 200) {
          let dataType = resp.type.toString();
          let binaryData = [];
          binaryData.push(resp);
          let downloadLink = document.createElement('a');
          downloadLink.href = window.URL.createObjectURL(new Blob(binaryData, {type: dataType}));
          downloadLink.setAttribute('download', "Mapas");
          document.body.appendChild(downloadLink);
          downloadLink.click();
        } else {
          this.alertService.error("No hay mapas", this.alertOptions);
        }
        
        this.descargandoMapas = false;
      }, err => {
        this.alertService.error(Utils.getMensajeError(err, "Error al descargar los mapas"), this.alertOptions);
        this.descargandoMapas = false;
      }
    );
  }

  editarCarrera(): void {
    this.router.navigate(["/editar", this.carrera.id]);
  }

  generarQR(): void {
    this.router.navigate(["qr"], {relativeTo: this.route});
  }

  verResultados(idRecorrido: number): void {
    this.router.navigate(["/resultados", idRecorrido]);
  }

  refresh(): void {
    this.errorCarga = false;
    this.cargaDatosCarrera();
  }

}
