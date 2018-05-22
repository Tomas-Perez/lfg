import {Component, OnDestroy, OnInit} from '@angular/core';
import {FriendBarService} from '../../_services/friend-bar.service';
import {FriendLocation} from '../../_models/FriendLocation';
import {Subject} from 'rxjs/Subject';
import 'rxjs/add/operator/takeUntil';
import {BasicUser} from '../../../_models/BasicUser';
import {FriendService} from '../../../_services/friend.service';
import {ActivatedRoute, Router} from '@angular/router';

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
              private router: Router,
              private route: ActivatedRoute
              ) { }

  ngOnInit() {
    this.friendBarService.friendLocationSubject.next(FriendLocation.LIST);
    this.friendService.friendListSubject.takeUntil(this.ngUnsubscribe)
      .subscribe(friends => this.friends = friends);
  }

  getUserInfo(id: number) {
    this.router.navigate([{outlets: {friends: ['user-info', id]}}],
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
