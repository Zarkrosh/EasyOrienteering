import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { SharedEditorService } from 'src/app/editor/shared-editor.service';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { Router } from '@angular/router';
import { AlertService } from 'src/app/alert';
import { Control, Recorrido, AppSettings, Carrera } from 'src/app/shared/app.model';
import { ClienteApiService } from 'src/app/shared/cliente-api.service';
import { DataService } from 'src/app/shared/data.service';

@Component({
  selector: 'app-resumen-carrera',
  templateUrl: './resumen-carrera.component.html',
  styleUrls: ['./resumen-carrera.component.scss']
})
export class ResumenCarreraComponent implements OnInit {

  // Datos de carrera
  carrera: Carrera;
  mapas: Map<string, string>;
  modalidadLinea = Carrera.MOD_TRAZADO;
  modalidadScore = Carrera.MOD_SCORE;

  // Alertas
  options = {
    autoClose: true,
    keepAfterRouteChange: true
  };


  constructor(
    protected alertService: AlertService,
    private modalService: NgbModal,
    private router: Router,
    private clienteApi: ClienteApiService,
    private data: DataService,
    private ref: ChangeDetectorRef) {
    
    this.mapas = new Map();

    try {
      // Inicia el modelo
      this.cargaCarrera();
      //this.mapas = this.data.getValueMapasTrazados();
      this.data.mapasTrazados.subscribe(mapas => {
        this.mapas = mapas;
        for(let recorrido of this.carrera.recorridos) {
          recorrido.mapa = this.mapas.get(recorrido.nombre);
        }
      });
    } catch (e) {
      // Error al cargar la carrera, borra y redirige a la creación
      //localStorage.removeItem(AppSettings.LOCAL_STORAGE_CARRERA);
      //this.router.navigate(['carreras', 'crear']);
      this.alertService.error("Error al cargar los datos de la carrera", this.options);
      alert(e);
      return;
    }
  }

  ngOnInit() {
    
  }


  /**
   * Carga los controles y recorridos de la carrera del almacenamiento local.
   */
  cargaCarrera() {
    let jCarrera = localStorage.getItem(AppSettings.LOCAL_STORAGE_CARRERA);
    this.carrera = JSON.parse(jCarrera) as Carrera;
  }


  guardaCarrera() {
    // TEST
    this.clienteApi.crearCarrera(this.carrera).subscribe(
      resp => {
        if(resp.status == 201) {
          // Carrera creada
          this.alertService.success("Carrera creada con éxito", this.options);
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

    //localStorage.removeItem(AppSettings.LOCAL_STORAGE_CARRERA); 
  }

  editarDatos() {
    this.router.navigate(['carreras', 'crear', 'datos'], { queryParams: {edit: true}});
  }
  editarRecorridos() {
    this.router.navigate(['carreras', 'crear', 'recorridos']);
  }

}
