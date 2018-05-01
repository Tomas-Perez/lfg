import { Component, OnInit } from '@angular/core';
import {Game} from '../../_models/Game';
import {GameService} from '../../_services/game.service';
import {Observable} from 'rxjs/Observable';
import {ActivatedRoute, ParamMap} from '@angular/router';

@Component({
  selector: 'app-game-list',
  templateUrl: './game-list.component.html',
  styleUrls: ['./game-list.component.css']
})
export class GameListComponent implements OnInit {

  // private games: Game[];
  private games$: Observable<Game[]>;

  constructor(
    private gameService: GameService,
    private route: ActivatedRoute
  ) { }

  ngOnInit() {
    /*
    this.gameService.getGameList().subscribe(games => {
        this.games = games;
      }
    );
    */
    this.games$ = this.route.paramMap
      .switchMap((params: ParamMap) => {
        //this.selectedId = +params.get('id');
        return this.gameService.getGameList();
      });
  }

  deleteGame(id: number) {
    this.gameService.deleteGame(id).subscribe(
      response => {
        if (response) {
          console.log('Game deleted'); // TODO notify
        } else {
          console.log('Error deleting game');
        }
      }
    );
  }

}
