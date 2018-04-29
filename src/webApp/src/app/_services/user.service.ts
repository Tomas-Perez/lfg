import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {SignUpInfo} from '../_models/json/SignUpInfo';
import {Observable} from 'rxjs/Observable';
import {catchError, map} from 'rxjs/operators';
import {SignUpStatus} from '../_models/SignUpStatus';

@Injectable()
export class UserService {

  private signUpUrl = 'http://localhost:8080/lfg/sign-up';

  constructor(private http: HttpClient) { }

  signUp(signUpInfo: SignUpInfo): Observable<SignUpStatus> {
    return this.http.post<any>(this.signUpUrl, signUpInfo, {
      headers: new HttpHeaders({ 'Content-Type': 'application/json'}),
      observe: 'response'
    })
      .pipe(
        map(response => {
          console.log(response);
          return SignUpStatus.success;
        }),
        catchError((err: any) => this.signUpErrorHandle(err))
      );
  }

  // TODO signUpErrors
  private signUpErrorHandle(err: any): Observable<SignUpStatus> {
    console.log(err);
    console.log(err.error.status);
    switch (err.error.status) {
      case 403: {return Observable.of(SignUpStatus.error); }
      //case 420
      default: return Observable.of(SignUpStatus.error);
    }
  }
}
