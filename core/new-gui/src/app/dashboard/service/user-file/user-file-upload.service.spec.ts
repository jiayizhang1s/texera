import { TestBed, inject } from '@angular/core/testing';

import { UserFileUploadService } from './user-file-upload.service';

describe('UserFileUploadService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [UserFileUploadService]
    });
  });

  it('should be created', inject([UserFileUploadService], (service: UserFileUploadService) => {
    expect(service).toBeTruthy();
  }));
});
