import { Injectable } from '@angular/core';
import {FriendLocation} from '../_models/FriendLocation';
import {BehaviorSubject} from 'rxjs/BehaviorSubject';

@Injectable()
export class FriendBarService {

  private friendLocation: FriendLocation;
  friendLocationSubject: BehaviorSubject<FriendLocation>;

  constructor() {
    this.friendLocationSubject = new BehaviorSubject<FriendLocation>(FriendLocation.NOTHING);
    this.friendLocationSubject.subscribe(friendLocation => this.friendLocation = friendLocation);
  }

}
