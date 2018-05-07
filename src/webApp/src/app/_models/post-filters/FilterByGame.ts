import {PostFilter} from './PostFilter';
import {Post} from '../Post';

export class FilterByGame implements PostFilter {

  gameName: string;
  activityName: string;
  gameId: number;

  constructor(gameName: string, gameId: number) {
    this.gameName = gameName;
    this.activityName = '';
    this.gameId = gameId;
  }

  filter(post: Post): boolean {
    return post.activity.game.id === this.gameId;
  }

  compareTo(post: PostFilter): boolean {
    return post.isFilterByGame() && (<FilterByGame>post).gameId === this.gameId;
  }

  isFilterByGame(): boolean {
    return true;
  }

  isFilterByActivity(): boolean {
    return false;
  }

}
