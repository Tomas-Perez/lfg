import {PostFilter} from './PostFilter';
import {Post} from '../Post';

export class Filter {

  filter(post: Post, filters: PostFilter[]): boolean {
    for (const filter of filters) {
      if (filter.filter(post)) {
        return true;
      }
    }
    return !(filters.length > 0);
  }

}
