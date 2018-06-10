import { Injectable } from '@angular/core';
import {$WebSocket} from 'angular2-websocket/angular2-websocket';
import {AuthService} from './auth.service';
import {UserService} from './user.service';
import {Subject} from 'rxjs/Subject';

@Injectable()
export class UserSocketService {

  private userWsUrl = 'ws://localhost:8080/lfg/websockets/user';
  private userWs: $WebSocket;
  newChatSubject: Subject<number>; // new chat id

  constructor(private authService: AuthService, private userService: UserService) {
    this.newChatSubject = new Subject();
    this.userService.userSubject.subscribe( user => {
      if (user != null) {
        this.connect();
      } else {}
    });
  }


  onNewChat(id: number) {
    this.newChatSubject.next(id);
  }

  private connect() { // TODO handle error
    const ws = new $WebSocket(this.userWsUrl + '?access-token=' + this.authService.getAccessToken());

    ws.onMessage(
      (msg: MessageEvent) => {
        const msgData = JSON.parse(msg.data);
        console.log(msgData);
        switch (msgData.type) {
          case 'newChat': {
            this.onNewChat(msgData.payload.id);
            break;
          }
          case 'deleteChat': {
            break;
          }
          case 'friendConnected': {
            break;
          }
          case 'friendDisconnected': {
            break;
          }
          case 'newFriend': {
            break;
          }
          case 'deleteFriend': {
            break;
          }
          case 'receivedFriendRequest': {
            break;
          }
          case 'sentFriendRequest': {
            break;
          }
        }
      });

    this.userWs = ws;
  }

}
