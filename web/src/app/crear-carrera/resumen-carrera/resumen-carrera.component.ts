import { Component, OnInit, ElementRef, ViewChild, ViewChildren, QueryList, Injectable } from '@angular/core';
import { NgbModal, NgbDateStruct, NgbDateAdapter, NgbDateParserFormatter, NgbCalendar, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { Router, ActivatedRoute } from '@angular/router';
import { AlertService } from 'src/app/alert';
import { AppSettings, Carrera, Control, Recorrido } from 'src/app/_shared/app.model';
import { ClienteApiService } from 'src/app/_services/cliente-api.service';
import { FormGroup, FormBuilder, Validators } from '@angular/forms';
import { EditorUbicacionComponent } from '../editores/editor-ubicacion/editor-ubicacion.component';
import { DataService } from 'src/app/_services/data.service';
import { FooterService } from 'src/app/_services/footer.service';
import { TokenStorageService } from 'src/app/_services/token-storage.service';

/**
 * This Service handles how the date is represented in scripts i.e. ngModel.
 */
@Injectable()
export class CustomAdapter extends NgbDateAdapter<string> {

  readonly DELIMITER = '-';

  fromModel(value: string | null): NgbDateStruct | null {
    if (value) {
      let date = value.split(this.DELIMITER);
      return {
        day : parseInt(date[0], 10),
        month : parseInt(date[1], 10),
        year : parseInt(date[2], 10)
      };
    }
    return null;
  }

  toModel(date: NgbDateStruct | null): string | null {
    return date ? date.day + this.DELIMITER + date.month + this.DELIMITER + date.year : null;
  }
}

/**
 * This Service handles how the date is rendered and parsed from keyboard i.e. in the bound input field.
 */
@Injectable()
export class CustomDateParserFormatter extends NgbDateParserFormatter {

  public readonly DELIMITER = '/';

  parse(value: string): NgbDateStruct | null {
    if (value) {
      let date = value.split(this.DELIMITER);
      return {
        day : parseInt(date[0], 10),
        month : parseInt(date[1], 10),
        year : parseInt(date[2], 10)
      };
    }
    return null;
  }

  format(date: NgbDateStruct | null): string {
    return date ? date.day + this.DELIMITER + date.month + this.DELIMITER + date.year : '';
  }
}

@Component({
  selector: 'app-resumen-carrera',
  templateUrl: './resumen-carrera.component.html',
  styleUrls: ['./resumen-carrera.component.scss'],

  // NOTE: For this example we are only providing current component, but probably
  // NOTE: you will want to provide your main App Module
  providers: [
    {provide: NgbDateAdapter, useClass: CustomAdapter},
    {provide: NgbDateParserFormatter, useClass: CustomDateParserFormatter}
  ]
})
export class ResumenCarreraComponent implements OnInit {

  readonly TIPO_CREAR = "crear";
  readonly TIPO_CREAR_NUEVA = "nueva";
  readonly TIPO_EDITAR = "editar";
  readonly PRIV_PUBLICA = "publica";
  readonly PRIV_PRIVADA = "privada";
  readonly MODALIDAD_TRAZADO = Carrera.MOD_TRAZADO;
  readonly MODALIDAD_SCORE = Carrera.MOD_SCORE;
  readonly TIPO_CIRCUITO = Carrera.TIPO_CIRCUITO;
  readonly TIPO_EVENTO = Carrera.TIPO_EVENTO;

  private editorUbicacion: EditorUbicacionComponent;
  @ViewChild('editorUbicacion', {static: false}) set content(content: EditorUbicacionComponent) {
    if(content) {
      this.editorUbicacion = content;
      if(this.carrera && this.carrera.latitud) {
        this.editorUbicacion.latitudElegida = this.carrera.latitud;
        this.editorUbicacion.longitudElegida = this.carrera.longitud;
        this.editorUbicacion.setMarcadorUbicacion(this.carrera.latitud, this.carrera.longitud);
      }
    }
  }


  // Modales
  @ViewChild('modalBorrador', {static: true}) modalBorrador: ElementRef<NgbModal>;
  @ViewChild('modalBorrarCarrera', {static: true}) modalBorrarCarrera: ElementRef<NgbModal>;
  @ViewChild('modalBorrarMapa', {static: true}) modalBorrarMapa: ElementRef<NgbModal>;
  @ViewChild('modalCancelarEdicion', {static: true}) modalCancelarEdicion: ElementRef<NgbModal>;
  @ViewChild('modalVisualizacionMapa', {static: true}) modalVisualizacionMapa: ElementRef<NgbModal>;
  activeModal: NgbModalRef;

  // Datos de carrera
  carreraForm: FormGroup;
  carrera: Carrera;
  controles: Control[];
  
  // Selector de fecha
  minDate: NgbDateStruct;
  maxDate: NgbDateStruct;

  tipoVista: string;
  titulo: string;

  errorCarga: boolean;
  guardandoCarrera: boolean;
  borrandoCarrera: boolean;
  indiceRecorridoBorradoMapa: number;

  // Alertas
  alertOptions = {
    autoClose: true,
    keepAfterRouteChange: true
  };

  constructor(
    protected alertService: AlertService,
    private modalService: NgbModal,
    private router: Router,
    private clienteApi: ClienteApiService,
    private data: DataService,
    private formBuilder: FormBuilder,
    private route: ActivatedRoute,
    private footer: FooterService,
    private tokenService: TokenStorageService) {
    
    this.errorCarga = this.borrandoCarrera = this.guardandoCarrera = false;
    this.controles = [];
  }

  ngOnInit() {
    this.footer.show();

    if(!this.tokenService.isLoggedIn()) {
      this.router.navigate(["/logout"]);
    }
    
    let today = new Date();
    let todayDate = today.getUTCDate() + "-" + (today.getUTCMonth()+1) + "-" + today.getUTCFullYear();
    // Fecha mínima de selección
    this.minDate = {
      day: today.getUTCDate(),
      month: today.getUTCMonth()+1,
      year: today.getUTCFullYear()
    };
    // Fecha máxima de selección
    this.maxDate = {
      day: today.getUTCDate(),
      month: today.getUTCMonth()+1,
      year: today.getUTCFullYear()+1
    };

    this.carreraForm = this.formBuilder.group({
      nombre: ['', Validators.required],
      tipo: [Carrera.TIPO_EVENTO, Validators.required],
      modalidad: [Carrera.MOD_TRAZADO, Validators.required],
      visibilidad: [this.PRIV_PUBLICA, Validators.required],
      fecha: [todayDate],
      notas: ['']
    });

    if(window.location.toString().indexOf(this.TIPO_EDITAR) > -1) {
      // Edición de carrera
      this.tipoVista = this.TIPO_EDITAR;
      this.titulo = "Editar carrera";
      // ¿Puede editar?
      // TODO
      let editar = true;
      if(editar) {
        this.cargaDatosCarrera();
      } else {
        this.alertService.error("Solo el organizador puede editar esta carrera", this.alertOptions);
        this.router.navigate(["/"]);
      }
    } else {
      // Creación de carrera
      this.tipoVista = this.TIPO_CREAR;
      this.titulo = "Crear carrera";
      if(window.location.toString().indexOf(this.TIPO_CREAR_NUEVA) > -1) {
        // Comienzo de nueva carrera, ¿hay borrador anterior?
        if(localStorage.getItem(AppSettings.LOCAL_STORAGE_CARRERA) !== null) {
          // Se notifica al usuario 
          // Las opciones de backdrop y keyboard son para evitar cerrar al clicar fuera o pulsar Escape
          this.activeModal = this.modalService.open(this.modalBorrador, {centered: true, size: 'lg', backdrop : 'static', keyboard : false});
        } else {
          // Crea una nueva carrera vacía
          this.nuevaCarreraVacia();
        }
      } else {
        // Vuelta al resumen, no se pregunta restauración
        this.restauraBorrador(true);
      }
    }

    // TODO Asíncrono para ediciones y restauraciones de borrador
    this.data.mapasTrazados.subscribe(mapas => {
      if(this.carrera && mapas && mapas.size > 0) {
        for(let recorrido of this.carrera.recorridos) {
          recorrido.mapa = mapas.get(recorrido.nombre);
        }
      }
    });
    
  }

  /**
   * Carga los datos de la carrera del ID
   */
  cargaDatosCarrera(): void {
    this.route.params.subscribe(routeParams => {
      let idCarrera = +routeParams['id']; // (+) Convierte string a number
      this.clienteApi.getCarrera(idCarrera).subscribe(
        resp => {
          if(resp.status == 200) {
            this.carrera = resp.body;

            if(this.carrera.organizador.nombre === this.tokenService.getUser().username) {
              this.f.nombre.setValue(this.carrera.nombre);
              this.f.tipo.setValue(this.carrera.tipo);
              this.f.modalidad.setValue(this.carrera.modalidad);
              this.f.visibilidad.setValue((this.carrera.privada) ? 'privada' : 'publica');
              this.f.fecha.setValue(this.carrera.fecha);
              this.f.notas.setValue(this.carrera.notas);
              this.actualizaListaControles();
            } else {
              this.alertService.error("Solo puede editar la carrera su organizador", this.alertOptions);
              this.router.navigate(["/carreras", this.carrera.id]);
            }
          } else {
            this.alertService.error("Error al obtener la carrera", this.alertOptions);
            this.errorCarga = true;
          }
        }, err => {
          if(err.status == 504) {
            this.alertService.error("No hay conexión con el servidor. Espera un momento y vuelve a intentarlo.", this.alertOptions);
          } else {
            this.alertService.error("Error al obtener la carrera: " + JSON.stringify(err), this.alertOptions);
          }
          this.errorCarga = true;
        }
      );
    });
  }


  guardaCarrera() {
    this.guardandoCarrera = true;
    // TODO Diferenciar entre creación y edición
    //  Si es creación, se envían todos los datos (formulario, mapas, etc)
    //  Si es edición, se envían los datos modificados (?) 
    
    // Evita envío innecesario de mapas y errores
    for(let recorrido of this.carrera.recorridos) {
      
      if(recorrido.mapa == true) {
        //recorrido.mapa = ""; // Indica que no se ha modificado
        recorrido.mapa = "data:image/gif;base64,R0lGODlhAQABAAAAACH5BAEKAAEALAAAAAABAAEAAAICTAEAOw=="; // En blanco
      }
      // Si es null es porque se ha borrado, se deja así
      // Si no es null ni true, es porque se ha asignado un nuevo mapa, se deja así
    }

    this.carrera.nombre = this.f.nombre.value;
    this.carrera.tipo = this.f.tipo.value;
    this.carrera.modalidad = this.f.modalidad.value;
    this.carrera.privada = (this.f.visibilidad.value == "privada") ? true : false;
    this.carrera.fecha = this.f.fecha.value;
    this.carrera.notas = this.f.notas.value;
    this.carrera.latitud = this.editorUbicacion.latitudElegida;
    this.carrera.longitud = this.editorUbicacion.longitudElegida;

    if(this.tipoVista == this.TIPO_CREAR) {
      this.clienteApi.createCarrera(this.carrera).subscribe(
        resp => {
          this.guardandoCarrera = false;
          if(resp.status == 201) {
            // Carrera creada
            this.alertService.success("Carrera creada con éxito", this.alertOptions);
            this.router.navigate(['carreras', resp.body.id]);
          } else {
            // Error
            this.alertService.error("No se pudo crear la carrera");
            console.log(resp);
          }
        },
        err => {
          this.guardandoCarrera = false;
          if(err.status == 504) {
            this.alertService.error("No hay conexión con el servidor. Espera un momento y vuelve a intentarlo.", this.alertOptions);
          } else {
            let mensaje = "Error al crear la carrera";
            if(typeof err.error === 'string') mensaje += ": " + err.error;
            this.alertService.error(mensaje, this.alertOptions);
            console.log(err);
          }
        }
      );
    } else {
      this.clienteApi.editCarrera(this.carrera).subscribe(
        resp => {
          this.guardandoCarrera = false;
          if(resp.status == 200) {
            // Carrera editada
            this.alertService.success("Carrera editada con éxito", this.alertOptions);
            this.router.navigate(['carreras', this.carrera.id]);
          } else {
            // Error
            this.alertService.error("No se pudo editar la carrera");
            console.log(resp);
          }
        },
        err => {
          this.guardandoCarrera = false;
          if(err.status == 504) {
            this.alertService.error("No hay conexión con el servidor. Espera un momento y vuelve a intentarlo.", this.alertOptions);
          } else {
            let mensaje = "Error al editar la carrera";
            if(typeof err.error === 'string') mensaje += ": " + err.error;
            this.alertService.error(mensaje, this.alertOptions);
            console.log(err);
          }
        }
      );
    }
    

    // Elimina el borrador
    //localStorage.removeItem(AppSettings.LOCAL_STORAGE_CARRERA); // TODO descomentar
  }


  /**
   * Guarda un borrador con los datos actuales de los formularios.
   */
  guardaDatosCarreraBorrador() {
    this.carrera.nombre = this.f.nombre.value;
    this.carrera.tipo = this.f.tipo.value;
    this.carrera.modalidad = this.f.modalidad.value;
    this.carrera.privada = (this.f.visibilidad.value == "privada") ? true : false;
    this.carrera.fecha = this.f.fecha.value;
    this.carrera.notas = this.f.notas.value;
    this.carrera.latitud = this.editorUbicacion.latitudElegida;
    this.carrera.longitud = this.editorUbicacion.longitudElegida;
    for(let recorrido of this.carrera.recorridos) {
      recorrido.mapa = null; // Borra mapa para evitar error de espacio de almacenamiento local
    }
    
    // Guarda borrador
    localStorage.setItem(AppSettings.LOCAL_STORAGE_CARRERA, JSON.stringify(this.carrera));
  }

  /**
   * Confirma la restauración o descartado del borrador. 
   */
  restauraBorrador(restaurar) {
    this.modalService.dismissAll();
    if(restaurar) {
      try {
        let jCarrera = localStorage.getItem(AppSettings.LOCAL_STORAGE_CARRERA);
        this.carrera = JSON.parse(jCarrera) as Carrera;
        this.f.nombre.setValue(this.carrera.nombre);
        this.f.tipo.setValue(this.carrera.tipo);
        this.f.modalidad.setValue(this.carrera.modalidad);
        this.f.visibilidad.setValue((this.carrera.privada) ? 'privada' : 'publica');
        this.f.fecha.setValue(this.carrera.fecha);
        this.f.notas.setValue(this.carrera.notas);
        this.actualizaListaControles();
      } catch (e) {
        console.log(e);
        this.alertService.error("Error al restaurar el borrador.", this.alertOptions);
        //this.nuevaCarreraVacia(); // TODO Descomentar
      }
    } else {
      // Descarta el borrador y crea una nueva
      this.data.setMapaBase(null);
      this.data.setMapasTrazados(null);
      this.nuevaCarreraVacia();
    }
  }

  /**
   * Genera una nueva carrera vacía.
   */
  nuevaCarreraVacia() {
    localStorage.removeItem(AppSettings.LOCAL_STORAGE_CARRERA);
    this.carrera = new Carrera();
  }

  actualizaListaControles(): void {
    this.controles = Object.values(this.carrera.controles).filter(function(control: Control) {
      return control.tipo === Control.TIPO_CONTROL;
    });
  }

  /**
   * Maneja click en el botón de borrado de carrera.
   */
  clickBotonBorrarCarrera() {
    // Muestra el diálogo de confirmación de borrado
    this.activeModal = this.modalService.open(this.modalBorrarCarrera, {centered: true, size: 'lg'});
  }

  /**
   * Confirma el borrado de la carrera.
   */
  confirmaBorrarCarrera() {
    this.borrandoCarrera = true;
    this.clienteApi.deleteCarrera(this.carrera.id).subscribe(
      resp => {
        this.borrandoCarrera = false;
        if(resp.status == 200) {
          this.alertService.success("Se ha borrado la carrera", this.alertOptions);
          this.router.navigate(['/explorar']);
        } else {
          // ?
          console.log(resp);
        }

      }, err => {
        this.borrandoCarrera = false;
        if(err.status == 504) {
          this.alertService.error("No hay conexión con el servidor. Espera un momento y vuelve a intentarlo.", this.alertOptions);
        } else {
          let mensaje = "No se pudo borrar la carrera";
          if(typeof err.error === 'string') mensaje += ": " + err.error;
          this.alertService.error(mensaje, this.alertOptions);
          console.log(err);
        }
      }
    );
    
    this.activeModal.close();
  }

  clickBotonBorrarMapa(recorrido: Recorrido): void {
    if(recorrido) {
      this.indiceRecorridoBorradoMapa = this.carrera.recorridos.indexOf(recorrido);
      this.activeModal = this.modalService.open(this.modalBorrarMapa, {centered: true, size: 'lg'});
    }
  }

  confirmaBorrarMapa(): void {
    if(this.indiceRecorridoBorradoMapa !== null) {
      this.carrera.recorridos[this.indiceRecorridoBorradoMapa].mapa = null;
    }

    this.activeModal.close();
  }

  /**
   * Añade un mapa a un recorrido.
   * @param fileInput Archivo seleccionado
   * @param recorrido Recorrido
   */
  procesaCargaMapa(fileInput: any, recorrido: Recorrido): void {
    if (fileInput.target.files && fileInput.target.files[0]) {
      const reader = new FileReader();
      reader.addEventListener('load', (event: any) => {
        // TODO Aplicar restricciones/validaciones

        let src = event.target.result;
        recorrido.mapa = src;
      });
      reader.readAsDataURL(fileInput.target.files[0]);
    }
  }

  /**
   * Descarga el mapa para un recorrido.
   * @param recorrido Recorrido
   */
  descargarMapa(recorrido: Recorrido) {
    if(recorrido !== null) {
      this.clienteApi.getMapaRecorrido(recorrido.id).subscribe(
        resp => {
          let reader = new FileReader();
          reader.addEventListener("load", () => {
            let rec = this.carrera.recorridos[this.carrera.recorridos.indexOf(recorrido)];
            rec.mapa = reader.result;
          }, false);

          if (resp.body) {
            reader.readAsDataURL(resp.body);
          }
        }, err => {
          if(err.status == 504) {
            this.alertService.error("No hay conexión con el servidor. Espera un momento y vuelve a intentarlo.", this.alertOptions);
          } else if(err.status == 404) {
            this.alertService.error("Este recorrido no tiene mapa.", this.alertOptions);
          }else if(err.status == 403) {
            this.alertService.error("No tienes permiso para descargar el mapa.", this.alertOptions);
          } else {
            let mensaje = "No se pudo descargar el mapa";
            if(typeof err.error === 'string') mensaje += ": " + err.error;
            this.alertService.error(mensaje, this.alertOptions);
            console.log(err);
          }
        }
      );
    } else {
      this.alertService.error("No se puede descargar este mapa", this.alertOptions);
    }
  }

  visualizarMapa(src: string): void {
    this.activeModal = this.modalService.open(this.modalVisualizacionMapa, {centered: true, windowClass: 'modal-mapa'});
    (document.getElementById("img-visualizacion") as HTMLImageElement).src = src;
  }

  /**
   * Maneja click en el botón de volver a la carrera.
   * Muestra diálogo de confirmación.
   */
  clickBotonVolver() {
    this.activeModal = this.modalService.open(this.modalCancelarEdicion, {centered: true, size: 'lg'});
  }

  /**
   * Vuelve a la vista de la carrera.
   */
  cancelarEdicion() {
    this.activeModal.close();
    if(this.carrera.id) this.router.navigate(['/carreras', this.carrera.id]);
    else this.router.navigate(['/explorar']);
  }

  /**
   * Navega a la vista de creación de recorridos.
   */
  crearRecorridos() {
    this.guardaDatosCarreraBorrador();
    this.router.navigate(['/crear', 'recorridos']);
  }

  /**
   * Navega a la vista de creación de controles.
   */
  crearControles() {
    this.guardaDatosCarreraBorrador();
    this.router.navigate(['/crear', 'controles']);
  }
  
  // Para acceder más comodo a los campos del formulario
  get f() { return this.carreraForm.controls; }

}
