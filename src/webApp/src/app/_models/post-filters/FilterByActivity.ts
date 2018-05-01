import {PostFilter} from './PostFilter';
import {Post} from '../Post';

export class FilterByActivity implements PostFilter {

  activityId: number;

  constructor(activityId: number) {
    this.activityId = activityId;
  }

  filter(post: Post): boolean {
    return post.activity.id === this.activityId;
  }

}
