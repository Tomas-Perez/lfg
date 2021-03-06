import {Component, OnDestroy, OnInit} from '@angular/core';
import {FriendStateService} from '../../_services/friend-state.service';
import {FriendLocation} from '../../_models/FriendLocation';
import {BasicUser} from '../../../_models/BasicUser';
import {FriendService} from '../../../_services/friend.service';
import 'rxjs/add/operator/takeUntil';
import {Subject} from 'rxjs/Subject';
import {Router} from '@angular/router';

@Component({
  selector: 'app-friend-requests',
  templateUrl: './friend-requests.component.html',
  styleUrls: ['./friend-requests.component.css']
})
export class FriendRequestsComponent implements OnInit, OnDestroy {

  private ngUnsubscribe: Subject<any> = new Subject();
  receivedRequests: BasicUser[];
  sentRequests: BasicUser[];

  constructor(private friendBarService: FriendStateService,
              private friendService: FriendService,
              private router: Router
              ) { }

  ngOnInit() {
    this.friendBarService.friendLocationSubject.next(FriendLocation.REQUEST);
    this.friendService.receivedRequestsSubject.takeUntil(this.ngUnsubscribe)
      .subscribe(requests => this.receivedRequests = requests);
    this.friendService.sentRequestsSubject.takeUntil(this.ngUnsubscribe)
      .subscribe(requests => this.sentRequests = requests);
  }

  getUserInfo(id: number) {
    this.router.navigate(['/app', { outlets: {friends: ['user-info', id] }}], {
      skipLocationChange: true
    });
  }

  acceptRequest(id: number) {
    this.friendService.confirmFriendRequest(id).subscribe(response => {});
  }

  deleteRequest(id: number) {
    this.friendService.removeFriend(id).subscribe(response => {});
  }

  deleteSentRequest(id: number) {
    this.friendService.removeFriend(id).subscribe(response => {});
  }

  ngOnDestroy() {
    this.ngUnsubscribe.next();
    this.ngUnsubscribe.complete();
  }
}
