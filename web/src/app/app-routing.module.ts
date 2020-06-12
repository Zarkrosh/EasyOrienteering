import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { InicioComponent } from './inicio/inicio.component';
import { LoginComponent } from './auth/login/login.component';
import { PerfilComponent } from './perfil/perfil.component';
import { RegistroComponent } from './auth/registro/registro.component';
import { OlvidoComponent } from './auth/olvido/olvido.component';
import { CreacionCarreraComponent } from './creacion-carrera/creacion-carrera.component';
import { TrazadorComponent } from './trazador/trazador.component';
import { EditorRecorridosComponent } from './editor-recorridos/editor-recorridos.component';


const routes: Routes = [
  {path: '', component: InicioComponent},
  {path: 'login', component: LoginComponent},
  {path: 'registro', component: RegistroComponent},
  {path: 'olvido', component: OlvidoComponent},
  {path: 'perfil', component: PerfilComponent},
  {path: 'carreras/crear', component: CreacionCarreraComponent},
  {path: 'carreras/crear/recorridos', component: EditorRecorridosComponent}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
