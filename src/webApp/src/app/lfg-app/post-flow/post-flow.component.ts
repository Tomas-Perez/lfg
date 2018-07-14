import {Component, OnDestroy, OnInit} from '@angular/core';
import {Post} from '../../_models/Post';
import {PostService} from '../../_services/post.service';
import {Subject} from 'rxjs/Subject';
import 'rxjs/add/operator/takeUntil';
import {GroupService} from '../../_services/group.service';
import {Router} from '@angular/router';
import {UserService} from '../../_services/user.service';
import {User} from '../../_models/User';
import {ChatService} from '../../_services/chat.service';
import {ChatType} from '../../_models/ChatType';

@Component({
  selector: 'app-post-flow',
  templateUrl: './post-flow.component.html',
  styleUrls: ['./post-flow.component.css'],
})
export class PostFlowComponent implements OnInit, OnDestroy {

  posts: Post[];
  private ngUnsubscribe: Subject<any> = new Subject();
  inGroup: boolean;
  user: User;

  constructor(private postService: PostService,
              private groupService: GroupService,
              private chatService: ChatService,
              private userService: UserService,
              private router: Router
  ) { }

  ngOnInit() {
    this.userService.userSubject.takeUntil(this.ngUnsubscribe).subscribe(
      user => this.user = user
    );
    this.groupService.currentGroupSubject.takeUntil(this.ngUnsubscribe).subscribe(
      group => this.inGroup = group != null
    );
    this.postService.postsSubject.takeUntil(this.ngUnsubscribe).subscribe(
      posts => {
        this.posts = posts;
      }
    );
    // this.postService.updatePosts();
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
    this.router.navigate(['/app', { outlets: {friends: ['user-info', id] }}], {
      skipLocationChange: true
    });
  }

  newChat(id: number) {
    this.chatService.newChat(ChatType.PRIVATE, id);
  }

  ngOnDestroy() {
    this.ngUnsubscribe.next();
    this.ngUnsubscribe.complete();
  }

}
