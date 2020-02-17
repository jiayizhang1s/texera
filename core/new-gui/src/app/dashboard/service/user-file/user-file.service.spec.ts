import { TestBed, inject } from '@angular/core/testing';

import { UserFileService } from './user-file.service';

describe('UserFileService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [UserFileService]
    });
  });

  it('should be created', inject([UserFileService], (service: UserFileService) => {
    expect(service).toBeTruthy();
  }));
});
