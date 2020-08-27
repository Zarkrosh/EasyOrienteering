import { Component, OnInit, ElementRef, ViewChild, ViewChildren, QueryList } from '@angular/core';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { Router, ActivatedRoute } from '@angular/router';
import { AlertService } from 'src/app/alert';
import { AppSettings, Carrera, Control } from 'src/app/shared/app.model';
import { ClienteApiService } from 'src/app/shared/cliente-api.service';
import { DataService } from 'src/app/shared/data.service';
import { FormGroup, FormBuilder, Validators } from '@angular/forms';
import { EditorUbicacionComponent } from '../editores/editor-ubicacion/editor-ubicacion.component';

@Component({
  selector: 'app-resumen-carrera',
  templateUrl: './resumen-carrera.component.html',
  styleUrls: ['./resumen-carrera.component.scss']
})
export class ResumenCarreraComponent implements OnInit {

  readonly TIPO_CREAR = "crear";
  readonly TIPO_CREAR_NUEVA = "nueva";
  readonly TIPO_EDITAR = "editar";
  readonly PRIV_PUBLICA = "publica";
  readonly PRIV_PRIVADA = "privada";
  readonly MODALIDAD_TRAZADO = Carrera.MOD_TRAZADO;
  readonly MODALIDAD_SCORE = Carrera.MOD_SCORE;

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
  @ViewChild('modalBorrador', {static: true}) modalBorrador: ElementRef<NgbModal>;
  @ViewChild('modalBorrarCarrera', {static: true}) modalBorrarCarrera: ElementRef<NgbModal>;

  // Datos de carrera
  carreraForm: FormGroup;
  carrera: Carrera;
  controles: Control[];
  mapasKeys: string[];

  tipoVista: string;
  titulo: string;

  errorCarga: boolean;
  borrandoCarrera: boolean;

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
    private route: ActivatedRoute) {
    
    //this.mapas = new Map();
    this.errorCarga = this.borrandoCarrera = false;
  }

  ngOnInit() {
    this.carreraForm = this.formBuilder.group({
      nombre: ['', Validators.required],
      tipo: [Carrera.TIPO_EVENTO, Validators.required],
      modalidad: [Carrera.MOD_TRAZADO, Validators.required],
      visibilidad: [this.PRIV_PUBLICA, Validators.required],
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
          this.modalService.open(this.modalBorrador, {centered: true, size: 'lg', backdrop : 'static', keyboard : false});
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
      if(this.carrera) {
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
            this.f.nombre.setValue(this.carrera.nombre);
            this.f.tipo.setValue(this.carrera.tipo);
            this.f.modalidad.setValue(this.carrera.modalidad);
            this.f.visibilidad.setValue((this.carrera.privada) ? 'privada' : 'publica');
            this.f.notas.setValue(this.carrera.notas);
            this.actualizaListaControles();

            // TODO Carga mapas recorrido(s)
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
    // TODO Diferenciar entre creación y edición
    //  Si es creación, se envían todos los datos (formulario, mapas, etc)
    //  Si es edición, se envían los datos modificados (?) 

    this.clienteApi.createCarrera(this.carrera).subscribe(
      resp => {
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
        let mensaje = "Error al crear la carrera";
        if(typeof err.error === 'string') mensaje += ": " + err.error;
        this.alertService.error(mensaje, this.alertOptions);
        console.log(err);
      }
    );

    // Elimina el borrador
    //localStorage.removeItem(AppSettings.LOCAL_STORAGE_CARRERA); 
  }


  /**
   * Guarda un borrador con los datos actuales de los formularios.
   */
  guardaDatosCarrera() {
    this.carrera.nombre = this.f.nombre.value;
    this.carrera.tipo = this.f.tipo.value;
    this.carrera.modalidad = this.f.modalidad.value;
    this.carrera.privada = (this.f.visibilidad.value == "privada") ? true : false;
    this.carrera.notas = this.f.notas.value;
    this.carrera.latitud = this.editorUbicacion.latitudElegida;
    this.carrera.longitud = this.editorUbicacion.longitudElegida;
    if(this.carrera.modalidad === Carrera.MOD_SCORE) this.carrera.recorridos = [];
    else {
      for(let recorrido of this.carrera.recorridos) {
        recorrido.mapa = null; // Borra mapa para evitar error de espacio de almacenamiento local
      }
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
        this.f.notas.setValue(this.carrera.notas);
        this.actualizaListaControles();
      } catch (e) {
        console.log(e);
        this.alertService.error("Error al restaurar el borrador.", this.alertOptions);
        //this.nuevaCarreraVacia();
      }
    } else {
      // Descarta el borrador y crea una nueva
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
  clickBotonBorrar() {
    // Muestra el diálogo de confirmación de borrado
    this.modalService.open(this.modalBorrarCarrera, {centered: true, size: 'lg'});
  }

  /**
   * Confirma el borrado de la carrera.
   */
  confirmaBorrar(borrar: boolean) {
    if(borrar) {
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
          let mensaje = "No se pudo borrar la carrera";
          if(typeof err.error === 'string') mensaje += ": " + err.error;
          this.alertService.error(mensaje, this.alertOptions);
          this.borrandoCarrera = false;
          console.log(err);
        }
      );
    }
    
    this.modalService.dismissAll();
  }

  /**
   * Maneja click en el botón de volver a la carrera.
   */
  clickBotonVolver() {
    if(this.carrera.id) this.router.navigate(['/carreras', this.carrera.id]);
  }

  /**
   * Navega a la vista de edición de recorridos.
   */
  editarRecorridos() {
    this.guardaDatosCarrera();
    this.router.navigate(['/crear', 'recorridos']);
  }

  /**
   * Navega a la vista de edición de controles.
   */
  editarControles() {
    this.guardaDatosCarrera();
    this.router.navigate(['/crear', 'controles']);
  }
  
  // Para acceder más comodo a los campos del formulario
  get f() { return this.carreraForm.controls; }

}
