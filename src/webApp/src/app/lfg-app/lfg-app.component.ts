import {Component, OnDestroy, OnInit} from '@angular/core';
import 'simplebar';
import 'rxjs/add/operator/takeUntil';
import {FriendStateService} from './_services/friend-state.service';
import {Subject} from 'rxjs/Subject';

@Component({
  selector: 'app-lfg-app',
  templateUrl: './lfg-app.component.html',
  styleUrls: ['./lfg-app.component.css']
})
export class LfgAppComponent implements OnInit, OnDestroy {

  private ngUnsubscribe: Subject<any> = new Subject();
  isFriendsOpen: Boolean;

  constructor(private friendStateService: FriendStateService) { }

  ngOnInit() {
    this.friendStateService.openSubject.takeUntil(this.ngUnsubscribe)
      .subscribe(isOpen => this.isFriendsOpen = isOpen);
  }


  toggleFriendsOpen() {
    this.friendStateService.toggleOpen();
  }

  ngOnDestroy() {
    this.ngUnsubscribe.next();
    this.ngUnsubscribe.complete();
  }

}
