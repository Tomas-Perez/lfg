import {Component, OnInit} from '@angular/core';
import {UserService} from '../../_services/user.service';
import {SignUpStatus} from '../../_models/SignUpStatus';
import {User} from '../../_models/User';

@Component({
  selector: 'app-sign-up',
  templateUrl: './sign-up.component.html',
  styleUrls: ['./sign-up.component.css', '../home.component.css']
})
export class SignUpComponent implements OnInit {
  signUpInfo: User;
  formValid: boolean;
  usernameValid: boolean;
  usernameTaken: boolean;
  emailValid: boolean;
  emailTaken: boolean;
  passwordValid: boolean;


  constructor(private userService: UserService) { }

  ngOnInit() {
    this.signUpInfo = new User;
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

  // TODO
  signUpUser(): void {
    this.userService.signUp(this.signUpInfo)
      .subscribe(response => {
        switch (response) {
          case SignUpStatus.success: {
            // TODO sign in, route user
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
