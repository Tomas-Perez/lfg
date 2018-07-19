import {Component, OnDestroy, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {FriendService} from '../../../_services/friend.service';
import {BasicUser} from '../../../_models/BasicUser';
import {FriendStateService} from '../../_services/friend-state.service';
import {FriendLocation} from '../../_models/FriendLocation';
import {ChatType} from '../../../_models/ChatType';
import {ChatService} from '../../../_services/chat.service';
import {Subject} from 'rxjs/Subject';

@Component({
  selector: 'app-user-info',
  templateUrl: './user-info.component.html',
  styleUrls: ['./user-info.component.css']
})
export class UserInfoComponent implements OnInit, OnDestroy {

  private ngUnsubscribe: Subject<any> = new Subject();
  user: BasicUser;
  isAlreadyFriend: boolean;
  isRequestSent: boolean;
  isRequestReceived: boolean;
  isFriendsOpen: Boolean;

  constructor(private route: ActivatedRoute,
              private friendService: FriendService,
              private chatService: ChatService,
              private friendBarService: FriendStateService,
              private friendStateService: FriendStateService
  ) { }

  ngOnInit() {
    this.friendBarService.friendLocationSubject.next(FriendLocation.USERINFO);

    this.isAlreadyFriend = false;
    this.isRequestSent = false;
    this.isRequestReceived = false;

    this.user = null;

    this.friendStateService.openSubject.takeUntil(this.ngUnsubscribe)
      .subscribe(isOpen => this.isFriendsOpen = isOpen);

    this.route.params.subscribe(params => {
      const id = +params['id'];
      this.friendService.getUserInfo(id).subscribe(user => this.user = user);
      this.isAlreadyFriend = this.friendService.isInFriendList(id);
      if (!this.isAlreadyFriend) {
        this.isRequestReceived = this.friendService.isInReceivedRequestsList(id);
        if (!this.isRequestReceived) {
          this.isRequestSent = this.friendService.isInSentRequestsList(id);
        }
      }
    });
  }

  sendFriendRequest() {
    this.friendService.sendFriendRequest(this.user.id)
      .subscribe(response => {
        if (response) {
          this.isRequestSent = true;
        }
    });
  }

  acceptRequest() {
    this.friendService.confirmFriendRequest(this.user.id).subscribe(response => {
      if (response) {
        this.friendService.updateFriendRequests();
        this.isRequestReceived = false;
        this.isAlreadyFriend = true;
      }
    });
  }

  cancelRequest() {
    this.friendService.removeFriend(this.user.id).subscribe(response => {
      if (response) {
        this.isRequestSent = false;
      }
    });
  }

  deleteRequest() {
    this.friendService.removeFriend(this.user.id).subscribe(response => {
      if (response) {
        this.isRequestReceived = false;
      }
    });
  }

  removeFriend() {
    this.friendService.removeFriend(this.user.id)
      .subscribe(response => {
        if (response) {
          this.isAlreadyFriend = false;
        }
      });
  }

  newChat() {
    this.chatService.newChat(ChatType.PRIVATE, this.user.id);
  }

  ngOnDestroy() {
    this.ngUnsubscribe.next();
    this.ngUnsubscribe.complete();
  }
}
