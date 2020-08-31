import { Component, OnInit, ElementRef, ViewChild, HostListener } from '@angular/core';
import { AlertService } from '../../../alert';
import { SharedEditorService } from '../shared-editor.service';
import { Control, Recorrido, AppSettings, Carrera } from 'src/app/shared/app.model';
import { NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { Router } from '@angular/router';
import { EditorTrazadoComponent } from '../editor-trazado/editor-trazado.component';
import { DataService } from 'src/app/shared/data.service';
import { FormBuilder } from '@angular/forms';

declare var $: any; // JQuery

@Component({
  selector: 'app-editor-recorridos',
  templateUrl: './editor-recorridos.component.html',
  styleUrls: ['./editor-recorridos.component.scss']
})
export class EditorRecorridosComponent implements OnInit {
  // Route params para discernir edición
  readonly EDICION_CONTROLES: string = "controles";
  readonly EDICION_RECORRIDOS: string = "recorridos";
  tipoEdicion: string;

  // Plantillas de códigos de salida/control/meta
  START_PRECODE = "S";
  START_MIN_CODE = 1;
  CONTROL_PRECODE = "";
  CONTROL_MIN_CODE = 31;
  FINISH_PRECODE = "M";
  FINISH_MIN_CODE = 1;

  RECORRIDO_DEFAULT_PRE = "R";
  MAX_RECORRIDOS = 10;
  MAX_CONTROLES_RECORRIDO = 32;
  MAX_CONTROLES_CARRERA = 50;
  MAX_PUNTUACION_CONTROL = 100;

  @ViewChild('inputPurplePen', {static: true}) inputPurplePen: ElementRef<HTMLInputElement>; 
  @ViewChild('inputMapa', {static: true}) inputMapa: ElementRef<HTMLInputElement>;
  @ViewChild('nombreRecorrido', {static: true}) nombreRecorrido: ElementRef<HTMLInputElement>;
  @ViewChild('modalControl', {static: true}) modalControl: ElementRef<NgbModal>;
  @ViewChild('modalRecorrido', {static: true}) modalRecorrido: ElementRef<NgbModal>;
  @ViewChild('modalEleccionInicial', {static: true}) modalEleccionInicial: ElementRef<NgbModal>;
  @ViewChild('modalCancelar', {static: true}) modalCancelar: ElementRef<NgbModal>;

  wrapperTrazador: ElementRef<HTMLDivElement>;
  @ViewChild('wrapperTrazador', {static: false}) set contentWrapperTrazador(content: ElementRef<HTMLDivElement>) {
    if(content) {
      // Maximiza el contenido
      let offset = content.nativeElement.offsetTop;
      let wHeight = window.innerHeight;
      content.nativeElement.style.height = (wHeight - offset - 20) + "px";
      this.wrapperTrazador = content;
    }
  }
  wrapperRecorridos: ElementRef<HTMLDivElement>;
  @ViewChild('wrapperRecorridos', {static: false}) set contentWrapperRecorridos(content: ElementRef<HTMLDivElement>) {
    if(content) {
      // Maximiza el contenido
      let offset = content.nativeElement.offsetTop;
      let wHeight = window.innerHeight;
      content.nativeElement.style.height = (wHeight - offset - 100) + "px";
      this.wrapperRecorridos = content;
    }
  }
  trazador: EditorTrazadoComponent;
  @ViewChild('trazador', {static: false}) set contentTrazador(content: ElementRef) {
    if(content) {
      this.trazador = content.nativeElement;
    }
  }

  carrera: Carrera;
  recorridos: Map<string, Recorrido>;
  recorridoActual: string;
  nombresRecorridos: string[];
  controles: Map<string, Control>;
  listaControlesOrdenados: Control[]; // Lista global ordenada de controles

  editandoNombreRecorrido: boolean;

  // Diálogos modales
  readonly ELECCION_SOLO_RECORRIDOS = "RECORRIDOS";
  readonly ELECCION_TRAZAR = "TRAZAR";
  eleccionTrazado;
  activeModal: NgbModalRef;
  tempControl: Control; // Temporal para la confirmación de borrado

  // Alertas
  alertOptions = {
    autoClose: true,
    keepAfterRouteChange: true
  };

  // TODO
  //   Máximo de una salida y una meta por carrera
  //   Mensaje de confirmación de abandono de página para prevenir perder los datos


  constructor(private sharedData: SharedEditorService,
    protected alertService: AlertService,
    private modalService: NgbModal,
    private router: Router,
    private formBuilder: FormBuilder,
    private data: DataService) { }

  ngOnInit() {
    this.recorridos = new Map<string, Recorrido>();
    this.controles = new Map<string, Control>();
    this.listaControlesOrdenados = [];
    this.recorridoActual = null;
    this.editandoNombreRecorrido = false;
    this.eleccionTrazado = null;
  
    // Diferencia entre edición de controles y de recorridos
    //  Controles: para carreras score. Solo se pueden añadir controles en mapa maestro.
    //  Recorridos: para carreras con trazados. Se pueden crear múltiples recorridos.
    if(window.location.toString().indexOf(this.EDICION_CONTROLES) > -1) {
      // Edición de controles
      this.tipoEdicion = this.EDICION_CONTROLES;
    } else if(window.location.toString().indexOf(this.EDICION_RECORRIDOS) > -1) {
      // Edición de recorridos
      this.tipoEdicion = this.EDICION_RECORRIDOS;
    } else {
      this.alertService.error("Error al cargar el editor.", this.alertOptions);
      this.router.navigate(["/"]);
    }

    try {
      // Inicia el modelo
      this.cargarCarrera();
    } catch (e) {
      // Error al cargar la carrera, borra y redirige a la creación
      this.alertService.error("Error al cargar los datos de la carrera :(", this.alertOptions);
      console.log(e);
      localStorage.removeItem(AppSettings.LOCAL_STORAGE_CARRERA);
      this.router.navigate(['/crear', 'nueva']);
      return;
    }
    
    // Hace que el editor sea tan grande como el resto de la pantalla restante (borrar)
    //var wHeight = $(window).height();
    //var offset = $("#wrapper-trazador").offset().top;
    //$("#wrapper-trazador").height(wHeight - offset - 20);

    // Controlador de adición de control
    this.sharedData.nuevoControl.subscribe((control) => {
      if(control !== null) {
        if(this.controles.size < this.MAX_CONTROLES_CARRERA) {
          // TODO Comprobar máximo de 1 salida y 1 meta

          // Obtiene el código del control y crea el control
          var codigo = this.pullCodigoControl(control.tipo);
          control.codigo = codigo;
          control.puntuacion = this.getPuntuacionControl(control);

          if(this.tipoEdicion === this.EDICION_RECORRIDOS && this.recorridoActual !== null) {
            if(this.recorridoActual.length < this.MAX_CONTROLES_RECORRIDO) {
              this.controles.set(codigo, control);
              this.sharedData.actualizaControles(this.controles);
              this.recorridos.get(this.recorridoActual).trazado.push(codigo);
              // Actualiza el recorrido
              this.sharedData.actualizaRecorrido(this.recorridos.get(this.recorridoActual));
              this.actualizaListaGlobalControles(control);
            } else {
              this.alertService.error("Has alcanzado el límite de controles permitidos en un recorrido (" + this.MAX_CONTROLES_RECORRIDO + ")", this.alertOptions);
            }
          } else {
            this.controles.set(codigo, control);
            this.sharedData.actualizaControles(this.controles);
            this.actualizaListaGlobalControles(control);
          }
        } else {
          this.alertService.error("Has alcanzado el límite de controles permitidos en una carrera (" + this.MAX_CONTROLES_CARRERA + ")", this.alertOptions);
        }        
      }
    });

    // Controlador de borrado de control
    this.sharedData.controlBorrado.subscribe((control) => {
      if(control !== null) {
        if(this.tipoEdicion === this.EDICION_CONTROLES) {
          this.borrarControl(control, true);
        } else {
          // Borra el control del recorrido actual
          var sinUsar = true;
          // Itera sobre el resto de recorridos para ver si está en ellos el control
          for(let [nombre, recorrido] of this.recorridos) {
            if(nombre != this.recorridoActual) {
              for(var i = 0; i < recorrido.trazado.length && sinUsar; i++) {
                if(recorrido.trazado[i] === control.codigo) {
                  sinUsar = false;
                }
              }
            }
          }

          if(sinUsar) {
            // Como solo está en el trazado actual, se borra de ambos
            this.borrarControl(control, true);
          } else {
            // Se usa en otro trazado
            if(this.recorridoActual === null) {
              // Se desea borrado global -> se confirma
              this.tempControl = control;
              this.modalService.open(this.modalControl, {centered: true, size: 'lg'});
              return;
            }

            this.borrarControl(control, false);
          }
          
          // Actualiza el trazador
          this.sharedData.actualizaRecorrido(this.recorridos.get(this.recorridoActual));
        }
        
        this.sharedData.actualizaControles(this.controles);
      }
    });

  }

  /**
   * Carga los controles y recorridos de la carrera del almacenamiento local.
   */
  cargarCarrera() {
    let jCarrera = localStorage.getItem(AppSettings.LOCAL_STORAGE_CARRERA);
    this.carrera = JSON.parse(jCarrera) as Carrera;

    this.recorridos = new Map<string, Recorrido>();
    this.controles = new Map<string, Control>();
    this.nombresRecorridos = [];
    this.recorridoActual = null;

    if(this.carrera) {
      // Genera mapas de controles y recorridos
      if(this.carrera.recorridos != null && this.carrera.controles != null
          && this.carrera.recorridos.length > 0 && this.carrera.controles.size > 0) {
        this.carrera.recorridos.forEach((recorrido, indice, array) => {
          this.recorridos.set(recorrido.nombre, recorrido);
          this.nombresRecorridos.push(recorrido.nombre);
        });
        this.controles = new Map(Object.entries(this.carrera.controles));

        this.generaListaGlobalControles();
        // Actualiza el modelo compartido con el editor de trazados
        this.sharedData.actualizaControles(this.controles);
      } else {
        // Primera creación
        this.eleccionTrazado = this.ELECCION_SOLO_RECORRIDOS; // DEBUG
        /*
        this.activeModal = this.modalService.open(this.modalEleccionInicial, {centered: true, size: 'lg', backdrop : 'static', keyboard : false, windowClass: 'modal-fit'});
        this.activeModal.result.then(
          (data) => {
            // Close
            console.log("Close: " + data);
            this.eleccionTrazado = data;
          }, (reason) => {
            if(reason === 0) {
              // Ha conseguido cerrar el diálogo modal obligatorio de elegir
              this.alertService.error("Debes elegir una opción", this.alertOptions);
              this.router.navigate(["/crear"]);
            }
          }
        );
        */
      }
    } else {
      throw "Error al cargar la carrera";
    }
  }

  /**
   * Finaliza la edición.
   */
  guardar() {
    // Comprueba datos correctos
    if(this.tipoEdicion === this.EDICION_RECORRIDOS) {
      if(this.recorridos.size > 0) {
        // TODO Recorridos inacabados
      } else {
        this.alertService.error("Debes crear por lo menos un recorrido", this.alertOptions);
        return;
      }
    } else {
      // Las carreras score solo tienen un recorrido
      this.recorridos.clear();
      this.recorridos.set(AppSettings.NOMBRE_RECORRIDO_SCORE, new Recorrido(AppSettings.NOMBRE_RECORRIDO_SCORE));
      // TODO Salida meta
    }

    let mapasTrazados: Map<string, string> = new Map();
    let generarMapas = true; // TODO Dar a elegir
    if(generarMapas) {
      let canvasMapa: ElementRef<HTMLCanvasElement> = this.trazador.getCanvasMapaBase();
      if(this.tipoEdicion === this.EDICION_RECORRIDOS) {

        try {
          let canvasMapa: ElementRef<HTMLCanvasElement> = this.trazador.getCanvasMapaBase();
          // Genera las imágenes de los mapas con los trazados
          for(let recorrido of this.recorridos.values()) {
            // Crea un nuevo canvas para el trazado del recorrido, clonando el del mapa base
            let canvas = document.createElement('canvas');
            let canvasContext = canvas.getContext('2d');
            canvas.width = canvasMapa.nativeElement.width;
            canvas.height = canvasMapa.nativeElement.height;
            canvasContext.drawImage(canvasMapa.nativeElement, 0, 0);
            // Dibuja el trazado del recorrido en el nuevo canvas
            this.trazador.dibujaTrazado(canvasContext, recorrido.trazado, this.controles);
            // Obtiene la imagen resultante y la asigna al mapa
            mapasTrazados.set(recorrido.nombre, canvas.toDataURL('image/jpeg', AppSettings.CANVAS_MAPAS_RESOLUCION));
          }
        } catch (e) {
          this.alertService.error("No se pudieron generar los mapas con los recorridos", this.alertOptions);
          console.log(e);
        }
      } else {
        // Crea un nuevo canvas para el mapa maestro, clonando el del mapa base
        try {
          let canvas = document.createElement('canvas');
          let canvasContext = canvas.getContext('2d');
          canvas.width = canvasMapa.nativeElement.width;
          canvas.height = canvasMapa.nativeElement.height;
          canvasContext.drawImage(canvasMapa.nativeElement, 0, 0);
          // Dibuja todos los controles de la carrera
          //  TODO
          // Obtiene la imagen resultante y la asigna al mapa
          mapasTrazados.set(AppSettings.NOMBRE_RECORRIDO_SCORE, canvas.toDataURL('image/jpeg', AppSettings.CANVAS_MAPAS_RESOLUCION));
        } catch (e) {
          this.alertService.error("No se pudo generar el mapa con los controles", this.alertOptions);
          console.log(e);
        }
      }
    }

    // Guarda los mapas
    this.data.setMapasTrazados(mapasTrazados);
    // Guarda el borrador
    this.guardaBorrador();
    let mensaje = "Recorridos guardados";
    if(this.tipoEdicion === this.EDICION_CONTROLES) mensaje = "Controles guardados";
    this.alertService.success(mensaje, this.alertOptions);
    // Redirige al resumen de carrera
    this.router.navigate(['/crear']);
  }

  /**
   * Maneja el click en el botón de cancelar.
   */
  clickCancelar(): void {
    this.activeModal = this.modalService.open(this.modalCancelar, {centered: true, size: 'lg'});
  }

  /**
   * Cancela la edición.
   */
  cancelar(): void {
    // TODO Mensaje de confirmación
    this.router.navigate(['/crear']);
  }

  /**
   * Genera una lista global de controles (sin salidas o metas).
   */
  generaListaGlobalControles(): void {
    let array = Array.from(this.controles.values()).filter((control: Control) => (control.tipo === Control.TIPO_CONTROL));
    this.listaControlesOrdenados = array.sort((a, b) => (a.codigo > b.codigo) ? 1 : -1);
  }

  /**
   * Añade un nuevo control y reordena una lista global de controles (sin salidas o metas).
   * @param control Nuevo control
   */
  actualizaListaGlobalControles(control: Control): void {
    if(control.tipo === Control.TIPO_CONTROL) {
      // Omite salida y meta
      this.listaControlesOrdenados.push(control);
      this.listaControlesOrdenados.sort((a, b) => (a.codigo > b.codigo) ? 1 : -1);
    }
  }

  /**
   * Tras seleccionar un archivo de PurplePen, se importan sus recorridos y se reflejan en el mapa.
   * @param event Evento de selección de archivo
   
  importarPurplePen(event) {
    if(!this.checkFileAPIs()) return;

    let file : File = event.target.files[0];
    if(file) {
      var reader = new FileReader();

      reader.onload = function (e) {
        // Comprobar que se trata de un archivo válido PurplePen
        // TODO

        // Procesar los recorridos
        // TODO

        // Reflejar cambios en la vista
        // TODO
      }

      reader.readAsDataURL(file);
    } else {
      this.alertService.error("Ocurrió un error al importar el archivo.");
    }
  }*/

  /**
   * Carga el mapa seleccionado en el editor de trazados.
   * @param event Evento de selección de archivo
   */
  cargarMapa(event) {
    if(!this.checkFileAPIs()) return;
    if(event.target.files && event.target.files[0]) {
      const reader = new FileReader();
      reader.addEventListener('load', (event: any) => {
        // TODO Aplicar restricciones/validaciones
        this.trazador.cargaMapa(event.target.result);
      });
      reader.readAsDataURL(event.target.files[0]);
    } else {
      this.alertService.error("Ocurrió un error al cargar el mapa.");
    }
  }

  /**
   * Muestra todos los controles de la carrera.
   */
  todosLosControles() {
    this.recorridoActual = null;
    this.sharedData.actualizaRecorrido(null);
  }

  /**
   * Selecciona un recorrido de la lista.
   * @param nombre Nombre del recorrido seleccionado
   */
  seleccionaRecorrido(nombre) {
    if(this.recorridoActual !== nombre) {
      this.recorridoActual = nombre;
      this.sharedData.actualizaRecorrido(this.recorridos.get(nombre));
    }
  }

  /* Añade un nuevo recorrido */
  nuevoRecorrido() {
    if(this.recorridos.size < this.MAX_RECORRIDOS && this.tipoEdicion == this.EDICION_RECORRIDOS) {
      // Busca nombre por defecto para el nuevo recorrido
      var nombre = null;
      var i = 1;
      while(nombre === null) {
        if(!this.recorridos.has(this.RECORRIDO_DEFAULT_PRE + i)) {
          nombre = this.RECORRIDO_DEFAULT_PRE + i;
        }
        i++;
      }
      
      this.recorridos.set(nombre, new Recorrido(nombre));
      this.seleccionaRecorrido(nombre);
      this.nombresRecorridos.push(nombre); // Optimización
    }
  }

  /**
   * Habilita/deshabilita la edición del nombre de un recorrido.
   * Si se deshabilita, se actualiza el nombre del recorrido.
   * @param valor True para habilitar la edición, false para guardar el nombre
   */
  editarNombreRecorrido(valor: boolean) {
    let nuevoNombre = this.nombreRecorrido.nativeElement.value;
    if(!valor && this.recorridoActual !== null && this.recorridoActual !== nuevoNombre) {
      if(nuevoNombre.trim().length > 0) {
        if(!this.recorridos.has(nuevoNombre)) {
          // Se actualiza el nombre del recorrido
          let recorrido = this.recorridos.get(this.recorridoActual);
          recorrido.nombre = nuevoNombre;
          this.recorridos.delete(this.recorridoActual);
          this.recorridos.set(nuevoNombre, recorrido);
          // Actualiza el nombre también en el array de optimización de nombres
          let index = this.nombresRecorridos.indexOf(this.recorridoActual)
          this.nombresRecorridos[index] = nuevoNombre;
          // Vuelve a seleccionar el recorrido
          this.seleccionaRecorrido(nuevoNombre);
          this.editandoNombreRecorrido = valor;
        } else {
          // Otro recorrido tiene ese nombre
          this.alertService.error("Ya existe un recorrido con ese nombre", this.alertOptions);
        }
      } else {
        // No se permite un nombre vacío
        this.alertService.error("El nombre no puede estar vacío", this.alertOptions);
      }
    } else {
      this.editandoNombreRecorrido = valor;
    }
  }

  /* Muestra un diálogo de confirmación de borrado del recorrido actual */
  borrarRecorrido() {
    // Muestra diálogo de confirmación
    this.modalService.open(this.modalRecorrido, {centered: true, size: 'lg'});
  }

  /* Borra el recorrido actualmente seleccionado */
  borrarRecorridoConfirmado() {
    this.modalService.dismissAll();

    if(this.recorridoActual !== null) {
      // Borra el nombre del recorrido de la lista de optimización de la vista
      let index = this.nombresRecorridos.indexOf(this.recorridoActual);
      if (index > -1) {
        this.nombresRecorridos.splice(index, 1);
      }

      // Borra el recorrido
      this.recorridos.delete(this.recorridoActual);
    }

    // Muestra la vista general de controles
    this.recorridoActual = null;
    this.sharedData.actualizaRecorrido(null);
  }

  /* Confirmación de borrado total de control */
  borrarControlConfirmado() {
    this.modalService.dismissAll();
    this.borrarControl(this.tempControl, true);
  }

  /**
   * Borra un control del recorrido actual o de todos.
   * @param control Control a borrar
   * @param borradoGeneral True para borrado total de todos los recorridos, false solo para le actual
   */
  borrarControl(control: Control, borradoGeneral: boolean) {
    if(borradoGeneral) {
      // Borra el control de todos los recorridos
      this.recorridos.forEach((value, key, map) => {
        var index = value.trazado.indexOf(control.codigo);
        if(index !== -1) value.trazado.splice(index, 1);
      });
      // Borra de la lista general de controles
      this.controles.delete(control.codigo);
      this.listaControlesOrdenados.splice(this.listaControlesOrdenados.indexOf(control), 1);
    } else {
      // Lo elimina solo del recorrido actual
      var index = this.recorridos.get(this.recorridoActual).trazado.indexOf(control.codigo);
      this.recorridos.get(this.recorridoActual).trazado.splice(index, 1);
    }

    this.sharedData.confirmaBorrado(control.tipo);
  }

  /**
   * Devuelve el siguiente código de control libre, dependiendo del tipo.
   * @param tipo Tipo de control
   */
  pullCodigoControl(tipo: String) {
    var codigo = null, aux;
    switch(tipo) {
        case Control.TIPO_SALIDA:
          aux = this.START_MIN_CODE;
          while(codigo === null) {
            if(!this.controles.has(this.START_PRECODE + aux)) codigo = this.START_PRECODE + aux;
            aux++;
          }
          break;
        case Control.TIPO_CONTROL:
          aux = this.CONTROL_MIN_CODE;
          while(codigo === null) {
            if(!this.controles.has(this.CONTROL_PRECODE + aux)) codigo = this.CONTROL_PRECODE + aux;
            aux++;
          }
          break;
        case Control.TIPO_META:
          aux = this.FINISH_MIN_CODE;
          while(codigo === null) {
            if(!this.controles.has(this.FINISH_PRECODE + aux)) codigo = this.FINISH_PRECODE + aux;
            aux++;
          }
          break;
    }
    
    return codigo;
  }
  
  /**
   * Devuelve una puntuación por defecto para un control. Si es de tipo CONTROL, su puntuación
   * será el valor de las decenas de su código de control.
   * @param control Control
   */
  getPuntuacionControl(control: Control): number {
    let puntuacion = 0;
    if(control.tipo === Control.TIPO_CONTROL) {
      let punt = parseInt(control.codigo);
      if(!isNaN(punt)) {
          puntuacion = Math.floor(punt / 10);
      }
    }

    return puntuacion;
  }

  decPuntuacionControl(control: Control): void {
    if(control.puntuacion > 0) {
      control.puntuacion--;
    }
  }

  incPuntuacionControl(control: Control): void {
    if(control.puntuacion < this.MAX_PUNTUACION_CONTROL) {
      control.puntuacion++;
    } else {
      this.alertService.error("Puntuación máxima alcanzada (" + this.MAX_PUNTUACION_CONTROL + ")", this.alertOptions);
    }
  }

  cambiaPuntuacionControl(control: Control, event): void {
    let value = parseInt(event.target.value);
    if(isNaN(value)) {
      control.puntuacion = 0;
    } else {
      if(value < 0) value = 0;
      if(value > this.MAX_PUNTUACION_CONTROL) value = this.MAX_PUNTUACION_CONTROL;
      control.puntuacion = value;
    }
  }

  /**
   * Devuelve true si el navegador soporta las APIs de gestión de archivos, false en caso contrario.
   */
  checkFileAPIs(): boolean {
    /* // TODO Fix para Angular "Property 'File' does not exist on type 'Window'"
    let w = document.defaultView;
    if (!w.File || !w.FileReader || !w.FileList || !w.Blob) {
      this.alertService.error('Tu navegador no soporta la tecnología necesaria. Prueba con otro más moderno.');
      return false;
    }
    */

    return true;
  }
  
  /**
   * Guarda el borrador actual de la carrera en el almacenamiento local.
   */
  guardaBorrador() {
    this.carrera.recorridos = Array.from(this.recorridos.values());

    let jControles = {};
    for(let k of this.controles.keys()) {
      jControles[k] = this.controles.get(k);
    }
    this.carrera.controles = jControles as Map<any, any>;
    localStorage.setItem(AppSettings.LOCAL_STORAGE_CARRERA, JSON.stringify(this.carrera));
  }

}
