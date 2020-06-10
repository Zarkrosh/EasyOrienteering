import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ClienteApiService {
  // URL base de la API
  private static readonly BASE_URL = '/api/';
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


}
