import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { HttpClientModule } from '@angular/common/http';
import { InicioComponent } from './inicio/inicio.component';
import { PerfilComponent } from './perfil/perfil.component';
import { AlertModule } from './alert';
import { LoginComponent } from './auth/login/login.component';
import { RegistroComponent } from './auth/registro/registro.component';
import { ReactiveFormsModule } from '@angular/forms';
import { OlvidoComponent } from './auth/olvido/olvido.component';
import { CreacionCarreraComponent } from './creacion-carrera/creacion-carrera.component';
import { EditorRecorridosComponent } from './editor/editor-recorridos/editor-recorridos.component';
import { EditorTrazadoComponent } from './editor/editor-trazado/editor-trazado.component';

@NgModule({
  declarations: [
    AppComponent,
    InicioComponent,
    LoginComponent,
    PerfilComponent,
    RegistroComponent,
    OlvidoComponent,
    CreacionCarreraComponent,
    EditorRecorridosComponent,
    EditorTrazadoComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    AlertModule,
    ReactiveFormsModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
