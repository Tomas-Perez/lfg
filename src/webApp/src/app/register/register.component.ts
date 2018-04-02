import { Component, OnInit } from '@angular/core';
import {UserService} from '../user.service';
import {RegisterInfo} from '../RegisterInfo';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css']
})
export class RegisterComponent implements OnInit {

  registerInfo: RegisterInfo;

  constructor(private userService: UserService) { }

  ngOnInit() {
    this.registerInfo = new RegisterInfo;
  }

  registerUser(): void {
    this.userService.register(this.registerInfo)
      .subscribe(response => {
      if (response.status === 'ERROR') {
        // this.notifyError();
      } else if (response.status === 'SUCCESS') {
        // this.logIn(response.data);
      }
    });
  }



}
