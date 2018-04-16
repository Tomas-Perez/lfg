import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders, HttpResponse} from '@angular/common/http';
import {SignInInfo} from './models/json/SignInInfo';
import {SignUpInfo} from './models/json/SignUpInfo';
import {ApiResponse} from './models/ApiResponse';
import {Observable} from 'rxjs/Observable';
import {catchError, map, retry, tap} from 'rxjs/operators';
import 'rxjs/add/observable/of';

const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json'})
};

@Injectable()
export class AuthService {

  private signInUrl = 'http://localhost:8080/sign-in';
  private signUpUrl = 'http://localhost:8080/sign-up';
  private userMeUrl = 'http://localhost:8080/Users/me';

  constructor(private http: HttpClient) { }

  /**
   * Authenticates user, saves token and user to localStorage.
   * @param {SignInInfo} signInInfo
   * @returns {Observable<boolean>}
   */
  signIn (signInInfo: SignInInfo): Observable<boolean> {
    return this.http.post<any>(this.signInUrl, {email: signInInfo.email, password: signInInfo.password}, {observe: 'response'})
      .pipe(
        map(response => {
          const token = response.body.token;
          localStorage.setItem('token', token);
          return this.getCurrentUserInfo().subscribe(
            resp => {
              if (resp) {
                console.log('user retrieved and saved');
                return true;
              }
              return false;
            }
          ).closed;
        }),
        catchError(err => Observable.of(false))
    );
  }

  /**
   * Retrieves the user with the current token in local storage and saves it to local storage.
    */
  private getCurrentUserInfo(): Observable<boolean> {
    return this.http.post<any>(this.userMeUrl,
      localStorage.getItem('token'),
      {
        headers: new HttpHeaders({ 'Content-Type': 'application/json'}),
        observe: 'response'})
      .pipe(
        retry(3),
        map(response => {
          console.log(response);
          localStorage.setItem('user', JSON.stringify(response.body.data));
          return true;
        }),
        catchError(err => Observable.of(false))
      );
  }

  signUp(signUpInfo: SignUpInfo): Observable<ApiResponse<number>> {
    return this.http.post<ApiResponse<number>>(this.signUpUrl, signUpInfo)
      .pipe(
        tap(response => console.log(response))
      );
  }

}
