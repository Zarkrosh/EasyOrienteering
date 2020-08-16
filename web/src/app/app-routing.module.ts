import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { InicioComponent } from './inicio/inicio.component';
import { LoginComponent } from './auth/login/login.component';
import { PerfilComponent } from './perfil/perfil.component';
import { RegistroComponent } from './auth/registro/registro.component';
import { OlvidoComponent } from './auth/olvido/olvido.component';
import { CreacionCarreraComponent } from './creacion-carrera/creacion-carrera.component';
import { EditorRecorridosComponent } from './editor/editor-recorridos/editor-recorridos.component';
import { VistaCarreraComponent } from './vista-carrera/vista-carrera.component';
import { ResumenCarreraComponent } from './creacion-carrera/resumen-carrera/resumen-carrera.component';
import { QueEsComponent } from './que-es/que-es.component';


const routes: Routes = [
  {path: '', component: InicioComponent},
  {path: 'login', component: LoginComponent},
  {path: 'registro', component: RegistroComponent},
  {path: 'olvido', component: OlvidoComponent},
  {path: 'perfil', component: PerfilComponent},
  {path: 'easyorienteering', component: QueEsComponent},
  {path: 'carreras/crear/datos', component: CreacionCarreraComponent},
  {path: 'carreras/crear/recorridos', component: EditorRecorridosComponent},
  {path: 'carreras/crear/resumen', component: ResumenCarreraComponent},
  {path: 'carreras/:id', component: VistaCarreraComponent}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
