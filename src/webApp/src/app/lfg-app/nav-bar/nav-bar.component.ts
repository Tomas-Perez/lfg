import {Component, OnDestroy, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {AuthService} from '../../_services/auth.service';
import {UserService} from '../../_services/user.service';
import {User} from '../../_models/User';
import {Subject} from 'rxjs/Subject';
import 'rxjs/add/operator/takeUntil';
import {GroupService} from '../../_services/group.service';
import {NavBarService} from '../_services/nav-bar.service';
import {SpekbarLocation} from '../_models/SpekbarLocation';

@Component({
  selector: 'app-nav-bar',
  templateUrl: './nav-bar.component.html',
  styleUrls: ['./nav-bar.component.css']
})
export class NavBarComponent implements OnInit, OnDestroy {

  public SpekbarLocation = SpekbarLocation;
  private ngUnsubscribe: Subject<any> = new Subject();
  user: User;
  inGroup: boolean;
  spekbarLocation: SpekbarLocation;

  constructor(
    private userService: UserService,
    private authService: AuthService,
    private groupService: GroupService,
    private navBarService: NavBarService,
    private router: Router,
    private route: ActivatedRoute
              ) { }

  ngOnInit() {
    this.inGroup = false;

    this.navBarService.spekbarLocationSubject.takeUntil(this.ngUnsubscribe)
      .subscribe(spekLoc => {
        this.spekbarLocation = spekLoc;
      });

    this.groupService.currentGroupSubject.takeUntil(this.ngUnsubscribe)
      .subscribe(group => {
        if (group !== null) {
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
    this.router.navigate(['/']).then(
      e => {
        this.authService.logout();
      }
    );
  }

  navigate(url: string) {
    this.router.navigate([{ outlets: {spekbar: [url] }}],
      {
        relativeTo: this.route,
        skipLocationChange: true
    });
    // [routerLink]="[{ outlets: {'spekbar':['new-post'] }}]"
  }

  resetSpekbarLocation() {
    this.navBarService.spekbarLocationSubject.next(SpekbarLocation.NOTHING);
  }

  ngOnDestroy() {
    this.ngUnsubscribe.next();
    this.ngUnsubscribe.complete();
  }

}
