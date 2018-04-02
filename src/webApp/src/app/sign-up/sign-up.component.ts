import { Component, OnInit } from '@angular/core';
import {UserService} from '../user.service';
import {SignUpInfo} from '../SignUpInfo';

@Component({
  selector: 'app-sign-up',
  templateUrl: './sign-up.component.html',
  styleUrls: ['./sign-up.component.css']
})
export class SignUpComponent implements OnInit {

  signUpInfo: SignUpInfo;

  constructor(private userService: UserService) { }

  ngOnInit() {
    this.signUpInfo = new SignUpInfo;
  }

  signUpUser(): void {
    this.userService.signUp(this.signUpInfo)
      .subscribe(response => {
        if (response.status === 'ERROR') {
          // this.notifyError();
        } else if (response.status === 'SUCCESS') {
          // this.logIn(response.data);
        }
      });
  }

}
