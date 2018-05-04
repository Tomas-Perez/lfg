import { Component, OnInit } from '@angular/core';
import {Game} from '../../_models/Game';
import {Activity} from '../../_models/Activity';
import {GameService} from '../../_services/game.service';
import {Router} from '@angular/router';
import {Status} from '../../_models/Status';

@Component({
  selector: 'app-game-new',
  templateUrl: './game-new.component.html',
  styleUrls: ['./game-new.component.css', '../admin-panel.component.css']
})
export class GameNewComponent implements OnInit {

  //game: Game;
  gameName: string;
  nameState: Status;
  activityState: {activity: Activity, status: Status}[];

  constructor(private gameService: GameService, private router: Router) { }

  ngOnInit() {
    //this.game = new Game;
    //this.game.activities = [];
    this.nameState = Status.NOTHING;
    this.gameName = '';
    this.activityState = [];
  }

  addActivity() {
    const array = JSON.parse(JSON.stringify(this.activityState));
    array.push({activity: new Activity(''), status: Status.NOTHING})
    this.activityState = array;
  }

  removeActivity(i: number) {
    this.activityState.splice(i, 1);
  }

  gameFromData(): Game {
    const game = new Game;
    game.name = this.gameName;
    game.activities = [];
    for (const activity of this.activityState) {
      game.activities.push(activity.activity);
    }
    return game;
  }

  newGame() {
    const game = this.gameFromData();
    this.nameState = Status.PROCESS;
    this.gameService.newGame(this.gameService.gameToDbGame(game)).subscribe(
      id => {
        console.log('Game id:');
        console.log(id);
        if (id === -1 ) { // TODO notify errors (-1 or -2)
          return;
        } else if (id === -2) {
          return;
        }
        this.nameState = Status.successOrError(id !== -1 && id !== -2);

        for (const nActivity of this.activityState) {
          const dbActivity = this.gameService.activityToDbActivity(nActivity.activity, id);
          this.gameService.newActivity(dbActivity).subscribe(
            state => {
              nActivity.status = Status.successOrError(state);
              this.goBackIfDoneUploading();
            }
          );
        }
        this.goBackIfDoneUploading();
      }
    );
  }

  goBackIfDoneUploading() {
    if (!Status.isDone(this.nameState)){
      return;
    }
    for (const nActivity of this.activityState){
      if (!Status.isDone(nActivity.status)) {
        return;
      }
    }
    console.log('done');
    this.gameService.updateGameList();
    this.router.navigate(['/admin-panel/games']);
  }

  getStatus(status: Status): string {
    return Status[status];
  }

}
