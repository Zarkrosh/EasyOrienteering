import { TestBed } from '@angular/core/testing';

import { ClienteApiService } from './cliente-api.service';

describe('ClienteApiService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: ClienteApiService = TestBed.get(ClienteApiService);
    expect(service).toBeTruthy();
  });
});
