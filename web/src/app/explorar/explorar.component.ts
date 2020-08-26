import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { ClienteApiService } from '../shared/cliente-api.service';
import { AppSettings, Carrera } from '../shared/app.model';
import { Router } from '@angular/router';

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

  constructor(
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
    console.log("[*] Buscando página " + this.paginaActual);
    this.buscando = true;
    this.clienteApi.buscaCarreras(this.busqueda.nombre, this.busqueda.tipo, this.busqueda.modalidad, this.paginaActual, AppSettings.NUMERO_RESULTADOS_BUSQUEDA).subscribe(
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
