import { Component, OnInit, ViewChild, ElementRef } from '@angular/core';
import { AlertService } from 'src/app/alert';
import { ClienteApiService } from 'src/app/_services/cliente-api.service';
import { Router, ActivatedRoute } from '@angular/router';
import { Recorrido, ResultadoUsuario, ParcialUsuario, Carrera, ParticipacionesRecorridoResponse, Control } from 'src/app/_shared/app.model';
import { Utils } from 'src/app/_shared/utils';

@Component({
  selector: 'app-resultados',
  templateUrl: './resultados.component.html',
  styleUrls: ['./resultados.component.scss']
})
export class ResultadosComponent implements OnInit {
  readonly MOD_TRAZADO = Carrera.MOD_TRAZADO;
  readonly MOD_SCORE = Carrera.MOD_SCORE;

  // Tipos resultados
  readonly TIPO_OK = ResultadoUsuario.TIPO_OK;
  readonly TIPO_PENDIENTE = ResultadoUsuario.TIPO_PENDIENTE;
  readonly TIPO_ABANDONADO = ResultadoUsuario.TIPO_ABANDONADO;

  private tablaResultados: ElementRef<HTMLTableElement>;
  @ViewChild('tablaResultados', {static: false}) set content(content: ElementRef) {
    if(content) {
      this.tablaResultados = content;
    }
  }
  pos = { top: 0, left: 0, x: 0, y: 0 };

  idCarrera: number;
  recorrido: Recorrido;
  modalidad: string;
  resultados;
  grabbing: boolean;
  errorCarga: boolean;
  cabecerasTrazado: string[];
  listaControles: string[];

  // Alertas
  alertOptions = {
    autoClose: true,
    keepAfterRouteChange: false
  };

  constructor(private route: ActivatedRoute,
    private router: Router,
    private clienteApi: ClienteApiService,
    protected alertService: AlertService) { }

  ngOnInit() {
    this.modalidad = this.idCarrera = this.recorrido = this.cabecerasTrazado = this.resultados = this.listaControles = null;
    this.errorCarga = this.grabbing = false;

    this.cargaResultadosRecorrido();
  }


  cargaResultadosRecorrido(): void {
    this.resultados = null;
    this.route.params.subscribe(routeParams => {
      let idRecorrido = +routeParams['id'];
      // Obtiene los registros de los participantes del recorrido
      this.clienteApi.getParticipacionesRecorrido(idRecorrido).subscribe(
        resp => {
          if(resp.status == 200) {
            this.idCarrera = resp.body.idCarrera;
            this.generaResultados(resp.body);
          } else {
            this.alertService.error("Error al obtener los resultados", this.alertOptions);
            this.errorCarga = true;
          }
        }, err => {
          if(err.status == 404) {
            this.alertService.error("No existe ningún recorrido con ese ID", this.alertOptions);
          } else if(err.status == 504) {
            this.alertService.error("No hay conexión con el servidor. Espera un momento y vuelve a intentarlo.", this.alertOptions);
          } else {
            let mensaje = Utils.getMensajeError(err, "");
            this.alertService.error(mensaje, this.alertOptions);
          }
          this.errorCarga = true;
        }
      );
    })
  }

