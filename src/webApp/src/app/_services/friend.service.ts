import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {BehaviorSubject} from 'rxjs/BehaviorSubject';
import {BasicUser} from '../_models/BasicUser';
import {JsonConvert} from 'json2typescript';
import {Observable} from 'rxjs/Observable';
import {catchError, map, tap} from 'rxjs/operators';
import {User} from '../_models/User';
import {UserService} from './user.service';
import {UserSocketService} from './user-socket.service';
import {ChatAction} from '../_models/sockets/ChatAction';
import {FriendAction} from '../_models/sockets/FriendAction';

@Injectable()
export class FriendService {

  private jsonConvert: JsonConvert = new JsonConvert();
  private userUrl = 'http://localhost:8080/lfg/users';
  private friendUrl = 'http://localhost:8080/lfg/users/me/friends';
  private friendRequestUrl = 'http://localhost:8080/lfg/users/me/friend-requests';
  private friendRequestReceivedUrl = 'http://localhost:8080/lfg/users/me/friend-requests/received';
  private friendRequestSentUrl = 'http://localhost:8080/lfg/users/me/friend-requests/sent';
  private user: User;
  private friendList: BasicUser[];
  friendListSubject: BehaviorSubject<BasicUser[]>;
  private receivedFriendRequests: BasicUser[];
  receivedRequestsSubject: BehaviorSubject<BasicUser[]>;
  private sentRequestsList: BasicUser[];
  sentRequestsSubject: BehaviorSubject<BasicUser[]>;

  constructor(private http: HttpClient, private userService: UserService, private userSocketService: UserSocketService) {
    this.friendList = [];
    this.receivedFriendRequests = [];
    this.sentRequestsList = [];
    this.friendListSubject = new BehaviorSubject<BasicUser[]>([]);
    this.receivedRequestsSubject = new BehaviorSubject<BasicUser[]>([]);
    this.sentRequestsSubject = new BehaviorSubject<BasicUser[]>([]);

    this.setUpUserSubject();
    this.setUpFriendSocket();
  }

  private setUpUserSubject() {
    this.userService.userSubject.subscribe( user => {
      this.user = user;
      if (user !== null) {
        this.updateFriends();
        this.updateFriendRequests();
        this.updateSentRequests();
      } else {
        this.friendList = [];
        this.friendListSubject.next([]);
      }
    });
  }

  private setUpFriendSocket() {
    this.userSocketService.friendSubject.subscribe(
      (element: {action: FriendAction, id: number, username?: string}) => {
        const user = new BasicUser(element.id, element.username);
        switch (element.action) {
          case FriendAction.RECREQUEST: {
            this.onReceivedRequest(user);
            break;
          }
          case FriendAction.NEW: {
            this.onNewFriend(user);
            break;
          }
          case FriendAction.DELETE: {
            this.onDeleteFriend(user);
            break;
          }
        }
      }
    );
  }

  private onDeleteFriend(user: BasicUser) {
    if (this.isInSentRequestsList(user.id)) {
      this.deleteFriend(user.id, this.sentRequestsList);
      this.sentRequestsSubject.next(this.sentRequestsList);
    } else if (this.isInReceivedRequestsList(user.id)) {
      this.deleteFriend(user.id, this.receivedFriendRequests);
      this.receivedRequestsSubject.next(this.receivedFriendRequests);
    } else {
      this.deleteFriend(user.id, this.friendList);
      this.friendListSubject.next(this.friendList);
    }
  }

  private deleteFriend(id: number, array: BasicUser[]) {
    for (let i = 0; i < array.length; i++) {
      if (array[i].id === id) {
        array.splice(i, 1);
      }
    }
  }

  private onNewFriend(user: BasicUser) {
    this.friendList.push(user);
    this.friendListSubject.next(this.friendList);
  }

  private onReceivedRequest(user: BasicUser) {
    this.receivedFriendRequests.push(user);
    this.receivedRequestsSubject.next(this.receivedFriendRequests);
  }

