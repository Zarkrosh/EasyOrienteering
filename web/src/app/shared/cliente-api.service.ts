import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse, HttpHeaders, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Carrera } from './app.model';

@Injectable({
  providedIn: 'root'
})
export class ClienteApiService {
  // URL base de la API
  private static readonly BASE_URL = '/api/';
  private static readonly BASE_CARRERAS = 'carreras/';
  // Autenticación
  private static readonly API_LOGIN = 'login/';
  private static readonly API_REGISTRO = 'registro/';
  

  // Parámetros de la API
  private static readonly PAR_NOMBRE = 'nombre';
  private static readonly PAR_EMAIL = 'email';
  private static readonly PAR_NOMBRE_EMAIL = 'nombreEmail';
  private static readonly PAR_PASSWORD = 'password';


  constructor(private http: HttpClient) { }


  /******************* AUTENTICACIÓN *******************/
  /**
   * Realiza la petición de login de las credenciales de un usuario.
   * @param nombreEmail Nombre de usuario o correo electrónico
   * @param password Contraseña de la cuenta
   */
  login(nombreEmail: string, password: string) : Observable<HttpResponse<any>> {
    let url = ClienteApiService.BASE_URL + ClienteApiService.API_LOGIN;
    // Contenido
    var datos = {};
    datos[ClienteApiService.PAR_NOMBRE_EMAIL] = nombreEmail;
    datos[ClienteApiService.PAR_PASSWORD] = password;
    // Cabeceras
    const cabeceras = new HttpHeaders({ 'Content-Type': 'application/json' });
    return this.http.post<any>(url, datos, {observe: 'response', headers: cabeceras});
  }

  /**
   * TODO Logout 
   */

  /**
   * Realiza la petición de registro con los datos de un usuario.
   * @param nombre Nombre de usuario
   * @param email Email
   * @param password Contraseña
   */
  register(nombre: string, email: string, password: string) : Observable<HttpResponse<any>> {
    let url = ClienteApiService.BASE_URL + ClienteApiService.API_REGISTRO;
    // Contenido
    var datos = {};
    datos[ClienteApiService.PAR_NOMBRE] = nombre;
    datos[ClienteApiService.PAR_EMAIL] = email;
    datos[ClienteApiService.PAR_PASSWORD] = password;
    // Cabeceras
    const cabeceras = new HttpHeaders({ 'Content-Type': 'application/json' });
    return this.http.post<any>(url, datos, {observe: 'response', headers: cabeceras});
  }


  /******************* CARRERAS *******************/

  getCarrera(idCarrera: number) {
    let url = ClienteApiService.BASE_URL + ClienteApiService.BASE_CARRERAS + idCarrera;
    return this.http.get<Carrera>(url, {observe: 'response'});
  }

  crearCarrera(carrera: Carrera) {
    let url = ClienteApiService.BASE_URL + ClienteApiService.BASE_CARRERAS;
    // Cabeceras
    const cabeceras = new HttpHeaders({ 'Content-Type': 'application/json' });
    return this.http.post<any>(url, carrera, {observe: 'response', headers: cabeceras});
  }

  getControlesQRCarrera(idCarrera: number) {
    let url = ClienteApiService.BASE_URL + ClienteApiService.BASE_CARRERAS + idCarrera + '/qr';
    return this.http.get<any>(url, {observe: 'response'});
  }

  cambiaUbicacionCarrera(idCarrera: number, latitud: number, longitud: number) {
    let url = ClienteApiService.BASE_URL + ClienteApiService.BASE_CARRERAS + idCarrera + '/ubicacion';
    let ubicacion = new Object();
    ubicacion["latitud"] = latitud;
    ubicacion["longitud"] = longitud;
    return this.http.put<any>(url, ubicacion, {observe: 'response'});
  }

  buscaCarreras(nombre: string, tipo: string, modalidad: string, pagina: number, numero: number): Observable<HttpResponse<Carrera[]>> {
    let url = ClienteApiService.BASE_URL + ClienteApiService.BASE_CARRERAS + 'buscar';
    let params = new HttpParams();
    params = params.append('nombre', (nombre !== null) ? nombre : "");
    params = params.append('tipo', (tipo !== null) ? tipo : "");
    params = params.append('modalidad', (modalidad !== null) ? modalidad : "");
    params = params.append('pagina', (pagina !== null) ? pagina.toString() : "0");
    params = params.append('numero', (numero !== null) ? numero.toString() : "20");
    return this.http.get<Carrera[]>(url, {params: params, observe: 'response'});
  }

}
