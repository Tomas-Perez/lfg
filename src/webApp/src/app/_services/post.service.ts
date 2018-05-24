import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs/Observable';
import {catchError, map, switchMap, tap} from 'rxjs/operators';
import {Post} from '../_models/Post';
import {PostFilter} from '../_models/post-filters/PostFilter';
import {JsonConvert} from 'json2typescript';
import {Filter} from '../_models/post-filters/Filter';
import {DbPost} from '../_models/DbModels/DbPost';
import {BehaviorSubject} from 'rxjs/BehaviorSubject';
import {User} from '../_models/User';
import {UserService} from './user.service';

@Injectable()
export class PostService {

  private user: User;
  postsSubject: BehaviorSubject<Post[]>;
  private filters: PostFilter[];
  filtersSubject: BehaviorSubject<PostFilter[]>;
  private jsonConvert: JsonConvert = new JsonConvert();
  private postUrl = 'http://localhost:8080/lfg/posts';
  private currentPost: Post;
  currentPostSubject: BehaviorSubject<Post>;

  constructor(private http: HttpClient, private userService: UserService) {
    this.postsSubject = new BehaviorSubject([]);

    this.currentPostSubject = new BehaviorSubject<Post>(null);
    this.currentPostSubject.subscribe(post => this.currentPost = post);

    this.filtersSubject = new BehaviorSubject([]);
    this.filtersSubject.subscribe(filters => {
      this.filters = filters;
      this.updatePosts();
    });
    this.userService.userSubject.subscribe( user => {
      this.user = user;
      if (user !== null && user.post) {
        this.getPost(user.post.id).subscribe();
      } else {
        this.currentPostSubject.next(null);
      }
    });
  }

  getPost(id: number): Observable<boolean> {
    return this.http.get<any>(this.postUrl + '/' + id, {
      observe: 'response'
    })
      .pipe(
        map( getPostResponse => {
            const newPost = this.jsonConvert.deserialize(getPostResponse.body, Post);
            this.currentPostSubject.next(newPost);
            return true;
          }
        ),
        catchError((err: any) => this.getPostErrorHandle(err))
      );
  }

  private getPostErrorHandle(err: any){
    console.log('Error getting post');
    console.log(err);
    return Observable.of(false);
  }

  requestPosts(): Observable<Post[]> {
    const filterer = new Filter;
    return this.http.get<any>(this.postUrl, {
      observe: 'response'
    })
      .pipe(
        tap(response => console.log(response)),
        map(response => this.jsonConvert.deserialize(response.body, Post)),
        map(posts => posts.filter(post => filterer.filter(post, this.filters))),
        tap(posts => console.log(posts)),
        catchError(err => this.requestPostsErrorHandle(err))
      );
  }

  private requestPostsErrorHandle(err: any){
    console.log('Error getting posts');
    console.log(err);
    return Observable.of([]);
  }

  updatePosts(): void {
    this.requestPosts().subscribe(posts => this.postsSubject.next(posts));
  }

  newPost(post: DbPost): Observable<boolean> {
    return this.http.post<any>(this.postUrl, this.jsonConvert.serialize(post), {
      observe: 'response'
    })
      .pipe(
        switchMap(response => {
          const newPostUrl = response.headers.get('location');
          return this.http.get<any>(newPostUrl, {
            observe: 'response'
          })
            .pipe(
              switchMap( getGroupResponse => {
                  const newPost = this.jsonConvert.deserialize(getGroupResponse.body, Post);
                  this.currentPostSubject.next(newPost);
                  this.updatePosts(); // TODO delet this
                  return Observable.of(true);
                }
              ),
              catchError((err: any) => this.newPostErrorHandle(err))
            );
        }),
        catchError((err: any) => this.newPostErrorHandle(err))
      );
  }

  private newPostErrorHandle(err: any) {
    console.log('Error creating new post');
    console.log(err);
    return Observable.of(false);
  }

  deletePost(): Observable<boolean> {
    return this.http.delete<any>(this.postUrl + '/' + this.currentPost.id, {
      observe: 'response'
    })
      .pipe(
        map(response => {
          this.updatePosts();
          this.currentPostSubject.next(null);
          return true;
        }),
        catchError((err: any) => this.deletePostErrorHandle(err))
      );
  }

  private deletePostErrorHandle(err: any) {
    console.log('Error deleting post');
    console.log(err);
    return Observable.of(false);
  }
}
