import { Component, OnInit, ElementRef, ViewChild } from '@angular/core';
import { FormGroup, FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { AlertService } from 'src/app/alert';
import { ClienteApiService } from 'src/app/shared/cliente-api.service';

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
  errorGenerico: string;

  @ViewChild("password", {static: false}) passField: ElementRef;

  constructor(
      private formBuilder: FormBuilder,
      private route: ActivatedRoute,
      private router: Router,
      protected alertService: AlertService,
      private clienteApi: ClienteApiService) {
    // Si ya está loggeado redirige al home
    /*if (this.authenticationService.currentUserValue) {
        this.router.navigate(['/']);
    }*/
  }

  ngOnInit() {
    this.registroForm = this.formBuilder.group({
      usuario_email: ['', Validators.required],
      password: ['', Validators.required]
    });

    this.errorNombreEmail = this.errorGenerico = " ";
  }

  // Para acceder más comodo a los campos del formulario
  get f() { return this.registroForm.controls; }

  onSubmit() {
    if(this.registroForm.invalid) return;

    this.errorGenerico = ""; // Quita el posible mensaje de error anterior
    this.enviado = true;     // Invalida el envío de más peticiones

    this.clienteApi.login(this.f.usuario_email.value, this.f.password.value).subscribe(
      resp => {
        if(resp.status == 200) {
          // Registro exitoso, muestra mensaje de confirmación
          this.registrado = true;
        } else {
          this.errorGenerico = "'Error' al registrar la cuenta";
        }
        this.cargando = false;
      },
      err => {
        this.errorGenerico = "Error inesperado. Vuélvelo a intentar de nuevo";
        this.cargando = false;
      }
    );
  }

}