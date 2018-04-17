import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {Observable} from 'rxjs/Observable';
import {catchError, map, tap} from 'rxjs/operators';
import 'rxjs/add/observable/of';
import 'rxjs/add/observable/empty';

const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json'})
};

@Injectable()
export class AuthService {

  private authUrl = 'http://localhost:8080/sign-in';
  private userMeUrl = 'http://localhost:8080/users/me';

  constructor(private http: HttpClient) { }


  getAuthUrl(): string {
    return this.authUrl;
  }

  getAccessToken(): string {
    return localStorage.getItem('token');
  }

  /**
   * Authenticates user, saves token to localStorage.
   * @returns {Observable<boolean>}
   */
  authenticate(email: string, password: string): Observable<boolean> {
    return this.http.post<any>(this.authUrl, {email: email, password: password}, {
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

  refreshToken(): Observable<any> {
    return Observable.empty();
    // TODO
  }

  /**
   * Retrieves the user with the current local storage token and saves it to local storage.
    */
  getCurrentUserInfo(): Observable<boolean> {
    return this.http.get<any>(this.userMeUrl,
      {
        headers: new HttpHeaders({
          'Content-Type': 'application/json',
          'Authorization': 'Bearer ' + this.getAccessToken()
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

  // TODO
  logout() {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
  }

}
