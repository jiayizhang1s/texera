import { Injectable, EventEmitter } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';

import { Observable } from 'rxjs/Observable';
import { UserDictionary } from '../../type/user-dictionary';
import { environment } from '../../../../environments/environment';
import { GenericWebResponse } from '../../type/generic-web-response';
import { UserAccountService } from '../user-account/user-account.service';

const getDictionaryUrl = 'users/dictionaries/get-dictionary';
const deleteDictionaryUrl = 'users/dictionaries/delete-dictionary';
const updateDictionaryUrl = 'users/dictionaries/update-dictionary';

/**
 * User Dictionary service should be able to get all the saved-dictionary
 *  data from the back end for a specific user. The user can also upload new
 *  dictionary, view dictionaries, and edit the keys in a specific dictionary
 *  by calling methods in service. StubUserDictionaryService is used for replacing
 *  real service to complete testing cases. It uploads the mock data to the dashboard.
 *
 * @author Chen He
 */

@Injectable()
export class UserDictionaryService {
  private dictionaryArray: UserDictionary[] = [];

  constructor(
    private http: HttpClient,
    private userAccountService: UserAccountService) {
      this.detectUserChanges();
    }

  public refreshDictionary(): void {
    if (!this.userAccountService.isLogin()) {return; }

    this.getDictionaryHttpRequest(
      this.userAccountService.getCurrentUserField('userID')
      ).subscribe(
      dictionaries => this.dictionaryArray = dictionaries
    );
  }

  public deleteDictionary(dictID: number) {
    this.deleteDictionaryHttpRequest(dictID).subscribe(
      () => this.refreshDictionary()
    );
  }

  public updateDictionary(userDictionary: UserDictionary): void {
    this.updateDictionaryHttpRequest(userDictionary)
      .subscribe(
        () => this.refreshDictionary()
      );
  }

  public getDictionaryArray(): UserDictionary[] {
    return this.dictionaryArray;
  }

  public getDictionaryArrayLength(): number {
    return this.dictionaryArray.length;
  }

  private getDictionaryHttpRequest(userID: number): Observable<UserDictionary[]> {
    return this.http.get<UserDictionary[]>(`${environment.apiUrl}/${getDictionaryUrl}/${userID}`);
  }

  private deleteDictionaryHttpRequest(dictID: number): Observable<GenericWebResponse> {
    return this.http.delete<GenericWebResponse>(`${environment.apiUrl}/${deleteDictionaryUrl}/${dictID}`);
  }

  private updateDictionaryHttpRequest(userDictionary: UserDictionary): Observable<GenericWebResponse> {
    return this.http.post<GenericWebResponse>(`${environment.apiUrl}/${updateDictionaryUrl}`,
    JSON.stringify(userDictionary),
    {
      headers: new HttpHeaders({
        'Content-Type':  'application/json',
      })
    }
    );
  }

  private detectUserChanges(): void {
    this.userAccountService.getUserChangeEvent().subscribe(
      () => {
        if (this.userAccountService.isLogin()) {
          this.refreshDictionary();
        } else {
          this.clearDictionary();
        }
      }
    );
  }

  private clearDictionary(): void {
    this.dictionaryArray = [];
  }

}
