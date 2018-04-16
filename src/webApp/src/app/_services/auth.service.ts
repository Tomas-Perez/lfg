import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {SignInInfo} from '../_models/json/SignInInfo';
import {Observable} from 'rxjs/Observable';
import {catchError, map, tap} from 'rxjs/operators';
import 'rxjs/add/observable/of';

const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json'})
};

@Injectable()
export class AuthService {

  private signInUrl = 'http://localhost:8080/sign-in';
  private userMeUrl = 'http://localhost:8080/users/me';

  constructor(private http: HttpClient) { }

  /**
   * Authenticates user, saves token to localStorage.
   * @param {SignInInfo} signInInfo
   * @returns {Observable<boolean>}
   */
  signIn (signInInfo: SignInInfo): Observable<boolean> {
    return this.http.post<any>(this.signInUrl, {email: signInInfo.email, password: signInInfo.password}, {
      headers: new HttpHeaders({ 'Content-Type': 'application/json'}),
      observe: 'response'
    })
      .pipe(
        map(response => {
          const token = response.body.token;
          localStorage.setItem('token', token);
          return true;
        }),
        catchError(err => Observable.of(false))
      );
  }

  /**
   * Retrieves the user with the current local storage token and saves it to local storage.
    */
  getCurrentUserInfo(): Observable<boolean> {
    return this.http.get<any>(this.userMeUrl,
      {
        headers: new HttpHeaders({
          'Content-Type': 'application/json',
          'Authorization': 'Bearer ' + localStorage.getItem('token')
        }),
        observe: 'response'
      })
      .pipe(
        map(response => {
          console.log(response);
          localStorage.setItem('user', JSON.stringify(response.body));
          return true;
        }),
        catchError(err => Observable.of(false))
      );
  }

}
