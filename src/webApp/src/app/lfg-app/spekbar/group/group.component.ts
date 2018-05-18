import {Component, OnDestroy, OnInit} from '@angular/core';
import {Group} from '../../../_models/Group';
import {GroupService} from '../../../_services/group.service';
import {Subject} from 'rxjs/Subject';
import {User} from '../../../_models/User';
import {UserService} from '../../../_services/user.service';
import {DbPost} from '../../../_models/DbModels/DbPost';
import {GroupPostService} from './group-post.service';
import {PostService} from '../../../_services/post.service';
import {ActivatedRoute, Router} from '@angular/router';

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

  leftGroup: boolean; // True if user just left the group so the BehaviourSubject doesnt update.

  constructor(private groupService: GroupService,
              private userService: UserService,
              private postService: PostService,
              private groupPostService: GroupPostService,
              private router: Router,
              private route: ActivatedRoute) { }

  ngOnInit() {
    this.leftGroup = false;
    this.groupService.currentGroupSubject.takeUntil(this.ngUnsubscribe)
      .subscribe((group: Group) => {
        if (group !== null) {
          this.group = group;
          this.userService.userSubject.takeUntil(this.ngUnsubscribe)
            .subscribe( (user: User) => {
              this.user = user;
              this.isOwner = user.id === group.owner.id;
          });
        }
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

  leaveGroup() {
    this.groupService.leaveGroup().subscribe(
      response => {
        if (response) {
          this.router.navigate(['app/', { outlets: {spekbar: ['new-group'] }}],
            {
              skipLocationChange: true
            });
        } else {
          // TODO notify error leaving group
        }
      }
    );
  }

  kickPlayer(id: number) {
    this.groupService.kickMember(id).subscribe(
      response => {
        if (response) {
          this.groupService.updateGroup(this.group.id).subscribe();
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
