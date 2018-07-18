import {Component, OnDestroy, OnInit} from '@angular/core';
import {Game} from '../../../_models/Game';
import {GameService} from '../../../_services/game.service';
import {PostService} from '../../../_services/post.service';
import {UserService} from '../../../_services/user.service';
import 'rxjs/add/operator/takeUntil';
import {Subject} from 'rxjs/Subject';
import {NewPostService} from './new-post.service';
import {NewPostModel} from './new-post.model';
import {User} from '../../../_models/User';
import {Post} from '../../../_models/Post';
import {NavBarService} from '../../_services/nav-bar.service';
import {SpekbarLocation} from '../../_models/SpekbarLocation';
import {PlatformService} from '../../../_services/platform.service';
import {GamePlatform} from '../../../_models/GamePlatform';
import {ChatPlatform} from '../../../_models/ChatPlatform';

@Component({
  selector: 'app-new-post',
  templateUrl: './new-post.component.html',
  styleUrls: ['./new-post.component.css', '../spekbar.css']
})
export class NewPostComponent implements OnInit, OnDestroy {

  private games: Game[];
  private gamePlatforms: GamePlatform[];
  private chatPlatforms: ChatPlatform[];
  private newPostModel: NewPostModel = new NewPostModel;
  private user: User;
  private ngUnsubscribe: Subject<any> = new Subject();
  private post: Post;
  private postExists: boolean;
  private postErrorTimer: any;
  private isPostError: boolean;
  private postErrorTime: number;

  constructor(
    private userService: UserService,
    private gameService: GameService,
    private platformService: PlatformService,
    private newPostService: NewPostService,
    private navBarService: NavBarService,
    private postService: PostService
    ) { }

  ngOnInit() {
    this.isPostError = false;
    this.postExists = false;
    this.games = [];
    this.gamePlatforms = [];
    this.chatPlatforms = [];

    this.navBarService.spekbarLocationSubject.next(SpekbarLocation.NEWPOST);

    this.userService.userSubject.takeUntil(this.ngUnsubscribe)
      .subscribe( user => this.user = user);

    this.gameService.gamesSubject.takeUntil(this.ngUnsubscribe)
      .subscribe(games => this.games = games);

    this.newPostService.postSubject.takeUntil(this.ngUnsubscribe)
      .subscribe(newPostModel => this.newPostModel = newPostModel);

    this.platformService.gamePlatformsSubject.takeUntil(this.ngUnsubscribe)
      .subscribe(platforms => this.gamePlatforms = platforms);

    this.platformService.chatPlatformsSubject.takeUntil(this.ngUnsubscribe)
      .subscribe(platforms => this.chatPlatforms = platforms);

    this.postService.currentPostSubject.takeUntil(this.ngUnsubscribe)
      .subscribe(post => {
        this.post = post;
        if (this.post == null) {
          if (!this.postExists) {
            this.postExists = false;
          }
        } else {
          this.postExists = true;
        }
      });

  }

  newPost() {
    this.newPostModel.dbPost.activityID = this.games[this.newPostModel.selectedGameIndex]
                                            .activities[this.newPostModel.selectedActivityIndex].id;
    this.newPostModel.dbPost.gamePlatforms = [this.gamePlatforms[this.newPostModel.selectedGamePlatformIndex].id];
    if (this.newPostModel.selectedChatPlatformIndex >= 0) {
      this.newPostModel.dbPost.chatPlatforms = [this.chatPlatforms[this.newPostModel.selectedChatPlatformIndex].id];
    }
    this.newPostModel.dbPost.ownerID = this.user.id;

    this.postService.newPost(this.newPostModel.dbPost).subscribe(
      response => {
        if (response === 0) {
          console.log('Post created');
        } else if (response < 0) {
          // error getting new post
        } else if (response > 0) {
          if (this.postErrorTimer) {
            clearTimeout(this.postErrorTimer);
          }
          this.postErrorTime = response;
          this.isPostError = true;
          this.postErrorTimer = setTimeout( () => {
            this.isPostError = false;
          }, 5000 );
        }
      }
    );
  }

  deletePost() {
    this.postService.deleteCurrentPost().subscribe(
      response => {
        if (response) {
          console.log('Post deleted');
          this.postExists = false;
        }
      }
    );
  }

  ngOnDestroy() {
    this.ngUnsubscribe.next();
    this.ngUnsubscribe.complete();
    this.newPostService.updatePost(this.newPostModel);
  }

}
