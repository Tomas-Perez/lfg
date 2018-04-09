import { Component, OnInit } from '@angular/core';
import {SignInInfo} from '../../SignInInfo';
import {UserService} from '../../user.service';
import {User} from '../../User';

@Component({
  selector: 'app-sign-in',
  templateUrl: './sign-in.component.html',
  styleUrls: ['./sign-in.component.css']
})

export class SignInComponent implements OnInit {

  signInInfo: SignInInfo;
  wrong: boolean;

  constructor(private userService: UserService) { }

  ngOnInit() {
    this.signInInfo = new SignInInfo();
    this.wrong = false;
  }

  verifyCredentials(): void {
    this.userService.signIn(this.signInInfo)
      .subscribe(response => {
        if (response.status === 'ERROR') {
          this.notifyError();
        } else if (response.status === 'SUCCESS') {
          this.signIn(response.data);
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
    this.wrong = true;
  }

}
