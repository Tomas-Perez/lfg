import { Component, OnInit } from '@angular/core';
import {FriendBarService} from '../../_services/friend-bar.service';
import {FriendLocation} from '../../_models/FriendLocation';
import {BasicUser} from '../../../_models/BasicUser';
import {FriendService} from '../../../_services/friend.service';

@Component({
  selector: 'app-friend-requests',
  templateUrl: './friend-requests.component.html',
  styleUrls: ['./friend-requests.component.css']
})
export class FriendRequestsComponent implements OnInit {

  requests: BasicUser[];

  constructor(private friendBarService: FriendBarService,
              private friendService: FriendService
              ) { }

  ngOnInit() {
    this.friendBarService.friendLocationSubject.next(FriendLocation.REQUEST);
    this.friendService.getFriendRequests().subscribe(requests => this.requests = requests);
  }

}
