import {Component, OnDestroy, OnInit} from '@angular/core';
import {FriendLocation} from '../../_models/FriendLocation';
import {FriendStateService} from '../../_services/friend-state.service';
import {BasicUser} from '../../../_models/BasicUser';
import {Router} from '@angular/router';
import {FriendService} from '../../../_services/friend.service';
import {Subject} from 'rxjs/Subject';

@Component({
  selector: 'app-search',
  templateUrl: './search.component.html',
  styleUrls: ['./search.component.css']
})
export class SearchComponent implements OnInit, OnDestroy {

  private ngUnsubscribe: Subject<any> = new Subject();
  searchInput: string;
  results: BasicUser[];

  constructor(
    private friendBarService: FriendStateService,
    private friendService: FriendService,
    private router: Router
  ) { }

  ngOnInit() {
    this.searchInput = '';

    /*
    const a = new BasicUser();
    a.username = 'hola';
    const b = new BasicUser();
    b.username = 'hola';
    const c = new BasicUser();
    c.username = 'hola';

    this.results = [a, b, c];
    */
    this.results = [];
    this.friendBarService.friendLocationSubject.next(FriendLocation.SEARCH);
  }

  getUserInfo(id: number) {
    this.router.navigate(['/app', { outlets: {friends: ['user-info', id] }}], {
      skipLocationChange: true
    });
  }

  sendFriendRequest(id: number) {
    this.friendService.sendFriendRequest(id)
      .subscribe(response => {
        if (response) {
          this.friendService.updateSentRequests();
        }
      });
  }

  onSearchChange(event: Event) {
    console.log(event);
    // TODO search
    this.ngUnsubscribe.next();
    // this.friendService.search(this.searchInput).takeUntil(this.ngUnsubscribe).subscribe(result => this.results = results);
  }

  ngOnDestroy() {
    this.ngUnsubscribe.next();
    this.ngUnsubscribe.complete();
  }
}
