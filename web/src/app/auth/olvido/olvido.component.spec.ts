import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { OlvidoComponent } from './olvido.component';

describe('OlvidoComponent', () => {
  let component: OlvidoComponent;
  let fixture: ComponentFixture<OlvidoComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ OlvidoComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(OlvidoComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
