import { Component, OnInit, ViewChild, ElementRef } from '@angular/core';
import { Recorrido, Control, Coordenadas } from '../../shared/app.model';
import { AlertService } from '../../alert';
import { SharedEditorService } from '../shared-editor.service';

declare var $:any;

@Component({
  selector: 'editor-trazado',
  templateUrl: './editor-trazado.component.html',
  styleUrls: ['./editor-trazado.component.scss']
})
export class EditorTrazadoComponent implements OnInit {
  // Medidas y aspectos de los símbolos (según ISOM 2017)
  MM_UNIT_ORI = 8; // Valor (original) para controlar el escalado
  MM_UNIT = 8;     // Valor para controlar el escalado
  GENERAL_COLOR = "rgba(255,0,255,0.8)";
  GENERAL_COLOR_INACTIVE = "rgba(255,0,255,0.6)";
  GENERAL_LINE_WIDTH: number;
  START_SIDE_LENGTH: number;
  CONTROL_RADIUS: number;
  FINISH_EXT_RADIUS: number;
  FINISH_INT_RADIUS: number;
  NUMBER_SIZE: number;
  NUMBER_DISTANCE: number;
  NUMBER_FONT = "sans-serif"; 

  // Medidas y aspectos del marcador
  MARKER_DIAMETER = 10;
  MARKER_LINE_WIDTH = 1;
  MARKER_COLOR = 'rgba(255,0,255,0.7)'; // Magenta
  MARKER_STATE: number;
  MOVE_MARKER_COLOR = 'rgba(0,0,255,0.6)'; // Azul
  MOVE_MARKER_RADIUS = this.MM_UNIT;
  ID_CONTROL_MARCADO: string;
  BOOL_PRINTED_MARKED = false;

  // Lienzos
  @ViewChild('vistaMapa', {static: true}) vistaMapa: ElementRef<HTMLDivElement>; 
  @ViewChild('canvasMapa', {static: true}) canvasMapa: ElementRef<HTMLCanvasElement>; 
  @ViewChild('canvasTrazado', {static: true}) canvasTrazado: ElementRef<HTMLCanvasElement>; 
  @ViewChild('canvasMarcador', {static: true}) canvasMarcador: ElementRef<HTMLCanvasElement>;
  contextMapa: CanvasRenderingContext2D;
  contextTrazado: CanvasRenderingContext2D;
  contextMarcador: CanvasRenderingContext2D; 

  // Mapa base
  imgMapaOrig: HTMLImageElement; // Imagen original
  imgMapa: HTMLImageElement;     // Imagen modificada para zoom
  escala: number; // 7500, 10000 ...

  // Pantalla
  offsetX: number;     // Offset izquierdo de la vista del mapa
  offsetY: number;     // Offset derecho de la vista del mapa
  MAX_OFFSET_X;        // Offset de máximo desplazamiento horizontal
  MAX_OFFSET_Y;        // Offset de máximo desplazamiento vertical
  zoomLevel: number;   // Nivel de zoom
  MAX_ZOOM = 5;
  MIN_ZOOM = -2;
  ZOOM_SCALING = 0.2;  // Porcentaje de cambio de niveles de zoom

  // Ratón
  clicking: boolean;
  moviendoMapa: boolean;
  moviendoControl: boolean;
  dragX: number;
  dragY: number;
  mapX: number; // TEST
  mapY: number; // TEST
  canvX: number; // TEST
  canvY: number; // TEST

  // Modelo compartido
  controles: Map<string, Control>; // TODO Buscar mejor alternativa de tipo
  recorridoActual: Recorrido;

  // Alertas
  options = {
    autoClose: true,
    keepAfterRouteChange: false
  };

  constructor(private sharedData: SharedEditorService,
    protected alertService: AlertService,
    private el: ElementRef) { }

  ngOnInit() {
    this.clicking = this.moviendoControl = this.moviendoMapa = false;
    this.imgMapa = this.ID_CONTROL_MARCADO = null;
    this.offsetX = this.offsetY = 0;
    this.MARKER_STATE = Control.SALIDA;

    // DEBUG Para no tener que estar cargándolo a mano
    this.cargaMapaAutomatico();

    this.updateCanvasDims(this.canvasMarcador.nativeElement);
    this.setupCanvas(false);

    // Controlador de cambio del mapa
    this.sharedData.mapaBase.subscribe((nMapa) => {
      // TODO COPIAR DEL AUTOMATICO
    });

    // Controlador de cambio de los controles
    this.sharedData.controles.subscribe((nControles) => {
      this.controles = nControles;
    });

    // Controlador de cambio del recorrido
    this.sharedData.recorridoActual.subscribe((nRecorrido) => {
      this.recorridoActual = nRecorrido;
      if(nRecorrido === null) {
        // Muestra todos los controles
        // TODO
      } else {
        this.redibujaTrazado();
      }
    });

    // Controlador de confirmación de borrado de control
    this.sharedData.controlBorradoConf.subscribe((tipo) => {
      if(tipo === Control.SALIDA) {
        this.startSelected();
      } else if(tipo === Control.CONTROL) {
        this.controlSelected();
      } else if(tipo === Control.META) {
        this.finishSelected();
      }

      //this.redrawMarker
    });
  }
  

