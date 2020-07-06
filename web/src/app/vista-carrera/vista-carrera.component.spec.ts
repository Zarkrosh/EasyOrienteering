import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { VistaCarreraComponent } from './vista-carrera.component';

describe('VistaCarreraComponent', () => {
  let component: VistaCarreraComponent;
  let fixture: ComponentFixture<VistaCarreraComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ VistaCarreraComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(VistaCarreraComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
