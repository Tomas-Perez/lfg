import {Component, OnDestroy, OnInit} from '@angular/core';
import {FriendLocation} from '../../_models/FriendLocation';
import {FriendStateService} from '../../_services/friend-state.service';
import {BasicUser} from '../../../_models/BasicUser';
import {Router} from '@angular/router';
import {FriendService} from '../../../_services/friend.service';
import {Subject} from 'rxjs/Subject';
import {UserService} from '../../../_services/user.service';
import 'rxjs/add/operator/takeUntil';
import 'rxjs/add/operator/debounceTime';
import 'rxjs/add/operator/distinctUntilChanged';
import {SearchService} from './search.service';
import {User} from '../../../_models/User';

@Component({
  selector: 'app-search',
  templateUrl: './search.component.html',
  styleUrls: ['./search.component.css']
})
export class SearchComponent implements OnInit, OnDestroy {

  private ngUnsubscribe: Subject<any> = new Subject();
  user: User;
  searchChanged: Subject<string>;
  results: {user: BasicUser, isFriend: boolean}[];
  searchInput: string;

  constructor(
    private friendBarService: FriendStateService,
    private friendService: FriendService,
    private userService: UserService,
    private searchService: SearchService,
    private router: Router
  ) { }

  ngOnInit() {

    this.userService.userSubject.take(1).subscribe(user => this.user = user);

    this.searchService.searchTextSubject.take(1).subscribe(searchText => {
      this.searchInput = searchText;
      this.search(this.searchInput);
    });

    this.searchChanged = new Subject<string>();

    this.searchChanged
      .debounceTime(300)
      .distinctUntilChanged()
      .subscribe(searchText => this.search(searchText));

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

  onSearchChanged(event) {
    this.searchChanged.next(this.searchInput);
  }

  getUserInfo(id: number) {
    if (this.user.id === id) {return; }
    this.router.navigate(['/app', { outlets: {friends: ['user-info', id] }}], {
      skipLocationChange: true
    });
  }

  sendFriendRequest(id: number) {
    this.friendService.sendFriendRequest(id)
      .subscribe(response => {
        if (response) {
          this.search(this.searchInput);
        }
      });
  }

  search(text: string) {
    this.ngUnsubscribe.next();
    const sText = text.trim();
    if (sText === '') {
      this.results = [];
      return;
    }
    this.friendService.searchUsers(sText).takeUntil(this.ngUnsubscribe).subscribe(users => {
      const results = [];
      for (const user of users) {
        results.push({user: user, isFriend: this.isAlreadyFriendOrMe(user.id)});
      }
      this.results = results;
    });
  }

  isAlreadyFriendOrMe(id: number): boolean {
    return this.user.id === id || this.friendService.isFriendOrWillBe(id);
  }

  ngOnDestroy() {
    this.ngUnsubscribe.next();
    this.ngUnsubscribe.complete();
    this.searchService.updateSearchText(this.searchInput);
  }
}
