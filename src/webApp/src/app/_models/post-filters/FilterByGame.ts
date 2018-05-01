import {PostFilter} from './PostFilter';
import {Post} from '../Post';

export class FilterByGame implements PostFilter {

  gameId: number;

  constructor(gameId: number) {
    this.gameId = gameId;
  }

  filter(post: Post): boolean {
    return post.activity.game.id === this.gameId;
  }

}
