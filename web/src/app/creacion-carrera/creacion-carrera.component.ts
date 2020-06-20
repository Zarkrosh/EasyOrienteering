import { Component, OnInit, ElementRef, ViewChild } from '@angular/core';
import { FormGroup, FormBuilder, Validators } from '@angular/forms';
import { AppSettings, Carrera } from '../shared/app.model';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'app-creacion-carrera',
  templateUrl: './creacion-carrera.component.html',
  styleUrls: ['./creacion-carrera.component.scss']
})
export class CreacionCarreraComponent implements OnInit {

  @ViewChild('modalBorrador', {static: true}) modalBorrador: ElementRef<NgbModal>;
  
  carrera: Carrera;
  carreraForm: FormGroup;
  

  constructor(private formBuilder: FormBuilder,
    private modalService: NgbModal) { }

  ngOnInit() {
    this.carreraForm = this.formBuilder.group({
      tipo: ['', Validators.required],
      modalidad: ['', Validators.required],
      cronometraje: ['', Validators.required]
    });

    // Comprueba si hay algún borrador de carrera en el almacenamiento local
    if(localStorage.getItem(AppSettings.LOCAL_STORAGE_CARRERA) !== null) {
      // Existe borrador, se notifica al usuario
      // TODO Añadir detalles del borrador en el diálogo
      this.modalService.open(this.modalBorrador, {centered: true, size: 'lg'});
    } else {
      // Crea una nueva carrera
      this.nuevaCarreraVacia();
    }
  }

  /**
   * Genera una nueva carrera vacía.
   */
  nuevaCarreraVacia() {
    this.carrera = new Carrera("", [], [], Carrera.EVENTO, false);
    this.guardaBorrador();
  }

  /**
   * Confirma la restauración o descartado del borrador. 
   */
  restauraBorrador(restaurar) {
    this.modalService.dismissAll();
    if(restaurar) {
      let jCarrera = localStorage.getItem(AppSettings.LOCAL_STORAGE_CARRERA);
      // TODO Manejar posible excepción
      this.carrera = JSON.parse(jCarrera) as Carrera;
      this.guardaBorrador();
    } else {
      // Descarta el borrador y crea una nueva
      localStorage.removeItem(AppSettings.LOCAL_STORAGE_CARRERA);
      this.nuevaCarreraVacia();
    }
  }

  /**
   * Guarda el borrador actual de la carrera en el almacenamiento local.
   */
  guardaBorrador() {
    localStorage.setItem(AppSettings.LOCAL_STORAGE_CARRERA, JSON.stringify(this.carrera));
  }

}
