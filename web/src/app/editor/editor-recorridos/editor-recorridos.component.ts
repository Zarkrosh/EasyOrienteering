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
  contadorSalidas: number;
  contadorControles: number;
  contadorMetas: number;

  RECORRIDO_DEFAULT_PRE = "Rec-";

  @ViewChild('inputPurplePen', {static: true}) inputPurplePen: ElementRef<HTMLInputElement>; 
  @ViewChild('inputMapa', {static: true}) inputMapa: ElementRef<HTMLInputElement>;
  @ViewChild('modalBorrado', {static: true}) modalBorrado: ElementRef<NgbModal>;
  
  recorridos: Map<string, Recorrido>;
  recorridoActual: string;
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
    this.contadorSalidas = this.contadorControles = this.contadorMetas = 0;
    this.recorridos = new Map();
    this.controles = new Map();
    this.nuevoRecorrido();

    // Controlador de adición de control
    this.sharedData.nuevoControl.subscribe((control) => {
      if(control !== null) {
        // Obtiene el código del control y crea el control
        var codigo = this.pullCodigoControl(control.tipo);
        control.codigo = codigo;
        this.controles[codigo] = control;
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
        /*
        for(var i = 0; i < this.listaRecorridos.length && sinUsar; i++) {
          var currRec = this.listarecorridos.get(i];
          if(this.recorridoActual !== currRec) {
            for(var j = 0; j < currRec.controles.length && sinUsar; j++) {
              if(currRec.idControles[j] === control.codigo) {
                sinUsar = false;
              }
            }
          }
        }
        */

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
    
    this.recorridoActual = nombre;
    this.recorridos.set(nombre, new Recorrido(nombre));
    this.sharedData.actualizaRecorrido(this.recorridos.get(nombre));
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
      delete this.controles[control.codigo];
    } else {
      // Lo elimina solo del recorrido actual
      var index = this.recorridos.get(this.recorridoActual).idControles.indexOf(control.codigo);
      this.recorridos.get(this.recorridoActual).idControles.splice(index, 1);
    }

    this.sharedData.confirmaBorrado(control.tipo);
  }

  /**
   * Devuelve el siguiente código de control libre, dependiendo del tipo.
   * @param tipo Tipo de control
   */
  pullCodigoControl(tipo: number) {
    var codigo = null, codigosTipo = [];
    // Primero obtiene la lista de códigos de controles del mismo tipo ya existentes
    for(var kPunto in this.controles) {
        var control = this.controles[kPunto];
        if(control.tipo === tipo) codigosTipo.push(control.codigo);
    }
    
    // Comprueba si hay alguno borrado
    var precodigo, numeroMinimo, contador;
    switch(tipo) {
        case Control.START:
            if(this.contadorSalidas - codigosTipo.length > this.START_MIN_CODE) {
                // Hay alguna borrada
                precodigo = this.START_PRECODE;
                numeroMinimo = this.START_MIN_CODE;
                contador = this.contadorSalidas;
            } else {
                // Se usa el siguiente
                codigo = this.START_PRECODE + (this.START_MIN_CODE + this.contadorSalidas++);
            }
            break;
        case Control.CONTROL:
            if(this.contadorControles - codigosTipo.length > this.CONTROL_MIN_CODE) {
                // Hay alguno borrado
                precodigo = this.CONTROL_PRECODE;
                numeroMinimo = this.CONTROL_MIN_CODE;
                contador = this.contadorControles;
            } else {
                // Se usa el siguiente
                codigo = this.CONTROL_PRECODE + (this.CONTROL_MIN_CODE + this.contadorControles++);
            }
            break;
        case Control.FINISH:
            if(this.contadorMetas - codigosTipo.length > this.FINISH_MIN_CODE) {
                // Hay alguna borrada
                precodigo = this.FINISH_PRECODE;
                numeroMinimo = this.FINISH_MIN_CODE;
                contador = this.contadorMetas;
            } else {
                // Se usa el siguiente
                codigo = this.FINISH_PRECODE + (this.FINISH_MIN_CODE + this.contadorMetas++);
            }
            break;
    }
    
    if(codigo === null) {
        // Se ha borrado alguno -> se va buscando con el array de códigos
        for(var i = numeroMinimo; i < contador && codigo === null; i++) {
            var nCodigo = precodigo + i;
            if(!codigosTipo.includes(nCodigo)) {
                // ¡Libre!
                codigo = nCodigo;
            }
        }
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
