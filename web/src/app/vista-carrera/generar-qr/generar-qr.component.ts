import { Component, OnInit } from '@angular/core';
import { Control, Carrera, AppSettings } from 'src/app/shared/app.model';
import { ActivatedRoute, Router } from '@angular/router';
import { ClienteApiService } from 'src/app/shared/cliente-api.service';
import { AlertService } from 'src/app/alert';
import * as QRCode from 'easyqrcodejs';
import * as html2canvas from 'html2canvas';
import pdfMake from 'pdfmake/build/pdfmake';
import pdfFonts from 'pdfmake/build/vfs_fonts';
pdfMake.vfs = pdfFonts.pdfMake.vfs;

@Component({
  selector: 'app-generar-qr',
  templateUrl: './generar-qr.component.html',
  styleUrls: ['./generar-qr.component.scss']
})
export class GenerarQRComponent implements OnInit {


  errorCarga: boolean;
  carrera: Carrera;
  controles: Control[];
  secretos: Map<string, string>;

  // Alertas
  optionsAlerts = {
    autoClose: true,
    keepAfterRouteChange: false
  };

  constructor(private route: ActivatedRoute,
    private router: Router,
    private clienteApi: ClienteApiService,
    protected alertService: AlertService) { }

  ngOnInit() {
    this.controles = [];
    this.secretos = new Map<string, string>();
    
    this.cargaDatosCarrera();
  }

  descargar(): void {
    // Genera el PDF con los controles
    let filename = 'Controles-QR.pdf';
    let informe = document.getElementById("informePDF");
    
    let options = {
      background: "white",
      logging: false,
      scale: 2
    };
    html2canvas(informe, options).then(canvas => {
      var data = canvas.toDataURL();
      var docDefinition = {
        content: [{
          image: data,
          width: 500 // A4
        }]
      };
      pdfMake.createPdf(docDefinition).open(); // .download()
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

            // Genera los códigos QR
            this.clienteApi.getControlesQRCarrera(idCarrera).subscribe(
              resp => {
                if(resp.status == 200) {
                  // Genera la lista
                  this.secretos = new Map(Object.entries(resp.body));
                  for(let control of this.controles) {
                    let options = {
                      text: this.secretos.get(control.codigo),
                      width: AppSettings.TAM_LADO_QR,
                      height: AppSettings.TAM_LADO_QR,
                      quietZone: 20,
                      quietZoneColor: 'transparent',
                    }
                    
                    new QRCode(document.getElementById("QR-" + control.codigo), options);
                  }
                }
              }, err => {
                console.log(err);
                this.alertService.error(err.error, this.optionsAlerts);
                this.errorCarga = true;
              }
            );
          } else {
            this.alertService.error("Error al obtener la carrera. Código: " + resp.status, this.optionsAlerts);
            this.errorCarga = true;
          }
        }, err => {
          if(err.status == 504) {
            this.alertService.error("No hay conexión con el servidor. Espera un momento y vuelve a intentarlo.", this.optionsAlerts);
          } else {
            this.alertService.error(err.error, this.optionsAlerts);
          }
          this.errorCarga = true;
        }
      );
    })
  }

  refresh(): void {
    this.errorCarga = false;
    this.cargaDatosCarrera();
  }

}
