import { Component, OnInit } from '@angular/core';
import { AlertService } from '../alert';
import { TokenStorageService } from '../_services/token-storage.service';

@Component({
  selector: 'app-inicio',
  templateUrl: './inicio.component.html',
  styleUrls: ['./inicio.component.scss']
})
export class InicioComponent implements OnInit {

  // Alertas
  options = {
    autoClose: true,
    keepAfterRouteChange: false
  };

  conectado: boolean = false;

  constructor(protected alertService: AlertService,
    public tokenService: TokenStorageService) { }

  ngOnInit() {
    this.conectado = this.tokenService.isLoggedIn();
  }

}
