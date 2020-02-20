import { TestBed, inject } from '@angular/core/testing';

import { UserDictionaryUploadService } from './user-dictionary-upload.service';

describe('UserDictionaryUploadService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [UserDictionaryUploadService]
    });
  });

  it('should be created', inject([UserDictionaryUploadService], (service: UserDictionaryUploadService) => {
    expect(service).toBeTruthy();
  }));
});
