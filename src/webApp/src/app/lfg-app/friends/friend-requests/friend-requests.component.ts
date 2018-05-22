import {Component, OnDestroy, OnInit} from '@angular/core';
import {FriendBarService} from '../../_services/friend-bar.service';
import {FriendLocation} from '../../_models/FriendLocation';
import {BasicUser} from '../../../_models/BasicUser';
import {FriendService} from '../../../_services/friend.service';
import 'rxjs/add/operator/takeUntil';
import {Subject} from 'rxjs/Subject';
import {ActivatedRoute, Router} from '@angular/router';

@Component({
  selector: 'app-friend-requests',
  templateUrl: './friend-requests.component.html',
  styleUrls: ['./friend-requests.component.css']
})
export class FriendRequestsComponent implements OnInit, OnDestroy {

  private ngUnsubscribe: Subject<any> = new Subject();
  receivedRequests: BasicUser[];
  sentRequests: BasicUser[];

  constructor(private friendBarService: FriendBarService,
              private friendService: FriendService,
              private router: Router,
              private route: ActivatedRoute
              ) { }

  ngOnInit() {
    this.friendBarService.friendLocationSubject.next(FriendLocation.REQUEST);
    this.friendService.friendRequestsSubject.takeUntil(this.ngUnsubscribe)
      .subscribe(requests => this.receivedRequests = requests);
    this.friendService.sentRequestsSubject.takeUntil(this.ngUnsubscribe)
      .subscribe(requests => this.sentRequests = requests);
  }

  getUserInfo(id: number) {
    this.router.navigate([{outlets: {friends: ['user-info', id]}}],
      {
        relativeTo: this.route,
        skipLocationChange: true
      });
  }

  acceptRequest(id: number){ // TODO interface
    this.friendService.confirmFriendRequest(id).subscribe(response => {
      this.friendService.updateFriendRequests();
      this.friendService.updateFriends();
    })
  }

  deleteRequest(id: number){ // TODO interface
    this.friendService.removeFriend(id).subscribe(response => {
      this.friendService.updateFriendRequests();
    })
  }

  deleteSentRequest(id: number){ // TODO interface
    this.friendService.removeFriend(id).subscribe(response => {
      this.friendService.updateSentRequests();
    })
  }

  ngOnDestroy() {
    this.ngUnsubscribe.next();
    this.ngUnsubscribe.complete();
  }
}
