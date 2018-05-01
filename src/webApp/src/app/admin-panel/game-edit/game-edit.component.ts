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
    // TODO request update game
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
