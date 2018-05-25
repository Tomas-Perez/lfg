import {Component, OnDestroy, OnInit} from '@angular/core';
import {Subject} from 'rxjs/Subject';
import {UserService} from '../../_services/user.service';
import {User} from '../../_models/User';

@Component({
  selector: 'app-me-info',
  templateUrl: './me-info.component.html',
  styleUrls: ['./me-info.component.css']
})
export class MeInfoComponent implements OnInit, OnDestroy {

  private ngUnsubscribe: Subject<any> = new Subject();
  user: User;

  constructor(private userService: UserService) { }

  ngOnInit() {
    this.userService.userSubject.takeUntil(this.ngUnsubscribe)
      .subscribe(user => {
        this.user = user;
      });
  }

  ngOnDestroy() {
    this.ngUnsubscribe.next();
    this.ngUnsubscribe.complete();
  }


}
