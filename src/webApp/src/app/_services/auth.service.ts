import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {Observable} from 'rxjs/Observable';
import {catchError, map} from 'rxjs/operators';
import 'rxjs/add/observable/of';
import 'rxjs/add/observable/empty';
import 'rxjs/add/operator/share';
import {BehaviorSubject} from 'rxjs/BehaviorSubject';


@Injectable()
export class AuthService {

  private authUrl = 'http://localhost:8080/lfg/sign-in';
  private userMeUrl = 'http://localhost:8080/lfg/users/me';

  private loggedIn = false;
  loggedIn$ = new BehaviorSubject<boolean>(this.loggedIn);

  constructor(private http: HttpClient) {
    if (this.tokenValid()) {
      this.setLoggedIn(true);
    }
  }

  tokenValid() {
    return this.getAccessToken() != null; // TODO check expiration time
  }

  setLoggedIn(value: boolean) {
    this.loggedIn$.next(value);
    this.loggedIn = value;
  }

  getAuthUrl(): string {
    return this.authUrl;
  }

  getAccessToken(): string {
    return localStorage.getItem('token');
  }

  isLoggedIn(): Observable<boolean> {
    return this.loggedIn$.asObservable().share();
  }

  /**
   * Authenticates user, saves token to localStorage.
   * @returns {Observable<boolean>}
   */
  authenticate(email: string, password: string): Observable<boolean> {
    return this.http.post<any>(this.authUrl, {email: email, password: password}, {
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

  // TODO
  refreshToken(): Observable<any> {
    return Observable.empty();
  }

  /**
   * Retrieves the user with the current local storage token and saves it to local storage.
    */
  getCurrentUserInfo(): Observable<boolean> {
    return this.http.get<any>(this.userMeUrl,
      {
        headers: new HttpHeaders({
          'Authorization': 'Bearer ' + this.getAccessToken()
        }),
        observe: 'response'
      })
      .pipe(
        map(response => {
          console.log(response);
          localStorage.setItem('user', JSON.stringify(response.body));
          this.setLoggedIn(true);
          return true;
        }),
        catchError(err => this.handleUserInfoError())
      );
  }

  handleUserInfoError(): Observable<boolean> {
    console.log('User info retrieval error');
    this.logout();
    return Observable.of(false);
  }

  logout() {
    console.log('logging out');
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    this.setLoggedIn(false);
  }

}
