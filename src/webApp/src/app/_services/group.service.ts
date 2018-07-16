import { Injectable } from '@angular/core';
import {JsonConvert} from 'json2typescript';
import {DbGroup} from '../_models/DbModels/DbGroup';
import {Observable} from 'rxjs/Observable';
import {catchError, map, switchMap} from 'rxjs/operators';
import {Group} from '../_models/Group';
import {BehaviorSubject} from 'rxjs/BehaviorSubject';
import {UserService} from './user.service';
import {User} from '../_models/User';
import {HttpService} from './http.service';
import {$WebSocket} from 'angular2-websocket/angular2-websocket';
import {WsService} from './ws.service';
import {AuthService} from './auth.service';
import {UserSocketService} from './user-socket.service';
import {GroupAction} from '../_models/sockets/GroupAction';

@Injectable()
export class GroupService {

  private jsonConvert: JsonConvert = new JsonConvert();
  private groupsUrl = '/groups';
  private groupWsUrl: string;
  private currentGroup: Group;
  private user: User;
  private groupWs: $WebSocket;
  currentGroupSubject: BehaviorSubject<Group>;

  constructor(private http: HttpService,
              private userService: UserService,
              private userSocketService: UserSocketService,
              private wsService: WsService,
              private authService: AuthService
  ) {
    this.groupWsUrl = this.wsService.getUrl('/groups/');
    this.currentGroup = null;
    this.currentGroupSubject = new BehaviorSubject<Group>(null);
    this.currentGroupSubject.subscribe(group => {
      if (group != null) {
        this.connectToGroupWs(group.id);
      } else {
        if (this.groupWs) {
          this.groupWs.close();
        }
      }
    });

    this.userService.userSubject.subscribe( user => {
      this.user = user;
      if (user !== null && user.groups.length) {
        this.updateGroup(user.groups[0].id).subscribe();
      } else {
        this.currentGroup = null;
        this.currentGroupSubject.next(null);
        if (this.groupWs) {
          this.groupWs.close();
        }
      }
    });

    this.setUpUserSocket();
  }

  private setUpUserSocket() {
    this.userSocketService.groupSubject.subscribe(
      (element: {action: GroupAction, id: number}) => {

        switch (element.action) {
          case GroupAction.NEW: {
            break;
          }
          case GroupAction.DELETE: {
            this.onDeleteGroup();
            break;
          }
        }
      });
  }


  onNewMember(id: number, username: string) {
    this.currentGroup.addMember(id, username);
  }

  onDeleteMember(id: number) {
    this.currentGroup.deleteMember(id);
  }

  onDeleteGroup() {
    this.currentGroup = null;
    this.currentGroupSubject.next(null);
  }

  onNewOwner(oldOwner: number, newOwner: number) {
    this.currentGroup.changeOwner(newOwner);
  }

  private connectToGroupWs(id: number) { // TODO handle error

    if (this.groupWs) {
      this.groupWs.close();
    }

    const url = this.groupWsUrl + id + '?access-token=' + this.authService.getAccessToken();
    const ws = new $WebSocket(url);

    ws.onMessage(
      (msg: MessageEvent) => {
        const msgData = JSON.parse(msg.data);
        console.log(msgData);
        switch (msgData.type) {
          case 'newMember': {
            this.onNewMember(msgData.payload.id, msgData.payload.username);
            break;
          }
          case 'deleteMember': {
            this.onDeleteMember(msgData.payload.id);
            break;
          }
          case 'newOwner': {
            this.onNewOwner(msgData.payload.oldOwnerID, msgData.payload.newOwnerID);
            break;
          }
        }
      });

    this.groupWs = ws;
  }

