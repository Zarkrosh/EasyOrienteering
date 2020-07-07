import { Component, OnInit, ElementRef, ViewChild } from '@angular/core';
import { AlertService } from '../../alert';
import { SharedEditorService } from '../shared-editor.service';
import { Control, Recorrido, Coordenadas, AppSettings, Carrera } from 'src/app/shared/app.model';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { IfStmt } from '@angular/compiler';
import { Router, ActivatedRoute } from '@angular/router';
import { ClienteApiService } from 'src/app/shared/cliente-api.service';

declare var $: any; // JQuery

@Component({
  selector: 'app-editor-recorridos',
  templateUrl: './editor-recorridos.component.html',
  styleUrls: ['./editor-recorridos.component.scss']
})
export class EditorRecorridosComponent implements OnInit {
  // Plantillas de códigos de salida/control/meta
  START_PRECODE = "S-";
  START_MIN_CODE = 1;
  CONTROL_PRECODE = "";
  CONTROL_MIN_CODE = 31;
  FINISH_PRECODE = "M-";
  FINISH_MIN_CODE = 1;

  RECORRIDO_DEFAULT_PRE = "Rec-";
  MAX_RECORRIDOS = 10;
  MAX_CONTROLES_RECORRIDO = 32;

  @ViewChild('inputPurplePen', {static: true}) inputPurplePen: ElementRef<HTMLInputElement>; 
  @ViewChild('inputMapa', {static: true}) inputMapa: ElementRef<HTMLInputElement>;
  @ViewChild('nombreRecorrido', {static: true}) nombreRecorrido: ElementRef<HTMLInputElement>;
  @ViewChild('modalControl', {static: true}) modalControl: ElementRef<NgbModal>;
  @ViewChild('modalRecorrido', {static: true}) modalRecorrido: ElementRef<NgbModal>;
  
  carrera: Carrera;
  recorridos: Map<string, Recorrido>;
  recorridoActual: string;
  nombresRecorridos: string[];
  controles: Map<string, Control>;

  editandoNombreRecorrido: boolean;

  // Diálogos modales
  tempControl: Control; // Temporal para la confirmación de borrado

  // Alertas
  options = {
    autoClose: true,
    keepAfterRouteChange: false
  };

  // TODO
  //   Mensaje de confirmación de abandono de página para prevenir perder los datos


  constructor(private sharedData: SharedEditorService,
    protected alertService: AlertService,
    private modalService: NgbModal,
    private router: Router,
    private route: ActivatedRoute,
    private clienteApi: ClienteApiService) { }

  ngOnInit() {
    this.recorridos = new Map<string, Recorrido>();
    this.recorridoActual = null;

    try {
      // Inicia el modelo
      this.cargarCarrera();
    } catch (e) {
      // Error al cargar la carrera, borra y redirige a la creación
      localStorage.removeItem(AppSettings.LOCAL_STORAGE_CARRERA);
      this.router.navigate(['carreras', 'crear']);
      return;
    }

    
    // Chapucilla que funciona de momento para completar pantalla
    var wHeight = $(window).height();
    var offset = $("#wrapper").offset().top;
    $("#wrapper").height(wHeight - offset - 20);


    // Vista
    this.editandoNombreRecorrido = false;

    // Controlador de adición de control
    this.sharedData.nuevoControl.subscribe((control) => {
      if(control !== null) {
        // Obtiene el código del control y crea el control
        var codigo = this.pullCodigoControl(control.tipo);
        control.codigo = codigo;
        this.controles.set(codigo, control);
        this.sharedData.actualizaControles(this.controles);

        // Lo añade al recorrido actualmente seleccionado (si lo hay)
        if(this.recorridoActual !== null) {
          this.recorridos.get(this.recorridoActual).trazado.push(codigo);
          // Actualiza el recorrido
          this.sharedData.actualizaRecorrido(this.recorridos.get(this.recorridoActual));
        }
      }
    });

    // Controlador de borrado de control
    this.sharedData.controlBorrado.subscribe((control) => {
      if(control !== null) {
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
        this.sharedData.actualizaControles(this.controles);
        this.sharedData.actualizaRecorrido(this.recorridos.get(this.recorridoActual));
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
      this.carrera.recorridos.forEach((recorrido, indice, array) => {
        this.recorridos.set(recorrido.nombre, recorrido);
        this.nombresRecorridos.push(recorrido.nombre);
      });
      this.controles = new Map(Object.entries(this.carrera.controles));
    } else {
      throw "Error al cargar la carrera";
    }

    // Actualiza el modelo compartido con el editor de trazados
    this.sharedData.actualizaControles(this.controles);
  }

  /* Finaliza la pantalla de edición de recorridos */
  guardarRecorridos() {
    // TODO
    // Elimina borrador (esto debería hacerse tras recibir confirmación de creación del servidor)
    this.guardaBorrador();
    this.alertService.success("Recorridos guardados");
    //localStorage.removeItem(AppSettings.LOCAL_STORAGE_CARRERA); 
  }

  /**
   * Tras seleccionar un archivo de PurplePen, se importan sus recorridos y se reflejan en el mapa.
   * @param event Evento de selección de archivo
   */
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
  }

  /**
   * Carga el mapa seleccionado en el editor de trazados.
   * @param event Evento de selección de archivo
   */
  cargarMapa(event) {
    if(!this.checkFileAPIs()) return;
    
    let file : File = event.target.files[0];
    if(file) {
      this.sharedData.cambiarMapaBase(file);
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
    if(this.recorridos.size < this.MAX_RECORRIDOS) {
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
          this.alertService.error("Ya existe un recorrido con ese nombre", this.options);
        }
      } else {
        // No se permite un nombre vacío
        this.alertService.error("El nombre no puede estar vacío", this.options);
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
    } else {
      // Lo elimina solo del recorrido actual
      var index = this.recorridos.get(this.recorridoActual).trazado.indexOf(control.codigo);
      this.recorridos.get(this.recorridoActual).trazado.splice(index, 1);
    }

    this.sharedData.confirmaBorrado(control.tipo);
  }

  /* Devuelve una lista con los nombres de los recorridos */
  getNombresRecorridos() {
    return this.nombresRecorridos;
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
    let lRecorridos = Array.from(this.recorridos.values());
    this.carrera.recorridos = lRecorridos;

    let jControles = {};
    for(let k of this.controles.keys()) {
      jControles[k] = this.controles.get(k);
    }
    this.carrera.controles = jControles as Map<any, any>;
    localStorage.setItem(AppSettings.LOCAL_STORAGE_CARRERA, JSON.stringify(this.carrera));

    // TEST
    this.clienteApi.crearCarrera(this.carrera).subscribe(
      resp => {
        if(resp.status == 201) {
          // Carrera creada
          localStorage.setItem(AppSettings.LOCAL_STORAGE_CARRERA, JSON.stringify(resp.body));
          this.alertService.success("Carrera creada con éxito");
          this.router.navigate(['carreras', resp.body.id]);
        } else {
          // Error
          this.alertService.error("No se pudo crear la carrera");
        }
      },
      err => {
        this.alertService.error("Error al crear la carrera: " + err);
      }
    );
  }

}
