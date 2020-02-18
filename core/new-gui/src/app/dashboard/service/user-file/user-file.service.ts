import { Injectable } from '@angular/core';
import { UserFile } from '../../type/user-file';
import { UserAccountService } from '../user-account/user-account.service';
import { Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';

import { environment } from '../../../../environments/environment';
import { GenericWebResponse } from '../../type/generic-web-response';
import { FileUploadItem } from '../../type/file-upload-item';

const getFilesUrl = 'users/files/get-files';
const deleteFilesUrl = 'users/files/delete-file';

@Injectable()
export class UserFileService {
  private fileArray: UserFile[] = [];

  constructor(
    private userAccountService: UserAccountService,
    private http: HttpClient) {
      this.detectUserChanges();
    }

  public getFile(index: number): UserFile {
    return this.fileArray[index];
  }

  public getFileArray(): UserFile[] {
    return this.fileArray;
  }

  public getFileField<Field extends keyof UserFile>(index: number, field: Field): UserFile[Field] {
    return this.getFile(index)[field];
  }

  public getFileArrayLength(): number {
    return this.fileArray.length;
  }

  public updateFiles(): void {
    if (!this.userAccountService.isLogin()) {return; }

    this.getFilesHttpRequest(
      this.userAccountService.getCurrentUserField('userID')
      ).subscribe(
      files => this.fileArray = files
    );
  }

  public deleteFile(targetFile: UserFile): void {
    this.deleteFileHttpRequest(targetFile.id).subscribe(
      () => this.updateFiles()
    );
  }

  public deleteFileHttpRequest(fileID: number): Observable<GenericWebResponse> {
    return this.http.delete<GenericWebResponse>(`${environment.apiUrl}/${deleteFilesUrl}/${fileID}`);
  }


  private getFilesHttpRequest(userID: number): Observable<UserFile[]> {
    return this.http.get<UserFile[]>(`${environment.apiUrl}/${getFilesUrl}/${userID}`);
  }

  private detectUserChanges(): void {
    this.userAccountService.getUserChangeEvent().subscribe(
      () => {
        if (this.userAccountService.isLogin()) {
          this.updateFiles();
        } else {
          this.clearUserFile();
        }
      }
    );
  }

  private clearUserFile(): void {
    this.fileArray = [];
  }
}
