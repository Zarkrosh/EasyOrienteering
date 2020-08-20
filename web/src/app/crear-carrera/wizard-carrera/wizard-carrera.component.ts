import { Component, OnInit, ElementRef, ViewChild } from '@angular/core';
import { FormGroup, FormBuilder, Validators } from '@angular/forms';
import { AppSettings, Carrera } from '../../shared/app.model';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { Router, ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-wizard-carrera',
  templateUrl: './wizard-carrera.component.html',
  styleUrls: ['./wizard-carrera.component.scss']
})
export class WizardCarreraComponent implements OnInit {

  carrera: Carrera;

  constructor(
    private router: Router) { }

  ngOnInit() {
    
  }

  /*
  clickRecorridos() {
    if(this.carreraForm.valid) {
      // TODO Comprobar valores correctos
      this.carreraDesdeDatos();
      this.router.navigate(['crear', 'recorridos']);
    } else {
      // TODO
    }
  }
  */

}
