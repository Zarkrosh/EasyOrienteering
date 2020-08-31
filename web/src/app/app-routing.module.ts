import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { InicioComponent } from './inicio/inicio.component';
import { LoginComponent } from './auth/login/login.component';
import { PerfilComponent } from './perfil/perfil.component';
import { RegistroComponent } from './auth/registro/registro.component';
import { OlvidoComponent } from './auth/olvido/olvido.component';
import { VistaCarreraComponent } from './vista-carrera/vista-carrera.component';
import { QueEsComponent } from './que-es/que-es.component';
import { GenerarQRComponent } from './vista-carrera/generar-qr/generar-qr.component';
import { WizardCarreraComponent } from './crear-carrera/wizard-carrera/wizard-carrera.component';
import { EditorRecorridosComponent } from './crear-carrera/editores/editor-recorridos/editor-recorridos.component';
import { ResumenCarreraComponent } from './crear-carrera/resumen-carrera/resumen-carrera.component';
import { ExplorarComponent } from './explorar/explorar.component';
import { MapaCircuitosComponent } from './explorar/mapa-circuitos/mapa-circuitos.component';
import { ResultadosComponent } from './vista-carrera/resultados/resultados.component';


const routes: Routes = [
  {path: '', component: InicioComponent},
  {path: 'login', component: LoginComponent},
  {path: 'registro', component: RegistroComponent},
  {path: 'olvido', component: OlvidoComponent},
  {path: 'perfil', component: PerfilComponent},
  {path: 'easyorienteering', component: QueEsComponent},
  {path: 'explorar', component: ExplorarComponent},
  {path: 'crear/wizard', component: WizardCarreraComponent},
  {path: 'crear/recorridos', component: EditorRecorridosComponent},
  {path: 'crear/controles', component: EditorRecorridosComponent},
  {path: 'crear/nueva', component: ResumenCarreraComponent},
  {path: 'crear', component: ResumenCarreraComponent},
  {path: 'editar/:id', component: ResumenCarreraComponent},
  {path: 'carreras/:id', component: VistaCarreraComponent},
  {path: 'carreras/:id/qr', component: GenerarQRComponent},
  {path: 'resultados/:id', component: ResultadosComponent}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
