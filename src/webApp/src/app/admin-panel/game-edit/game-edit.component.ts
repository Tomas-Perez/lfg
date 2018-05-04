import {Component, OnInit} from '@angular/core';
import {Game} from '../../_models/Game';
import {ActivatedRoute, Router} from '@angular/router';
import {GameService} from '../../_services/game.service';
import 'rxjs/add/operator/switchMap';
import {Activity} from '../../_models/Activity';
import {Status} from '../../_models/Status';
import {DbGame} from '../../_models/DbGame';
import {Location} from "@angular/common";

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
    private router: Router,
    private location: Location
  ) { }

  ngOnInit() {
    this.nameState = Status.NOTHING;
    this.activityToDeleteState = [];
    this.deleteActivityCount = 0;
    this.getGame(true);
  }

  getGame(firsTime: boolean) {
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
      .catch(err => this.location.back());
  }

  setUpActivityState() {
    this.activityState = [];
    for (let i = 0; i < this.game.activities.length; i++) {
      this.activityState.push({activity: JSON.parse(JSON.stringify(this.game.activities[i])), status: Status.NOTHING});
    }
    console.log(this.activityState);
  }

  restartActivityStateAndTrim() {
    for (let i = 0; i < this.activityState.length; i++) {
      this.activityState[i].status = Status.NOTHING;
      this.activityState[i].activity.name = this.activityState[i].activity.name.trim();
    }
  }

  updateGame() {
    this.restartActivityStateAndTrim();

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
      console.log(activity.name);
      for (const eActivity of this.activityState) {
        if (activity.id === eActivity.activity.id) {
          console.log(eActivity.activity.name);
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
              this.game.activities.push(JSON.parse(JSON.stringify(eActivity.activity)));
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
    const array = JSON.parse(JSON.stringify(this.activityState));
    array.push({activity: new Activity(''), status: Status.NOTHING});
    this.activityState = array;
  }

  removeActivity(i: number) {
    const activitySt = this.activityState.splice(i, 1)[0];
    if (activitySt.activity.name.trim()) {
      this.activityToDeleteState.push({activity: activitySt.activity, status: Status.NOTHING});
    }
    console.log(this.activityToDeleteState);
  }

  getStatus(status: Status): string {
    return Status[status];
  }

}
