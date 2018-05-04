import {Component, OnInit} from '@angular/core';
import {Game} from '../../_models/Game';
import {ActivatedRoute, Router} from '@angular/router';
import {GameService} from '../../_services/game.service';
import 'rxjs/add/operator/switchMap';
import {Activity} from '../../_models/Activity';
import {Status} from '../../_models/Status';
import {DbGame} from '../../_models/DbGame';

@Component({
  selector: 'app-game-edit',
  templateUrl: './game-edit.component.html',
  styleUrls: ['./game-edit.component.css', '../admin-panel.component.css']
})
export class GameEditComponent implements OnInit {

  // game$: Observable<Game>;
  game: Game;
  editName: string;
  nameState: Status;
  activityState: {activity: Activity, status: Status}[];
  activityToDeleteState: {activity: Activity, status: Status}[];
  deleteActivityCount: number; // If activityToDeleteState.length === deleteActivityCount then the deletion is finished.

  constructor(
    private route: ActivatedRoute,
    private gameService: GameService,
    private router: Router
  ) { }

  ngOnInit() {
    this.nameState = Status.NOTHING;
    this.activityToDeleteState = [];
    this.deleteActivityCount = 0;
    this.getGame(true);
  }

  getGame(firsTime: boolean) {// TODO goBack() if error
    /*
    this.game$ = this.route.paramMap
      .switchMap((params: ParamMap) =>
        this.gameService.getGame(+params.get('id')));
     */
    const id = +this.route.snapshot.paramMap.get('id');
    this.gameService.getGame(id).toPromise()
      .then(
      game => {
        console.log('Game updated');
        this.game = game;
        this.editName = game.name;
        if (firsTime) {
          this.setUpActivityState();
        }
      })
      .catch(err => this.router.navigate(['/admin-panel/games']));
  }

  setUpActivityState() {
    this.activityState = [];
    for (let i = 0; i < this.game.activities.length; i++) {
      this.activityState.push({activity: this.game.activities[i], status: Status.NOTHING});
    }
    console.log(this.activityState);
  }

  restartActivityState() {
    for (let i = 0; i < this.activityState.length; i++) {
      this.activityState[i].status = Status.NOTHING;
    }
  }

  updateGame() {
    this.restartActivityState();
    console.log(this.game.activities);
    console.log(this.activityState);
    console.log(this.activityToDeleteState);

    // Update Game
    if (this.game.name !== this.editName) {
      const dbGame = new DbGame(this.editName);
      this.nameState = Status.PROCESS;
      this.gameService.updateGame(dbGame, this.game.id).subscribe(
        response => {
          if (response) {
            console.log(this.game.name + ' updated to ' + this.editName);
            this.game.name = this.editName;
          }
          this.nameState = Status.successOrError(response);
        }
      );
    }

    // Update activities
    for (const activity of this.game.activities) {
      for (const eActivity of this.activityState) {
        if (activity.id === eActivity.activity.id) {
          if (activity.name !== eActivity.activity.name) {
            eActivity.status = Status.PROCESS;
            const dbActivity = this.gameService.activityToDbActivity(eActivity.activity, this.game.id);
            this.gameService.updateActivity(dbActivity, eActivity.activity.id).subscribe(
              response => {
                if (response) {
                  console.log(activity.name + ' updated to ' + eActivity.activity.name);
                  activity.name = eActivity.activity.name;
                }
                eActivity.status = Status.successOrError(response);
              }
            );
          }
          break;
        }
      }
    }

    // Delete activities
    for (let i = 0; i < this.activityToDeleteState.length; i++) {
      const dActivity = this.activityToDeleteState[i];
      if (dActivity.activity.id && (dActivity.status === Status.NOTHING || dActivity.status === Status.ERROR)) {
        dActivity.status = Status.PROCESS;
        this.gameService.deleteActivity(dActivity.activity.id).subscribe(
          response => {
            if (response) {
              console.log(dActivity.activity.name + ' deleted'); // TODO notify error on deleting
              this.game.activities = this.game.activities.filter(act => act.id !== dActivity.activity.id);
            }
            dActivity.status = Status.successOrError(response);
          }
        );
      } else {
        this.activityToDeleteState.splice(i--, 1);
      }
    }

    // New activities
    for (const eActivity of this.activityState) {
      if (!eActivity.activity.id) {
        const dbActivity = this.gameService.activityToDbActivity(eActivity.activity, this.game.id);
        eActivity.status = Status.PROCESS;
        this.gameService.newActivityWithId(dbActivity).subscribe(
          id => {
            if (id >= 0) {
              console.log(dbActivity.name + ' added');
              eActivity.activity.id = id;
              this.game.activities.push(eActivity.activity);
            }
            eActivity.status = Status.successOrError(id >= 0);
          }
        );
      }
    }
  }

  reloadGameIfDoneUploading() {
    let done: boolean;
    done = Status.isDone(this.nameState);
    for (const activityState of this.activityState) {
      if (!Status.isDone(activityState.status)) {
        done = false;
      }
    }
    for (const activityState of this.activityToDeleteState) {
      if (!Status.isDone(activityState.status)) {
        done = false;
      }
    }
    if (done) {
      this.getGame(false);
    }
  }

  addActivity() {
    this.activityState.push({activity: new Activity, status: Status.NOTHING});
    console.log(this.activityState);
  }

  removeActivity(i: number) {
    const activity = this.activityState.splice(i, 1)[0];
    this.activityToDeleteState.push({activity: activity.activity, status: Status.NOTHING});
    console.log(this.activityState);
    console.log(this.activityToDeleteState);
  }

}