  /**
   * Configura los lienzos del editor
   */
  setupCanvas(porImagen: boolean) {
    // Evita menú contextual del lienzo superior (click derecho) 
    this.canvasMarcador.nativeElement.oncontextmenu = function(e) { return false; }; 

    if(this.canvasMapa.nativeElement.getContext && 
        this.canvasTrazado.nativeElement.getContext && 
        this.canvasMarcador.nativeElement.getContext) {
      // Lienzos iniciados correctamente
      
      if(porImagen) {
        // TEST: canvas con el mismo tamaño que la imagen
        this.updateCanvasDims2(this.canvasMapa.nativeElement, this.imgMapa.width, this.imgMapa.height);
        this.updateCanvasDims2(this.canvasTrazado.nativeElement, this.imgMapa.width, this.imgMapa.height);
        this.updateCanvasDims(this.canvasMarcador.nativeElement);
      } else {
        // Actualiza las dimensiones reales según las visuales
        this.updateCanvasDims(this.canvasMapa.nativeElement);
        this.updateCanvasDims(this.canvasTrazado.nativeElement);
        this.updateCanvasDims(this.canvasMarcador.nativeElement);
      }

      // Configura los contextos
      this.contextMapa = this.canvasMapa.nativeElement.getContext('2d');
      this.contextTrazado = this.canvasTrazado.nativeElement.getContext('2d');
      this.contextTrazado.lineWidth = this.GENERAL_LINE_WIDTH;
      this.contextTrazado.strokeStyle = this.GENERAL_COLOR;
      this.contextMarcador = this.canvasMarcador.nativeElement.getContext('2d');
      this.contextMarcador.lineWidth = this.GENERAL_LINE_WIDTH;
      this.contextMarcador.strokeStyle = this.GENERAL_COLOR;
    } else {
      this.alertService.error('Error de carga. Usa un navegador más moderno', this.options);
    }

  }


  /********************************************************************
   *                    PANEL DE TIPO DE CONTROL                      *    
   ********************************************************************/
  /* El usuario selecciona que quiere marcar una salida */
  startSelected() {
    this.removeSelected(); // Borra el actual seleccionado
    $("#tipo-salida").addClass("tipo-seleccionado"); // Y selecciona el nuevo
    this.MARKER_STATE = Control.SALIDA;
    this.redibujaTrazado();
  }
  
  /* El usuario selecciona que quiere marcar un control */
  controlSelected() {
    this.removeSelected(); // Borra el actual seleccionado
    $("#tipo-control").addClass("tipo-seleccionado"); // Y selecciona el nuevo
    this.MARKER_STATE = Control.CONTROL;
    this.redibujaTrazado();
  }
  
  /* El usuario selecciona que quiere marcar una meta */
  finishSelected() {
    this.removeSelected(); // Borra el actual seleccionado
    $("#tipo-meta").addClass("tipo-seleccionado"); // Y selecciona el nuevo
    this.MARKER_STATE = Control.META;
    this.redibujaTrazado();
  }
  
  /* Elimina la selección del actualmente seleccionado */
  removeSelected() {
    $(".tipo-punto").removeClass("tipo-seleccionado");
  }




  /********************************************************************
   *                      LIENZO DE MAPA BASE                         *    
   ********************************************************************/
  /* Redibuja el mapa base */
  redibujaMapa() {
    this.clearCanvasMap(); // Limpia el mapa anterior
    this.contextMapa.drawImage(this.imgMapa, 0, 0, this.imgMapa.width, this.imgMapa.height);
  }

  /* Limpia el lienzo del mapa */
  clearCanvasMap() {
    console.log("CLEAR (Mapa)")
    this.contextMapa.clearRect(0, 0, this.canvasMapa.nativeElement.width, this.canvasMapa.nativeElement.height);
  }




