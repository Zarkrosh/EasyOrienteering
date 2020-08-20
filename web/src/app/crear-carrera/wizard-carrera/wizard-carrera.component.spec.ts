import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { WizardCarreraComponent } from './wizard-carrera.component';

describe('WizardCarreraComponent', () => {
  let component: WizardCarreraComponent;
  let fixture: ComponentFixture<WizardCarreraComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ WizardCarreraComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(WizardCarreraComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
