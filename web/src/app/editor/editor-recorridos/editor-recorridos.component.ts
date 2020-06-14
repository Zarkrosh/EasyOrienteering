import { Component, OnInit, ElementRef, ViewChild } from '@angular/core';
import { AlertService } from '../../alert';
import { SharedEditorService } from '../shared-editor.service';
import { Control, Recorrido, Coordenadas } from 'src/app/shared/app.model';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

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

  @ViewChild('inputPurplePen', {static: true}) inputPurplePen: ElementRef<HTMLInputElement>; 
  @ViewChild('inputMapa', {static: true}) inputMapa: ElementRef<HTMLInputElement>;
  @ViewChild('modalBorrado', {static: true}) modalBorrado: ElementRef<NgbModal>;
  
  recorridos: Map<string, Recorrido>;
  recorridoActual: string;
  nombresRecorridos: string[];
  controles: Map<string, Control>;

  // Diálogos modales
  tempControl: Control; // Temporal para la confirmación de borrado

  // Alertas
  options = {
    autoClose: true,
    keepAfterRouteChange: false
  };

  constructor(private sharedData: SharedEditorService,
    protected alertService: AlertService,
    private modalService: NgbModal) { }

  ngOnInit() {
    // Chapucilla que funciona de momento para completar pantalla
    var wHeight = $(window).height();
    var offset = $("#wrapper-inferior").offset().top;
    $("#wrapper-inferior").height(wHeight - offset - 20);

    // Inicia el modelo
    this.nombresRecorridos = [];
    this.recorridos = new Map();
    this.controles = new Map();
    this.nuevoRecorrido();

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
          this.recorridos.get(this.recorridoActual).idControles.push(codigo);
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
            for(var i = 0; i < recorrido.idControles.length && sinUsar; i++) {
              if(recorrido.idControles[i] === control.codigo) {
                sinUsar = false;
              }
            }
          }
        }

        if(sinUsar) {
          // Como solo está en el trazado actual, se borra de ambos
          this.borrarControl(control, true);
        } else {
          // Se usa en otro trazado, solo se borra del actual
          this.borrarControl(control, false);
        }
        
        // Actualiza el trazador
        this.sharedData.actualizaControles(this.controles);
        this.sharedData.actualizaRecorrido(this.recorridos.get(this.recorridoActual));
      }
    });
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
    this.nombresRecorridos.push(nombre); // TEST
  }

  /* Confirmación de borrado total de control */
  // this.modalService.open(this.modalBorrado, {centered: true, size: 'lg'});
  borrarConfirmed() {
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
        var index = value.idControles.indexOf(control.codigo);
        if(index !== -1) value.idControles.splice(index, 1);
      });
      // Borra de la lista general de controles
      this.controles.delete(control.codigo);
    } else {
      // Lo elimina solo del recorrido actual
      var index = this.recorridos.get(this.recorridoActual).idControles.indexOf(control.codigo);
      this.recorridos.get(this.recorridoActual).idControles.splice(index, 1);
    }

    this.sharedData.confirmaBorrado(control.tipo);
  }

  getNombresRecorridos() {
    return this.nombresRecorridos;
  }

  /**
   * Devuelve el siguiente código de control libre, dependiendo del tipo.
   * @param tipo Tipo de control
   */
  pullCodigoControl(tipo: number) {
    var codigo = null, aux;
    switch(tipo) {
        case Control.SALIDA:
          aux = this.START_MIN_CODE;
          while(codigo === null) {
            if(!this.controles.has(this.START_PRECODE + aux)) codigo = this.START_PRECODE + aux;
            aux++;
          }
          break;
        case Control.CONTROL:
          aux = this.CONTROL_MIN_CODE;
          while(codigo === null) {
            if(!this.controles.has(this.CONTROL_PRECODE + aux)) codigo = this.CONTROL_PRECODE + aux;
            aux++;
          }
          break;
        case Control.META:
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

}
