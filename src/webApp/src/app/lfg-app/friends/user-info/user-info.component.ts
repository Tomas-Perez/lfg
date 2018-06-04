import { Component, OnInit } from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {FriendService} from '../../../_services/friend.service';
import {BasicUser} from '../../../_models/BasicUser';
import {FriendStateService} from '../../_services/friend-state.service';
import {FriendLocation} from '../../_models/FriendLocation';

@Component({
  selector: 'app-user-info',
  templateUrl: './user-info.component.html',
  styleUrls: ['./user-info.component.css']
})
export class UserInfoComponent implements OnInit {

  user: BasicUser;
  isAlreadyFriend: boolean;
  isRequestSent: boolean;
  isRequestReceived: boolean;

  constructor(private route: ActivatedRoute,
              private friendService: FriendService,
              private friendBarService: FriendStateService
  ) { }

  ngOnInit() {
    this.friendBarService.friendLocationSubject.next(FriendLocation.USERINFO);

    this.isAlreadyFriend = false;
    this.isAlreadyFriend = false;
    this.isRequestSent = false;
    this.isRequestReceived = false;

    this.user = null;

    this.route.params.subscribe(params => {
      const id = +params['id'];
      this.friendService.getUserInfo(id).subscribe(user => this.user = user);
      this.isAlreadyFriend = this.friendService.isInFriendList(id);
      if(!this.isAlreadyFriend){
        this.isRequestReceived = this.friendService.isInReceivedRequestsList(id);
        if(!this.isRequestReceived){
          this.isRequestSent = this.friendService.isInSentRequestsList(id);
        }
      }
    });
  }

  sendFriendRequest() {
    this.friendService.sendFriendRequest(this.user.id)
      .subscribe(response => {
        if(response){
          this.isRequestSent = true;
          this.friendService.updateSentRequests();
          this.isRequestSent = true;
        }
    });
  }

  acceptRequest(){
    this.friendService.confirmFriendRequest(this.user.id).subscribe(response => {
      if(response){
        this.friendService.updateFriendRequests();
        this.friendService.updateFriends();
        this.isRequestReceived = false;
        this.isAlreadyFriend = true;
      }
    });
  }

  cancelRequest(){
    this.friendService.removeFriend(this.user.id).subscribe(response => {
      if(response){
        this.friendService.updateSentRequests();
        this.isRequestSent = false;
      }
    });
  }

  deleteRequest(){
    this.friendService.removeFriend(this.user.id).subscribe(response => {
      if(response){
        this.friendService.updateFriendRequests();
        this.isRequestReceived = false;
      }
    });
  }

  removeFriend() {
    this.friendService.removeFriend(this.user.id)
      .subscribe(response => {
        if(response) {
          this.friendService.updateFriends();
          this.isAlreadyFriend = false;
        }
      });
  }
}
