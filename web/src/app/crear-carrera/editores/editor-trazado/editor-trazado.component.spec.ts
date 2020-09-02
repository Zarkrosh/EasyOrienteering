import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { EditorTrazadoComponent } from './editor-trazado.component';

describe('EditorTrazadoComponent', () => {
  let component: EditorTrazadoComponent;
  let fixture: ComponentFixture<EditorTrazadoComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ EditorTrazadoComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(EditorTrazadoComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
