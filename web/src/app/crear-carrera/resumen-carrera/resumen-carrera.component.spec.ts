import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ResumenCarreraComponent } from './resumen-carrera.component';

describe('ResumenCarreraComponent', () => {
  let component: ResumenCarreraComponent;
  let fixture: ComponentFixture<ResumenCarreraComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ResumenCarreraComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ResumenCarreraComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
