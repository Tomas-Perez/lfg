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
  fileHolder: FileHolder;
  imgState: boolean;

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
    this.fileHolder = null;
    this.gameService.updateGameList();
    this.gameService.gamesSubject.takeUntil(this.ngUnsubscribe).subscribe(games => this.games = games);
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

  onImgUploadFinished(file: any) {
    console.log(file);
    this.imgState = true;
    this.fileHolder = file;
  }

  onImgRemoved(file: FileHolder) {
    this.imgState = false;
    this.fileHolder = null;
  }

  onImgUpload(id: number) {
    if (this.fileHolder == null) {
      return;
    }
    this.gameService.updateGameImage(id, this.fileHolder.file);
  }

  ngOnDestroy(): void {
    this.ngUnsubscribe.next();
    this.ngUnsubscribe.complete();
  }

}
