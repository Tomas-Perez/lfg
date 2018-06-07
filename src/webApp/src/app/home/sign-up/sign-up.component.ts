import {Component, OnInit} from '@angular/core';
import {UserService} from '../../_services/user.service';
import {SignUpStatus} from '../../_models/SignUpStatus';
import {User} from '../../_models/User';
import {AuthService} from '../../_services/auth.service';
import {Router} from '@angular/router';

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
  wrongInfo: boolean;


  constructor(private userService: UserService,
              private authService: AuthService,
              private router: Router
              ) { }

  ngOnInit() {
    this.signUpInfo = new User;
    this.formValid = false;
    this.usernameValid = true;
    this.usernameTaken = false;
    this.emailValid = true;
    this.passwordValid = true;
    this.wrongInfo = false;
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
            console.log('Signed up');
            this.authService.authenticate(this.signUpInfo.email, this.signUpInfo.password)
              .subscribe(b => {
                if(b) {
                  this.router.navigate(['/app'])}
                }
              );
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
            this.notifyError();
            console.log('error occured');
            break;
          }
        }
      });
  }

  notifyError(): void {
    this.wrongInfo = true;
  }

  checkErrors(username, email, password): void {
    this.usernameValid = username;
    this.emailValid = email;
    this.passwordValid = password;
  }

}
