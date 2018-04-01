import { Component, OnInit } from '@angular/core';
import {LogInInfo} from '../logInInfo';
import {UserService} from '../user.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {

  loginInfo: LogInInfo;

  constructor(private userService: UserService) { }

  ngOnInit() {
  }

  log(): void {
    console.log('Email: ' + this.loginInfo.email);
    console.log('Password: ' + this.loginInfo.password);
    this.userService.logIn(this.loginInfo);
  }

}
