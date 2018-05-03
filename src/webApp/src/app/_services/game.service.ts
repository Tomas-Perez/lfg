import { Injectable } from '@angular/core';
import {Observable} from 'rxjs/Observable';
import {HttpClient} from '@angular/common/http';
import {catchError, map, switchMap} from 'rxjs/operators';
import {Game} from '../_models/Game';
import {JsonConvert} from 'json2typescript';
import {DbGame} from '../_models/DbGame';
import {DbActivity} from '../_models/DbActivity';
import {Activity} from '../_models/Activity';
import {BehaviorSubject} from 'rxjs/BehaviorSubject';

@Injectable()
export class GameService {

  private jsonConvert: JsonConvert = new JsonConvert();
  private gamesUrl = 'http://localhost:8080/lfg/games';
  private activitiesUrl = 'http://localhost:8080/lfg/activities';
  private games: BehaviorSubject<Game[]>;

  constructor(private http: HttpClient) {
    this.games = new BehaviorSubject([]);
    this.updateGameList();
  }

  getGameList(): BehaviorSubject<Game[]> {
    return this.games;
  }

  /**
   * Requests a new game list and updates this.games
   */
  updateGameList() {
    this.requestGames().subscribe(games => this.games.next(games));
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

  newGame(game: DbGame): Observable<number> {
    return this.http.post<any>(this.gamesUrl, this.jsonConvert.serialize(game), {
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
                  const newGame = this.jsonConvert.deserialize(getGameResponse.body, Game);

                  /*
                  const games = this.games.getValue();
                  games.push(newGame);
                  this.games.next(games);
                  */

                  return Observable.of(newGame.id);
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

  updateGame(game: DbGame, id: number): Observable<boolean> {
    return this.http.post<any>(this.gamesUrl + '/' + id, this.jsonConvert.serialize(game), {
      observe: 'response'
    })
      .pipe(
        map(response => {
          console.log(response);
          return true;
        }),
        catchError((err: any) => this.updateGameErrorHandle(err))
      );
  }

  private updateGameErrorHandle(err: any) {
    console.log('Error updating Game');
    console.log(err);
    return Observable.of(false);
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

  updateActivity(activity: DbActivity, id: number): Observable<boolean> {
    return this.http.post<any>(this.activitiesUrl + '/' + id, this.jsonConvert.serialize(activity), {
      observe: 'response'
    })
      .pipe(
        map(response => {
          console.log(response);
          return true;
        }),
        catchError((err: any) => this.updateActivityErrorHandle(err))
      );
  }

  private updateActivityErrorHandle(err: any) {
    console.log('Error updating Activity');
    console.log(err);
    return Observable.of(false);
  }

  deleteActivity(id: number): Observable<boolean> {
    return this.http.delete<any>(this.activitiesUrl + '/' + id, {
      observe: 'response'
    })
      .pipe(
        map(response => {
          console.log(response);
          return true;
        }),
        catchError((err: any) => this.deleteActivityErrorHandle(err))
      );
  }

  private deleteActivityErrorHandle(err: any) {
    console.log('Error deleting Activity');
    console.log(err);
    return Observable.of(false);
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

  deleteGame(id: number): Observable<boolean> {
    return this.http.delete<any>(this.gamesUrl + '/' + id,
      {
        observe: 'response'
      })
      .pipe(
        map(response => {
          console.log(response);
          let i = 0;
          for (const game of this.games.getValue()) {
            if (game.id === id ) {
              const newGames = this.games.getValue();
              newGames.splice(i, 1);
              this.games.next(newGames);
              return true;
            }
            i++;
          }
        }),
        catchError(err => this.deleteGameErrorHandling(err))
      );
  }

  private deleteGameErrorHandling(err) {
    return Observable.of(false);
  }

}
