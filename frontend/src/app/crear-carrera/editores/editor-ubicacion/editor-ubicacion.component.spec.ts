import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { EditorUbicacionComponent } from './editor-ubicacion.component';

describe('EditorUbicacionComponent', () => {
  let component: EditorUbicacionComponent;
  let fixture: ComponentFixture<EditorUbicacionComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ EditorUbicacionComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(EditorUbicacionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
