import { Component, OnInit } from '@angular/core';
import {Game} from '../../_models/Game';
import {Activity} from '../../_models/Activity';
import {GameService} from '../../_services/game.service';
import {Router} from '@angular/router';

@Component({
  selector: 'app-game-new',
  templateUrl: './game-new.component.html',
  styleUrls: ['./game-new.component.css', '../admin-panel.component.css']
})
export class GameNewComponent implements OnInit {

  game: Game;
  activitiesState: Boolean[];

  constructor(private gameService: GameService, private router: Router) { }

  ngOnInit() {
    this.game = new Game;
    this.game.activities = [];
    this.activitiesState = []; // boolean[] -> index matching with game.activities. Made for future interface notifications.
  }

  addActivity() {
    this.game.activities.push(new Activity);
    this.activitiesState.push(undefined);
  }

  removeActivity(i: number) {
    this.game.activities.splice(i, 1);
    this.activitiesState.splice(i, 1);
  }

  newGame() {
    console.log(this.game);
    this.gameService.newGame(this.gameService.gameToDbGame(this.game)).subscribe(
      id => {
        console.log('Game id:');
        console.log(id);
        if (id === -1 ) { // TODO notify errors (-1 or -2)
          return;
        } else if (id === -2) {
          return;
        }
        for (let i = 0; i < this.game.activities.length ; i++) {
          const activity = this.game.activities[i];
          const dbActivity = this.gameService.activityToDbActivity(activity, id);
          this.gameService.newActivity(dbActivity).subscribe(
            state => {
              this.activitiesState[i] = state;
              this.goBackIfDoneUploading();
            }
          );
        }
        this.goBackIfDoneUploading();
      }
    );
  }

  goBackIfDoneUploading() {
    let done = true;
    for (const activityState of this.activitiesState){
      if (activityState === undefined) {
        done = false;
      }
    }
    if (done) {
      console.log('done');
      this.gameService.updateGameList();
      this.router.navigate(['/admin-panel/games']);
    }
  }

}
