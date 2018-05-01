import { Component, OnInit } from '@angular/core';
import {Game} from '../../_models/Game';
import {GameService} from '../../_services/game.service';
import {PostService} from '../../_services/post.service';
import {FilterByGame} from '../../_models/post-filters/FilterByGame';
import {FilterByActivity} from '../../_models/post-filters/FilterByActivity';

@Component({
  selector: 'app-post-filter',
  templateUrl: './post-filter.component.html',
  styleUrls: ['./post-filter.component.css']
})
export class PostFilterComponent implements OnInit {

  private games: Game[];
  private selectedGameIndex: number;
  private selectedActivityIndex: number;

  constructor(
    private gameService: GameService,
    private postService: PostService
  ) { }

  ngOnInit() {
    this.gameService.getGameList().subscribe(
      games => {
        this.games = games;
      });
  }

  addFilter() {
    if (this.selectedGameIndex) {
      if (this.selectedActivityIndex > -1) {
        const activityId = this.games[this.selectedGameIndex].activities[this.selectedActivityIndex].id;
        this.postService.addFilter(new FilterByActivity(activityId));
      } else {
        const gameId = this.games[this.selectedGameIndex].id;
        this.postService.addFilter(new FilterByGame(gameId));
      }
    }
  }

}
