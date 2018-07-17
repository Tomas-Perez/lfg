import {Component, OnDestroy, OnInit} from '@angular/core';
import {Game} from '../../_models/Game';
import {GameService} from '../../_services/game.service';
import {ActivatedRoute} from '@angular/router';
import {Subject} from 'rxjs/Subject';
import 'rxjs/add/operator/takeUntil';
import {FileHolder} from 'angular2-image-upload';

@Component({
  selector: 'app-game-list',
  templateUrl: './game-list.component.html',
  styleUrls: ['./game-list.component.css']
})
export class GameListComponent implements OnInit, OnDestroy {

  private games: Game[];
  private ngUnsubscribe: Subject<any> = new Subject();
  gameImages: any[];

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
    this.gameImages = [];
    this.gameService.updateGameList();
    this.gameService.gamesSubject.takeUntil(this.ngUnsubscribe).subscribe(games => {
      this.games = games;
      this.getGameImages();
    });
  }

  getGameImages() {
    for (let i = 0; i < this.games.length; i++) {
      this.gameImages.push(null);
      this.gameService.getGameImage(this.games[i].id).subscribe(img => {
        this.gameImages[i] = img;
      });
    }
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
