import {Component, OnDestroy, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {FriendLocation} from '../../_models/FriendLocation';
import {FriendBarService} from '../../_services/friend-bar.service';
import {Subject} from 'rxjs/Subject';

@Component({
  selector: 'app-friend-bar',
  templateUrl: './friend-bar.component.html',
  styleUrls: ['./friend-bar.component.css']
})
export class FriendBarComponent implements OnInit, OnDestroy {

  public FriendLocation = FriendLocation;
  private ngUnsubscribe: Subject<any> = new Subject();
  friendLocation: FriendLocation;

  constructor(
    private router: Router,
    private friendBarService: FriendBarService,
    private route: ActivatedRoute
  ) { }

  ngOnInit() {
    this.friendBarService.friendLocationSubject.takeUntil(this.ngUnsubscribe)
      .subscribe(friendLocation => this.friendLocation = friendLocation);

  }

  navigate(url: string) {
    this.router.navigate([{outlets: {friends: [url]}}],
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
