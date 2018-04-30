import { Component, OnInit } from '@angular/core';
import 'SimpleBar';
import {User} from '../_models/User';
import {UserService} from '../_services/user.service';

@Component({
  selector: 'app-lfg-app',
  templateUrl: './lfg-app.component.html',
  styleUrls: ['./lfg-app.component.css']
})
export class LfgAppComponent implements OnInit {

  user: User;

  constructor(private userService: UserService) { }

  ngOnInit() {
    this.userService.getCurrentUser().subscribe(
      user => {
        this.user = user;
      });
  }

}
