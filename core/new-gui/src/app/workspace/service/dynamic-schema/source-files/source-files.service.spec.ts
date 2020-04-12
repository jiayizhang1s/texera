import { TestBed } from '@angular/core/testing';

import { SourceFilesService } from './source-files.service';

describe('SourceFilesService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: SourceFilesService = TestBed.get(SourceFilesService);
    expect(service).toBeTruthy();
  });
});
