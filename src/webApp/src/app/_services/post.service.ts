import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs/Observable';
import {catchError, map, tap} from 'rxjs/operators';
import {Post} from '../_models/Post';
import {PostFilter} from '../_models/post-filters/PostFilter';
import {JsonConvert} from 'json2typescript';
import {Filter} from '../_models/post-filters/Filter';
import {DbPost} from '../_models/DbPost';
import {BehaviorSubject} from 'rxjs/BehaviorSubject';

@Injectable()
export class PostService {

  private posts: BehaviorSubject<Post[]>;
  private filters: PostFilter[];
  private jsonConvert: JsonConvert = new JsonConvert();

  private postUrl = 'http://localhost:8080/lfg/posts';

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
    this.posts = new BehaviorSubject([]);
    this.filters = [];

  }

  requestPosts() {
    const filterer = new Filter;
    return this.http.get<any>(this.postUrl, {
      observe: 'response'
    })
      .pipe(
        tap(response => console.log(response)),
        map(response => this.jsonConvert.deserialize(response.body, Post)),
        map(posts => posts.filter(post => filterer.filter(post, this.filters))),
        catchError(err => Observable.of([]))
      );
  }

  getPosts(): BehaviorSubject<Post[]> {
    return this.posts;
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

  updatePosts(): void {
    this.requestPosts().subscribe(posts => this.posts.next(posts));
  }

  newPost(post: DbPost): Observable<boolean> {
    return this.http.post<any>(this.postUrl, this.jsonConvert.serialize(post), {
      observe: 'response'
    })
      .pipe(
        map(response => {
          console.log(response);
          return true;
        }),
        catchError((err: any) => this.newPostErrorHandle(err))
      );
  }

  private newPostErrorHandle(err: any) {
    console.log('Error creating new post');
    console.log(err);
    return Observable.of(false);
  }

  addFilter(filt: PostFilter) {
    this.filters.push(filt);
    this.requestPosts().subscribe();
  }

  resetFilters() {
    this.filters = [];
    this.requestPosts().subscribe();
  }

  getFiltersLength(): number {
    return this.filters.length;
  }

}
