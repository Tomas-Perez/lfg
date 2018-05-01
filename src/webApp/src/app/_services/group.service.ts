import { Injectable } from '@angular/core';
import {JsonConvert} from 'json2typescript';
import {Group} from '../_models/Group';
import {Observable} from 'rxjs/Observable';
import {HttpClient} from '@angular/common/http';
import {catchError, map} from 'rxjs/operators';

@Injectable()
export class GroupService {

  private jsonConvert: JsonConvert = new JsonConvert();
  private groupsUrl = 'http://localhost:8080/lfg/groups';

  constructor(private http: HttpClient) { }

  newGroup(group: Group): Observable<boolean> {
    return this.http.post<any>(this.groupsUrl, this.jsonConvert.serialize(group), {
      observe: 'response'
    })
      .pipe(
        map(response => {
          console.log(response);
          return true;
        }),
        catchError((err: any) => this.newGroupErrorHandle(err))
      );
  }

  private newGroupErrorHandle(err: any) {
    console.log('Error creating new post');
    console.log(err);
    return Observable.of(false);
  }

}
