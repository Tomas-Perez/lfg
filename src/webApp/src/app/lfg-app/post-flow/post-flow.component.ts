import {Component, OnDestroy, OnInit} from '@angular/core';
import {Post} from '../../_models/Post';
import {PostService} from '../../_services/post.service';
import {Subject} from 'rxjs/Subject';
import 'rxjs/add/operator/takeUntil';

@Component({
  selector: 'app-post-flow',
  templateUrl: './post-flow.component.html',
  styleUrls: ['./post-flow.component.css']
})
export class PostFlowComponent implements OnInit, OnDestroy {

  posts: Post[];
  private ngUnsubscribe: Subject<any> = new Subject();

  constructor(private postService: PostService) { }

  ngOnInit() {
    this.postService.getPosts().takeUntil(this.ngUnsubscribe).subscribe(
      posts => {
        this.posts = posts;
      }
    );
    this.postService.updatePosts();
  }

  updatePosts() {
    this.postService.updatePosts();
  }

  ngOnDestroy() {
    this.ngUnsubscribe.next();
    this.ngUnsubscribe.complete();
  }

}
