import {Component, OnDestroy, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {FriendLocation} from '../../_models/FriendLocation';
import {FriendBarService} from '../../_services/friend-bar.service';
import {Subject} from 'rxjs/Subject';
import {FriendService} from '../../../_services/friend.service';

@Component({
  selector: 'app-friend-bar',
  templateUrl: './friend-bar.component.html',
  styleUrls: ['./friend-bar.component.css']
})
export class FriendBarComponent implements OnInit, OnDestroy {

  public FriendLocation = FriendLocation;
  private ngUnsubscribe: Subject<any> = new Subject();
  private friendRequests: number;
  friendLocation: FriendLocation;

  constructor(
    private friendBarService: FriendBarService,
    private friendService: FriendService,
    private router: Router,
    private route: ActivatedRoute
  ) { }

  ngOnInit() {
    this.friendRequests = 0;
    this.friendBarService.friendLocationSubject.takeUntil(this.ngUnsubscribe)
      .subscribe(friendLocation => this.friendLocation = friendLocation);
    this.friendService.friendRequestsSubject.takeUntil(this.ngUnsubscribe)
      .subscribe(requests => this.friendRequests = requests.length);
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
