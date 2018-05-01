import { Component, OnInit } from '@angular/core';
import {Game} from '../../_models/Game';
import {Activity} from '../../_models/Activity';
import {GameService} from '../../_services/game.service';

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
    console.log(this.game.activities);
  }

  addActivity() {
    this.game.activities.push(new Activity);
  }

  removeActivity(i: number) {
    this.game.activities.splice(i, 1);
  }

  newGame() {
    console.log(this.game);
  }

}
