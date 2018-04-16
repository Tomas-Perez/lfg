import { Component, OnInit } from '@angular/core';
import {SignUpInfo} from '../../models/json/SignUpInfo';
import {AuthService} from '../../auth.service';

@Component({
  selector: 'app-sign-up',
  templateUrl: './sign-up.component.html',
  styleUrls: ['./sign-up.component.css', '../home.component.css']
})
export class SignUpComponent implements OnInit {

  signUpInfo: SignUpInfo;
  formValid: boolean;
  usernameValid: boolean;
  emailValid: boolean;
  passwordValid: boolean;

  constructor(private authService: AuthService) { }

  ngOnInit() {
    this.signUpInfo = new SignUpInfo;
    this.formValid = false;
    this.usernameValid = true;
    this.emailValid = true;
    this.passwordValid = true;
  }

  signUpUser(): void {
    this.authService.signUp(this.signUpInfo)
      .subscribe(response => {
        if (response.status === 'ERROR') {
          this.notifyError();
        } else if (response.status === 'SUCCESS') {
          // this.logIn(response.data);
        }
      });
  }

  notifyError(): void {
    this.formValid = true;
  }

  checkErrors(username, email, password): void {
    this.usernameValid = username;
    this.emailValid = email;
    this.passwordValid = password;
  }

}
