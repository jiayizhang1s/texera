import { Injectable } from '@angular/core';
import { ManualDictionary, DictionaryUploadItem } from '../../type/user-dictionary';
import { UserAccountService } from '../user-account/user-account.service';
import { UserDictionaryService } from './user-dictionary.service';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { GenericWebResponse } from '../../type/generic-web-response';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { isNgTemplate } from '@angular/compiler';

const postDictUrl = 'users/dictionaries/upload-dict';
const putManualDictUrl = 'users/dictionaries/upload-manual-dict';

@Injectable({
  providedIn: 'root'
})
export class UserDictionaryUploadService {
  public manualDictionary: ManualDictionary = this.createEmptyManualDictionary();
  private dictionaryUploadItemArray: DictionaryUploadItem[] = [];

  constructor(
    private userAccountService: UserAccountService,
    private userDictionaryService: UserDictionaryService,
    private http: HttpClient
    ) {
      this.detectUserChanges();
    }

    public getDictionaryArray(): DictionaryUploadItem[] {
      return this.dictionaryUploadItemArray;
    }

    public getDictionaryArrayLength(): number {
      return this.dictionaryUploadItemArray.length;
    }

    public getDictionaryUploadItem(index: number): DictionaryUploadItem {
      return this.dictionaryUploadItemArray[index];
    }

    public isManualDictionaryValid(): boolean {
      return this.manualDictionary.name !== '' && this.manualDictionary.content !== '';
    }

    public isItemValid(dictionaryUploadItem: DictionaryUploadItem): boolean {
      return dictionaryUploadItem.file.type.includes('text/plain') && this.isItemNameUnique(dictionaryUploadItem);
    }

    public isItemNameUnique(dictionaryUploadItem: DictionaryUploadItem): boolean {
      return this.dictionaryUploadItemArray
        .filter(item => item.name === dictionaryUploadItem.name)
        .length === 1;
    }

    public isUploadEnable(): boolean {
      return this.dictionaryUploadItemArray.every(
        dictionaryUploadItem => this.isItemValid(dictionaryUploadItem)
      );
    }

    public insertNewDictionary(file: File): void {
      this.dictionaryUploadItemArray.push(this.createDictionaryUploadItem(file));
    }

    public deleteDictionary(dictionaryUploadItem: DictionaryUploadItem): void {
      this.dictionaryUploadItemArray = this.dictionaryUploadItemArray.filter(
        dict => dict !== dictionaryUploadItem
      );
    }

    public uploadAllDictionary() {
      this.dictionaryUploadItemArray.forEach(
        dictionaryUploadItem => this.uploadDictionary(dictionaryUploadItem).subscribe(
          () => {
            this.userDictionaryService.refreshDictionary();
            this.deleteDictionary(dictionaryUploadItem);
          }, error => {
            // TODO
            console.log(error);
            alert(`Error encountered: ${error.status}\nMessage: ${error.message}`);
          }
        )
      );
    }

    public uploadManualDictionary(): Observable<GenericWebResponse> {
      if (!this.userAccountService.isLogin()) {throw new Error(`Can not upload manual dictionary when not login`); }
      if (!this.isManualDictionaryValid) {throw new Error(`Can not upload invalid manual dictionary`); }

      if (this.manualDictionary.separator === '') { this.manualDictionary.separator = ','; }
      return this.putManualDictionaryHttpRequest(this.manualDictionary, this.userAccountService.getCurrentUserField('userID'));
    }

    private putManualDictionaryHttpRequest(manualDictionary: ManualDictionary, userID: number): Observable<GenericWebResponse> {
      return this.http.put<GenericWebResponse>(
        `${environment.apiUrl}/${putManualDictUrl}/${userID}`,
        JSON.stringify(manualDictionary),
        {
          headers: new HttpHeaders({
            'Content-Type':  'application/json',
          })
        }
      );
    }

    private uploadDictionary(dictionaryUploadItem: DictionaryUploadItem): Observable<GenericWebResponse> {
      if (!this.userAccountService.isLogin()) {
        throw new Error(`Can not upload files when not login`);
      }
      const formData: FormData = new FormData();
      formData.append('file', dictionaryUploadItem.file, dictionaryUploadItem.name);
      formData.append('description', dictionaryUploadItem.description);
      return this.postDictionaryHttpRequest(formData, this.userAccountService.getCurrentUserField('userID'));
    }

    private postDictionaryHttpRequest(formData: FormData, userID: number): Observable<GenericWebResponse> {
      return this.http.post<GenericWebResponse>(
        `${environment.apiUrl}/${postDictUrl}/${userID}`,
        formData
        );
    }

    private detectUserChanges(): void {
      this.userAccountService.getUserChangeEvent().subscribe(
        () => {
          if (!this.userAccountService.isLogin()) {
            this.clearUserDictionary();
          }
        }
      );
    }

    private clearUserDictionary(): void {
      this.dictionaryUploadItemArray = [];
      this.manualDictionary = this.createEmptyManualDictionary();
    }

    private createEmptyManualDictionary(): ManualDictionary {
      return {
        name : '',
        content: '',
        separator: '',
        description: ''
      };
    }

    private createDictionaryUploadItem(file: File): DictionaryUploadItem {
      return {
        file : file,
        name: file.name,
        description: ''
      };
    }
}
