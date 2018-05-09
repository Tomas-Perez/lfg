import {Component, OnDestroy, OnInit} from '@angular/core';
import 'simplebar';
import {User} from '../_models/User';
import {UserService} from '../_services/user.service';
import {AuthService} from '../_services/auth.service';
import {Router, ActivatedRoute} from '@angular/router';
import 'rxjs/add/operator/takeUntil';
import {Subject} from 'rxjs/Subject';

@Component({
  selector: 'app-lfg-app',
  templateUrl: './lfg-app.component.html',
  styleUrls: ['./lfg-app.component.css']
})
export class LfgAppComponent implements OnInit, OnDestroy {

  private ngUnsubscribe: Subject<any> = new Subject();
  user: User;

  constructor(private userService: UserService,
              private authService: AuthService,
              private router: Router,
              private route: ActivatedRoute
  ) { }

  ngOnInit() {
    this.userService.userSubject.takeUntil(this.ngUnsubscribe)
      .subscribe(user => {
        this.user = user;
      });
  }

  logOut() {
    this.authService.logout();
    this.router.navigate(['/home']);
  }

  navigate(url: string) {
    this.router.navigate([{ outlets: {spekbar: [url] }}],
      {
        relativeTo: this.route,
        skipLocationChange: true
    });
    // [routerLink]="[{ outlets: {'spekbar':['new-post'] }}]"
  }

  ngOnDestroy() {
    this.ngUnsubscribe.next();
    this.ngUnsubscribe.complete();
  }

}
