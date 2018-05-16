import {Component, OnDestroy, OnInit} from '@angular/core';
import {Group} from '../../../_models/Group';
import {GroupService} from '../../../_services/group.service';
import {Subject} from 'rxjs/Subject';
import {User} from '../../../_models/User';
import {UserService} from '../../../_services/user.service';
import {DbPost} from '../../../_models/DbModels/DbPost';
import {GroupPostService} from './group-post.service';
import {PostService} from '../../../_services/post.service';

@Component({
  selector: 'app-group',
  templateUrl: './group.component.html',
  styleUrls: ['./group.component.css', '../spekbar.css']
})
export class GroupComponent implements OnInit, OnDestroy {

  private ngUnsubscribe: Subject<any> = new Subject();
  group: Group;
  user: User;
  isOwner: boolean;
  post: DbPost = new DbPost();

  constructor(private groupService: GroupService,
              private userService: UserService,
              private postService: PostService,
              private groupPostService: GroupPostService) { }

  ngOnInit() {
    this.groupService.currentGroupSubject.takeUntil(this.ngUnsubscribe)
      .subscribe((group: Group) => {
        this.group = group;
        this.userService.userSubject.takeUntil(this.ngUnsubscribe)
          .subscribe( (user: User) => {
            this.user = user;
            this.isOwner = user.id === group.owner.id;
          })
      });
    this.groupPostService.postSubject.takeUntil(this.ngUnsubscribe)
      .subscribe((post: DbPost) => {
        this.post = post;
        this.post.groupID = this.group.id;
      });
  }

  postGroup() {
    this.postService.newPost(this.post).subscribe(
      response => {
        if (response) {
          console.log('Post created');
        }
      }
    );
  }

  ngOnDestroy() {
    this.ngUnsubscribe.next();
    this.ngUnsubscribe.complete();
    this.groupPostService.updatePost(this.post);
  }

}