  /********************************************************************
   *                      LIENZO DE MARCADOR                          *    
   ********************************************************************/
  /* Controla el evento de desplazamiento del ratón por el lienzo del marcador */
  mouseMoveMarcador(evento) {
    var fCoords = this.getCoordenadasCanvasMarcador(evento); // Coordenadas en el lienzo de marcador
    var rCoords = this.getCoordenadasSistema(fCoords);       // Coordenadas reales del sistema
    this.mapX = rCoords.x; // DEBUG
    this.mapY = rCoords.y; // DEBUG
    this.canvX = fCoords.x + Math.abs(this.offsetX);
    this.canvY = fCoords.y + Math.abs(this.offsetY);

    if(this.clicking) {
      // Está moviendo el cursor sobre el mapa con el botón izquierdo del ratón pulsado
      this.limpiaCanvasMarcador(); // Borra el marcador
      
      if(this.ID_CONTROL_MARCADO !== null && this.recorridoActual.idControles.includes(this.ID_CONTROL_MARCADO)) {
        // Está moviendo un control -> actualiza sus coordenadas
        this.controles.get(this.ID_CONTROL_MARCADO).coords = rCoords;
        // Pinta una cruz marcadora
        this.dibujaCruzMarcador(fCoords);
        this.redibujaTrazado();
      } else {
        // Está haciendo un drag del mapa
        if(!this.moviendoMapa) {
          this.moviendoMapa = true;
          // Se muestra el cursor de drag
          this.vistaMapa.nativeElement.style.cursor = "grabbing";
        }
        
        // Calcula los nuevos offset a partir del cursor actual y del inicial
        this.offsetX += evento.pageX - this.dragX;
        this.offsetY += evento.pageY - this.dragY;
        this.dragX = evento.pageX;
        this.dragY = evento.pageY;
        
        // Límites de movimiento del mapa dependiendo de la imagen
        if(this.offsetX > 0) this.offsetX = 0;
        if(this.offsetY > 0) this.offsetY = 0;
        if(this.offsetX < this.MAX_OFFSET_X) this.offsetX = this.MAX_OFFSET_X;
        if(this.offsetY < this.MAX_OFFSET_Y) this.offsetY = this.MAX_OFFSET_Y;
        // Desplaza los canvas
        this.desplazaCanvas();
      }
    } else {
      // Está moviendo el cursor libremente por encima del mapa
      // Comprueba si está encima de un control
      var prevMarked = this.ID_CONTROL_MARCADO;
      this.ID_CONTROL_MARCADO = null;
      for(let [codigo, control] of this.controles) {
        if(control.tipo === this.MARKER_STATE) {
          if(Math.abs(control.coords.x - rCoords.x) <= this.CONTROL_RADIUS &&
            Math.abs(control.coords.y - rCoords.y) <= this.CONTROL_RADIUS) {
            // Está encima -> se selecciona
            this.ID_CONTROL_MARCADO = control.codigo;
            break;
          }
        }
      }
      
      if(prevMarked !== null && this.ID_CONTROL_MARCADO === null) {
        this.BOOL_PRINTED_MARKED = false;
      }
      
      // Redibuja el marcador
      this.redrawMarker(fCoords);
    }
  }

  /* Controla el evento de salida del desplazamiento del ratón por el lienzo del marcador */
  mouseLeaveMarcador(evento) {
    this.limpiaCanvasMarcador();
    this.resetConfiguracionRaton();
  }

  /* Evento de pulsado del marcador */
  mouseDownMarcador(evento) {
    if(evento.which === 1) {
      // Botón izquierdo
      this.clicking = true;
      
      if(this.ID_CONTROL_MARCADO !== null) {
          // Movimiento de control
          // TODO Cambiar el marcador a rellenado por ejemplo
      }
      
      // Aunque al final no sea un drag, se recogen las coordenadas
      this.dragX = evento.pageX;
      this.dragY = evento.pageY;
    }
  }

  /* Evento de soltado del marcador */
  mouseUpMarcador(evento) {
    var fCoords = this.getCoordenadasCanvasMarcador(evento); // Coordenadas en el lienzo marcador
    var rCoords = this.getCoordenadasSistema(fCoords);       // Coordenadas reales del sistema

    if(evento.which === 1) {
      // Click izquierdo
      if(this.ID_CONTROL_MARCADO !== null && this.recorridoActual.idControles.includes(this.ID_CONTROL_MARCADO)) {
        // Acaba de resituar un control
        this.limpiaCanvasMarcador();
      } else if(this.moviendoMapa) {
        // Suelta después de hacer un drag -> se dibuja de nuevo el marcador
        this.redrawMarker(fCoords);
      } else {
        // Adición de control
        if(this.recorridoActual !== null) {
          if(this.ID_CONTROL_MARCADO !== null) {
            // Reutiliza control
            console.log("REUTILIZA CONTROL " + this.ID_CONTROL_MARCADO);
            this.recorridoActual.idControles.push(this.ID_CONTROL_MARCADO);
            this.redibujaTrazado();
          } else {
            // Nuevo control
            this.sharedData.anadirControl(new Control(this.MARKER_STATE, rCoords));
          }

          if(this.MARKER_STATE === Control.SALIDA) {
            // Cambia automáticamente a control
            this.controlSelected();
          }
        } else {
          // Nuevo control cuando se muestran todos los controles
          // ¿?
        }
      }
    } else if(evento.which === 2) {
      // Click central -> selecciona la meta
      this.finishSelected();
      this.redrawMarker(fCoords);
    } else if(evento.which === 3) {
      // Click derecho -> elimina el último control, o el marcado
      if(this.recorridoActual.idControles.length > 0) {
        let control: Control;
        if(this.ID_CONTROL_MARCADO !== null && this.recorridoActual.idControles.includes(this.ID_CONTROL_MARCADO)) {
          // Borra control marcado
          control = this.controles.get(this.ID_CONTROL_MARCADO);
        } else {
          // Borra último control
          control = this.controles.get(this.recorridoActual.idControles[this.recorridoActual.idControles.length-1]);
        }

        // Notifica el borrado del control
        this.sharedData.borrarControl(control);
        // Redibuja el marcador
        this.redrawMarker(fCoords);
      }
    }

    this.resetConfiguracionRaton();
  }

