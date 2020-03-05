import { TestBed, inject } from '@angular/core/testing';

import { UserDictionaryUploadService } from './user-dictionary-upload.service';
import { UserAccountService } from '../user-account/user-account.service';

describe('UserDictionaryUploadService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        UserDictionaryUploadService,
        UserAccountService
      ]
    });
  });

  // it('should be created', inject([UserDictionaryUploadService], (service: UserDictionaryUploadService) => {
  //   expect(service).toBeTruthy();
  // }));
});
