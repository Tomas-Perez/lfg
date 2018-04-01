import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import {LogInInfo} from './logInInfo';

@Injectable()
export class UserService {

  private loginUrl = 'http://localhost:8080/login';

  constructor(private http: HttpClient) { }

  logIn (logInInfo: LogInInfo): void {
    this.http.post<Object>(this.loginUrl, logInInfo)
      .subscribe(object => console.log(object));
  }
}
