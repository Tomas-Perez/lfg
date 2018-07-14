import { Injectable } from '@angular/core';
import {Observable} from 'rxjs/Observable';
import {catchError, map} from 'rxjs/operators';
import {SignUpStatus} from '../_models/SignUpStatus';
import {User} from '../_models/User';
import {AuthService} from './auth.service';
import {JsonConvert} from 'json2typescript';
import {BehaviorSubject} from 'rxjs/BehaviorSubject';
import {HttpService} from './http.service';
import {BasicUser} from '../_models/BasicUser';

@Injectable()
export class UserService {

  private signUpUrl = '/sign-up';
  private userMeUrl = '/users/me';
  private usersUrl = '/users';

  private jsonConvert: JsonConvert = new JsonConvert();


  private user: User;
  userSubject: BehaviorSubject<User>;

  constructor(private http: HttpService, private authService: AuthService) {
    this.userSubject = new BehaviorSubject<User>(null);

    this.userSubject.subscribe(user => this.user = user);

    this.authService.isLoggedInBS().subscribe(loggedIn => {
      if (loggedIn) {
        this.updateUserInfo();
      } else {
        this.userSubject.next(null);
      }
    });

  }

  updateUserInfo(): void {
    this.requestUserInfo().subscribe( user => this.userSubject.next(user));
  }

  /**
   * Retrieves the user with the current local storage token and saves it to local storage.
    */
  requestUserInfo(): Observable<User> {
    return this.http.get(this.userMeUrl,
      {
        observe: 'response'
      })
      .pipe(
        map(response => {
          console.log(response);
          return this.jsonConvert.deserialize(response.body, User);
        }),
        catchError(err => this.handleUserInfoError())
      );
  }

  handleUserInfoError(): Observable<User> {
    console.log('User info retrieval error');
    this.authService.logout();
    return Observable.of(null);
  }

  signUp(signUpInfo: User): Observable<SignUpStatus> {
    return this.http.post(this.signUpUrl, this.jsonConvert.serialize(signUpInfo), {
      observe: 'response'
    })
      .pipe(
        map(response => {
          console.log(response);
          return <any> SignUpStatus.success;
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

      // case 420
      default: return Observable.of(SignUpStatus.error);
    }
  }

  searchUsers(search: string): Observable<BasicUser[]> {
    return this.http.get(this.usersUrl + '?search=' + search,
      {
        observe: 'response'
      })
      .pipe(
        map(response => {
          console.log(response);
          return this.jsonConvert.deserialize(response.body, BasicUser);
        }),
        catchError(err => this.searchUsersErrorHandle(err))
      );
  }

  private searchUsersErrorHandle(err: any): Observable<BasicUser[]> {
    console.log(err);
    return Observable.of([]);
  }
}
