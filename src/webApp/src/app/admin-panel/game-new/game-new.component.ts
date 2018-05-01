import { Component, OnInit } from '@angular/core';
import {Game} from '../../_models/Game';
import {Activity} from '../../_models/Activity';
import {GameService} from '../../_services/game.service';
import {Observable} from 'rxjs/Observable';

@Component({
  selector: 'app-game-new',
  templateUrl: './game-new.component.html',
  styleUrls: ['./game-new.component.css', '../admin-panel.component.css']
})
export class GameNewComponent implements OnInit {

  game: Game;

  constructor(private gameService: GameService) { }

  ngOnInit() {
    this.game = new Game;
    this.game.activities = [];
  }

  addActivity() {
    this.game.activities.push(new Activity);
  }

  removeActivity(i: number) {
    this.game.activities.splice(i, 1);
  }

  newGame() {
    console.log(this.game);
    let gameId = -3;

    this.gameService.newGame(this.game).subscribe(
      id => {
        console.log('Game id:');
        console.log(id);
        if (id === -1 ) { // TODO notify errors (-1 or -2)
          return;
        } else if (id === -2) {
          return;
        }
        gameId = id;

        const activitiesState = []; // boolean[] -> index matching with game.activities. Made for future interface notifications.
        for (let i = 0; i < this.game.activities.length ; i++) {
          activitiesState.push(undefined);
          const activity = this.game.activities[i];
          const dbActivity = this.gameService.activityToDbActivity(activity, gameId);
          this.gameService.newActivity(dbActivity).subscribe(
            state => {
              activitiesState[i] = state;
              console.log('Activity:', i);
              console.log(dbActivity.name);
              console.log(activitiesState);
            }
          );
        }

      }
    );

  }

}
