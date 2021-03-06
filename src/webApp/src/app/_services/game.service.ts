import { Injectable } from '@angular/core';
import {Observable} from 'rxjs/Observable';
import {catchError, map, switchMap} from 'rxjs/operators';
import {Game} from '../_models/Game';
import {JsonConvert} from 'json2typescript';
import {DbGame} from '../_models/DbModels/DbGame';
import {DbActivity} from '../_models/DbModels/DbActivity';
import {Activity} from '../_models/Activity';
import {BehaviorSubject} from 'rxjs/BehaviorSubject';
import {HttpService} from './http.service';
import {ImageService} from './image.service';

@Injectable()
export class GameService {

  private jsonConvert: JsonConvert = new JsonConvert();
  private gamesUrl = '/games';
  private activitiesUrl = '/activities';
  gamesSubject: BehaviorSubject<Game[]>;

  constructor(private http: HttpService, private imageService: ImageService) {
    this.gamesSubject = new BehaviorSubject([]);
    this.updateGameList();
  }

  /**
   * Requests a new game list and updates this.gamesSubject
   */
  updateGameList() {
    this.requestGames().subscribe(games => this.gamesSubject.next(games));
  }

  gameToDbGame(game: Game): DbGame {
    const dbGame = new DbGame();
    dbGame.name = game.name;
    return dbGame;
  }

  activityToDbActivity(activity: Activity, gameID: number): DbActivity {
    const dbActivity = new DbActivity();
    dbActivity.name = activity.name;
    dbActivity.gameID = gameID;
    return dbActivity;
  }

  requestGames(): Observable<Game[]> {
    return this.http.get(this.gamesUrl,
      {
        observe: 'response'
      })
      .pipe(
        map(response => {
          console.log(response);
          return this.jsonConvert.deserializeArray(response.body, Game);
        }),
        catchError(err => this.requestGamesErrorHandle(err))
      );
  }

  private requestGamesErrorHandle(err: any) {
    console.log('Error getting games');
    console.log(err);
    return Observable.of([]);
  }

  newGame(game: DbGame): Observable<number> {
    return this.http.post(this.gamesUrl, this.jsonConvert.serialize(game), {
      observe: 'response'
    })
      .pipe(
        switchMap(response => {
          const createdGameUrl = response.headers.get('location');
          return this.http.get(createdGameUrl, {
            observe: 'response'
          }, true)
            .pipe(
              switchMap( getGameResponse => {
                  const newGame = this.jsonConvert.deserialize(getGameResponse.body, Game);

                  /*
                  const gamesSubject = this.gamesSubject.getValue();
                  gamesSubject.push(newGame);
                  this.gamesSubject.next(gamesSubject);
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
    console.log('Error adding new game code: ' + code);
    return Observable.of(code);
  }

  updateGame(game: DbGame, id: number): Observable<boolean> {
    return this.http.post(this.gamesUrl + '/' + id, this.jsonConvert.serialize(game), {
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
    return Observable.of(false);
  }

  newActivityWithId(activity: DbActivity): Observable<number> {
    return this.http.post(this.activitiesUrl, this.jsonConvert.serialize(activity), {
      observe: 'response'
    })
      .pipe(
        switchMap(response => {
          const createdActivityUrl = response.headers.get('location');
          return this.http.get(createdActivityUrl, {
            observe: 'response'
          }, true)
            .pipe(
              switchMap( getActivityResponse => {
                  const newActivity = this.jsonConvert.deserialize(getActivityResponse.body, DbActivity);
                  return Observable.of(newActivity.id);
                }
              ),
              catchError((err: any) => this.newActivityWithIdErrorHandle(-2))
            );
        }),
        catchError((err: any) => this.newActivityWithIdErrorHandle(-1))
      );
  }

  private newActivityWithIdErrorHandle(code: any) {
    console.log('Error adding new activity');
    return Observable.of(code);
  }

  newActivity(activity: DbActivity): Observable<boolean> {
    return this.http.post(this.activitiesUrl, this.jsonConvert.serialize(activity), {
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
    return this.http.post(this.activitiesUrl + '/' + id, this.jsonConvert.serialize(activity), {
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
    return this.http.delete(this.activitiesUrl + '/' + id, {
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


  getGame(id: number): Observable<Game> {
    return this.http.get(this.gamesUrl + '/' + id,
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
    return this.http.delete(this.gamesUrl + '/' + id,
      {
        observe: 'response'
      })
      .pipe(
        map(response => {
          console.log(response);
          let i = 0;
          for (const game of this.gamesSubject.getValue()) {
            if (game.id === id ) {
              const newGames = this.gamesSubject.getValue();
              newGames.splice(i, 1);
              this.gamesSubject.next(newGames);
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

  updateGameImage(id: number, image: any) {
    this.imageService.uploadImage(this.gamesUrl + '/' + id + '/image', image).subscribe(res => {
      if (res) { }
    });
  }

  getGameImage(id: number): Observable<any> {
    return this.imageService.getImage(this.gamesUrl + '/' + id + '/image');
  }

  getAndAsignGameImage(game: Game) {
    this.getGameImage(game.id).subscribe( img => game.image = img);
  }

  deleteGameImage(id: number) {
    return this.http.delete(this.gamesUrl + '/' + id + '/image', {
      observe: 'response'
    })
      .pipe(
        map(response => {
          console.log(response);
          return true;
        }),
        catchError((err: any) => this.deleteGameImageErrorHandling(err))
      );
  }

  private deleteGameImageErrorHandling(err) {
    return Observable.of(false);
  }

}
