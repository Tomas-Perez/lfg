import {Component, OnInit, ViewChild} from '@angular/core';
import {SignUpInfo} from '../../_models/json/SignUpInfo';
import {UserService} from '../../_services/user.service';
import {SignUpStatus} from '../../_models/SignUpStatus';
import {FormGroup} from '@angular/forms';

@Component({
  selector: 'app-sign-up',
  templateUrl: './sign-up.component.html',
  styleUrls: ['./sign-up.component.css', '../home.component.css']
})
export class SignUpComponent implements OnInit {

  signUpInfo: SignUpInfo;
  formValid: boolean;
  usernameValid: boolean;
  usernameTaken: boolean;
  emailValid: boolean;
  emailTaken: boolean;
  passwordValid: boolean;
  @ViewChild('signUpForm') form: FormGroup;


  constructor(private userService: UserService) { }

  ngOnInit() {
    this.signUpInfo = new SignUpInfo;
    this.formValid = false;
    this.usernameValid = true;
    this.usernameTaken = false;
    this.emailValid = true;
    this.passwordValid = true;
  }

  sendForm() {
    this.resetTakenVariables();
    this.signUpUser();
  }

  resetTakenVariables() {
    this.usernameTaken = false;
    this.emailTaken = false;
  }

  signUpUser(): void {
    this.userService.signUp(this.signUpInfo)
      .subscribe(response => {
        switch (response) {
          case SignUpStatus.success: {

            //TODO sign in, route user
            console.log('succcccccc');
            break;
          }
          case SignUpStatus.emailTaken: {
            console.log('email taken');
            break;
          }
          case SignUpStatus.userTaken: {
            this.usernameTaken = true;
            console.log('user taken');
            break;
          }
          case SignUpStatus.error: {
            console.log('error occured');
            break;
          }
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
