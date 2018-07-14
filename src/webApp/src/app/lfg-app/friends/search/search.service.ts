import { Injectable } from '@angular/core';
import {BehaviorSubject} from 'rxjs/BehaviorSubject';

@Injectable()
export class SearchService {

  searchText: string;
  searchTextSubject: BehaviorSubject<string>;

  constructor() {
    this.searchText = '';
    this.searchTextSubject = new BehaviorSubject(this.searchText);
  }

   updateSearchText(searchText: string) {
    this.searchText = searchText;
    this.searchTextSubject.next(this.searchText);
  }

}
