import {Component, OnDestroy, OnInit} from '@angular/core';
import {Game} from '../../_models/Game';
import {GameService} from '../../_services/game.service';
import {PostService} from '../../_services/post.service';
import {UserService} from '../../_services/user.service';
import 'rxjs/add/operator/takeUntil';
import {Subject} from 'rxjs/Subject';
import {NewPostService} from './new-post.service';
import {NewPostModel} from './new-post.model';

@Component({
  selector: 'app-new-post',
  templateUrl: './new-post.component.html',
  styleUrls: ['./new-post.component.css']
})
export class NewPostComponent implements OnInit, OnDestroy {

  private games: Game[];
  private newPostModel: NewPostModel = new NewPostModel;
  private ngUnsubscribe: Subject<any> = new Subject();

  constructor(
    private userService: UserService,
    private gameService: GameService,
    private newPostService: NewPostService,
    private postService: PostService
    ) { }

  ngOnInit() {

    this.gameService.gamesSubject.takeUntil(this.ngUnsubscribe)
      .subscribe(games => {
        this.games = games;

        this.newPostService.postSubject.takeUntil(this.ngUnsubscribe)
          .subscribe(newPostModel => {
            this.newPostModel = newPostModel;
        });

    });

  }

  newPost() {
    console.log(this.newPostModel.dbPost.activityID);
    this.userService.getCurrentUser().subscribe(
      user => {
        this.newPostModel.dbPost.ownerID = user.id;
        this.postService.newPost(this.newPostModel.dbPost).subscribe(
          response => {
            if (response) {
              console.log('Post created');
            }
          }
        );
      }
    );
  }

  ngOnDestroy() {
    this.ngUnsubscribe.next();
    this.ngUnsubscribe.complete();
    this.newPostService.updatePost(this.newPostModel);
  }

}
