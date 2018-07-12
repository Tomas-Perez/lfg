import {Component, OnDestroy, OnInit} from '@angular/core';
import {Subject} from 'rxjs/Subject';
import {UserService} from '../../_services/user.service';
import {User} from '../../_models/User';
import {ActivatedRoute, Router} from '@angular/router';

@Component({
  selector: 'app-me-info',
  templateUrl: './me-info.component.html',
  styleUrls: ['./me-info.component.css']
})
export class MeInfoComponent implements OnInit, OnDestroy {

  private ngUnsubscribe: Subject<any> = new Subject();
  user: User;

  constructor(private userService: UserService, private router: Router, private route: ActivatedRoute) { }

  ngOnInit() {
    this.userService.userSubject.takeUntil(this.ngUnsubscribe)
      .subscribe(user => {
        this.user = user;
      });
  }

  navigate(url: string) {
    this.router.navigate([{ outlets: {spekbar: [url] }}],
      {
        relativeTo: this.route,
        skipLocationChange: true
      });
  }

  ngOnDestroy() {
    this.ngUnsubscribe.next();
    this.ngUnsubscribe.complete();
  }


}
