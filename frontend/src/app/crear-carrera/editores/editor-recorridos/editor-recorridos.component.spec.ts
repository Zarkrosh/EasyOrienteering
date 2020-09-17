import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { EditorRecorridosComponent } from './editor-recorridos.component';

describe('EditorRecorridosComponent', () => {
  let component: EditorRecorridosComponent;
  let fixture: ComponentFixture<EditorRecorridosComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ EditorRecorridosComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(EditorRecorridosComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
