import { Injectable } from '@angular/core';
import {FriendLocation} from '../_models/FriendLocation';
import {BehaviorSubject} from 'rxjs/BehaviorSubject';

@Injectable()
export class FriendStateService {

  private friendLocation: FriendLocation;
  friendLocationSubject: BehaviorSubject<FriendLocation>;
  private open: Boolean;
  openSubject: BehaviorSubject<Boolean>;

  constructor() {
    this.friendLocationSubject = new BehaviorSubject<FriendLocation>(FriendLocation.NOTHING);
    this.friendLocationSubject.subscribe(friendLocation => this.friendLocation = friendLocation);
    this.openSubject = new BehaviorSubject<Boolean>(true);
    this.openSubject.subscribe(state => this.open = state);
  }

  toggleOpen() {
    this.openSubject.next(!this.open);
  }

}
