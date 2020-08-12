import { Component, OnInit, ElementRef, ViewChild } from '@angular/core';
import { FormGroup, FormBuilder, Validators } from '@angular/forms';
import { AppSettings, Carrera, Control } from '../shared/app.model';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { Router, ActivatedRoute } from '@angular/router';

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
    private modalService: NgbModal,
    private route: ActivatedRoute,
    private router: Router) { }

  ngOnInit() {
    this.carreraForm = this.formBuilder.group({
      nombre: ['', Validators.required],
      tipo: ['EVENTO', Validators.required],
      modalidad: ['LINEA', Validators.required],
      visibilidad: ['publica', Validators.required]
      //cronometraje: ['QR', Validators.required]
    });

    // Comprueba si hay algún borrador de carrera en el almacenamiento local
    if(localStorage.getItem(AppSettings.LOCAL_STORAGE_CARRERA) !== null) {
      // Existe borrador
      // TODO Añadir detalles del borrador en el diálogo
      if(this.route.snapshot.queryParams['edit']) {
        // Se restaura automáticamente
        this.restauraBorrador(true);
      } else {
        // Se notifica al usuario
        this.modalService.open(this.modalBorrador, {centered: true, size: 'lg'});
      }
    } else {
      // Crea una nueva carrera: de momento no
      this.nuevaCarreraVacia();
    }
  }

  // Para acceder más comodo a los campos del formulario
  get f() { return this.carreraForm.controls; }

  /**
   * Genera una nueva carrera vacía.
   */
  nuevaCarreraVacia() {
    localStorage.removeItem(AppSettings.LOCAL_STORAGE_CARRERA);
    this.carrera = new Carrera("", [], new Map(), Carrera.TIPO_EVENTO, Carrera.MOD_TRAZADO, false, false);
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
      this.f.nombre.setValue(this.carrera.nombre);
      this.f.tipo.setValue(this.carrera.tipo);
      this.f.modalidad.setValue(this.carrera.modalidad);
      let visibilidad = (this.carrera.privada) ? 'privada' : 'publica';
      this.f.visibilidad.setValue(visibilidad);
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


  carreraDesdeDatos() {
    let privada: boolean = (this.f.visibilidad.value == "privada") ? true : false;
    this.carrera.nombre = this.f.nombre.value;
    this.carrera.tipo = this.f.tipo.value;
    this.carrera.modalidad = this.f.modalidad.value;
    this.carrera.privada = privada;
    this.guardaBorrador();
  }

  clickRecorridos() {
    if(this.carreraForm.valid) {
      // TODO Comprobar valores correctos
      this.carreraDesdeDatos();
      this.router.navigate(['carreras', 'crear', 'recorridos']);
    } else {
      // TODO
    }
  }

}
