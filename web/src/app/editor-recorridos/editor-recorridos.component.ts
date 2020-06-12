import { Component, OnInit, ElementRef, ViewChild } from '@angular/core';

declare var $: any; // JQuery

@Component({
  selector: 'app-editor-recorridos',
  templateUrl: './editor-recorridos.component.html',
  styleUrls: ['./editor-recorridos.component.scss']
})
export class EditorRecorridosComponent implements OnInit {

  constructor() { }

  ngOnInit() {
    // Chapucilla que funciona de momento
    var wHeight = $(window).height();
    var offset = $("#wrapper-inferior").offset().top;
    $("#wrapper-inferior").height(wHeight - offset - 20);
  }

  /**
   * Tras seleccionar un archivo de PurplePen, se importan sus recorridos y se reflejan en el mapa.
   * @param event Evento de selección de archivo
   */
  importarPurplePen(event) {
    let files: FileList = event.target.files;
    let file : File = files[0];

    // Comprobar que se trata de un archivo válido PurplePen
    // TODO

    // Procesar los recorridos
    // TODO

    // Reflejar cambios en la vista
    // TODO
  }

}