  /* Evento de rueda de ratón para cambiar el zoom */
  wheelMarcador(evento) {
    evento.preventDefault();
    var fCoords = this.getCoordenadasCanvasMarcador(evento);
    //var delta = Math.max(-1, Math.min(1, (evento.wheelDelta || -evento.detail)));
    var delta = -(evento.deltaY / Math.abs(evento.deltaY)); // Normaliza el delta
    // TODO Verificar que funciona en más navegadores

    this.zoomLevel += delta;
    if(this.zoomLevel > this.MAX_ZOOM) {
      this.zoomLevel = this.MAX_ZOOM;
      return; // Retornar para evitar redrawing
    } 
    if(this.zoomLevel < this.MIN_ZOOM) {
      this.zoomLevel = this.MIN_ZOOM;
      return; // Retornar para evitar redrawing
    }

    // Cambia el zoom
    this.setZoom(this.zoomLevel, fCoords, delta);
    // Redibuja el marcador
    this.redrawMarker(fCoords);
  }

  /**
   * Asigna el nivel de zoom especificado y actualiza la vista.
   * @param nivel Nivel de zoom
   */
  setZoom(nivel, coordsMarcador, delta) {
    console.log("ZOOM: " + this.zoomLevel); // DEBUG
    this.zoomLevel = nivel;
    var scale = this.getEscaladoZoom();

    // Actualiza las métricas
    this.actualizaMetricas();

    // Actualiza los offsets para un zoom en la zona de marcado
    // TODO No acabado
    this.offsetX += (-delta * coordsMarcador.x) * scale;
    this.offsetY += (-delta * coordsMarcador.y) * scale;
    this.desplazaCanvas();

    // Modifica el tamaño de la imagen para generar el zoom
    this.imgMapa.width = this.imgMapaOrig.width * scale;
    this.imgMapa.height = this.imgMapaOrig.height * scale;
    this.actualizaLimites(); // Actualiza los nuevos límites
    this.setupCanvas(true);  // Reajusta los lienzos
    this.redibujaMapa();     // Redibuja el mapa
    this.redibujaTrazado();  // Redibuja el trazado
  }

  /* Dibuja el marcador de control */
  dibujaSalidaMarker(coordenadas: Coordenadas) {
    // Dibuja el triángulo orientado hacia el norte
    this.dibujaSalida(coordenadas, coordenadas.x, -coordenadas.y, this.contextMarcador, true);
    // Y luego la cruz central
    this.dibujaCruzMarcador(coordenadas);
  }

  /* Dibuja el marcador de control */
  dibujaControlMarker(coordenadas: Coordenadas) {
    // Dibuja el círculo
    this.dibujaControl(coordenadas, this.contextMarcador, true);
    // Y luego la cruz central
    this.dibujaCruzMarcador(coordenadas);
  }

  /* Dibuja el marcador de meta */
  dibujaMetaMarker(coordenadas: Coordenadas) {
    // Dibuja los círculos exterior e interior
    this.dibujaMeta(coordenadas, this.contextMarcador, true);
    // Y luego la cruz central
    this.dibujaCruzMarcador(coordenadas);
  }

  /**
   * Dibuja el marcador del tipo actual de control seleccionado. Tiene en cuenta
   * si está encima de un control, y lo marca en dicho caso.
   * @param x Coordenada x
   * @param y Coordenada y
   */
  redrawMarker(coordenadas: Coordenadas) {
    if(this.ID_CONTROL_MARCADO === null) {
      // Movimiento libre
      this.limpiaCanvasMarcador(); // Borra marcador anterior
      switch(this.MARKER_STATE) {
        case Control.SALIDA:
          this.dibujaSalidaMarker(coordenadas);
          break;
        case Control.CONTROL:
          this.dibujaControlMarker(coordenadas);
          break;
        case Control.META:
          this.dibujaMetaMarker(coordenadas);
          break;
      }

      // Efecto de orientación del triángulo de salida cuando dibuja el primer control
      var idsControles = this.recorridoActual.idControles;
      if(idsControles.length > 0) {
        let ultimo = this.controles.get(idsControles[idsControles.length-1]);
        let coords = this.getCoordenadasCanvasTrazado(ultimo.coords);
        coords.x += this.offsetX;
        coords.y += this.offsetY;
        if(ultimo.tipo === Control.SALIDA) {
          this.dibujaSalida(coords, coordenadas.x, coordenadas.y, this.contextMarcador, true);
        }
      }
    } else {
      // Está encima de un control
      if(!this.BOOL_PRINTED_MARKED) {
        this.BOOL_PRINTED_MARKED = true;
        // Limpia el marcador anterior
        this.limpiaCanvasMarcador();
        
        let control = this.controles.get(this.ID_CONTROL_MARCADO);
        let coords = this.getCoordenadasCanvasTrazado(control.coords);
        coords.x += this.offsetX;
        coords.y += this.offsetY;
        
        if(this.recorridoActual.idControles.includes(control.codigo)) {
          // Encima de un control del recorrido actual -> opción de mover control
          this.dibujaMarcadorDesplazamiento(coords);
        } else {
          // Encima de un control de otro recorrido -> opción de añadir control a recorrido
          switch(this.MARKER_STATE) {
            case Control.SALIDA:
              this.dibujaSalida(coords, coords.x, -coords.y, this.contextMarcador, true);
              break;
            case Control.CONTROL:
              this.dibujaControl(coords, this.contextMarcador, true);
              break;
            case Control.META:
              this.dibujaMeta(coords, this.contextMarcador, true);
              break;
          }

          this.dibujaCruzMarcador(coords);

          // Efecto de orientación del triángulo de salida cuando es el último elemento
          var idsControles = this.recorridoActual.idControles;
          if(idsControles.length > 0) {
            var ultimo = this.controles.get(idsControles[idsControles.length-1]);
            if(ultimo.tipo === Control.SALIDA) {
              let coordsUlt = this.getCoordenadasCanvasTrazado(ultimo.coords);
              coordsUlt.x += this.offsetX;
              coordsUlt.y += this.offsetY;
              this.dibujaSalida(coordsUlt, coords.x, coords.y, this.contextMarcador, true);
            }
          }
        }
      } else {
        // Evita redibujar de nuevo, optimizando el canvas
      }
    }
  }

