import { Component, OnInit, ElementRef, ViewChild } from '@angular/core';
import { FormGroup, FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { AlertService } from 'src/app/alert';
import { ClienteApiService } from 'src/app/shared/cliente-api.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit {
  loginForm: FormGroup;
  cargando = false;
  enviado = false;

  mensajeError: string;
  mostrarMensaje = false;

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
    this.loginForm = this.formBuilder.group({
      usuario_email: ['', Validators.required],
      password: ['', Validators.required]
    });
    this.mensajeError = " ";
  }

  // Para acceder más comodo a los campos del formulario
  get f() { return this.loginForm.controls; }

  onSubmit() {
    if(this.loginForm.invalid) return;

    this.mostrarMensaje = false; // Quita el posible mensaje de error anterior
    this.enviado = true;         // Invalida el envío de más peticiones

    this.clienteApi.login(this.f.usuario_email.value, this.f.password.value).subscribe(
      resp => {
        if(resp.status == 200) {
          // Login exitoso, redirige a la página principal
          this.router.navigate(['/home']);
        } else {
          this.mensajeError = "'Error' al conectar";
        }
        this.cargando = false;
      },
      err => {
        if(err.status == 500) {
          // Error interno
          this.mensajeError = "Error inesperado. Vuélvelo a intentar de nuevo";
        } else {
          // Login fallido: limpia el contenido de la contraseña y coloca el focus en el input
          this.f.password.setValue("");
          this.passField.nativeElement.focus();
          this.mensajeError = "Credenciales incorrectas";
        }
        this.cargando = false;
      }
    );
  }

}
