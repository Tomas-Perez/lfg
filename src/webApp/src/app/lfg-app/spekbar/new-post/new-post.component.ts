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

@Component({
  selector: 'app-new-post',
  templateUrl: './new-post.component.html',
  styleUrls: ['./new-post.component.css', '../spekbar.css']
})
export class NewPostComponent implements OnInit, OnDestroy {

  private games: Game[];
  private newPostModel: NewPostModel = new NewPostModel;
  private user: User;
  private ngUnsubscribe: Subject<any> = new Subject();
  private post: Post;

  constructor(
    private userService: UserService,
    private gameService: GameService,
    private newPostService: NewPostService,
    private postService: PostService
    ) { }

  ngOnInit() {

    this.userService.userSubject.takeUntil(this.ngUnsubscribe)
      .subscribe( user => this.user = user);

    this.gameService.gamesSubject.takeUntil(this.ngUnsubscribe)
      .subscribe(games => {
        this.games = games;

        this.newPostService.postSubject.takeUntil(this.ngUnsubscribe)
          .subscribe(newPostModel => {
            this.newPostModel = newPostModel;
        });
    });

    this.postService.currentPostSubject.subscribe(post => this.post = post);

  }

  newPost() {
    this.newPostModel.dbPost.ownerID = this.user.id;
    this.postService.newPost(this.newPostModel.dbPost).subscribe(
      response => {
        if (response) {
          console.log('Post created');
        }
      }
    );
  }

  deletePost(){
    this.postService.deletePost().subscribe(
      response => {
        if(response){
          console.log('Post deleted');
        }
      }
    )
  }

  ngOnDestroy() {
    this.ngUnsubscribe.next();
    this.ngUnsubscribe.complete();
    this.newPostService.updatePost(this.newPostModel);
  }

}
