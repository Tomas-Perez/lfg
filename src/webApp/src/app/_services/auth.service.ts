import { Injectable } from '@angular/core';
import {Observable} from 'rxjs/Observable';
import {catchError, map} from 'rxjs/operators';
import 'rxjs/add/observable/of';
import 'rxjs/add/observable/empty';
import 'rxjs/add/operator/share';
import {BehaviorSubject} from 'rxjs/BehaviorSubject';
import {ActivatedRoute, Router} from '@angular/router';
import {HttpService} from './http.service';


@Injectable()
export class AuthService {

  static authUrl = '/sign-in';
  private userMeUrl = '/users/me';


  private loggedIn = false;
  private loggedIn$ = new BehaviorSubject<boolean>(this.loggedIn);

  constructor(private http: HttpService,
              private router: Router,
              private route: ActivatedRoute
              ) {

    if (this.tokenValid()) {
      this.setLoggedIn(true);
    } else {
      this.logout();
    }
  }

  tokenValid() {
    return this.getAccessToken() != null; // TODO check expiration time
  }

  setLoggedIn(value: boolean) {
    this.loggedIn$.next(value);
    this.loggedIn = value;
  }

  getAccessToken(): string {
    return localStorage.getItem('token');
  }

  isLoggedIn(): boolean {
    return this.loggedIn;
  }

  isLoggedInBS(): BehaviorSubject<boolean> {
    return this.loggedIn$;
  }

  /**
   * Authenticates user, saves token to localStorage.
   * @returns {Observable<boolean>}
   */
  authenticate(email: string, password: string): Observable<boolean> {
    return this.http.post(AuthService.authUrl, {email: email, password: password}, {
      observe: 'response'
    })
      .pipe(
        map(response => {
          const token = response.body.token;
          localStorage.setItem('token', token);
          this.setLoggedIn(true);
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
   * Retrieves current user info and returns its admin value
   * @returns {Observable<boolean>}
   */
  authAdmin(): Observable<boolean> {
    return this.http.get(this.userMeUrl,
      {
        observe: 'response'
      })
      .pipe(
        map(response => {
          return response.body.admin;
        }),
        catchError(err => this.handleAdminAuthError())
      );
  }

  private handleAdminAuthError() {
    this.logout();
    return Observable.of(false);
  }

  logout() {
    console.log('logged out');
    localStorage.removeItem('token');
    this.setLoggedIn(false);
    this.router.navigate(['']); // TODO change this ?
  }

}