  /**
   * Dibuja una cruz en el centro del marcador para una mayor precisión.
   * @param coordenadas Coordenadas de la cruz
   */
  dibujaCruzMarcador(coordenadas: Coordenadas) { 
    // Guarda estado anterior
    this.contextMarcador.save();
    
    // Dibuja la cruz
    this.contextMarcador.beginPath();
    this.contextMarcador.lineWidth = this.MARKER_LINE_WIDTH;
    this.contextMarcador.strokeStyle = this.MARKER_COLOR;
    this.contextMarcador.moveTo(coordenadas.x - this.MARKER_DIAMETER, coordenadas.y); 
    this.contextMarcador.lineTo(coordenadas.x + this.MARKER_DIAMETER, coordenadas.y); // Linea horizontal
    this.contextMarcador.moveTo(coordenadas.x, coordenadas.y - this.MARKER_DIAMETER); 
    this.contextMarcador.lineTo(coordenadas.x, coordenadas.y + this.MARKER_DIAMETER); // Linea vertical
    this.contextMarcador.stroke();
    
    // Restaura el estado
    this.contextMarcador.restore();
  }

  /* Dibuja el indicio de desplazamiento de control */
  dibujaMarcadorDesplazamiento(coordenadas: Coordenadas) {
    // Guarda estado anterior
    this.contextMarcador.save();
    
    // Dibuja un círculo azul
    this.contextMarcador.beginPath();
    this.contextMarcador.lineWidth = this.GENERAL_LINE_WIDTH;
    this.contextMarcador.strokeStyle = this.MOVE_MARKER_COLOR;
    this.contextMarcador.arc(coordenadas.x, coordenadas.y, this.MOVE_MARKER_RADIUS, 0, 2 * Math.PI);
    this.contextMarcador.stroke();
    
    // Restaura el estado
    this.contextMarcador.restore();
  }

  /* Limpia el lienzo del trazado */
  limpiaCanvasMarcador() {
    this.contextMarcador.clearRect(0, 0, this.canvasMarcador.nativeElement.width, this.canvasMarcador.nativeElement.height);
  }







  /********************************************************************
   *                      LIENZO DE TRAZADO                           *    
   ********************************************************************/

  /* Redibuja los elementos del recorrido */
  redibujaTrazado() {
    if(!this.recorridoActual) return;

    // Borra el trazado anterior
    this.borraTrazado();

    var idsControles = this.recorridoActual.idControles;
    if(idsControles.length === 1) {
      var primerControl = this.controles.get(idsControles[0]);
      let coords = this.getCoordenadasCanvasTrazado(primerControl.coords);
      if(primerControl.tipo === Control.SALIDA) {
        // El triángulo se orienta hacia el cursor en el canvas del marcador
      } else if (primerControl.tipo === Control.CONTROL) {
        this.dibujaControl(coords, this.contextTrazado, true);
      } else {
        this.dibujaMeta(coords, this.contextTrazado, true);
      }
    } else {
        // Itera sobre los elementos dibujando los tramos
        for(var i = 0; i < idsControles.length-1; i++) {
            // Controles del tramo
            let actual = this.controles.get(idsControles[i]);
            let siguiente = this.controles.get(idsControles[i+1]);

            // Dibuja el tramo
            this.dibujaTramo(actual, siguiente);
        }
        // Vuelve a iterar por tríos para dibujar el orden de los controles
        var numControl = 1;
        for(i = 0; i < idsControles.length; i++) {
            let previo = null, actual = this.controles.get(idsControles[i]), siguiente = null;
            // No tiene sentido un control como primer punto, pero por si acaso para que no de un error
            if(i > 0) previo = this.controles.get(idsControles[i-1]);
            // Tiene sentido un control como último punto, mientras se está trazando
            if(i < idsControles.length-1) siguiente = this.controles.get(idsControles[i+1]);
            
            // Dibuja el número si es un control
            if(actual.tipo === Control.CONTROL) {
                this.dibujaNumero(previo, actual, siguiente, numControl);
                numControl++;
            }
        }
    }
    
    // Pinta los controles de otros recorridos (del mismo tipo actual)
    this.controles.forEach((control, codigo, map) => {
      if(control.tipo === this.MARKER_STATE && !idsControles.includes(control.codigo)) {
        // Controles del mismo tipo seleccionado que no están en el recorrido actual
        let coords = this.getCoordenadasCanvasTrazado(control.coords);
        switch(control.tipo) {
          case Control.SALIDA:
          this.dibujaSalida(coords, coords.x, -coords.y, this.contextTrazado, false); // Orientado al norte
            break;
          case Control.CONTROL:
          this.dibujaControl(coords, this.contextTrazado, false);
            break;
          case Control.META:
            this.dibujaMeta(coords, this.contextTrazado, false);
            break;
        }
      }
    });
  }

