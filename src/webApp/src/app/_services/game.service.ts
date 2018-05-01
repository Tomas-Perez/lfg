import { Injectable } from '@angular/core';
import {Observable} from 'rxjs/Observable';
import {HttpClient} from '@angular/common/http';
import {catchError, map} from 'rxjs/operators';
import {Game} from '../_models/Game';
import {JsonConvert} from 'json2typescript';
import {DbGame} from '../_models/DbGame';
import {DbActivity} from '../_models/DbActivity';

@Injectable()
export class GameService {

  private jsonConvert: JsonConvert = new JsonConvert();
  private gamesUrl = 'http://localhost:8080/lfg/games';
  private games: Observable<Game[]>;

  constructor(private http: HttpClient) {
    this.games = this.requestGames();

    const game = new DbGame();
    game.name = 'testGame';
    this.newGame(game).subscribe();
  }

  gameToDbGame(game: Game): DbGame {
    const dbGame = new DbGame();
    dbGame.name = game.name;
    return dbGame;
  }


  newGame(game: DbGame): Observable<boolean> {

    return this.http.post<any>(this.gamesUrl, this.jsonConvert.serialize(game), {
      observe: 'response'
    })
      .pipe(
        map(response => {
          console.log(response);
          return true;
        }),
        catchError((err: any) => this.newGameErrorHandle(err))
      );
  }

  private newGameErrorHandle(err: any) {
    console.log('Error adding new game');
    console.log(err);
    return Observable.of(false);
  }

  getGameList(): Observable<Game[]> {
    return this.games;
  }

  requestGames(): Observable<Game[]> {
    return this.http.get<any>(this.gamesUrl,
      {
        observe: 'response'
      })
      .pipe(
        map(response => {
          console.log(response);
          return this.jsonConvert.deserializeArray(response.body, Game);
        }),
        catchError(err => Observable.throw(err))
      );
  }

  getGame(id: number) {
    return this.http.get<any>(this.gamesUrl + '/' + id,
      {
        observe: 'response'
      })
      .pipe(
        map(response => {
          console.log(response);
          return this.jsonConvert.deserialize(response.body, Game);
        }),
        catchError(err => Observable.throw(err))
      );
  }

}
