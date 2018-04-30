import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {Observable} from 'rxjs/Observable';
import {catchError, map} from 'rxjs/operators';
import {SignUpStatus} from '../_models/SignUpStatus';
import {User} from '../_models/User';
import {AuthService} from './auth.service';
import {JsonConvert} from 'json2typescript';

@Injectable()
export class UserService {

  private signUpUrl = 'http://localhost:8080/lfg/sign-up';

  private jsonConvert: JsonConvert = new JsonConvert();

  constructor(private http: HttpClient, private authService: AuthService) {
  }

  getCurrentUser(): Observable<User> {
    const userS = localStorage.getItem('user');
    if (userS != null) {
      try {
        return Observable.of(this.jsonConvert.deserialize(JSON.parse(userS), User));
      } catch (e) {
        return this.authService.getCurrentUserInfo();
      }
    } else {
      return this.authService.getCurrentUserInfo();
    }
  }

  signUp(signUpInfo: User): Observable<SignUpStatus> {
    return this.http.post<any>(this.signUpUrl, this.jsonConvert.serialize(signUpInfo), {
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
    console.log(err.status);
    switch (err.error.status) {
      case 403: {return Observable.of(SignUpStatus.error); }
      //case 420
      default: return Observable.of(SignUpStatus.error);
    }
  }
}
