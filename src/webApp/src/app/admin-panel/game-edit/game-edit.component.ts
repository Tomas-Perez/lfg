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

  constructor(
    private route: ActivatedRoute,
    private gameService: GameService,
    private location: Location
  ) { }

  ngOnInit() {
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
        }
      );
    }

    for (const activity of this.game.activities) {
      let found = false;
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
              }
            );
          }
          break;
        }
      }
      console.log(found);
      if (!found) {
        this.gameService.deleteActivity(activity.id).subscribe(
          response => {
            if (response) {
              console.log(activity.name + ' deleted');
            }
          }
        );
      }
    }
    for (const eActivity of this.editGame.activities) {
      if (!eActivity.id) {
        const dbActivity = this.gameService.activityToDbActivity(eActivity, this.game.id);
        this.gameService.newActivity(dbActivity).subscribe(
          response => {
            if (response) {
              console.log(dbActivity.name + ' added');
            }
          }
        );
      }
    }
    //this.getGame(); // TODO reload game when everything is finished
    //this.goBack();
  }

  addActivity() {
    this.editGame.activities.push(new Activity);
  }

  removeActivity(i: number) {
    this.editGame.activities.splice(i, 1);
  }

  goBack(): void {
    this.location.back();
  }

}
