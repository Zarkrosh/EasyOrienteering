import { Component, OnInit, ElementRef, ViewChild, HostListener } from '@angular/core';
import { AlertService } from '../../../alert';
import { SharedEditorService } from '../shared-editor.service';
import { Control, Recorrido, AppSettings, Carrera } from 'src/app/_shared/app.model';
import { NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { Router } from '@angular/router';
import { EditorTrazadoComponent } from '../editor-trazado/editor-trazado.component';
import { DataService } from 'src/app/_services/data.service';
import { FormBuilder } from '@angular/forms';
import { FooterService } from 'src/app/_services/footer.service';

@Component({
  selector: 'app-editor-recorridos',
  templateUrl: './editor-recorridos.component.html',
  styleUrls: ['./editor-recorridos.component.scss']
})
export class EditorRecorridosComponent implements OnInit {
  // Constantes externas
  readonly CODIGO_SALIDA = AppSettings.CODIGO_SALIDA;
  readonly CODIGO_META = AppSettings.CODIGO_META;
  // Route params para discernir edición
  readonly EDICION_CONTROLES: string = "controles";
  readonly EDICION_RECORRIDOS: string = "recorridos";
  tipoEdicion: string;
  // Regexp numérica
  readonly REGEXP_NUMERICA = /^\d+$/;

  // Plantillas de códigos de salida/control/meta
  CONTROL_MIN_CODE: number = 31;

  RECORRIDO_DEFAULT_PRE = "R";
  MAX_RECORRIDOS = 10;
  MAX_RECORRIDOS_SCORE = 1;
  MAX_RECORRIDOS_TRAZADO = 10;
  MAX_CONTROLES_RECORRIDO = 32;
  MAX_CONTROLES_CARRERA = 50;
  MAX_PUNTUACION_CONTROL = 100;

  @ViewChild('inputPurplePen', {static: true}) inputPurplePen: ElementRef<HTMLInputElement>; 
  @ViewChild('inputMapa', {static: true}) inputMapa: ElementRef<HTMLInputElement>;
  nombreRecorrido: ElementRef<HTMLInputElement>;
  @ViewChild('nombreRecorrido', {static: false}) set contentNombreRecorrido(content: ElementRef<HTMLInputElement>) {
    if(content) this.nombreRecorrido = content;
  }

  wrapperTrazador: ElementRef<HTMLDivElement>;
  @ViewChild('wrapperTrazador', {static: false}) set contentWrapperTrazador(content: ElementRef<HTMLDivElement>) {
    if(content && !this.wrapperTrazador) {
      // Maximiza el contenido
      let offset = content.nativeElement.offsetTop;
      let wHeight = window.innerHeight;
      content.nativeElement.style.height = (wHeight - offset - 20) + "px";
      this.wrapperTrazador = content;
    }
  }
  wrapperRecorridos: ElementRef<HTMLDivElement>;
  @ViewChild('wrapperRecorridos', {static: false}) set contentWrapperRecorridos(content: ElementRef<HTMLDivElement>) {
    if(content && !this.wrapperRecorridos) {
      // Maximiza el contenido
      let offset = content.nativeElement.offsetTop;
      let wHeight = window.innerHeight;
      content.nativeElement.style.height = (wHeight - offset - 20) + "px";
      this.wrapperRecorridos = content;
    }
  }
  trazador: EditorTrazadoComponent;
  @ViewChild('trazador', {static: false}) set contentTrazador(content: EditorTrazadoComponent) {
    if(content && !this.trazador) {
      this.trazador = content;
    }
  }

  carrera: Carrera;
  recorridos: Map<string, Recorrido>;
  recorridoActual: Recorrido;
  nombresRecorridos: string[];
  controles: Map<string, Control>;
  listaControlesOrdenados: Control[]; // Lista global ordenada de controles

  editandoNombreRecorrido: boolean;

  // Diálogos modales
  readonly ELECCION_NO_TRAZAR = "NO TRAZAR";
  readonly ELECCION_TRAZAR = "TRAZAR";
  @ViewChild('modalControl', {static: true}) modalControl: ElementRef<NgbModal>;
  @ViewChild('modalRecorrido', {static: true}) modalRecorrido: ElementRef<NgbModal>;
  @ViewChild('modalEleccionInicial', {static: true}) modalEleccionInicial: ElementRef<NgbModal>;
  @ViewChild('modalCancelar', {static: true}) modalCancelar: ElementRef<NgbModal>;
  eleccionTrazar: string;
  activeModal: NgbModalRef;
  tempControl: Control; // Temporal para la confirmación de borrado

  // Alertas
  alertOptions = {
    autoClose: true,
    keepAfterRouteChange: true
  };

  constructor(private dataEditores: SharedEditorService,
    protected alertService: AlertService,
    private modalService: NgbModal,
    private router: Router,
    private formBuilder: FormBuilder,
    private data: DataService,
    private footer: FooterService) { }

  ngOnInit() {
    this.footer.hide();
    this.recorridos = new Map<string, Recorrido>();
    this.controles = new Map<string, Control>();
    this.listaControlesOrdenados = [];
    this.recorridoActual = null;
    this.editandoNombreRecorrido = false;
    this.eleccionTrazar = null;
  
    // Diferencia entre edición de controles y de recorridos
    //  Controles: para carreras score. Solo se pueden añadir controles en mapa maestro.
    //  Recorridos: para carreras con trazados. Se pueden crear múltiples recorridos.
    if(window.location.toString().indexOf(this.EDICION_CONTROLES) > -1) {
      // Edición de controles
      this.tipoEdicion = this.EDICION_CONTROLES;
      this.MAX_RECORRIDOS = this.MAX_RECORRIDOS_SCORE;
    } else if(window.location.toString().indexOf(this.EDICION_RECORRIDOS) > -1) {
      // Edición de recorridos
      this.tipoEdicion = this.EDICION_RECORRIDOS;
      this.MAX_RECORRIDOS = this.MAX_RECORRIDOS_TRAZADO;
    } else {
      this.alertService.error("Error al cargar el editor.", this.alertOptions);
      this.router.navigate(["/"]);
    }

    try {
      // Inicia el modelo
      this.cargaCarrera();
    } catch (e) {
      // Error al cargar la carrera, borra y redirige a la creación
      this.alertService.error("Error al cargar los datos de la carrera ☹", this.alertOptions);
      console.log(e);
      localStorage.removeItem(AppSettings.LOCAL_STORAGE_CARRERA);
      this.router.navigate(['/crear', 'nueva']);
      return;
    }
    
    
    this.dataEditores.clearData();
    // Controlador de adición de control
    this.dataEditores.nuevoControl.subscribe((control) => {
      if(control !== null) {
        console.log("[RECORRIDOS] Nueva petición de control:");
        console.log(control);
        if(this.controles.size < this.MAX_CONTROLES_CARRERA) {
          if(control.tipo === Control.TIPO_SALIDA && this.controles.has(this.CODIGO_SALIDA)) {
            this.alertService.error("Solo puede haber una salida por carrera.", this.alertOptions);
          } else if(control.tipo === Control.TIPO_META && this.controles.has(this.CODIGO_META)) {
            this.alertService.error("Solo puede haber una meta por carrera.", this.alertOptions);
          } else {
            // Obtiene el código del control y crea el control
            var codigo = this.pullCodigoControl(control.tipo);
            control.codigo = codigo;
            control.puntuacion = this.getPuntuacionControl(control);

            if(this.tipoEdicion === this.EDICION_RECORRIDOS && this.recorridoActual !== null) {
              if(this.recorridoActual.trazado.length < this.MAX_CONTROLES_RECORRIDO) {
                if(this.recorridoActual.trazado.length == 0 || this.recorridoActual.trazado[this.recorridoActual.trazado.length-1] !== this.CODIGO_META) {
                  this.controles.set(codigo, control);
                  this.dataEditores.setControles(this.controles);
                  this.recorridoActual.trazado.push(codigo);
                  this.actualizaListaGlobalControles(control);
                }
              } else {
                this.alertService.error("Has alcanzado el límite de controles permitidos en un recorrido (" + this.MAX_CONTROLES_RECORRIDO + ")", this.alertOptions);
              }
            } else {
              this.controles.set(codigo, control);
              this.dataEditores.setControles(this.controles);
              this.actualizaListaGlobalControles(control);
            } 
          }
        } else {
          this.alertService.error("Has alcanzado el límite de controles permitidos en una carrera (" + this.MAX_CONTROLES_CARRERA + ")", this.alertOptions);
        }        
      }
    });

    // Controlador de borrado de control
    this.dataEditores.controlBorrado.subscribe((control) => {
      if(control !== null) {
        if(this.tipoEdicion === this.EDICION_CONTROLES) {
          this.borrarControl(control, true);
        } else {
          this.borrarControl(control, false);
        }
        
        //this.dataEditores.setControles(this.controles);
      }
    });

  }

  /**
   * Carga los controles y recorridos de la carrera del almacenamiento local.
   */
  cargaCarrera() {
    let jCarrera = localStorage.getItem(AppSettings.LOCAL_STORAGE_CARRERA);
    this.carrera = JSON.parse(jCarrera) as Carrera;

    this.recorridos = new Map<string, Recorrido>();
    this.controles = new Map<string, Control>();
    this.nombresRecorridos = [];
    this.recorridoActual = null;

    if(this.carrera) {
      // Genera mapas de controles y recorridos
      if(this.carrera.recorridos != null && this.carrera.controles != null
          && this.carrera.recorridos.length > 0) {
        // Edición
        this.carrera.recorridos.forEach((recorrido, indice, array) => {
          this.recorridos.set(recorrido.nombre, recorrido);
          this.nombresRecorridos.push(recorrido.nombre);
        });
        this.controles = new Map(Object.entries(this.carrera.controles));
        this.eleccionTrazar = (this.carrera.soloRecorridos) ? this.ELECCION_NO_TRAZAR : this.ELECCION_TRAZAR;
        this.recorridoActual = this.carrera.recorridos[0];
        this.generaListaGlobalControles();
        // Actualiza el modelo compartido con el editor de trazados
        this.dataEditores.setControles(this.controles);
        if(!this.carrera.soloRecorridos) {
          // Carga mapa base (si lo hay)
          this.data.mapaBase.subscribe((mapa) => {
            if(mapa !== null && mapa.length > 0) {
              // Carga mapa base
              this.dataEditores.cambiarMapaBase(mapa);
            } else {
              this.alertService.error("No se pudo cargar el mapa anterior. Vuelve a cargarlo.", this.alertOptions);
            }
          });
        }
      } else {
        // Primera creación
        this.dataEditores.setControles(new Map<string, Control>());
        this.dataEditores.cambiarMapaBase("");
        this.dataEditores.setRecorridoActual(null);
        this.activeModal = this.modalService.open(this.modalEleccionInicial, {centered: true, size: 'lg', backdrop : 'static', keyboard : false, windowClass: 'modal-fit'});
        this.activeModal.result.then(
          (data) => {
            // Close
            this.eleccionTrazar = data;
            if(this.eleccionTrazar === this.ELECCION_NO_TRAZAR) {
              // Añade salida y meta
              this.controles.set(this.CODIGO_SALIDA, new Control(this.CODIGO_SALIDA, Control.TIPO_SALIDA, null));
              this.controles.set(this.CODIGO_META, new Control(this.CODIGO_META, Control.TIPO_META, null));
            }
            if(this.tipoEdicion === this.EDICION_RECORRIDOS) {
              this.nuevoRecorrido();
            } else {
              // Recorrido de carrera SCORE
              let nombre = "SCORE";
              this.recorridos.set(nombre, new Recorrido(nombre));
              this.seleccionaRecorrido(nombre);
              this.nombresRecorridos.push(nombre);
              this.recorridoActual.trazado.push(this.CODIGO_SALIDA);
              this.recorridoActual.trazado.push(this.CODIGO_META);
            }
          }, (reason) => {
            if(reason === 0) {
              // Ha conseguido cerrar el diálogo modal obligatorio de elegir
              this.alertService.error("Debes elegir una opción", this.alertOptions);
              this.router.navigate(["/crear"]);
            }
          }
        );

        // TODO Solucionar
        if(this.tipoEdicion === this.EDICION_CONTROLES) {
          // Existe un bug en el trazador, no se da a elegir
          this.activeModal.close(this.ELECCION_NO_TRAZAR);
        }
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
        if(!this.controles.has(this.CODIGO_SALIDA)) {
          this.alertService.error("Debes indicar una salida", this.alertOptions);
          return;
        } else if(!this.controles.has(this.CODIGO_META)) {
          this.alertService.error("Debes indicar una meta", this.alertOptions);
          return;
        } else {
          let vacios = new Set<string>();
          for(let recorrido of this.recorridos.values()) {
            let trazado = recorrido.trazado;
            if(trazado.length == 0) {
              // Recorrido vacío, se añade para borrar
              vacios.add(recorrido.nombre);
            } else if(trazado.indexOf(this.CODIGO_META) < 0) {
              // No tiene meta, se añade. ¿Se puede?
              if(trazado.length >= this.MAX_CONTROLES_RECORRIDO) {
                // Está lleno, se borra el último
                trazado.pop();
              }
              trazado.push(this.CODIGO_META);
              console.log("[*] Recorrido " + recorrido.nombre + " sin finalizar, finalizado automáticamente.");
            }
          }

          for(let vacio of vacios) {
            console.log("[*] Borrando recorrido vacío: " + vacio);
            this.nombresRecorridos.splice(this.nombresRecorridos.indexOf(vacio), 1);
            this.recorridos.delete(vacio);
          }
        }
      } else {
        this.alertService.error("Debes crear por lo menos un recorrido", this.alertOptions);
        return;
      }
    }

    if(this.eleccionTrazar === this.ELECCION_TRAZAR) {
      let mapasTrazados: Map<string, string> = new Map();
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

      // Guarda los mapas en el servicio de datos compartido
      this.data.setMapaBase(this.trazador.getImagenMapaBase()); // TODO esto afecta al trazador, depurar
      this.data.setMapasTrazados(mapasTrazados);
    } else {
      // ELECCION_SOLO_RECORRIDOS
      // TODO ¿Hace falta algo?
    }

    // Guarda el borrador
    this.guardaBorrador();
    let mensaje = "Recorridos guardados";
    if(this.tipoEdicion === this.EDICION_CONTROLES) mensaje = "Controles guardados";
    this.alertService.success(mensaje, this.alertOptions);
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
        this.dataEditores.cambiarMapaBase(event.target.result);
      });
      reader.readAsDataURL(event.target.files[0]);
    } else {
      this.alertService.error("Ocurrió un error al cargar el mapa.");
    }
  }

  /**
   * Selecciona un recorrido de la lista.
   * @param nombre Nombre del recorrido seleccionado
   */
  seleccionaRecorrido(nombre: string) {
    if(!this.recorridoActual || this.recorridoActual.nombre !== nombre) {
      this.recorridoActual = this.recorridos.get(nombre);
      this.dataEditores.setRecorridoActual(this.recorridoActual);
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

      if(this.controles.has(this.CODIGO_SALIDA)) {
        // Asigna el control de salida automáticamente
        this.recorridoActual.trazado.push(this.CODIGO_SALIDA);
      }
      if(this.eleccionTrazar === this.ELECCION_NO_TRAZAR && this.controles.has(this.CODIGO_META)) {
        this.recorridoActual.trazado.push(this.CODIGO_META);
      }
    } else {
      this.alertService.error("No se pueden añadir más recorridos", this.alertOptions);
    }
  }

  /**
   * Habilita/deshabilita la edición del nombre de un recorrido.
   * Si se deshabilita, se actualiza el nombre del recorrido.
   * @param editar True para habilitar la edición, false para guardar el nombre
   */
  editarNombreRecorrido(editar: boolean) {
    let nuevoNombre = this.nombreRecorrido.nativeElement.value.trim();
    if(!editar && this.recorridoActual !== null && this.recorridoActual.nombre !== nuevoNombre) {
      if(nuevoNombre.length > 0) {
        if(!this.recorridos.has(nuevoNombre)) {
          // Se actualiza el nombre del recorrido
          let prevNombre = this.recorridoActual.nombre;
          let recorrido = this.recorridos.get(prevNombre);
          this.recorridos.delete(prevNombre); // Borra la entrada en el mapa con el nombre anterior
          recorrido.nombre = nuevoNombre;
          this.recorridos.set(nuevoNombre, recorrido); // Inserta la nueva entrada con el nuevo nombre
          // Actualiza el nombre también en el array de optimización de nombres
          let index = this.nombresRecorridos.indexOf(prevNombre);
          this.nombresRecorridos[index] = nuevoNombre;
          // Vuelve a seleccionar el recorrido
          this.seleccionaRecorrido(nuevoNombre);
          this.editandoNombreRecorrido = editar;
        } else {
          // Otro recorrido tiene ese nombre
          this.alertService.error("Ya existe un recorrido con ese nombre", this.alertOptions);
        }
      } else {
        // No se permite un nombre vacío
        this.alertService.error("El nombre no puede estar vacío", this.alertOptions);
      }
    } else {
      this.editandoNombreRecorrido = editar;
    }
  }

  /* Muestra un diálogo de confirmación de borrado del recorrido actual */
  borrarRecorrido() {
    // Muestra diálogo de confirmación
    this.activeModal = this.modalService.open(this.modalRecorrido, {centered: true, size: 'lg'});
  }

  /* Borra el recorrido actualmente seleccionado */
  borrarRecorridoConfirmado() {
    this.modalService.dismissAll();

    if(this.recorridoActual !== null) {
      if(this.recorridos.size > 1) {
        // Borra el nombre del recorrido de la lista de optimización de la vista
        let index = this.nombresRecorridos.indexOf(this.recorridoActual.nombre);
        if (index > -1) {
          this.nombresRecorridos.splice(index, 1);
        }

        // Borra el recorrido
        this.recorridos.delete(this.recorridoActual.nombre);
        this.seleccionaRecorrido(this.nombresRecorridos[0]);
      } else {
        this.alertService.error("Debe haber por lo menos un recorrido", this.alertOptions);
      }
    }
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
      var sinUsar = true;
      // Itera sobre el resto de recorridos para ver si está en ellos el control
      for(let [nombre, recorrido] of this.recorridos) {
        if(nombre != this.recorridoActual.nombre) {
          for(var i = 0; i < recorrido.trazado.length && sinUsar; i++) {
            if(recorrido.trazado[i] === control.codigo) {
              sinUsar = false;
            }
          }
        }
      }

      if(sinUsar) {
        // Como solo está en el trazado actual, se borra por completo
        this.borrarControl(control, true);
      } else {
        // Se usa en otro trazado
        if(this.recorridoActual === null) {
          // Se desea borrado global -> se confirma
          this.tempControl = control;
          this.activeModal = this.modalService.open(this.modalControl, {centered: true, size: 'lg'});
          return;
        }

        // Lo elimina solo del recorrido actual
        var index = this.recorridoActual.trazado.indexOf(control.codigo);
        this.recorridoActual.trazado.splice(index, 1);
      }
      
      // Actualiza el trazador
      //this.sharedData.setRecorridoActual(this.recorridoActual);
    }

    if(this.eleccionTrazar === this.ELECCION_TRAZAR) {
      if(this.trazador) this.trazador.confirmaBorrado(control.tipo);
    }
  }

  /**
   * Devuelve el siguiente código de control libre, dependiendo del tipo.
   * @param tipo Tipo de control
   */
  pullCodigoControl(tipo: String): string {
    var codigo = null, aux;
    switch(tipo) {
        case Control.TIPO_SALIDA:
          if(!this.controles.has(this.CODIGO_SALIDA)) {
            codigo = this.CODIGO_SALIDA;
          }
          break;
        case Control.TIPO_CONTROL:
          aux = this.CONTROL_MIN_CODE;
          while(codigo === null) {
            if(!this.controles.has(aux.toString())) codigo = aux.toString();
            aux++;
          }
          break;
        case Control.TIPO_META:
          if(!this.controles.has(this.CODIGO_META)) {
            codigo = this.CODIGO_META;
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


  keyUpInputControl(evento: any): void {
    if(evento.key === "Enter") {
      // ENTER: añade nuevo control
      if(this.recorridoActual.trazado.length < this.MAX_CONTROLES_RECORRIDO || this.controles.size < this.MAX_CONTROLES_CARRERA) {
        let codigo: string = evento.target.value.trim();
        if(codigo.length > 0) {
          codigo = codigo.substring(0, 10);
          if(codigo.length >= 2) {
            if(codigo !== this.CODIGO_SALIDA && codigo !== this.CODIGO_META) {
              if(this.REGEXP_NUMERICA.test(codigo)) {
                // Los controles numéricos deben tener un valor > 30
                if(parseInt(codigo) <= 30) {
                  this.alertService.error("Los códigos de control numéricos deben ser > 30.", this.alertOptions);
                  return;
                }
              }

              // TODO ¿Alguna validación más?
              if(this.tipoEdicion === this.EDICION_CONTROLES) {
                // SCORE
                if(!this.controles.get(codigo)) {
                  // Nuevo control
                  let control = new Control(codigo, Control.TIPO_CONTROL, null);
                  control.puntuacion = this.getPuntuacionControl(control);
                  this.controles.set(codigo, control);
                  this.actualizaListaGlobalControles(control);
                  evento.target.value = "";
                } else {
                  this.alertService.error("Control ya existente", this.alertOptions); 
                }
              } else {
                // TRAZADO
                if(this.recorridoActual.trazado.length === 0 || this.recorridoActual.trazado[this.recorridoActual.trazado.length-1] !== codigo) {
                  if(!this.controles.get(codigo)) {
                    // Nuevo control
                    this.controles.set(codigo, new Control(codigo, Control.TIPO_CONTROL, null));
                  }
                  // Añade al recorrido en la penúltima posición
                  this.recorridoActual.trazado.splice(this.recorridoActual.trazado.length-1, 0, codigo);
                  if(this.recorridoActual.trazado.length == this.MAX_CONTROLES_RECORRIDO) {
                    this.alertService.warn("Máximo de controles por recorrido alcanzado.", this.alertOptions);
                  }
                  evento.target.value = "";
                } else {
                  this.alertService.error("Elige otro control.", this.alertOptions);
                }
              }
            } else {
              this.alertService.error("Código de control no válido.", this.alertOptions);
              evento.target.value = "";
            }
          } else {
            this.alertService.error("Introduce al menos 2 caracteres para el código del control.", this.alertOptions);
          }
        }
      } else {
        this.alertService.error("Máximo de controles por recorrido alcanzado.", this.alertOptions);
      }
    }
  }

  keyDownInputControl(evento: any): boolean {
    if(evento.key === "Backspace" && evento.target.value.length == 0) {
      // BACKSPACE: borra el penúltimo control (la meta no puede borrarla)
      let aBorrar = this.recorridoActual.trazado[this.recorridoActual.trazado.length-2];
      if(this.controles.get(aBorrar).tipo == Control.TIPO_CONTROL) {
        this.borrarControl(this.controles.get(aBorrar), false);
        evento.target.value = aBorrar;

        // Detiene propagación para keyup
        evento.stopPropagation();
        evento.preventDefault();  
        evento.returnValue = false;
        evento.cancelBubble = true;
        return false;
      }
    }
  }

  /**
   * Borra el control especificado del recorrido actual.
   * @param codigo Codigo de control
   */
  borrarControlRecorrido(codigo: string): void {
    this.borrarControl(this.controles.get(codigo), false);
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

    let controles = {}; // Si no no se guarda bien en localstorage
    if(this.tipoEdicion === this.EDICION_RECORRIDOS) {
      // Genera un set con los controles utilizados
      let setUtilizados = new Set(["SALIDA", "META"]);
      for(let rec of this.carrera.recorridos) {
        for(let con of rec.trazado) {
          setUtilizados.add(con);
        }
      }
      // Evita guardar los controles que ya no son utilizados
      // TODO: si hay controles numéricos con valores superiores a los no utilizados, rebajar automáticamente
      
      for(let k of this.controles.keys()) {
        if(setUtilizados.has(k)) {
          controles[k] = this.controles.get(k);
        }
      }
    } else {
      for(let k of this.controles.keys()) {
        controles[k] = this.controles.get(k);
      }
    }
    this.carrera.controles = controles as Map<any, any>; // Si no no se guarda bien
    this.carrera.soloRecorridos = this.eleccionTrazar === this.ELECCION_NO_TRAZAR;
    localStorage.setItem(AppSettings.LOCAL_STORAGE_CARRERA, JSON.stringify(this.carrera));
  }

}
