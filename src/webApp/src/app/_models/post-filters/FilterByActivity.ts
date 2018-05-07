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

  compareTo(post: PostFilter): boolean {
    return post.isFilterByActivity() && (<FilterByActivity>post).activityId === this.activityId;
  }

  isFilterByGame(): boolean {
    return false;
  }

  isFilterByActivity(): boolean {
    return true;
  }

}
