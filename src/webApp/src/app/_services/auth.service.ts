import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs/Observable';
import {catchError, map} from 'rxjs/operators';
import 'rxjs/add/observable/of';
import 'rxjs/add/observable/empty';
import 'rxjs/add/operator/share';
import {BehaviorSubject} from 'rxjs/BehaviorSubject';
import {ActivatedRoute, Router} from '@angular/router';


@Injectable()
export class AuthService {

  static authUrl = 'http://localhost:8080/lfg/sign-in';
  private userMeUrl = 'http://localhost:8080/lfg/users/me';


  private loggedIn = false;
  private loggedIn$ = new BehaviorSubject<boolean>(this.loggedIn);

  constructor(private http: HttpClient,
              private router: Router,
              private route: ActivatedRoute
              ) {

    /*
       const lul = this.jsonConvert.deserialize({
           'posts': [
             {
               'id': 123,
               'activity': {
                 'id': 111,
                 'name': 'activityyy',
                 'game': {
                   'id': 222,
                   'name': 'gameee'
                 }
               },
               'owner': {
                 'id': 11234123,
                 'name': 'ownerName',
               },
               'date': '1995-12-17T03:24:00',
               'description': 'descriptionnnn'
             },
             {
               'id': 123,
               'activity': {
                 'id': 111,
                 'name': 'activityyy',
                 'game': {
                   'id': 222,
                   'name': 'gameee'
                 }
               },
               "owner": {
                 "id": 11234123,
                 "name": "ownerName",
               },
               'date': '1995-12-17T03:24:00',
               'description': 'descriptionnnn'
             }
           ]}.posts,
         Post);
       console.log(lul);
    */

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
    return this.http.post<any>(AuthService.authUrl, {email: email, password: password}, {
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
    return this.http.get<any>(this.userMeUrl,
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
