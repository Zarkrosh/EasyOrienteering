import { Component, OnInit } from '@angular/core';
import { AlertService } from '../alert';

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

  constructor(protected alertService: AlertService) { }

  ngOnInit() {
    
  }

}
