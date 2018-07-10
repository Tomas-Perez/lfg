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
import {$WebSocket} from 'angular2-websocket/angular2-websocket';
import {AuthService} from './auth.service';
import {FilterByGame} from '../_models/post-filters/FilterByGame';
import {FilterByActivity} from '../_models/post-filters/FilterByActivity';

@Injectable()
export class PostService {

  private user: User;
  private posts: Post[];
  postsSubject: BehaviorSubject<Post[]>;
  private filters: PostFilter[];
  filtersSubject: BehaviorSubject<PostFilter[]>;
  private jsonConvert: JsonConvert = new JsonConvert();
  private postUrl = 'http://localhost:8080/lfg/posts';
  private currentPost: Post;
  currentPostSubject: BehaviorSubject<Post>;

  // private postWsUrl = 'ws://localhost:8080/lfg/websockets/posts';
  private postWs: $WebSocket;

  constructor(private http: HttpClient, private authService: AuthService, private userService: UserService) {
    this.postsSubject = new BehaviorSubject([]);

    this.currentPostSubject = new BehaviorSubject<Post>(null);
    this.currentPostSubject.subscribe(post => this.currentPost = post);

    this.filtersSubject = new BehaviorSubject([]);
    this.filtersSubject.subscribe(filters => {
      this.filters = filters;
      // this.newPostSocket();
      this.updatePosts();
    });
    this.userService.userSubject.subscribe( user => {
      this.user = user;
      if (user !== null && user.post) {
        this.getCurrentPost(user.post.id).subscribe();
      } else {
        this.currentPostSubject.next(null);
        if (this.postWs) {
          this.postWs.close();
        }
      }
    });
  }

   private newPostSocket(wsUrl: string) {

    if (this.postWs) {
      this.postWs.close();
    }

    const url = wsUrl + this.authService.getAccessToken();
    console.log(url);
    /*
    if (this.filters.length > 0) {

      for (const filter of this.filters) {
        if (filter.isFilterByGame()) {
          const gameFilter = <FilterByGame>filter;
          url = url + '&filter=' + gameFilter.gameId;
        } else {
          const activityFilter = <FilterByActivity>filter;
          url = url + '&filter=' + activityFilter.gameId + ':' + activityFilter.activityId;
        }
      }
    }
    */
    const ws = new $WebSocket(url);

    ws.onMessage(
      (msg: MessageEvent) => {
        const msgData = JSON.parse(msg.data);
          console.log(msgData);
          switch (msgData.type) {
            case 'newPost': {
              this.onNewPost(msgData.payload.id);
              break;
            }
            case 'deletePost': {
              this.onDeletePost(msgData.payload.id);
              break;
            }
          }
        });

    this.postWs = ws;
  }

  onDeletePost(id: number) {
    for (let i = 0; i < this.posts.length; i++) {
      if (this.posts[i].id === id) {
        this.posts.splice(i, 1);
        this.postsSubject.next(this.posts);
      }
    }
  }

  onNewPost(id: number) {
    this.getPost(id).subscribe(
      post => {
        this.posts = [post].concat(this.posts);
        this.postsSubject.next(this.posts);
      }
    );
  }

  getPost(id: number): Observable<Post> {
    return this.http.get<any>(this.postUrl + '/' + id, {
      observe: 'response'
    })
      .pipe(
        map( getPostResponse => {
            const post = this.jsonConvert.deserialize(getPostResponse.body, Post);
            return post;
          }
        ),
        catchError((err: any) => this.getPostErrorHandle(err))
      );
  }

  private getPostErrorHandle(err: any) {
    console.log('Error getting post');
    console.log(err);
    return Observable.of(null);
  }

  getCurrentPost(id: number): Observable<boolean> {
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
        catchError((err: any) => this.getCurrentPostErrorHandle(err))
      );
  }

  private getCurrentPostErrorHandle(err: any) {
    console.log('Error getting post');
    console.log(err);
    return Observable.of(false);
  }

  requestPosts(): Observable<any> {
    // const filterer = new Filter;

    let url = this.postUrl;

    if (this.filters.length > 0) {
      url = url + '?';
      let first = true;
      for (const filter of this.filters) {
        if (!first) {
          url += '&';
        }
        if (filter.isFilterByGame()) {
          const gameFilter = <FilterByGame>filter;
          url += 'filter=' + gameFilter.gameId;
        } else {
          const activityFilter = <FilterByActivity>filter;
          url += 'filter=' + activityFilter.gameId + ':' + activityFilter.activityId;
        }
        first = false;
      }
    }

    return this.http.get<any>(url, {
      observe: 'response'
    })
      .pipe(
        tap(response => console.log(response)),
        map(response => {
          return {posts: this.jsonConvert.deserialize(response.body.posts, Post), wsPath: response.body.socketPath};
        }),
        // map(posts => posts.filter(post => filterer.filter(post, this.filters))),
        catchError(err => this.requestPostsErrorHandle(err))
      );
  }

  private requestPostsErrorHandle(err: any) {
    console.log('Error getting posts');
    console.log(err);
    return Observable.of(null);
  }

  updatePosts(): void {
    this.requestPosts().subscribe(element => {
      if (element != null) {
        this.posts = element.posts;
        this.postsSubject.next(this.posts);
        this.newPostSocket(element.wsPath);
      }
    });
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

  deleteCurrentPost(): Observable<boolean> {
    return this.http.delete<any>(this.postUrl + '/' + this.currentPost.id, {
      observe: 'response'
    })
      .pipe(
        map(response => {
          this.currentPostSubject.next(null);
          return true;
        }),
        catchError((err: any) => this.deleteCurrentPostErrorHandle(err))
      );
  }

  private deleteCurrentPostErrorHandle(err: any) {
    console.log('Error deleting post');
    console.log(err);
    return Observable.of(false);
  }
}