  newGroup(group: DbGroup): Observable<boolean> {
    return this.http.post(this.groupsUrl, this.jsonConvert.serialize(group), {
      observe: 'response'
    })
      .pipe(
        switchMap(response => {
          const createdGroupUrl = response.headers.get('location');
          return this.http.get(createdGroupUrl, {
            observe: 'response'
          }, true)
            .pipe(
              switchMap( getGroupResponse => {
                  console.log(getGroupResponse);
                  this.currentGroup = this.jsonConvert.deserialize(getGroupResponse.body, Group);
                  this.currentGroupSubject.next(this.currentGroup);
                  return Observable.of(true);
                }
              ),
              catchError((err: any) => this.newGroupErrorHandle(err))
            );
        }),
        catchError((err: any) => this.newGroupErrorHandle(err))
      );
  }

  private newGroupErrorHandle(err: any) {
    console.log('Error creating new group');
    console.log(err);
    return Observable.of(false);
  }

  updateGroup(id: number): Observable<boolean> {
    return this.http.get(this.groupsUrl + '/' + id, {
      observe: 'response'
    })
      .pipe(
        map( getGroupResponse => {
            this.currentGroup = this.jsonConvert.deserialize(getGroupResponse.body, Group);
            this.currentGroupSubject.next(this.currentGroup);
            return true;
          }
        ),
        catchError((err: any) => this.updateGroupErrorHandle(err))
      );
  }

  private updateGroupErrorHandle(err: any) {
    console.log('Error retrieving group'); // TODO remove user from group?
    console.log(err);
    return Observable.of(false);
  }

  joinGroup(idGroup: number): Observable<boolean> {
    if (this.currentGroup !== null) {
      return this.leaveGroup().pipe(
        switchMap(result => {
          if (result) {
            return this.joinGroupRequest(idGroup);
          } else {
            return this.joinGroupErrorHandle();
          }
        }),
        catchError((err: any) => this.leaveGroupErrorHandle(err))
      );
    } else {
      return this.joinGroupRequest(idGroup);
    }
  }

  joinGroupRequest(idGroup: number): Observable<boolean> {
    return this.http.post(this.groupsUrl + '/' + idGroup + '/members' , {id: this.user.id}, {
      observe: 'response'
    })
      .pipe(
        switchMap(response => {
          return this.updateGroup(idGroup);
        }),
        catchError((err: any) => this.joinGroupErrorHandle(err))
      );
  }

  private joinGroupErrorHandle(err?: any) {
    console.log('Error joining group');
    console.log(err);
    return Observable.of(false);
  }

  leaveGroup(): Observable<boolean> {
    return this.http.delete(this.groupsUrl + '/' + this.currentGroup.id + '/members/' + this.user.id, {
      observe: 'response'
    })
      .pipe(
        map(response => {
          this.currentGroup = null;
          this.currentGroupSubject.next(null);
          return true;
        }),
        catchError((err: any) => this.leaveGroupErrorHandle(err))
      );
  }

  private leaveGroupErrorHandle(err: any) {
    console.log('Error leaving group');
    console.log(err);
    return Observable.of(false);
  }

  kickMember(idMember: number): Observable<boolean> {
    return this.http.delete(this.groupsUrl + '/' + this.currentGroup.id + '/members/' + idMember, {
      observe: 'response'
    })
      .pipe(
        map(response => {
          return true;
        }),
        catchError((err: any) => this.kickMemberErrorHandle(err))
      );
  }

  private kickMemberErrorHandle(err: any) {
    console.log('Error kicking member');
    console.log(err);
    return Observable.of(false);
  }

  promoteToLeader(newOwnerId: number): Observable<boolean> {
    return this.http.post(this.groupsUrl + '/' + this.currentGroup.id , {newOwnerID: newOwnerId}, {
      observe: 'response'
    })
      .pipe(
        map(response => {
          return true;
        }),
        catchError((err: any) => this.promoteToLeaderErrorHandle(err))
      );
  }

  private promoteToLeaderErrorHandle(err?: any) {
    console.log('Error promoting to leader');
    console.log(err);
    return Observable.of(false);
  }
}
