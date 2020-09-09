import { Component, OnInit, ElementRef, ViewChild } from '@angular/core';
import { Usuario, Carrera } from '../_shared/app.model';
import { ClienteApiService } from '../_services/cliente-api.service';
import { Utils } from '../_shared/utils';
import { Router } from '@angular/router';
import { NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { FormGroup, FormBuilder, Validators } from '@angular/forms';
import { AlertService } from '../alert';

@Component({
  selector: 'app-perfil',
  templateUrl: './perfil.component.html',
  styleUrls: ['./perfil.component.scss']
})
export class PerfilComponent implements OnInit {

  // Datos de usuario
  usuario: Usuario;
  organizadas: Carrera[];
  participadas: Carrera[];


  editorNombre: ElementRef<HTMLInputElement>;
  @ViewChild('editorNombre', {static: false}) set contentEditorNombre(content: ElementRef<HTMLInputElement>) {
    if(content) this.editorNombre = content;
  }
  editandoNombre: boolean;
  cambiandoNombre: boolean;

  editorClub: ElementRef<HTMLInputElement>;
  @ViewChild('editorClub', {static: false}) set contentEditorClub(content: ElementRef<HTMLInputElement>) {
    if(content) this.editorClub = content;
  }
  editandoClub: boolean;
  cambiandoClub: boolean;

  mensajeErrorCarga: string;
  mensajeErrorCargaOrganizadas: string;
  mensajeErrorCargaParticipadas: string;


  // Modal
  @ViewChild('modalBorrarCuenta', {static: true}) modalBorrarCuenta: ElementRef<NgbModal>;
  @ViewChild('modalCambiarPass', {static: true}) modalCambiarPass: ElementRef<NgbModal>;
  activeModal: NgbModalRef;

  cambioPassForm: FormGroup;

  // Alertas
  alertOptions = {
    autoClose: true,
    keepAfterRouteChange: false
  };

  constructor(private clienteApi: ClienteApiService,
    private router: Router,
    private modalService: NgbModal,
    private formBuilder: FormBuilder,
    protected alertService: AlertService) {

    this.mensajeErrorCarga = null;
    this.editandoNombre = this.editandoClub = this.cambiandoNombre = this.cambiandoClub = false;
  }

  ngOnInit() {
    this.cargaDatosUsuario();

    this.cambioPassForm = this.formBuilder.group({
      prevPass: ['', Validators.minLength(8)],
      nuevaPass: ['', Validators.minLength(8)],
      nuevaPassConf: ['', Validators.minLength(8)]
    }, {
      validator: this.checkPasswords 
    });
  }

  cargaDatosUsuario(): void {
    this.usuario = null;
    this.clienteApi.getDatosUsuario().subscribe(
      resp => {
        this.usuario = resp.body;

        // Carga carreras organizadas
        this.clienteApi.getCarrerasOrganizadas().subscribe(
          resp => {
            this.organizadas = resp.body;
          }, err => {
            this.mensajeErrorCargaOrganizadas = Utils.getMensajeError(err, "Error: ");
          }
        );
          
        // Carga carreras participadas
        this.clienteApi.getCarrerasParticipadas().subscribe(
          resp => {
            this.participadas = resp.body;
          }, err => {
            this.mensajeErrorCargaParticipadas = Utils.getMensajeError(err, "Error: ");
          }
        );

      }, err => {
        if(err.status == 401) {
          this.router.navigate(["/logout"]);
        } else {
          this.mensajeErrorCarga = Utils.getMensajeError(err, "Error al obtener los datos de perfil");
        }
      }
    );
  }

  editarNombre(editar: boolean): void {
    if(editar) {
      this.editandoNombre = true;
    } else {
      // Finaliza edición
      let nuevoNombre = this.editorNombre.nativeElement.value.trim();
      if(nuevoNombre !== this.usuario.nombre) {
        this.cambiandoNombre = true;
        this.clienteApi.cambiaNombre(nuevoNombre).subscribe(
          resp => {
            if(resp.status === 200) {
              this.usuario.nombre = resp.body.nombre;
              this.alertService.success("Nombre actualizado", this.alertOptions);
            } else {
              // Inesperado
              this.cargaDatosUsuario();
            }
            this.cambiandoNombre = this.editandoNombre = false;
          }, err => {
            this.alertService.error(Utils.getMensajeError(err, ""), this.alertOptions);
            this.cambiandoNombre = this.editandoNombre = false;
          }
        );
      } else {
        this.editandoNombre = false;
      }
    }
  }

  editarClub(editar: boolean): void {
    if(editar) {
      this.editandoClub = true;
    } else {
      // Finaliza edición
      let nuevoClub = this.editorClub.nativeElement.value.trim();
      if(nuevoClub !== this.usuario.club) {
        this.cambiandoClub = true;
        this.clienteApi.cambiaClub(nuevoClub).subscribe(
          resp => {
            if(resp.status === 200) {
              this.usuario.club = resp.body.club;
              this.alertService.success("Club actualizado", this.alertOptions);
            } else {
              // Inesperado
              this.cargaDatosUsuario();
            }
            this.cambiandoClub = this.editandoClub = false;
          }, err => {
            this.alertService.error(Utils.getMensajeError(err, ""), this.alertOptions);
            this.cambiandoClub = this.editandoClub = false;
          }
        );
      } else {
        this.editandoClub = false;
      }
    }
  }

  // Para acceder más comodo a los campos del formulario
  get f() { return this.cambioPassForm.controls; }

  checkPasswords(group: FormGroup) {
    let pass = group.get('nuevaPass').value;
    let passwordConf = group.get('nuevaPassConf').value;

    return pass === passwordConf ? null : { notSame: true }     
  }

  clickCambiarPassword(): void {
    this.activeModal = this.modalService.open(this.modalCambiarPass, {centered: true, size: 'md'});
  }

  cambiarPassword(): void {
    this.activeModal.close();
    let prevPass = this.f.prevPass.value;
    let nuevaPass = this.f.nuevaPass.value;
    let nuevaPassConf = this.f.nuevaPassConf.value;

    if(nuevaPass === nuevaPassConf) {
      if(prevPass !== nuevaPass) {
        this.clienteApi.cambiaPassword(prevPass, nuevaPass).subscribe(
          resp => {
            this.alertService.success("Contraseña actualizada", this.alertOptions);
            this.cambioPassForm.reset();
          }, err => {
            this.alertService.error(Utils.getMensajeError(err, ""), this.alertOptions);
            this.cambioPassForm.reset();
          }
        );
      } else {
        // Misma pass que la anterior
        this.alertService.error("Es la misma contraseña", this.alertOptions);
      }
    } else {
      // Diferente confirmación
      this.alertService.error("Las contraseñas no coinciden", this.alertOptions);
    }

    
  }


  clickBorrarCarrera(): void {
    this.activeModal = this.modalService.open(this.modalBorrarCuenta, {centered: true, size: 'md'});
  }

  confirmaBorrarCuenta(): void {
    this.activeModal.close();
    alert("Picaste");
    // TODO
  }

}
