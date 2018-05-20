import { Component, OnInit } from '@angular/core';
import {FriendBarService} from '../../_services/friend-bar.service';
import {FriendLocation} from '../../_models/FriendLocation';

@Component({
  selector: 'app-friend-list',
  templateUrl: './friend-list.component.html',
  styleUrls: ['./friend-list.component.css']
})
export class FriendListComponent implements OnInit {

  constructor(private friendBarService: FriendBarService) { }

  ngOnInit() {
    this.friendBarService.friendLocationSubject.next(FriendLocation.LIST);
  }

}
