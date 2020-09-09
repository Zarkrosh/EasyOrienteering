import { Component, OnInit, ViewChild, ElementRef } from '@angular/core';
import { Control, Carrera, AppSettings } from 'src/app/_shared/app.model';
import { ActivatedRoute, Router } from '@angular/router';
import { ClienteApiService } from 'src/app/_services/cliente-api.service';
import { AlertService } from 'src/app/alert';
import * as QRCode from 'easyqrcodejs';
import * as html2canvas from 'html2canvas';
import pdfMake from 'pdfmake/build/pdfmake';
import pdfFonts from 'pdfmake/build/vfs_fonts';
import { TokenStorageService } from 'src/app/_services/token-storage.service';
pdfMake.vfs = pdfFonts.pdfMake.vfs;

@Component({
  selector: 'app-generar-qr',
  templateUrl: './generar-qr.component.html',
  styleUrls: ['./generar-qr.component.scss']
})
export class GenerarQRComponent implements OnInit {


  private vistaPDF: ElementRef<HTMLDivElement>;
  @ViewChild('vistaPDF', {static: false}) set content(content: ElementRef) {
    if(content) {
      this.vistaPDF = content;
    }
  }

  carrera: Carrera;
  controles: Control[];
  secretos: Map<string, string>;
  paginasControles: HTMLDivElement[][];
  elemsControles: HTMLDivElement[];

  errorCarga: boolean;
  generandoPDF: boolean;

  // Alertas
  optionsAlerts = {
    autoClose: true,
    keepAfterRouteChange: false
  };

  constructor(private route: ActivatedRoute,
    private router: Router,
    private clienteApi: ClienteApiService,
    protected alertService: AlertService,
    private tokenService: TokenStorageService) { }

  ngOnInit() {
    if(!this.tokenService.isLoggedIn()) {
      this.router.navigate(["/logout"]);
      return;
    }

    this.controles = [];
    this.secretos = new Map<string, string>();
    this.generandoPDF = false;
    this.paginasControles = [];
    this.elemsControles = [];
    
    this.cargaDatosCarrera();
  }

  async descargarPDF() {
    // Genera el PDF con los controles
    this.generandoPDF = true;
    const filename = 'Controles-QR.pdf';
    const widA4 = 595.28;
    let optionsCanvas = {
      background: "white",
      logging: false,
      scale: 2
    };
    let docDefinition = {
      pageSize: 'A4',
      pageMargins: [0,0,0,0],
      content: [],
      info: {
        title: 'Controles ' + this.carrera.nombre,
        author: 'EasyOrienteering'
      }
    }

    const paginas: HTMLDivElement[] = <HTMLDivElement[]> Array.from(document.getElementsByClassName('pagina-pdf'));
    for(let i = 0; i < paginas.length; i++) {
      await html2canvas(<HTMLElement> paginas[i], optionsCanvas).then(canvas => {
        let data = canvas.toDataURL();
        let c = {
          image: data,
          width: widA4
        }

        if(i < paginas.length - 1) {
          c["pageBreak"] = 'after';
        }

        docDefinition.content.push(c);
      });
    }

    //pdfMake.createPdf(docDefinition).open(filename, () => this.generandoPDF = false); // Debug más rápido
    pdfMake.createPdf(docDefinition).download(filename, () => this.generandoPDF = false);
  }


  actualizaPaginasPDF() {
    console.log("[*] Actualizando páginas");

    // Genera las páginas en base a la configuración actual
    const chunk = 6; // 3 filas y 2 columnas 
    this.paginasControles = [];
    for(let i = 0, j = this.elemsControles.length; i < j; i += chunk) {
      this.paginasControles.push(this.elemsControles.slice(i, i + chunk));
    }

    for(let pagina of this.paginasControles) {
      let divPagina = document.createElement('div') as HTMLDivElement;
      let divLista = document.createElement('div') as HTMLDivElement;
      divPagina.classList.add("pagina-pdf");
      divLista.classList.add("lista-controles");

      for(let control of pagina) {
        divLista.appendChild(control);
      }

      divPagina.appendChild(divLista);
      this.vistaPDF.nativeElement.appendChild(divPagina);
    }
  }

  generaControles() {
    // Controles
    for(let control of this.controles) {
      // Crea los elementos individuales
      let divControl = document.createElement('div') as HTMLDivElement;
      let divQR = document.createElement('div') as HTMLDivElement;
      let spanCodigo = document.createElement('span') as HTMLSpanElement;
      let spanMarca = document.createElement('span') as HTMLSpanElement;
      divControl.classList.add("control");
      divQR.classList.add("control-QR");
      spanCodigo.classList.add("control-codigo");
      spanMarca.classList.add("control-marca-agua");

      spanCodigo.innerText = control.codigo;
      spanMarca.innerText = AppSettings.MARCA_AGUA_CONTROLES;
      // Genera el código QR
      let options = {
        text: this.secretos.get(control.codigo),
        width: AppSettings.TAM_LADO_QR,
        height: AppSettings.TAM_LADO_QR,
        quietZone: 20,
        quietZoneColor: 'transparent',
        logo: "",
        logoWidth: 50,
        logoHeight: 50,
        logoBackgroundColor: '#ffffff'
      };
      if(control.tipo === Control.TIPO_SALIDA) options.logo = "http://localhost:4200/assets/img/salida.png";
      else if(control.tipo === Control.TIPO_CONTROL) options.logo = "http://localhost:4200/assets/img/control.png";
      else if(control.tipo === Control.TIPO_META) options.logo = "http://localhost:4200/assets/img/meta.png";
      new QRCode(divQR, options);

      // Compone el elemento
      divControl.appendChild(divQR);
      divControl.appendChild(spanCodigo);
      divControl.appendChild(spanMarca);
      this.elemsControles.push(divControl);
    }

    this.actualizaPaginasPDF();
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
            let salida = null;
            for(let control of Object.entries(this.carrera.controles)) {
              if(control[1].tipo != Control.TIPO_SALIDA) this.controles.push(control[1]);
              else salida = control[1];
            }

            // Triángulos de recorridos o score
            if(this.carrera.modalidad === Carrera.MOD_TRAZADO) {
              for(let recorrido of this.carrera.recorridos) {
                if(recorrido.trazado.length > 0) {
                  this.controles.push(new Control(recorrido.nombre, Control.TIPO_SALIDA, null));
                }
              }
            } else {
              if(salida !== null) {
                this.controles.push(new Control("SCORE", Control.TIPO_SALIDA, null));
              }
            }

            // Obtiene los valores de los QR
            this.clienteApi.getControlesQRCarrera(idCarrera).subscribe(
              resp => {
                if(resp.status == 200) {
                  // Genera la lista
                  this.secretos = new Map(Object.entries(resp.body));
                  this.generaControles();
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

  volver(): void {
    this.router.navigate(['/carreras', this.carrera.id], { replaceUrl: true });
  }

  refresh(): void {
    this.errorCarga = false;
    this.cargaDatosCarrera();
  }

}
