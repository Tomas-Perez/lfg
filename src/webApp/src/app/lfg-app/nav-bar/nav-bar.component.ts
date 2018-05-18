import {Component, OnDestroy, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {AuthService} from '../../_services/auth.service';
import {UserService} from '../../_services/user.service';
import {User} from '../../_models/User';
import {Subject} from 'rxjs/Subject';
import 'rxjs/add/operator/takeUntil';
import {GroupService} from '../../_services/group.service';

@Component({
  selector: 'app-nav-bar',
  templateUrl: './nav-bar.component.html',
  styleUrls: ['./nav-bar.component.css']
})
export class NavBarComponent implements OnInit, OnDestroy {

  private ngUnsubscribe: Subject<any> = new Subject();
  user: User;
  inGroup: boolean;

  constructor(
    private userService: UserService,
    private authService: AuthService,
    private groupService: GroupService,
    private router: Router,
    private route: ActivatedRoute
              ) { }

  ngOnInit() {
    this.inGroup = false;

    this.groupService.currentGroupSubject.takeUntil(this.ngUnsubscribe)
      .subscribe(group => {
        if (group !== null){
          this.inGroup = true;
        } else {
          this.inGroup = false;
        }
      });

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
        //skipLocationChange: true
    });
    // [routerLink]="[{ outlets: {'spekbar':['new-post'] }}]"
  }

  ngOnDestroy() {
    this.ngUnsubscribe.next();
    this.ngUnsubscribe.complete();
  }

}
