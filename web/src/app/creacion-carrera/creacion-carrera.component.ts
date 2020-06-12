import { Component, OnInit } from '@angular/core';
import { FormGroup, FormBuilder, Validators } from '@angular/forms';

@Component({
  selector: 'app-creacion-carrera',
  templateUrl: './creacion-carrera.component.html',
  styleUrls: ['./creacion-carrera.component.scss']
})
export class CreacionCarreraComponent implements OnInit {
  carreraForm: FormGroup;

  constructor(private formBuilder: FormBuilder) { }

  ngOnInit() {
    this.carreraForm = this.formBuilder.group({
      tipo: ['', Validators.required],
      modalidad: ['', Validators.required],
      cronometraje: ['', Validators.required],
      modomapa: ['', Validators.required]
    });
  }

}