  /**
   * Dibuja el número del control actual, teniendo en cuenta las posiciones de un posible
   * control previo y otro posible control siguiente, para una vista del recorrido más cómoda.
   * @param previo Control previo (null para el primer control)
   * @param actual Control actual (en el que se dibuja el número)
   * @param siguiente Control siguiente (null para el último control)
   * @param numero Número del control
   */
  dibujaNumero(previo: Control, actual: Control, siguiente: Control, numero) {
    // Recalcula las coordenadas de los controles según el zoom
    var coordsPre = (previo) ? this.getCoordenadasCanvasTrazado(previo.coords) : null;
    var coordsAct = this.getCoordenadasCanvasTrazado(actual.coords);
    var coordsSig = (siguiente) ? this.getCoordenadasCanvasTrazado(siguiente.coords) : null;
    // Primero obtiene el ángulo entre los controles previo y siguiente respecto al actual
    var anguloPrevio, anguloSiguiente, anguloResultante;
    if(previo === null) {
        // Control como primer punto
        anguloPrevio = 0;
    } else {
        var pX = coordsAct.x - coordsPre.x;
        var pY = coordsPre.y - coordsAct.y;
        anguloPrevio = Math.atan2(pY, pX) + Math.PI; // +PI para calcular en el actual
    }
    
    if(siguiente === null) {
        // Control como último punto
        anguloSiguiente = 0;
    } else {
        var sX = coordsSig.x - coordsAct.x;
        var sY = coordsAct.y - coordsSig.y;
        anguloSiguiente = Math.atan2(sY, sX); // (radianes)
        if(anguloSiguiente < 0) {
            anguloSiguiente = 2*Math.PI + anguloSiguiente;
        }
    }

    // Con el ángulo obtenido, calcula las coordenadas resultantes
    anguloResultante = Math.min(anguloSiguiente, anguloPrevio) + Math.abs(anguloSiguiente - anguloPrevio)/2;
    if(Math.abs(anguloSiguiente - anguloPrevio) < Math.PI) {
        // Se invierte
        anguloResultante += Math.PI;
    }
    var x = coordsAct.x + this.NUMBER_DISTANCE * Math.cos(anguloResultante);
    var y = coordsAct.y - this.NUMBER_DISTANCE * Math.sin(anguloResultante);
    
    // Lo dibuja
    this.contextTrazado.font = this.NUMBER_SIZE + 'px ' + this.NUMBER_FONT;
    this.contextTrazado.lineWidth = this.GENERAL_LINE_WIDTH;
    this.contextTrazado.fillStyle = this.GENERAL_COLOR;
    this.contextTrazado.textAlign = "center";
    this.contextTrazado.textBaseline = "middle";
    this.contextTrazado.fillText(numero, x, y);
  }

  /* Dibuja un tramo entre dos controles */
  dibujaTramo(initial: Control, final: Control) {
    // Recalcula las coordenadas de los controles según el zoom
    var coordsIni = this.getCoordenadasCanvasTrazado(initial.coords);
    var coordsFin = this.getCoordenadasCanvasTrazado(final.coords);
    // Calcula el ángulo entre ambos controles
    //var dX = final.coords.x - initial.coords.x;
    //var dY = initial.coords.y - final.coords.y;
    var dX = coordsFin.x - coordsIni.x; // TEST
    var dY = coordsIni.y - coordsFin.y; // TEST
    var angulo = Math.atan2(dY, dX); // (radianes)
    // Obtiene las coordenadas del primer control
    var pR;
    if(initial.tipo === Control.SALIDA) {
        // Triángulo de salida
        pR = Math.sqrt(3) / 3 * this.START_SIDE_LENGTH; 
        this.dibujaSalida(coordsIni, coordsFin.x, coordsFin.y, this.contextTrazado, true);
    } else if(initial.tipo === Control.CONTROL) {
        // Control intermedio
        pR = this.CONTROL_RADIUS;
        this.dibujaControl(coordsIni, this.contextTrazado, true);
    } else {
        // Meta
        pR = this.FINISH_EXT_RADIUS;
        this.dibujaMeta(coordsIni, this.contextTrazado, true);
    }
    var pX = coordsIni.x + (pR * Math.cos(angulo));
    var pY = coordsIni.y - (pR * Math.sin(angulo));
    
    // Obtiene las coordenadas del segundo control
    angulo += Math.PI; // Ángulo opuesto
    var sR;
    if(final.tipo === Control.SALIDA) {
        // Triángulo de salida
        sR = Math.sqrt(3) / 3 * this.START_SIDE_LENGTH; 
        // No se pinta el triángulo por su orientación al siguiente control
        // No dibuja la línea
        return;
    } else if(final.tipo === Control.CONTROL) {
        // Control intermedio
        sR = this.CONTROL_RADIUS;
        this.dibujaControl(coordsFin, this.contextTrazado, true);
    } else {
        // Meta
        sR = this.FINISH_EXT_RADIUS;
        this.dibujaMeta(coordsFin, this.contextTrazado, true);
    }
    var sX = coordsFin.x + (sR * Math.cos(angulo));
    var sY = coordsFin.y - (sR * Math.sin(angulo));
    
    // Finalmente dibuja la línea
    this.contextTrazado.beginPath();
    this.contextTrazado.moveTo(pX, pY);
    this.contextTrazado.lineTo(sX, sY);
    this.contextTrazado.stroke();
  }

