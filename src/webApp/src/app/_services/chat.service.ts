import { Injectable } from '@angular/core';
import {$WebSocket} from 'angular2-websocket/angular2-websocket';
import {Chat} from '../_models/Chat';
import {BehaviorSubject} from 'rxjs/BehaviorSubject';
import {HttpClient} from '@angular/common/http';
import {catchError, switchMap} from 'rxjs/operators';
import {Observable} from 'rxjs/Observable';
import {JsonConvert} from 'json2typescript';
import {AuthService} from './auth.service';
import {Message} from '../_models/Message';
import {SendMessage} from '../_models/Sockets/SendMessage';

@Injectable()
export class ChatService {

  private chatUrl = 'http://localhost:8080/lfg/chats';
  private chatWsUrl = 'ws://localhost:8080/lfg/websockets/chats/';
  private jsonConvert: JsonConvert = new JsonConvert();
  private chatsWs: Map<number, $WebSocket>;
  private chats: Chat[];
  chatsSubject: BehaviorSubject<Chat[]>;


  constructor(private http: HttpClient, private authService: AuthService) {
    this.chatsWs = new Map();
    this.chats = [];
    this.chatsSubject = new BehaviorSubject<Chat[]>(this.chats);

    this.newChat([2443, 2444]);

  }

  onAvailableUsers(ids: number[], chat: Chat) {
    /*
    console.log("onAvailableUsers");
    chat.updateMembers(ids);
    console.log("updated chat " + chat.id + " users");
    */
  }

  onChatMessage(message: Message, chat: Chat) {
    chat.pushMessage(message);
  }

  onConnectedUser(id: number, chat: Chat) {
    chat.addMember(id);
  }

  onDisconnectedUser(id: number, chat: Chat) {
    chat.removeMember(id);
  }

  newChat(ids: number[]) {
    this.requestNewChat(ids).subscribe((data: {chat: Chat, wsUrl: string}) => {
      const chat = data.chat;
      const wsUrl = data.wsUrl;
      this.chats.push(chat);
      this.chatsSubject.next(this.chats);
      const ws = new $WebSocket(wsUrl + '?access-token=' + this.authService.getAccessToken());

      ws.onMessage(
        (msg: MessageEvent) => {
          const msgData = JSON.parse(msg.data);
          console.log(msgData);
          switch (msgData.type) {
            case 'broadcastTextMessage': {
              this.onChatMessage(this.jsonConvert.deserialize(msgData.payload.message, Message), chat);
              break;
            }
            case 'broadcastAvailableUsers': {
              this.onAvailableUsers(msgData.payload, chat);
              break;
            }
            case 'broadcastConnectedUser': {
              this.onConnectedUser(msgData.payload, chat);
              break;
            }
            case 'broadcastDisconnectedUser': {
              this.onDisconnectedUser(msgData.payload, chat);
              break;
            }
          }
          // TODO
        }
      );

      this.chatsWs.set(chat.id, ws);
    });
  }

  sendMessage(id: number, message: string): boolean{
    const ws = this.chatsWs.get(id);
    if (!ws){
      return false;
    }
    const sendMsg = new SendMessage('sendTextMessage', message);
    ws.send(this.jsonConvert.serialize(sendMsg));
    return true;
  }

  requestNewChat(ids: number[]): Observable<{chat: Chat, wsUrl: string}> {
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
