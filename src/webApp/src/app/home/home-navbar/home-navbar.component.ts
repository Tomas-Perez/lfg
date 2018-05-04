import { Component, OnInit } from '@angular/core';
import { RouterLinkActive } from '@angular/router';
import {AuthService} from '../../_services/auth.service';
import {Observable} from 'rxjs/Observable';

@Component({
  selector: 'app-home-navbar',
  templateUrl: './home-navbar.component.html',
  styleUrls: ['./home-navbar.component.css']
})
export class HomeNavbarComponent implements OnInit {

  navbarCollapsed = true;
  isLoggedIn: boolean;

  constructor(public authService: AuthService) {
    this.authService.isLoggedInBS().subscribe(isLoggedIn => this.isLoggedIn = isLoggedIn);
  }

  ngOnInit() {

  }

}
