import { Component, OnInit, ElementRef, ViewChild } from '@angular/core';
import { Usuario, Carrera } from '../_shared/app.model';
import { ClienteApiService } from '../_services/cliente-api.service';
import { Utils } from '../_shared/utils';
import { Router } from '@angular/router';
import { NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';

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

  mensajeErrorCarga: string;
  mensajeErrorCargaOrganizadas: string;
  mensajeErrorCargaParticipadas: string;

  // Modal
  @ViewChild('modalBorrarCuenta', {static: true}) modalBorrarCuenta: ElementRef<NgbModal>;
  activeModal: NgbModalRef;


  constructor(private clienteApi: ClienteApiService,
    private router: Router,
    private modalService: NgbModal) {

    this.mensajeErrorCarga = null;
  }

  ngOnInit() {
    this.cargaDatosUsuario();
  }

  cargaDatosUsuario(): void {
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



  clickBorrarCarrera(): void {
    this.activeModal = this.modalService.open(this.modalBorrarCuenta, {centered: true, size: 'md'});
  }

  confirmaBorrarCuenta(): void {
    this.activeModal.close();
    alert("Picaste");
    // TODO
  }

}
