import { Component, OnInit } from '@angular/core';
import {FriendBarService} from '../../_services/friend-bar.service';
import {FriendLocation} from '../../_models/FriendLocation';

@Component({
  selector: 'app-friend-requests',
  templateUrl: './friend-requests.component.html',
  styleUrls: ['./friend-requests.component.css']
})
export class FriendRequestsComponent implements OnInit {

  constructor(private friendBarService: FriendBarService) { }

  ngOnInit() {
    this.friendBarService.friendLocationSubject.next(FriendLocation.REQUEST);
  }

}
