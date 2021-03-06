import { Injectable } from '@angular/core';
import {BehaviorSubject} from 'rxjs/BehaviorSubject';
import {BasicUser} from '../_models/BasicUser';
import {JsonConvert} from 'json2typescript';
import {Observable} from 'rxjs/Observable';
import {catchError, map, tap} from 'rxjs/operators';
import {User} from '../_models/User';
import {UserService} from './user.service';
import {UserSocketService} from './user-socket.service';
import {FriendAction} from '../_models/sockets/FriendAction';
import {OnlineStatus} from '../_models/OnlineStatus';
import {HttpService} from './http.service';
import {ImageService} from './image.service';

@Injectable()
export class FriendService {

  private jsonConvert: JsonConvert = new JsonConvert();
  private userUrl = '/users';
  private friendUrl = '/users/me/friends';
  private friendRequestUrl = '/users/me/friend-requests';
  private friendRequestReceivedUrl = '/users/me/friend-requests/received';
  private friendRequestSentUrl = '/users/me/friend-requests/sent';
  private user: User;
  private friendList: BasicUser[];
  friendListSubject: BehaviorSubject<BasicUser[]>;
  private receivedFriendRequests: BasicUser[];
  receivedRequestsSubject: BehaviorSubject<BasicUser[]>;
  private sentRequestsList: BasicUser[];
  sentRequestsSubject: BehaviorSubject<BasicUser[]>;

  // Buffer for friend status updates before the friend list is retrieved.
  private friendStatusBuffer: {user: BasicUser, status: OnlineStatus}[];

  constructor(private http: HttpService,
              private userService: UserService,
              private imageService: ImageService,
              private userSocketService: UserSocketService) {
    this.friendStatusBuffer = [];
    this.friendList = [];
    this.receivedFriendRequests = [];
    this.sentRequestsList = [];
    this.receivedRequestsSubject = new BehaviorSubject<BasicUser[]>([]);
    this.sentRequestsSubject = new BehaviorSubject<BasicUser[]>([]);
    this.friendListSubject = new BehaviorSubject<BasicUser[]>([]);

    this.setUpUserSubject();
    this.setUpFriendSocket();
  }

  private getImages(list: BasicUser[]) {
    for (const user of list) {
      if (user.image == null) {
        this.getImage(user);
      }
    }
  }

  private getImage(user: BasicUser) {
    this.imageService.getImage(this.userUrl + '/' + user.id + '/image')
      .subscribe(img => user.image = img);
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
          case FriendAction.SENTREQUEST: {
            this.onSentRequest(user);
            break;
          }
          case FriendAction.NEW: {
            this.onDeleteFriend(user);
            this.onNewFriend(user);
            break;
          }
          case FriendAction.DELETE: {
            this.onDeleteFriend(user);
            break;
          }
          case FriendAction.CONNECTED: {
            this.onStatusChanged(user, OnlineStatus.ONLINE);
            break;
          }
          case FriendAction.DISCONNECTED: {
            this.onStatusChanged(user, OnlineStatus.OFFLINE);
            break;
          }
        }
      }
    );
  }

  private onStatusChanged(user: BasicUser, status: OnlineStatus) {
    const i = this.getFriendIndex(user.id, this.friendList);
    if (i >= 0) {
      this.friendList[i].status = status;
    } else {
      this.friendStatusBuffer.push({user: user, status: status});
    }
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
    const i = this.getFriendIndex(id, array);
    if (i >= 0) {
      array.splice(i, 1);
    }
  }

  private getFriendIndex(id: number, array: BasicUser[]) {
    for (let i = 0; i < array.length; i++) {
      if (array[i].id === id) {
        return i;
      }
    }
    return -1;
  }

  private onNewFriend(user: BasicUser) {
    this.friendList.push(user);
    this.friendListSubject.next(this.friendList);
    this.getImage(user);
  }

  private onReceivedRequest(user: BasicUser) {
    this.receivedFriendRequests.push(user);
    this.receivedRequestsSubject.next(this.receivedFriendRequests);
    this.getImage(user);
  }

  private onSentRequest(user: BasicUser) {
    this.sentRequestsList.push(user);
    this.sentRequestsSubject.next(this.sentRequestsList);
    this.getImage(user);
  }

  private clearStatusBuffer() {
    for (const userStatus of this.friendStatusBuffer) {
      const i = this.getFriendIndex(userStatus.user.id, this.friendList);
      if (i >= 0) {
        this.friendList[i].status = userStatus.status;
      }
    }
    this.friendStatusBuffer = [];
  }

  getUserInfo(id: number): Observable<BasicUser> {
    return this.http.get(this.userUrl + '/' + id, {
      observe: 'response'
    })
      .pipe(
        map(response => {
          const user = this.jsonConvert.deserialize(response.body, BasicUser);
          this.getImage(user);
          return user;
        }),
        catchError(err => this.requestFriendsErrorHandle(err))
      );
  }

  requestFriends(): Observable<BasicUser[]> {
    return this.http.get(this.friendUrl, {
      observe: 'response'
    })
      .pipe(
        tap(response => console.log(response)),
        map(response => this.jsonConvert.deserialize(response.body, BasicUser)),
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
      this.getImages(friends);
      this.clearStatusBuffer();
    });
  }

  confirmFriendRequest(id: number): Observable<boolean> {
    return this.http.post(this.friendUrl, id, {
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
    return this.http.delete(this.friendUrl + '/' + id, {
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
    return this.http.post(this.friendRequestUrl, id, {
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
    return this.http.get(this.friendRequestReceivedUrl, {
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
      this.getImages(requests);
    });
  }

  getSentFriendRequests(): Observable<BasicUser[]> {
    return this.http.get(this.friendRequestSentUrl, {
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
      this.getImages(requests);
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

  isFriendOrWillBe(id: number) {
    return this.isInFriendList(id) || this.isInReceivedRequestsList(id) || this.isInSentRequestsList(id);
  }

  searchUsers(search: string): Observable<BasicUser[]> {
    return this.http.get(this.userUrl + '?search=' + search,
      {
        observe: 'response'
      })
      .pipe(
        map(response => {
          console.log(response);
          const users = this.jsonConvert.deserialize(response.body, BasicUser);
          this.getImages(users);
          return users;
        }),
        catchError(err => this.searchUsersErrorHandle(err))
      );
  }

  private searchUsersErrorHandle(err: any): Observable<BasicUser[]> {
    console.log(err);
    return Observable.of([]);
  }
}
