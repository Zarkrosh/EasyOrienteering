import { Component, OnInit, ViewChild, ElementRef } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { ClienteApiService } from '../shared/cliente-api.service';
import { AppSettings, Carrera } from '../shared/app.model';
import { Router } from '@angular/router';
import { AlertService } from '../alert';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'app-explorar',
  templateUrl: './explorar.component.html',
  styleUrls: ['./explorar.component.scss']
})
export class ExplorarComponent implements OnInit {

  busquedaForm: FormGroup;
  paginaActual: number;
  resultados: Carrera[];
  buscando: boolean;
  busqueda;

  // TEST
  @ViewChild('modalMapaCircuitos', {static: true}) modalMapaCircuitos: ElementRef<NgbModal>;


  // Alertas
  alertOptions = {
    autoClose: true,
    keepAfterRouteChange: true
  };

  constructor(
    protected alertService: AlertService,
    private modalService: NgbModal,
    private clienteApi: ClienteApiService,
    private router: Router,
    private formBuilder: FormBuilder) { }

  ngOnInit() {
    this.paginaActual = 0;
    this.resultados = null;
    this.buscando = false;
    this.busquedaForm = this.formBuilder.group({
      nombre: [''],
      tipo: [''],
      modalidad: ['']
    });
    this.busqueda = null;

    this.buscarCarreras();
  }



  buscarCarreras(): void {
    if(this.busquedaForm.invalid) return;
    this.paginaActual = 0;
    this.resultados = [];
    this.busqueda = {
      nombre: this.f.nombre.value,
      tipo: this.f.tipo.value,
      modalidad: this.f.modalidad.value
    }
    this.realizaBusqueda();
  }

  realizaBusqueda(): void {
    console.log("[*] Buscando página " + this.paginaActual); // DEBUG
    this.buscando = true;
    this.clienteApi.buscaCarreras(this.busqueda.nombre, this.busqueda.tipo, this.busqueda.modalidad, this.paginaActual, AppSettings.NUMERO_RESULTADOS_BUSQUEDA).subscribe(
      resp => {
        if(resp.status === 200) {
          this.resultados = this.resultados.concat(resp.body as Carrera[]);
        } else {
          // ?
          this.alertService.warn("Respuesta inesperada", this.alertOptions);
          console.log(resp);
        }
        this.buscando = false;
      }, err => {
        if(err.status == 504) {
          this.alertService.error("No hay conexión con el servidor", this.alertOptions);
        } else {
          let mensaje = "Se produjo un error";
          if(typeof err.error === 'string') mensaje += ": " + err.error;
          this.alertService.error(mensaje, this.alertOptions);
        }
        
        console.log(err);
        this.buscando = false;
      }
    );
  }

  muestraMapaCircuitos(mostrar: boolean) {
    if(mostrar) this.modalService.open(this.modalMapaCircuitos, {centered: true, windowClass: 'modal-fit'});
    else this.modalService.dismissAll();
  }

  verCarrera(idCarrera: number) {
    this.router.navigate(["/carreras", idCarrera]);
  }

  onScroll() {
    this.paginaActual++;
    if(this.busqueda) {
      this.realizaBusqueda();
    }
  }

  // Para acceder más comodo a los campos del formulario
  get f() { return this.busquedaForm.controls; }
}
