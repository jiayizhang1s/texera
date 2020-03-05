import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';

import { Observable } from 'rxjs/Observable';
import { environment } from '../../../../environments/environment';
import { EventEmitter } from '@angular/core';
import { observable } from 'rxjs';
import { UserAccount } from '../../type/user-account';
import { UserAccountResponse } from '../../type/user-account';
import { UserAccountService } from './user-account.service';

export const stubUserID = 1;
export const stubUserName = 'testUser';

@Injectable()
export class StubUserAccountService extends UserAccountService {
  private stubUserChangeEvent: EventEmitter<UserAccount> = new EventEmitter();
  private stubIsLoginFlag: boolean = false;

  constructor(private stubHttp: HttpClient) {
    super(stubHttp);
  }

  public getUserID(): number {
    return stubUserID;
  }

  public registerUser(userName: string): Observable<UserAccountResponse> {
    this.stubIsLoginFlag = true;
    this.stubUserChangeEvent.emit({
      userName: stubUserName,
      userID: stubUserID
    });
    return Observable.of(
      {
        code: 0,
        message: '',
        userAccount: {
          userName: stubUserName,
          userID: stubUserID
        }
    }
    );
  }

  public loginUser(userName: string):  Observable<UserAccountResponse> {
    this.stubIsLoginFlag = true;
    this.stubUserChangeEvent.emit({
      userName: stubUserName,
      userID: stubUserID
    });
    return Observable.of(
      {
        code: 0,
        message: '',
        userAccount: {
          userName: stubUserName,
          userID: stubUserID
        }
    }
    );
  }

  public logOut(): void {
    this.stubIsLoginFlag = false;
    this.stubUserChangeEvent.emit({
      userName: '',
      userID: -1
    });
  }

  public isLogin(): boolean {
    return this.stubIsLoginFlag;
  }

  public getUserChangeEvent(): EventEmitter<UserAccount> {
    return this.stubUserChangeEvent;
  }

}