  /**
   * Dibuja un control de salida orientado hacia unas coordenadas.
   * @param coordenadas Coordenadas del control 
   * @param nX Coordenada X del punto hacia el que se orienta
   * @param nY Coordenada Y del punto hacia el que se orienta
   * @param ctx Contexto usado para el dibujo
   * @param activo True para color activo, false para un poco transparente
   */
  dibujaSalida(coordenadas: Coordenadas, nX: number, nY: number, ctx, activo: boolean) {
    var radExt = Math.sqrt(3) / 3 * this.START_SIDE_LENGTH; // Radio del círculo circunscrito
    var dX = nX - coordenadas.x; // 30 parece una buena distancia de margen
    var dY = coordenadas.y - nY;

    var grados = Math.atan2(dY, dX); // Ángulo con el siguiente punto
    // Calcula el primer punto
    var pX = radExt * Math.cos(grados);
    var pY = radExt * Math.sin(grados);
    var pPunto = [coordenadas.x + pX, coordenadas.y - pY];
    // Calcula el resto de puntos a partir del primero. Diferencias de 120º por ser equilátero.
    // Ley de rotación: x' = x cos α - y sin α   |   y' = y cos α + x sin α
    var c120 = Math.cos(this.toRad(120));
    var s120 = Math.sin(this.toRad(120));
    var c240 = Math.cos(this.toRad(240));
    var s240 = Math.sin(this.toRad(240));
    var sPunto = [pX * c120 - pY * s120, -pY * c120 - pX * s120];
    var tPunto = [pX * c240 - pY * s240, -pY * c240 - pX * s240];
    // Reacomoda los puntos con el primero
    sPunto[0] += coordenadas.x;
    sPunto[1] += coordenadas.y;
    tPunto[0] += coordenadas.x;
    tPunto[1] += coordenadas.y;
    
    // Dibuja el triángulo
    ctx.beginPath();
    ctx.lineWidth = this.GENERAL_LINE_WIDTH;
    if(activo) ctx.strokeStyle = this.GENERAL_COLOR;
    else ctx.strokeStyle = this.GENERAL_COLOR_INACTIVE;
    ctx.moveTo(pPunto[0], pPunto[1]);
    ctx.lineTo(sPunto[0], sPunto[1]);
    ctx.lineTo(tPunto[0], tPunto[1]);
    ctx.closePath();
    ctx.stroke();
  }

  /* Dibuja un punto de control */
  dibujaControl(coordenadas: Coordenadas, ctx, activo) {
    // Dibuja el círculo
    ctx.beginPath();
    ctx.lineWidth = this.GENERAL_LINE_WIDTH;
    if(activo) ctx.strokeStyle = this.GENERAL_COLOR;
    else ctx.strokeStyle = this.GENERAL_COLOR_INACTIVE;
    ctx.arc(coordenadas.x, coordenadas.y, this.CONTROL_RADIUS, 0, 2 * Math.PI);
    ctx.stroke();
  }

  /* Dibuja un punto de meta */
  dibujaMeta(coordenadas: Coordenadas, ctx, activo) {
    // Dibuja los círculos exterior e interior
    ctx.beginPath();
    ctx.lineWidth = this.GENERAL_LINE_WIDTH;
    if(activo) ctx.strokeStyle = this.GENERAL_COLOR;
    else ctx.strokeStyle = this.GENERAL_COLOR_INACTIVE;
    ctx.arc(coordenadas.x, coordenadas.y, this.FINISH_EXT_RADIUS, 0, 2 * Math.PI);
    ctx.stroke();
    ctx.beginPath();
    ctx.arc(coordenadas.x, coordenadas.y, this.FINISH_INT_RADIUS, 0, 2 * Math.PI);
    ctx.stroke();
  }

  /* Limpia el lienzo del trazado */
  borraTrazado() {
    console.log("CLEAR (Trazado)"); // debug
    this.contextTrazado.clearRect(Math.abs(this.offsetX), Math.abs(this.offsetY), 
                this.canvasTrazado.nativeElement.width + Math.abs(this.offsetX), 
                this.canvasTrazado.nativeElement.height + Math.abs(this.offsetY));
  }

















  


  /* Devuelve el factor de escalado del zoom actual */
  getEscaladoZoom() {
    return 1 + this.ZOOM_SCALING * this.zoomLevel;
  }


  /* Coloca los lienzos móviles en la posición indicada por los offsets */
  desplazaCanvas() {
    this.canvasMapa.nativeElement.style.left = this.offsetX + "px";
    this.canvasMapa.nativeElement.style.top = this.offsetY + "px";
    this.canvasTrazado.nativeElement.style.left = this.offsetX + "px";
    this.canvasTrazado.nativeElement.style.top = this.offsetY + "px";
  }

