import {Component, OnDestroy, OnInit} from '@angular/core';
import {FriendStateService} from '../../_services/friend-state.service';
import {FriendLocation} from '../../_models/FriendLocation';
import {Subject} from 'rxjs/Subject';
import 'rxjs/add/operator/takeUntil';
import {BasicUser} from '../../../_models/BasicUser';
import {FriendService} from '../../../_services/friend.service';
import {Router} from '@angular/router';
import {OnlineStatus} from '../../../_models/OnlineStatus';
import {ChatService} from '../../../_services/chat.service';
import {ChatType} from '../../../_models/ChatType';

@Component({
  selector: 'app-friend-list',
  templateUrl: './friend-list.component.html',
  styleUrls: ['./friend-list.component.css']
})
export class FriendListComponent implements OnInit, OnDestroy {

  public OnlineStatus = OnlineStatus;
  private ngUnsubscribe: Subject<any> = new Subject();
  private friends: BasicUser[];

  constructor(private friendBarService: FriendStateService,
              private friendService: FriendService,
              private chatService: ChatService,
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

  newChat(id: number) {
    this.chatService.newChat(ChatType.PRIVATE, id);
  }

  ngOnDestroy() {
    this.ngUnsubscribe.next();
    this.ngUnsubscribe.complete();
  }

}