  generaResultados(respuesta: ParticipacionesRecorridoResponse): void {
    this.recorrido = respuesta.recorrido;
    this.modalidad = respuesta.modalidad;
    let puntuacionesControles: Map<string, number> = new Map(Object.entries(respuesta.puntuacionesControles));
    let trazado = this.recorrido.trazado;
    let resultados: ResultadoUsuario[] = [];
    // Lista de controles
    this.listaControles = [];
    for(let control of puntuacionesControles.keys()) {
      if(control !== "SALIDA") this.listaControles.push(control);
    }

    // Sets para calcular los puestos de los tiempos parciales y totales
    let setsParciales: Set<number>[] = [];
    for(let i = 0; i < trazado.length - 1; i++) setsParciales.push(new Set());
    let setsAcumulados: Set<number>[] = [];
    for(let i = 0; i < trazado.length - 1; i++) setsAcumulados.push(new Set());

    for(let participacion of respuesta.participaciones) {
      let corredor = participacion.corredor;
      let registros = participacion.registros;
      let tipoResultado = ResultadoUsuario.TIPO_OK;
      if(participacion.abandonado) tipoResultado = ResultadoUsuario.TIPO_ABANDONADO;
      else if(participacion.pendiente) tipoResultado = ResultadoUsuario.TIPO_PENDIENTE;
      let tiempoAcumulado = 0; // Segundos
      let puntuacion = 0;
      let parcialesUsuario: ParcialUsuario[] = [];
      let puntosRegistrados = new Array(this.listaControles.length).fill(false);

      if(this.modalidad === Carrera.MOD_TRAZADO) {
        // TRAZADO
        for(let i = 1; i < registros.length; i++) {
          let dPri = new Date(registros[i-1].fecha);
          let dSec = new Date(registros[i].fecha);
          if(dPri !== null && dSec !== null) {
            let tiempoParcial = dSec.getTime() / 1000 - dPri.getTime() / 1000;
            tiempoAcumulado += tiempoParcial;
            parcialesUsuario.push(new ParcialUsuario(tiempoParcial, tiempoAcumulado));
            setsParciales[i-1].add(tiempoParcial);
            setsAcumulados[i-1].add(tiempoAcumulado);
          } else {
            // El usuario ha abandonado el recorrido
            tipoResultado = ResultadoUsuario.TIPO_ABANDONADO;
          }
        }
      } else {
        // SCORE
        for(let registro of registros) {
            let codigoControl: string = registro.control;
            if(registro.fecha !== null) {
                let punt = puntuacionesControles.get(codigoControl);
                if(puntuacion !== null) {
                  puntuacion += punt;
                  let i = this.listaControles.indexOf(codigoControl);
                  if(i >= 0) puntosRegistrados[i] = true;
                }
            } else {
              // Recorrido abandonado
              tipoResultado = ResultadoUsuario.TIPO_ABANDONADO;
            }
        }

        tiempoAcumulado = new Date(registros[registros.length - 1].fecha).getTime() / 1000
                - new Date(registros[0].fecha).getTime() / 1000;
      }

      let resultadoUsuario = new ResultadoUsuario(corredor.id, corredor.nombre, corredor.club, tiempoAcumulado, puntuacion, tipoResultado);
      resultadoUsuario.parciales = parcialesUsuario;
      resultadoUsuario.puntosRegistrados = puntosRegistrados;
      resultados.push(resultadoUsuario);
    }

    if(this.modalidad === Carrera.MOD_TRAZADO) {
      this.cabecerasTrazado = [];
      for(let i = 0; i < trazado.length - 1; i++) {
        let p = (i === 0) ? "S" : i;
        let s = (i < trazado.length-2) ? (i+1) + " (" + trazado[i+1] + ")" : "M";
        this.cabecerasTrazado.push(p + "-" + s);
      }
      // Ordena lista por tiempo total y tipo
      resultados.sort(function(r1, r2) {
        // Comparación entre tipos
        let c = ResultadoUsuario.ORDEN_TIPOS.indexOf(r1.tipo) - ResultadoUsuario.ORDEN_TIPOS.indexOf(r2.tipo); 
        if(c === 0) {
          // Mismo tipo
          switch(r1.tipo) {
            case ResultadoUsuario.TIPO_OK:
              // Ordenados por tiempo ascendente
              c = r1.tiempoTotal - r2.tiempoTotal;
              break;
            case ResultadoUsuario.TIPO_PENDIENTE:
            case ResultadoUsuario.TIPO_ABANDONADO:
            default:
              // Ordenados según número de controles completados: más controles -> antes
              c = r1.parciales.length - r2.parciales.length;
              if(c === 0) {
                // Ordenados por tiempo ascendente
                c = r1.tiempoTotal - r2.tiempoTotal;
              }
              break;
          }
        }

        return c;
      });

      // Convierte los sets de tiempos a listas ordenadas
      let parcialesOrdenados: number[][] = [];
      for(let set of setsParciales) parcialesOrdenados.push(Array.from(set).sort((a, b) => (a - b)));
      let acumuladosOrdenados: number[][] = [];
      for(let set of setsAcumulados) acumuladosOrdenados.push(Array.from(set).sort((a, b) => (a - b)));
      // Actualiza posiciones y diferencias
      let tiempoGanador = (resultados.length > 0) ? resultados[0].tiempoTotal : null;
      for(let resultadoUsuario of resultados) {
          let i = 0;
          for(let parcial of resultadoUsuario.parciales) {
            parcial.posicionParcial = parcialesOrdenados[i].indexOf(parcial.tiempoParcial) + 1;
            parcial.posicionAcumulada = acumuladosOrdenados[i].indexOf(parcial.tiempoAcumulado) + 1;
            i++;
          }


          if(resultadoUsuario.parciales.length != trazado.length-1) {
            // Pendiente / Abandonado
          } else {
            // La posición final de un usuario es la misma que la acumulada de su último parcial
            resultadoUsuario.posicion = resultadoUsuario.parciales[resultadoUsuario.parciales.length-1].posicionAcumulada;
            resultadoUsuario.diferenciaGanador = resultadoUsuario.tiempoTotal - tiempoGanador;
          }
      }
    } else {
      // Ordena lista por puntuación y tiempo
      resultados.sort(function(r1, r2) {
        // Comparación entre tipos
        let c = ResultadoUsuario.ORDEN_TIPOS.indexOf(r1.tipo) - ResultadoUsuario.ORDEN_TIPOS.indexOf(r2.tipo); 
        if(c === 0) {
          // Mismo tipo. Entre el mismo tipo se usa el mismo criterio (puntuación, tiempo)
          // Ordenados por puntuación descendente
          c = r2.puntuacion - r1.puntuacion;
          if(c == 0) {
              // Misma puntuación, se ordena por tiempo ascendente
              c = r1.tiempoTotal - r2.tiempoTotal;
          }
        }

        return c;
      }); 

      // Actualiza posiciones
      // TODO Tener en cuenta empates de puntuación y tiempo
      let posicion = 1;
      for(let resultadoUsuario of resultados) {
        resultadoUsuario.posicion = posicion++;
      }

    }

    this.resultados = resultados;
  }