  /* DEBUG: borrar */
  cargaMapaAutomatico() {
    this.imgMapa = new Image();
    this.imgMapaOrig = new Image();
    this.imgMapa.onload = (() => {
      this.setZoom(0, new Coordenadas(0,0), 0);
      this.actualizaLimites();
      //updateRecorrido();

      //this.offsetX = -(this.imgMapa.width / 2);
      //this.offsetY = -(this.imgMapa.height / 2);
      this.offsetX = -500;
      this.offsetY = -1000;
      this.desplazaCanvas(); // Actualiza posiciones de los canvas

      // TEST
      this.setupCanvas(true);
      this.redibujaMapa();
    });

    this.imgMapa.src = "assets/img/sample-map.jpg";
    this.imgMapaOrig.src = "assets/img/sample-map.jpg";
  }

  /* Actualiza los límites horizontal y vertical de desplazamiento de la vista */
  actualizaLimites() {
    this.MAX_OFFSET_X = -(this.imgMapa.width - this.vistaMapa.nativeElement.getBoundingClientRect().width);
    this.MAX_OFFSET_Y = -(this.imgMapa.height - this.vistaMapa.nativeElement.getBoundingClientRect().height);
    // Resitúa si al quitar zoom se está fuera del máximo
    if(this.offsetX > 0) this.offsetX = 0;
    if(this.offsetY > 0) this.offsetY = 0;
    if(this.offsetX < this.MAX_OFFSET_X) this.offsetX = this.MAX_OFFSET_X;
    if(this.offsetY < this.MAX_OFFSET_Y) this.offsetY = this.MAX_OFFSET_Y;
    // Recoloca los canvas por si acaso
    this.desplazaCanvas();
  }

  /* Resetea las configuraciones del sistema de drag/click */
  resetConfiguracionRaton() {
    this.clicking = this.moviendoMapa = this.moviendoControl = false;
    this.vistaMapa.nativeElement.style.cursor = "none";
  }

  /* Al redimensionarse, los lienzos son afectados por lo que se recarga su configuración. */
  onResize(event) {
    if(this.imgMapa) {
      this.setupCanvas(true);
      this.redibujaMapa();
      this.redibujaTrazado();
    }
  }

  /* Actualiza las métricas acorde al valor actual de MM_UNIT */
  actualizaMetricas() {
    this.MM_UNIT = this.getEscaladoZoom() * this.MM_UNIT_ORI;
    // Cálculo de medidas acorde a la ISOM 2017
    this.GENERAL_LINE_WIDTH = 0.35 * this.MM_UNIT;  // 0.35mm
    this.START_SIDE_LENGTH  = 6 * this.MM_UNIT;     
    this.CONTROL_RADIUS     = 5 * this.MM_UNIT / 2; 
    this.FINISH_EXT_RADIUS  = 6 * this.MM_UNIT / 2; 
    this.FINISH_INT_RADIUS  = 4 * this.MM_UNIT / 2; 
    this.NUMBER_SIZE        = 4 * this.MM_UNIT;     
    this.NUMBER_DISTANCE    = 6 * this.MM_UNIT;
  }

  /* Actualiza las dimensiones reales del lienzo según las de su vista actual */
  updateCanvasDims(canvas) {
    var rect = canvas.getBoundingClientRect();
    canvas.width = rect.width;
    canvas.height = rect.height;
  }

  // TEST
  updateCanvasDims2(canvas, width, height) {
    canvas.width = width;
    canvas.height = height;
  }



  /**
   * Devuelve las coordenadas corregidas en el lienzo de marcado teniendo en cuenta el zoom
   * y el desplazamiento actuales.
   * @param coordsMarcador Coordenadas en el lienzo de marcador
   */
  getCoordenadasSistema(coordsMarcador): Coordenadas {
    let x = (coordsMarcador.x + Math.abs(this.offsetX)) / this.getEscaladoZoom();
    let y = (coordsMarcador.y + Math.abs(this.offsetY)) / this.getEscaladoZoom();

    return new Coordenadas(x, y);
  }

  /**
   * Transforma las coordenadas del sistema a coordenadas ajustadas para el lienzo
   * de trazados según el nivel de zoom y desplazamiento actual.
   * @param coordsSistema Coordenadas del sistema
   */
  getCoordenadasCanvasTrazado(coordsSistema): Coordenadas {
    let x = coordsSistema.x * this.getEscaladoZoom();
    let y = coordsSistema.y * this.getEscaladoZoom();

    return new Coordenadas(x, y);
  }

  /**
   * Devuelve las coordenadas corregidas del lienzo de marcador.
   * @param event Evento generado en el lienzo de marcador
   */
  getCoordenadasCanvasMarcador(event): Coordenadas {
    var rectMarcador = this.canvasMarcador.nativeElement.getBoundingClientRect();
    let x = event.clientX - rectMarcador.left;
    let y = event.clientY - rectMarcador.top;

    return new Coordenadas(x, y);
  }
  
  /* Convierte grados a radianes */
  toRad(deg) {
    return deg * Math.PI / 180.0;
  }

}
