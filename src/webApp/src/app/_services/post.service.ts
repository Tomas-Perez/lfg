import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs/Observable';
import {catchError, filter, map} from 'rxjs/operators';
import {Post} from '../_models/Post';
import {PostFilter} from '../_models/post-filters/PostFilter';
import {JsonConvert} from 'json2typescript';
import {Filter} from '../_models/post-filters/Filter';

@Injectable()
export class PostService {

  private posts: Observable<Post[]>;
  private filters: PostFilter[];
  private jsonConvert: JsonConvert = new JsonConvert();

  private postUrl = 'http://localhost:8080/lfg/post';

  constructor(private http: HttpClient) {

    /*
    const lul = this.jsonConvert.deserialize({
           'posts': [
             {
               'id': 123,
               'activity': {
                 'id': 111,
                 'name': 'activityyy',
                 'game': {
                   'id': 222,
                   'name': 'gameee'
                 }
               },
               'owner': {
                 'id': 11234123,
                 'name': 'ownerName',
               },
               'date': '1995-12-17T03:24:00',
               'description': 'descriptionnnn'
             },
             {
               'id': 123,
               'activity': {
                 'id': 111,
                 'name': 'activityyy',
                 'game': {
                   'id': 222,
                   'name': 'gameee'
                 }
               },
               "owner": {
                 "id": 11234123,
                 "name": "ownerName",
               },
               'date': '1995-12-17T03:24:00',
               'description': 'descriptionnnn'
             }
           ]}.posts,
         Post);
    this.posts = Observable.of(lul);
    */
    this.posts = Observable.of([]);
    this.filters = [];

  }

  getPosts(): Observable<Post[]> {
    const filterer = new Filter;
    return this.http.get<any>(this.postUrl, {
      observe: 'response'
    })
      .pipe(
        map(response => this.jsonConvert.deserialize(response.body.posts, Post)),
        map(posts => posts.filter(post => filterer.filter(post, this.filters))),
        catchError(err => Observable.of([]))
      );
    /*
    const filterer = new Filter;
    return <Observable<Post[]>>(this.posts)
      .pipe(
        map(posts => this.jsonConvert.deserialize(posts, Post)),
        map(posts => posts.filter(post => filterer.filter(post, this.filters))),
        catchError(err => Observable.of([]))
      );
    */
  }

}