  mouseDownResultados(evento) {
    // https://github.com/phuoc-ng/html-dom/blob/master/demo/drag-to-scroll/index.html
    this.tablaResultados.nativeElement.style.cursor = 'grabbing';
    this.tablaResultados.nativeElement.style.userSelect = 'none';
    this.pos = {
      left: this.tablaResultados.nativeElement.scrollLeft,
      top: this.tablaResultados.nativeElement.scrollTop,
      // Get the current mouse position
      x: evento.clientX,
      y: evento.clientY
    };

    this.grabbing = true;
  }

  mouseMoveResultados(event) {
    if(this.grabbing) {
      // How far the mouse has been moved
      const dx = event.clientX - this.pos.x;
      const dy = event.clientY - this.pos.y;
      // Scroll the element
      this.tablaResultados.nativeElement.scrollTop = this.pos.top - dy;
      this.tablaResultados.nativeElement.scrollLeft = this.pos.left - dx;
    }
  }

  mouseUpResultados() {
    this.tablaResultados.nativeElement.style.cursor = 'grab';
    this.tablaResultados.nativeElement.style.removeProperty('user-select');
    this.grabbing = false;
  }

  clickBotonActualizar(): void {
    this.cargaResultadosRecorrido();
  }

  clickBotonVolver(): void {
    this.router.navigate(["/carreras", this.idCarrera]);
  }
}
