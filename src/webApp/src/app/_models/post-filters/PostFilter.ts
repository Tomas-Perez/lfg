import {Post} from '../Post';

export interface PostFilter {

  gameName: string;
  activityName: string;

  filter(post: Post): boolean;
}
