import { TestBed } from '@angular/core/testing';

import { SharedEditorService } from './shared-editor.service';

describe('SharedEditorService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: SharedEditorService = TestBed.get(SharedEditorService);
    expect(service).toBeTruthy();
  });
});
