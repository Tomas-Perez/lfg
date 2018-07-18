import {Post} from '../Post';
import {Activity} from '../Activity';
import {Game} from '../Game';
import {GamePlatform} from '../GamePlatform';
import {ChatPlatform} from '../ChatPlatform';
import {PostType} from './PostType';

export class PostFilter {

  game: Game;
  activity: Activity;
  gamePlatform: GamePlatform;
  chatPlatform: ChatPlatform;
  postType: PostType;

  constructor(game: Game, activity?: Activity, chatPlatform?: ChatPlatform, gamePlatform?: GamePlatform, postType?: PostType) {
    this.game = game || null;
    this.activity = activity || null;
    this.gamePlatform = gamePlatform || null;
    this.chatPlatform = chatPlatform || null;
    this.postType = postType || null;
  }

  filter(post: Post): boolean {
    return post.activity.id === this.activity.id &&
      post.chatPlatforms[0].id === this.chatPlatform.id &&
      post.gamePlatforms[0].id === this.gamePlatform.id;
  }

  compareTo(filter: PostFilter): boolean {
    return filter.game.id === this.game.id &&
      ((filter.activity === this.activity) /* || filter.activity.id === this.activity.id*/) &&
      ((filter.chatPlatform === this.chatPlatform) /* || filter.chatPlatform.id === this.chatPlatform.id*/) &&
      ((filter.gamePlatform === this.gamePlatform) /*|| filter.gamePlatform.id === this.gamePlatform.id*/) &&
      ((filter.postType === this.postType) /* || filter.postType === this.postType*/);
  }

  toQueryParam(): string {
    return 'filter=A' + this.game.id +
      (this.activity != null ? ':' + this.activity.id : '') +
      (this.gamePlatform != null ? ',G' + this.gamePlatform.id : '') +
      (this.chatPlatform != null ? ',C' + this.chatPlatform.id : '') +
      (this.chatPlatform != null ? ',T' + this.postType : '');
  }
}
