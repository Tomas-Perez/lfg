import { Injectable } from '@angular/core';
import {$WebSocket} from 'angular2-websocket/angular2-websocket';
import {Chat} from '../_models/Chat';
import {BehaviorSubject} from 'rxjs/BehaviorSubject';
import {HttpClient} from '@angular/common/http';
import {catchError, map, switchMap} from 'rxjs/operators';
import {Observable} from 'rxjs/Observable';
import {JsonConvert} from 'json2typescript';
import {AuthService} from './auth.service';

@Injectable()
export class ChatService {

  private chatUrl = 'http://localhost:8080/lfg/chats';
  private chatWsUrl = 'ws://localhost:8080/lfg/websockets/chats/';
  private jsonConvert: JsonConvert = new JsonConvert();
  private chatsWs: $WebSocket[]; // TODO map with chat id?
  private chats: Chat[];
  chatsSubject: BehaviorSubject<Chat[]>;


  constructor(private http: HttpClient, private authService: AuthService) {
    this.chatsWs = [];
    this.chats = [];
    this.chatsSubject = new BehaviorSubject<Chat[]>(this.chats);

    this.newChat([2443, 2444])

  }

  newChat(ids: number[]){
    this.requestNewChat(ids).subscribe((data: {chat: Chat, wsUrl: string}) => {
      this.chats.push(data.chat);
      this.chatsSubject.next(this.chats);
      const ws = new $WebSocket(data.wsUrl + '?access-token=' + this.authService.getAccessToken());

      ws.onMessage(
        (msg: MessageEvent)=> {
          console.log("onMessage ", msg.data);
          // TODO
        }
      );

      // TODO subscribe to ws
      this.chatsWs.push(ws);
    })
  }

  requestNewChat(ids: number[]): Observable<{chat: Chat, wsUrl: string}>{
    return this.http.post<any>(this.chatUrl, {members: ids} , {
      observe: 'response'
    })
      .pipe(
        switchMap(response => {
          const chatWsUrl = response.body.socketPath;
          console.log(response);
          console.log(chatWsUrl);
          const createdChatUrl = response.headers.get('location');
          return this.http.get<any>(createdChatUrl, {
            observe: 'response'
          })
            .pipe(
              switchMap( getGameResponse => {
                  const newChat = this.jsonConvert.deserialize(getGameResponse.body, Chat);
                  return Observable.of({chat: newChat, wsUrl: chatWsUrl});
                }
              ),
              catchError((err: any) => this.newChatErrorHandle(-2))
            );
        }),
        catchError((err: any) => this.newChatErrorHandle(-1))
      );
  }

  private newChatErrorHandle(code: any) {
    console.log('Error creating new chat code: ' + code);
    return Observable.of(null);
  }

}
