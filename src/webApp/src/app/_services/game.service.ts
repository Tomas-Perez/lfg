import { Injectable } from '@angular/core';
import {Observable} from 'rxjs/Observable';
import {HttpClient} from '@angular/common/http';
import {catchError, map, switchMap} from 'rxjs/operators';
import {Game} from '../_models/Game';
import {JsonConvert} from 'json2typescript';
import {DbGame} from '../_models/DbGame';
import {DbActivity} from '../_models/DbActivity';
import {Activity} from '../_models/Activity';

@Injectable()
export class GameService {

  private jsonConvert: JsonConvert = new JsonConvert();
  private gamesUrl = 'http://localhost:8080/lfg/games';
  private activitiesUrl = 'http://localhost:8080/lfg/activities';
  private games: Observable<Game[]>;

  constructor(private http: HttpClient) {
    this.games = this.requestGames();
  }

  gameToDbGame(game: Game): DbGame {
    const dbGame = new DbGame();
    dbGame.name = game.name;
    return dbGame;
  }

  activityToDbActivity(activity: Activity, id: number): DbActivity {
    const dbActivity = new DbActivity();
    dbActivity.name = activity.name;
    dbActivity.gameID = id;
    return dbActivity;
  }

  newGame(game: Game): Observable<number> {
    /*
    console.log(this.jsonConvert.serialize(this.gameToDbGame(game)));
    console.log(this.jsonConvert.serialize(this.gameToDbActivities(game, 1)));

    return Observable.of(false);
    */
    return this.http.post<any>(this.gamesUrl, this.jsonConvert.serialize(this.gameToDbGame(game)), {
      observe: 'response'
    })
      .pipe(
        switchMap(response => {
          const createdGameUrl = response.headers.get('location');
          return this.http.get<any>(createdGameUrl, {
            observe: 'response'
          })
            .pipe(
              switchMap( getGameResponse => {
                  return Observable.of(this.jsonConvert.deserialize(getGameResponse.body, Game).id);
                }
              ),
              catchError((err: any) => this.newGameErrorHandle(-2))
            );
        }),
        catchError((err: any) => this.newGameErrorHandle(-1))
      );
  }

  private newGameErrorHandle(code: any) {
    console.log('Error adding new game');
    console.log(code);
    return Observable.of(code);
  }

  newActivity(activity: DbActivity): Observable<boolean> {
    return this.http.post<any>(this.activitiesUrl, this.jsonConvert.serialize(activity), {
      observe: 'response'
    })
      .pipe(
        map(response => {
          console.log(response);
          return true;
        }),
        catchError((err: any) => this.newActivityErrorHandle(err))
      );
  }

  private newActivityErrorHandle(err: any) {
    console.log('Error adding new Activity');
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
