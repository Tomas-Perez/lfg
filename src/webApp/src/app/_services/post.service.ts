import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs/Observable';
import {catchError, map} from 'rxjs/operators';
import {Post} from '../_models/Post';

@Injectable()
export class PostService {

  private postUrl = 'http://localhost:8080/lfg/post';

  constructor(private http: HttpClient) { }

  getPosts(): Observable<Post[]> {
    return this.http.get<any>(this.postUrl, {
      observe: 'response'
    })
      .pipe(
        map(response => {
          return response.body.posts;
        }),
        catchError(err => Observable.of([]))
      );
  }

}
