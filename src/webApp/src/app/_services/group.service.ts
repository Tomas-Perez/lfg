import { Injectable } from '@angular/core';
import {JsonConvert} from 'json2typescript';
import {DbGroup} from '../_models/DbModels/DbGroup';
import {Observable} from 'rxjs/Observable';
import {HttpClient} from '@angular/common/http';
import {catchError, map, switchMap} from 'rxjs/operators';
import {Group} from '../_models/Group';
import {BehaviorSubject} from 'rxjs/BehaviorSubject';
import {UserService} from './user.service';
import {User} from '../_models/User';

@Injectable()
export class GroupService {

  private jsonConvert: JsonConvert = new JsonConvert();
  private groupsUrl = 'http://localhost:8080/lfg/groups';
  private currentGroup: Group;
  private user: User;
  currentGroupSubject: BehaviorSubject<Group>;

  constructor(private http: HttpClient, private userService: UserService) {
    this.currentGroupSubject = new BehaviorSubject<Group>(null);
    this.userService.userSubject.subscribe( user => {
      if (user !== null && user.groups.length){
        this.user = user;
        this.updateGroup(user.groups[0].id).subscribe();

      }
    });
  }

  newGroup(group: DbGroup): Observable<boolean> {
    return this.http.post<any>(this.groupsUrl, this.jsonConvert.serialize(group), {
      observe: 'response'
    })
      .pipe(
        switchMap(response => {
          const createdGroupUrl = response.headers.get('location');
          return this.http.get<any>(createdGroupUrl, {
            observe: 'response'
          })
            .pipe(
              switchMap( getGroupResponse => {
                  const newGroup = this.jsonConvert.deserialize(getGroupResponse.body, Group);
                  this.currentGroupSubject.next(newGroup);
                  console.log(this.currentGroupSubject.getValue());
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
    return this.http.get<any>(this.groupsUrl + '/' + id, {
      observe: 'response'
    })
      .pipe(
        map( getGroupResponse => {
            const newGroup = this.jsonConvert.deserialize(getGroupResponse.body, Group);
            this.currentGroup = newGroup;
            this.currentGroupSubject.next(newGroup);
            return true;
          }
        ),
        catchError((err: any) => this.updateGroupErrorHandle(err))
      );
  }

  private updateGroupErrorHandle(err: any) {
    console.log('Error retrieving group'); //TODO remove user from group
    console.log(err);
    return Observable.of(false);
  }

  joinGroup(id: number): Observable<boolean> {
    return this.http.post<any>(this.groupsUrl + '/' + id + '/members' , {id: id}, {
      observe: 'response'
    })
      .pipe(
        switchMap(response => {
          return this.updateGroup(id);
        }),
        catchError((err: any) => this.joinGroupErrorHandle(err))
      );
  }

  private joinGroupErrorHandle(err: any) {
    console.log('Error joining group');
    console.log(err);
    return Observable.of(false);
  }
}
