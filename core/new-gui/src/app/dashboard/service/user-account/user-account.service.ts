import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';

import { Observable } from 'rxjs/Observable';
import { environment } from '../../../../environments/environment';
import { EventEmitter } from '@angular/core';
import { observable } from 'rxjs';
import { UserAccount } from '../../type/user-account';
import { UserAccountResponse } from '../../type/user-account';

const registerURL = 'users/accounts/register';
const loginURL = 'users/accounts/login';


@Injectable()
export class UserAccountService {
  private userChangeEvent: EventEmitter<UserAccount> = new EventEmitter();
  private currentUser: UserAccount = this.createEmptyUser();
  private isLoginFlag: boolean = false;

  constructor(private http: HttpClient) { }

  public registerUser(userName: string): Observable<UserAccountResponse> {
    if (this.isLogin()) {throw new Error('Already logged in when register.'); }
    if (this.checkUserAuthorizationLegal(userName)) {throw new Error(`userName ${userName} is illegal`); }

    return this.register(userName).map(
      res => {
        if (res.code === 0) {
          this.changeUser(res.userAccount, res.code);
          return res;
        } else { // register failed
          return res;
        }
      }
    );
  }

  public loginUser(userName: string):  Observable<UserAccountResponse> {
    if (this.isLogin()) {throw new Error('Already logged in when login in.'); }
    if (this.checkUserAuthorizationLegal(userName)) {throw new Error(`userName ${userName} is illegal`); }

    return this.login(userName).map(
      res => {
        if (res.code === 0) {
          this.changeUser(res.userAccount, res.code);
          return res;
        } else { // login in failed
          return res;
        }
      }
    );
  }

  public logOut(): void {
    this.changeUser(this.createEmptyUser(), 1);
  }

  public isLogin(): boolean {
    return this.isLoginFlag;
  }

  // public getCurrentUser(): UserAccount {
  //   return this.currentUser;
  // }

  public getCurrentUserField<Field extends keyof UserAccount>(field: Field): UserAccount[Field] {
    return this.currentUser[field];
  }

  public getUserChangeEvent(): EventEmitter<UserAccount> {
    return this.userChangeEvent;
  }


  private register(userName: string): Observable<UserAccountResponse> {
    const formData: FormData = new FormData();
    formData.append('userName', userName);
    return this.http.post<UserAccountResponse>(`${environment.apiUrl}/${registerURL}`, formData);
  }

  private login(userName: string): Observable<UserAccountResponse> {
    const formData: FormData = new FormData();
    formData.append('userName', userName);
    return this.http.post<UserAccountResponse>(`${environment.apiUrl}/${loginURL}`, formData);
  }

  private createEmptyUser(): UserAccount {
    const emptyUser: UserAccount = {
      userName : '',
      userID : -1
    };
    return emptyUser;
  }

  private changeUser(userAccount: UserAccount, code: 0 | 1): void {
    this.isLoginFlag = code === 0;
    this.currentUser = userAccount;
    this.userChangeEvent.emit(this.currentUser);
  }

  private checkUserAuthorizationLegal(userName: string) {
    return !this.isLogin() &&
      userName !== null &&
      userName.length > 0;
  }

}
