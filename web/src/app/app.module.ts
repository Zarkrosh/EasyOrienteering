import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { HttpClientModule } from '@angular/common/http';
import { LeafletModule } from '@asymmetrik/ngx-leaflet';
import { InfiniteScrollModule } from 'ngx-infinite-scroll';
import { NgbModule} from '@ng-bootstrap/ng-bootstrap';
import { InicioComponent } from './inicio/inicio.component';
import { PerfilComponent } from './perfil/perfil.component';
import { AlertModule } from './alert';
import { LoginComponent } from './auth/login/login.component';
import { RegistroComponent } from './auth/registro/registro.component';
import { ReactiveFormsModule, FormsModule } from '@angular/forms';
import { OlvidoComponent } from './auth/olvido/olvido.component';
import { WizardCarreraComponent } from './crear-carrera/wizard-carrera/wizard-carrera.component';
import { EditorRecorridosComponent } from './crear-carrera/editores/editor-recorridos/editor-recorridos.component';
import { EditorTrazadoComponent } from './crear-carrera/editores/editor-trazado/editor-trazado.component';
import { VistaCarreraComponent } from './vista-carrera/vista-carrera.component';
import { ResumenCarreraComponent } from './crear-carrera/resumen-carrera/resumen-carrera.component';
import { QueEsComponent } from './que-es/que-es.component';
import { GenerarQRComponent } from './vista-carrera/generar-qr/generar-qr.component';
import { EditorUbicacionComponent } from './crear-carrera/editores/editor-ubicacion/editor-ubicacion.component';
import { ExplorarComponent } from './explorar/explorar.component';
import { MapaCircuitosComponent } from './explorar/mapa-circuitos/mapa-circuitos.component';
import { DatePipe } from '@angular/common';
import { ResultadosComponent } from './vista-carrera/resultados/resultados.component';
import { PipeTiempo } from './shared/pipes/PipeTiempo';
import { NotFoundComponent } from './not-found/not-found.component';
import { PipeFechaBonita } from './shared/pipes/PipeFechaBonita';

@NgModule({
  declarations: [
    AppComponent,
    InicioComponent,
    LoginComponent,
    PerfilComponent,
    RegistroComponent,
    OlvidoComponent,
    WizardCarreraComponent,
    EditorRecorridosComponent,
    EditorTrazadoComponent,
    VistaCarreraComponent,
    ResumenCarreraComponent,
    QueEsComponent,
    GenerarQRComponent,
    EditorUbicacionComponent,
    ExplorarComponent,
    MapaCircuitosComponent,
    ResultadosComponent,
    PipeTiempo,
    PipeFechaBonita,
    NotFoundComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    AlertModule,
    FormsModule,
    ReactiveFormsModule,
    NgbModule,
    LeafletModule,
    InfiniteScrollModule
  ],
  providers: [DatePipe],
  bootstrap: [AppComponent]
})
export class AppModule { }
