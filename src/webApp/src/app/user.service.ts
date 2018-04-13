import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import {SignInInfo} from './SignInInfo';
import {SignUpInfo} from './SignUpInfo';
import {User} from './User';
import {ApiResponse} from './ApiResponse';
import {Observable} from 'rxjs/Observable';
import {map, tap} from 'rxjs/operators';

const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json'})
};

@Injectable()
export class UserService {

  private signInUrl = 'http://localhost:8080/sign-in';
  private signUpUrl = 'http://localhost:8080/sign-up';

  constructor(private http: HttpClient) { }

  signIn (signInInfo: SignInInfo): Observable<ApiResponse<User>> {
    return this.http.post<ApiResponse<User>>(this.signInUrl, signInInfo, httpOptions)
      .pipe(
        tap(response => {
          console.log(response);
        })
        //map((response: ApiResponse) => <User>response.data)
      );
  }

  signUp(signUpInfo: SignUpInfo): Observable<ApiResponse<number>> {
    return this.http.post<ApiResponse<number>>(this.signUpUrl, signUpInfo)
      .pipe(
        tap(response => console.log(response))
      );
  }
}
