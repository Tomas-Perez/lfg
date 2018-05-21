import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {BehaviorSubject} from 'rxjs/BehaviorSubject';
import {BasicUser} from '../_models/BasicUser';
import {JsonConvert} from 'json2typescript';
import {Observable} from 'rxjs/Observable';
import {catchError, map, tap} from 'rxjs/operators';
import {User} from '../_models/User';
import {UserService} from './user.service';

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

  constructor(private http: HttpClient, private userService: UserService) {
    this.friendListSubject = new BehaviorSubject<BasicUser[]>([]);
    this.friendListSubject.subscribe(friendList => this.friendList = friendList);

    this.userService.userSubject.subscribe( user => {
      this.user = user;
      if (user !== null) {
        this.updateFriends();
      } else {
        this.friendListSubject.next([]);
      }
    });
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
    this.requestFriends().subscribe(posts => this.friendListSubject.next(posts));
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
        catchError(err => this.requestFriendsErrorHandle(err))
      );
  }

  private getFriendRequestErrorHandle(err: any) {
    console.log('Error getting friend requests');
    return Observable.of(false);
  }

  getSentFriendRequests(): Observable<BasicUser[]> {
    return this.http.get<any>(this.friendRequestSentUrl, {
      observe: 'response'
    })
      .pipe(
        map(response => this.jsonConvert.deserialize(response.body, BasicUser)),
        catchError(err => this.requestFriendsErrorHandle(err))
      );
  }

  private getSentFriendRequestErrorHandle(err: any) {
    console.log('Error getting sent requests');
    return Observable.of(false);
  }

  isInFriendList(id: number): boolean {
    for (const friend of this.friendList) {
      if (friend.id === id) {
        return true;
      }
    }
    return false;
  }
}
