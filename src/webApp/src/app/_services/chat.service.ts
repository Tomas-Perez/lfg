import { Injectable } from '@angular/core';
import {$WebSocket, WebSocketSendMode} from 'angular2-websocket/angular2-websocket';
import {Chat} from '../_models/Chat';
import {BehaviorSubject} from 'rxjs/BehaviorSubject';
import {HttpClient} from '@angular/common/http';
import {catchError, map, switchMap} from 'rxjs/operators';
import {Observable} from 'rxjs/Observable';
import {JsonConvert} from 'json2typescript';
import {AuthService} from './auth.service';
import {Message} from '../_models/Message';
import {SendMessage} from '../_models/SendMessage';
import {ChatType} from '../_models/ChatType';
import {User} from '../_models/User';
import {UserService} from './user.service';
import {UserSocketService} from './user-socket.service';

@Injectable()
export class ChatService {

  private user: User;
  private chatUrl = 'http://localhost:8080/lfg/chats';
  private chatWsUrl = 'ws://localhost:8080/lfg/websockets/chats/';
  private jsonConvert: JsonConvert = new JsonConvert();
  private chatsWs: Map<number, $WebSocket>;
  private chats: Chat[];
  chatsSubject: BehaviorSubject<Chat[]>;


  constructor(private http: HttpClient,
              private authService: AuthService,
              private userService: UserService,
              private userSocketService: UserSocketService) {

    this.chatsWs = new Map();
    this.chatsSubject = new BehaviorSubject<Chat[]>([]);
    this.chatsSubject.subscribe(chats => {
      console.log(chats);
      this.chats = chats;
    });

    this.userService.userSubject.subscribe( user => {
      this.user = user;

      // TODO change the way logout is handled
      for (const chat of Array.from(this.chatsWs)) { // close ws
        chat[1].close();
      }
      this.chatsWs = new Map();
      this.chatsSubject.next([]);
      //

      if (user !== null && user.chats) {
        for (const chat of user.chats) {
          if (!this.chatExistsById(chat.id)) {
            this.getChatAndConnect(chat.id)
              .subscribe(newChat => {
                if (newChat != null) {
                  this.addChat(newChat);
                }
              });
          }
        }
      }
    });

    this.userSocketService.newChatSubject.subscribe(
      chatId => {
        if (!this.chatExistsById(chatId)) {
          this.getChatAndConnect(chatId).subscribe(chat => this.addChat(chat));
        }
      }
    );
  }

  private addChat(chat: Chat) {
    this.chatsSubject.next(this.chats.concat([chat]));
  }

  getChatAndConnect(id: number): Observable<Chat> {
    return this.getChat(id).pipe(
      map(chat => {
        if (chat != null) {
          this.connectToChat(chat, this.chatWsUrl, true);
          return chat;
        }
      })
    );
  }

  getChat(id: number): Observable<Chat> {
    return this.http.get<any>(this.chatUrl + '/' + id, {
      observe: 'response'
    })
      .pipe(
        map( getPostResponse => {
          console.log(getPostResponse);
          return this.jsonConvert.deserialize(getPostResponse.body, Chat);
        }),
        catchError((err: any) => this.getChatErrorHandle(err))
      );
  }

  private getChatErrorHandle(code: any) {
    console.log('Error creating new chat code: ' + code);
    return Observable.of(null);
  }

  /**
   * Checks if a chat with the same members exists, returns true if it does.
   * @param {number[]} ids
   * @returns {boolean}
   */
  chatExistsByIds(ids: number[]): boolean {
    for (const chat of this.chats) {
      let checks = false;
      for (const chatMember of chat.members) {
        checks = false;
        for (const id of ids) {
          if (id === chatMember.id) {
            checks = true;
            break;
          }
        }

        if (!checks) {
          break;
        }
      }

      if (checks) {
        return true;
      }
    }
    return false;
  }

  chatExistsById(id: number): boolean {
    for (const chat of this.chats) {
      if (chat.id === id) {
        return true;
      }
    }
    return false;
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

  connectToChat(chat: Chat, wsUrl: string, addId: boolean) { // TODO handle error
    const ws = new $WebSocket(wsUrl + (addId ? chat.id : '') + '?access-token=' + this.authService.getAccessToken());

    ws.onMessage(
    (msg: MessageEvent) => {
      const msgData = JSON.parse(msg.data);
      console.log(msgData);
      switch (msgData.type) {
        case 'broadcastTextMessage': {
          this.onChatMessage(this.jsonConvert.deserialize(msgData.payload, Message), chat);
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
    });

    this.chatsWs.set(chat.id, ws);
  }

  newChat(type: ChatType, ids: number[]): boolean {

    if (this.chatExistsByIds(ids)) { return false; }
    console.log("asdf");

    this.requestNewChat(type, ids).subscribe((data: {chat: Chat, wsUrl: string}) => {
      const chat = data.chat;
      const wsUrl = data.wsUrl;
      this.addChat(chat);
      this.connectToChat(chat, wsUrl, false);
    });
    return true;
  }

  sendMessage(id: number, message: string): boolean {
    const ws = this.chatsWs.get(id);
    if (!ws) {
      return false;
    }
    const sendMsg = new SendMessage('sendTextMessage', message);
    ws.send(this.jsonConvert.serialize(sendMsg)).subscribe();

    return true;
  }

  requestNewChat(type: ChatType, ids: number[]): Observable<{chat: Chat, wsUrl: string}> {
    return this.http.post<any>(this.chatUrl, {type: type, members: ids} , {
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

  deleteChat(id: number): Observable<boolean> {
     return this.http.post<any>(this.chatUrl + '/' + id, {close: true, memberID: this.user.id}, {
       observe: 'response'
     })
       .pipe(
         map(response => {
           console.log(response);
           for (let i = 0; i < this.chats.length; i++) {
            if (this.chats[i].id === id) {
              this.chats.splice(i, 1); // TODO should be immutable
              this.chatsSubject.next(this.chats);
              this.closeAndDeleteWs(id);
              return;
            }
           }
           return true;
         }),
         catchError((err: any) => this.deleteChatErrorHandle(err))
      );
  }

  private deleteChatErrorHandle(err: any) {
    console.log('Error deleting chat');
    console.log(err);
    return Observable.of(null);
  }

  closeAndDeleteWs(id: number) {
    this.chatsWs.get(id).close();
    this.chatsWs.delete(id);
  }

}
