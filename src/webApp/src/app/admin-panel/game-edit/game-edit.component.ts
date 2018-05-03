import {Component, OnInit} from '@angular/core';
import {Game} from '../../_models/Game';
import {ActivatedRoute} from '@angular/router';
import {GameService} from '../../_services/game.service';
import {Location} from '@angular/common';
import 'rxjs/add/operator/switchMap';
import {Activity} from '../../_models/Activity';

@Component({
  selector: 'app-game-edit',
  templateUrl: './game-edit.component.html',
  styleUrls: ['./game-edit.component.css', '../admin-panel.component.css']
})
export class GameEditComponent implements OnInit {

  // game$: Observable<Game>;
  game: Game;
  editGame: Game;
  activitiesState: Boolean[];
  gameState: boolean;

  constructor(
    private route: ActivatedRoute,
    private gameService: GameService,
    private location: Location
  ) { }

  ngOnInit() {
    this.gameState = undefined;
    this.getGame();
  }

  getGame() {// TODO goBack() if error
    /*
    this.game$ = this.route.paramMap
      .switchMap((params: ParamMap) =>
        this.gameService.getGame(+params.get('id')));
     */
    const id = +this.route.snapshot.paramMap.get('id');
    this.gameService.getGame(id)
      .subscribe(game => {
        this.game = game;
        this.editGame = JSON.parse(JSON.stringify(game));
        this.activitiesState = new Array<Boolean>(this.game.activities.length); // boolean[] -> index matching with editGame.activities. Made for future interface notifications.
      });
  }

  updateGame() {
    const dbGame = this.gameService.gameToDbGame(this.editGame);
    if (this.game.name !== this.editGame.name) {
      this.gameService.updateGame(dbGame, this.game.id).subscribe(
        response => {
          if (response) {
            console.log(this.game.name + ' updated to ' + this.editGame.name);
          }
          this.gameState = response;
        }
      );
    }

    for (const activity of this.game.activities) {
      let found = false;
      let i = 0; // activitiesState index
      for (const eActivity of this.editGame.activities) {
        if (activity.id === eActivity.id) {
          found = true;
          if (activity.name !== eActivity.name) {
            const dbActivity = this.gameService.activityToDbActivity(eActivity, this.game.id);
            this.gameService.updateActivity(dbActivity, activity.id).subscribe(
              response => {
                if (response) {
                  console.log(activity.name + ' updated to ' + eActivity.name);
                }
                this.activitiesState[i] = response;
              }
            );
          } else {
            this.activitiesState[i] = true;
          }
          break;
        }
        i++;
      }
      console.log(found);
      if (!found) {
        this.gameService.deleteActivity(activity.id).subscribe(
          response => {
            if (response) {
              console.log(activity.name + ' deleted'); // TODO notify error on deleting
            }
          }
        );
      }
    }
    let j = 0; // activitiesState index
    for (const eActivity of this.editGame.activities) {
      if (!eActivity.id) {
        const dbActivity = this.gameService.activityToDbActivity(eActivity, this.game.id);
        this.gameService.newActivity(dbActivity).subscribe(
          response => {
            if (response) {
              console.log(dbActivity.name + ' added');
            }
            this.activitiesState[i] = response;
          }
        );
      }
      j++;
    }
    //this.getGame(); // TODO reload game when everything is finished
    //this.goBack();
  }

  addActivity() {
    this.editGame.activities.push(new Activity);
    this.activitiesState.push(undefined);
  }

  removeActivity(i: number) {
    this.editGame.activities.splice(i, 1);
    this.activitiesState.splice(i, 1);
  }

  goBack(): void {
    this.location.back();
  }

}
