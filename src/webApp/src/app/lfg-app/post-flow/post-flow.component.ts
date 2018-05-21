import {Component, OnDestroy, OnInit} from '@angular/core';
import {Post} from '../../_models/Post';
import {PostService} from '../../_services/post.service';
import {Subject} from 'rxjs/Subject';
import 'rxjs/add/operator/takeUntil';
import {GroupService} from '../../_services/group.service';
import {ActivatedRoute, Router} from '@angular/router';

@Component({
  selector: 'app-post-flow',
  templateUrl: './post-flow.component.html',
  styleUrls: ['./post-flow.component.css']
})
export class PostFlowComponent implements OnInit, OnDestroy {

  posts: Post[];
  private ngUnsubscribe: Subject<any> = new Subject();
  inGroup: boolean;

  constructor(private postService: PostService,
              private groupService: GroupService,
              private router: Router,
              private route: ActivatedRoute
  ) { }

  ngOnInit() {
    this.groupService.currentGroupSubject.takeUntil(this.ngUnsubscribe).subscribe(
      group => this.inGroup = group != null
    );
    this.postService.postsSubject.takeUntil(this.ngUnsubscribe).subscribe(
      posts => {
        this.posts = posts;
      }
    );
    this.postService.updatePosts();
  }

  updatePosts() {
    this.postService.updatePosts();
  }

  joinGroup(groupId: number) {
    this.groupService.joinGroupRequest(groupId)
      .subscribe(response => {
        if (response) {
          this.router.navigate(['/app', { outlets: {spekbar: ['group'] }}], {
            skipLocationChange: true
          });
        }
      });
  }

  getUserInfo(id: number) {
    this.router.navigate([{outlets: {friends: ['user-info', id]}}],
      {
        relativeTo: this.route,
        skipLocationChange: true
      });
  }

  ngOnDestroy() {
    this.ngUnsubscribe.next();
    this.ngUnsubscribe.complete();
  }

}
