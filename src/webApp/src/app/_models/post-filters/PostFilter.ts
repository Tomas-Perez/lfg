import {Post} from '../Post';

export interface PostFilter {
  filter(post: Post): boolean;
}
