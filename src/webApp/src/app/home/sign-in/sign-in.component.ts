import { Component, OnInit } from '@angular/core';
import {SignInInfo} from '../../_models/json/SignInInfo';
import {AuthService} from '../../_services/auth.service';
import {User} from '../../_models/User';

@Component({
  selector: 'app-sign-in',
  templateUrl: './sign-in.component.html',
  styleUrls: ['./sign-in.component.css', '../home.component.css']
})

export class SignInComponent implements OnInit {

  signInInfo: SignInInfo;
  wrongInfo: boolean;
  formValid: boolean;
  emailValid: boolean;
  passwordValid: boolean;

  constructor(private authService: AuthService) { }

  ngOnInit() {
    this.signInInfo = new SignInInfo;
    this.formValid = false;
    this.emailValid = true;
    this.passwordValid = true;
  }

  verifyCredentials(): void {
    this.authService.signIn(this.signInInfo)
      .subscribe(valid => {
        console.log(valid);
        if (valid) {
          console.log('Credentials correct');
        } else {
          console.log('Credentials with errors');
          this.notifyError();
          return;
        }
      });
    this.authService.getCurrentUserInfo()
      .subscribe(valid => {
        console.log(valid);
        if (valid) {
          //TODO reroute user
        } else {
          this.notifyError();
        }
      });

  }

  // TODO actually log in
  signIn(user: User): void {
    console.log(
      'LOGGED IN\n' +
      'Username: ' + user.username + '\n' +
      'Password: ' + user.password + '\n' +
      'Email: ' + user.email + '\n' +
      'UserId: ' + user.userId + '\n' +
      'isAdmin: ' + user.isadmin);
  }

  notifyError(): void {
    this.wrongInfo = true;
  }

  checkErrors(email, password): void {
    this.emailValid = email;
    this.passwordValid = password;
  }

}
