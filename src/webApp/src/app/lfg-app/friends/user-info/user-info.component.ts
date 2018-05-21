import { Component, OnInit } from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {FriendService} from '../../../_services/friend.service';
import {BasicUser} from '../../../_models/BasicUser';
import {FriendBarService} from '../../_services/friend-bar.service';
import {FriendLocation} from '../../_models/FriendLocation';

@Component({
  selector: 'app-user-info',
  templateUrl: './user-info.component.html',
  styleUrls: ['./user-info.component.css']
})
export class UserInfoComponent implements OnInit {

  user: BasicUser;
  isAlreadyFriend: boolean;
  requestSent: boolean;
  id: number;

  constructor(private route: ActivatedRoute,
              private friendService: FriendService,
              private friendBarService: FriendBarService
  ) { }

  ngOnInit() {
    this.friendBarService.friendLocationSubject.next(FriendLocation.USERINFO);

    this.user = null;

    this.requestSent = false;
    this.route.params.subscribe(params => {
      const id = +params['id'];
      this.id = id;
      this.friendService.getUserInfo(id).subscribe(user => this.user = user);
      this.isAlreadyFriend = this.friendService.isInFriendList(id);
    });
  }

  sendFriendRequest() {
    this.friendService.sendFriendRequest(this.id)
      .subscribe(response => {
          this.requestSent = response;
    });
  }

}
