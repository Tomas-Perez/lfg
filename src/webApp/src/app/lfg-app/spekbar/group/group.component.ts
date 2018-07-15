import {Component, OnDestroy, OnInit} from '@angular/core';
import {Group} from '../../../_models/Group';
import {GroupService} from '../../../_services/group.service';
import {Subject} from 'rxjs/Subject';
import {User} from '../../../_models/User';
import {UserService} from '../../../_services/user.service';
import {DbPost} from '../../../_models/DbModels/DbPost';
import {GroupPostService} from './group-post.service';
import {PostService} from '../../../_services/post.service';
import {Router} from '@angular/router';
import {Post} from '../../../_models/Post';
import {NavBarService} from '../../_services/nav-bar.service';
import {SpekbarLocation} from '../../_models/SpekbarLocation';

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
  newPost: DbPost;
  post: Post;

  constructor(private groupService: GroupService,
              private userService: UserService,
              private postService: PostService,
              private groupPostService: GroupPostService,
              private navBarService: NavBarService,
              private router: Router,
              ) { }

  ngOnInit() {
    this.newPost = new DbPost();

    this.navBarService.spekbarLocationSubject.next(SpekbarLocation.GROUP);

    this.groupService.currentGroupSubject.takeUntil(this.ngUnsubscribe)
      .subscribe((group: Group) => {
        if (group !== null) {
          this.group = group;
          this.userService.userSubject.takeUntil(this.ngUnsubscribe)
            .subscribe( (user: User) => {
              this.user = user;
              this.isOwner = user.id === group.owner.id;
          });
        } else {
          this.navigateToNewGroup();
        }
      });

    this.postService.currentPostSubject.takeUntil(this.ngUnsubscribe).subscribe(post => this.post = post);

    this.groupPostService.postSubject.takeUntil(this.ngUnsubscribe)
      .subscribe((post: DbPost) => {
        this.newPost = post;
        this.newPost.groupID = this.group.id;
      });
  }

  postGroup() {
    this.postService.newPost(this.newPost).subscribe(
      response => {
        if (response) {
          console.log('Post created');
        }
      }
    );
  }

  deletePost() {
    this.postService.deleteCurrentPost().subscribe(
      response => {
        if (response) {
          console.log('Post deleted');
        }
      }
    );
  }

  leaveGroup() {
    if (this.post !== null) { this.deletePost(); }
    this.groupService.leaveGroup().subscribe(
      response => {
        if (response) {
          this.navigateToNewGroup();
        } else {
          // TODO notify error leaving group
        }
      }
    );
  }

  navigateToNewGroup() {
    this.router.navigate(['app/', { outlets: {spekbar: ['new-group'] }}],
      {
        skipLocationChange: true
      });
  }

  kickPlayer(id: number) {
    this.groupService.kickMember(id).subscribe(
      response => {
        if (response) {
          console.log('Kicked user');
        }
      }
    );
  }

  ngOnDestroy() {
    this.ngUnsubscribe.next();
    this.ngUnsubscribe.complete();
    this.groupPostService.updatePost(this.newPost);
  }

}
