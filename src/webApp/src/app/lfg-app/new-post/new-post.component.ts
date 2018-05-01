import { Component, OnInit } from '@angular/core';
import {Game} from '../../_models/Game';
import {GameService} from '../../_services/game.service';
import {PostService} from '../../_services/post.service';
import {DbPost} from '../../_models/DbPost';
import {AuthService} from '../../_services/auth.service';
import {UserService} from '../../_services/user.service';

@Component({
  selector: 'app-new-post',
  templateUrl: './new-post.component.html',
  styleUrls: ['./new-post.component.css']
})
export class NewPostComponent implements OnInit {


  private games: Game[];
  private selectedGameIndex: number;
  private selectedActivityIndex: number;
  private description: string;

  constructor(
    private userService: UserService,
    private gameService: GameService,
    private postService: PostService
    ) { }

  ngOnInit() {
    this.gameService.getGameList().subscribe(
      games => {
        this.games = games;
    });
    this.description = '';
  }

  newPost() {
    const post = new DbPost;
    post.description = this.description;
    post.activityID = this.games[this.selectedGameIndex].activities[this.selectedActivityIndex].id;
    this.userService.getCurrentUser().subscribe(
      user => {
        post.ownerID = user.id;
        this.postService.newPost(post).subscribe(
          response => {
            if (response) {
              console.log('Post created');
            }
          }
        );
      }
    );
  }

}
