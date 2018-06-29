import {Component, OnDestroy, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {FriendLocation} from '../../_models/FriendLocation';
import {FriendStateService} from '../../_services/friend-state.service';
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
  private isOpen: Boolean;

  constructor(
    private friendStateService: FriendStateService,
    private friendService: FriendService,
    private router: Router,
    private route: ActivatedRoute
  ) { }

  ngOnInit() {
    this.friendRequests = 0;

    this.friendStateService.friendLocationSubject.takeUntil(this.ngUnsubscribe)
      .subscribe(friendLocation => this.friendLocation = friendLocation);

    this.friendStateService.openSubject.takeUntil(this.ngUnsubscribe)
      .subscribe(isOpen => this.isOpen = isOpen);

    this.friendService.receivedRequestsSubject.takeUntil(this.ngUnsubscribe)
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
