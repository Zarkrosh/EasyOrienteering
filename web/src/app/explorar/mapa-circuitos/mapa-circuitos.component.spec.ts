import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { MapaCircuitosComponent } from './mapa-circuitos.component';

describe('MapaCircuitosComponent', () => {
  let component: MapaCircuitosComponent;
  let fixture: ComponentFixture<MapaCircuitosComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ MapaCircuitosComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(MapaCircuitosComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
