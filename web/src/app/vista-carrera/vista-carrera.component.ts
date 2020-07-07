import { Component, OnInit, ElementRef } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { ClienteApiService } from '../shared/cliente-api.service';
import { AlertService } from '../alert';
import { Carrera, Control } from '../shared/app.model';
import * as QRCode from 'easyqrcodejs';

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

  constructor(private route: ActivatedRoute,
    private clienteApi: ClienteApiService,
    protected alertService: AlertService) { }

  ngOnInit() {
    this.carrera = null;
    this.controles = [];
    this.secretos = new Map<string, string>();

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
              this.controles.push(control[1]);
            }

            // DEBUG: muestra los controles QR
            this.clienteApi.getSecretosControlesCarrera(idCarrera).subscribe(
              resp => {
                if(resp.status == 200) {
                  // Renderiza la lista
                  this.secretos = new Map(Object.entries(resp.body));

                  for(let control of this.controles) {
                    let options = {
                      text: control.codigo + "-" + this.secretos.get(control.codigo),
                      width: 200,
                      height: 200,
                      quietZone: 20,
                      quietZoneColor: 'transparent',
                    }
                    new QRCode(document.getElementById("QR-" + control.codigo), options);
                  }
                }
              }, err => {
                this.alertService.error("Error al obtener los secretos: " + JSON.stringify(err), this.optionsAlerts);
              }
            );
          } else {
            this.alertService.error("Error al obtener la carrera", this.optionsAlerts);
          }
        }, err => {
          if(err.status == 404) {
            this.alertService.error("No existe ninguna carrera con ese ID", this.optionsAlerts);
          } else {
            this.alertService.error("Error al obtener la carrera: " + JSON.stringify(err), this.optionsAlerts);
          }
        }
      );
    })
  }

}