  getUserInfo(id: number): Observable<BasicUser> { // TODO when backend is ready
    return this.http.get<any>(this.userUrl + '/' + id, {
      observe: 'response'
    })
      .pipe(
        map(response => this.jsonConvert.deserialize(response.body, BasicUser)),
        catchError(err => this.requestFriendsErrorHandle(err))
      );
  }

  requestFriends(): Observable<BasicUser[]> {
    return this.http.get<any>(this.friendUrl, {
      observe: 'response'
    })
      .pipe(
        map(response => this.jsonConvert.deserialize(response.body, BasicUser)),
        tap(response => console.log(response)),
        catchError(err => this.requestFriendsErrorHandle(err))
      );
  }

  private requestFriendsErrorHandle(err: any) {
    console.log('Error requesting friends');
    console.log(err);
    return Observable.of([]);
  }

  updateFriends(): void {
    this.requestFriends().subscribe(friends => {
      this.friendList = friends;
      this.friendListSubject.next(friends);
    });
  }

  confirmFriendRequest(id: number): Observable<boolean> {
    return this.http.post<any>(this.friendUrl, id, {
      observe: 'response'
    })
      .pipe(
        map(response => {
          console.log(response);
          return true;
        }),
        catchError((err: any) => this.confirmFriendRequestErrorHandle(err))
      );
  }

  private confirmFriendRequestErrorHandle(err: any) {
    console.log('Error confirming request');
    return Observable.of(false);
  }

  removeFriend(id: number): Observable<boolean> {
    return this.http.delete<any>(this.friendUrl + '/' + id, {
      observe: 'response'
    })
      .pipe(
        map(response => {
          console.log(response);
          return true;
        }),
        catchError((err: any) => this.removeFriendErrorHandle(err))
      );
  }

  private removeFriendErrorHandle(err: any) {
    console.log('Error confirming request');
    return Observable.of(false);
  }

  sendFriendRequest(id: number): Observable<boolean> {
    return this.http.post<any>(this.friendRequestUrl, id, {
      observe: 'response'
    })
      .pipe(
        map(response => {
          console.log(response);
          return true;
        }),
        catchError((err: any) => this.sendFriendRequestErrorHandle(err))
      );
  }

  private sendFriendRequestErrorHandle(err: any) {
    console.log('Error confirming request');
    return Observable.of(false);
  }

  getFriendRequests(): Observable<BasicUser[]> {
    return this.http.get<any>(this.friendRequestReceivedUrl, {
      observe: 'response'
    })
      .pipe(
        map(response => this.jsonConvert.deserialize(response.body, BasicUser)),
        catchError(err => this.getFriendRequestErrorHandle(err))
      );
  }

  private getFriendRequestErrorHandle(err: any) {
    console.log('Error getting friend requests');
    return Observable.of(false);
  }

  updateFriendRequests(): void {
    this.getFriendRequests().subscribe(requests => {
      this.receivedFriendRequests = requests;
      this.receivedRequestsSubject.next(requests);
    });
  }

  getSentFriendRequests(): Observable<BasicUser[]> {
    return this.http.get<any>(this.friendRequestSentUrl, {
      observe: 'response'
    })
      .pipe(
        map(response => this.jsonConvert.deserialize(response.body, BasicUser)),
        catchError(err => this.getSentFriendRequestErrorHandle(err))
      );
  }

  private getSentFriendRequestErrorHandle(err: any) {
    console.log('Error getting sent requests');
    return Observable.of(false);
  }

  updateSentRequests(): void {
    this.getSentFriendRequests().subscribe(requests => {
      this.sentRequestsList = requests;
      this.sentRequestsSubject.next(requests);
    });
  }

  isInFriendList(id: number): boolean {
    for (const friend of this.friendList) {
      if (friend.id === id) {
        return true;
      }
    }
    return false;
  }

  isInReceivedRequestsList(id: number): boolean {
    for (const friend of this.receivedFriendRequests) {
      if (friend.id === id) {
        return true;
      }
    }
    return false;
  }

  isInSentRequestsList(id: number): boolean {
    for (const friend of this.sentRequestsList) {
      if (friend.id === id) {
        return true;
      }
    }
    return false;
  }

}
