import { Injectable } from '@angular/core';
import {$WebSocket} from 'angular2-websocket/angular2-websocket';
import {AuthService} from './auth.service';
import {UserService} from './user.service';
import {Subject} from 'rxjs/Subject';
import {ChatAction} from '../_models/sockets/ChatAction';
import {FriendAction} from '../_models/sockets/FriendAction';
import {WsService} from './ws.service';
import {GroupAction} from '../_models/sockets/GroupAction';
import {PostAction} from '../_models/sockets/PostAction';

@Injectable()
export class UserSocketService {

  private userWsUrl: string;
  private userWs: $WebSocket;
  chatSubject: Subject<{action: ChatAction, id: number}>; // new|delete, id
  friendSubject: Subject<{action: FriendAction, id: number, username?: string}>;
  groupSubject: Subject<{action: GroupAction, id: number}>;
  postSubject: Subject<{action: PostAction, id: number}>;

  constructor(private wsService: WsService, private authService: AuthService, private userService: UserService) {
    this.userWsUrl = this.wsService.getUrl('/user');
    this.chatSubject = new Subject();
    this.friendSubject = new Subject();
    this.groupSubject = new Subject();
    this.postSubject = new Subject();
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

  onDeleteGroup(id: number) {
    this.groupSubject.next({action: GroupAction.DELETE, id: id});
  }

  onNewGroupPost(id: number) {
    this.postSubject.next({action: PostAction.NEW, id: id});
  }

  onDeleteGroupPost(id: number) {
    this.postSubject.next({action: PostAction.DELETE, id: id});
  }

  onNewPost(id: number) {
    this.postSubject.next({action: PostAction.NEW, id: id});
  }

  onDeletePost(id: number) {
    this.postSubject.next({action: PostAction.DELETE, id: id});
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
          case 'newGroup': {
            break;
          }
          case 'deleteGroup': {
            this.onDeleteGroup(msgData.payload.id);
            break;
          }
          case 'newPost': {
            // this.onNewPost(msgData.payload.id); is already added on http response
            break;
          }
          case 'deletePost': {
            this.onDeletePost(msgData.payload.id);
            break;
          }
          case 'newGroupPost': {
            this.onNewGroupPost(msgData.payload.id);
            break;
          }
          case 'deleteGroupPost': {
            this.onDeleteGroupPost(msgData.payload.id);
            break;
          }
        }
      });

    this.userWs = ws;
  }

}
