import { Injectable } from '@angular/core';
import {$WebSocket} from 'angular2-websocket/angular2-websocket';
import {AuthService} from './auth.service';
import {UserService} from './user.service';
import {Subject} from 'rxjs/Subject';
import {ChatAction} from '../_models/sockets/ChatAction';
import {FriendAction} from '../_models/sockets/FriendAction';

@Injectable()
export class UserSocketService {

  private userWsUrl = 'ws://localhost:8080/lfg/websockets/user';
  private userWs: $WebSocket;
  chatSubject: Subject<{action: ChatAction, id: number}>; // new|delete, id
  friendSubject: Subject<{action: FriendAction, id: number, username?: string}>;

  constructor(private authService: AuthService, private userService: UserService) {
    this.chatSubject = new Subject();
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
    this.chatSubject.next({action: ChatAction.NEW, id: id});
  }

  onDeleteChat(id: number) {
    this.chatSubject.next({action: ChatAction.DELETE, id: id});
  }

  onFriendConnected(id: number) {
    this.friendSubject.next({action: FriendAction.CONNECTED, id: id});
  }

  onFriendDisconnected(id: number) {
    this.friendSubject.next({action: FriendAction.DISCONNECTED, id: id});
  }

  onNewFriend(id: number, username: string) {
    this.friendSubject.next({action: FriendAction.NEW, id: id, username: username});
  }

  onDeleteFriend(id: number) {
    this.friendSubject.next({action: FriendAction.DELETE, id: id});
  }

  onReceivedFriendRequest(id: number, username: string) {
    this.friendSubject.next({action: FriendAction.RECREQUEST, id: id, username: username});
  }

  onSentFriendRequest(id: number, username: string) {
    this.friendSubject.next({action: FriendAction.SENTREQUEST, id: id, username: username});
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
