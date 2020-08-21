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
import { ReactiveFormsModule } from '@angular/forms';
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
    ExplorarComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    AlertModule,
    ReactiveFormsModule,
    NgbModule,
    LeafletModule,
    InfiniteScrollModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
