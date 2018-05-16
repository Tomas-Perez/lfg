import { Injectable } from '@angular/core';
import {BehaviorSubject} from 'rxjs/BehaviorSubject';
import {PostFilter} from '../../../_models/post-filters/PostFilter';
import {PostService} from '../../../_services/post.service';

@Injectable()
export class PostFilterService {

  filters: PostFilter[];
  filtersSubject: BehaviorSubject<PostFilter[]>;

  constructor(postService: PostService) {
    this.filtersSubject = postService.filtersSubject;
    this.filtersSubject.subscribe(filters => this.filters = filters);
  }

  addFilter(filter: PostFilter) {
    if (!this.filters.find(elem => filter.compareTo(elem))) {
      this.filters.push(filter);
      this.filtersSubject.next(this.filters);
    }
  }

  removeFilter(filter: PostFilter) {
    this.filtersSubject.next(this.filters.filter(el => el !== filter));
  }

  resetFilters() {
    this.filtersSubject.next([]);
  }

  getFiltersLength(): number {
    return this.filters.length;
  }

}
