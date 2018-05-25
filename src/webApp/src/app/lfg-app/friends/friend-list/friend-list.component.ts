import {Component, OnDestroy, OnInit} from '@angular/core';
import {FriendBarService} from '../../_services/friend-bar.service';
import {FriendLocation} from '../../_models/FriendLocation';
import {Subject} from 'rxjs/Subject';
import 'rxjs/add/operator/takeUntil';
import {BasicUser} from '../../../_models/BasicUser';
import {FriendService} from '../../../_services/friend.service';
import {Router} from '@angular/router';

@Component({
  selector: 'app-friend-list',
  templateUrl: './friend-list.component.html',
  styleUrls: ['./friend-list.component.css']
})
export class FriendListComponent implements OnInit, OnDestroy {

  private ngUnsubscribe: Subject<any> = new Subject();
  private friends: BasicUser[];

  constructor(private friendBarService: FriendBarService,
              private friendService: FriendService,
              private router: Router
              ) { }

  ngOnInit() {
    this.friendBarService.friendLocationSubject.next(FriendLocation.LIST);
    this.friendService.friendListSubject.takeUntil(this.ngUnsubscribe)
      .subscribe(friends => this.friends = friends);
  }

  getUserInfo(id: number) {
    this.router.navigate(['/app', { outlets: {friends: ['user-info', id] }}], {
      skipLocationChange: true
    });
  }

  ngOnDestroy() {
    this.ngUnsubscribe.next();
    this.ngUnsubscribe.complete();
  }

}
