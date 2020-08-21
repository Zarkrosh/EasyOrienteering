import { Component, OnInit } from '@angular/core';
import { FormBuilder, Validators, FormGroup } from '@angular/forms';
import { ClienteApiService } from '../shared/cliente-api.service';
import { AppSettings, Carrera } from '../shared/app.model';

@Component({
  selector: 'app-explorar',
  templateUrl: './explorar.component.html',
  styleUrls: ['./explorar.component.scss']
})
export class ExplorarComponent implements OnInit {

  busquedaForm: FormGroup;
  busquedaNombre: string;
  busquedaTipo: string;
  busquedaModalidad: string;
  paginaActual: number;
  resultados: Carrera[];
  buscando: boolean;

  constructor(
    private clienteApi: ClienteApiService,
    private formBuilder: FormBuilder) { }

  ngOnInit() {
    this.paginaActual = 0;
    this.resultados = null;
    this.buscando = false;
    this.busquedaForm = this.formBuilder.group({
      nombre: ['', Validators.required],
      tipo: [''],
      modalidad: ['']
    });
  }



  buscarCarreras(): void {
    if(this.busquedaForm.invalid) return;
    this.paginaActual = 0;
    this.resultados = [];
    this.busquedaNombre = this.f.nombre.value;
    this.busquedaTipo = this.f.tipo.value;
    this.busquedaModalidad = this.f.modalidad.value;
    this.realizaBusqueda();
  }

  realizaBusqueda(): void {
    this.buscando = true;
    this.clienteApi.buscaCarreras(this.busquedaNombre, this.busquedaTipo, this.busquedaModalidad, this.paginaActual, AppSettings.NUMERO_RESULTADOS_BUSQUEDA).subscribe(
      resp => {
        if(resp.status === 200) {
          this.resultados = this.resultados.concat(resp.body as Carrera[]);
        } else {
          // ?
        }
        this.buscando = false;
      }, err => {
        // ?

        this.buscando = false;
      }
    );
  }


  onScroll() {
    console.log("Scrolled");
    this.paginaActual++;

  }

  // Para acceder más comodo a los campos del formulario
  get f() { return this.busquedaForm.controls; }
}
