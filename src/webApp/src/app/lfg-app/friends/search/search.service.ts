import { Injectable } from '@angular/core';
import {BehaviorSubject} from 'rxjs/BehaviorSubject';
import {UserService} from '../../../_services/user.service';

@Injectable()
export class SearchService {

  searchText: string;
  searchTextSubject: BehaviorSubject<string>;

  constructor(private userService: UserService) {
    this.searchText = '';
    this.searchTextSubject = new BehaviorSubject(this.searchText);
    this.userService.userSubject.subscribe(user => {
      this.searchText = '';
      this.searchTextSubject.next(this.searchText);
    });
  }

   updateSearchText(searchText: string) {
    this.searchText = searchText;
    this.searchTextSubject.next(this.searchText);
  }

}
