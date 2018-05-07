import {PostFilter} from './PostFilter';
import {Post} from '../Post';

export class FilterByActivity implements PostFilter {

  gameName: string;
  activityName: string;
  activityId: number;

  constructor(gameName: string, activityName: string, activityId: number) {
    this.gameName = gameName;
    this.activityName = activityName;
    this.activityId = activityId;
  }

  filter(post: Post): boolean {
    return post.activity.id === this.activityId;
  }

}
