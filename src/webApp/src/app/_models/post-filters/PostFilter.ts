import {Post} from '../Post';

export interface PostFilter {

  gameName: string;
  activityName: string;

  filter(post: Post): boolean;
  isFilterByGame(): boolean;
  isFilterByActivity(): boolean;
  compareTo(post: PostFilter): boolean;
}
