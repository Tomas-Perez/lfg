import {Component, OnDestroy, OnInit} from '@angular/core';
import {AuthService} from '../../_services/auth.service';
import {Subject} from 'rxjs/Subject';
import 'rxjs/add/operator/takeUntil';

@Component({
  selector: 'app-home-navbar',
  templateUrl: './home-navbar.component.html',
  styleUrls: ['./home-navbar.component.css']
})
export class HomeNavbarComponent implements OnInit, OnDestroy {

  navbarCollapsed = true;
  isLoggedIn: boolean;
  private ngUnsubscribe: Subject<any> = new Subject();

  constructor(public authService: AuthService) {
    this.authService.isLoggedInBS().takeUntil(this.ngUnsubscribe).subscribe(isLoggedIn => this.isLoggedIn = isLoggedIn);
  }

  ngOnInit() {

  }

  logOut() {
    this.authService.logout();
  }

  ngOnDestroy() {
    this.ngUnsubscribe.next();
    this.ngUnsubscribe.complete();
  }
}
