import { Component, OnInit } from '@angular/core';
import {LogInInfo} from '../LogInInfo';
import {UserService} from '../user.service';
import {User} from '../User';

@Component({
  selector: 'app-log-in',
  templateUrl: './log-in.component.html',
  styleUrls: ['./log-in.component.css']
})
export class LogInComponent implements OnInit {

  loginInfo: LogInInfo;
  wrong: boolean;

  constructor(private userService: UserService) { }

  ngOnInit() {
    this.loginInfo = new LogInInfo();
    this.wrong = false;
  }

  verifyCredentials(): void {
    this.userService.logIn(this.loginInfo)
      .subscribe(response => {
        if (response.status === 'ERROR') {
          this.notifyError();
        } else if (response.status === 'SUCCESS') {
          this.logIn(response.data);
        }
      });
  }

  // TODO actually log in
  logIn(user: User): void {
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
