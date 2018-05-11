import { Component, OnInit } from '@angular/core';
import {AuthService} from '../../_services/auth.service';
import {User} from '../../_models/User';
import {Router} from '@angular/router';

@Component({
  selector: 'app-sign-in',
  templateUrl: './sign-in.component.html',
  styleUrls: ['./sign-in.component.css', '../home.component.css']
})

export class SignInComponent implements OnInit {

  emailInput: string;
  passwordInput: string;
  wrongInfo: boolean;
  formValid: boolean;
  emailValid: boolean;
  passwordValid: boolean;

  constructor(private authService: AuthService, private router: Router) { }

  ngOnInit() {
    this.formValid = false;
    this.emailValid = true;
    this.passwordValid = true;
  }

  /**
   * Calls authService authenticate() to get token.
   */
  verifyCredentials(): void {
    this.authService.authenticate(this.emailInput, this.passwordInput)
      .subscribe(
        correct => {
        if (correct) {
          console.log('Credentials correct');
          this.router.navigate(['/app']);
        } else {
          console.log('Credentials with errors');
          this.notifyError();
          return;
        }
      });
  }

  signIn(user: User): void {
    console.log(
      'LOGGED IN\n' +
      'Username: ' + user.username + '\n' +
      'Password: ' + user.password + '\n' +
      'Email: ' + user.email + '\n' +
      'UserId: ' + user.id + '\n' +
      'isAdmin: ' + user.admin);
  }

  notifyError(): void {
    this.wrongInfo = true;
  }

  checkErrors(email, password): void {
    this.emailValid = email;
    this.passwordValid = password;
  }

}
