import { Injectable } from '@angular/core';
import { FileUploadItem } from '../../type/file-upload-item';
import { GenericWebResponse } from '../../type/generic-web-response';
import { Observable } from 'rxjs';
import { UserAccountService } from '../user-account/user-account.service';
import { HttpClient } from '@angular/common/http';
import { environment } from 'src/environments/environment';
import { UserFileService } from './user-file.service';

const postFileUrl = 'users/files/upload-file';

@Injectable()
export class UserFileUploadService {
  private fileUploadItems: FileUploadItem[] = [];

  constructor(
    private userAccountService: UserAccountService,
    private userFileService: UserFileService,
    private http: HttpClient) {}

  public insertNewFile(file: File): void {
    this.fileUploadItems.push(new FileUploadItem(file));
  }

  public getFileArrayLength(): number {
    return this.fileUploadItems.length;
  }

  public getFileUploadItem(index: number): FileUploadItem {
    return this.fileUploadItems[index];
  }

  public deleteFile(fileUploadItem: FileUploadItem): void {
    this.fileUploadItems.filter(
      file => file !== fileUploadItem
    );
  }

  public uploadAllFiles() {
    this.fileUploadItems.forEach(
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
