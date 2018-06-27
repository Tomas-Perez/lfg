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
      } else {
        if (this.userWs) {
          this.userWs.close();
        }
      }
    });
  }


  onNewChat(id: number) {
    this.newChatSubject.next(id);
  }

  onDeleteChat(id: number) {

  }

  onFriendConnected(id: number) {

  }

  onFriendDisconnected(id: number) {

  }


  onNewFriend(id: number, username: string) {

  }


  onDeleteFriend(id: number) {

  }


  onReceivedFriendRequest(id: number, username: string) {

  }


  onSentFriendRequest(id: number, username: string) {

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
            this.onDeleteChat(msgData.payload.id);
            break;
          }
          case 'friendConnected': {
            this.onFriendConnected(msgData.payload.id);
            break;
          }
          case 'friendDisconnected': {
            this.onFriendDisconnected(msgData.payload.id);
            break;
          }
          case 'newFriend': {
            this.onNewFriend(msgData.payload.id, msgData.payload.username);
            break;
          }
          case 'deleteFriend': {
            this.onDeleteFriend(msgData.payload.id);
            break;
          }
          case 'receivedFriendRequest': {
            this.onReceivedFriendRequest(msgData.payload.id, msgData.payload.username);
            break;
          }
          case 'sentFriendRequest': {
            this.onSentFriendRequest(msgData.payload.id, msgData.payload.username);
            break;
          }
        }
      });

    this.userWs = ws;
  }

}
