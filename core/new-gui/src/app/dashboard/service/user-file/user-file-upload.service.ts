import { Injectable } from '@angular/core';
import { FileUploadItem } from '../../type/file-upload-item';
import { GenericWebResponse } from '../../type/generic-web-response';
import { Observable } from 'rxjs';
import { UserAccountService } from '../user-account/user-account.service';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../../environments/environment';
import { UserFileService } from './user-file.service';

const postFileUrl = 'users/files/upload-file';

@Injectable()
export class UserFileUploadService {
  private fileUploadItemArray: FileUploadItem[] = [];

  constructor(
    private userAccountService: UserAccountService,
    private userFileService: UserFileService,
    private http: HttpClient) {}

  public insertNewFile(file: File): void {
    this.fileUploadItemArray.push(new FileUploadItem(file));
  }

  public getFileArray(): FileUploadItem[] {
    return this.fileUploadItemArray;
  }

  public getFileArrayLength(): number {
    return this.fileUploadItemArray.length;
  }

  public getFileUploadItem(index: number): FileUploadItem {
    return this.fileUploadItemArray[index];
  }

  public deleteFile(fileUploadItem: FileUploadItem): void {
    this.fileUploadItemArray = this.fileUploadItemArray.filter(
      file => file !== fileUploadItem
    );
  }

  public uploadAllFiles() {
    this.fileUploadItemArray.forEach(
      fileUploadItem => this.uploadFile(fileUploadItem).subscribe(
        () => {
          this.userFileService.updateFiles();
          this.deleteFile(fileUploadItem);
        }, error => {
          // TODO
        }
      )
    );
  }

  private uploadFile(fileUploadItem: FileUploadItem): Observable<GenericWebResponse> {
    fileUploadItem.setUploading(true);
    const formData: FormData = new FormData();
    formData.append('file', fileUploadItem.getFile(), fileUploadItem.getName());
    formData.append('size', fileUploadItem.getSize().toString());
    formData.append('description', fileUploadItem.description);
    return this.postUserFile(formData);
  }

  private postUserFile(formData: FormData): Observable<GenericWebResponse> {
    return this.http.post<GenericWebResponse>(`${environment.apiUrl}/${postFileUrl}`, formData);
  }
}
