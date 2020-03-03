import { TestBed, inject } from '@angular/core/testing';

import { UserFileUploadService, postFileUrl } from './user-file-upload.service';
import { UserFileService } from './user-file.service';
import { StubUserFileService } from './stub-user-file.service';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { UserAccountService } from '../user-account/user-account.service';
import { StubUserAccountService } from '../user-account/stub-user-account.service';

const arrayOfBlob: Blob[] = Array<Blob>(); // just for test,needed for creating File object.
const testFileName = 'testTextFile';
const testFile: File = new File( arrayOfBlob, testFileName, {type: 'text/plain'});

describe('UserFileUploadService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        { provide: UserAccountService, useClass: StubUserAccountService },
        { provide: UserFileService, useClass: StubUserFileService },
        UserFileUploadService
      ],
      imports: [
        HttpClientTestingModule
      ]
    });

    afterEach(inject([HttpTestingController], (httpMock: HttpTestingController) => {
      httpMock.verify();
    }));
  });

  it('should be created', inject([UserFileUploadService, UserFileService, HttpTestingController], (service: UserFileUploadService) => {
    expect(service).toBeTruthy();
  }));

  it('should include no file by default', inject([UserFileUploadService, UserFileService, HttpTestingController],
    (service: UserFileUploadService) => {
    expect(service.getFileArrayLength).toBe(0);
    expect(service.getFileUploadItem(0)).toThrowError();
  }));

  it('should insert file successfully', inject([UserFileUploadService, UserFileService, HttpTestingController],
    (service: UserFileUploadService) => {
    service.insertNewFile(testFile);
    expect(service.getFileArrayLength).toBe(1);
    expect(service.getFileArray()[0]).toEqual(service.getFileUploadItem(0));
    expect(service.getFileUploadItem(0).file).toEqual(testFile);
    expect(service.getFileUploadItem(0).name).toEqual(testFileName);
    expect(service.getFileUploadItem(0).isUploadingFlag).toBeFalsy();
    expect(service.getFileUploadItem(1)).toThrowError();
  }));

  it('should delete file successfully', inject([UserFileUploadService, UserFileService, HttpTestingController],
    (service: UserFileUploadService) => {
    service.insertNewFile(testFile);
    expect(service.getFileArrayLength).toBe(1);
    const testFileUploadItem = service.getFileUploadItem(0);
    service.deleteFile(testFileUploadItem);
    expect(service.getFileArrayLength).toBe(0);
  }));

  it('should upload file successfully', inject([UserFileUploadService, UserFileService, HttpTestingController],
    (service: UserFileUploadService, userFileService: UserFileService, httpMock: HttpTestingController) => {
    service.insertNewFile(testFile);
    expect(service.getFileArrayLength).toBe(1);
    service.uploadAllFiles();

    spyOn(userFileService, 'refreshFiles');
    expect(userFileService.refreshFiles).toHaveBeenCalled().then(
      () => expect(service.getFileArrayLength()).toBe(0)
    );

    const req = httpMock.expectOne(postFileUrl);
    expect(req.request.method).toEqual('POST');
    req.flush({code: 0, message: ''});
  }));
});
