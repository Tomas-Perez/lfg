import {Component, OnDestroy, OnInit} from '@angular/core';
import {Game} from '../../_models/Game';
import {GameService} from '../../_services/game.service';
import {ActivatedRoute, ParamMap} from '@angular/router';
import {Subject} from 'rxjs/Subject';
import {Observable} from 'rxjs/Observable';
import 'rxjs/add/operator/takeUntil';

@Component({
  selector: 'app-game-list',
  templateUrl: './game-list.component.html',
  styleUrls: ['./game-list.component.css']
})
export class GameListComponent implements OnInit, OnDestroy {

  private games: Game[];
  private ngUnsubscribe: Subject<any> = new Subject();

  constructor(
    private gameService: GameService,
    private route: ActivatedRoute
  ) { }

  ngOnInit() {
    /*
    this.route.paramMap
      .switchMap((params: ParamMap) => {
        //this.selectedId = +params.get('id');
      });
      */
    this.gameService.getGameList().takeUntil(this.ngUnsubscribe).subscribe(games => this.games = games);
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

  ngOnDestroy(): void {
    this.ngUnsubscribe.next();
    this.ngUnsubscribe.complete();
  }

}
