import { Component, OnInit, ElementRef, ViewChild } from '@angular/core';
import { FormGroup, FormBuilder, Validators, ValidationErrors, AbstractControl } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { AlertService } from 'src/app/alert';
import { ClienteApiService } from 'src/app/_services/cliente-api.service';
import { TokenStorageService } from 'src/app/_services/token-storage.service';
import { Utils } from 'src/app/_shared/utils';

@Component({
  selector: 'app-registro',
  templateUrl: './registro.component.html',
  styleUrls: ['./registro.component.scss']
})
export class RegistroComponent implements OnInit {
  registroForm: FormGroup;
  cargando = false;
  enviado = false;
  registrado = false;

  errorNombreEmail: string;
  mensajeError: string;

  @ViewChild("password", {static: false}) passField: ElementRef;

  constructor(
      private formBuilder: FormBuilder,
      private router: Router,
      protected alertService: AlertService,
      private clienteApi: ClienteApiService,
      private tokenService: TokenStorageService) {
    
  }

  ngOnInit() {
    if(this.tokenService.isLoggedIn()) {
      this.router.navigate(["/perfil"]);
    }

    this.registroForm = this.formBuilder.group({
      email: ['', Validators.required],
      nombre: ['', Validators.required],
      club: [''],
      password: ['', Validators.minLength(8)],
      passwordConf: ['', Validators.required]
    }, {
      validator: this.checkPasswords 
    });

    this.errorNombreEmail = this.mensajeError = "";
    this.registrado = this.cargando = false;
  }

  // Para acceder más comodo a los campos del formulario
  get f() { return this.registroForm.controls; }

  onSubmit() {
    if(this.registroForm.invalid) return;

    this.mensajeError = ""; // Quita el posible mensaje de error anterior
    this.cargando = true;
    this.clienteApi.register(this.f.nombre.value, this.f.email.value, this.f.club.value, this.f.password.value).subscribe(
      resp => {
        if(resp.status == 200) {
          // Registro exitoso, muestra mensaje de confirmación
          this.registrado = true;
        } else {
          this.mensajeError = "Error al registrar la cuenta";
        }
        this.cargando = false;
      },
      err => {
        this.mensajeError = Utils.getMensajeError(err, "");
        this.cargando = false;
      }
    );
  }

  
  checkPasswords(group: FormGroup) {
    let pass = group.get('password').value;
    let passwordConf = group.get('passwordConf').value;

    return pass === passwordConf ? null : { notSame: true }     
  }

}