import { Component, OnInit, ElementRef, ViewChild } from '@angular/core';
import { AlertService } from '../../alert';
import { SharedEditorService } from '../shared-editor.service';
import { Control, Recorrido, Coordenadas } from 'src/app/shared/app.model';

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

  @ViewChild('inputPurplePen', {static: true}) inputPurplePen: ElementRef<HTMLInputElement>; 
  @ViewChild('inputMapa', {static: true}) inputMapa: ElementRef<HTMLInputElement>;
  
  recorridoActual: Recorrido;
  controles: Record<string, Control>;

  // Alertas
  options = {
    autoClose: true,
    keepAfterRouteChange: false
  };

  constructor(private sharedData: SharedEditorService,
    protected alertService: AlertService) { }

  ngOnInit() {
    // Chapucilla que funciona de momento para completar pantalla
    var wHeight = $(window).height();
    var offset = $("#wrapper-inferior").offset().top;
    $("#wrapper-inferior").height(wHeight - offset - 20);

    // Controlador de adición de controles
    this.sharedData.nuevoControl.subscribe((control) => {
      if(control !== null) {
        // Obtiene el código del control y crea el control
        var codigo = this.pullCodigoControl(control.tipo);
        control.codigo = codigo;
        this.controles[codigo] = control;
        this.sharedData.actualizaControles(this.controles);

        // Lo añade al recorrido actualmente seleccionado (si lo hay)
        if(this.recorridoActual !== null) {
          this.recorridoActual.idControles.push(codigo);
          // Actualiza el recorrido
          this.sharedData.actualizaRecorrido(this.recorridoActual);
        }
      }
    });


    // Inicia el modelo
    this.contadorSalidas = this.contadorControles = this.contadorMetas = 0;
    this.controles = {};
    this.recorridoActual = new Recorrido("R-1");
    this.sharedData.actualizaRecorrido(this.recorridoActual);
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


  cargarMapa(event) {
    if(!this.checkFileAPIs()) return;
    
    let file : File = event.target.files[0];
    if(file) {
      this.sharedData.cambiarMapaBase(file);
    } else {
      this.alertService.error("Ocurrió un error al cargar el mapa.");
    }
    
  }





  /* Devuelve el siguiente código de control libre, dependiendo del tipo */
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
