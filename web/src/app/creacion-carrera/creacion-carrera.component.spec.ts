import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { CreacionCarreraComponent } from './creacion-carrera.component';

describe('CreacionCarreraComponent', () => {
  let component: CreacionCarreraComponent;
  let fixture: ComponentFixture<CreacionCarreraComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ CreacionCarreraComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CreacionCarreraComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
