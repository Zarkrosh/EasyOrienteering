import { Component, OnInit, ElementRef, ViewChild } from '@angular/core';
import { FormGroup, FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { AlertService } from 'src/app/alert';
import { ClienteApiService } from 'src/app/_services/cliente-api.service';
import { TokenStorageService } from 'src/app/_services/token-storage.service';
import { NavbarService } from 'src/app/_services/navbar.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit {
  readonly LOGIN = "login";
  readonly LOGOUT = "logout";

  loginForm: FormGroup;
  cargando: boolean;
  mensajeError: string;

  @ViewChild("password", {static: false}) passField: ElementRef;

  constructor(
      private formBuilder: FormBuilder,
      private route: ActivatedRoute,
      private router: Router,
      protected alertService: AlertService,
      private clienteApi: ClienteApiService,
      private tokenService: TokenStorageService,
      private navbarService: NavbarService) {}

  ngOnInit() {
    if(window.location.toString().indexOf(this.LOGOUT) > -1) {
      // Cierra sesión
      // TODO Invalidar token en back-end
      this.tokenService.logout();
      this.navbarService.setLoggedInView(false);
      this.router.navigate(["/login"]);
    } else {
      if(this.tokenService.isLoggedIn()) {
        this.router.navigate(["/perfil"]);
      }
  
      this.loginForm = this.formBuilder.group({
        username: ['', Validators.required],
        password: ['', Validators.required]
      });
      this.mensajeError = "";
      this.cargando = false;
    }
  }

  // Para acceder más comodo a los campos del formulario
  get f() { return this.loginForm.controls; }

  onSubmit() {
    if(this.loginForm.invalid) return;

    this.mensajeError = "";
    this.mensajeError = null;
    this.cargando = true;
    this.clienteApi.login(this.f.username.value, this.f.password.value).subscribe(
      resp => {
        if(resp.status == 200) {
          console.log(resp);
          // Login exitoso, redirige a la página de perfil
          this.tokenService.saveUser(resp.body);
          this.tokenService.saveToken(resp.body.token);
          this.navbarService.setLoggedInView(true);
          this.router.navigate(['/perfil']);
        } else {
          this.mensajeError = "'Error' al conectar";
        }
        this.cargando = false;
      },
      err => {
        if(err.status == 504) {
          this.mensajeError = "No hay conexión con el servidor";
        } else if(err.status == 500) {
          // Error interno
          this.mensajeError = "Error inesperado. Vuélvelo a intentar de nuevo";
        } else {
          // Login fallido: limpia el contenido de la contraseña y coloca el focus en el input
          this.f.password.setValue("");
          this.passField.nativeElement.focus();
          this.mensajeError = "Login incorrecto";
        }
        this.cargando = false;
      }
    );
  }

}
