import { Injectable } from '@angular/core';
import {JsonConvert} from 'json2typescript';
import {BehaviorSubject} from 'rxjs/BehaviorSubject';
import {GamePlatform} from '../_models/GamePlatform';
import {ChatPlatform} from '../_models/ChatPlatform';
import {Observable} from 'rxjs/Observable';
import {HttpService} from './http.service';
import {catchError, map} from 'rxjs/operators';

@Injectable()
export class PlatformService {

  private jsonConvert: JsonConvert = new JsonConvert();
  private gamePlatformsUrl = '/game-platforms';
  private chatPlatformsUrl = '/chat-platforms';
  private gamePlatformsList: GamePlatform[];
  private chatPlatformsList: ChatPlatform[];
  gamePlatformsSubject: BehaviorSubject<GamePlatform[]>;
  chatPlatformsSubject: BehaviorSubject<ChatPlatform[]>;

  constructor(private http: HttpService) {
    this.gamePlatformsList = [];
    this.chatPlatformsList = [];
    this.gamePlatformsSubject = new BehaviorSubject([]);
    this.chatPlatformsSubject = new BehaviorSubject([]);
    this.updateGamePlatformsList();
    this.updateChatPlatformsList();
  }

  updateGamePlatformsList() {
    this.requestGamePlatforms().subscribe(platforms => {
      this.gamePlatformsList = platforms;
      this.gamePlatformsSubject.next(this.gamePlatformsList);
    });
  }

  updateChatPlatformsList() {
    this.requestChatPlatforms().subscribe(platforms => {
      this.chatPlatformsList = platforms;
      this.chatPlatformsSubject.next(this.chatPlatformsList);
    });
  }

  private requestGamePlatforms(): Observable<GamePlatform[]> {
    return this.http.get(this.gamePlatformsUrl,
      {
        observe: 'response'
      })
      .pipe(
        map(response => {
          console.log(response);
          return this.jsonConvert.deserializeArray(response.body, GamePlatform);
        }),
        catchError(err => this.requestGamePlatformsErrorHandle(err))
      );
  }

  private requestGamePlatformsErrorHandle(err: any) {
    console.log('Error getting game platforms');
    console.log(err);
    return Observable.of([]);
  }

  private requestChatPlatforms(): Observable<ChatPlatform[]> {
    return this.http.get(this.chatPlatformsUrl,
      {
        observe: 'response'
      })
      .pipe(
        map(response => {
          console.log(response);
          return this.jsonConvert.deserializeArray(response.body, ChatPlatform);
        }),
        catchError(err => this.requestChatPlatformsErrorHandle(err))
      );
  }

  private requestChatPlatformsErrorHandle(err: any) {
    console.log('Error getting chat platforms');
    console.log(err);
    return Observable.of([]);
  }
}
